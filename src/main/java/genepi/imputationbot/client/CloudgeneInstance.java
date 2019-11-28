package genepi.imputationbot.client;

import java.util.Vector;

import org.json.JSONObject;

public class CloudgeneInstance {

	private String hostname;

	private String token;

	private String name = null;

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

	public String getName() throws CloudgeneException {
		if (name == null) {
			CloudgeneClient client = new CloudgeneClient(new Vector<CloudgeneInstance>());
			JSONObject server = client.getServerDetails(this);
			name = server.getString("name");
		}
		return name;
	}

}
