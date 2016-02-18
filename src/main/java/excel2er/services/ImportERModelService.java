package excel2er.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.ERModelEditor;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERAttribute;
import com.change_vision.jude.api.inf.model.IERDatatype;
import com.change_vision.jude.api.inf.model.IERDomain;
import com.change_vision.jude.api.inf.model.IEREntity;
import com.change_vision.jude.api.inf.model.IERModel;
import com.change_vision.jude.api.inf.model.IERRelationship;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Attribute;
import excel2er.models.Configuration;
import excel2er.models.Entity;
import excel2er.services.finder.DataTypeFinder;
import excel2er.services.finder.DomainFinder;
import excel2er.services.finder.ERModelFinder;
import excel2er.services.finder.IEREntityFinder;

public class ImportERModelService {
	private static final Logger logger = LoggerFactory
			.getLogger(ImportERModelService.class);

	private Result result;

	private IEREntityFinder erFinder;

	public ImportERModelService() {
		init();
	}

	private void init() {
		result = new Result();
		erFinder = new IEREntityFinder();
	}

	public Result importERModel(Configuration configuration) {

		init();

		ParseExcelToEntityModelService parseService = new ParseExcelToEntityModelService();

		List<Entity> entities = parseService.parse(configuration);

        ProjectAccessor projectAccessor = getProjectAccessor();
        ERModelEditor editor = getERModelEditor(projectAccessor);
        boolean isCreateRelationship = configuration.getForeignKeyCol() != null;

		for (Entity entity : entities) {
			String entityName = entity.getEntityLogicalName();
			try {
                projectAccessor.getTransactionManager().beginTransaction();
                mergeEntity(editor, entity);
                mergeAttribute(editor, entity, isCreateRelationship);
                if (isCreateRelationship) {
                    mergeRelationship(editor, entity);
                }
                projectAccessor.getTransactionManager().endTransaction();
                result.inclementImportedElementsCount();
			} catch (ApplicationException e) {
				// continue import
				log_info(Messages.getMessage("log.error.create_entity_end",
						entityName));
                aboartTransaction();
			}
		}

		return result;
	}

    private IEREntity mergeRelationship(ERModelEditor editor, Entity entity) {
        String entityName = entity.getEntityLogicalName();
        try {

            IEREntity erEntity = erFinder.findEREntity(entityName);

            for (Attribute attr : entity.getAttributes()) {
                if (!attr.isForeignKey()) {
                    continue;
                }
                String referenceEntityName = attr.getReferenceEntityName();
                if (StringUtils.isEmpty(referenceEntityName)) {
                    log_error(Messages.getMessage(
                            "log.error.create_relationship.reference.parameter_error", erEntity,
                            attr));
                    continue;
                }
                if (StringUtils.equals(entityName, referenceEntityName)) {
                    continue;
                }
                IEREntity parentEREntity = erFinder.findEREntity(referenceEntityName);
                if (parentEREntity == null) {
                    log_error(Messages.getMessage(
                            "log.error.create_relationship.reference.parameter_error", erEntity,
                            attr));
                    continue;
                }
                try {
                    if (attr.isPrimaryKey()) {
                        List<IERRelationship> relationships = getIdentifyingRelationships(
                                parentEREntity, erEntity);
                        int needRelationshipCounts = needIdentifyingRelationshipCounts(entity,
                                referenceEntityName);
                        if (needRelationshipCounts <= relationships.size()) {
                            continue;
                        }
                        editor.createIdentifyingRelationship(parentEREntity, erEntity,
                                attr.getReferenceAttributeName(), null);
                    } else {
                        List<IERRelationship> relationships = getNonIdentifyingRelationships(
                                parentEREntity, erEntity);
                        int needRelationshipCounts = needNonIdentifyingRelationshipCounts(entity,
                                referenceEntityName);
                        if (needRelationshipCounts <= relationships.size()) {
                            continue;
                        }
                        editor.createNonIdentifyingRelationship(parentEREntity, erEntity,
                                attr.getReferenceAttributeName(), null);
                    }
                } catch (InvalidEditingException e) {
                    if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                        log_error(Messages.getMessage(
                                "log.error.create_relationship.parameter_error", parentEREntity,
                                erEntity, attr.getReferenceAttributeName()), e);
                        aboartTransaction();
                        throw new ApplicationException(e);
                    }
                    if (StringUtils.equals(e.getKey(),
                            InvalidEditingException.INVALID_ERINDEX_FOR_RELATIONSHIP_ERROR_KEY)) {
                        log_error(
                                Messages.getMessage(
                                        "log.error.create_relationship.invalid_erindex_for_relationship_error",
                                        parentEREntity, erEntity, attr.getReferenceAttributeName()),
                                e);
                        aboartTransaction();
                        throw new ApplicationException(e);
                    }
                    if (StringUtils.equals(e.getKey(),
                            InvalidEditingException.ILLEGALMODELTYPE_ERROR_KEY)) {
                        log_error(Messages.getMessage(
                                "log.error.create_relationship.illegalmodeltype_error",
                                parentEREntity, erEntity, attr.getReferenceAttributeName()), e);
                        aboartTransaction();
                        throw new ApplicationException(e);
                    }
                    if (StringUtils.equals(e.getKey(), InvalidEditingException.NO_NAME_ERROR_KEY)) {
                        log_error(Messages.getMessage(
                                "log.error.create_relationship.parameter_error", parentEREntity,
                                erEntity, attr.getReferenceAttributeName()), e);
                        aboartTransaction();
                        throw new ApplicationException(e);
                    }
                    if (StringUtils.equals(e.getKey(),
                            InvalidEditingException.NAME_DOUBLE_ERROR_KEY)) {
                        log_error(Messages.getMessage(
                                "log.error.create_relationship.name_double_error", parentEREntity,
                                erEntity, attr.getReferenceAttributeName()), e);
                        aboartTransaction();
                        throw new ApplicationException(e);
                    }
                    if (StringUtils.equals(e.getKey(), InvalidEditingException.READ_ONLY_KEY)) {
                        log_error(Messages.getMessage("log.error.create_relationship.read_only",
                                parentEREntity, erEntity, attr.getReferenceAttributeName()), e);
                        aboartTransaction();
                        throw new ApplicationException(e);
                    }
                    log_error(Messages.getMessage("log.error.create_relationship.error",
                            parentEREntity, erEntity, attr.getReferenceAttributeName()), e);
                    aboartTransaction();
                    throw new ApplicationException(e);
                }
            }

            return erEntity;
        } catch (ClassNotFoundException e) {
            log_error(Messages.getMessage("log.error.create_entity", entityName, e.getMessage()), e);

            aboartTransaction();

            throw new ApplicationException(e);
        } catch (ProjectNotFoundException e) {
            log_error(Messages.getMessage("error.project.not.found"), e);

            aboartTransaction();
            throw new ApplicationException(e);
        }
    }

    private List<IERRelationship> getNonIdentifyingRelationships(IEREntity parentEntity,
            IEREntity childEntity) {
        List<IERRelationship> nonIdentifyingRelationships = new ArrayList<IERRelationship>();
        for (IERRelationship relationship : getRelationships(parentEntity, childEntity)) {
            if (relationship.isNonIdentifying()) {
                nonIdentifyingRelationships.add(relationship);
            }
        }
        return nonIdentifyingRelationships;
    }

    private List<IERRelationship> getIdentifyingRelationships(IEREntity parentEntity,
            IEREntity childEntity) {
        List<IERRelationship> identifyingRelationships = new ArrayList<IERRelationship>();
        for (IERRelationship relationship : getRelationships(parentEntity, childEntity)) {
            if (relationship.isIdentifying()) {
                identifyingRelationships.add(relationship);
            }
        }
        return identifyingRelationships;
    }

    private int needIdentifyingRelationshipCounts(Entity entity, String parentEntityName) {
        return needRelationshipCounts(entity, parentEntityName, true);
    }

    private int needNonIdentifyingRelationshipCounts(Entity entity, String parentEntityName) {
        return needRelationshipCounts(entity, parentEntityName, false);
    }

    private int needRelationshipCounts(Entity entity, String parentEntityName,
            boolean isIdentifyingRelationship) {
        Map<String, Integer> countMap = new HashMap<String, Integer>();
        for (Attribute attr : entity.getAttributes()) {
            if (!attr.isForeignKey()) {
                continue;
            }
            if (isIdentifyingRelationship && !attr.isPrimaryKey()) {
                continue;
            }
            if (!StringUtils.equals(attr.getReferenceEntityName(), parentEntityName)) {
                continue;
            }
            int count = 1;
            if (countMap.containsKey(attr)) {
                count += countMap.get(attr);
            }
            countMap.put(attr.getReferenceAttributeName(), count);
        }
        if (countMap.isEmpty()) {
            return 0;
        }
        String maxCountAttr = null;
        for (String key : countMap.keySet()) {
            if (maxCountAttr == null) {
                maxCountAttr = key;
                continue;
            }
            if (countMap.get(key) > countMap.get(maxCountAttr)) {
                maxCountAttr = key;
            }
        }
        return countMap.get(maxCountAttr);
    }

    private List<IERRelationship> getRelationships(IEREntity parentEntity, IEREntity childEntity) {
        List<IERRelationship> relationships = new ArrayList<IERRelationship>();
        for (IERRelationship child : parentEntity.getChildrenRelationships()) {
            if (child.getChild().equals(childEntity)) {
                relationships.add(child);
            }
        }
        return relationships;
    }

    private ERModelEditor getERModelEditor(ProjectAccessor projectAccessor) {
        try {
            return projectAccessor.getModelEditorFactory().getERModelEditor();
        } catch (InvalidEditingException e) {
            throw new ApplicationException(e);
        }
    }

    private ProjectAccessor getProjectAccessor() {
        try {
            return AstahAPI.getAstahAPI().getProjectAccessor();
        } catch (ClassNotFoundException e) {
            throw new ApplicationException(e);
        }
    }

	private void log_append(String message) {
		result.appendMessage(message);
		result.appendMessage(SystemUtils.LINE_SEPARATOR);
	}

	private void log_info(String message) {
		logger.info(message);
		log_append(message);
	}

	private void log_error(String message) {
		logger.error(message);
		log_append(message);
		result.setErrorOccured(true);
	}

	private void log_error(String message, Throwable e) {
		logger.error(message, e);
		log_append(message);
		result.setErrorOccured(true);
	}

    IEREntity mergeEntity(ERModelEditor editor, Entity entity) {
		String entityName = entity.getEntityLogicalName();
		try {

            IERModel erModel = new ERModelFinder().find();

            if (erModel == null) {
                erModel = editor.createERModel(null, "ER Model");
			}
			IEREntity entityModel = mergeEntity(entity, entityName,
					editor, erModel);

			return entityModel;
		} catch (ClassNotFoundException e) {
			log_error(Messages.getMessage("log.error.create_entity",
					entityName, e.getMessage()), e);

			aboartTransaction();

			throw new ApplicationException(e);
		} catch (InvalidEditingException e) {
			if (StringUtils.equals(e.getKey(),
					InvalidEditingException.PARAMETER_ERROR_KEY)) {
				log_error(Messages.getMessage(
						"log.error.create_entity.parameter_error", entityName));
			} else if (StringUtils.equals(e.getKey(),
					InvalidEditingException.NAME_DOUBLE_ERROR_KEY)) {
				log_error(Messages.getMessage(
						"log.error.create_entity.duplicate_entity", entityName));
			} else {
				log_error(Messages.getMessage(
						"log.error.create_entity.invalideditingexception",
						entityName, e.getKey()), e);
			}

			aboartTransaction();

			throw new ApplicationException(e);
		} catch (ProjectNotFoundException e) {
			log_error(Messages.getMessage("error.project.not.found"), e);

			aboartTransaction();
			throw new ApplicationException(e);
		}
	}

    IEREntity mergeAttribute(ERModelEditor editor, Entity entity, boolean needJudgesFK) {
        String entityName = entity.getEntityLogicalName();
        try {

            IEREntity entityModel = new IEREntityFinder().findEREntity(entityName);

            DomainFinder domainFinder = new DomainFinder();
            DataTypeFinder dataTypeFinder = new DataTypeFinder();
            ERModelFinder erModelFinder = new ERModelFinder();

            for (Attribute attr : entity.getAttributes()) {
                if (needJudgesFK && attr.isForeignKey()) {
                    continue;
                }
                IERDomain domain = domainFinder.find(attr);
                IERAttribute attrModel = null;
                if (domain != null) {
                    attrModel = mergeAttributeUsingDomain(editor, entityModel, attr, domain);

                    setEditablePropertyUsingDomain(attr, attrModel);
                    continue;
                }
                IERDatatype dataType = dataTypeFinder.find(attr.getDataType());
                if (dataType == null) {
                    logger.debug(Messages.getMessage("log.create.datatype", attr.getDataType()));
                    IERModel erModel = erModelFinder.find();
                    dataType = createDataType(editor, erModel, attr.getDataType());
                }
                attrModel = mergeAttribute(editor, entityModel, attr, dataType);

                setAdditionalProperty(attr, attrModel);

            }
            return entityModel;
        } catch (ClassNotFoundException e) {
            log_error(Messages.getMessage("log.error.create_entity", entityName, e.getMessage()), e);

            aboartTransaction();

            throw new ApplicationException(e);
        } catch (InvalidEditingException e) {
            if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                log_error(Messages
                        .getMessage("log.error.create_entity.parameter_error", entityName));
            } else if (StringUtils
                    .equals(e.getKey(), InvalidEditingException.NAME_DOUBLE_ERROR_KEY)) {
                log_error(Messages.getMessage("log.error.create_entity.duplicate_entity",
                        entityName));
            } else {
                log_error(Messages.getMessage("log.error.create_entity.invalideditingexception",
                        entityName, e.getKey()), e);
            }

            aboartTransaction();

            throw new ApplicationException(e);
        } catch (ProjectNotFoundException e) {
            log_error(Messages.getMessage("error.project.not.found"), e);

            aboartTransaction();
            throw new ApplicationException(e);
        }
    }

	private IEREntity mergeEntity(Entity entity, String entityName,
			ERModelEditor editor, IERModel erModel)
			throws InvalidEditingException, ClassNotFoundException,
			ProjectNotFoundException {

		IEREntity model = erFinder.findEREntity(entityName);
		if (model == null) {
			logger.info(Messages.getMessage("log.create_entity", entityName));
			
			return editor.createEREntity(erModel.getSchemata()[0], entityName,
					entity.getEntityPhysicalName());
		}
		
		logger.info(Messages.getMessage("log.update_entity", entityName));
		updateEntity(entity, model);

		return model;
	}

	private void updateEntity(Entity entity, IEREntity model)
			throws InvalidEditingException {
		model.setPhysicalName(entity.getEntityPhysicalName());
	}

	private IERDatatype createDataType(ERModelEditor editor, IERModel erModel,
			String dataType) throws InvalidEditingException {
		return editor.createERDatatype(erModel, dataType);
	}

	private void setEditablePropertyUsingDomain(Attribute attr,
			IERAttribute attrModel) throws InvalidEditingException {

		attrModel.setPhysicalName(attr.getPhysicalName());
		
		attrModel.setPrimaryKey(attr.isPrimaryKey());
		if(attr.isPrimaryKey()){
			attrModel.setNotNull(true);
		}

		if (StringUtils.isNotEmpty(attr.getDefaultValue()))
			attrModel.setDefaultValue(attr.getDefaultValue());

		if (StringUtils.isNotEmpty(attr.getDefinition()))
			attrModel.setDefinition(attr.getDefinition());
	}

	private void setAdditionalProperty(Attribute attr, IERAttribute attrModel)
			throws InvalidEditingException {

		attrModel.setPrimaryKey(attr.isPrimaryKey());
		if(attr.isPrimaryKey()){
			attrModel.setNotNull(true);
		}else{
			attrModel.setNotNull(attr.isNotNull());
		}

		if (StringUtils.isNotEmpty(attr.getDefaultValue()))
			attrModel.setDefaultValue(attr.getDefaultValue());

		if (StringUtils.isNotEmpty(attr.getDefinition()))
			attrModel.setDefinition(attr.getDefinition());

		if (StringUtils.isNotEmpty(attr.getLength()))
			attrModel.setLengthPrecision(attr.getLength());
	}

	private IERAttribute mergeAttribute(ERModelEditor editor,
			IEREntity entityModel, Attribute attr, IERDatatype dataType)
			throws InvalidEditingException, ClassNotFoundException, ProjectNotFoundException {


		IERAttribute attribute = erFinder.findERAttribute(entityModel, attr.getLogicalName());
		
		if(attribute == null){
			logger.info(Messages.getMessage("log.create_attribute",
					entityModel.getName(), attr.getLogicalName()));
			
			return editor.createERAttribute(entityModel,
					attr.getLogicalName(), attr.getPhysicalName(), dataType);
		}
		
		logger.info(Messages.getMessage("log.update_attribute",
				entityModel.getName(), attr.getLogicalName()));

		updateAttribute(attr, dataType, attribute);
		
		return attribute;
	}

	private void updateAttribute(Attribute attr, IERDatatype dataType,
			IERAttribute attribute) throws InvalidEditingException {
		attribute.setPhysicalName(attr.getPhysicalName());
		attribute.setDatatype(dataType);
	}

	private IERAttribute mergeAttributeUsingDomain(ERModelEditor editor,
			IEREntity entityModel, Attribute attr, IERDomain domain)
			throws InvalidEditingException, ClassNotFoundException, ProjectNotFoundException {
		
		IERAttribute attribute = erFinder.findERAttribute(entityModel, attr.getLogicalName());

		if(attribute == null){
			logger.info(Messages.getMessage("log.create_attribute_using_domain",
					entityModel.getName(), attr.getLogicalName(), domain.getName()));

			return editor.createERAttribute(entityModel,
				attr.getLogicalName(), attr.getPhysicalName(), domain);
		}
		
		logger.info(Messages.getMessage("log.update_attribute_using_domain",
				entityModel.getName(), attr.getLogicalName(), domain.getName()));

		return attribute;
	}

	private void aboartTransaction() {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI()
					.getProjectAccessor();
			projectAccessor.getTransactionManager().abortTransaction();
		} catch (Exception e) {
			//
		}
	}
}
