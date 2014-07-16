package excel2er.services.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import excel2er.models.Attribute;
import excel2er.models.Configuration;
import excel2er.models.Entity;

public class AttributeParserTest {

	Configuration createConf(String excelFileName, String startRow,
			String logicalCol) {
		Configuration conf = new Configuration();
		conf.setInputFilePath(getWorkspaceFilePath(excelFileName));

		conf.setStartRow(startRow);
		conf.setAttributeLogicalCol(logicalCol);

		return conf;
	}

	private List<Attribute> parse(Configuration conf, String sheetName) {
		Workbook wb = ParserUtils.getWorkbook(conf);
		Sheet sheet = wb.getSheet(sheetName);

		Entity entity = new Entity();
		AttributeParser parser = new AttributeParser();
		parser.parse(conf, sheet, entity);

		return entity.getAttributes();
	}

	@Test
	public void should_set_logicalname() throws Exception {
		Configuration conf = createConf("entityListModel.xls", "9", "B");

		List<Attribute> attrs = parse(conf, "CustomerSheet");

		assertThat(attrs.size(), is(6));

		assertThat(attrs.get(0).getLogicalName(), is("CustomerID"));
		assertThat(attrs.get(1).getLogicalName(), is("Name"));
		assertThat(attrs.get(2).getLogicalName(), is("Mail"));
		assertThat(attrs.get(3).getLogicalName(), is("ZipCode"));
		assertThat(attrs.get(4).getLogicalName(), is("Address"));
		assertThat(attrs.get(5).getLogicalName(), is("Telephone"));
	}

	@Test
	public void should_set_physicalname() throws Exception {
		Configuration conf = createConf("entityListModel.xls", "9", "B");
		conf.setAttributePhysicalCol("G");

		List<Attribute> attrs = parse(conf, "CustomerSheet");

		assertThat(attrs.size(), is(6));

		assertThat(attrs.get(0).getPhysicalName(), is("CUSTOMERID"));
		assertThat(attrs.get(1).getPhysicalName(), is("NAME"));
		assertThat(attrs.get(2).getPhysicalName(), is("MAIL"));
		assertThat(attrs.get(3).getPhysicalName(), is("ZIPCODE"));
		assertThat(attrs.get(4).getPhysicalName(), is("ADDRESS"));
		assertThat(attrs.get(5).getPhysicalName(), is("TELEPHONE"));
	}

	@Test
	public void should_set_primarykey() throws Exception {
		Configuration conf = createConf("entityListModel.xls", "9", "B");
		conf.setPrimaryKeyCol("L");
		List<Attribute> attrs = parse(conf, "CustomerSheet");

		assertThat(attrs.size(), is(6));
		assertThat("CustomerID", attrs.get(0).isPrimaryKey(), is(true));
		assertThat("Name", attrs.get(1).isPrimaryKey(), is(false));
		assertThat("Mail", attrs.get(2).isPrimaryKey(), is(false));
		assertThat("ZipCode", attrs.get(3).isPrimaryKey(), is(true));
		assertThat("Address", attrs.get(4).isPrimaryKey(), is(false));
		assertThat("Telephone", attrs.get(5).isPrimaryKey(), is(true));
	}

	@Test
	public void should_set_notnull() throws Exception {
		Configuration conf = createConf("entityListModel.xls", "9", "B");
		conf.setNotNullCol("16");//P
		List<Attribute> attrs = parse(conf, "CustomerSheet");

		assertThat(attrs.size(), is(6));
		assertThat("CustomerID", attrs.get(0).isNotNull(), is(true));
		assertThat("Name", attrs.get(1).isNotNull(), is(true));
		assertThat("Mail", attrs.get(2).isNotNull(), is(false));
		assertThat("ZipCode", attrs.get(3).isNotNull(), is(false));
		assertThat("Address", attrs.get(4).isNotNull(), is(true));
		assertThat("Telephone", attrs.get(5).isNotNull(), is(false));
	}

	@Test
	public void should_set_datatype() throws Exception {
		Configuration conf = createConf("entityListModel.xls", "9", "B");
		conf.setDataTypeCol("Q");
		List<Attribute> attrs = parse(conf, "CustomerSheet");

		assertThat(attrs.size(), is(6));
		assertThat("CustomerID", attrs.get(0).getDataType(), is("VARCHAR"));
		assertThat("Name", attrs.get(1).getDataType(), is("INT"));
		assertThat("Mail", attrs.get(2).getDataType(), is("DATE"));
		assertThat("ZipCode", attrs.get(3).getDataType(), is("FLOAT"));
		assertThat("Address", attrs.get(4).getDataType(), is("VARCHAR"));
		assertThat("Telephone", attrs.get(5).getDataType(), is("BIT"));
	}

	@Test
	public void should_set_length() throws Exception {
		Configuration conf = createConf("entityListModel.xls", "9", "B");
		conf.setLengthCol("U");
		List<Attribute> attrs = parse(conf, "CustomerSheet");

		assertThat(attrs.size(), is(6));
		assertThat("CustomerID", attrs.get(0).getLength(), is("20"));
		assertThat("Name", attrs.get(1).getLength(), is("30"));
		assertThat("Mail", attrs.get(2).getLength(), is("50"));
		assertThat("ZipCode", attrs.get(3).getLength(), is("20"));
		assertThat("Address", attrs.get(4).getLength(), is("200"));
		assertThat("Telephone", attrs.get(5).getLength(), is("20"));
	}
	
	@Test
	public void should_set_defaultvalue() throws Exception {
		Configuration conf = createConf("entityListModel.xls", "9", "B");
		conf.setDefaultValueCol("Y");
		List<Attribute> attrs = parse(conf, "CustomerSheet");

		assertThat(attrs.size(), is(6));
		assertThat("CustomerID", attrs.get(0).getDefaultValue(), is(nullValue()));
		assertThat("Name", attrs.get(1).getDefaultValue(), is("a"));
		assertThat("Mail", attrs.get(2).getDefaultValue(), is(nullValue()));
		assertThat("ZipCode", attrs.get(3).getDefaultValue(), is("b"));
		assertThat("Address", attrs.get(4).getDefaultValue(), is("c"));
		assertThat("Telephone", attrs.get(5).getDefaultValue(), is("d"));
	}
	
	@Test
	public void should_set_definition() throws Exception {
		Configuration conf = createConf("entityListModel.xls", "9", "B");
		conf.setDefinitionCol("AC");
		List<Attribute> attrs = parse(conf, "CustomerSheet");

		assertThat(attrs.size(), is(6));
		assertThat("CustomerID", attrs.get(0).getDefinition(), is("abc"));
		assertThat("Name", attrs.get(1).getDefinition(), is("def"));
		assertThat("Mail", attrs.get(2).getDefinition(), is(nullValue()));
		assertThat("ZipCode", attrs.get(3).getDefinition(), is("zzz"));
		assertThat("Address", attrs.get(4).getDefinition(), is(nullValue()));
		assertThat("Telephone", attrs.get(5).getDefinition(), is("bbb"));
	}
	
	private String getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename).getFile();
	}

}
