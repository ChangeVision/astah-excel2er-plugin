package excel2er.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import excel2er.Messages;
import excel2er.models.Configuration;
import excel2er.models.ConfigurationBase;
import excel2er.services.ImportERModelService;
import excel2er.services.Result;

public class ImportDialog extends ImportDialogBase {

	private static final long serialVersionUID = 8758963086319476079L;
	private InputFilePanel inputFilePanel;
	private ImportButton generateButton;
	private EntityPanel entityPanel;
	private ERAttributePanel attributePanel;
	private static final String NAME = "ImportDialog";
	private static int WIDTH = 510;
	private static int HEIGHT = 140;
	protected static final int GAP = 1;

	public ImportDialog(JFrame window) {
		super(window, true);
		setName(NAME);
		setTitle(Messages.getMessage("dialog.title"));
		createContents();
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(window);
	}

	private void createContents() {
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
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

	protected void executeService() {
		ImportERModelService service = new ImportERModelService();
		Result result = service.importERModel((Configuration)getConfiguration());

		showResultDialog(Status.NORMAL, result);
	}


	private void createSouthContent() {
		JPanel sourthContentPanel = new JPanel(new GridLayout(1, 2, GAP, GAP));
		generateButton = new ImportButton(new ActionListener() {
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
	class ImportButton extends JButton {
		static final String NAME = "import";

		private ImportButton(ActionListener listener) {
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

	public ConfigurationBase getConfiguration() {
		Configuration configuration = new Configuration();

		configuration.setInputFilePath(inputFilePanel.getInputFilePath());
		configuration.setUseSheetName(entityPanel.isUseSheetName());
		configuration.setAdvanceSetting(entityPanel.isAdvanceSetting());
		configuration.setEntityLogicalRow(entityPanel.getLogicalRow());
		configuration.setEntityLogicalCol(entityPanel.getLogicalCol());
		configuration.setEntityPhysicalRow(entityPanel.getPhysicalRow());
		configuration.setEntityPhysicalCol(entityPanel.getPhysicalCol());
		configuration.setStartRow(attributePanel.getStartRow());
		configuration.setAttributeLogicalCol(attributePanel.getLogicalCol());
		configuration.setAttributePhysicalCol(attributePanel.getPhysicalCol());
		configuration.setPrimaryKeyCol(attributePanel.getPrimaryKeyCol());
		configuration.setNotNullCol(attributePanel.getNotNullCol());
		configuration.setDefaultValueCol(attributePanel.getDefaultValueCol());
		configuration.setDataTypeCol(attributePanel.getDataTypeCol());
		configuration.setLengthCol(attributePanel.getLengthCol());
		configuration.setDefinitionCol(attributePanel.getDefinitionCol());

		return configuration;
	}

}
