package genepi.imputationbot.client;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class CloudgeneUser {

	private final JSONObject user;

	public CloudgeneUser(JSONObject user) {
		this.user = user;
	}

	public String getFullName() {
        // Parsed from /users/{username}/profile
        if (user.has("fullName")) {
            String fullName = user.getString("fullName");
            if (!fullName.isEmpty()) {
                return fullName;
            }
        }

        // Parsed from /server
        if (user.has("name")) {
            String name = user.getString("name");
            if (!name.isEmpty()) {
                return name;
            }
        }

        return "Mr. Shy";
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
