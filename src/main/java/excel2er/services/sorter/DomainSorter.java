package excel2er.services.sorter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import excel2er.models.Domain;

public class DomainSorter {

    private LogicalNameComparator logicalNameComparator = new LogicalNameComparator();
    private ParentDomainNameComparator parentDomainNameComparator = new ParentDomainNameComparator();
    private OrderLessGenerationComparator orderLessGenerationComparator = new OrderLessGenerationComparator();
    private static final String SEPARATOR = "::";

    public void sort(List<Domain> domains) {
        Collections.sort(domains, logicalNameComparator);
        Collections.sort(domains, parentDomainNameComparator);
        Collections.sort(domains, orderLessGenerationComparator);
    }

    private class LogicalNameComparator implements Comparator<Domain> {

        @Override
        public int compare(Domain domain1, Domain domain2) {
            String logicalName1 = StringUtils.defaultString(domain1.getLogicalName());
            String logicalName2 = StringUtils.defaultString(domain2.getLogicalName());
            return logicalName1.compareTo(logicalName2);
        }
    }

    private class ParentDomainNameComparator implements Comparator<Domain> {

        @Override
        public int compare(Domain domain1, Domain domain2) {
            return getName(domain1.getParentDomain()).compareTo(getName(domain2.getParentDomain()));
        }

        private String getName(String fullName) {
            String[] splitName = StringUtils.defaultString(fullName).split(SEPARATOR);
            return splitName[splitName.length - 1];
        }

    }

    private class OrderLessGenerationComparator implements Comparator<Domain> {

        @Override
        public int compare(Domain domain1, Domain domain2) {

            String parentDomainFullName1 = StringUtils.defaultString(domain1.getParentDomain());
            String parentDomainFullName2 = StringUtils.defaultString(domain2.getParentDomain());
            int parentGenerationNummber1 = parentDomainFullName1.split(SEPARATOR).length;
            int parentGenerationNummber2 = parentDomainFullName2.split(SEPARATOR).length;
            if (parentGenerationNummber1 > parentGenerationNummber2) {
                return 1;
            }
            if (parentGenerationNummber1 == parentGenerationNummber2) {
                return 0;
            }
            return -1;
        }
    }

}