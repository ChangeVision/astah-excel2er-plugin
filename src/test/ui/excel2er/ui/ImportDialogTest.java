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
import excel2er.services.ImportERModelService.Result;

@RunWith(GUITestRunner.class)
public class ImportDialogTest {

	private FrameFixture frameFixture;
	private DialogFixture dialogFixture;
	private ImportDialog target;

	@Before
	public void setUp() throws Exception {

		JFrame frame = new JFrame();
		frameFixture = new FrameFixture(frame);
		target = new ImportDialog(frame);
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

		assertThat(target.getConfiguration().getInputFilePath(),
				is("/tmp/test/path"));
	}

	@Test
	public void should_get_isUseSheetName() throws Exception {
		dialogFixture.radioButton(EntityPanel.UseSheetNameButton.NAME).check();

		assertThat(target.getConfiguration().isUseSheetName(), is(true));
	}

	@Test
	public void should_get_isAdvanceSetting() throws Exception {
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();

		assertThat(target.getConfiguration().isAdvanceSetting(), is(true));
	}

	@Test
	public void should_get_logical_row_and_col_for_entity() throws Exception {
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();

		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_ROW)
				.setText("4");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_COL)
				.setText("5");

		assertThat(target.getConfiguration().getEntityLogicalRow(), is("4"));
		assertThat(target.getConfiguration().getEntityLogicalCol(), is("5"));
	}

	@Test
	public void should_get_physical_row_and_col_for_entity() throws Exception {
		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.check();

		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_ROW)
				.setText("40");
		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_COL)
				.setText("50");

		assertThat(target.getConfiguration().getEntityPhysicalRow(), is("40"));
		assertThat(target.getConfiguration().getEntityPhysicalCol(), is("50"));
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

		assertThat(target.getConfiguration().getAttributeLogicalCol(), is("1"));
		assertThat(target.getConfiguration().getAttributePhysicalCol(), is("2"));
		assertThat(target.getConfiguration().getPrimaryKeyCol(), is("3"));
		assertThat(target.getConfiguration().getNotNullCol(), is("4"));
		assertThat(target.getConfiguration().getDefaultValueCol(), is("5"));
		assertThat(target.getConfiguration().getDataTypeCol(), is("6"));
		assertThat(target.getConfiguration().getLengthCol(), is("7"));
		assertThat(target.getConfiguration().getDefinitionCol(), is("8"));
	}

	@Test
	public void should_show_error_dialog_not_set_inputfile() throws Exception {
		dialogFixture.button(ImportDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();
		assertThat(dialogFixture.optionPane().textBox(ImportDialog.DETAIL_TEXT)
				.text(), is(Messages.getMessage("error.inputfile_required")));
	}

	@Test
	public void should_show_error_dialog_not_set_startrow() throws Exception {
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				"/tmp/dummy");
		dialogFixture.textBox(ERAttributePanel.StartRow.NAME).setText("");

		dialogFixture.button(ImportDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();

		assertThat(
				dialogFixture.optionPane().textBox(ImportDialog.DETAIL_TEXT)
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
		
		dialogFixture.button(ImportDialog.ImportButton.NAME).click();
		dialogFixture.optionPane().requireVisible();
		
		StringBuilder sb = new StringBuilder();
		sb.append(buildMessage("explain_entity","entity.logicalname.row")).append(SystemUtils.LINE_SEPARATOR);
		sb.append(buildMessage("explain_entity","entity.logicalname.col")).append(SystemUtils.LINE_SEPARATOR);
		sb.append(buildMessage("explain_entity","entity.physicalname.row")).append(SystemUtils.LINE_SEPARATOR);
		sb.append(buildMessage("explain_entity","entity.physicalname.col"));
		
		assertThat(dialogFixture.optionPane().textBox(ImportDialog.DETAIL_TEXT)
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
		result.inclementEntitesCount();
		result.appendMessage("aaaa\nbbbbb\nccccc");
		result.setErrorOccured(false);
		target.showResultDialog(ImportDialog.Status.NORMAL, result);
	}
	
	@Ignore
	@Test
	public void show_normal_result_with_error() throws Exception {
		Result result = new Result();
		result.inclementEntitesCount();
		result.appendMessage("aaaa\nbbbbb\nccccc");
		result.setErrorOccured(true);
		target.showResultDialog(ImportDialog.Status.NORMAL, result);
	}
	
	private String buildMessage(String key,String subkey){
		return Messages.getMessage(
				"error.required",Messages.getMessage(key) + " - " + Messages.getMessage(subkey));
	}
}
