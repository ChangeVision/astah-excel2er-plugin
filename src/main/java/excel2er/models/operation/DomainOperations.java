package excel2er.models.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;

import excel2er.models.Domain;
import excel2er.services.finder.DomainFinder;
import excel2er.services.sorter.DomainSorter;

public class DomainOperations {

    public static final String DEFAULT_DATA_TYPE = "CHAR";

    public List<Domain> addNeedCreateParentDomains(List<Domain> domains)
            throws ClassNotFoundException, ProjectNotFoundException {

        new DomainSorter().sort(domains);

        List<String> domainFullLogicalNames = new ArrayList<String>();
        for (Domain domain : domains) {
            domainFullLogicalNames.add(domain.getFullLogicalName());
        }

        DomainFinder finder = new DomainFinder();
        for (Domain domain : domains) {
            String parentDomainFullLogicalName = StringUtils
                    .defaultString(domain.getParentDomain());
            if (parentDomainFullLogicalName.isEmpty()) {
                continue;
            }
            if (domainFullLogicalNames.contains(parentDomainFullLogicalName)) {
                continue;
            }
            String separator = domain.getNamespaceSeparator();
            if (finder.find(parentDomainFullLogicalName, separator) != null) {
                continue;
            }
            ArrayList<Domain> newDomains = new ArrayList<Domain>(domains);
            newDomains.add(createDomain(parentDomainFullLogicalName, separator));
            return addNeedCreateParentDomains(newDomains);
        }
        return domains;
    }

    private Domain createDomain(String fullLogicalName, String nameSpaceSeparator) {
        Domain domain = new Domain();
        domain.setFullLogicalName(fullLogicalName, nameSpaceSeparator);
        if (domain.getParentDomain() == null) {
            domain.setDataType(DEFAULT_DATA_TYPE);
        }
        return domain;
    }

}
