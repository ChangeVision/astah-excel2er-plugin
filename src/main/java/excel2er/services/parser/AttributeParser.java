package excel2er.services.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Sheet;

import excel2er.models.Attribute;
import excel2er.models.Configuration;
import excel2er.models.Entity;

public class AttributeParser {
	private static final int EXCEL_LIMIT_ROW = 65536;

	public void parse(Configuration configuration, Sheet sheet, Entity entity) {
		int startRow = NumberUtils.toInt(configuration.getStartRow());

		while (true) {

			String logicalName = ParserUtils.getCellValue(sheet, startRow,
					configuration.getAttributeLogicalCol());
			String physicalName = ParserUtils.getCellValue(sheet, startRow,
					configuration.getAttributePhysicalCol());

			if (ParserUtils.isEmptyBoth(logicalName, physicalName)
					|| startRow > EXCEL_LIMIT_ROW) {
				break;
			}

			Attribute attr = new Attribute();
			attr.setLogicalName(logicalName);
			attr.setPhysicalName(physicalName);

			parsePrimaryKey(configuration, sheet, startRow, attr);

			parseNotNull(configuration, sheet, startRow, attr);

			parseDataType(configuration, sheet, startRow, attr);

			parseLength(configuration, sheet, startRow, attr);

			parseDefaultValue(configuration, sheet, startRow, attr);

			parseDefinition(configuration, sheet, startRow, attr);

			entity.addAttribute(attr);
			startRow++;
		}
		;
	}

	private void parseDefinition(Configuration configuration, Sheet sheet,
			int startRow, Attribute attr) {
		String value = ParserUtils.getCellValue(sheet, startRow,
				configuration.getDefinitionCol());
		if (StringUtils.isNotEmpty(value)) {
			attr.setDefinition(value);
		}

	}

	private void parseDefaultValue(Configuration configuration, Sheet sheet,
			int startRow, Attribute attr) {
		String value = ParserUtils.getCellValue(sheet, startRow,
				configuration.getDefaultValueCol());
		if (StringUtils.isNotEmpty(value)) {
			attr.setDefaultValue(value);
		}
	}

	private void parseLength(Configuration configuration, Sheet sheet,
			int startRow, Attribute attr) {
		String value = ParserUtils.getCellValue(sheet, startRow,
				configuration.getLengthCol());
		if (StringUtils.isNotEmpty(value)) {
			attr.setLength(value);
		}
	}

	void parseDataType(Configuration configuration, Sheet sheet, int startRow,
			Attribute attr) {
		String value = ParserUtils.getCellValue(sheet, startRow,
				configuration.getDataTypeCol());
		if (StringUtils.isNotEmpty(value)) {
			attr.setDataType(value);
		}
	}

	void parseNotNull(Configuration configuration, Sheet sheet, int startRow,
			Attribute attr) {
		String value = ParserUtils.getCellValue(sheet, startRow,
				configuration.getNotNullCol());
		if (StringUtils.isNotEmpty(value)) {
			attr.setNotNull(true);
		}
	}

	void parsePrimaryKey(Configuration configuration, Sheet sheet,
			int startRow, Attribute attr) {
		String value = ParserUtils.getCellValue(sheet, startRow,
				configuration.getPrimaryKeyCol());
		if (StringUtils.isNotEmpty(value)) {
			attr.setPrimaryKey(true);
		}
	}

}
