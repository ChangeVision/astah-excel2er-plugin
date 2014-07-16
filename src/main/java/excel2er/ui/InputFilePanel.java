package excel2er.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import excel2er.Messages;

public class InputFilePanel extends JPanel {
	private static final long serialVersionUID = 6227307820865854523L;
	static final String NAME = "output_directory_panel";
	private InputFileText inputFileText;

	public InputFilePanel() {
		setName(NAME);
		setLayout(new GridBagLayout());
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setBorder(BorderFactory.createTitledBorder(Messages.getMessage("explain_input_file")));
		createContents();
		setVisible(true);
	}

	private void createContents() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(2, 10, 0, 10);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new Empty(), gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		add(inputFileText = new InputFileText(), gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 3;
		add(new ReferenceButton(), gbc);

	}

	class InputFileText extends JTextField {

		private static final int WIDTH = 200;
		private static final int HEIGHT = 14;
		private static final long serialVersionUID = -5561324124348079144L;
		static final String NAME = "output_directory";

		public InputFileText() {
			setName(NAME);
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
			setMinimumSize(new Dimension(WIDTH, HEIGHT));
		}
	}

	class ReferenceButton extends JButton {

		private static final long serialVersionUID = 4246421697607707364L;
		static final String NAME = "reference_button";

		public ReferenceButton() {
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle(Messages.getMessage("explain_input_file"));
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel File(xls,xlsx)", "xls", "xlsx"));
					int option = chooser.showOpenDialog(InputFilePanel.this);
					if (JFileChooser.APPROVE_OPTION == option) {
						File file = chooser.getSelectedFile();
						inputFileText.setText(file.getAbsolutePath());
					}
				}
			});
		}
	}


	public static void main(String[] args) {
		JFrame frame = new JFrame("OutputDirectoryPanelTest");
		JDialog dialog = new JDialog(frame);
		InputFilePanel p = new InputFilePanel();
		dialog.add(p);
		frame.setSize(400, 300);
		dialog.pack();
		dialog.setVisible(true);
	}

	public String getInputFilePath() {
		return inputFileText.getText();
	}


}
