package excel2er.services.finder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.change_vision.jude.api.inf.model.IERDomain;

import excel2er.AstahModelManager;
import excel2er.models.Attribute;

public class DomainFinderTest {

	@Test
	public void domain_not_found_from_empty_project() throws Exception {
		AstahModelManager.open(this.getClass()
				.getResource("empty.asta"));
		DomainFinder finder = new DomainFinder();

		Attribute attr = new Attribute();
		attr.setLogicalName("dummy");
		IERDomain domain = finder.find(attr);

		assertThat(domain, is(nullValue()));
	}

	@Test
	public void find_domain() throws Exception {
		AstahModelManager.open(this.getClass().getResource(
				"domain.asta"));
		DomainFinder finder = new DomainFinder();

		Attribute attr = new Attribute();
		attr.setLogicalName("d1");
		attr.setDataType("VARCHAR");
		attr.setLength("10");

		IERDomain domain = finder.find(attr);

		assertThat(domain, is(notNullValue()));
		assertThat(domain.getLogicalName(), is("d1"));
	}

	@Test
	public void find_domain_all_condition() throws Exception {
		AstahModelManager.open(this.getClass().getResource(
				"domain.asta"));
		DomainFinder finder = new DomainFinder();

		Attribute attr = new Attribute();
		attr.setLogicalName("full");
		attr.setPhysicalName("a");
		attr.setDataType("BIT");
		attr.setLength("100");
		// attr.setDefaultValue("200");
		attr.setNotNull(true);

		IERDomain domain = finder.find(attr);

		assertThat(domain, is(notNullValue()));
		assertThat(domain.getLogicalName(), is("full"));
	}

}
