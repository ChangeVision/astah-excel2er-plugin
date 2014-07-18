package excel2er.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
	public void should_not_create_entity_in_already_exist_ermodel() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("already_exist_ermodel.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("Entity0");

		try{
			service.createAstahModel(entity);
			fail();
		}catch(ApplicationException e){
			InvalidEditingException rootCause = (InvalidEditingException)ExceptionUtils.getRootCause(e);
			assertThat(rootCause.getKey(),is(InvalidEditingException.NAME_DOUBLE_ERROR_KEY));
		}
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
	public void should_not_create_attribute_when_set_not_exist_datatype() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERModelService service = new ImportERModelService();

		Entity entity = new Entity();
		entity.setEntityLogicalName("logical");
		
		Attribute attr = new Attribute();
		attr.setLogicalName("attr1");
		attr.setDataType("NOTEXIST");
		entity.addAttribute(attr);
		
		IEREntity actual = service.createAstahModel(entity);

		assertThat(actual.getNonPrimaryKeys().length,is(0));
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
	
	private URL getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename);
	}
}
