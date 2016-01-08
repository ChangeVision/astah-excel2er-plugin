package excel2er.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.JFrame;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import excel2er.ConfigClearRule;

@RunWith(GUITestRunner.class)
public class DomainPanelTest {

	private FrameFixture frameFixture;
	private JPanelFixture panelFixture;

	@Rule
	public ConfigClearRule rule = new ConfigClearRule();
	private DomainPanel target;
	
	@Before
	public void setUp() throws Exception {
		JFrame frame = new JFrame();
		frameFixture = new FrameFixture(frame);
		target = new DomainPanel();
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
	public void should_get_start_row() throws Exception {
		panelFixture.textBox(DomainPanel.StartRow.NAME).setText("12");
		
		assertThat(target.getStartRow(),is("12"));
	}
	
	@Test
	public void should_get_logical_col() throws Exception {
		panelFixture.textBox(DomainPanel.ItemCol.LOGICAL).setText("A");
		
		assertThat(target.getLogicalCol(),is("A"));
	}

	@Test
	public void should_get_physical_col() throws Exception {
		panelFixture.textBox(DomainPanel.ItemCol.PHYSICAL).setText("Z");
		
		assertThat(target.getPhysicalCol(),is("Z"));
	}
	
    @Test
    public void should_get_alias1_col() throws Exception {
        panelFixture.textBox(DomainPanel.ItemCol.ALIAS1).setText("W");

        assertThat(target.getAlias1Col(), is("W"));
    }

    @Test
    public void should_get_alias2_col() throws Exception {
        panelFixture.textBox(DomainPanel.ItemCol.ALIAS2).setText("W");

        assertThat(target.getAlias2Col(), is("W"));
    }

	@Test
	public void should_get_datatype_col() throws Exception {
		panelFixture.textBox(DomainPanel.ItemCol.DATATYPE).setText("X");
		
		assertThat(target.getDataTypeCol(),is("X"));
	}

    @Test
    public void should_get_length_and_precision_col() throws Exception {
        panelFixture.textBox(DomainPanel.ItemCol.LENGTH_AND_PRECISION).setText("W");

        assertThat(target.getLengthAndPrecisionCol(), is("W"));
    }

    @Test
    public void should_get_not_null_col() throws Exception {
        panelFixture.textBox(DomainPanel.ItemCol.NOTNULL).setText("W");

        assertThat(target.getNotNullCol(), is("W"));
    }

    @Test
    public void should_get_parent_domain_col() throws Exception {
        panelFixture.textBox(DomainPanel.ItemCol.PARENT_DOMAIN).setText("D");

        assertThat(target.getParentDomainCol(), is("D"));
    }

	@Test
	public void should_get_definition_col() throws Exception {
		panelFixture.textBox(DomainPanel.ItemCol.DEFINITION).setText("W");
		
		assertThat(target.getDefinitionCol(),is("W"));
	}
}
