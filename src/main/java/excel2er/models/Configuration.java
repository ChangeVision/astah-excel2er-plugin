package excel2er.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import excel2er.Messages;
import excel2er.exceptions.ValidationError;

public class Configuration {

	private String inputFilePath;
	private boolean useSheetName = false;
	private boolean advanceSetting = false;
	private String entityLogicalRow;
	private String entityLogicalCol;
	private String entityPhysicalRow;
	private String attributeLogicalCol;
	private String entityPhysicalCol;
	private String AttributePhysicalCol;
	private String primaryKeyCol;
	private String notNullCol;
	private String defaultValueCol;
	private String dataTypeCol;
	private String lengthCol;
	private String definitionCol;
	private String startRow;

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

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
		return AttributePhysicalCol;
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
	}

	public void setAdvanceSetting(boolean advanceSetting) {
		this.advanceSetting = advanceSetting;
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
		this.AttributePhysicalCol = physicalCol;
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
	 * validate input value.
	 * this function check only row item.because reference of column use character or index.
	 * @return errors
	 */
	public List<ValidationError> validate(){
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		if(!useSheetName && !advanceSetting){
			throw new IllegalArgumentException("useSheetName or advanceSetting must true");
		}
		
		//for entity
		validateDigit(errors,getEntityLogicalRow(),"explain_entity","entity.logicalname.row");
		validateDigit(errors,getEntityPhysicalRow(),"explain_entity","entity.physicalname.row");
		
		//for attribute
		validateDigit(errors,getStartRow(),"explain_attribute","start_row");
		
		return errors;
	}
	
	
	private void validateDigit(List<ValidationError> errors, String value, String key, String subkey) {
		if(!StringUtils.isEmpty(value) && !NumberUtils.isDigits(value)){
			String message =  Messages.getMessage("error.not.digit", Messages.getMessage(key) + " - " + Messages.getMessage(subkey));
			errors.add(new ValidationError(message));
		}
	}

}
