package genepi.imputationbot.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import genepi.base.Tool;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;
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
			JSONArray jobs = client.getJobs();

			if (getValue("json") != null) {

				String filename = getValue("json").toString();

				StringBuffer buffer = new StringBuffer();
				buffer.append(jobs.toString());
				FileUtil.writeStringBufferToFile(filename, buffer);
				printlnInGreen("Written all jobs to file '" + filename + "'");

			} else {

				if (jobs.length() == 0) {
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
				if (isFlagSet("all") || jobs.length() < length) {
					length = jobs.length();
				}

				String[][] data = new String[length][header.length];

				for (int i = 0; i < length; i++) {
					JSONObject job = jobs.getJSONObject(i);
					data[i][0] = "#" + (jobs.length() - i);
					data[i][1] = job.getInt("positionInQueue") + "";
					data[i][1] = getJobStateAsText(job.getInt("state"));
					data[i][2] = job.getString("application");
					data[i][3] = job.getString("id");
					data[i][4] = DATE_FORMAT.format(new Date(job.getLong("submittedOn")));
					data[i][5] = ((job.getLong("endTime") - job.getLong("startTime")) / 1000) + " sec";
				}

				info(FlipTable.of(header, data));
				info("Showing " + 1 + " to " + length + " of " + jobs.length() + " jobs.\n\n");
			}
			return 0;

		} catch (Exception e) {

			error(e.toString());
			return 1;

		}
	}

}
