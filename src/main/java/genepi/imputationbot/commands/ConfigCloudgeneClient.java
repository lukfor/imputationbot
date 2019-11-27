package genepi.imputationbot.commands;

import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneApiToken;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;
import genepi.imputationbot.client.CloudgeneException;

public class ConfigCloudgeneClient extends BaseCommand {

	public static final String DEFAULT_HOSTNAME = "https://imputationserver.sph.umich.edu";

	public ConfigCloudgeneClient(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {

	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		String hostname = read("Imputationserver Url", DEFAULT_HOSTNAME);
		String token = read("API Token");

		// remove trailing slashes
		hostname = hostname.replaceFirst("/*$", "");

		if (token.isEmpty()) {
			error("Please enter a API token. Learn more about the token on https://imputationserver.readthedocs.io/en/latest/api/");
			return 1;
		}

		CloudgeneClientConfig config = new CloudgeneClientConfig();
		config.setHostname(hostname);
		config.setToken(token);

		CloudgeneClient client = new CloudgeneClient(config);

		// verify token
		try {
			CloudgeneApiToken apiToken = client.verifyToken(token);
			if (!apiToken.isValid()) {
				throw new CloudgeneException(100, apiToken.toString());
			}

		} catch (CloudgeneException e) {
			if (e.getCode() == 404) {
				throw new CloudgeneException(e.getCode(),
						"Token could not be verified. Are you sure Imputationserver is running on '"
								+ config.getHostname() + "'?");
			} else {
				throw e;
			}
		}

		// test api token by getting user profile
		JSONObject user = client.getAuthUser();
		println();
		String name = user.getString("fullName").isEmpty() ? "Mr. Shy" : user.getString("fullName");
		println("Hi " + name + " 👋");
		println();

		JSONObject server = client.getServerDetails();
		JSONArray apps = server.getJSONArray("apps");
		JSONObject defaultApp = null;

		if (apps.length() == 0) {

			error("No application found on '" + hostname + "'");
			return 1;

		} else if (apps.length() == 1) {

			defaultApp = apps.getJSONObject(0);

		} else {

			println("More than one application found on '" + hostname + "'.");
			println("Please select a default application:");
			for (int i = 0; i < apps.length(); i++) {
				JSONObject app = apps.getJSONObject(i);
				println("  [" + (i + 1) + "] " + app.get("name"));
			}

			String choice = read("Choice");
			try {
				int choiceIndex = Integer.parseInt(choice);
				if (choiceIndex <= 0 || choiceIndex > apps.length()) {
					error("Wrong choice. Please enter a value between 1 to " + apps.length());
					return 1;
				}
				defaultApp = apps.getJSONObject(choiceIndex - 1);
			} catch (Exception e) {
				error("Wrong choice. Please enter a value between 1 to " + apps.length());
				return 1;
			}

		}

		config.setApp(defaultApp.getString("id"));
		writeConfig(config);

		println();
		println();
		printlnInGreen("Imputation Bot is ready to submit jobs to '" + defaultApp.getString("name") + "' 👍");
		println();
		return 0;

	}

}
