package excel2er.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IEREntity;

import excel2er.AstahModelManager;
import excel2er.models.Entity;

public class ImportERModelServiceTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Rule
	public TestName testName = new TestName();
	
//	@Before
//	public void tearDown() throws Exception {
//		File newFile = folder.newFile();
//		System.out.println(testName.getMethodName() + " expected save as " + newFile.getPath());
//		AstahModelManager.save(newFile);
//	}
	
	@Test
	public void should_create_entity_in_empty_astah_project() throws Exception{
		AstahModelManager.open(getWorkspaceFileStream("empty.asta"));
		ImportERModelService service = new ImportERModelService();
		
		Entity entity = new Entity();
		entity.setEntityLogicalName("test");
		IEREntity actual = service.createAstahModel(entity);
		
		assertThat(actual.getLogicalName(),is("test"));
		
		//AstahAPI.getAstahAPI().getProjectAccessor().save();
	}

	private InputStream getWorkspaceFileStream(String filename) {
		return this.getClass().getResourceAsStream(filename);
	}
}
