package excel2er.services.parser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Configuration;

public class ParserUtils {

	private static final int POI_OFFSET_START_INDEX = 1;
	private static FormulaEvaluator formulaEvaluator;

	public static boolean isEmptyBoth(String one, String other) {
		return StringUtils.isEmpty(one) && StringUtils.isEmpty(other);
	}

	public static Workbook getWorkbook(Configuration configuration) {
		String inputFilePath = configuration.getInputFilePath();
		try {
			Workbook workbook = WorkbookFactory.create(new File(inputFilePath));
			formulaEvaluator = workbook.getCreationHelper()
					.createFormulaEvaluator();
			return workbook;
		} catch (POIXMLException e) {
			throw new ApplicationException(Messages.getMessage(
					"error.poi.exception",
					ExceptionUtils.getRootCauseMessage(e)), e);
		} catch (InvalidFormatException e) {
			throw new ApplicationException(e);
		} catch (IOException e) {
			throw new ApplicationException(
					Messages.getMessage("error.file_notfound"));
		}
	}

	public static String getCellValue(Sheet sheet, int refRow, String refCol) {
		if(refRow < 0 || refCol == null)
			return null;
		
		Row row = sheet.getRow(refRow - POI_OFFSET_START_INDEX);
		if(row == null){
			return null;
		}
		
		Cell cell = null;
		if (NumberUtils.isDigits(refCol)) {
			cell = row.getCell(NumberUtils.toInt(refCol)
					- POI_OFFSET_START_INDEX);
		} else {
			CellReference ref = new CellReference(refCol);
			if(ref != null){
				cell = row.getCell(ref.getCol());
			}
		}

		if (cell == null) {
			return null;
		}

		try {
			Object ret = getCellValue(cell);
			if (ret != null) {
				return ret.toString();
			}
			return null;
		} catch (Exception e) {
			// ignore.
			return null;
		}
	}

	private static Object getCellValue(Cell cell) {
		Object value = null;
		CellValue cellValue = formulaEvaluator.evaluate(cell);
		switch (cellValue.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
		case Cell.CELL_TYPE_ERROR:
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			value = cell.getNumericCellValue();
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			// CELL_TYPE_FORMULA will never happen
			break;
		default:
			break;
		}

		return value;
	}
}
