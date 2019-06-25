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
        logger.info("######## Start ########");
		
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
                overwriteAstahModel = overwriteAstahModel(configuration, domain);
            } catch (ApplicationException e) {
                log_info(Messages.getMessage("log.error.overwrite_domain_end", domainName));
                continue;
            }
            if (overwriteAstahModel != null) {
                continue;
            }
			try{
                createAstahModel(configuration, domain);
			}catch(ApplicationException e){
				//continue import
				log_info(Messages.getMessage("log.error.create_domain_end", domainName));
			}
		}
		
        logger.info("######## Finish ########");
		return result;
	}

    private IERDomain overwriteAstahModel(DomainConfiguration configuration, Domain domain) {
        String domainFullName = domain.getFullLogicalName();
        try {
            IERDomain erDomain = getERDomain(domain);
            if (erDomain == null) {
                return null;
            }

            if (!isNeedOverwrites(configuration, erDomain, domain)) {
                log_info(Messages.getMessage(
                        "log.info.overwrite_domain.is_unnecessary_change_value", domainFullName));
                return erDomain;
            }

            logger.debug(Messages.getMessage("log.overwrite_domain", domainFullName));
            TransactionManager.beginTransaction();

            overwriteLogicalName(erDomain, domain);
            if (StringUtils.isNotEmpty(configuration.getPhysicalCol())) {
                overwritePhysicalName(erDomain, domain);
            }
            overwriteDatatype(erDomain, domain);
            if (StringUtils.isNotEmpty(configuration.getLengthAndPrecisionCol())) {
                overwriteLengthPrecision(erDomain, domain);
            }
            if (StringUtils.isNotEmpty(configuration.getParentDomainCol())) {
                overwriteParentDomain(erDomain, domain);
            }
            setAdditionalProperty(configuration, erDomain, domain);

            TransactionManager.endTransaction();
            result.inclementImportedElementsCount();
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
            if (StringUtils.equals(e.getKey(), InvalidEditingException.READ_ONLY_KEY)) {
                log_error(Messages.getMessage("log.error.overwrite_domain.read_only_error",
                        domainFullName));
                throw new ApplicationException(e);
            }
            log_error(Messages.getMessage("log.error.overwrite_domain.invalideditingexception",
                    domainFullName, e.getKey()), e);
            throw new ApplicationException(e);
        }
    }

    boolean isNeedOverwrites(DomainConfiguration configuration, IERDomain erDomain, Domain domain) {
        if (StringUtils.isNotEmpty(configuration.getPhysicalCol())) {
            if (isNeedChangePhysicalName(erDomain, domain)) {
                return true;
            }
        }
        if (StringUtils.isNotEmpty(configuration.getAlias1Col())) {
            if (isNeedChangeAlias1(erDomain, domain)) {
                return true;
            }
        }
        if (StringUtils.isNotEmpty(configuration.getAlias2Col())) {
            if (isNeedChangeAlias2(erDomain, domain)) {
                return true;
            }
        }
        if (StringUtils.isNotEmpty(configuration.getLengthAndPrecisionCol())) {
            if (isNeedChangeLengthPrecision(erDomain, domain)) {
                return true;
            }
        }
        if (StringUtils.isNotEmpty(configuration.getNotNullCol())) {
            if (isNeedChangeNotNull(erDomain, domain)) {
                return true;
            }
        }
        if (StringUtils.isNotEmpty(configuration.getParentDomainCol())) {
            if (isNeedChangeParentDomain(erDomain, domain)) {
                return true;
            }
        }
        if (StringUtils.isNotEmpty(configuration.getDefinitionCol())) {
            if (isNeedChangeDefinition(erDomain, domain)) {
                return true;
            }
        }
        return isNeedChangeLogicalName(erDomain, domain) || isNeedChangeDatatype(erDomain, domain);
    }

    void overwriteParentDomain(IERDomain erDomain, Domain domain) throws ClassNotFoundException,
            ProjectNotFoundException, InvalidEditingException {
        if (!isNeedChangeParentDomain(erDomain, domain)) {
            return;
        }
        String domainFullName = domain.getFullLogicalName();
        IERDomain parentDomain = getParentERDomain(domain);
        logger.debug(String.format("Set ParentDomain \"%s\" to %s", parentDomain.getName(),
                domainFullName));
        try {
            erDomain.setParentDomain(parentDomain);
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

    private boolean isNeedChangeParentDomain(IERDomain erDomain, Domain domain) {
        IElement container = erDomain.getContainer();
        if ((container == null || !(container instanceof IERDomain))
                && StringUtils.isEmpty(domain.getParentDomain())) {
            return false;
        }
        if (container != null
                && !isNeedChangeValue(
                        new ERDomainOperations().getFullLogicalName((IERDomain) container,
                                domain.getNamespaceSeparator()), domain.getParentDomain())) {
            return false;
        }
        return true;
    }

    void overwriteLengthPrecision(IERDomain domainModel, Domain domain)
            throws InvalidEditingException {
        if (!isNeedChangeLengthPrecision(domainModel, domain)) {
            return;
        }
        String lengthPrecision = StringUtils.defaultString(domain.getLengthAndPrecision());
        String fullLogicalName = domain.getFullLogicalName();
        logger.debug(String.format("Set LengthPrecision \"%s\" to %s", lengthPrecision,
                fullLogicalName));
        try {
            domainModel.setLengthPrecision(lengthPrecision);
        } catch (InvalidEditingException e) {
        if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                try {
                    log_error(Messages.getMessage(
                            "log.error.set_length_and_precision_domain.parameter_error",
                            fullLogicalName));
                    IERDatatype dataType = getDataTypeForLog(domainModel);
                    if (dataType != null) {
                        logger.debug(String.format(
                                "%s is Invalid LengthPrecision. %s has constraint is %s, %s.",
                                lengthPrecision, dataType.getName(), dataType.getLengthConstraint(),
                                dataType.getPrecisionConstraint()));
                    } else {
                        logger.debug(String.format(
                                "%s is Invalid LengthPrecision.",
                                lengthPrecision));
                    }
                    
                    throw new ApplicationException(e);
                } finally {
                    TransactionManager.abortTransaction();
                }
            }
            throw e;
        }
    }

    private boolean isNeedChangeLengthPrecision(IERDomain domainModel, Domain domain) {
        return isNeedChangeValue(domainModel.getLengthPrecision(), domain.getLengthAndPrecision());
    }

    void overwriteDatatype(IERDomain erDomain, Domain domain) throws ProjectNotFoundException,
            ClassNotFoundException, InvalidEditingException {
        if (!isNeedChangeDatatype(erDomain, domain)) {
            return;
        }
        String domainFullName = domain.getFullLogicalName();
        IERDatatype dataType = getDataType(domain);
        logger.debug(String.format("Set Datatype \"%s\" to %s", dataType.getName(),
                domainFullName));
        try {
            erDomain.setDatatype(dataType);
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

    private boolean isNeedChangeDatatype(IERDomain erDomain, Domain domain) {
        return isNeedChangeValue(erDomain.getDatatypeName(), domain.getDataType());
    }

    void overwritePhysicalName(IERDomain erDomain, Domain domain) throws InvalidEditingException {
        if (!isNeedChangePhysicalName(erDomain, domain)) {
            return;
        }
        String domainFullName = domain.getFullLogicalName();
        String physicalName = domain.getPhysicalName();
        logger.debug(String.format("Set PhysicalName \"%b\" to %s", physicalName,
                domainFullName));
        try {
            erDomain.setPhysicalName(physicalName);
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

    private boolean isNeedChangePhysicalName(IERDomain erDomain, Domain domain) {
        return isNeedChangeValue(erDomain.getPhysicalName(), domain.getPhysicalName());
    }

    void overwriteLogicalName(IERDomain erDomain, Domain domain) throws InvalidEditingException {
        if (!isNeedChangeLogicalName(erDomain, domain)) {
            return;
        }
        String domainFullName = domain.getFullLogicalName();
        String logicalName = domain.getLogicalName();
        logger.debug(String.format("Set LogicalName \"%s\" to %s", logicalName,
                domainFullName));
        try {
            erDomain.setLogicalName(logicalName);
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

    private boolean isNeedChangeLogicalName(IERDomain erDomain, Domain domain) {
        return isNeedChangeValue(erDomain.getLogicalName(), domain.getLogicalName());
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
	
    IERDomain createAstahModel(DomainConfiguration configuration, Domain domain) {
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
            IERDomain parentERDomain = null;
            if (StringUtils.isNotEmpty(configuration.getParentDomainCol())) {
                parentERDomain = getParentERDomain(domain);
            }

            logger.debug(Messages.getMessage("log.create_domain", domainFullName));
            IERDomain domainModel = editor.createERDomain(erModel, parentERDomain,
                    domain.getLogicalName(), domain.getPhysicalName(), dataType);
            if (StringUtils.isNotEmpty(configuration.getLengthAndPrecisionCol())) {
                setLengthPrecision(domainModel, domain);
            }
            setAdditionalProperty(configuration, domainModel, domain);
			
			projectAccessor.getTransactionManager().endTransaction();
            result.inclementImportedElementsCount();
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
        logger.debug("Create ER Model");
        return editor.createERModel(projectAccessor.getProject(), "ER Model");
    }

    private IERDatatype getDataType(Domain domain) throws ProjectNotFoundException,
            ClassNotFoundException, InvalidEditingException {
        String dataTypeName = domain.getDataType();
        if (StringUtils.isEmpty(dataTypeName)) {
            return null;
        }
        IERDatatype dataType = new DataTypeFinder().find(dataTypeName);
        if (dataType != null) {
            return dataType;
        }
        logger.debug(Messages.getMessage("log.create.datatype", dataTypeName));
        ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
        ERModelEditor editor = projectAccessor.getModelEditorFactory().getERModelEditor();
        return createDataType(editor, getERModel(), dataTypeName);
    }

    private IERDatatype getDataTypeForLog(IERDomain domainModel) {
        IERDatatype dataType = null;
        String dataTypeName = domainModel.getDatatypeName();
        try {
             dataType = new DataTypeFinder().find(dataTypeName);
        } catch (ProjectNotFoundException e) {
            //
        } catch (ClassNotFoundException e) {
            //
        }
        return dataType;
    }

	private IERDatatype createDataType(ERModelEditor editor,IERModel erModel, String dataType) throws InvalidEditingException {
		return editor.createERDatatype(erModel, dataType);
	}

    private void setAdditionalProperty(DomainConfiguration configuration, IERDomain domainModel, Domain domain)
            throws InvalidEditingException {
        if (StringUtils.isNotEmpty(configuration.getAlias1Col())) {
            setAlias1(domainModel, domain);
        }
        if (StringUtils.isNotEmpty(configuration.getAlias2Col())) {
            setAlias2(domainModel, domain);
        }
        if (StringUtils.isNotEmpty(configuration.getNotNullCol())) {
            setNotNull(domainModel, domain);
        }
        if (StringUtils.isNotEmpty(configuration.getDefinitionCol())) {
            setDefinition(domainModel, domain);
        }
	}

    void setDefinition(IERDomain domainModel, Domain domain) throws InvalidEditingException {
        if (!isNeedChangeDefinition(domainModel, domain)) {
            return;
        }
        String definition = StringUtils.defaultString(domain.getDefinition());
        String fullLogicalName = domain.getFullLogicalName();
        logger.debug(String.format("Set Definition \"%s\" to %s", definition,
               fullLogicalName));
        try {
            domainModel.setDefinition(definition);
        } catch (InvalidEditingException e) {
            if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                TransactionManager.abortTransaction();
                log_error(Messages.getMessage("log.error.set_definition_domain.parameter_error",
                        fullLogicalName));
                throw new ApplicationException(e);
            }
            throw e;
        }
    }

    private boolean isNeedChangeDefinition(IERDomain domainModel, Domain domain) {
        return isNeedChangeValue(domainModel.getDefinition(), domain.getDefinition());
    }

    void setNotNull(IERDomain domainModel, Domain domain) throws InvalidEditingException {
        if (!isNeedChangeNotNull(domainModel, domain)) {
            return;
        }
        boolean notNull = domain.getNotNull();
        logger.debug(String.format("Set NotNull \"%s\" to %s", notNull,
                domain.getFullLogicalName()));
        domainModel.setNotNull(notNull);
    }

    private boolean isNeedChangeNotNull(IERDomain domainModel, Domain domain) {
        return isNeedChangeValue(domainModel.isNotNull(), domain.getNotNull());
    }

    void setLengthPrecision(IERDomain domainModel, Domain domain) throws InvalidEditingException {
        String lengthAndPrecision = domain.getLengthAndPrecision();
        if (StringUtils.isNotEmpty(lengthAndPrecision)) {
            String fullLogicalName = domain.getFullLogicalName();
            logger.debug(String.format("Set LengthPrecision \"%s\" to %s",
                    lengthAndPrecision, fullLogicalName));
            try {
                domainModel.setLengthPrecision(lengthAndPrecision);
            } catch (InvalidEditingException e) {
                if (StringUtils.equals(e.getKey(), InvalidEditingException.PARAMETER_ERROR_KEY)) {
                    try {
                        log_error(Messages.getMessage(
                                "log.error.set_length_and_precision_domain.parameter_error",
                                fullLogicalName));
                        IERDatatype dataType = getDataTypeForLog(domainModel);
                        if (dataType != null) {
                            logger.debug(String.format(
                                    "%s is Invalid LengthPrecision. %s has constraint is %s, %s.",
                                    lengthAndPrecision, dataType.getName(),
                                    dataType.getLengthConstraint(),
                                    dataType.getPrecisionConstraint()));
                        } else {
                            logger.debug(String.format("%s is Invalid LengthPrecision.",
                                    lengthAndPrecision));
                        }
                        throw new ApplicationException(e);
                    } finally {
                        aboartTransaction();
                    }
                }
                throw e;
            }
        }
    }

    void setAlias2(IERDomain domainModel, Domain domain) throws InvalidEditingException {
        if (!isNeedChangeAlias2(domainModel, domain)) {
            return;
        }
        String alias2 = domain.getAlias2();
        logger.debug(String.format("Set Alias2 \"%s\" to %s", alias2,
                domain.getFullLogicalName()));
        domainModel.setAlias2(alias2);
    }

    private boolean isNeedChangeAlias2(IERDomain domainModel, Domain domain) {
        return isNeedChangeValue(domainModel.getAlias2(), domain.getAlias2());
    }

    void setAlias1(IERDomain domainModel, Domain domain) throws InvalidEditingException {
        if (!isNeedChangeAlias1(domainModel, domain)) {
            return;
        }
        String alias1 = domain.getAlias1();
        logger.debug(String.format("Set Alias1 \"%s\" to %s", alias1,
                domain.getFullLogicalName()));
        domainModel.setAlias1(alias1);
    }

    private boolean isNeedChangeAlias1(IERDomain domainModel, Domain domain) {
        return isNeedChangeValue(domainModel.getAlias1(), domain.getAlias1());
    }

    private boolean isNeedChangeValue(boolean oldBoolean, boolean newBoolean) {
        return oldBoolean != newBoolean;
    }

    private boolean isNeedChangeValue(String oldStr, String newStr) {
        return !StringUtils.defaultString(oldStr).equals(StringUtils.defaultString(newStr));
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
