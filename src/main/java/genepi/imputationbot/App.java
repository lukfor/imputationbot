package genepi.imputationbot;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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
import genepi.imputationbot.util.Emoji;
import genepi.imputationbot.util.OperatingSystem;

public class App extends Toolbox {

	public static final String VERSION = "2.1.0";

	public App(String command, String[] args) {
		super(command, args);
		printHeader();
	}

	public static void main(String[] args) throws Exception {

		// disable ansi colors on windows
		if (OperatingSystem.isWindows()) {
			AnsiColors.disable();
			Emoji.disable();
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
	
	
	private void printHeader() {
		System.out.println();
		System.out.println("imputation-bot " + App.VERSION + " " + Emoji.ROBOT);
		System.out.println("https://imputationserver.sph.umich.edu");
		System.out.println("(c) 2019-2020 Lukas Forer, Sebastian Schoenherr and Christian Fuchsberger");

		try {
			URL url = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(url.openStream());
			Attributes attr = manifest.getMainAttributes();
			String buildTime = attr.getValue("Build-Time");
			String builtBy = attr.getValue("Built-By");
			System.out.println("Built by " + builtBy + " on " + buildTime);

		} catch (IOException E) {
			// handle
		}

		System.out.println();
	}
}
