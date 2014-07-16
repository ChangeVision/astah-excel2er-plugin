package excel2er.services.parser;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Sheet;

import excel2er.models.Configuration;
import excel2er.models.Entity;

public class EntityParser {

	public Entity parse(Configuration configuration, Sheet sheet) {

		Entity entity = new Entity();
		if (configuration.isUseSheetName()) {
			entity.setEntityLogicalName(sheet.getSheetName());
		} else {
			entity.setEntityLogicalName(ParserUtils.getCellValue(sheet,
					toInt(configuration.getEntityLogicalRow()),
					configuration.getEntityLogicalCol()));
			entity.setEntityPhysicalName(ParserUtils.getCellValue(sheet,
					toInt(configuration.getEntityPhysicalRow()),
					configuration.getEntityPhysicalCol()));
		}
		return entity;
	}

	private int toInt(String value) {
		return NumberUtils.toInt(value);
	}
}
