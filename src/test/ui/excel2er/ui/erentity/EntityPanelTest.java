package excel2er.ui.erentity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import excel2er.ui.erentity.EntityPanel;

@RunWith(GUITestRunner.class)
public class EntityPanelTest {

	private FrameFixture frameFixture;
	private JPanelFixture panelFixture;

	@Before
	public void setUp() throws Exception {

		JFrame frame = new JFrame();
		frameFixture = new FrameFixture(frame);
		EntityPanel target = new EntityPanel(new JDialog());
		frame.add(target);
		panelFixture = new JPanelFixture(frameFixture.robot, target);
		frameFixture.moveToFront();
		frameFixture.show();
		frameFixture.focus();
	}

	@After
	public void tearDown() throws Exception {
		if (frameFixture != null)
			frameFixture.cleanUp();
	}

	@Test
	public void should_be_able_to_select_single_radiobutton() throws Exception {
		panelFixture.radioButton(EntityPanel.UseSheetNameButton.NAME).click();
		panelFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME)
				.requireNotSelected();

		panelFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME).click();
		panelFixture.radioButton(EntityPanel.UseSheetNameButton.NAME)
				.requireNotSelected();
	}

	@Test
	public void should_get_usesheetname_button_is_selected() throws Exception {
		panelFixture.radioButton(EntityPanel.UseSheetNameButton.NAME).click();
		assertThat(((EntityPanel) panelFixture.target).isUseSheetName(),
				is(true));

		panelFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME).click();
		assertThat(((EntityPanel) panelFixture.target).isUseSheetName(),
				is(false));
	}

	@Test
	public void should_get_advancesetting_button_is_selected() throws Exception {
		panelFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME).click();
		assertThat(((EntityPanel) panelFixture.target).isAdvanceSetting(),
				is(true));

		panelFixture.radioButton(EntityPanel.UseSheetNameButton.NAME).click();
		assertThat(((EntityPanel) panelFixture.target).isAdvanceSetting(),
				is(false));
	}

	@Test
	public void should_get_logical_row_and_col() throws Exception {
		panelFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME).click();

		assertThat(((EntityPanel) panelFixture.target).getLogicalRow(), is("1"));
		assertThat(((EntityPanel) panelFixture.target).getLogicalCol(), is("H"));

		panelFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_ROW)
				.setText("3");
		panelFixture.textBox(EntityPanel.AdvanceElementRowCol.LOGICAL_COL)
				.setText("4");

		assertThat(((EntityPanel) panelFixture.target).getLogicalRow(), is("3"));
		assertThat(((EntityPanel) panelFixture.target).getLogicalCol(), is("4"));
	}

	@Test
	public void should_get_physical_row_and_col() throws Exception {
		panelFixture.radioButton(EntityPanel.AdvanceSettingButton.NAME).click();

		assertThat(((EntityPanel) panelFixture.target).getPhysicalRow(), is("2"));
		assertThat(((EntityPanel) panelFixture.target).getPhysicalCol(), is("H"));

		panelFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_ROW)
				.setText("8");
		panelFixture.textBox(EntityPanel.AdvanceElementRowCol.PHYSICAL_COL)
				.setText("9");

		assertThat(((EntityPanel) panelFixture.target).getPhysicalRow(),
				is("8"));
		assertThat(((EntityPanel) panelFixture.target).getPhysicalCol(),
				is("9"));
	}
}
