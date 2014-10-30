package excel2er.services.finder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.change_vision.jude.api.inf.model.IERAttribute;
import com.change_vision.jude.api.inf.model.IEREntity;

import excel2er.AstahModelManager;

public class IEREntityFinderTest {

	@Test
	public void findEntity() throws Exception {
		AstahModelManager.open(this.getClass().getResource("find_er_entity.asta"));
		IEREntityFinder finder = new IEREntityFinder();

		
		IEREntity entity = finder.findEREntity("Entity1");
		
		assertThat(entity, is(notNullValue()));
		assertThat(entity.getLogicalName(),is("Entity1"));
	}

	@Test
	public void findAttribute() throws Exception {
		AstahModelManager.open(this.getClass().getResource("find_er_entity.asta"));
		IEREntityFinder finder = new IEREntityFinder();

		
		IERAttribute pk_attr1 = finder.findERAttribute(finder.findEREntity("Entity1"), "pk_attr1");
		
		assertThat(pk_attr1, is(notNullValue()));
		assertThat(pk_attr1.getLogicalName(),is("pk_attr1"));
		assertThat(pk_attr1.isPrimaryKey(),is(true));
		
		IERAttribute attr2 = finder.findERAttribute(finder.findEREntity("Entity1"), "attr2");
		
		assertThat(attr2, is(notNullValue()));
		assertThat(attr2.getLogicalName(),is("attr2"));
	}

}
