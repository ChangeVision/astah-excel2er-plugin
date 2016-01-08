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
import excel2er.models.ConfigurationBase;
import excel2er.models.DomainConfiguration;
import excel2er.services.ImportERDomainService;
import excel2er.services.Result;

public class ImportERDomainDialog extends ImportDialogBase{

	private static final long serialVersionUID = -4227686440674677417L;

	private InputFilePanel inputFilePanel;
	private ImportButton importButton;

	private DomainPanel domainPanel;
	private static final String NAME = "ImportERDomainDialog";
	private static int WIDTH = 510;
	private static int HEIGHT = 140;
	protected static final int GAP = 1;
	static final String DETAIL_TEXT = "detail_text";

	public ImportERDomainDialog(JFrame window) {
		super(window, true);
		setName(NAME);
		setTitle(Messages.getMessage("dialog.erdomain.title"));
		createContents();
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(window);
	}

	private void createContents() {
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		createMainContent();

		createSouthContent();
		getRootPane().setDefaultButton(importButton);
	}

	private void createMainContent() {
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		inputFilePanel = new InputFilePanel();
		inputFilePanel.setInputFileText(ConfigUtil.getDomainLatestLoadedFile());
		mainContentPanel.add(inputFilePanel, gbc);
		gbc.gridy = 1;
		domainPanel = new DomainPanel();
		mainContentPanel.add(domainPanel, gbc);

		getContentPane().add(mainContentPanel);
	}

	private void createSouthContent() {
		JPanel sourthContentPanel = new JPanel(new GridLayout(1, 2, GAP, GAP));
		importButton = new ImportButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		sourthContentPanel.add(importButton);
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
		ImportERDomainDialog p = new ImportERDomainDialog(frame);
		p.pack();
		p.setVisible(true);
	}

	@Override
	protected ConfigurationBase getConfiguration() {
		DomainConfiguration configuration = new DomainConfiguration();

		configuration.setInputFilePath(inputFilePanel.getInputFilePath());
		configuration.setLogicalCol(domainPanel.getLogicalCol());
		configuration.setPhysicalCol(domainPanel.getPhysicalCol());
		configuration.setStartRow(domainPanel.getStartRow());
		configuration.setDataTypeCol(domainPanel.getDataTypeCol());
        configuration.setParentDomainCol(domainPanel.getParentDomainCol());
		configuration.setDefinitionCol(domainPanel.getDefinitionCol());

		return configuration;
	}

	@Override
	protected void executeService() {
		
		ImportERDomainService service = new ImportERDomainService();
		Result result = service.importERDomain((DomainConfiguration)getConfiguration());

		saveLatestSettingToConfigurationFile();
		
		showResultDialog(Status.NORMAL, result);
	}

	private void saveLatestSettingToConfigurationFile(){
		ConfigUtil.saveDomainLatestLoadedFilePath(inputFilePanel.getInputFilePath());
		
		domainPanel.saveLatestSetting();
	}
}
