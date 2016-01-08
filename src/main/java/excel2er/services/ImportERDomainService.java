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
import com.change_vision.jude.api.inf.model.IERDatatype;
import com.change_vision.jude.api.inf.model.IERDomain;
import com.change_vision.jude.api.inf.model.IERModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Domain;
import excel2er.models.DomainConfiguration;
import excel2er.services.finder.DataTypeFinder;
import excel2er.services.finder.DomainFinder;

public class ImportERDomainService {
	private static final Logger logger = LoggerFactory
			.getLogger(ImportERDomainService.class);

	private Result result;
	
	public ImportERDomainService() {
		init();
	}
	
	private void init() {
		result = new Result();
	}

	public Result importERDomain(DomainConfiguration configuration) {
		
		init();
		
		ParseExcelToDomainModelService parseService = new ParseExcelToDomainModelService();

		List<Domain> domains = parseService.parse(configuration);

		for (Domain domain : domains) {
			String domainName = domain.getLogicalName();
			try{
				createAstahModel(domain);
			}catch(ApplicationException e){
				//continue import
				log_info(Messages.getMessage("log.error.create_domain_end", domainName));
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
	
	IERDomain createAstahModel(Domain domain) {
		String domainName = domain.getLogicalName();
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

			DataTypeFinder dataTypeFinder = new DataTypeFinder();
			IERDatatype dataType = dataTypeFinder.find(domain
					.getDataType());
			if (dataType == null) {
				logger.debug(Messages.getMessage(
						"log.create.datatype",
						domain.getDataType()));
				dataType = createDataType(editor,erModel,domain.getDataType());
			}
			
            IERDomain parentERDomain = new DomainFinder().find(domain.getParentDomain(),
                    domain.getNamespaceSeparator());
            IERDomain domainModel = editor.createERDomain(erModel, parentERDomain,
                    domain.getLogicalName(), domain.getPhysicalName(), dataType);

			setAdditionalProperty(domain,domainModel);
			
			logger.info(Messages.getMessage("log.create_domain", domainName));
			
			projectAccessor.getTransactionManager().endTransaction();
			result.inclementEntitesCount();
			log_info(Messages.getMessage("log.create_domain_end", domainName));
			return domainModel;
		} catch (ClassNotFoundException e) {
			log_error(Messages.getMessage("log.error.create_domain",
					domainName, e.getMessage()), e);
			
			aboartTransaction();
			
			throw new ApplicationException(e);
		} catch (InvalidEditingException e) {
			if(StringUtils.equals(e.getKey(),InvalidEditingException.PARAMETER_ERROR_KEY)){
				log_error(Messages.getMessage("log.error.create_domain.parameter_error",
						domainName));
			}else if(StringUtils.equals(e.getKey(),InvalidEditingException.NAME_DOUBLE_ERROR_KEY)){
				log_error(Messages.getMessage("log.error.create_domain.duplicate_entity",
						domainName));
			}else { 
				log_error(Messages.getMessage("log.error.create_domain.invalideditingexception",
						domainName,e.getKey()), e);
			}
			
			aboartTransaction();
			
			throw new ApplicationException(e);
		} catch (ProjectNotFoundException e) {
			log_error(Messages.getMessage("error.project.not.found"), e);
			
			aboartTransaction();
			throw new ApplicationException(e);
		}
	}
	
	private IERDatatype createDataType(ERModelEditor editor,IERModel erModel, String dataType) throws InvalidEditingException {
		return editor.createERDatatype(erModel, dataType);
	}

	private void setAdditionalProperty(Domain domain, IERDomain domainModel)
			throws InvalidEditingException {

		if (StringUtils.isNotEmpty(domain.getDefinition()))
			domainModel.setDefinition(domain.getDefinition());

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
