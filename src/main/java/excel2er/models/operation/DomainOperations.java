package excel2er.models.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERDomain;

import excel2er.models.Domain;
import excel2er.services.finder.DomainFinder;
import excel2er.services.sorter.DomainSorter;

public class DomainOperations {

    private static final DomainFinder DOMAIN_FINDER = new DomainFinder();
    public static final String DEFAULT_DATA_TYPE = "CHAR";

    public List<Domain> addNeedCreateParentDomains(List<Domain> domains)
            throws ClassNotFoundException, ProjectNotFoundException {

        new DomainSorter().sort(domains);

        List<String> domainFullLogicalNames = new ArrayList<String>();
        for (Domain domain : domains) {
            domainFullLogicalNames.add(domain.getFullLogicalName());
        }

        for (Domain domain : domains) {
            String parentFullLogicalName = StringUtils.defaultString(domain.getParentDomain());
            if (parentFullLogicalName.isEmpty()) {
                continue;
            }
            if (domainFullLogicalNames.contains(parentFullLogicalName)) {
                continue;
            }
            String separator = domain.getNamespaceSeparator();
            if (isDomainInProject(domain.getParentDomain(), separator)) {
                continue;
            }
            ArrayList<Domain> newDomains = new ArrayList<Domain>(domains);
            Domain newParent = createDomain(parentFullLogicalName, separator, domain.getDataType());
            newDomains.add(newParent);
            return addNeedCreateParentDomains(newDomains);
        }
        return domains;
    }

    private Domain createDomain(String fullLogicalName, String nameSpaceSeparator,
            String dataTypeName) {
        Domain domain = new Domain();
        domain.setFullLogicalName(fullLogicalName, nameSpaceSeparator);
        if (domain.getParentDomain() != null) {
            return domain;
        }
        if (StringUtils.isNotEmpty(dataTypeName)) {
            domain.setDataType(dataTypeName);
            return domain;
        }
        domain.setDataType(DEFAULT_DATA_TYPE);
        return domain;
    }

    public boolean isDomainInProject(String domainFullLogicalName, String namespaceSeparator)
            throws ClassNotFoundException, ProjectNotFoundException {
        return getERDomainInProject(domainFullLogicalName, namespaceSeparator) != null;
    }

    public IERDomain getERDomainInProject(String domainFullLogicalName, String namespaceSeparator)
            throws ClassNotFoundException, ProjectNotFoundException {
        return DOMAIN_FINDER.find(domainFullLogicalName, namespaceSeparator);
    }

}
