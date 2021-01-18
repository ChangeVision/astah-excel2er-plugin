package excel2er.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.JFrame;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import excel2er.Messages;
import excel2er.models.DomainConfiguration;
import excel2er.services.Result;

@RunWith(GUITestRunner.class)
public class ImportERDomainDialogTest {

	private FrameFixture frameFixture;
	private DialogFixture dialogFixture;
	private ImportERDomainDialog target;

	@Before
	public void setUp() throws Exception {

		JFrame frame = new JFrame();
		frameFixture = new FrameFixture(frame);
		target = new ImportERDomainDialog(frame);
		dialogFixture = new DialogFixture(frameFixture.robot, target);
		dialogFixture.moveToFront();
		dialogFixture.show();
		dialogFixture.focus();
	}

	@After
	public void tearDown() throws Exception {
		if (dialogFixture != null) {
			dialogFixture.cleanUp();
		}
		if (frameFixture != null) {
			frameFixture.cleanUp();
		}
	}

	private DomainConfiguration getConf() {
		return (DomainConfiguration) target.getConfiguration();
	}

	@Test
	public void should_get_inputfilepath() throws Exception {
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				"/tmp/test/path");

		assertThat(getConf().getInputFilePath(), is("/tmp/test/path"));
	}

	@Test
	public void should_get_start_row() throws Exception {
		dialogFixture.textBox(DomainPanel.StartRow.NAME).setText("111");

		assertThat(getConf().getStartRow(), is("111"));
	}
	
	@Test
	public void should_get_logical_col() throws Exception {
		dialogFixture.textBox(DomainPanel.ItemCol.LOGICAL).setText("222");

		assertThat(getConf().getLogicalCol(), is("222"));
	}
	
	@Test
	public void should_get_physical_col() throws Exception {
		dialogFixture.textBox(DomainPanel.ItemCol.PHYSICAL).setText("333");

		assertThat(getConf().getPhysicalCol(), is("333"));
	}
	
	@Test
	public void should_get_datatype_col() throws Exception {
		dialogFixture.textBox(DomainPanel.ItemCol.DATATYPE).setText("444");

		assertThat(getConf().getDataTypeCol(), is("444"));
	}
	
	@Test
	public void should_get_definition_col() throws Exception {
		dialogFixture.textBox(DomainPanel.ItemCol.DEFINITION).setText("555");

		assertThat(getConf().getDefinitionCol(), is("555"));
	}
	
	@Test
	public void should_show_error_dialog_not_set_inputfile() throws Exception {
		dialogFixture.button(ImportERDomainDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();
		assertThat(dialogFixture.optionPane().textBox(ImportEREntityDialog.DETAIL_TEXT)
				.text(), is(Messages.getMessage("error.inputfile_required")));
	}
	
	@Test
	public void should_show_error_dialog_when_startrow_set_not_digit_value() throws Exception {
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				"/tmp/test/path");
		dialogFixture.textBox(DomainPanel.ItemCol.LOGICAL).setText("A");
		dialogFixture.textBox(DomainPanel.ItemCol.DATATYPE).setText("B");
		
		dialogFixture.textBox(DomainPanel.StartRow.NAME).setText("abc");
		
		dialogFixture.button(ImportERDomainDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();
		assertThat(dialogFixture.optionPane().textBox(ImportEREntityDialog.DETAIL_TEXT)
				.text(), is(Messages.getMessage("error.not.digit",Messages.getMessage("explain_domain") + " - " + Messages.getMessage("start_row"))));
	}
	
	@Test
	public void should_show_error_dialog_not_set_logicalname() throws Exception {
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				"/tmp/test/path");
		dialogFixture.textBox(DomainPanel.ItemCol.LOGICAL).setText("");
		dialogFixture.textBox(DomainPanel.ItemCol.DATATYPE).setText("B");
		dialogFixture.textBox(DomainPanel.StartRow.NAME).setText("9");
		
		dialogFixture.button(ImportEREntityDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();
		assertThat(dialogFixture.optionPane().textBox(ImportEREntityDialog.DETAIL_TEXT)
				.text(), is(Messages.getMessage("error.required",Messages.getMessage("explain_domain") + " - " + Messages.getMessage("item_logical_domain"))));
	}
	
	@Ignore
	@Test
	public void show_error_result() throws Exception {
		target.showErrorResultDialog("error message");
	}
	
	@Ignore
	@Test
	public void show_normal_result() throws Exception {
		Result result = new Result();
		result.appendMessage("aaaa\nbbbbb\nccccc");
		result.setErrorOccured(false);
		target.showResultDialog(ImportDialogBase.Status.NORMAL, result);
	}
	
	@Ignore
	@Test
	public void show_normal_result_with_error() throws Exception {
		Result result = new Result();
		result.appendMessage("aaaa\nbbbbb\nccccc");
		result.setErrorOccured(true);
		target.showResultDialog(ImportDialogBase.Status.NORMAL, result);
	}
}
