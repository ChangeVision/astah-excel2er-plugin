package excel2er.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import excel2er.Messages;
import excel2er.exceptions.ApplicationException;
import excel2er.models.Domain;
import excel2er.models.DomainConfiguration;
import excel2er.services.parser.DomainParser;
import excel2er.services.parser.ParserUtils;

public class ParseExcelToDomainModelService {

	public List<Domain> parse(DomainConfiguration configuration) {
		Workbook wb = ParserUtils.getWorkbook(configuration);

		Sheet sheet = null;
		try{
			if(!StringUtils.isEmpty(configuration.getSheetName())){
				sheet = wb.getSheet(configuration.getSheetName());
			}else{
				sheet = wb.getSheetAt(0);
			}
		}catch(Exception e){
			throw new ApplicationException(Messages.getMessage("error.not_found_sheet"));
		}
		
		if(sheet == null){
			throw new ApplicationException(Messages.getMessage("error.not_found_sheet"));
		}
		
		DomainParser domainParser = new DomainParser();
		List<Domain> domains = domainParser.parse(configuration, sheet);

		return domains;
	}

}
