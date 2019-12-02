package genepi.imputationbot.commands;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.util.CommandlineOptionsUtil;

public class ListRefPanels extends BaseCommand {

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ListRefPanels(String[] args) {
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

		for (CloudgeneInstance instance : getInstanceList().getAll()) {

			instance.printReferencePanels();
			println();

		}

		return 0;
	}

}
