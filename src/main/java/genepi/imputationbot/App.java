package genepi.imputationbot;

import genepi.base.Toolbox;
import genepi.imputationbot.commands.AddInstance;
import genepi.imputationbot.commands.DownloadResults;
import genepi.imputationbot.commands.ListInstances;
import genepi.imputationbot.commands.ListJobs;
import genepi.imputationbot.commands.ListProjects;
import genepi.imputationbot.commands.ListRefPanels;
import genepi.imputationbot.commands.RemoveInstance;
import genepi.imputationbot.commands.RemoveProject;
import genepi.imputationbot.commands.RunImputationJob;
import genepi.imputationbot.commands.RunQualityControlJob;
import genepi.imputationbot.commands.ShowVersion;
import genepi.imputationbot.commands.UpdateInstance;
import genepi.imputationbot.util.AnsiColors;
import genepi.imputationbot.util.OperatingSystem;

public class App extends Toolbox {

	public static final String VERSION = "0.9.0";

	public App(String command, String[] args) {
		super(command, args);
	}

	public static void main(String[] args) throws Exception {

		// disable ansi colors on windows
		if (OperatingSystem.isWindows()) {
			AnsiColors.disable();
		}

		App toolbox = new App("imputationbot", args);
		toolbox.addTool("add-instance", AddInstance.class);
		toolbox.addTool("update-instance", UpdateInstance.class);
		toolbox.addTool("remove-instance", RemoveInstance.class);
		toolbox.addTool("instances", ListInstances.class);
		toolbox.addTool("download", DownloadResults.class);
		toolbox.addTool("impute", RunImputationJob.class);
		toolbox.addTool("refpanels", ListRefPanels.class);
		toolbox.addTool("jobs", ListJobs.class);
		toolbox.addTool("qc", RunQualityControlJob.class);
		toolbox.addTool("projects", ListProjects.class);
		toolbox.addTool("remove-project", RemoveProject.class);
		toolbox.addTool("version", ShowVersion.class);
		toolbox.start();

	}
}
