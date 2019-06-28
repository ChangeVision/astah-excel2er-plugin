package excel2er.services;

import java.util.List;

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
import com.change_vision.jude.api.inf.model.IElement;
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

        logger.info("######## Start ########");
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
        logger.info("######## Finish ########");

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
                logger.debug("Create ER Model");
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
                    final String dataTypeName = attr.getDataType();
                    IERDatatype dataType = dataTypeFinder.find(dataTypeName);
					if (dataType == null) {
                        if (dataTypeName == null) {
                            log_error(Messages.getMessage("log.error.create_entity.datatype_empty",
                                    attr.getLogicalName()));
                            aboartTransaction();
                            throw new ApplicationException();
                        }
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

        String entityName = getEntityName(attrModel);
        String attrName = attrModel.getName();
        logger.debug(String.format("Set PhysicalName \"%s\" to %s::%s}", attr.getPhysicalName(),
                entityName, attrName));
		attrModel.setPhysicalName(attr.getPhysicalName());

        logger.debug(String.format("Set PrimaryKey \"%s\" to %s::%s", attr.isPrimaryKey(),
                entityName, attrName));
		attrModel.setPrimaryKey(attr.isPrimaryKey());
		if(attr.isPrimaryKey()){
            logger.debug(String.format("Set NotNull \"%b\" to %s::%s", true, entityName, attrName));
			attrModel.setNotNull(true);
		}

        if (StringUtils.isNotEmpty(attr.getDefaultValue())) {
            logger.debug(String.format("Set DefaultValue \"%s\" to %s::%s", attr.getDefaultValue(),
                    entityName, attrName));
			attrModel.setDefaultValue(attr.getDefaultValue());
        }

        if (StringUtils.isNotEmpty(attr.getDefinition())) {
            logger.debug(String.format("Set Definition \"%s\" to %s::%s", attr.getDefinition(),
                    entityName, attrName));
			attrModel.setDefinition(attr.getDefinition());
        }
	}

	private void setAdditionalProperty(Attribute attr, IERAttribute attrModel)
			throws InvalidEditingException {

        String entityName = getEntityName(attrModel);
        String attrName = attrModel.getName();
        logger.debug(String.format("Set PrimaryKey \"%s\" to %s::%s", attr.isPrimaryKey(),
                entityName, attrName));
		attrModel.setPrimaryKey(attr.isPrimaryKey());

        boolean notNull;
		if(attr.isPrimaryKey()){
            notNull = true;
		}else{
            notNull = attr.isNotNull();
		}
        logger.debug(String.format("Set NotNull \"%b\" to %s::%s", notNull, entityName, attrName));
        attrModel.setNotNull(notNull);

        if (StringUtils.isNotEmpty(attr.getDefaultValue())) {
            logger.debug(String.format("Set DefaultValue \"%s\" to %s::%s", attr.getDefaultValue(),
                    entityName, attrName));
			attrModel.setDefaultValue(attr.getDefaultValue());
        }

        if (StringUtils.isNotEmpty(attr.getDefinition())) {
            logger.debug(String.format("Set Definition \"%s\" to %s::%s", attr.getDefinition(),
                    entityName, attrName));
			attrModel.setDefinition(attr.getDefinition());
        }

        if (StringUtils.isNotEmpty(attr.getLength())) {
            logger.debug(String.format("Set LengthPrecision \"%s\" to %s::%s", attr.getLength(),
                    entityName, attrName));
            try {
                attrModel.setLengthPrecision(attr.getLength());
            } catch (InvalidEditingException e) {
                IERDatatype dataType = attrModel.getDatatype();
                logger.debug(
                        String.format("%s is Invalid LengthPrecision. %s has constraint is %s, %s.",
                                attr.getLength(), dataType, dataType.getLengthConstraint(),
                                dataType.getPrecisionConstraint()));
                throw e;
            }
        }
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
        String entityName = getEntityName(attribute);
        String attrName = attribute.getName();
        logger.debug(String.format("Set PhysicalName \"%s\" to %s::%s", attr.getPhysicalName(),
                entityName, attrName));
		attribute.setPhysicalName(attr.getPhysicalName());
        logger.debug(
                String.format("Set DataType \"%s\" to %s::%s", dataType, entityName, attrName));
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

    private String getEntityName(IERAttribute attrModel) {
        IElement owner = attrModel.getOwner();
        if (owner instanceof IEREntity) {
            return ((IEREntity) owner).getName();
        }
        return "";
    }
}
