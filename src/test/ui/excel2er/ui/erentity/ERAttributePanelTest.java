package excel2er.ui.erentity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import javax.swing.JFrame;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import excel2er.ui.erentity.ERAttributePanel;

@RunWith(GUITestRunner.class)
public class ERAttributePanelTest {

	private FrameFixture frameFixture;
	private JPanelFixture panelFixture;

	@Before
	public void setUp() throws Exception {

		JFrame frame = new JFrame();
		frameFixture = new FrameFixture(frame);
		ERAttributePanel target = new ERAttributePanel();
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
	public void should_get_initial_value_of_start_row() throws Exception {
		assertThat(((ERAttributePanel) panelFixture.target).getStartRow(),
				is("9"));
	}

	@Test
	public void should_get_start_row() throws Exception {
		panelFixture.textBox(ERAttributePanel.StartRow.NAME).setText("10");

		assertThat(((ERAttributePanel) panelFixture.target).getStartRow(),
				is("10"));
	}

	@Test
	public void should_get_item() throws Exception {
		panelFixture.textBox(ERAttributePanel.ItemCol.LOGICAL).setText("1");
		panelFixture.textBox(ERAttributePanel.ItemCol.PHYSICAL).setText("2");
		panelFixture.textBox(ERAttributePanel.ItemCol.PRIMARYKEY).setText("3");
		panelFixture.textBox(ERAttributePanel.ItemCol.NOTNULL).setText("4");
		panelFixture.textBox(ERAttributePanel.ItemCol.DEFAULT_VALUE).setText(
				"5");
		panelFixture.textBox(ERAttributePanel.ItemCol.DATATYPE).setText("6");
		panelFixture.textBox(ERAttributePanel.ItemCol.LENGTH).setText("7");
		panelFixture.textBox(ERAttributePanel.ItemCol.DEFINITION).setText("8");

		assertThat(((ERAttributePanel) panelFixture.target).getLogicalCol(),
				is("1"));
		assertThat(((ERAttributePanel) panelFixture.target).getPhysicalCol(),
				is("2"));
		assertThat(((ERAttributePanel) panelFixture.target).getPrimaryKeyCol(),
				is("3"));
		assertThat(((ERAttributePanel) panelFixture.target).getNotNullCol(),
				is("4"));
		assertThat(
				((ERAttributePanel) panelFixture.target).getDefaultValueCol(),
				is("5"));
		assertThat(((ERAttributePanel) panelFixture.target).getDataTypeCol(),
				is("6"));
		assertThat(((ERAttributePanel) panelFixture.target).getLengthCol(),
				is("7"));
		assertThat(((ERAttributePanel) panelFixture.target).getDefinitionCol(),
				is("8"));
	}

}
