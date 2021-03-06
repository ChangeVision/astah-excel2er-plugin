package excel2er.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.JFrame;

import org.apache.commons.lang3.SystemUtils;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import excel2er.Messages;
import excel2er.models.Configuration;
import excel2er.services.Result;

@RunWith(GUITestRunner.class)
public class ImportEREntityDialogTest {

	private FrameFixture frameFixture;
	private DialogFixture dialogFixture;
	private ImportEREntityDialog target;

	@Before
	public void setUp() throws Exception {

		JFrame frame = new JFrame();
		frameFixture = new FrameFixture(frame);
		target = new ImportEREntityDialog(frame);
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

	@Test
	public void should_get_inputfilepath() throws Exception {
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				"/tmp/test/path");

		assertThat(((Configuration)target.getConfiguration()).getInputFilePath(),
				is("/tmp/test/path"));
	}

	@Test
	public void should_get_isUseSheetName() throws Exception {
		dialogFixture.radioButton(EntityPanel.UseSheetNameButton.NAME).check();

		assertThat(((Configuration)target.getConfiguration()).isUseSheetName(), is(true));
	}

	@Test
	public void should_get_isAdvanceSetting() throws Exception {
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();

		assertThat(((Configuration)target.getConfiguration()).isAdvanceSetting(), is(true));
	}

	@Test
	public void should_get_logical_row_and_col_for_entity() throws Exception {
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();

		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_ROW)
				.setText("4");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_COL)
				.setText("5");

		assertThat(((Configuration)target.getConfiguration()).getEntityLogicalRow(), is("4"));
		assertThat(((Configuration)target.getConfiguration()).getEntityLogicalCol(), is("5"));
	}

	@Test
	public void should_get_physical_row_and_col_for_entity() throws Exception {
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();

		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_ROW)
				.setText("40");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_COL)
				.setText("50");

		assertThat(((Configuration)target.getConfiguration()).getEntityPhysicalRow(), is("40"));
		assertThat(((Configuration)target.getConfiguration()).getEntityPhysicalCol(), is("50"));
	}

	@Test
	public void should_get_item() throws Exception {
		dialogFixture.textBox(ERAttributePanel.ItemCol.LOGICAL).setText("1");
		dialogFixture.textBox(ERAttributePanel.ItemCol.PHYSICAL).setText("2");
		dialogFixture.textBox(ERAttributePanel.ItemCol.PRIMARYKEY).setText("3");
		dialogFixture.textBox(ERAttributePanel.ItemCol.NOTNULL).setText("4");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFAULT_VALUE).setText(
				"5");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DATATYPE).setText("6");
		dialogFixture.textBox(ERAttributePanel.ItemCol.LENGTH).setText("7");
		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFINITION).setText("8");

		assertThat(((Configuration)target.getConfiguration()).getAttributeLogicalCol(), is("1"));
		assertThat(((Configuration)target.getConfiguration()).getAttributePhysicalCol(), is("2"));
		assertThat(((Configuration)target.getConfiguration()).getPrimaryKeyCol(), is("3"));
		assertThat(((Configuration)target.getConfiguration()).getNotNullCol(), is("4"));
		assertThat(((Configuration)target.getConfiguration()).getDefaultValueCol(), is("5"));
		assertThat(((Configuration)target.getConfiguration()).getDataTypeCol(), is("6"));
		assertThat(((Configuration)target.getConfiguration()).getLengthCol(), is("7"));
		assertThat(((Configuration)target.getConfiguration()).getDefinitionCol(), is("8"));
	}

	@Test
	public void should_show_error_dialog_not_set_inputfile() throws Exception {
		dialogFixture.button(ImportEREntityDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();
		assertThat(dialogFixture.optionPane().textBox(ImportEREntityDialog.DETAIL_TEXT)
				.text(), is(Messages.getMessage("error.inputfile_required")));
	}

	@Test
	public void should_show_error_dialog_not_set_startrow() throws Exception {
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				"/tmp/dummy");
		dialogFixture.textBox(ERAttributePanel.StartRow.NAME).setText("");

		dialogFixture.button(ImportEREntityDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();

		assertThat(
				dialogFixture.optionPane().textBox(ImportEREntityDialog.DETAIL_TEXT)
						.text(),
				is(Messages.getMessage("error.required",
						Messages.getMessage("explain_attribute") + " - "
								+ Messages.getMessage("start_row"))));
	}

	@Test
	public void should_show_error_dialog_not_set_entity_logical_physical_col_and_row()
			throws Exception {
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				this.getClass().getResource("entityListModel.xls").getFile());
		
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();

		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_COL).setText("");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_ROW).setText("");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_COL).setText("");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_ROW).setText("");
		
		dialogFixture.button(ImportEREntityDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();
		
		StringBuilder sb = new StringBuilder();
		sb.append(buildMessage("explain_entity","entity.logicalname.row")).append(SystemUtils.LINE_SEPARATOR);
		sb.append(buildMessage("explain_entity","entity.logicalname.col")).append(SystemUtils.LINE_SEPARATOR);
		sb.append(buildMessage("explain_entity","entity.physicalname.row")).append(SystemUtils.LINE_SEPARATOR);
		sb.append(buildMessage("explain_entity","entity.physicalname.col"));
		
		assertThat(dialogFixture.optionPane().textBox(ImportEREntityDialog.DETAIL_TEXT)
				.text(), is(sb.toString()));
		
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
	
	private String buildMessage(String key,String subkey){
		return Messages.getMessage(
				"error.required",Messages.getMessage(key) + " - " + Messages.getMessage(subkey));
	}
}
