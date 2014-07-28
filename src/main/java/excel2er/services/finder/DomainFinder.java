package excel2er.services.finder;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERDomain;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import excel2er.models.Attribute;

public class DomainFinder {

	private static final Logger logger = LoggerFactory
			.getLogger(DomainFinder.class);
	
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

							logger.trace("matching " + domain.getName());
							EqualsBuilder eb = new EqualsBuilder();
							append("getDatatypeName",eb, domain.getDatatypeName(),
									convertNull(attr.getDataType()));
							// append(eb,domain.getDefaultValue(),
							// convertNull(attr.getDefaultValue()));
							append("getLengthPrecision",eb, domain.getLengthPrecision(),
									convertNull(attr.getLength()));
							append("getLogicalName",eb, domain.getLogicalName(),
									convertNull(attr.getLogicalName()));
							append("getPhysicalName",eb, domain.getPhysicalName(),
									convertNull(attr.getPhysicalName()));
							append("isNotNull",eb, domain.isNotNull(), attr.isNotNull());

							return eb.isEquals();

						}
						return false;
					}

					private void append(String message,EqualsBuilder eb, String one,
							String other) {
						logger.trace(message + " match(domain,importmodel) " + one + ":" + other);
						eb.append(one, other);
					}

					private void append(String message,EqualsBuilder eb, boolean one,
							boolean other) {
						logger.trace(message + " match(domain,importmodel) " + one + ":" + other);
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
