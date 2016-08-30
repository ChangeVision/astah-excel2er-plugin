package excel2er.services.sorter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import excel2er.models.Domain;

public class DomainSorterTest {

    DomainSorter sut = null;

    @Before
    public void before() throws Exception {
        sut = new DomainSorter();
    }

    @Test
    public void sort_parentIsNot_orderOfLogicalName() {

        List<Domain> domains = new ArrayList<Domain>();

        Domain domain = new Domain();
        domain.setLogicalName("domain3");
        domain.setParentDomain(null);
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("domain0");
        domain.setParentDomain(null);
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("domain2");
        domain.setParentDomain(null);
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("domain1");
        domain.setParentDomain(null);
        domains.add(domain);

        sut.sort(domains);

        assertThat(domains.get(0).getLogicalName(), is("domain0"));
        assertThat(domains.get(1).getLogicalName(), is("domain1"));
        assertThat(domains.get(2).getLogicalName(), is("domain2"));
        assertThat(domains.get(3).getLogicalName(), is("domain3"));

    }

    @Test
    public void sort_parentIsSameNumber_orderOfParentDomain() {

        List<Domain> domains = new ArrayList<Domain>();

        Domain domain = new Domain();
        domain.setLogicalName("domain3");
        domain.setParentDomain("parent3");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("domain0");
        domain.setParentDomain("parent1");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("domain2");
        domain.setParentDomain("parent2");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("domain1");
        domain.setParentDomain("parent0");
        domains.add(domain);

        sut.sort(domains);

        assertThat(domains.get(0).getLogicalName(), is("domain1"));
        assertThat(domains.get(1).getLogicalName(), is("domain0"));
        assertThat(domains.get(2).getLogicalName(), is("domain2"));
        assertThat(domains.get(3).getLogicalName(), is("domain3"));

    }

    @Test
    public void sort_theNumberOfGenerationIsDifference_orderOfGenerationNumber() {

        List<Domain> domains = new ArrayList<Domain>();

        Domain domain = new Domain();
        domain.setLogicalName("domain3");
        domain.setParentDomain(null);
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("domain1");
        domain.setParentDomain("parent2::parent5::parent8");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("domain2");
        domain.setParentDomain("parent0::parent1");
        domains.add(domain);

        domain = new Domain();
        domain.setLogicalName("domain0");
        domain.setParentDomain("parent7");
        domains.add(domain);

        sut.sort(domains);

        assertThat(domains.get(0).getLogicalName(), is("domain3"));
        assertThat(domains.get(1).getLogicalName(), is("domain0"));
        assertThat(domains.get(2).getLogicalName(), is("domain2"));
        assertThat(domains.get(3).getLogicalName(), is("domain1"));

    }

}
