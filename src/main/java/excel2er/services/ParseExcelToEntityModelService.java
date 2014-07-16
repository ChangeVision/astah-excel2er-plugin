package excel2er.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import excel2er.models.Configuration;
import excel2er.models.Entity;
import excel2er.services.parser.AttributeParser;
import excel2er.services.parser.EntityParser;
import excel2er.services.parser.ParserUtils;

public class ParseExcelToEntityModelService {

	public List<Entity> parse(Configuration configuration) {
		Workbook wb = ParserUtils.getWorkbook(configuration);

		AttributeParser attrParser = new AttributeParser();

		List<Entity> entities = new ArrayList<Entity>();

		int allSheetCount = wb.getNumberOfSheets();
		for (int i = 0; i < allSheetCount; i++) {
			Sheet sheet = wb.getSheetAt(i);

			EntityParser entityParser = new EntityParser();
			Entity entity = entityParser.parse(configuration, sheet);

			if (ParserUtils.isEmptyBoth(entity.getEntityLogicalName(),
					entity.getEntityPhysicalName())) {
				continue;
			}

			attrParser.parse(configuration, sheet, entity);

			entities.add(entity);
		}

		return entities;
	}

}
