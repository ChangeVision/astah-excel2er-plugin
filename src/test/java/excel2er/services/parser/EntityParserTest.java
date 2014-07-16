package excel2er.services.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import excel2er.models.Configuration;
import excel2er.models.Entity;
import excel2er.services.parser.EntityParser;
import excel2er.services.parser.ParserUtils;

public class EntityParserTest {

	
	
	@Test
	public void should_set_usesheetname_to_entity_logical_physical_name() throws Exception {
		Configuration conf = new Configuration();
		conf.setInputFilePath(getWorkspaceFilePath("entityListModel.xls"));
		conf.setUseSheetName(true);
		
		EntityParser parser = new EntityParser();
		
		Workbook wb = ParserUtils.getWorkbook(conf);
		Sheet sheet = wb.getSheet("CustomerSheet");
		
		Entity entity = parser.parse(conf, sheet);
		
		assertThat(entity.getEntityLogicalName(),is("CustomerSheet"));
		assertThat(entity.getEntityPhysicalName(),is(nullValue()));
	}
	
	@Test
	public void should_set_entity_logical_physical_name_using_advancesetting() throws Exception {
		Configuration conf = new Configuration();
		conf.setInputFilePath(getWorkspaceFilePath("entityListModel.xls"));
		conf.setAdvanceSetting(true);
		conf.setEntityLogicalRow("1");
		conf.setEntityLogicalCol("H");
		conf.setEntityPhysicalRow("2");
		conf.setEntityPhysicalCol("H");
		
		EntityParser parser = new EntityParser();
		
		Workbook wb = ParserUtils.getWorkbook(conf);
		Sheet sheet = wb.getSheet("CustomerSheet");
		
		Entity entity = parser.parse(conf, sheet);
		
		assertThat(entity.getEntityLogicalName(),is("Customer"));
		assertThat(entity.getEntityPhysicalName(),is("CUSTOMER"));
	}
	
	
	private String getWorkspaceFilePath(String filename){
		return this.getClass().getResource(filename).getFile();
	}
}
