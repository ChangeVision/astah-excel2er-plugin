package excel2er;

import java.io.InputStream;

import junit.framework.AssertionFailedError;

import com.change_vision.jude.api.inf.AstahAPI;

public class AstahModelManager {

	public static void open(InputStream stream) throws Exception {
		if (stream == null)
			throw new AssertionFailedError("project not found.");

		AstahAPI.getAstahAPI().getProjectAccessor().open(stream);
	}

	public static void close() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
}
