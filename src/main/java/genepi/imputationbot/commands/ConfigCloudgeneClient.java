package genepi.imputationbot.commands;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;

public class ConfigCloudgeneClient extends BaseCommand {

	private static Scanner scanner = new Scanner(System.in);

	public static final String DEFAULT_HOSTNAME = "https://imputationserver.sph.umich.edu";

	public ConfigCloudgeneClient(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}

	@Override
	public int runAndHandleErrors() throws Exception {

		System.out.print("Imputationserver Url [" + DEFAULT_HOSTNAME + "]: ");
		String hostname = scanner.nextLine();
		if (hostname.isEmpty()) {
			hostname = DEFAULT_HOSTNAME;
		}

		// remove trailing slashes
		hostname = hostname.replaceFirst("/*$", "");

		System.out.print("API Token [None]: ");
		String token = scanner.nextLine();
		if (token.isEmpty()) {
			error("Please enter a API token. Learn more about the token on https://imputationserver.readthedocs.io/en/latest/api/");
			return 1;
		}

		CloudgeneClientConfig config = new CloudgeneClientConfig();
		config.setHostname(hostname);
		config.setToken(token);

		CloudgeneClient client = new CloudgeneClient(config);
		JSONObject server = client.getServerDetails();
		JSONArray apps = server.getJSONArray("apps");
		if (apps.length() == 0) {
			error("No application found on '" + hostname + "'");
			return 1;
		} else if (apps.length() == 1) {
			JSONObject app = apps.getJSONObject(0);
			info("Use application '" + app.getString("name") + "' as default application for new jobs.");
			config.setApp(app.getString("id"));
		} else {
			info("More than one application found on '" + hostname + "'.");
			info("Please select a default application:");
			for (int i = 0; i < apps.length(); i++) {
				JSONObject app = apps.getJSONObject(i);
				info("  [" + (i + 1) + "] " + app.get("name"));
			}
			System.out.print("Choice: ");
			String choice = scanner.nextLine();
			try {
				int choiceIndex = Integer.parseInt(choice);
				if (choiceIndex > 0 && choiceIndex <= apps.length()) {
					JSONObject app2 = apps.getJSONObject(choiceIndex - 1);
					info("Use application '" + app2.getString("name") + "' as default application for new jobs.");
					config.setApp(app2.getString("id"));
				} else {
					error("Wrong choiche. Please enter a value between 1 to " + apps.length());
					return 1;
				}
			} catch (Exception e) {
				error("Wrong choiche. Please enter a value between 1 to " + apps.length());
				return 1;
			}
		}

		writeConfig(config);

		printlnInGreen("Config file updated. Imputation Bot is ready to submit jobs 👍");
		System.out.println();
		return 0;

	}

}
