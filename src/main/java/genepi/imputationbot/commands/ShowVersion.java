package genepi.imputationbot.commands;

import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneClient;

public class ShowVersion extends BaseCommand {

	public ShowVersion(String[] args) {
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

		CloudgeneClient client = getClient();

		JSONObject app = client.getDefaultApp();
		JSONObject user = client.getAuthUser();

		println("Application: ");
		println("  Name: " + app.get("name"));
		println("  Version: " + app.get("version"));
		println("Hostname: " + getConfig().getHostname());
		if (user.has("mail")) {
			println("Username: " + user.get("username") + " <" + user.get("mail") + ">");
		} else {
			println("Username: " + user.get("username"));
		}
		println();

		return 0;

	}

}
