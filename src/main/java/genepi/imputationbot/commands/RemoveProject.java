package genepi.imputationbot.commands;

import genepi.imputationbot.model.Project;

public class RemoveProject extends BaseCommand {

	public static final String HELP = "A list of all projects can be obtained with 'imputationbot projects'.";

	public RemoveProject(String... args) {
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

		String[] ids = getRemainingArgs();

		if (ids.length < 1) {
			error("Please specify a project name. " + HELP);
			return -1;
		}

		String id = ids[0];

		Project project = getProjectList().getByName(id);

		if (project != null) {

			getProjectList().remove(project);
			saveProjects();
			saveInstanceList();
			printlnInGreen("Project " + project.getName() + " removed.");
			println();
			return 0;

		} else {
			error("Unknown Project '" + id + "'. " + HELP);
			return -1;
		}

	}

}
