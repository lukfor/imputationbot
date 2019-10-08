package genepi.imputationbot.commands;

import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;
import genepi.imputationbot.client.CloudgeneJob;

public class DownloadResults extends BaseCommand {

	public DownloadResults(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addParameter("job", "job id");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public int run() {

		String id = getValue("job").toString();

		try {
			CloudgeneClientConfig config = readConfig();
			CloudgeneClient client = new CloudgeneClient(config);

			CloudgeneJob cloudgeneJob = client.getJobDetails(id);

			cloudgeneJob.downloadAll(client);

			return 0;

		} catch (Exception e) {

			error(e.toString());

			return 1;

		}
	}

}
