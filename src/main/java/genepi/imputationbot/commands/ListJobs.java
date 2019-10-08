package genepi.imputationbot.commands;

import java.text.SimpleDateFormat;
import java.util.List;

import genepi.base.Tool;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;
import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.util.FlipTable;
import genepi.io.FileUtil;

public class ListJobs extends BaseCommand {

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ListJobs(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addOptionalParameter("json", "write result as json to the provided file", Tool.STRING);
		addFlag("all", "Show all jobs");
	}

	@Override
	public void init() {
	}

	@Override
	public int run() {

		try {
			CloudgeneClientConfig config = readConfig();
			CloudgeneClient client = new CloudgeneClient(config);
			List<CloudgeneJob> jobs = client.getJobs();

			if (getValue("json") != null) {

				String filename = getValue("json").toString();

				StringBuffer buffer = new StringBuffer();
				buffer.append(jobs.toString());
				FileUtil.writeStringBufferToFile(filename, buffer);
				printlnInGreen("Written all jobs to file '" + filename + "'");

			} else {

				if (jobs.size() == 0) {
					info("No jobs found.\n");
					return 0;
				}

				String[] header = new String[6];
				header[0] = "";
				header[1] = "Queue";
				header[1] = "Status";
				header[2] = "Application";
				header[3] = "Job";
				header[4] = "Submitted On";
				header[5] = "Execution Time";

				int length = 10;
				if (isFlagSet("all") || jobs.size() < length) {
					length = jobs.size();
				}

				String[][] data = new String[length][header.length];

				for (int i = 0; i < length; i++) {
					CloudgeneJob job = jobs.get(i);
					data[i][0] = "#" + (jobs.size() - i);
					data[i][1] = job.getQueuePosition() + "";
					data[i][1] = job.getJobStateAsText();
					data[i][2] = job.getApplication();
					data[i][3] = job.getId();
					data[i][4] = DATE_FORMAT.format(job.getSubmittedOn());
					data[i][5] = job.getExecutionTime() + " sec";
				}

				info(FlipTable.of(header, data));
				info("Showing " + 1 + " to " + length + " of " + jobs.size() + " jobs.\n\n");
			}
			return 0;

		} catch (Exception e) {

			error(e.toString());
			return 1;

		}
	}

}
