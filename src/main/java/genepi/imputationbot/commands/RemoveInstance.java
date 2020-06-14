package genepi.imputationbot.commands;

import java.util.List;
import java.util.Vector;

import genepi.imputationbot.client.CloudgeneInstance;

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

		List<CloudgeneInstance> instances = new Vector<CloudgeneInstance>(getInstanceList().getAll());

		String[] ids = getRemainingArgs();

		if (ids.length < 1) {
			error("Please specify a instance ID. " + HELP);
			return -1;
		}

		int id = -1;
		try {
			id = Integer.parseInt(ids[0]);
		} catch (NumberFormatException e) {
			error("Unknown instance '" + ids[0] + "'. " + HELP);
			return -1;
		}

		if (id >= 1 && id <= instances.size()) {
			CloudgeneInstance instance = instances.get(id - 1);
			getInstanceList().remove(instance);
			saveInstanceList();
			printlnInGreen(instance.getName() + " (" + instance.getHostname() + ") removed.");
			println();
			return 0;

		} else {
			error("Unknown instance '" + id + "'. " + HELP);
			return -1;
		}

	}

}
