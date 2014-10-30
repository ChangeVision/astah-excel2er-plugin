package excel2er.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IERAttribute;
import com.change_vision.jude.api.inf.model.IEREntity;

import excel2er.AstahModelManager;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Attribute;
import excel2er.models.Entity;
import excel2er.services.finder.IEREntityFinder;

public class ImportERModelServiceTest {

	@Rule
	public TestName testName = new TestName();

	private boolean debug = false;

	@After
	public void tearDown() throws Exception {
		if (debug) {
			String filename = testName.getMethodName() + ".asta";
			File saveFile = new File(SystemUtils.getJavaIoTmpDir(), filename);
			AstahModelManager.save(saveFile);
			System.out.println(testName.getMethodName() + " result save as "
					+ saveFile.getPath());
		}
		AstahModelManager.close();
	}

	@Test
	public void should_create_entity_in_empty_astah_project() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("test");
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getLogicalName(), is("test"));
	}
	
	@Test
	public void should_create_entity_in_already_exist_ermodel_astah_project() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("already_exist_ermodel.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("test");
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getLogicalName(), is("test"));
	}
	
	@Test
	public void should_update_entity_when_already_entity_exist() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("already_exist_entity.asta"));

		IEREntity target = new IEREntityFinder().findEREntity("Exist");
		assertThat(target.getLogicalName(),is("Exist"));
		assertThat(target.getPhysicalName(),is("EXIST"));
		assertThat(target.getDefinition(),is("def"));
		
		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("Exist");
		entity.setEntityPhysicalName("EDITED_EXIST");
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getLogicalName(), is("Exist"));
		assertThat(actual.getPhysicalName(), is("EDITED_EXIST"));
		assertThat(actual.getDefinition(), is("def"));
	}
	
	@Test
	public void should_not_create_entity_without_logicalname()
			throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityPhysicalName("physical");
		try{
			service.createAstahModel(entity);
			fail();
		}catch(ApplicationException e){
			InvalidEditingException rootCause = (InvalidEditingException)ExceptionUtils.getRootCause(e);
			assertThat(rootCause.getKey(),is(InvalidEditingException.PARAMETER_ERROR_KEY));
		}
	}
	
	@Test
	public void should_create_entity_with_logical_and_physicalname()
			throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		entity.setEntityPhysicalName("physical");
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getLogicalName(), is("logical"));
		assertThat(actual.getPhysicalName(), is("physical"));
	}

	@Test
	public void should_create_attribute_when_set_necessary_properties() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("attr1");
		attr.setDataType("VARCHAR");
		entity.addAttribute(attr);
		
		IEREntity actualEntity = service.createAstahModel(entity);

		assertThat(actualEntity.getNonPrimaryKeys().length,is(1));
		
		IERAttribute actualAttr = actualEntity.getNonPrimaryKeys()[0];
		//Necessary property
		assertThat(actualAttr.getLogicalName(),is("attr1"));
		assertThat(actualAttr.getDatatype().getName(),is("VARCHAR"));
		
		//Unnecessary property
		assertThat(actualAttr.getPhysicalName(),is(""));
		assertThat(actualAttr.isPrimaryKey(),is(false));
		assertThat(actualAttr.isNotNull(),is(false));
		assertThat(actualAttr.getDefaultValue(),is(""));
		assertThat(actualAttr.getLengthPrecision(),is("10"));
	}
	
	@Test
	public void should_create_attribute_when_set_primarykey_property() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("attr1");
		attr.setDataType("VARCHAR");
		attr.setPrimaryKey(true);
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getPrimaryKeys().length,is(1));

		IERAttribute actualAttr = actual.getPrimaryKeys()[0];
		assertThat(actualAttr.getLogicalName(),is("attr1"));
		assertThat(actualAttr.isPrimaryKey(),is(true));
	}
	
	@Test
	public void should_create_attribute_when_set_notnull_property() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("attr1");
		attr.setDataType("VARCHAR");
		attr.setNotNull(true);
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getNonPrimaryKeys().length,is(1));

		IERAttribute actualAttr = actual.getNonPrimaryKeys()[0];
		assertThat(actualAttr.getLogicalName(),is("attr1"));
		assertThat(actualAttr.isNotNull(),is(true));
	}
	
	@Test
	public void should_create_attribute_when_set_defaultvalue_property() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("attr1");
		attr.setDataType("VARCHAR");
		attr.setDefaultValue("default value");
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getNonPrimaryKeys().length,is(1));

		IERAttribute actualAttr = actual.getNonPrimaryKeys()[0];
		assertThat(actualAttr.getLogicalName(),is("attr1"));
		assertThat(actualAttr.getDefaultValue(),is("default value"));
	}
	
	@Test
	public void should_create_attribute_when_set_length_property() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("attr1");
		attr.setDataType("VARCHAR");
		attr.setLength("100");
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getNonPrimaryKeys().length,is(1));

		IERAttribute actualAttr = actual.getNonPrimaryKeys()[0];
		assertThat(actualAttr.getLogicalName(),is("attr1"));
		assertThat(actualAttr.getLengthPrecision(),is("100"));
	}
	
	@Test
	public void should_create_attribute_when_set_definition_property() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("attr1");
		attr.setDataType("VARCHAR");
		attr.setDefinition("def\nnewline");
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getNonPrimaryKeys().length,is(1));

		IERAttribute actualAttr = actual.getNonPrimaryKeys()[0];
		assertThat(actualAttr.getLogicalName(),is("attr1"));
		assertThat(actualAttr.getDefinition(),is("def\nnewline"));
	}
	
	@Test
	public void should_create_attribute_when_set_not_exist_datatype() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("attr1");
		attr.setDataType("NOTEXIST");
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getNonPrimaryKeys().length,is(1));

		IERAttribute actualAttr = actual.getNonPrimaryKeys()[0];
		assertThat(actualAttr.getLogicalName(),is("attr1"));
		assertThat(actualAttr.getDatatype().getName(),is("NOTEXIST"));
	}
	
	@Test
	public void should_create_attribute_when_set_use_additional_datatype() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("add_data_type.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("attr1");
		attr.setDataType("ADDITIONAL");
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getNonPrimaryKeys().length,is(1));
		
		IERAttribute actualAttr = actual.getNonPrimaryKeys()[0];
		
		assertThat(actualAttr.getLogicalName(), is("attr1"));
		assertThat(actualAttr.getDatatype().getName(), is("ADDITIONAL"));
	}
	
	@Test
	public void should_create_attribute_when_use_domain() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("exist_domain.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("testdomain");
		attr.setPhysicalName("TESTDOMAIN");
		attr.setDataType("FLOAT");
		attr.setLength("20");
		attr.setDefaultValue("100");
		attr.setNotNull(true);
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getNonPrimaryKeys().length,is(1));
		IERAttribute actualAttr = actual.getNonPrimaryKeys()[0];
		assertThat(actualAttr.getLogicalName(),is("testdomain"));
		assertThat(actualAttr.getPhysicalName(),is("TESTDOMAIN"));
		assertThat(actualAttr.getDatatype().getName(),is("FLOAT"));
		assertThat(actualAttr.getLengthPrecision(),is("20"));
		assertThat(actualAttr.getDefaultValue(),is("100"));
		assertThat(actualAttr.isNotNull(),is(true));
		assertThat(actualAttr.getDefinition(),is(""));
		assertThat(actualAttr.isPrimaryKey(),is(false));
	}
	
	@Test
	public void should_create_attribute_when_use_domain_and_set_editable_properties() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("exist_domain.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("testdomain");
		attr.setPhysicalName("TESTDOMAIN");
		attr.setDataType("FLOAT");
		attr.setLength("20");
		attr.setDefaultValue("100");
		attr.setNotNull(true);
		
		//editable properties
		attr.setPrimaryKey(true);
		attr.setDefaultValue("333");
		attr.setDefinition("defdefdef");
		
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getPrimaryKeys().length,is(1));
		IERAttribute actualAttr = actual.getPrimaryKeys()[0];
		assertThat(actualAttr.getLogicalName(),is("testdomain"));
		assertThat(actualAttr.getPhysicalName(),is("TESTDOMAIN"));
		assertThat(actualAttr.getDatatype().getName(),is("FLOAT"));
		assertThat(actualAttr.getLengthPrecision(),is("20"));
		assertThat(actualAttr.isNotNull(),is(true));
		
		//editable properties
		assertThat(actualAttr.isPrimaryKey(),is(true));
		assertThat(actualAttr.getDefaultValue(),is("333"));
		assertThat(actualAttr.getDefinition(),is("defdefdef"));
	}
	
	@Test
	public void should_update_attribute_when_import_exist_primary_attribute() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("exist_attribute.asta"));
		
		//test precondition
		IEREntity target = new IEREntityFinder().findEREntity("Exist1");
		assertThat(target.getLogicalName(),is("Exist1"));
		assertThat(target.getPhysicalName(),is("EXIST1"));

		IERAttribute primaryKey = target.getPrimaryKeys()[0];
		assertThat(primaryKey.getLogicalName(),is("exist1"));
		assertThat(primaryKey.getPhysicalName(),is("EXIST1"));
		assertThat(primaryKey.getDatatype().getName(),is("VARCHAR"));
		assertThat(primaryKey.isPrimaryKey(),is(true));
		assertThat(primaryKey.isNotNull(),is(true));
		assertThat(primaryKey.getDefaultValue(),is("default"));
		assertThat(primaryKey.getLengthPrecision(),is("20"));
		assertThat(primaryKey.getDefinition(),is("def_default"));
		
		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("Exist1");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("exist1");
		attr.setPhysicalName("EDITED_EXIST1");
		attr.setDataType("FLOAT");
		attr.setPrimaryKey(true);
		attr.setNotNull(true);
		attr.setDefaultValue("100");
		attr.setLength("200");
		attr.setDefinition("edited_def");
		entity.addAttribute(attr);
		
		IEREntity actualEntity = service.createAstahModel(entity);

		assertThat(actualEntity.getPrimaryKeys().length,is(1));
		
		IERAttribute actualAttr = actualEntity.getPrimaryKeys()[0];
		
		assertThat(actualAttr.getLogicalName(),is("exist1"));
		assertThat(actualAttr.getPhysicalName(),is("EDITED_EXIST1"));
		assertThat(actualAttr.getDatatype().getName(),is("FLOAT"));
		
		assertThat(actualAttr.isPrimaryKey(),is(true));
		assertThat(actualAttr.isNotNull(),is(true));
		assertThat(actualAttr.getDefaultValue(),is("100"));
		assertThat(actualAttr.getLengthPrecision(),is("200"));
		assertThat(actualAttr.getDefinition(),is("edited_def"));
	}
	
	@Test
	public void should_update_attribute_when_import_exist_nonprimary_attribute() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("exist_attribute.asta"));
		
		//test precondition
		IEREntity target = new IEREntityFinder().findEREntity("Exist1");
		assertThat(target.getLogicalName(),is("Exist1"));
		assertThat(target.getPhysicalName(),is("EXIST1"));

		IERAttribute key = target.getNonPrimaryKeys()[0];
		assertThat(key.getLogicalName(),is("exist2"));
		assertThat(key.getPhysicalName(),is("EXIST2"));
		assertThat(key.getDatatype().getName(),is("CHAR"));
		assertThat(key.isPrimaryKey(),is(false));
		assertThat(key.isNotNull(),is(true));
		assertThat(key.getDefaultValue(),is("default2"));
		assertThat(key.getLengthPrecision(),is("5"));
		assertThat(key.getDefinition(),is("def_default2"));
		
		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("Exist1");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("exist2");
		attr.setPhysicalName("EDITED_EXIST2");
		attr.setDataType("FLOAT");
		attr.setPrimaryKey(false);
		attr.setNotNull(false);
		attr.setDefaultValue("100");
		attr.setLength("200");
		attr.setDefinition("edited_def");
		entity.addAttribute(attr);
		
		IEREntity actualEntity = service.createAstahModel(entity);

		assertThat(actualEntity.getNonPrimaryKeys().length,is(1));
		
		IERAttribute actualAttr = actualEntity.getNonPrimaryKeys()[0];
		
		assertThat(actualAttr.getLogicalName(),is("exist2"));
		assertThat(actualAttr.getPhysicalName(),is("EDITED_EXIST2"));
		assertThat(actualAttr.getDatatype().getName(),is("FLOAT"));
		
		assertThat(actualAttr.isPrimaryKey(),is(false));
		assertThat(actualAttr.isNotNull(),is(false));
		assertThat(actualAttr.getDefaultValue(),is("100"));
		assertThat(actualAttr.getLengthPrecision(),is("200"));
		assertThat(actualAttr.getDefinition(),is("edited_def"));
	}
	
	@Test
	public void should_update_attribute_nonprimarykey_to_primarykey() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("exist_attribute.asta"));
		
		//test precondition
		IEREntity target = new IEREntityFinder().findEREntity("Exist1");
		assertThat(target.getLogicalName(),is("Exist1"));
		assertThat(target.getPhysicalName(),is("EXIST1"));

		IERAttribute key = target.getNonPrimaryKeys()[0];
		assertThat(key.getLogicalName(),is("exist2"));
		assertThat(key.isPrimaryKey(),is(false));
		assertThat(key.isNotNull(),is(true));
		
		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("Exist1");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("exist2");
		attr.setDataType("FLOAT");
		attr.setPrimaryKey(true);
		attr.setNotNull(false);
		entity.addAttribute(attr);
		
		IEREntity actualEntity = service.createAstahModel(entity);

		assertThat(actualEntity.getPrimaryKeys().length,is(2));
		assertThat(actualEntity.getNonPrimaryKeys().length,is(0));
		
		IERAttribute actualAttr = null;
		for(IERAttribute candidate : actualEntity.getPrimaryKeys()){
			if(candidate.getLogicalName().equals("exist2")){
				actualAttr = candidate;
				break;
			}
		}
		
		assertThat(actualAttr.getLogicalName(),is("exist2"));
		assertThat(actualAttr.isPrimaryKey(),is(true));
		assertThat(actualAttr.isNotNull(),is(true));
	}
	
	@Test
	public void should_update_attribute_primarykey_to_nonprimarykey() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("exist_attribute.asta"));
		
		//test precondition
		IEREntity target = new IEREntityFinder().findEREntity("Exist1");
		assertThat(target.getLogicalName(),is("Exist1"));
		assertThat(target.getPhysicalName(),is("EXIST1"));

		IERAttribute primaryKey = target.getPrimaryKeys()[0];
		assertThat(primaryKey.getLogicalName(),is("exist1"));
		assertThat(primaryKey.isPrimaryKey(),is(true));
		assertThat(primaryKey.isNotNull(),is(true));
		
		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("Exist1");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("exist1");
		attr.setDataType("FLOAT");
		attr.setPrimaryKey(false);
		attr.setNotNull(false);
		entity.addAttribute(attr);
		
		IEREntity actualEntity = service.createAstahModel(entity);

		assertThat(actualEntity.getPrimaryKeys().length,is(0));
		assertThat(actualEntity.getNonPrimaryKeys().length,is(2));
		
		IERAttribute actualAttr = null;
		for(IERAttribute candidate : actualEntity.getNonPrimaryKeys()){
			if(candidate.getLogicalName().equals("exist1")){
				actualAttr = candidate;
				break;
			}
		}
		assertThat(actualAttr.getLogicalName(),is("exist1"));
		assertThat(actualAttr.isPrimaryKey(),is(false));
		assertThat(actualAttr.isNotNull(),is(false));
	}
	
	private URL getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename);
	}
}
