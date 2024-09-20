package genepi.imputationbot.commands;

import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneApiToken;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneException;
import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.client.CloudgeneInstanceList;
import genepi.imputationbot.client.CloudgeneUser;
import genepi.imputationbot.util.Console;
import genepi.imputationbot.util.Emoji;

public class UpdateInstance extends BaseCommand {

	public static final String DEFAULT_HOSTNAME = "https://imputationserver.sph.umich.edu";

	public static final String HELP = "A list of all instances including the ID can be obtained with 'imputationbot instances'.";

	private String[] args;

	public UpdateInstance(String... args) {
		super(args);
		this.args = args;
	}

	@Override
	public void createParameters() {

	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		CloudgeneInstanceList instances = getInstanceList();

		if (instances.isEmpty()) {
			error("No instances found. Please run 'imputationbot add-instance' and enter your API Token");
			return 1;
		}

		int id = -1;
		if (args.length < 1) {
			// list instances
			if (instances.getAll().size() == 1) {
				println("Instance: " + instances.getAll().toArray()[0]);
				id = 1;
			} else {
				id = Console.select("Select Instance: ", instances.getAll().toArray());
			}

		} else {
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				error("Unknown instance '" + args[0] + "'. " + HELP);
				return 1;
			}

		}

		if (id < 1 || id > instances.getAll().size()) {
			error("Unknown instance '" + id + "'. " + HELP);
			return 1;
		}

		String token = "";
		if (args.length > 1) {
			token = args[1];
		} else {
			token = read("API Token");
		}

		if (token == null || token.trim().isEmpty()) {
			error("Please enter API token. Learn more about the token on https://imputationserver.readthedocs.io/en/latest/api/");
			return 1;
		}

		CloudgeneClient client = getClient(false);

		CloudgeneInstance oldInstance = instances.getById(id);
		instances.remove(oldInstance);

		CloudgeneInstance instance = new CloudgeneInstance();
		instance.setHostname(oldInstance.getHostname());
		instance.setToken(token);

		// verify token
		try {
			System.out.println("Token ---> " + token);
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
		println("Hi " + user.getFullName() + " " + Emoji.WAVING_HAND);
		println();

		JSONObject defaultApp = client.getDefaultApp(instance);
		instances.add(instance);
		saveInstanceList();

		println();
		println();
		printlnInGreen("Imputation Bot is ready to submit jobs to " + instance.getHostname() + " ("
				+ defaultApp.getString("name") + ") " + defaultApp.getString("version") + ") " + Emoji.THUMBS_UP);
		println();
		return 0;

	}

}
