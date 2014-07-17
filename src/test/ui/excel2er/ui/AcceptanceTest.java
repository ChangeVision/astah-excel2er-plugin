package excel2er.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.net.URL;

import javax.swing.JFrame;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.fest.swing.timing.Pause;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import excel2er.AstahModelManager;

@RunWith(GUITestRunner.class)
public class AcceptanceTest {

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
	public void accept_import() throws Exception {
		AstahModelManager.open(getWorkspaceFilePath("empty.asta"));
		
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				getWorkspaceFilePath("entityListModel.xls").getFile());
		dialogFixture.radioButton(EntityPanel.UseSheetNameButton.NAME).check();
		dialogFixture.textBox(ERAttributePanel.StartRow.NAME).setText("9");
		dialogFixture.textBox(ERAttributePanel.ItemCol.LOGICAL).setText("B");
		
		dialogFixture.button(ImportDialog.GenerateButton.NAME).click();
	
	}
	
	
	public void template() throws Exception {
		dialogFixture.textBox(InputFilePanel.InputFileText.NAME).setText(
				getWorkspaceFilePath("entityListModel.xls").getFile());

		dialogFixture.radioButton(EntityPanel.UseSheetNameButton.NAME).check();
//		dialogFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
//				.check();
//		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_ROW)
//				.setText("4");
//		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_COL)
//				.setText("5");
//		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_ROW)
//				.setText("40");
//		dialogFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_COL)
//				.setText("50");

		dialogFixture.textBox(ERAttributePanel.ItemCol.LOGICAL).setText("B");
//		dialogFixture.textBox(ERAttributePanel.ItemCol.PHYSICAL).setText("2");
//		dialogFixture.textBox(ERAttributePanel.ItemCol.PRIMARYKEY).setText("3");
//		dialogFixture.textBox(ERAttributePanel.ItemCol.NOTNULL).setText("4");
//		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFAULT_VALUE).setText(
//				"5");
//		dialogFixture.textBox(ERAttributePanel.ItemCol.DATATYPE).setText("6");
//		dialogFixture.textBox(ERAttributePanel.ItemCol.LENGTH).setText("7");
//		dialogFixture.textBox(ERAttributePanel.ItemCol.DEFINITION).setText("8");
//
//		assertThat(target.getConfiguration().getInputFilePath(),
//				is("/tmp/test/path"));
	}
	
	private URL getWorkspaceFilePath(String filename) {
		return this.getClass().getResource(filename);
	}
}
