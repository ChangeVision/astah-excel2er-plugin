package excel2er.models;

import org.apache.commons.lang3.StringUtils;

public class Domain {

    private String namespaceSeparator = "::";

	private String logicalName;
	private String physicalName;
    private String alias1;
    private String alias2;
	private String dataType;
    private String lengthAndPrecision;
    private String notNull;
    private String parentDomain;
	private String definition;

	public String getNamespaceSeparator() {
        return namespaceSeparator;
    }

    public void setNamespaceSeparator(String namespaceSeparator) {
        this.namespaceSeparator = namespaceSeparator;
    }

    public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	public String getLogicalName() {
		return logicalName;
	}

	public String getPhysicalName() {
		return physicalName;
	}

	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}

    public String getAlias1() {
        return alias1;
    }

    public void setAlias1(String alias1) {
        this.alias1 = alias1;
    }

    public String getAlias2() {
        return alias2;
    }

    public void setAlias2(String alias2) {
        this.alias2 = alias2;
    }

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

    public String getLengthAndPrecision() {
        return lengthAndPrecision;
    }

    public void setLengthAndPrecision(String lengthAndPrecision) {
        this.lengthAndPrecision = lengthAndPrecision;
    }

    public boolean getNotNull() {
        if ("Y".equals(notNull)) {
            return true;
        }
        if ("N".equals(notNull)) {
            return false;
        }
        return StringUtils.isNotEmpty(notNull);
    }

    public void setNotNull(String notNull) {
        this.notNull = notNull;
    }

    public String getParentDomain() {
        return parentDomain;
    }

    public void setParentDomain(String parentDomain) {
        this.parentDomain = parentDomain;
    }

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

    public String getFullLogicalName() {
        if (getLogicalName() == null) {
            return null;
        }
        StringBuffer fullLogicalNameBuffer = new StringBuffer(getLogicalName());
        String parentDomainFullLogicalName = StringUtils.defaultString(getParentDomain());
        if (!parentDomainFullLogicalName.isEmpty()) {
            fullLogicalNameBuffer.insert(0, getNamespaceSeparator());
            fullLogicalNameBuffer.insert(0, parentDomainFullLogicalName);
        }
        return fullLogicalNameBuffer.toString();
    }

    public void setFullLogicalName(String fullLogicalName, String nameSpaceSeparator) {
        setNamespaceSeparator(nameSpaceSeparator);
        if (fullLogicalName == null) {
            setLogicalName(null);
            return;
        }
        String[] splitLogicalName = fullLogicalName.split(nameSpaceSeparator);
        String logicalName = splitLogicalName[splitLogicalName.length - 1];
        setLogicalName(logicalName);
        if (splitLogicalName.length > 1) {
            String[] parentDomainSplitLogicalName = new String[splitLogicalName.length - 1];
            for (int i = 0; i < parentDomainSplitLogicalName.length; i++) {
                parentDomainSplitLogicalName[i] = splitLogicalName[i];
            }
            setParentDomain(StringUtils.join(parentDomainSplitLogicalName, nameSpaceSeparator));
        }
    }

}
