package excel2er.actions;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

import excel2er.Messages;
import excel2er.ui.ImportDialog;

public class ImportExcelToERModelAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
		try {

			checkOpenProject();

			JFrame frame = (JFrame) window.getParent();
			ImportDialog dialog = new ImportDialog(frame);
			dialog.setVisible(true);

		} catch (ProjectNotFoundException e) {
			JOptionPane.showMessageDialog(window.getParent(),
					Messages.getMessage("error.project.not.found"), "Warn",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(window.getParent(),
					"Unexpected error has occurred.", "Alert",
					JOptionPane.ERROR_MESSAGE);
			throw new UnExpectedException();
		}
		return null;
	}

	private void checkOpenProject() throws ClassNotFoundException,
			ProjectNotFoundException {
		AstahAPI.getAstahAPI().getProjectAccessor().getProject();
	}

}
