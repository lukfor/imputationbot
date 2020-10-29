package genepi.imputationbot.commands;

import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.client.CloudgeneInstanceList;
import genepi.imputationbot.util.Console;

public class RemoveInstance extends BaseCommand {

	public static final String HELP = "A list of all instances including the ID can be obtained with 'imputationbot instances'.";

	public RemoveInstance(String... args) {
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

		CloudgeneInstanceList instances = getInstanceList();

		if (instances.isEmpty()) {
			error("No instances found. Please run 'imputationbot add-instance' and enter your API Token");
			return 1;
		}

		int id = -1;

		String[] ids = getRemainingArgs();

		if (ids.length < 1) {

			// list instances
			id = Console.select("Select Instance: ", instances.getAll().toArray());

		} else {
			try {
				id = Integer.parseInt(ids[0]);
			} catch (NumberFormatException e) {
				error("Unknown instance '" + ids[0] + "'. " + HELP);
				return 1;
			}

		}

		if (id >= 1 && id <= instances.getAll().size()) {
			CloudgeneInstance instance = instances.getById(id);
			getInstanceList().remove(instance);
			saveInstanceList();
			printlnInGreen(instance.getName() + " (" + instance.getHostname() + ") removed.");
			println();
			return 0;

		} else {
			error("Unknown instance '" + id + "'. " + HELP);
			return 1;
		}

	}

}
