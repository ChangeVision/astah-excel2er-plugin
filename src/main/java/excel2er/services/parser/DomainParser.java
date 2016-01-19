package excel2er.services.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Sheet;

import excel2er.models.Domain;
import excel2er.models.DomainConfiguration;

public class DomainParser {
	private static final int EXCEL_LIMIT_ROW = 65536;

	public List<Domain> parse(DomainConfiguration configuration, Sheet sheet) {
		int startRow = NumberUtils.toInt(configuration.getStartRow());

		List<Domain> domains = new ArrayList<Domain>();
		
		while (true) {

			String logicalName = ParserUtils.getCellValue(sheet, startRow,
					configuration.getLogicalCol());
			String physicalName = ParserUtils.getCellValue(sheet, startRow,
					configuration.getPhysicalCol());

			if (StringUtils.isEmpty(logicalName)
					|| startRow > EXCEL_LIMIT_ROW) {
				break;
			}

			Domain domain = new Domain();
			domain.setLogicalName(logicalName);
			domain.setPhysicalName(physicalName);

            parseAlias1(configuration, sheet, startRow, domain);
            parseAlias2(configuration, sheet, startRow, domain);
			parseDataType(configuration, sheet, startRow, domain);
            parseLengthAndPrecision(configuration, sheet, startRow, domain);
            parseNotNull(configuration, sheet, startRow, domain);
            parseParentDomain(configuration, sheet, startRow, domain);
			parseDefinition(configuration, sheet, startRow, domain);
			
			domains.add(domain);
			startRow++;
		}
		
		return domains;
	}

    private void parseParentDomain(DomainConfiguration configuration, Sheet sheet, int startRow,
            Domain domain) {
        String value = ParserUtils
                .getCellValue(sheet, startRow, configuration.getParentDomainCol());
        if (StringUtils.isNotEmpty(value)) {
            domain.setParentDomain(value);
        }
    }

	private void parseDefinition(DomainConfiguration configuration, Sheet sheet,
			int startRow, Domain domain) {
		String value = ParserUtils.getCellValue(sheet, startRow,
				configuration.getDefinitionCol());
		if (StringUtils.isNotEmpty(value)) {
			domain.setDefinition(value);
		}
	}

	void parseDataType(DomainConfiguration configuration, Sheet sheet, int startRow,
			Domain domain) {
		String value = ParserUtils.getCellValue(sheet, startRow,
				configuration.getDataTypeCol());
		if (StringUtils.isNotEmpty(value)) {
			domain.setDataType(value);
		}
	}

    private void parseAlias1(DomainConfiguration configuration, Sheet sheet, int startRow,
            Domain domain) {
        String value = ParserUtils.getCellValue(sheet, startRow, configuration.getAlias1Col());
        if (StringUtils.isNotEmpty(value)) {
            domain.setAlias1(value);
        }
    }

    private void parseAlias2(DomainConfiguration configuration, Sheet sheet, int startRow,
            Domain domain) {
        String value = ParserUtils.getCellValue(sheet, startRow, configuration.getAlias2Col());
        if (StringUtils.isNotEmpty(value)) {
            domain.setAlias2(value);
        }
    }

    private void parseLengthAndPrecision(DomainConfiguration configuration, Sheet sheet,
            int startRow, Domain domain) {
        String value = ParserUtils.getCellValue(sheet, startRow,
                configuration.getLengthAndPrecisionCol());
        if (StringUtils.isEmpty(value)) {
            return;
        }
        final String decimal_point_and_zero = ".0";
        if (value.endsWith(decimal_point_and_zero)) {
            value = value.substring(0, value.length() - decimal_point_and_zero.length());
        }
        if (StringUtils.isNotEmpty(value)) {
            domain.setLengthAndPrecision(value);
        }
    }

    private void parseNotNull(DomainConfiguration configuration, Sheet sheet, int startRow,
            Domain domain) {
        String value = ParserUtils.getCellValue(sheet, startRow, configuration.getNotNullCol());
        if (StringUtils.isNotEmpty(value)) {
            domain.setNotNull(value);
        }
    }
}
