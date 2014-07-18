package excel2er.models;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import excel2er.Messages;
import excel2er.exceptions.ValidationError;

public abstract class ConfigurationBase {

	private String inputFilePath;
	
	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public String getInputFilePath() {
		return inputFilePath;
	}
	
	public void validRequired(List<ValidationError> errors, String value,
			String key, String subkey) {

		if (StringUtils.isEmpty(value)) {
			String message = Messages.getMessage(
					"error.required",
					Messages.getMessage(key) + " - "
							+ Messages.getMessage(subkey));
			errors.add(new ValidationError(message));
		}
	}

	public void validateDigit(List<ValidationError> errors, String value,
			String key, String subkey) {

		validRequired(errors, value, key, subkey);

		if (!StringUtils.isEmpty(value) && !NumberUtils.isDigits(value)) {
			String message = Messages.getMessage(
					"error.not.digit",
					Messages.getMessage(key) + " - "
							+ Messages.getMessage(subkey));
			errors.add(new ValidationError(message));
		}
	}

	public abstract List<ValidationError> validate();
}
