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
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Attribute;
import excel2er.models.Configuration;
import excel2er.models.Entity;
import excel2er.services.finder.DataTypeFinder;
import excel2er.services.finder.DomainFinder;

public class ImportERModelService {
	private static final Logger logger = LoggerFactory
			.getLogger(ImportERModelService.class);

	private Result result;
	
	public ImportERModelService() {
		init();
	}
	
	private void init() {
		result = new Result();
	}

	public Result importERModel(Configuration configuration) {
		
		init();
		
		ParseExcelToEntityModelService parseService = new ParseExcelToEntityModelService();

		List<Entity> entities = parseService.parse(configuration);

		for (Entity entity : entities) {
			String entityName = entity.getEntityLogicalName();
			try{
				createAstahModel(entity);
			}catch(ApplicationException e){
				//continue import
				log_info(Messages.getMessage("log.error.create_entity_end", entityName));
			}
		}
		
		return result;
	}

	private void log_append(String message) {
		result.appendMessage(message);
		result.appendMessage(SystemUtils.LINE_SEPARATOR);
	}
	
	private void log_info(String message){
		logger.info(message);
		log_append(message);
	}

	private void log_error(String message) {
		logger.error(message);
		log_append(message);
		result.setErrorOccured(true);
	}
	
	private void log_error(String message,Throwable e) {
		logger.error(message,e);
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

			IEREntity entityModel = editor.createEREntity(
					erModel.getSchemata()[0], entityName,
					entity.getEntityPhysicalName());

			logger.info(Messages.getMessage("log.create_entity", entityName));

			DomainFinder domainFinder = new DomainFinder();
			DataTypeFinder dataTypeFinder = new DataTypeFinder();

			for (Attribute attr : entity.getAttributes()) {
				IERDomain domain = domainFinder.find(attr);
				IERAttribute attrModel = null;
				if (domain != null) {
					attrModel = createAttributeUsingDomain(editor, entityModel,
							attr, domain);

					setEditablePropertyUsingDomain(attr, attrModel);
				} else {
					IERDatatype dataType = dataTypeFinder.find(attr
							.getDataType());
					if (dataType == null) {
						log_error(Messages.getMessage(
								"log.error.create_attribute.missing_datatype",
								entityName, attr.getLogicalName()));
						continue;
					}
					attrModel = createAttribute(editor, entityModel, attr,
							dataType);

					setAdditionalProperty(attr, attrModel);
				}

			}
			projectAccessor.getTransactionManager().endTransaction();
			result.inclementEntitesCount();
			log_info(Messages.getMessage("log.create_entity_end", entityName));
			return entityModel;
		} catch (ClassNotFoundException e) {
			log_error(Messages.getMessage("log.error.create_entity",
					entityName, e.getMessage()), e);
			
			aboartTransaction();
			
			throw new ApplicationException(e);
		} catch (InvalidEditingException e) {
			if(StringUtils.equals(e.getKey(),InvalidEditingException.PARAMETER_ERROR_KEY)){
				log_error(Messages.getMessage("log.error.create_entity.parameter_error",
						entityName));
			}else if(StringUtils.equals(e.getKey(),InvalidEditingException.NAME_DOUBLE_ERROR_KEY)){
				log_error(Messages.getMessage("log.error.create_entity.duplicate_entity",
						entityName));
			}else { 
				log_error(Messages.getMessage("log.error.create_entity.invalideditiongexception",
						entityName,e.getKey()), e);
			}
			
			aboartTransaction();
			
			throw new ApplicationException(e);
		} catch (ProjectNotFoundException e) {
			log_error(Messages.getMessage("error.project.not.found"), e);
			
			aboartTransaction();
			throw new ApplicationException(e);
		}
	}

	private void setEditablePropertyUsingDomain(Attribute attr, IERAttribute attrModel)
			throws InvalidEditingException {
		
		attrModel.setPrimaryKey(attr.isPrimaryKey());

		if (StringUtils.isNotEmpty(attr.getDefaultValue()))
			attrModel.setDefaultValue(attr.getDefaultValue());

		if (StringUtils.isNotEmpty(attr.getDefinition()))
			attrModel.setDefinition(attr.getDefinition());
	}

	private void setAdditionalProperty(Attribute attr, IERAttribute attrModel)
			throws InvalidEditingException {

		attrModel.setPrimaryKey(attr.isPrimaryKey());

		attrModel.setNotNull(attr.isNotNull());

		if (StringUtils.isNotEmpty(attr.getDefaultValue()))
			attrModel.setDefaultValue(attr.getDefaultValue());

		if (StringUtils.isNotEmpty(attr.getDefinition()))
			attrModel.setDefinition(attr.getDefinition());

		if (StringUtils.isNotEmpty(attr.getLength()))
			attrModel.setLengthPrecision(attr.getLength());
	}

	private IERAttribute createAttribute(ERModelEditor editor,
			IEREntity entityModel, Attribute attr, IERDatatype dataType)
			throws InvalidEditingException {
		logger.info(Messages.getMessage("log.create_attribute",
				entityModel.getName(), attr.getLogicalName()));

		IERAttribute attrModel = editor.createERAttribute(entityModel,
				attr.getLogicalName(), attr.getPhysicalName(), dataType);

		return attrModel;
	}

	private IERAttribute createAttributeUsingDomain(ERModelEditor editor,
			IEREntity entityModel, Attribute attr, IERDomain domain)
			throws InvalidEditingException {

		logger.info(Messages.getMessage("log.create_attribute_using_domain",
				entityModel.getName(), attr.getLogicalName(), domain.getName()));

		IERAttribute attrModel = editor.createERAttribute(entityModel,
				attr.getLogicalName(), attr.getPhysicalName(), domain);

		return attrModel;
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

	public static class Result {
		private int createdEntitiesCount = 0;
		private StringBuilder sb = new StringBuilder();
		private boolean errorOccured = false;
		
		public void inclementEntitesCount(){
			createdEntitiesCount++;
		}
		
		public void appendMessage(String message){
			sb.append(message);
		}
		
		public String getMessage(){
			return sb.toString();
		}
		
		public int getCreatedEntitiesCount(){
			return createdEntitiesCount;
		}
		
		public void setErrorOccured(boolean value){
			errorOccured = value;
		}
		
		public boolean isErrorOccured(){
			return errorOccured;
		}
	}
}
