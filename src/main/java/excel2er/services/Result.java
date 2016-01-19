package excel2er.services;

public class Result {
    private int importedElementsCount = 0;
	private StringBuilder sb = new StringBuilder();
	private boolean errorOccured = false;
	
    public void inclementImportedElementsCount() {
        importedElementsCount++;
	}
	
	public void appendMessage(String message){
		sb.append(message);
	}
	
	public String getMessage(){
		return sb.toString();
	}
	
    public int getImportedElementsCount() {
        return importedElementsCount;
	}
	
	public void setErrorOccured(boolean value){
		errorOccured = value;
	}
	
	public boolean isErrorOccured(){
		return errorOccured;
	}
}