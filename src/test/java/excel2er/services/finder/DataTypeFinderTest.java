package excel2er.services.finder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import com.change_vision.jude.api.inf.model.IERDatatype;

import excel2er.AstahModelManager;

public class DataTypeFinderTest {

	@Test
	public void test_equals_builder() throws Exception {
		EqualsBuilder b = new EqualsBuilder();
		b.append("", null);
		assertThat(b.isEquals(), is(false));

		b = new EqualsBuilder();
		b.append("", "");
		assertThat(b.isEquals(), is(true));
	}

	@Test
	public void not_find_data_type_from_empty_project() throws Exception {
		AstahModelManager.open(this.getClass()
				.getResource("empty.asta"));

		DataTypeFinder finder = new DataTypeFinder();
		IERDatatype dataType = finder.find("VARCHAR");
		assertThat(dataType, is(nullValue()));
	}

	@Test
	public void find_data_type() throws Exception {

		AstahModelManager.open(this.getClass().getResource(
				"exist_ermodel.asta"));

		DataTypeFinder finder = new DataTypeFinder();

		IERDatatype dataType = finder.find("VARCHAR");

		assertThat(dataType, is(notNullValue()));
		assertThat(dataType.getName(), is("VARCHAR"));
	}

	@Test
	public void find_data_type_ignore_case() throws Exception {

		AstahModelManager.open(this.getClass().getResource(
				"exist_ermodel.asta"));

		DataTypeFinder finder = new DataTypeFinder();

		IERDatatype dataType = finder.find("varchar");

		assertThat(dataType, is(notNullValue()));
		assertThat(dataType.getName(), is("VARCHAR"));
	}

}
