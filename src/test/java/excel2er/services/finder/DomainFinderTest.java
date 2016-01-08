package excel2er.services.finder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.change_vision.jude.api.inf.model.IERDomain;

import excel2er.AstahModelManager;
import excel2er.models.Attribute;

public class DomainFinderTest {

    DomainFinder sut = null;

    @Before
    public void before() throws Exception {
        sut = new DomainFinder();
    }

	@Test
	public void domain_not_found_from_empty_project() throws Exception {
		AstahModelManager.open(this.getClass()
				.getResource("empty.asta"));

		Attribute attr = new Attribute();
		attr.setLogicalName("dummy");
        IERDomain domain = sut.find(attr);

		assertThat(domain, is(nullValue()));
	}

	@Test
	public void find_domain() throws Exception {
		AstahModelManager.open(this.getClass().getResource(
				"domain.asta"));

		Attribute attr = new Attribute();
		attr.setLogicalName("d1");
		attr.setDataType("VARCHAR");
		attr.setLength("10");

        IERDomain domain = sut.find(attr);

		assertThat(domain, is(notNullValue()));
		assertThat(domain.getLogicalName(), is("d1"));
	}

	@Test
	public void find_domain_all_condition() throws Exception {
		AstahModelManager.open(this.getClass().getResource(
				"domain.asta"));

		Attribute attr = new Attribute();
		attr.setLogicalName("full");
		attr.setPhysicalName("a");
		attr.setDataType("BIT");
		attr.setLength("100");
		// attr.setDefaultValue("200");
		attr.setNotNull(true);

        IERDomain domain = sut.find(attr);

		assertThat(domain, is(notNullValue()));
		assertThat(domain.getLogicalName(), is("full"));
	}

    @Test
    public void find_string_string_domain_full_logical_name() throws Exception {
        AstahModelManager.open(this.getClass().getResource("domain.asta"));

        IERDomain actual = sut.find("d1", "::");
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getLogicalName(), is("d1"));
        assertThat(actual.getContainer(), is(nullValue()));

        actual = sut.find("d1::d2", "::");
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getLogicalName(), is("d2"));
        assertThat(actual.getContainer(), is(notNullValue()));
        assertThat(((IERDomain) actual.getContainer()).getLogicalName(), is("d1"));

        actual = sut.find("d1::d2::d3", "::");
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getLogicalName(), is("d3"));
        assertThat(actual.getContainer(), is(notNullValue()));
        assertThat(((IERDomain) actual.getContainer()).getLogicalName(), is("d2"));

    }

}
