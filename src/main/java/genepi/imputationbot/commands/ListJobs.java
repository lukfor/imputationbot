package genepi.imputationbot.commands;

import java.text.SimpleDateFormat;

import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.client.CloudgeneJobList;
import genepi.imputationbot.model.Project;
import genepi.imputationbot.model.ProjectList;

public class ListJobs extends BaseCommand {

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ListJobs(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addFlag("all", "Show all jobs");
	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		CloudgeneClient client = getClient();

		String[] jobIds = getRemainingArgs();

		if (jobIds.length > 0) {

			String id = jobIds[0];

			ProjectList projects = getProjectList();
			Project project = projects.getByName(id);

			if (project != null) {

				println("Project: " + project.getName());
				println();

				CloudgeneJobList jobs = client.getJobs(project);
				println(jobs.toString());

				return 0;

			} else {

				CloudgeneJob cloudgeneJob = client.getJobDetails(id);
				println(cloudgeneJob.toString());

				return 0;
			}

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
