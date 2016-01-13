package excel2er.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.ERModelEditor;
import com.change_vision.jude.api.inf.editor.ITransactionManager;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERDatatype;
import com.change_vision.jude.api.inf.model.IERDomain;
import com.change_vision.jude.api.inf.model.IERModel;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Domain;
import excel2er.models.DomainConfiguration;
import excel2er.models.operation.DomainOperations;
import excel2er.services.finder.DataTypeFinder;
import excel2er.services.finder.DomainFinder;
import excel2er.services.finder.ERModelFinder;
import excel2er.services.operations.ERDomainOperations;

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
        try {
            domains = new DomainOperations().addNeedCreateParentDomains(domains);
        } catch (ClassNotFoundException e) {
            log_error(Messages.getMessage("log.error.failed.get.parent.domain", e.getMessage()), e);
        } catch (ProjectNotFoundException e) {
            log_error(Messages.getMessage("error.project.not.found"), e);
            throw new ApplicationException(e);
        }

		for (Domain domain : domains) {
			String domainName = domain.getLogicalName();
             IERDomain overwriteAstahModel = null;
            try {
                overwriteAstahModel = overwriteAstahModel(domain);
            } catch (ApplicationException e) {
                log_info(Messages.getMessage("log.error.overwrite_domain_end", domainName));
                continue;
            }
            if (overwriteAstahModel != null) {
                continue;
            }
			try{
				createAstahModel(domain);
			}catch(ApplicationException e){
				//continue import
				log_info(Messages.getMessage("log.error.create_domain_end", domainName));
			}
		}
		
		return result;
	}

    private IERDomain overwriteAstahModel(Domain domain) {
        String domainFullName = domain.getFullLogicalName();
        try {
            IERDomain erDomain = getERDomain(domain);
            if (erDomain == null) {
                return null;
            }

            logger.info(Messages.getMessage("log.overwrite_domain", domainFullName));
            TransactionManager.beginTransaction();

            overwriteLogicalName(erDomain, domain);
            overwritePhysicalName(erDomain, domain);
            overwriteDatatype(erDomain, domain);
            overwriteParentDomain(erDomain, domain);
            setAdditionalProperty(erDomain, domain);

            TransactionManager.endTransaction();
            log_info(Messages.getMessage("log.overwrite_domain_end", domainFullName));

            return erDomain;

        } catch (ClassNotFoundException e) {
            TransactionManager.abortTransaction();
            log_error(
                    Messages.getMessage("log.error.overwrite_domain", domainFullName,
                            e.getMessage()), e);
            throw new ApplicationException(e);
        } catch (ProjectNotFoundException e) {
            TransactionManager.abortTransaction();
            log_error(Messages.getMessage("error.project.not.found"), e);
            throw new ApplicationException(e);
        } catch (InvalidEditingException e) {
            TransactionManager.abortTransaction();
            if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                TransactionManager.abortTransaction();
                log_error(Messages.getMessage("log.error.overwrite_domain.parameter_error",
                        domainFullName));
                throw new ApplicationException(e);
            }
            if (StringUtils.equals(e.getKey(), InvalidEditingException.NAME_DOUBLE_ERROR_KEY)) {
                log_error(Messages.getMessage("log.error.overwrite_domain.duplicate_entity",
                        domainFullName));
                throw new ApplicationException(e);
            }
            log_error(Messages.getMessage("log.error.overwrite_domain.invalideditingexception",
                    domainFullName, e.getKey()), e);
            throw new ApplicationException(e);
        }
    }

    void overwriteParentDomain(IERDomain erDomain, Domain domain) throws ClassNotFoundException,
            ProjectNotFoundException, InvalidEditingException {
        IElement container = erDomain.getContainer();
        if ((container == null || !(container instanceof IERDomain))
                && StringUtils.isEmpty(domain.getParentDomain())) {
            return;
        }
        if (container != null
                && StringUtils.equals(
                        new ERDomainOperations().getFullLogicalName((IERDomain) container,
                                domain.getNamespaceSeparator()), domain.getParentDomain())) {
            return;
        }
        String domainFullName = domain.getFullLogicalName();
        try {
            erDomain.setParentDomain(getParentERDomain(domain));
        } catch (InvalidEditingException e) {
            if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                TransactionManager.abortTransaction();
                log_error(Messages.getMessage(
                        "log.error.overwrite_parent_domain_domain.parameter_error", domainFullName));
                throw new ApplicationException(e);
            }
            throw e;
        } catch (ApplicationException e) {
            TransactionManager.abortTransaction();
            throw e;
        }
    }

    void overwriteDatatype(IERDomain erDomain, Domain domain) throws ProjectNotFoundException,
            ClassNotFoundException, InvalidEditingException {
        if (StringUtils.equals(erDomain.getDatatypeName(), domain.getDataType())) {
            return;
        }
        String domainFullName = domain.getFullLogicalName();
        try {
            erDomain.setDatatype(getDataType(domain));
        } catch (InvalidEditingException e) {
            if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                TransactionManager.abortTransaction();
                log_error(Messages.getMessage(
                        "log.error.overwrite_datatype_domain.parameter_error", domainFullName));
                throw new ApplicationException(e);
            }
            throw e;
        }
    }

    void overwritePhysicalName(IERDomain erDomain, Domain domain) throws InvalidEditingException {
        if (StringUtils.equals(erDomain.getPhysicalName(), domain.getPhysicalName())) {
            return;
        }
        String domainFullName = domain.getFullLogicalName();
        try {
            erDomain.setPhysicalName(domain.getPhysicalName());
        } catch (InvalidEditingException e) {
            if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                TransactionManager.abortTransaction();
                log_error(Messages.getMessage(
                        "log.error.overwrite_physical_domain.parameter_error", domainFullName));
                throw new ApplicationException(e);
            }
            throw e;
        }
    }

    void overwriteLogicalName(IERDomain erDomain, Domain domain) throws InvalidEditingException {
        if (StringUtils.equals(erDomain.getLogicalName(), domain.getLogicalName())) {
            return;
        }
        String domainFullName = domain.getFullLogicalName();
        try {
            erDomain.setLogicalName(domain.getLogicalName());
        } catch (InvalidEditingException e) {
            if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                TransactionManager.abortTransaction();
                log_error(Messages.getMessage("log.error.overwrite_logical_domain.parameter_error",
                        domainFullName));
                throw new ApplicationException(e);
            }
            throw e;
        }
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
        String domainFullName = domain.getFullLogicalName();
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI()
					.getProjectAccessor();
			ERModelEditor editor = projectAccessor.getModelEditorFactory()
					.getERModelEditor();

			ITransactionManager transactionManager = projectAccessor
					.getTransactionManager();
			transactionManager.beginTransaction();

            IERModel erModel = getERModel();
            IERDatatype dataType = getDataType(domain);
            IERDomain parentERDomain = getParentERDomain(domain);

            IERDomain domainModel = editor.createERDomain(erModel, parentERDomain,
                    domain.getLogicalName(), domain.getPhysicalName(), dataType);

			setAdditionalProperty(domainModel,domain);
			
            logger.info(Messages.getMessage("log.create_domain", domainFullName));
			
			projectAccessor.getTransactionManager().endTransaction();
			result.inclementEntitesCount();
            log_info(Messages.getMessage("log.create_domain_end", domainFullName));
			return domainModel;
		} catch (ClassNotFoundException e) {
            log_error(
                    Messages.getMessage("log.error.create_domain", domainFullName, e.getMessage()),
                    e);
			
			aboartTransaction();
			
			throw new ApplicationException(e);
		} catch (InvalidEditingException e) {
			if(StringUtils.equals(e.getKey(),InvalidEditingException.PARAMETER_ERROR_KEY)){
                log_error(Messages.getMessage("log.error.create_domain.parameter_error",
                        domainFullName));
			}else if(StringUtils.equals(e.getKey(),InvalidEditingException.NAME_DOUBLE_ERROR_KEY)){
                log_error(Messages.getMessage("log.error.create_domain.duplicate_entity",
                        domainFullName));
			}else { 
                log_error(Messages.getMessage("log.error.create_domain.invalideditingexception",
                        domainFullName, e.getKey()), e);
			}
			
			aboartTransaction();
			
			throw new ApplicationException(e);
		} catch (ProjectNotFoundException e) {
			log_error(Messages.getMessage("error.project.not.found"), e);
			
			aboartTransaction();
			throw new ApplicationException(e);
        } catch (ApplicationException e) {
            aboartTransaction();
            throw e;
		}
	}

    private IERDomain getERDomain(Domain domain) throws ClassNotFoundException,
            ProjectNotFoundException {
        return new DomainFinder().find(domain.getFullLogicalName(), domain.getNamespaceSeparator());
    }

    IERDomain getParentERDomain(Domain domain) throws ProjectNotFoundException {
        if (StringUtils.isEmpty(domain.getParentDomain())) {
            return null;
        }
        IERDomain parentERDomain;
        try {
            parentERDomain = new DomainFinder().find(domain.getParentDomain(),
                    domain.getNamespaceSeparator());
        } catch (ClassNotFoundException e) {
            throw new ApplicationException(Messages.getMessage(
                    "log.error.failed.get.parent.domain",
                    String.format("%s is not found.", domain.getParentDomain())), e);
        }
        if (parentERDomain == null) {
            throw new ApplicationException(Messages.getMessage(
                    "log.error.failed.get.parent.domain",
                    String.format("%s is not found.", domain.getParentDomain())));
        }
        return parentERDomain;
    }

    private IERModel getERModel() throws ProjectNotFoundException, InvalidEditingException,
            ClassNotFoundException {
        IERModel erModel = new ERModelFinder().find();
        if (erModel != null) {
          return erModel;
        }
        ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
        ERModelEditor editor = projectAccessor.getModelEditorFactory().getERModelEditor();
        return editor.createERModel(projectAccessor.getProject(), "ER Model");
    }

    private IERDatatype getDataType(Domain domain) throws ProjectNotFoundException,
            ClassNotFoundException, InvalidEditingException {
        if (StringUtils.isEmpty(domain.getDataType())) {
            return null;
        }
        IERDatatype dataType = new DataTypeFinder().find(domain.getDataType());
        if (dataType != null) {
            return dataType;
        }
        logger.debug(Messages.getMessage("log.create.datatype", domain.getDataType()));
        ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
        ERModelEditor editor = projectAccessor.getModelEditorFactory().getERModelEditor();
        return createDataType(editor, getERModel(), domain.getDataType());
    }
	
	private IERDatatype createDataType(ERModelEditor editor,IERModel erModel, String dataType) throws InvalidEditingException {
		return editor.createERDatatype(erModel, dataType);
	}

    private void setAdditionalProperty(IERDomain domainModel, Domain domain)
            throws InvalidEditingException {
        setAlias1(domainModel, domain);
        setAlias2(domainModel, domain);
        setLengthPrecision(domainModel, domain);
        setNotNull(domainModel, domain);
        setDefinition(domainModel, domain);
	}

    private void setDefinition(IERDomain domainModel, Domain domain) throws InvalidEditingException {
        if (StringUtils.isNotEmpty(domain.getDefinition())) {
            domainModel.setDefinition(domain.getDefinition());
        }
    }

    private void setNotNull(IERDomain domainModel, Domain domain) throws InvalidEditingException {
        domainModel.setNotNull(StringUtils.isNotEmpty(domain.getNotNull()));
    }

    private void setLengthPrecision(IERDomain domainModel, Domain domain)
            throws InvalidEditingException {
        if (StringUtils.isNotEmpty(domain.getLengthAndPrecision())) {
            domainModel.setLengthPrecision(domain.getLengthAndPrecision());
        }
    }

    private void setAlias2(IERDomain domainModel, Domain domain) throws InvalidEditingException {
        if (StringUtils.isNotEmpty(domain.getAlias2())) {
            domainModel.setAlias2(domain.getAlias2());
        }
    }

    private void setAlias1(IERDomain domainModel, Domain domain) throws InvalidEditingException {
        if (StringUtils.isNotEmpty(domain.getAlias1())) {
            domainModel.setAlias1(domain.getAlias1());
        }
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
