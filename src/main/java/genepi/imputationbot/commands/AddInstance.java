package genepi.imputationbot.commands;

import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneApiToken;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneException;
import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.client.CloudgeneInstanceList;
import genepi.imputationbot.client.CloudgeneUser;

public class AddInstance extends BaseCommand {

	public static final String DEFAULT_HOSTNAME = "https://imputationserver.sph.umich.edu";

	public AddInstance(String[] args) {
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

		CloudgeneInstance instance = new CloudgeneInstance();
		instance.setHostname(hostname);
		instance.setToken(token);

		CloudgeneInstanceList instances = getInstanceList(false);
		CloudgeneClient client = getClient();

		// verify token
		try {
			CloudgeneApiToken apiToken = client.verifyToken(instance, token);
			if (!apiToken.isValid()) {
				throw new CloudgeneException(100, apiToken.toString());
			}

		} catch (CloudgeneException e) {
			if (e.getCode() == 404) {
				throw new CloudgeneException(e.getCode(),
						"Token could not be verified. Are you sure Imputationserver is running on '"
								+ instance.getHostname() + "'?");
			} else {
				throw e;
			}
		}

		// test api token by getting user profile
		CloudgeneUser user = client.getAuthUser(instance);
		println();
		println("Hi " + user.getFullName() + " 👋");
		println();

		JSONObject defaultApp = client.getDefaultApp(instance);
		instances.add(instance);	
		saveInstanceList();

		println();
		println();
		printlnInGreen("Imputation Bot is ready to submit jobs to "+instance.getHostname() + " (" + defaultApp.getString("name") + ") "
				+ defaultApp.getString("version") + ") 👍");
		println();
		return 0;

	}

}
