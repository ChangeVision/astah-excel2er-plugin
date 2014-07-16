package excel2er.services;

import excel2er.models.Configuration;

public class ImportService {

	public void execute(Configuration configuration) {
		ParseExcelToEntityModelService parseService = new ParseExcelToEntityModelService();
		parseService.parse(configuration);
	}

	
}
