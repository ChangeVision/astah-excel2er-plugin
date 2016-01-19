package excel2er.models;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import excel2er.Messages;
import excel2er.exceptions.ValidationError;

public class DomainConfigurationTest {
	
	private DomainConfiguration conf = null;

	private String getMessage(String key, String parameter, String subparameter) {
		return Messages.getMessage(key, Messages.getMessage(parameter) + " - "
				+ Messages.getMessage(subparameter));
	}

	private void assertNotErrorExist(DomainConfiguration conf) {
		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(0));
	}

	private void assertRequireErrorExist(DomainConfiguration conf, String key,
			String subkey) {
		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0).getMessage(),
				is(getMessage("error.required", key, subkey)));
	}

	private void assertErrorExist(DomainConfiguration conf, String key) {
		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0).getMessage(), is(Messages.getMessage(key)));
	}

	private void assertDigitErrorExist(DomainConfiguration conf, String key,
			String subkey) {
		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0).getMessage(),
				is(getMessage("error.not.digit", key, subkey)));
	}

	@Before
	public void setUp() {
		conf = new DomainConfiguration();
		conf.setStartRow("9");
		conf.setLogicalCol("B");
		conf.setDataTypeCol("VARCHAR");
		conf.setInputFilePath("/tmp/dummypath");
	}

	@Test
	public void validate_necessary_property() throws Exception {
		conf = new DomainConfiguration();

		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(4));

	}
	
	@Test
	public void validate_inputfile() throws Exception {
		conf.setInputFilePath("");
		assertErrorExist(conf, "error.inputfile_required");
		
		conf.setInputFilePath("/tmp/dummypath");
		assertNotErrorExist(conf);
	}
	
	@Test
	public void validate_digit() {
		conf.setStartRow("a1");
		assertThat(conf.validate().size(), is(1));

		conf.setStartRow("1a");
		assertThat(conf.validate().size(), is(1));

		conf.setStartRow("10.1");
		assertThat(conf.validate().size(), is(1));

		conf.setStartRow("1");
		assertThat(conf.validate().size(), is(0));

		conf.setStartRow("10");
		assertThat(conf.validate().size(), is(0));
	}
	
	@Test
	public void validate_logical_col() {
		conf.setLogicalCol("1");
		assertNotErrorExist(conf);

		conf.setLogicalCol("a");
		assertNotErrorExist(conf);
		
		conf.setLogicalCol("");
		assertRequireErrorExist(conf, "explain_domain", "item_logical_domain");
	}
	
	@Test
	public void validate_physical_col() {
		conf.setPhysicalCol("1");
		assertNotErrorExist(conf);

		conf.setPhysicalCol("a");
		assertNotErrorExist(conf);
	}

    @Test
    public void validate_parent_domain_col() {
        conf.setParentDomainCol("1");
        assertNotErrorExist(conf);

        conf.setParentDomainCol("a");
        assertNotErrorExist(conf);

        conf.setParentDomainCol("");
        assertNotErrorExist(conf);

        conf.setParentDomainCol(null);
        assertNotErrorExist(conf);
    }

	@Test
	public void validate_datatype_col() {
		conf.setDataTypeCol("1");
		assertNotErrorExist(conf);

		conf.setDataTypeCol("a");
		assertNotErrorExist(conf);
		
		conf.setDataTypeCol("");
		assertRequireErrorExist(conf, "explain_domain", "item_datatype");

		conf.setDataTypeCol(null);
		assertRequireErrorExist(conf, "explain_domain", "item_datatype");

	}
	
	@Test
	public void validate_startrow() {
		conf.setStartRow("1");
		assertNotErrorExist(conf);

		conf.setStartRow("a");
		assertDigitErrorExist(conf, "explain_domain", "start_row");
	}
}
