package genepi.imputationbutler.commands;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbutler.client.CloudgeneClient;
import genepi.imputationbutler.client.CloudgeneClientConfig;

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
	public int run() {

		System.out.print("Imputationserver Url [" + DEFAULT_HOSTNAME + "]: ");
		String hostname = scanner.nextLine();
		if (hostname.isEmpty()) {
			hostname = DEFAULT_HOSTNAME;
		}

		System.out.print("API Token [None]: ");
		String token = scanner.nextLine();
		if (token.isEmpty()) {
			error("Please enter a API token. Learn more about the token on https://imputationserver.readthedocs.io/en/latest/api/");
			return 1;
		}

		try {
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
				error("More than one application found on '" + hostname + "'");
				return 1;
			}

			writeConfig(config);
		} catch (Exception e) {
			error("Config file could not be written. " + e);
			return 1;

		}

		// TODO: load https://imputationserver.sph.umich.edu/api/v2/server
		// TODO: if apps > 1 ask user --> use app[0] name as default.

		printlnInGreen("Config file updated.");

		return 0;
	}

}
