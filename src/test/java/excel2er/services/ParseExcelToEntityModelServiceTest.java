package excel2er.services;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import excel2er.models.Attribute;
import excel2er.models.Configuration;
import excel2er.models.Entity;

public class ParseExcelToEntityModelServiceTest {

	@Test
	public void should_generate_entity_per_wellform_sheet() {
		Configuration conf = new Configuration();
		conf.setInputFilePath(getWorkspaceFilePath("entityLists.xls"));
		conf.setStartRow("9");
		conf.setAdvanceSetting(true);
		conf.setEntityLogicalRow("1");
		conf.setEntityLogicalCol("H");
		conf.setEntityPhysicalRow("2");
		conf.setEntityPhysicalRow("H");

		conf.setAttributeLogicalCol("B");
		conf.setAttributePhysicalCol("G");
		conf.setPrimaryKeyCol("L");
		conf.setNotNullCol("P");
		conf.setDataTypeCol("Q");
		conf.setLengthCol("U");
		conf.setDefaultValueCol("Y");
		conf.setDefinitionCol("AC");

		ParseExcelToEntityModelService service = new ParseExcelToEntityModelService();
		List<Entity> entities = service.parse(conf);

		assertThat(entities.size(), is(4));
		assertThat(entities.get(0).getEntityLogicalName(), is("Customer"));

		List<Attribute> attr = entities.get(0).getAttributes();

		assertThat(attr.size(), is(6));
		assertThat(attr.get(0).getLogicalName(), is("CustomerID"));
		assertThat(attr.get(0).getPhysicalName(), is("CUSTOMERID"));
		assertThat(attr.get(0).isPrimaryKey(), is(true));
		assertThat(attr.get(0).isNotNull(), is(true));
		assertThat(attr.get(0).getDataType(), is("VARCHAR"));
		assertThat(attr.get(0).getLength(), is("20"));
		assertThat(attr.get(0).getDefaultValue(), is(nullValue()));
		assertThat(attr.get(0).getDefinition(), is("abc"));

		assertThat(entities.get(3).getEntityLogicalName(), is("Product"));

		attr = entities.get(3).getAttributes();
		assertThat(attr.size(), is(4));
		assertThat(attr.get(3).getLogicalName(), is("Price"));
		assertThat(attr.get(3).getPhysicalName(), is("PRICE"));
		assertThat(attr.get(3).isPrimaryKey(), is(false));
		assertThat(attr.get(3).isNotNull(), is(false));
		assertThat(attr.get(3).getDataType(), is("INT"));
		assertThat(attr.get(3).getLength(), is(nullValue()));
		assertThat(attr.get(3).getDefaultValue(), is("a"));
		assertThat(attr.get(3).getDefinition(), is("b"));
	}

	@Test
	public void should_generate_generate_entity_per_wellform_sheet_using_sheetname()
			throws Exception {
		Configuration conf = new Configuration();
		conf.setInputFilePath(getWorkspaceFilePath("entityLists.xls"));
		conf.setStartRow("9");
		conf.setUseSheetName(true);

		conf.setAttributeLogicalCol("B");
		conf.setAttributePhysicalCol("G");
		conf.setPrimaryKeyCol("L");
		conf.setNotNullCol("P");
		conf.setDataTypeCol("Q");
		conf.setLengthCol("U");
		conf.setDefaultValueCol("Y");
		conf.setDefinitionCol("AC");

		ParseExcelToEntityModelService service = new ParseExcelToEntityModelService();
		List<Entity> entities = service.parse(conf);

		assertThat(entities.size(), is(6));
		assertThat(entities.get(0).getEntityLogicalName(), is("Domain List"));
		assertThat(entities.get(1).getEntityLogicalName(), is("Entity List"));
		assertThat(entities.get(2).getEntityLogicalName(), is("CustomerSheet"));
		assertThat(entities.get(3).getEntityLogicalName(), is("Order"));
		assertThat(entities.get(4).getEntityLogicalName(), is("OrderDetail"));
		assertThat(entities.get(5).getEntityLogicalName(), is("Product"));

		assertThat(entities.get(0).getAttributes().size(), is(0));
		assertThat(entities.get(1).getAttributes().size(), is(0));
		assertThat(entities.get(2).getAttributes().size(), is(6));
		assertThat(entities.get(3).getAttributes().size(), is(4));
		assertThat(entities.get(4).getAttributes().size(), is(5));
		assertThat(entities.get(5).getAttributes().size(), is(4));
	}

	private String getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename).getFile();
	}
}
