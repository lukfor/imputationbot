package genepi.imputationbot.commands;

import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;

public class ShowVersion extends BaseCommand {

	public ShowVersion(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		CloudgeneClientConfig config = readConfig();
		CloudgeneClient client = new CloudgeneClient(config);
		JSONObject app = client.getAppDetails(config.getApp());
		JSONObject user = client.getAuthUser();

		System.out.println("Application: ");
		System.out.println("  Name: " + app.get("name"));
		System.out.println("  Version: " + app.get("version"));
		System.out.println("Hostname: " + config.getHostname());
		if (user.has("mail")) {
			System.out.println("Username: " + user.get("username") + " <" + user.get("mail") + ">");
		} else {
			System.out.println("Username: " + user.get("username"));
		}
		System.out.println();

		return 0;

	}

}
