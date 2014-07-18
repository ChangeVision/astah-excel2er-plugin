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

			parseDataType(configuration, sheet, startRow, domain);

			parseDefinition(configuration, sheet, startRow, domain);
			
			domains.add(domain);
			startRow++;
		}
		
		return domains;
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


}
