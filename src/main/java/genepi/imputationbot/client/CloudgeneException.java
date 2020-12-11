package genepi.imputationbot.client;

import org.apache.http.conn.HttpHostConnectException;

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

	public CloudgeneException(int code, Exception exception) {
		this.code = code;
		if (exception instanceof HttpHostConnectException) {
			this.message = "Server is not responding (unavailable).";
		} else {
			this.message = exception.toString();
		}
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
