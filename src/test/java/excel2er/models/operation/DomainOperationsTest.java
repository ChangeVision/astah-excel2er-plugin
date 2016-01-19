package excel2er.models.operation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import excel2er.AstahModelManager;
import excel2er.models.Domain;

public class DomainOperationsTest {

    DomainOperations sut = null;
    private static final String EMPTY_ASTAH_MODEL_PATH = "DomainOperationsTest0.asta";
    private static final String HAS_DOMAINS_ASTAH_MODEL_PATH = "DomainOperationsTest1.asta";

    @Before
    public void before() throws Exception {

        sut = new DomainOperations();
    }

    @After
    public void after() throws Exception {
        AstahModelManager.close();
    }

    @Test
    public void addNeedCreateParentDomains_alldomainHasNotparent_returnASorted() throws Exception {

        AstahModelManager.open(this.getClass().getResource(EMPTY_ASTAH_MODEL_PATH));

        List<Domain> domains = new ArrayList<Domain>();

        Domain domain0 = new Domain();
        domain0.setFullLogicalName("domain0", "::");
        domain0.setDataType("CHAR");
        domains.add(domain0);

        Domain domain2 = new Domain();
        domain2.setFullLogicalName("domain2", "::");
        domain2.setDataType("CHAR");
        domains.add(domain2);

        Domain domain1 = new Domain();
        domain1.setFullLogicalName("domain1", "::");
        domain1.setDataType("CHAR");
        domains.add(domain1);

        List<Domain> actuals = sut.addNeedCreateParentDomains(domains);

        assertThat(actuals.get(0), is(domain0));
        assertThat(actuals.get(1), is(domain1));
        assertThat(actuals.get(2), is(domain2));

        AstahModelManager.close();
    }

    @Test
    public void addNeedCreateParentDomains_domainsHasParent_projectDoesNotHaveParent_addParentDomain()
            throws Exception {

        AstahModelManager.open(this.getClass().getResource(EMPTY_ASTAH_MODEL_PATH));

        List<Domain> domains = new ArrayList<Domain>();

        Domain domain = new Domain();
        domain.setFullLogicalName("domain0::domain1", "::");
        domain.setDataType("CHAR");
        domains.add(domain);

        domain = new Domain();
        domain.setFullLogicalName("domain2::domain3::domain4", "::");
        domain.setDataType("CHAR");
        domains.add(domain);

        List<Domain> actuals = sut.addNeedCreateParentDomains(domains);

        assertThat(actuals.size(), is(5));
        assertThat(actuals.get(0).getFullLogicalName(), is("domain0"));
        assertThat(actuals.get(1).getFullLogicalName(), is("domain2"));
        assertThat(actuals.get(2).getFullLogicalName(), is("domain0::domain1"));
        assertThat(actuals.get(3).getFullLogicalName(), is("domain2::domain3"));
        assertThat(actuals.get(4).getFullLogicalName(), is("domain2::domain3::domain4"));

        AstahModelManager.close();

    }

    @Test
    public void addNeedCreateParentDomains_domainsHasParent_domainsHasSameNameParent_addParentDomain()
            throws Exception {

        AstahModelManager.open(this.getClass().getResource(EMPTY_ASTAH_MODEL_PATH));

        List<Domain> domains = new ArrayList<Domain>();

        Domain domain = new Domain();
        domain.setLogicalName("Domain0");
        domain.setDataType("CHAR");
        domain.setParentDomain(null);
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("Domain1");
        domain.setDataType("CHAR");
        domain.setParentDomain("Domain0");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("Domain2");
        domain.setDataType("CHAR");
        domain.setParentDomain("Domain0::Domain1");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("Domain3");
        domain.setDataType("CHAR");
        domain.setParentDomain(null);
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("Domain4");
        domain.setDataType("CHAR");
        domain.setParentDomain("Domain1");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("Domain5");
        domain.setDataType("CHAR");
        domain.setParentDomain("Domain0::Domain2");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("Domain6");
        domain.setDataType("CHAR");
        domain.setParentDomain(null);
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("Domain7");
        domain.setDataType("CHAR");
        domain.setParentDomain("Domain2");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("Domain8");
        domain.setDataType("CHAR");
        domain.setParentDomain("Domain0::Domain3");
        domains.add(domain);

        List<Domain> actuals = sut.addNeedCreateParentDomains(domains);

        assertThat(actuals.size(), is(13));
        assertThat(actuals.get(0).getFullLogicalName(), is("Domain0"));
        assertThat(actuals.get(1).getFullLogicalName(), is("Domain1"));
        assertThat(actuals.get(2).getFullLogicalName(), is("Domain2"));
        assertThat(actuals.get(3).getFullLogicalName(), is("Domain3"));
        assertThat(actuals.get(4).getFullLogicalName(), is("Domain6"));
        assertThat(actuals.get(5).getFullLogicalName(), is("Domain0::Domain1"));
        assertThat(actuals.get(6).getFullLogicalName(), is("Domain0::Domain2"));
        assertThat(actuals.get(7).getFullLogicalName(), is("Domain0::Domain3"));
        assertThat(actuals.get(8).getFullLogicalName(), is("Domain1::Domain4"));
        assertThat(actuals.get(9).getFullLogicalName(), is("Domain2::Domain7"));
        assertThat(actuals.get(10).getFullLogicalName(), is("Domain0::Domain1::Domain2"));
        assertThat(actuals.get(11).getFullLogicalName(), is("Domain0::Domain2::Domain5"));
        assertThat(actuals.get(12).getFullLogicalName(), is("Domain0::Domain3::Domain8"));

        AstahModelManager.close();
    }

    @Test
    public void addNeedCreateParentDomains_domainsHasParent_projectHaveParent_notAddParentDomain()
            throws Exception {

        AstahModelManager.open(this.getClass().getResource(HAS_DOMAINS_ASTAH_MODEL_PATH));

        List<Domain> domains = new ArrayList<Domain>();

        Domain domain = new Domain();
        domain.setFullLogicalName("domain0::domain1", "::");
        domain.setDataType("CHAR");
        domains.add(domain);

        domain = new Domain();
        domain.setFullLogicalName("domain2::domain3::domain4", "::");
        domain.setDataType("CHAR");
        domains.add(domain);

        List<Domain> actuals = sut.addNeedCreateParentDomains(domains);

        assertThat(actuals.size(), is(3));
        assertThat(actuals.get(0).getFullLogicalName(), is("domain0::domain1"));
        assertThat(actuals.get(1).getFullLogicalName(), is("domain2::domain3"));
        assertThat(actuals.get(2).getFullLogicalName(), is("domain2::domain3::domain4"));

        AstahModelManager.close();

    }

}
