package excel2er.services;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
import com.change_vision.jude.api.inf.model.IERDomain;

import excel2er.AstahModelManager;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Domain;

public class ImportERDomainServiceTest {

	@Rule
	public TestName testName = new TestName();

	private boolean debug = true;

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
	public void should_create_domain_in_empty_astah_project() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERDomainService service = new ImportERDomainService();

		Domain domain = new Domain();
		domain.setLogicalName("test");
		domain.setDataType("VARCHAR");
		IERDomain actual = service.createAstahModel(domain);

		assertThat(actual.getLogicalName(), is("test"));
	}
	
	@Test
	public void should_create_domain_with_all_properties() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));

		ImportERDomainService service = new ImportERDomainService();

		Domain domain = new Domain();
		domain.setLogicalName("test");
		domain.setPhysicalName("TEST");
		domain.setDataType("VARCHAR");
		domain.setDefinition("abc");
		IERDomain actual = service.createAstahModel(domain);

		assertThat(actual.getLogicalName(), is("test"));
		assertThat(actual.getPhysicalName(), is("TEST"));
		assertThat(actual.getDatatypeName(), is("VARCHAR"));
		assertThat(actual.getDefinition(), is("abc"));
	}
	
	@Test
	public void should_create_domain_in_already_exist_ermodel_astah_project() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("already_exist_ermodel.asta"));

		ImportERDomainService service = new ImportERDomainService();

		Domain domain = new Domain();
		domain.setLogicalName("test");
		domain.setDataType("VARCHAR");
		IERDomain actual = service.createAstahModel(domain);

		assertThat(actual.getLogicalName(), is("test"));
	}
	
	@Test
	public void should_not_create_domain_in_already_exist_ermodel() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("exist_domain.asta"));

		ImportERDomainService service = new ImportERDomainService();

		Domain domain = new Domain();
		domain.setLogicalName("testdomain");
		domain.setDataType("FLOAT");

		try{
			service.createAstahModel(domain);
			fail();
		}catch(ApplicationException e){
			InvalidEditingException rootCause = (InvalidEditingException)ExceptionUtils.getRootCause(e);
			assertThat(rootCause.getKey(),is(InvalidEditingException.NAME_DOUBLE_ERROR_KEY));
		}
	}
	
	@Test
	public void should_not_create_domain_without_logicalname()
			throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));
		
		ImportERDomainService service = new ImportERDomainService();

		Domain domain = new Domain();
		domain.setDataType("FLOAT");
		try{
			service.createAstahModel(domain);
			fail();
		}catch(ApplicationException e){
			InvalidEditingException rootCause = (InvalidEditingException)ExceptionUtils.getRootCause(e);
			assertThat(rootCause.getKey(),is(InvalidEditingException.PARAMETER_ERROR_KEY));
		}
	}
	
	@Test
	public void should_create_domain_without_not_exist_datatype()
			throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));
		
		ImportERDomainService service = new ImportERDomainService();
		Domain domain = new Domain();
		domain.setLogicalName("test");
		domain.setPhysicalName("TEST");
		domain.setDataType("NEWTYPE");
		domain.setDefinition("abc");
		
		IERDomain actual = service.createAstahModel(domain);

		assertThat(actual.getLogicalName(), is("test"));
		assertThat(actual.getPhysicalName(), is("TEST"));
		assertThat(actual.getDatatypeName(), is("NEWTYPE"));
		assertThat(actual.getDefinition(), is("abc"));
	}

    @Test
    public void createAstahModel_parent_difference_in_same_name_domain_creatable() throws Exception {
        AstahModelManager.open(getWorkspaceFilePath("addParentDomain.asta"));

        ImportERDomainService service = new ImportERDomainService();
        Domain domain = new Domain();
        domain.setLogicalName("Domein0");
        domain.setDataType("CHAR");
        domain.setParentDomain(null);

        IERDomain actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein0"));
        assertThat(actual.getDatatypeName(), is("CHAR"));
        assertThat(actual.getContainer(), is(nullValue()));

        domain = new Domain();
        domain.setLogicalName("Domein1");
        domain.setDataType("CHAR");
        domain.setParentDomain("Domein0");

        actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein1"));
        assertThat(actual.getDatatypeName(), is("CHAR"));
        IERDomain parentDomain = (IERDomain) actual.getContainer();
        assertThat(parentDomain.getLogicalName(), is("Domein0"));

        domain = new Domain();
        domain.setLogicalName("Domein2");
        domain.setDataType("CHAR");
        domain.setParentDomain("Domein0::Domein1");

        actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein2"));
        assertThat(actual.getDatatypeName(), is("CHAR"));
        parentDomain = (IERDomain) actual.getContainer();
        assertThat(parentDomain.getLogicalName(), is("Domein1"));
        assertThat(((IERDomain) parentDomain.getContainer()).getLogicalName(), is("Domein0"));
    }

    @Test
    public void createAstahModel_can_import_alias1() throws Exception {
        AstahModelManager.open(getWorkspaceFilePath("addAlias1.asta"));

        ImportERDomainService service = new ImportERDomainService();
        Domain domain = new Domain();
        domain.setLogicalName("Domein0");
        domain.setDataType("CHAR");
        domain.setAlias1("Alias1");

        IERDomain actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein0"));
        assertThat(actual.getAlias1(), is("Alias1"));
    }
    
    @Test
    public void createAstahModel_can_import_alias2() throws Exception {
        AstahModelManager.open(getWorkspaceFilePath("addAlias2.asta"));

        ImportERDomainService service = new ImportERDomainService();
        Domain domain = new Domain();
        domain.setLogicalName("Domein0");
        domain.setDataType("CHAR");
        domain.setAlias2("Alias2");

        IERDomain actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein0"));
        assertThat(actual.getAlias2(), is("Alias2"));
    }
    
    @Test
    public void createAstahModel_can_import_length_and_precision() throws Exception {
        AstahModelManager.open(getWorkspaceFilePath("addLengthAndPrecision.asta"));

        ImportERDomainService service = new ImportERDomainService();
        Domain domain = new Domain();
        domain.setLogicalName("Domein0");
        domain.setDataType("CHAR");
        domain.setLengthAndPrecision("10");

        IERDomain actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein0"));
        assertThat(actual.getLengthPrecision(), is("10"));
    }

    @Test
    public void createAstahModel_can_import_not_null() throws Exception {
        AstahModelManager.open(getWorkspaceFilePath("addNotNull.asta"));

        ImportERDomainService service = new ImportERDomainService();
        Domain domain = new Domain();
        domain.setLogicalName("Domein0");
        domain.setDataType("CHAR");
        domain.setNotNull("N");

        IERDomain actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein0"));
        assertThat(actual.isNotNull(), is(true));

        domain = new Domain();
        domain.setLogicalName("Domein1");
        domain.setDataType("CHAR");
        domain.setNotNull("○");

        actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein1"));
        assertThat(actual.isNotNull(), is(true));

        domain = new Domain();
        domain.setLogicalName("Domein2");
        domain.setDataType("CHAR");
        domain.setNotNull(null);

        actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein2"));
        assertThat(actual.isNotNull(), is(false));
    }

    @Test
    public void createAstahModel_inputOfLengthPrecisionIsEssentialType_lengthHasNotBeenEntered_setDefaultLengthPrecision()
            throws Exception {
        AstahModelManager.open(getWorkspaceFilePath("setDefaultLengthPrecision.asta"));

        ImportERDomainService service = new ImportERDomainService();
        Domain domain = new Domain();
        domain.setLogicalName("Domein0");
        domain.setDataType("TYPE THAT REQUIRES A LENGTH");
        domain.setLengthAndPrecision(null);

        IERDomain actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein0"));
        assertThat(actual.getDatatypeName(), is("TYPE THAT REQUIRES A LENGTH"));
        assertThat(actual.getLengthPrecision(), is("DefaultLength"));

        domain = new Domain();
        domain.setLogicalName("Domein1");
        domain.setDataType("TYPE THAT REQUIRES A PRECISION");
        domain.setLengthAndPrecision(null);

        actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein1"));
        assertThat(actual.getDatatypeName(), is("TYPE THAT REQUIRES A PRECISION"));
        assertThat(actual.getLengthPrecision(), is("DefaultPrecision"));

        domain = new Domain();
        domain.setLogicalName("Domein2");
        domain.setDataType("TYPE THAT REQUIRES LENGTH AND PRECISION");
        domain.setLengthAndPrecision(null);

        actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein2"));
        assertThat(actual.getDatatypeName(), is("TYPE THAT REQUIRES LENGTH AND PRECISION"));
        assertThat(actual.getLengthPrecision(), is("DefaultLength,DefaultPrecision"));
    }

    @Test
    public void createAstahModel_addNewDataType() throws Exception {
        AstahModelManager.open(getWorkspaceFilePath("add_data_type.asta"));

        ImportERDomainService service = new ImportERDomainService();
        Domain domain = new Domain();
        domain.setLogicalName("Domein0");
        domain.setDataType("AAA");
        domain.setLengthAndPrecision(null);

        IERDomain actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein0"));
        assertThat(actual.getDatatypeName(), is("AAA"));
        assertThat(actual.getLengthPrecision(), is(""));

        domain = new Domain();
        domain.setLogicalName("Domein1");
        domain.setDataType("BBB");
        domain.setLengthAndPrecision("10");

        actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein1"));
        assertThat(actual.getDatatypeName(), is("BBB"));
        assertThat(actual.getLengthPrecision(), is("10"));

        domain = new Domain();
        domain.setLogicalName("Domein2");
        domain.setDataType("CCC");
        domain.setLengthAndPrecision("10,10");

        actual = service.createAstahModel(domain);

        assertThat(actual.getLogicalName(), is("Domein2"));
        assertThat(actual.getDatatypeName(), is("CCC"));
        assertThat(actual.getLengthPrecision(), is("10,10"));

    }

	private URL getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename);
	}
}
