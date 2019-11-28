package genepi.imputationbot.commands;

import java.text.SimpleDateFormat;

import genepi.imputationbot.model.Project;
import genepi.imputationbot.model.ProjectList;

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

		ProjectList projects = getProjects();
		for (Project project: projects.getProjects()) {
			println("Project: " + project);
		}
		
		return 0;

	}

}
