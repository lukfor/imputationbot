package genepi.imputationbot.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;

import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneClientConfig;
import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.util.ComandlineOptionsUtil;

public class AbstractRunJob extends BaseCommand {

	public static String QC_JOB = "qconly";

	public static String IMPUTATION_JOB = "imputation";

	private String[] args;

	private String mode;

	public AbstractRunJob(String[] args, String mode) {
		super(args);
		this.mode = mode;
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
			Option optionWait = new Option(null, "wait", false, "Wait until the job is executed");
			optionWait.setRequired(false);
			options.addOption(optionWait);

			Option optionPassword = new Option(null, "password", true, "Password used to encrypt results");
			optionPassword.setRequired(false);
			options.addOption(optionPassword);

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

			// add mode
			form.getEntries().add(new FormData("mode", mode));

			// add password
			if (line.hasOption("password")) {
				// TODO: add password check! smae as for username?
				form.getEntries().add(new FormData("password", line.getOptionValue("password")));
				println();
				println("💡 User defined password set. Don't forget your password, you need it to decrypt your results!");
			}

			CloudgeneJob job = client.submitJob(config.getApp(), form);

			println();

			if (mode.equals(QC_JOB)) {
				printlnInGreen("Quality Control job submitted successfully");
			} else {
				printlnInGreen("Imputation job submitted successfully");
			}

			println();
			println("👉 Check the job progress on " + config.getHostname() + "/index.html#!jobs/" + job.getId());
			println();
			println();
			if (line.hasOption("wait")) {
				println("Job is running....");
				client.waitForJob(job.getId());
				CloudgeneJob jobDetails = client.getJobDetails(job.getId());

				println("Job completed. State: " + jobDetails.getJobStateAsText());
				println();
				println();
			}

			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			error(e);
			return 1;
		}
	}

}
