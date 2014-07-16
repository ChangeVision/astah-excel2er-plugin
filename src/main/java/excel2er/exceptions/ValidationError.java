package excel2er.exceptions;

public class ValidationError {

	private String message;

	public ValidationError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
