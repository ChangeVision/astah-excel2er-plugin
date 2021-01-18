package excel2er.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import javax.swing.JFrame;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import excel2er.ui.InputFilePanel;

@RunWith(GUITestRunner.class)
public class InputFilePanelTest {

	private FrameFixture frameFixture;
	private JPanelFixture panelFixture;

	@Before
	public void setUp() throws Exception {

		JFrame frame = new JFrame();
		frameFixture = new FrameFixture(frame);
		InputFilePanel target = new InputFilePanel();
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
	public void should_text_display_filepath_where_approve_by_filechooser()
			throws Exception {
		panelFixture.button(InputFilePanel.ReferenceButton.NAME).click();

        File expectedFilePath = new File(
                this.getClass().getResource("entityListModel.xls").getFile());

		panelFixture.fileChooser().requireEnabled();
        panelFixture.fileChooser().selectFile(expectedFilePath);
		panelFixture.fileChooser().approve();

        assertThat(new File(panelFixture.textBox().text()), is(expectedFilePath));
	}

	@Test
	public void should_accept_only_excelfile() throws Exception {
		panelFixture.button(InputFilePanel.ReferenceButton.NAME).click();

		File file = new File(this.getClass().getResource("entityListModel.xls")
				.getFile());
		assertThat(
				panelFixture.fileChooser().target.getFileFilter().accept(file),
				is(true));

		file = new File(this.getClass().getResource("test.xlsx").getFile());
		assertThat(
				panelFixture.fileChooser().target.getFileFilter().accept(file),
				is(true));

		file = new File(this.getClass().getResource("test.docx").getFile());
		assertThat(
				panelFixture.fileChooser().target.getFileFilter().accept(file),
				is(false));

	}

}
