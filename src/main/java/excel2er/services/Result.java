package excel2er.services;

public class Result {
	private StringBuilder sb = new StringBuilder();
	private boolean errorOccured = false;
	
	public void appendMessage(String message){
		sb.append(message);
	}
	
	public String getMessage(){
		return sb.toString();
	}
	
	public void setErrorOccured(boolean value){
		errorOccured = value;
	}
	
	public boolean isErrorOccured(){
		return errorOccured;
	}
}