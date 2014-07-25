package excel2er.services.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Configuration;

public class ParserUtilsTest {

	@Test
	public void should_error_occur_when_load_not_exist_file() {
		Configuration conf = new Configuration();
		conf.setInputFilePath("dummy");

		try {
			ParserUtils.getWorkbook(conf);
			fail();
		} catch (ApplicationException e) {
			assertThat(e.getMessage(),
					is(Messages.getMessage("error.file_notfound")));
		}
	}

	@Test
	public void should_error_occur_when_invalid_format_file() throws Exception {
		Configuration conf = new Configuration();
		conf.setInputFilePath(getWorkspaceFilePath("test.docx"));

		try {
			ParserUtils.getWorkbook(conf);
			fail();
		} catch (ApplicationException e) {
			assertThat(
					ExceptionUtils.indexOfThrowable(e, POIXMLException.class) > 0,
					is(true));
		}
	}
	
	@Test
	public void should_get_workspace_when_load_xls_excel_file()
			throws Exception {
		Configuration conf = new Configuration();
		conf.setInputFilePath(getWorkspaceFilePath("oldstyle.xls"));

		Workbook workbook = ParserUtils.getWorkbook(conf);

		assertThat(workbook.getSheetName(0), is("Domain List"));
	}

	@Test
	public void should_get_workspace_when_load_xlsx_excel_file()
			throws Exception {
		Configuration conf = new Configuration();
		conf.setInputFilePath(getWorkspaceFilePath("newstyle.xlsx"));

		Workbook workbook = ParserUtils.getWorkbook(conf);

		assertThat(workbook.getSheetName(0), is("Sheet1"));
	}

	@Test
	public void should_get_value_from_stringtype_cell() throws Exception {
		Configuration conf = new Configuration();
		conf.setInputFilePath(getWorkspaceFilePath("cellvalues.xls"));

		Workbook wb = ParserUtils.getWorkbook(conf);
		Sheet sheet = wb.getSheetAt(0);

		assertThat(ParserUtils.getCellValue(sheet, 1, "A"), is("abc"));
		assertThat(Double.valueOf(ParserUtils.getCellValue(sheet, 2, "1")),
				is(123.d));
		assertThat(Boolean.valueOf(ParserUtils.getCellValue(sheet, 3, "1")),
				is(true));
		assertThat(ParserUtils.getCellValue(sheet, 4, "A"), is("-"));
		assertThat(ParserUtils.getCellValue(sheet, 5, "A"), is("✓"));
		assertThat(Double.valueOf(ParserUtils.getCellValue(sheet, 6, "A")),
				is(6.d));
		assertThat(Double.valueOf(ParserUtils.getCellValue(sheet, 7, "A")),
				is(123.d));
		assertThat(ParserUtils.getCellValue(sheet, 8, "A"), is("日本語"));
	}

	private String getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename).getFile();
	}
}
