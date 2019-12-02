package genepi.imputationbot.client;

import org.json.JSONObject;

public class CloudgeneUser {

	private JSONObject user;

	public CloudgeneUser(JSONObject user) {
		this.user = user;
	}

	public String getFullName() {
		return user.getString("fullName").isEmpty() ? "Mr. Shy" : user.getString("fullName");
	}

	public String getMail() {
		if (user.has("mail")) {
			return user.getString("mail");
		} else {
			return null;
		}
	}
	
	public String getUsername() {
		return user.getString("username");
	}

}
