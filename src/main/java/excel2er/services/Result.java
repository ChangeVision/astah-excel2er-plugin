package excel2er.services;

public class Result {
	private int createdEntitiesCount = 0;
	private StringBuilder sb = new StringBuilder();
	private boolean errorOccured = false;
	
	public void inclementEntitesCount(){
		createdEntitiesCount++;
	}
	
	public void appendMessage(String message){
		sb.append(message);
	}
	
	public String getMessage(){
		return sb.toString();
	}
	
	public int getCreatedEntitiesCount(){
		return createdEntitiesCount;
	}
	
	public void setErrorOccured(boolean value){
		errorOccured = value;
	}
	
	public boolean isErrorOccured(){
		return errorOccured;
	}
}