package genepi.imputationbutler;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import genepi.base.Toolbox;
import genepi.imputationbutler.commands.ConfigCloudgeneClient;
import genepi.imputationbutler.commands.DownloadResults;
import genepi.imputationbutler.commands.ListJobs;
import genepi.imputationbutler.commands.RunImputationJob;
import genepi.imputationbutler.commands.RunQualityControlJob;


public class App  extends Toolbox {
	
	public static final String VERSION  = "0.0.1";

	public App(String command, String[] args) {
		super(command, args);
		printHeader();
	}

	private void printHeader() {
		System.out.println();
		System.out.println("Imputation Butler " + App.VERSION);
		System.out.println("https://imputationserver.sph.umich.edu");
		System.out.println("(c) 2019 Lukas Forer and Sebastian Schoenherr");

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

	public static void main(String[] args) throws Exception {
		App toolbox = new App("imputation-butler", args);
		toolbox.addTool("configure", ConfigCloudgeneClient.class);
		toolbox.addTool("run", RunImputationJob.class);
		toolbox.addTool("validate", RunQualityControlJob.class);
		toolbox.addTool("list", ListJobs.class);
		toolbox.addTool("download", DownloadResults.class);

		toolbox.start();

	}
}
