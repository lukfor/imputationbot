package genepi.imputationbot.commands;

import java.text.SimpleDateFormat;

import genepi.base.Tool;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;
import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.client.CloudgeneJobList;

public class ListJobs extends BaseCommand {

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ListJobs(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addOptionalParameter("job", "job id", Tool.STRING);
		addFlag("all", "Show all jobs");
	}

	@Override
	public void init() {
	}

	@Override
	public int runAndHandleErrors() throws Exception {

		CloudgeneClientConfig config = readConfig();
		CloudgeneClient client = new CloudgeneClient(config);

		if (getValue("job") != null) {

			String id = getValue("job").toString();
			CloudgeneJob cloudgeneJob = client.getJobDetails(id);

			println(cloudgeneJob.toString());

			return 0;

		} else {

			CloudgeneJobList jobs = client.getJobs();

			if (isFlagSet("all")) {
				jobs.setLimit(-1);
			}

			println(jobs.toString());

			return 0;

		}

	}

}
