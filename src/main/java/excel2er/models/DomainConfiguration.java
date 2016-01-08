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
    private String alias1Col;
    private String alias2Col;
	private String dataTypeCol;
    private String lengthAndPrecisionCol;
    private String notNullCol;
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

    public String getAlias1Col() {
        return alias1Col;
    }

    public void setAlias1Col(String alias1Col) {
        this.alias1Col = alias1Col;
    }

    public String getAlias2Col() {
        return alias2Col;
    }

    public void setAlias2Col(String alias2Col) {
        this.alias2Col = alias2Col;
    }

	public String getDataTypeCol() {
		return dataTypeCol;
	}

	public void setDataTypeCol(String dataTypeCol) {
		this.dataTypeCol = dataTypeCol;
	}

    public String getLengthAndPrecisionCol() {
        return lengthAndPrecisionCol;
    }

    public void setLengthAndPrecisionCol(String lengthAndPrecisionCol) {
        this.lengthAndPrecisionCol = lengthAndPrecisionCol;
    }

    public String getNotNullCol() {
        return notNullCol;
    }

    public void setNotNullCol(String notNullCol) {
        this.notNullCol = notNullCol;
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
