package excel2er;

import java.io.File;
import java.net.URL;

import com.change_vision.jude.api.inf.AstahAPI;

public class AstahModelManager {

	public static void open(URL url) throws Exception {
		if(url == null)
			throw new IllegalArgumentException("missing url");
		
		AstahAPI.getAstahAPI().getProjectAccessor().open(url.getFile());
	}

	public static void close() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	public static void save(File file) throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().saveAs(file.getPath());
	}
	
}
