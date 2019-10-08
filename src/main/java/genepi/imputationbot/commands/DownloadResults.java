package genepi.imputationbot.commands;

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
	public int runAndHandleErrors() throws Exception {

		String id = getValue("job").toString();

		CloudgeneClientConfig config = readConfig();
		CloudgeneClient client = new CloudgeneClient(config);

		CloudgeneJob job = client.getJobDetails(id);

		if (job.isRunning()) {
			println("Job is running....");
			client.waitForJob(job.getId());
			job = client.getJobDetails(job.getId());

			println("Job completed. State: " + job.getJobStateAsText());
			println();
			println();
		}

		job.downloadAll(client);

		return 0;

	}

}
