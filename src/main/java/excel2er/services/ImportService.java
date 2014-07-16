package excel2er.services;

import java.util.List;

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

import excel2er.exceptions.ApplicationException;
import excel2er.models.Attribute;
import excel2er.models.Configuration;
import excel2er.models.Entity;
import excel2er.services.finder.DataTypeFinder;
import excel2er.services.finder.DomainFinder;

public class ImportService {
	private static final Logger logger = LoggerFactory
			.getLogger(ImportService.class);

	public void execute(Configuration configuration) {
		ParseExcelToEntityModelService parseService = new ParseExcelToEntityModelService();

		List<Entity> entities = parseService.parse(configuration);

		for (Entity entity : entities) {
			createAstahModel(entity);
		}
	}

	private void createAstahModel(Entity entity) {
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
					erModel.getSchemata()[0], entity.getEntityLogicalName(),
					entity.getEntityLogicalName());

			logger.info(String.format("create entity({0})",
					entity.getEntityLogicalName()));

			DomainFinder domainFinder = new DomainFinder();
			DataTypeFinder dataTypeFinder = new DataTypeFinder();

			for (Attribute attr : entity.getAttributes()) {
				IERDomain domain = domainFinder.find(attr);
				IERAttribute attrModel = null;
				if (domain != null) {
					attrModel = createAttributeUsingDomain(editor, entityModel, attr,
							domain);
				} else {
					IERDatatype dataType = dataTypeFinder.find(attr
							.getDataType());
					if (dataType == null) {
						logger.warn(
								String.format("attribute({0}) can't create.because dataType({1}) is missing."),
								attr.getLogicalName(), attr.getDataType());
						continue;
					}
					attrModel = createAttribute(editor, entityModel, attr, dataType);
					attrModel.setDefaultValue(attr.getDefaultValue());
					attrModel.setDefinition(attr.getDefinition());
					attrModel.setLengthPrecision(attr.getLength());
				}

			}
			
			transactionManager.endTransaction();
			
		} catch (ClassNotFoundException e) {
			aboartTransaction();
			throw new ApplicationException(e);
		} catch (InvalidEditingException e) {
			aboartTransaction();
			throw new ApplicationException(e);
		} catch (ProjectNotFoundException e) {
			aboartTransaction();
			throw new ApplicationException(e);
		} finally {
			try {
				ProjectAccessor projectAccessor = AstahAPI.getAstahAPI()
						.getProjectAccessor();
				projectAccessor.getTransactionManager().endTransaction();
			} catch (Exception e) {
				//
			}

		}
	}

	private IERAttribute createAttribute(ERModelEditor editor, IEREntity entityModel,
			Attribute attr, IERDatatype dataType)
			throws InvalidEditingException {
		IERAttribute attrModel = editor.createERAttribute(entityModel,
				attr.getLogicalName(), attr.getPhysicalName(),
				dataType);
		logger.info(String.format("create attribute({0})",
				attr.getLogicalName()));
		return attrModel;
	}

	private IERAttribute createAttributeUsingDomain(ERModelEditor editor,
			IEREntity entityModel, Attribute attr, IERDomain domain)
			throws InvalidEditingException {
		IERAttribute attrModel = editor.createERAttribute(entityModel,
				attr.getLogicalName(), attr.getPhysicalName(),
				domain);
		logger.info(String.format(
				"create attribute({0}) using domain",
				attr.getLogicalName()));
		
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

}
