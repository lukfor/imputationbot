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
import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.model.Project;
import genepi.imputationbot.model.ProjectJob;
import genepi.imputationbot.model.ProjectList;
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
			CloudgeneClient client = getClient();
			JSONObject app = client.getDefaultApp();

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

			Option optionProjectName = new Option(null, "name", true, "Optional project name");
			optionProjectName.setRequired(false);
			options.addOption(optionProjectName);

			// parse the command line arguments
			CommandLine line = null;
			try {

				line = parser.parse(options, args);

			} catch (Exception e) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("imputationbot impute", "\nImputation Parameters", options, "", true);
				println();
				ComandlineOptionsUtil.printDetails(params);
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

			String[] referencePanels = form.getEntries().getFirst("refpanel").getValue().split(",");

			String projectName = line.getOptionValue("name");
			Project project = new Project();
			project.setName(projectName);

			for (int i = 0; i < referencePanels.length; i++) {

				String referencePanel = referencePanels[i];

				FormDataSet newForm = new FormDataSet();
				newForm.setMultipart(true);
				newForm.getEntries().addAll(form.getEntries());
				newForm.getEntries().getFirst("refpanel").setValue(referencePanel);

				if (projectName != null) {
					newForm.getEntries().add(new FormData("job-name",
							projectName + " (" + referencePanel.replaceAll("apps@", "") + ")"));
				}

				println();
				println("Submitting job... [" + (i + 1) + "/" + referencePanels.length + "]");

				CloudgeneJob job = client.submitJob(app.getString("id"), newForm);
				//reload job to get job name etc..
				job = client.getJobDetails(job.getId());

				if (mode.equals(QC_JOB)) {
					printlnInGreen("Quality Control job '" + job.getName() + "' submitted successfully");
				} else {
					printlnInGreen("Imputation job '" + job.getName() + "' submitted successfully");
				}

				println("👉 Check the job progress on " + getConfig().getHostname() + "/index.html#!jobs/"
						+ job.getId());
				println();


				ProjectJob projectJob = new ProjectJob();
				projectJob.setJob(job.getId());
				// projectJob.setParams(params);

				project.getJobs().add(projectJob);

			}

			if (project.getName() != null) {
				ProjectList projects = getProjects();
				projects.add(project);
				saveProjects();
				printlnInGreen("Project " + project + " created.");
				println();
				println();
			}

			if (line.hasOption("wait")) {
				println("Jobs are running....");
				client.waitForProject(project);
				println("All jobs completed.");
				for (ProjectJob projectJob : project.getJobs()) {
					CloudgeneJob jobDetails = client.getJobDetails(projectJob.getJob());
					println("  " + projectJob.getJob() + ": " + jobDetails.getJobStateAsText());
				}
				println();
				println();
			}

			return 0;
		} catch (Exception e) {
			error(e);
			return 1;
		}
	}

}
