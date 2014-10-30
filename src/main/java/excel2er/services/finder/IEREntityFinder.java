package excel2er.services.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERAttribute;
import com.change_vision.jude.api.inf.model.IEREntity;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class IEREntityFinder {

	public IEREntity findEREntity(final String entityName)
			throws ProjectNotFoundException, ClassNotFoundException {
		ProjectAccessor projectAccessor = AstahAPI.getAstahAPI()
				.getProjectAccessor();
		INamedElement[] result = projectAccessor.findElements(IEREntity.class,
				entityName);

		if (result != null && result.length > 0)
			return (IEREntity) result[0];

		return null;
	}

	public IERAttribute findERAttribute(IEREntity entity,
			String attributeLogicalName) throws ProjectNotFoundException,
			ClassNotFoundException {
		if(entity == null)
			return null;
		
		List<IERAttribute> attributes = new ArrayList<IERAttribute>();
		attributes.addAll(Arrays.asList(entity.getPrimaryKeys()));
		attributes.addAll(Arrays.asList(entity.getNonPrimaryKeys()));
		
		for (IERAttribute attr : attributes) {
			if(StringUtils.equals(attr.getLogicalName(), attributeLogicalName)){
				return attr;
			}
		}
		
		return null;
	}
}
