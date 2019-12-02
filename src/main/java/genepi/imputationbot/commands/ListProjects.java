package genepi.imputationbot.commands;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.model.Project;
import genepi.imputationbot.model.ProjectJob;
import genepi.imputationbot.model.ProjectList;
import genepi.imputationbot.util.FlipTable;

public class ListProjects extends BaseCommand {

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ListProjects(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		String[] header = new String[3];
		header[0] = "Name";
		header[1] = "Created At";
		header[2] = "Jobs";

		ProjectList projectList = getProjectList();
		List<Project> projects = new Vector<Project>(projectList.getProjects());

		String[][] data = new String[projects.size()][header.length];

		for (int i = 0; i < projects.size(); i++) {
			Project project = projects.get(i);
			data[i][0] = project.getName();
			data[i][1] = project.getCreated().toString();
			String jobs = "";
			for (ProjectJob job : project.getJobs()) {
				jobs += job.getJob() + "\n";
			}
			data[i][2] = jobs;
		}

		String table = FlipTable.of(header, data) + "\n";
		println(table);

		return 0;

	}

}
