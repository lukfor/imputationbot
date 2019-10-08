package genepi.imputationbot.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.html.FormDataSet;

import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;
import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.util.ComandlineOptionsUtil;

public class RunImputationJob extends BaseCommand {

	private String[] args;

	public RunImputationJob(String[] args) {
		super(args);
		this.args = args;
	}

	@Override
	public void createParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {

	}

	@Override
	public int run() {
		return 0;
	}
	
	@Override
	public int runAndHandleErrors() throws Exception {
		return 0;
	}

	// we override start instead of run, because we use our own cli parser based
	// on inputs defined in the yaml file
	@Override
	public int start() {

		try {
			CloudgeneClientConfig config = readConfig();
			CloudgeneClient client = new CloudgeneClient(config);
			JSONObject app = client.getAppDetails(config.getApp());
			JSONArray params = app.getJSONArray("params");

			// create the command line parser
			CommandLineParser parser = new DefaultParser();

			// create options for each input param in yaml file
			Options options = ComandlineOptionsUtil.createOptionsFromApp(params);

			// add wait flag
			Option option = new Option(null, "wait", false, "Wait until the job is executed");
			option.setRequired(false);
			options.addOption(option);

			// parse the command line arguments
			CommandLine line = null;
			try {

				line = parser.parse(options, args);

			} catch (Exception e) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("imputation-butler run", "\nImputation Parameters", options, "", true);
				println();
				error(e.getMessage());
				println();
				return 1;
			}

			FormDataSet form = ComandlineOptionsUtil.createForm(params, line);
			CloudgeneJob job = client.submitJob(config.getApp(), form);

			println();
			printlnInGreen("Job submitted 👍");
			println();
			println();
			println("Job id is " + job.getId());

			if (line.hasOption("wait")) {
				println("Job is running....");
				client.waitForJob(job.getId());
				CloudgeneJob jobDetails = client.getJobDetails(job.getId());

				println("Job completed. State: " + jobDetails.getJobStateAsText());

			}

			return 0;
		} catch (Exception e) {
			error(e);
			return 1;
		}
	}

}
