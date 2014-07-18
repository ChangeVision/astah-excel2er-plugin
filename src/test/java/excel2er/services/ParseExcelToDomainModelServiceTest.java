package excel2er.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Domain;
import excel2er.models.DomainConfiguration;

public class ParseExcelToDomainModelServiceTest {

	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void should_generate_domain_use_sheetname() {
		DomainConfiguration conf = new DomainConfiguration();
		conf.setInputFilePath(getWorkspaceFilePath("domainLists.xls"));
		conf.setSheetName("Domain List");
		conf.setStartRow("4");
		conf.setLogicalCol("B");
		conf.setPhysicalCol("G");
		conf.setDataTypeCol("V");
		conf.setDefinitionCol("AJ");

		ParseExcelToDomainModelService service = new ParseExcelToDomainModelService();
		List<Domain> domains = service.parse(conf);

		assertThat(domains.size(), is(4));
		
		Domain actual = domains.get(0);
		assertThat(actual.getLogicalName(), is("Name"));
		assertThat(actual.getPhysicalName(),is("NAME"));
		assertThat(actual.getDataType(), is("VARCHAR"));
		assertThat(actual.getDefinition(), is("name def"));
		
		actual = domains.get(3);
		assertThat(actual.getLogicalName(), is("Birthday"));
		assertThat(actual.getPhysicalName(),is("BIRTHDAY"));
		assertThat(actual.getDataType(), is("DATE"));
		assertThat(actual.getDefinition(), is("birthday def"));
	}

	@Test
	public void should_generate_domain_not_set_sheetname() {
		DomainConfiguration conf = new DomainConfiguration();
		conf.setInputFilePath(getWorkspaceFilePath("domainLists.xls"));
		conf.setSheetName(null);
		conf.setStartRow("4");
		conf.setLogicalCol("B");
		conf.setPhysicalCol("G");
		conf.setDataTypeCol("V");
		conf.setDefinitionCol("AJ");

		ParseExcelToDomainModelService service = new ParseExcelToDomainModelService();
		List<Domain> domains = service.parse(conf);

		assertThat(domains.size(), is(4));
		
		Domain actual = domains.get(0);
		assertThat(actual.getLogicalName(), is("Name"));
		assertThat(actual.getPhysicalName(),is("NAME"));
		assertThat(actual.getDataType(), is("VARCHAR"));
		assertThat(actual.getDefinition(), is("name def"));
		
		actual = domains.get(3);
		assertThat(actual.getLogicalName(), is("Birthday"));
		assertThat(actual.getPhysicalName(),is("BIRTHDAY"));
		assertThat(actual.getDataType(), is("DATE"));
		assertThat(actual.getDefinition(), is("birthday def"));
	}

	@Test
	public void should_occur_error_when_set_not_exist_sheetname() throws Exception {
		thrown.expect(ApplicationException.class);
		thrown.expectMessage(Messages.getMessage("error.not_found_sheet"));
		
		DomainConfiguration conf = new DomainConfiguration();
		conf.setInputFilePath(getWorkspaceFilePath("domainLists.xls"));
		conf.setSheetName("notfound");

		ParseExcelToDomainModelService service = new ParseExcelToDomainModelService();
		
		service.parse(conf);
	}
	
	private String getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename).getFile();
	}
}
