package excel2er.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.ERModelEditor;
import com.change_vision.jude.api.inf.editor.ITransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERAttribute;
import com.change_vision.jude.api.inf.model.IERDatatype;
import com.change_vision.jude.api.inf.model.IERDomain;
import com.change_vision.jude.api.inf.model.IEREntity;
import com.change_vision.jude.api.inf.model.IERModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Attribute;
import excel2er.models.Configuration;
import excel2er.models.Entity;
import excel2er.services.finder.DataTypeFinder;
import excel2er.services.finder.DomainFinder;
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

		for (Entity entity : entities) {
			String entityName = entity.getEntityLogicalName();
			try {
				createAstahModel(entity);
			} catch (ApplicationException e) {
				// continue import
				log_info(Messages.getMessage("log.error.create_entity_end",
						entityName));
			}
		}

		return result;
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

	IEREntity createAstahModel(Entity entity) {
		String entityName = entity.getEntityLogicalName();
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI()
					.getProjectAccessor();
			ERModelEditor editor = projectAccessor.getModelEditorFactory()
					.getERModelEditor();

			ITransactionManager transactionManager = projectAccessor
					.getTransactionManager();
			transactionManager.beginTransaction();

			INamedElement[] candidate = projectAccessor
					.findElements(IERModel.class);
			IERModel erModel = null;
			if (candidate == null || candidate.length == 0) {
				erModel = editor.createERModel(projectAccessor.getProject(),
						"ER Model");
			} else {
				erModel = (IERModel) candidate[0];
			}

			IEREntity entityModel = mergeEntity(entity, entityName,
					editor, erModel);

			DomainFinder domainFinder = new DomainFinder();
			DataTypeFinder dataTypeFinder = new DataTypeFinder();

			for (Attribute attr : entity.getAttributes()) {
				IERDomain domain = domainFinder.find(attr);
				IERAttribute attrModel = null;
				if (domain != null) {
					attrModel = mergeAttributeUsingDomain(editor, entityModel,
							attr, domain);

					setEditablePropertyUsingDomain(attr, attrModel);
				} else {
					IERDatatype dataType = dataTypeFinder.find(attr
							.getDataType());
					if (dataType == null) {
						logger.debug(Messages.getMessage("log.create.datatype",
								attr.getDataType()));
						dataType = createDataType(editor, erModel,
								attr.getDataType());
					}
					attrModel = mergeAttribute(editor, entityModel, attr,
							dataType);

					setAdditionalProperty(attr, attrModel);
				}

			}
			projectAccessor.getTransactionManager().endTransaction();
            result.inclementImportedElementsCount();
			return entityModel;
		} catch (ClassNotFoundException e) {
			log_error(Messages.getMessage("log.error.create_entity",
					entityName, e.getMessage()), e);

			aboartTransaction();

			throw new ApplicationException(e);
		} catch (InvalidEditingException e) {
			if (StringUtils.equals(e.getKey(),
					InvalidEditingException.PARAMETER_ERROR_KEY)) {
                log_error(e.getMessage());
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

	private IEREntity mergeEntity(Entity entity, String entityName,
			ERModelEditor editor, IERModel erModel)
			throws InvalidEditingException, ClassNotFoundException,
			ProjectNotFoundException {

		IEREntity model = erFinder.findEREntity(entityName);
		if (model == null) {
			logger.info(Messages.getMessage("log.create_entity", entityName));
            try {
                return editor.createEREntity(erModel.getSchemata()[0], entityName,
                        entity.getEntityPhysicalName());
            } catch (InvalidEditingException e) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("Entity LogicalName", entityName);
                throw addParam(e, params);
            }
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
        try {
            return editor.createERDatatype(erModel, dataType);
        } catch (InvalidEditingException e) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("DataType", dataType);
                throw addParam(e, params);
        }

	}

	private void setEditablePropertyUsingDomain(Attribute attr,
			IERAttribute attrModel) throws InvalidEditingException {

        try {
            attrModel.setPhysicalName(attr.getPhysicalName());
        } catch (InvalidEditingException e) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("Attr PhysicalName", attr.getPhysicalName());
            if (attrModel.getOwner() instanceof INamedElement) {
                String entity = ((INamedElement) attrModel.getOwner()).getName();
                params.put("Entity",entity);
            }
            throw addParam(e, params);
        }

        try {
            attrModel.setPrimaryKey(attr.isPrimaryKey());
            if(attr.isPrimaryKey()){
                attrModel.setNotNull(true);
            }
        } catch (InvalidEditingException e) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put( "PrimaryKey", "" + attr.isPrimaryKey());
            params.put("Attr LogicalName", attr.getLogicalName());
            if (attrModel.getOwner() instanceof INamedElement) {
                String entity = ((INamedElement) attrModel.getOwner()).getName();
                params.put( "Entity", entity);
            }
            throw addParam(e,params);
        }

        try {
            if (StringUtils.isNotEmpty(attr.getDefaultValue()))
                attrModel.setDefaultValue(attr.getDefaultValue());
        } catch (InvalidEditingException e) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("DefaultValue", attr.getDefaultValue());
            params.put("Attr LogicalName", attr.getLogicalName());
            if (attrModel.getOwner() instanceof INamedElement) {
                String entity = ((INamedElement) attrModel.getOwner()).getName();
                params.put( "Entity", entity);
            }
            throw addParam(e, params);
        }

        try {
            if (StringUtils.isNotEmpty(attr.getDefinition()))
                attrModel.setDefinition(attr.getDefinition());
        } catch (InvalidEditingException e) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("Definition", attr.getDefinition());
            params.put("Attr LogicalName", attr.getLogicalName());
            if (attrModel.getOwner() instanceof INamedElement) {
                String entity = ((INamedElement) attrModel.getOwner()).getName();
                params.put( "Entity", entity);
            }
            throw addParam(e, params);
        }
	}

	private void setAdditionalProperty(Attribute attr, IERAttribute attrModel)
			throws InvalidEditingException {

        try {
            attrModel.setPrimaryKey(attr.isPrimaryKey());
            if (attr.isPrimaryKey()) {
                attrModel.setNotNull(true);
            } else {
                attrModel.setNotNull(attr.isNotNull());
            }
        } catch (InvalidEditingException e) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("PrimaryKey", "" + attr.isPrimaryKey());
            params.put("Attr LogicalName", attr.getLogicalName());
            if (attrModel.getOwner() instanceof INamedElement) {
                String entity = ((INamedElement) attrModel.getOwner()).getName();
                params.put( "Entity", entity);
            }
            throw addParam(e, params);
        }

        try {
            if (StringUtils.isNotEmpty(attr.getDefaultValue()))
                attrModel.setDefaultValue(attr.getDefaultValue());
        } catch (InvalidEditingException e) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("Default", attr.getDefaultValue());
            params.put("Attr LogicalName", attr.getLogicalName());
            if (attrModel.getOwner() instanceof INamedElement) {
                String entity = ((INamedElement) attrModel.getOwner()).getName();
                params.put( "Entity", entity);
            }
            throw addParam(e, params);
        }

        try {
            if (StringUtils.isNotEmpty(attr.getDefinition()))
                attrModel.setDefinition(attr.getDefinition());
        } catch (InvalidEditingException e) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("Definition", attr.getDefinition());
            params.put("Attr LogicalName", attr.getLogicalName());
            if (attrModel.getOwner() instanceof INamedElement) {
                String entity = ((INamedElement) attrModel.getOwner()).getName();
                params.put( "Entity", entity);
            }
            throw addParam(e, params);
        }

        try {
            if (StringUtils.isNotEmpty(attr.getLength()))
                attrModel.setLengthPrecision(attr.getLength());
        } catch (InvalidEditingException e) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("Length", attr.getLength());
            params.put("DataType", attr.getDataType());
            params.put("Attr LogicalName", attr.getLogicalName());
            if (attrModel.getOwner() instanceof INamedElement) {
                String entity = ((INamedElement) attrModel.getOwner()).getName();
                params.put( "Entity", entity);
            }
            throw addParam(e, params);
        }
	}

	private IERAttribute mergeAttribute(ERModelEditor editor,
			IEREntity entityModel, Attribute attr, IERDatatype dataType)
			throws InvalidEditingException, ClassNotFoundException, ProjectNotFoundException {


		IERAttribute attribute = erFinder.findERAttribute(entityModel, attr.getLogicalName());
		
		if(attribute == null){
			logger.info(Messages.getMessage("log.create_attribute",
					entityModel.getName(), attr.getLogicalName()));
            try {
                return editor.createERAttribute(entityModel, attr.getLogicalName(),
                        attr.getPhysicalName(), dataType);
            } catch (InvalidEditingException e) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("Attr LogicalName", attr.getLogicalName());
                params.put("Attr PhysicalName", attr.getPhysicalName());
                params.put("DataType", dataType.getName());
                params.put( "Entity", entityModel.getName());
                throw addParam(e, params);
            }

		}
		
		logger.info(Messages.getMessage("log.update_attribute",
				entityModel.getName(), attr.getLogicalName()));

		updateAttribute(attr, dataType, attribute);
		
		return attribute;
	}

	private void updateAttribute(Attribute attr, IERDatatype dataType,
			IERAttribute attribute) throws InvalidEditingException {
        try {
            attribute.setPhysicalName(attr.getPhysicalName());
        } catch (InvalidEditingException e) {
            String entity = null;
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("Attr PhysicalName", attr.getPhysicalName());
            if (attribute.getOwner() instanceof INamedElement) {
                entity = ((INamedElement) attribute.getOwner()).getName();
                params.put( "Entity", entity);
            }
            throw addParam(e, params);
        }

        try {
            attribute.setDatatype(dataType);
        } catch (InvalidEditingException e) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("DataType", dataType.getName());
            throw addParam(e, params);
        }
	}

	private IERAttribute mergeAttributeUsingDomain(ERModelEditor editor,
			IEREntity entityModel, Attribute attr, IERDomain domain)
			throws InvalidEditingException, ClassNotFoundException, ProjectNotFoundException {
		
		IERAttribute attribute = erFinder.findERAttribute(entityModel, attr.getLogicalName());

		if(attribute == null){
			logger.info(Messages.getMessage("log.create_attribute_using_domain",
					entityModel.getName(), attr.getLogicalName(), domain.getName()));

            try {
                return editor.createERAttribute(entityModel, attr.getLogicalName(),
                        attr.getPhysicalName(), domain);
            } catch (InvalidEditingException e) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("Attr LogicalName", attr.getLogicalName());
                params.put("Attr PhysicalName", attr.getPhysicalName());
                params.put("Domain", domain.getName());
                params.put( "Entity", entityModel.getName());
                throw addParam(e, params);
            }
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

    private InvalidEditingException addParam(InvalidEditingException e,
            LinkedHashMap<String, String> params) {
        List<String> keyValueList = new ArrayList<String>();

        for (Map.Entry<String, String> param : params.entrySet()) {
            keyValueList.add(param.getKey() + ":" + param.getValue());
        }

        String paramStr = StringUtils.join(keyValueList, ", ");
        return new InvalidEditingException(e.getKey(), e.getMessage() + "( " + paramStr + " )");
    }

}
