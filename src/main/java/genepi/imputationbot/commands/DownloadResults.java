package genepi.imputationbot.commands;

import java.io.File;

import genepi.base.Tool;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.model.Project;
import genepi.imputationbot.model.ProjectList;

public class DownloadResults extends BaseCommand {

	public DownloadResults(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addOptionalParameter("password", "Use this password to encrypt files", Tool.STRING);
		addOptionalParameter("output", "Folder where to store all downloaded files (default location: ./JOB_ID)",
				Tool.STRING);
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

		CloudgeneClient client = getClient();

		ProjectList projects = getProjects();
		Project project = projects.getByName(jobIds[0]);

		if (project != null) {

			println("Project: " + project.getName());
			println();
			
			//use all ids from project
			jobIds = new String[project.getJobs().size()];
			for (int i = 0; i< project.getJobs().size(); i++) {
				jobIds[i] = project.getJobs().get(i).getJob();
			}
			
		}
		
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

			println("Downloading job " + job.getId() + "... " + "[" + (i+1) + "/" + jobIds.length + "]");
			Object password = getValue("password");
			Object output = getValue("output");
			String outputFolder = null;
			if (output != null) {
				outputFolder = output.toString();
			} else {
				outputFolder = job.getId();
			}
			if (password != null) {
				job.downloadAll(client, outputFolder, password.toString());
			} else {
				job.downloadAll(client, outputFolder);
			}
			println();
			printlnInGreen("All data downloaded and stored in " + (new File(outputFolder)).getAbsolutePath());
			println();
			println();
		}

		return 0;

	}

}
