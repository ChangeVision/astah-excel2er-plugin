package excel2er.models;

public class Attribute {

	private String logicalName;
	private boolean isPrimaryKey;
	private String physicalName;
	private boolean isNotNull;
	private String dataType;
	private String length;
	private String defaultValue;
	private String definition;

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

}
