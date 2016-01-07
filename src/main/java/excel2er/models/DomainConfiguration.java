package excel2er.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import excel2er.Messages;
import excel2er.exceptions.ValidationError;

public class DomainConfiguration extends ConfigurationBase {

	private String sheetName;
	private String logicalCol;
	private String physicalCol;
	private String dataTypeCol;
    private String parentDomainCol;
	private String definitionCol;
	private String startRow;

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getLogicalCol() {
		return logicalCol;
	}

	public void setLogicalCol(String logicalCol) {
		this.logicalCol = logicalCol;
	}

	public String getPhysicalCol() {
		return physicalCol;
	}

	public void setPhysicalCol(String physicalCol) {
		this.physicalCol = physicalCol;
	}

	public String getDataTypeCol() {
		return dataTypeCol;
	}

	public void setDataTypeCol(String dataTypeCol) {
		this.dataTypeCol = dataTypeCol;
	}

    public String getParentDomainCol() {
        return parentDomainCol;
    }

    public void setParentDomainCol(String parentDomainCol) {
        this.parentDomainCol = parentDomainCol;
    }

	public String getDefinitionCol() {
		return definitionCol;
	}

	public void setDefinitionCol(String definitionCol) {
		this.definitionCol = definitionCol;
	}

	public String getStartRow() {
		return startRow;
	}

	public void setStartRow(String startRow) {
		this.startRow = startRow;
	}

	/**
	 * validate input value. this function check only row item.because reference
	 * of column use character or index.
	 * 
	 * @return errors
	 */
	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<ValidationError>();

		if (StringUtils.isEmpty(getInputFilePath())) {
			errors.add(new ValidationError(Messages
					.getMessage("error.inputfile_required")));
		}

		validRequired(errors, getDataTypeCol(), "explain_domain",
				"item_datatype");
		validateDigit(errors, getStartRow(), "explain_domain", "start_row");
		validRequired(errors, getLogicalCol(), "explain_domain", "item_logical_domain");

		return errors;
	}

}
