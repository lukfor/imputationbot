package genepi.imputationbot.commands;

import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneApiToken;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneInstance;

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

		for (CloudgeneInstance instance : getInstances().getInstances()) {

			CloudgeneClient client = getClient();

			JSONObject server = client.getServerDetails(instance);
			JSONObject app = client.getDefaultApp(instance);
			JSONObject user = client.getAuthUser(instance);

			println(server.getString("name") + ":");
			println("  Hostname: " + instance.getHostname());
			if (user.has("mail")) {
				println("  Username: " + user.get("username") + " <" + user.get("mail") + ">");
			} else {
				println("  Username: " + user.get("username"));
			}
			println("  Application: ");
			println("    Name: " + app.get("name"));
			println("    Version: " + app.get("version"));

			CloudgeneApiToken token = client.verifyToken(instance, instance.getToken());
			println("  " + token.toString());
			println();
		}
		return 0;

	}

}
