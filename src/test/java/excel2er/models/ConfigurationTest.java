package excel2er.models;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import excel2er.Messages;
import excel2er.exceptions.ValidationError;

public class ConfigurationTest {

	private String getMessage(String key , String subKey){
		return Messages.getMessage("error.not.digit",Messages.getMessage(key) + " - " + Messages.getMessage(subKey));
	}
	
	private void assertNotErrorExist(Configuration conf){
		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(0));
	}
	
	private void assertErrorExist(Configuration conf,String key,String subkey){
		List<ValidationError> errors = conf.validate();
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0).getMessage(), is(getMessage(key,subkey)));
	}
	
	@Test
	public void validate_digit() {
		Configuration conf = new Configuration();
		
		conf.setStartRow("a1");
		assertThat(conf.validate().size(),is(1));
		
		conf.setStartRow("1a");
		assertThat(conf.validate().size(),is(1));

		conf.setStartRow("10.1");
		assertThat(conf.validate().size(),is(1));

		conf.setStartRow("1");
		assertThat(conf.validate().size(),is(0));

		conf.setStartRow("10");
		assertThat(conf.validate().size(),is(0));
	}
	
	@Test
	public void validate_multiply() throws Exception {
		Configuration conf = new Configuration();
		
		conf.setStartRow("a1");
		conf.setEntityLogicalRow("a2");
		
		assertThat(conf.validate().size(),is(2));
	}
	
	@Test
	public void validate_attribute_logical_col() {
		Configuration conf = new Configuration();

		conf.setAttributeLogicalCol("1");
		assertNotErrorExist(conf);
		
		conf.setAttributeLogicalCol("a");
		assertNotErrorExist(conf);
	}
	
	@Test
	public void validate_attribute_physical_col() {
		Configuration conf = new Configuration();

		conf.setAttributePhysicalCol("1");
		assertNotErrorExist(conf);
		
		conf.setAttributePhysicalCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_datatype_col() {
		Configuration conf = new Configuration();

		conf.setDataTypeCol("1");
		assertNotErrorExist(conf);
		
		conf.setDataTypeCol("a");
		assertNotErrorExist(conf);
	}
	
	@Test
	public void validate_defaultvalue_col() {
		Configuration conf = new Configuration();

		conf.setDefaultValueCol("1");
		assertNotErrorExist(conf);
		
		conf.setDefaultValueCol("a");
		assertNotErrorExist(conf);
	}
	
	@Test
	public void validate_definition_col() {
		Configuration conf = new Configuration();

		conf.setDefinitionCol("1");
		assertNotErrorExist(conf);
		
		conf.setDefinitionCol("a");
		assertNotErrorExist(conf);
	}
	
	@Test
	public void validate_entitylogical_col() {
		Configuration conf = new Configuration();

		conf.setEntityLogicalCol("1");
		assertNotErrorExist(conf);
		
		conf.setEntityLogicalCol("a");
		assertNotErrorExist(conf);
	}
	
	@Test
	public void validate_entitylogical_row() {
		Configuration conf = new Configuration();

		conf.setEntityLogicalRow("1");
		assertNotErrorExist(conf);
		
		conf.setEntityLogicalRow("a");
		assertErrorExist(conf,"explain_entity","entity.logicalname.row");
	}
	
	@Test
	public void validate_entityphysical_col() {
		Configuration conf = new Configuration();

		conf.setEntityPhysicalCol("1");
		assertNotErrorExist(conf);
		
		conf.setEntityPhysicalCol("a");
		assertNotErrorExist(conf);
	}
	
	@Test
	public void validate_entityphysical_row() {
		Configuration conf = new Configuration();

		conf.setEntityPhysicalRow("1");
		assertNotErrorExist(conf);
		
		conf.setEntityPhysicalRow("a");
		assertErrorExist(conf,"explain_entity","entity.physicalname.row");
	}
	
	@Test
	public void validate_length_col() {
		Configuration conf = new Configuration();

		conf.setLengthCol("1");
		assertNotErrorExist(conf);
		
		conf.setLengthCol("a");
		assertNotErrorExist(conf);
	}
	
	@Test
	public void validate_notnull_col() {
		Configuration conf = new Configuration();

		conf.setNotNullCol("1");
		assertNotErrorExist(conf);
		
		conf.setNotNullCol("a");
		assertNotErrorExist(conf);
	}

	@Test
	public void validate_primarykey_col() {
		Configuration conf = new Configuration();

		conf.setPrimaryKeyCol("1");
		assertNotErrorExist(conf);
		
		conf.setPrimaryKeyCol("a");
		assertNotErrorExist(conf);
	}
	
	@Test
	public void validate_startrow() {
		Configuration conf = new Configuration();

		conf.setStartRow("1");
		assertNotErrorExist(conf);
		
		conf.setStartRow("a");
		assertErrorExist(conf,"explain_attribute","start_row");
	}
	
}
