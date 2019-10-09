package genepi.imputationbot.commands;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.util.ComandlineOptionsUtil;

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

		CloudgeneClient client = getClient();
		JSONObject app = client.getDefaultApp();

		JSONArray params = app.getJSONArray("params");
		
		ComandlineOptionsUtil.printDetails(params);
		println();
		
		return 0;
	}

}
