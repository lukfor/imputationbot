package genepi.imputationbot.client;

public class CloudgeneException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message; 
	
	public CloudgeneException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {	
		return message;
	}
	
}
