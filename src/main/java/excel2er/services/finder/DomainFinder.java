package excel2er.services.finder;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERDomain;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import excel2er.models.Attribute;

public class DomainFinder {

	public IERDomain find(final Attribute attr) throws ClassNotFoundException,
			ProjectNotFoundException {
		ProjectAccessor projectAccessor = AstahAPI.getAstahAPI()
				.getProjectAccessor();
		INamedElement[] result = projectAccessor
				.findElements(new ModelFinder() {

					@Override
					public boolean isTarget(INamedElement target) {
						if (target instanceof IERDomain) {
							IERDomain domain = (IERDomain) target;

							EqualsBuilder eb = new EqualsBuilder();
							append(eb, domain.getDatatypeName(),
									convertNull(attr.getDataType()));
							// append(eb,domain.getDefaultValue(),
							// convertNull(attr.getDefaultValue()));
							append(eb, domain.getLengthPrecision(),
									convertNull(attr.getLength()));
							append(eb, domain.getLogicalName(),
									convertNull(attr.getLogicalName()));
							append(eb, domain.getPhysicalName(),
									convertNull(attr.getPhysicalName()));
							append(eb, domain.isNotNull(), attr.isNotNull());

							return eb.isEquals();

						}
						return false;
					}

					private void append(EqualsBuilder eb, String one,
							String other) {
						eb.append(one, other);
					}

					private void append(EqualsBuilder eb, boolean one,
							boolean other) {
						eb.append(one, other);
					}

					private String convertNull(String value) {
						if (value == null) {
							return "";
						}
						return value;
					}
				});

		if (result != null && result.length > 0)
			return (IERDomain) result[0];

		return null;
	}

}
