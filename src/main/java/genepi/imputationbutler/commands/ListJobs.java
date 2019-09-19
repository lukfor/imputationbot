package genepi.imputationbutler.commands;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jakewharton.fliptables.FlipTable;

import genepi.base.Tool;
import genepi.imputationbutler.client.CloudgeneClient;
import genepi.imputationbutler.client.CloudgeneClientConfig;
import genepi.io.FileUtil;

public class ListJobs extends BaseCommand {

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
				
				String[] header = new String[4];
				header[0] = "";
				header[1] = "Job";
				header[2] = "Status";
				header[3] = "Application";


				int length = 10;
				if (isFlagSet("all")) {
					length = jobs.length();
				}

				String[][] data = new String[length][header.length];

				for (int i = 0; i < length; i++) {
					JSONObject job = jobs.getJSONObject(i);
					data[i][0] = "#" + (jobs.length() - i);
					data[i][1] = job.getString("id");
					data[i][2] = getJobStateAsText(job.getInt("state"));
					data[i][3] = job.getString("application");
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
