package excel2er.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import excel2er.Messages;
import excel2er.exceptions.ValidationError;

public class Configuration extends ConfigurationBase{

	private boolean useSheetName = false;
	private boolean advanceSetting = false;
	private String entityLogicalRow;
	private String entityLogicalCol;
	private String entityPhysicalRow;
	private String attributeLogicalCol;
	private String entityPhysicalCol;
	private String attributePhysicalCol;
	private String primaryKeyCol;
	private String notNullCol;
	private String defaultValueCol;
	private String dataTypeCol;
	private String lengthCol;
	private String definitionCol;
	private String startRow;

	public boolean isUseSheetName() {
		return useSheetName;
	}

	public boolean isAdvanceSetting() {
		return advanceSetting;
	}

	public String getEntityLogicalRow() {
		return entityLogicalRow;
	}

	public String getEntityLogicalCol() {
		return entityLogicalCol;
	}

	public String getEntityPhysicalRow() {
		return entityPhysicalRow;
	}

	public String getAttributeLogicalCol() {
		return attributeLogicalCol;
	}

	public String getEntityPhysicalCol() {
		return entityPhysicalCol;
	}

	public String getAttributePhysicalCol() {
		return attributePhysicalCol;
	}

	public String getPrimaryKeyCol() {
		return primaryKeyCol;
	}

	public String getNotNullCol() {
		return notNullCol;
	}

	public String getDefaultValueCol() {
		return defaultValueCol;
	}

	public String getDataTypeCol() {
		return dataTypeCol;
	}

	public String getLengthCol() {
		return lengthCol;
	}

	public String getDefinitionCol() {
		return definitionCol;
	}

	public void setUseSheetName(boolean useSheetName) {
		this.useSheetName = useSheetName;
		if(useSheetName)
			advanceSetting = false;
	}

	public void setAdvanceSetting(boolean advanceSetting) {
		this.advanceSetting = advanceSetting;
		if(advanceSetting)
			useSheetName = false;
	}

	public void setEntityLogicalRow(String logicalRow) {
		this.entityLogicalRow = logicalRow;
	}

	public void setEntityLogicalCol(String logicalCol) {
		this.entityLogicalCol = logicalCol;
	}

	public void setEntityPhysicalRow(String physicalRow) {
		this.entityPhysicalRow = physicalRow;
	}

	public void setEntityPhysicalCol(String physicalCol) {
		this.entityPhysicalCol = physicalCol;
	}

	public void setAttributeLogicalCol(String logicalCol) {
		this.attributeLogicalCol = logicalCol;
	}

	public void setAttributePhysicalCol(String physicalCol) {
		this.attributePhysicalCol = physicalCol;
	}

	public void setPrimaryKeyCol(String primaryKeyCol) {
		this.primaryKeyCol = primaryKeyCol;
	}

	public void setNotNullCol(String notNullCol) {
		this.notNullCol = notNullCol;
	}

	public void setDefaultValueCol(String defaultValueCol) {
		this.defaultValueCol = defaultValueCol;
	}

	public void setDataTypeCol(String dataTypeCol) {
		this.dataTypeCol = dataTypeCol;
	}

	public void setLengthCol(String lengthCol) {
		this.lengthCol = lengthCol;
	}

	public void setDefinitionCol(String definitionCol) {
		this.definitionCol = definitionCol;
	}

	public void setStartRow(String startRow) {
		this.startRow = startRow;
	}

	public String getStartRow() {
		return startRow;
	}

	/**
	 * validate input value. this function check only row item.because reference
	 * of column use character or index.
	 * 
	 * @return errors
	 */
	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<ValidationError>();

		if(StringUtils.isEmpty(getInputFilePath())){
			errors.add(new ValidationError(Messages.getMessage(
					"error.inputfile_required")));
		}
		
		if (!useSheetName && !advanceSetting) {
			errors.add(new ValidationError(Messages.getMessage("error.usesheetname_or_advancesetting")));
		}

		if(advanceSetting){
			validateDigit(errors, getEntityLogicalRow(), "explain_entity",
					"entity.logicalname.row");
			validRequired(errors, getEntityLogicalCol(), "explain_entity",
					"entity.logicalname.col");
			validateDigit(errors, getEntityPhysicalRow(), "explain_entity",
					"entity.physicalname.row");
			validRequired(errors, getEntityPhysicalCol(), "explain_entity",
					"entity.physicalname.col");
		}

		// for attribute
		validRequired(errors, getDataTypeCol() , "explain_attribute", "item_datatype");
		validateDigit(errors, getStartRow(), "explain_attribute", "start_row");

		return errors;
	}

}
