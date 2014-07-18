package excel2er.ui.erentity;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.exceptions.ValidationError;
import excel2er.models.Configuration;
import excel2er.services.ImportERModelService;
import excel2er.services.ImportERModelService.Result;

public class ImportDialog extends JDialog {

	private static final long serialVersionUID = 8758963086319476079L;
	private InputFilePanel inputFilePanel;
	private ImportButton generateButton;
	private EntityPanel entityPanel;
	private ERAttributePanel attributePanel;
	private static final String NAME = "ImportDialog";
	private static int WIDTH = 510;
	private static int HEIGHT = 140;
	protected static final int GAP = 1;
	static final String DETAIL_TEXT = "detail_text";

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

	protected void close() {
		setVisible(false);
	}

	private void execute() {
		Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		this.setCursor(waitCursor);

		ImportERModelService service = new ImportERModelService();
		try {
			List<ValidationError> errors = validateInput();

			if (errors.size() > 0) {
				showErrorResultDialog(getValidationErrorMessage(errors));
				return;
			}

			Result result = service.importERModel(getConfiguration());

			showResultDialog(Status.NORMAL, result);
		} catch (Throwable t) {
			StringWriter sw = new StringWriter();
			PrintWriter w = new PrintWriter(sw);
			t.printStackTrace(w);
			showErrorResultDialog(sw.toString());
			throw new ApplicationException(t);
		} finally {
			Cursor defaultCursor = Cursor.getDefaultCursor();
			this.setCursor(defaultCursor);
			close();
		}
	}

	private String getValidationErrorMessage(List<ValidationError> errors) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < errors.size(); i++) {
			ValidationError error = errors.get(i);
			sb.append(error.getMessage());
			if (i < errors.size() - 1) {
				sb.append(SystemUtils.LINE_SEPARATOR);
			}
		}
		return sb.toString();
	}

	void showErrorResultDialog(String errorMessage) {
		ImportERModelService.Result result = new ImportERModelService.Result();
		result.appendMessage(errorMessage);
		showResultDialog(Status.ERROR, result);
	}

	void showResultDialog(Status status, Result result) {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		int messageType = JOptionPane.INFORMATION_MESSAGE;
		String mainMessage = null;
		if (status.equals(Status.NORMAL)) {
			if (result.isErrorOccured()) {
				mainMessage = Messages.getMessage(
						"result.dialog.normal_with_error",
						result.getCreatedEntitiesCount());
			} else {
				mainMessage = Messages.getMessage("result.dialog.normal",
						result.getCreatedEntitiesCount());
			}
		} else if (status.equals(Status.ERROR)) {
			messageType = JOptionPane.ERROR_MESSAGE;
			mainMessage = Messages.getMessage("result.dialog.error");
		}

		final JLabel messageLabel = new JLabel(mainMessage);
		messageLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(messageLabel);

		String detailMessage = result.getMessage();
		if (StringUtils.isNotEmpty(detailMessage)) {
			JTextArea detailTextArea = new JTextArea(detailMessage, 5, 10);
			detailTextArea.setName(DETAIL_TEXT);
			detailTextArea.setAlignmentY(Component.BOTTOM_ALIGNMENT);
			detailTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
			detailTextArea.setEditable(false);
			detailTextArea.setLineWrap(true);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(detailTextArea);

			panel.add(scrollPane);
		}

		JOptionPane.showMessageDialog(this, panel,
				Messages.getMessage("result.dialog.title"), messageType);
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

	public Configuration getConfiguration() {
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

	List<ValidationError> validateInput() {
		return getConfiguration().validate();
	}

	enum Status {
		NORMAL, ERROR;
	}
}
