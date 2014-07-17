package excel2er.models;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import excel2er.Messages;
import excel2er.exceptions.ValidationError;

public class ConfigurationTest {

	private Configuration conf = null;
	
	private String getMessage(String key, String subKey) {
		return Messages.getMessage("error.not.digit", Messages.getMessage(key)
				+ " - " + Messages.getMessage(subKey));
	}

	private void assertNotErrorExist(Configuration conf) {
		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(0));
	}

	private void assertErrorExist(Configuration conf, String key) {
		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0).getMessage(), is(Messages.getMessage(key)));
	}

	private void assertErrorExist(Configuration conf, String key, String subkey) {
		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0).getMessage(), is(getMessage(key, subkey)));
	}

	@Before
	public void setUp(){
		conf = new Configuration();
		conf.setUseSheetName(true);
		conf.setInputFilePath("/tmp/dummypath");
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
	public void validate_multiply() throws Exception {
		conf.setStartRow("a1");
		conf.setEntityLogicalRow("a2");

		assertThat(conf.validate().size(), is(2));
	}

	@Test
	public void validate_attribute_logical_col() {
		conf.setAttributeLogicalCol("1");
		assertNotErrorExist(conf);

		conf.setAttributeLogicalCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_attribute_physical_col() {
		conf.setAttributePhysicalCol("1");
		assertNotErrorExist(conf);

		conf.setAttributePhysicalCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_datatype_col() {
		conf.setDataTypeCol("1");
		assertNotErrorExist(conf);

		conf.setDataTypeCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_defaultvalue_col() {
		conf.setDefaultValueCol("1");
		assertNotErrorExist(conf);

		conf.setDefaultValueCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_definition_col() {
		conf.setDefinitionCol("1");
		assertNotErrorExist(conf);

		conf.setDefinitionCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_entitylogical_col() {
		conf.setEntityLogicalCol("1");
		assertNotErrorExist(conf);

		conf.setEntityLogicalCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_entitylogical_row() {
		conf.setEntityLogicalRow("1");
		assertNotErrorExist(conf);

		conf.setEntityLogicalRow("a");
		assertErrorExist(conf, "explain_entity", "entity.logicalname.row");
	}

	@Test
	public void validate_entityphysical_col() {
		conf.setEntityPhysicalCol("1");
		assertNotErrorExist(conf);

		conf.setEntityPhysicalCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_entityphysical_row() {
		conf.setEntityPhysicalRow("1");
		assertNotErrorExist(conf);

		conf.setEntityPhysicalRow("a");
		assertErrorExist(conf, "explain_entity", "entity.physicalname.row");
	}

	@Test
	public void validate_length_col() {
		conf.setLengthCol("1");
		assertNotErrorExist(conf);

		conf.setLengthCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_notnull_col() {
		conf.setNotNullCol("1");
		assertNotErrorExist(conf);

		conf.setNotNullCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_primarykey_col() {
		conf.setPrimaryKeyCol("1");
		assertNotErrorExist(conf);

		conf.setPrimaryKeyCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_startrow() {
		conf.setStartRow("1");
		assertNotErrorExist(conf);

		conf.setStartRow("a");
		assertErrorExist(conf, "explain_attribute", "start_row");
	}

}
