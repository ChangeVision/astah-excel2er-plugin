package excel2er.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
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
import excel2er.models.ConfigurationBase;
import excel2er.services.Result;

public abstract class ImportDialogBase extends JDialog{

	private static final long serialVersionUID = -5864706222779977637L;
	static final String DETAIL_TEXT = "detail_text";
	
	public ImportDialogBase(Frame owner, boolean modal) {
		super(owner, modal);
	}

	protected void close() {
		setVisible(false);
	}
	
	public void execute(){
		Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		this.setCursor(waitCursor);

		try {
			List<ValidationError> errors = validateInput();

			if (errors.size() > 0) {
				showErrorResultDialog(getValidationErrorMessage(errors));
				return;
			}
			
			executeService();
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
	
	List<ValidationError> validateInput() {
		return getConfiguration().validate();
	}
	
	protected abstract ConfigurationBase getConfiguration();
	
	protected abstract void executeService();
	
	protected String getValidationErrorMessage(List<ValidationError> errors) {
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

	protected void showErrorResultDialog(String errorMessage) {
		Result result = new Result();
		result.appendMessage(errorMessage);
		showResultDialog(Status.ERROR, result);
	}

	protected void showResultDialog(Status status, Result result) {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		int messageType = JOptionPane.INFORMATION_MESSAGE;
		String mainMessage = null;
		if (status.equals(Status.NORMAL)) {
			if (result.isErrorOccured()) {
				mainMessage = Messages.getMessage(
						"result.dialog.normal_with_error",
                        result.getImportedElementsCount());
			} else {
				mainMessage = Messages.getMessage("result.dialog.normal",
                        result.getImportedElementsCount());
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
            JTextArea detailTextArea = new JTextArea(detailMessage);
            detailTextArea.setFont(messageLabel.getFont());
            detailTextArea.setRows(25);
            detailTextArea.setColumns(60);
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
	
	enum Status {
		NORMAL, ERROR;
	}
}
