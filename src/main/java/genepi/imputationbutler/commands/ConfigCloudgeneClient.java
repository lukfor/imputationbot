package genepi.imputationbutler.commands;

import java.util.Scanner;

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
			printError(
					"Please enter a API token. Learn more about the token on https://imputationserver.readthedocs.io/en/latest/api/");
			return 1;
		}

		try {
			CloudgeneClientConfig config = new CloudgeneClientConfig();
			config.setHostname(hostname);
			config.setToken(token);
			writeConfig(config);
		} catch (Exception e) {
			printError("Config file could not be written. " + e);
			return 1;

		}

		printlnInGreen("Config file updated.");
		
		return 0;
	}

}
