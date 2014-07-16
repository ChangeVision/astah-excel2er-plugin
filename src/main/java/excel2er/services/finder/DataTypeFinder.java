package excel2er.services.finder;

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERDatatype;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class DataTypeFinder {

	public IERDatatype find(final String dataType)
			throws ProjectNotFoundException, ClassNotFoundException {
		ProjectAccessor projectAccessor = AstahAPI.getAstahAPI()
				.getProjectAccessor();
		INamedElement[] result = projectAccessor
				.findElements(new ModelFinder() {

					@Override
					public boolean isTarget(INamedElement target) {
						if (target instanceof IERDatatype) {
							IERDatatype domain = (IERDatatype) target;
							return StringUtils.equalsIgnoreCase(
									domain.getName(), dataType);
						}
						return false;
					}
				});

		if (result != null && result.length > 0)
			return (IERDatatype) result[0];

		return null;
	}

}
