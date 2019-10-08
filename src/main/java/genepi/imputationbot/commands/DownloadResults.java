package genepi.imputationbot.commands;

import genepi.base.Tool;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;
import genepi.imputationbot.client.CloudgeneJob;

public class DownloadResults extends BaseCommand {

	public DownloadResults(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addOptionalParameter("password", "Use this password to encrypt files", Tool.STRING);
	}

	@Override
	public void init() {
	}

	@Override
	public int runAndHandleErrors() throws Exception {

		String[] jobIds = getRemainingArgs();

		if (jobIds.length == 0) {
			error("Please provide a job id.");
			return 1;
		}

		CloudgeneClientConfig config = readConfig();
		CloudgeneClient client = new CloudgeneClient(config);

		for (int i = 0; i < jobIds.length; i++) {
			String id = jobIds[i];

			CloudgeneJob job = client.getJobDetails(id);

			if (job.isRunning()) {
				println("Job " + job.getId() + " is running. Download starts automatically when job is finished...");
				client.waitForJob(job.getId());
				job = client.getJobDetails(job.getId());

				println("Job completed. State: " + job.getJobStateAsText());
				println();
				println();
			}

			println("Download job " + job.getId() + "...");
			Object password = getValue("password");
			if (password != null) {
				job.downloadAll(client, password.toString());
			} else {
				job.downloadAll(client);
			}
			println();
			println();
		}

		printlnInGreen("All data downloaded.");
		println();
		println();

		return 0;

	}

}
