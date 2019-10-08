package genepi.imputationbot.client;

public class CloudgeneException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;

	private int code;

	public CloudgeneException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return code + " - " + message;
	}

	public int getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return code + " - " + message;
	}

}
