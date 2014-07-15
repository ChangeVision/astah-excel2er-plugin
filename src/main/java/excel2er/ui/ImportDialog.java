package excel2er.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import excel2er.Messages;

public class ImportDialog extends JDialog{

	private static final long serialVersionUID = 8758963086319476079L;
	private InputFilePanel inputFilePanel;
	private GenerateButton generateButton;
	private EntityPanel entityPanel;
	private ERAttributePanel attributePanel;
	private static final String NAME = "ImportDialog";
	private static int WIDTH = 510;
	private static int HEIGHT = 140;
	protected static final int GAP = 1;
	
	public ImportDialog(JFrame window) {
		super(window,true);
		setName(NAME);
		setTitle(Messages.getMessage("dialog.title"));
		createContents();
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(window);
	}
	
	
	private void createContents() {
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		createMainContent();

		createSouthContent();
        getRootPane().setDefaultButton(generateButton);
	}

	private void createMainContent() {
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		inputFilePanel = new InputFilePanel();
		mainContentPanel.add(inputFilePanel, gbc);
		gbc.gridy = 1;
		entityPanel = new EntityPanel(this);
		mainContentPanel.add(entityPanel, gbc);
		
		gbc.gridy = 2;
		attributePanel = new ERAttributePanel();
		mainContentPanel.add(attributePanel, gbc);

		getContentPane().add(mainContentPanel);
	}

	protected void close() {
		setVisible(false);
	}

	private void execute() {
		
	}
	
	private void createSouthContent() {
		JPanel sourthContentPanel = new JPanel(new GridLayout(1, 2, GAP, GAP));
		generateButton = new GenerateButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		sourthContentPanel.add(generateButton);
		sourthContentPanel.add(new CancelButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		}));
		getContentPane().add(sourthContentPanel);
	}

	@SuppressWarnings("serial")
	class GenerateButton extends JButton {
		static final String NAME = "generate";

		private GenerateButton(ActionListener listener) {
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addActionListener(listener);
		}
	}

	@SuppressWarnings("serial")
	class CancelButton extends JButton {
		static final String NAME = "cancel";

		private CancelButton(ActionListener listener) {
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addActionListener(listener);
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");
		ImportDialog p = new ImportDialog(frame);
		p.pack();
		p.setVisible(true);
	}

	public String getInputFilePath() {
		return inputFilePanel.getInputFilePath();
	}

	public Boolean isUseSheetName() {
		return entityPanel.isUseSheetName();
	}

	public Boolean isAdvanceSetting() {
		return entityPanel.isAdvanceSetting();
	}

	public String getElementLogicalRow() {
		return entityPanel.getLogicalRow();
	}

	public String getElementLogicalCol() {
		return entityPanel.getLogicalCol();
	}

	public String getPhysicalRow() {
		return entityPanel.getPhysicalRow();
	}

	public String getPhysicalCol() {
		return entityPanel.getPhysicalCol();
	}

	public String getAttributeLogicalCol() {
		return attributePanel.getLogicalCol();
	}

	public String getAttbitutePhysicalCol() {
		return attributePanel.getPhysicalCol();
	}

	public String getPrimaryKey() {
		return attributePanel.getPrimaryKey();
	}

	public String getNotNullCol() {
		return attributePanel.getNotNullCol();
	}

	public String getDefaultValueCol() {
		return attributePanel.getDefaultValueCol();
	}

	public String getDataTypeCol() {
		return attributePanel.getDataTypeCol();
	}

	public String getLengthCol() {
		return attributePanel.getLengthCol();
	}

	public String getDefinitionCol() {
		return attributePanel.getDefinitionCol();
	}
	
}
