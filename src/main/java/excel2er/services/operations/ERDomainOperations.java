package excel2er.services.operations;

import com.change_vision.jude.api.inf.model.IERDomain;
import com.change_vision.jude.api.inf.model.IElement;

public class ERDomainOperations {

    public String getFullLogicalName(IERDomain domain, String separator) {
        StringBuffer fullLogicalName = new StringBuffer(domain.getLogicalName());
        IElement container = domain.getContainer();
        if (container instanceof IERDomain) {
            IERDomain parentDomain = (IERDomain) container;
            fullLogicalName.insert(0, separator);
            fullLogicalName.insert(0, getFullLogicalName(parentDomain, separator));
        }
        return fullLogicalName.toString();
    }

}
