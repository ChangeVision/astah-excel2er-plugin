package excel2er.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Attribute {

	private String logicalName;
	private boolean isPrimaryKey;
	private String physicalName;
	private boolean isNotNull;
	private String dataType;
	private String length;
	private String defaultValue;
	private String definition;
    private String reference;
    private boolean foreignKey;
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("(.*)\\((.*)\\)");

	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	public String getLogicalName() {
		return logicalName;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public String getPhysicalName() {
		return physicalName;
	}

	public boolean isNotNull() {
		return isNotNull;
	}

	public String getDataType() {
		return dataType;
	}

	public String getLength() {
		return length;
	}

	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public void setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}

	public void setDataType(String value) {
		this.dataType = value;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String value) {
		this.definition = value;
	}

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public boolean isForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getReferenceEntityName() {
        String reference = getReference();
        if (StringUtils.isEmpty(reference)) {
            return null;
        }
        Matcher m = REFERENCE_PATTERN.matcher(reference);
        if (!m.find()) {
            return null;
        }
        return StringUtils.trim(m.group(1));
    }

    public String getReferenceAttributeName() {
        String reference = getReference();
        if (StringUtils.isEmpty(reference)) {
            return null;
        }
        Matcher m = REFERENCE_PATTERN.matcher(reference);
        if (!m.find()) {
            return null;
        }
        return StringUtils.trim(m.group(2));
    }

}
