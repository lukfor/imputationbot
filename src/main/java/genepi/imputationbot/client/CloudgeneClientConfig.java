package genepi.imputationbot.client;

public class CloudgeneClientConfig {

	private String hostname;

	private String token;

	private String app;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getApp() {
		return app;
	}

}
