package genepi.imputationbot.commands;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneAppException;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneException;
import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.model.Project;
import genepi.imputationbot.model.ProjectJob;
import genepi.imputationbot.model.ProjectList;
import genepi.imputationbot.util.CommandlineOptionsUtil;

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
		String referencePanel = parseArgs(args, "--refpanel");
		if (referencePanel == null) {
			error("No reference panel provided. Please use --refpanel to set a reference panel");
			return -1;
		}

		String projectName = parseArgs(args, "--project");
		Project project = null;

		try {
			if (projectName != null) {
				project = getProjectList().getByName(projectName);
				if (project == null) {
					// new project
					project = new Project();
					project.setName(projectName);
					ProjectList projects = getProjectList();
					projects.add(project);
				}
			}
		} catch (Exception e) {
			error(e);
			return 1;
		}

		try {

			String[] argsJob = new String[args.length];
			for (int j = 0; j < args.length; j++) {
				argsJob[j] = args[j];
				if (j > 0) {
					if (args[j - 1].equals("--refpanel")) {
						argsJob[j] = referencePanel;
					}
				}
			}

			CloudgeneInstance instance = getInstanceList().getByReferencePanel(referencePanel);

			if (instance == null) {
				error("No instance found that provides reference panel '" + referencePanel + "'");
				return -1;
			}

			println("Submitting job for " + referencePanel + " to " + instance.getName() + "...");

			ProjectJob projectJob = submitJob(instance, referencePanel, argsJob);
			if (projectJob == null) {
				return -1;
			}
			if (project != null) {
				project.getJobs().add(projectJob);
			}

			if (project != null) {
				saveProjects();
				printlnInGreen("Project " + project + " created.");
				println();
				println();
			}

			return 0;

		} catch (Exception e) {
			error(e);
			return 1;
		}
	}

	private ProjectJob submitJob(CloudgeneInstance instance, String referencePanel, String[] argsJob)
			throws IOException, CloudgeneAppException, CloudgeneException, Exception {

		CloudgeneClient client = getClient();

		JSONObject app = client.getDefaultApp(instance);

		JSONArray params = app.getJSONArray("params");

		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// create options for each input param in yaml file
		Options options = CommandlineOptionsUtil.createOptionsFromApp(params);

		// add wait flag
		Option optionWait = new Option(null, "wait", false, "Wait until the job is executed");
		optionWait.setRequired(false);
		options.addOption(optionWait);

		Option optionPassword = new Option(null, "password", true, "Password used to encrypt results");
		optionPassword.setRequired(false);
		options.addOption(optionPassword);

		Option optionProjectName = new Option(null, "project", true, "Optional project name");
		optionProjectName.setRequired(false);
		options.addOption(optionProjectName);

		Option optionStudyName = new Option(null, "name", true, "Optional project name");
		optionStudyName.setRequired(false);
		options.addOption(optionStudyName);

		// parse the command line arguments
		CommandLine line = null;
		try {

			line = parser.parse(options, argsJob);

		} catch (Exception e) {

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("imputationbot impute", "\nImputation Parameters", options, "", true);
			println();
			// CommandlineOptionsUtil.printDetails(params);
			println();
			error(e.getMessage());
			println();
			return null;
		}

		MultipartEntityBuilder form = CommandlineOptionsUtil.createForm(params, line);

		// add mode
		form.addTextBody("mode", mode);

		// add password
		if (line.hasOption("password")) {
			// TODO: add password check! smae as for username?
			form.addTextBody("password", line.getOptionValue("password"));
			println();
			println("  💡 User defined password set. Don't forget your password, you need it to decrypt your results!");
		}

		String projectName = line.getOptionValue("project");
		String studyName = line.getOptionValue("name");

		String jobName = null;
		if (projectName != null) {
			jobName = projectName + "_";
		}
		if (studyName != null) {
			jobName += studyName + "_";
		}

		if (jobName != null) {
			form.addTextBody("job-name", jobName + referencePanel.replaceAll("apps@", ""));
		}

		println();

		HttpEntity entity = form.build();
		CloudgeneJob job = client.submitJob(instance, app.getString("id"), entity);
		// reload job to get job name etc..
		job = client.getJobDetails(job.getId());

		if (mode.equals(QC_JOB)) {
			printlnInGreen("  Quality Control job '" + job.getName() + "' submitted successfully");
		} else {
			printlnInGreen("  Imputation job '" + job.getName() + "' submitted successfully");
		}

		println("  👉 Check the job progress on " + instance.getHostname() + "/index.html#!jobs/" + job.getId());
		println();
		println();
		ProjectJob projectJob = new ProjectJob();
		projectJob.setJob(job.getId());
		return projectJob;

	}

	private String parseArgs(String[] args, String option) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase(option)) {
				if (i + 1 < args.length) {
					return args[i + 1];
				}
			}
		}
		return null;
	}
}
