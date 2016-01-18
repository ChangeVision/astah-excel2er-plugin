package excel2er.services.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import excel2er.models.Domain;
import excel2er.models.DomainConfiguration;

public class DomainParserTest {

	DomainConfiguration createConf(String excelFileName, 
			String startRow) {
		DomainConfiguration conf = new DomainConfiguration();
		conf.setInputFilePath(getWorkspaceFilePath(excelFileName));
		conf.setStartRow(startRow);

		return conf;
	}

	private String getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename).getFile();
	}
	
	private List<Domain> parse(DomainConfiguration conf) {
		Workbook wb = ParserUtils.getWorkbook(conf);
		Sheet sheet = wb.getSheetAt(0);
		DomainParser parser = new DomainParser();
		return parser.parse(conf,sheet);
	}

	@Test
	public void should_parse() throws Exception {
		DomainConfiguration conf = new DomainConfiguration();
		conf.setInputFilePath(getWorkspaceFilePath("domainLists.xls"));
		conf.setStartRow("4");
		conf.setLogicalCol("B");
		conf.setPhysicalCol("G");
        conf.setAlias1Col("L");
        conf.setAlias2Col("Q");
		conf.setDataTypeCol("V");
        conf.setLengthAndPrecisionCol("Z");
        conf.setNotNullCol("AD");
        conf.setParentDomainCol("AE");
		conf.setDefinitionCol("AJ");
		
		List<Domain> domains = parse(conf);

		assertThat(domains.size(), is(4));

		Domain domain = domains.get(0);
		assertThat(domain.getLogicalName(), is("Name"));
		assertThat(domain.getPhysicalName(), is("NAME"));
        assertThat(domain.getAlias1(), is("NameAlias1"));
        assertThat(domain.getAlias2(), is("NameAlias2"));
		assertThat(domain.getDataType(), is("VARCHAR"));
        assertThat(domain.getLengthAndPrecision(), is("30"));// CELL_TYPE_STRING
        assertThat(domain.getNotNull(), is(false));
        assertThat(domain.getParentDomain(), is("NamesParent"));
		assertThat(domain.getDefinition(), is("name def"));
		
		domain = domains.get(1);
		assertThat(domain.getLogicalName(), is("Address"));
		assertThat(domain.getPhysicalName(), is("ADDRESS"));
        assertThat(domain.getAlias1(), is("AddressAlias1"));
        assertThat(domain.getAlias2(), is("AddressAlias2"));
		assertThat(domain.getDataType(), is("VARCHAR"));
        assertThat(domain.getLengthAndPrecision(), is("200"));// CELL_TYPE_STRING
        assertThat(domain.getNotNull(), is(false));
        assertThat(domain.getParentDomain(), is("AddressesParent"));
		assertThat(domain.getDefinition(), is("address def"));

		domain = domains.get(2);
		assertThat(domain.getLogicalName(), is("Age"));
		assertThat(domain.getPhysicalName(), is("AGE"));
        assertThat(domain.getAlias1(), is("AgeAlias1"));
        assertThat(domain.getAlias2(), is("AgeAlias2"));
		assertThat(domain.getDataType(), is("INT"));
        assertThat(domain.getLengthAndPrecision(), is("10"));// CELL_TYPE_NUMERIC
        assertThat(domain.getNotNull(), is(false));
        assertThat(domain.getParentDomain(), is("AgesParent"));
		assertThat(domain.getDefinition(), is("age def"));

		domain = domains.get(3);
		assertThat(domain.getLogicalName(), is("Birthday"));
		assertThat(domain.getPhysicalName(), is("BIRTHDAY"));
        assertThat(domain.getAlias1(), is("BirthdayAlias1"));
        assertThat(domain.getAlias2(), is("BirthdayAlias2"));
		assertThat(domain.getDataType(), is("DATE"));
        assertThat(domain.getLengthAndPrecision(), is("20"));// CELL_TYPE_NUMERIC
        assertThat(domain.getNotNull(), is(false));
        assertThat(domain.getParentDomain(), is("BirthdaysParent"));
		assertThat(domain.getDefinition(), is("birthday def"));

	}

}
