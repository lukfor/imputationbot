package genepi.imputationbot;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.restlet.engine.Engine;
import org.restlet.ext.slf4j.Slf4jLoggerFacade;

import genepi.base.Toolbox;
import genepi.imputationbot.commands.ConfigCloudgeneClient;
import genepi.imputationbot.commands.DownloadResults;
import genepi.imputationbot.commands.ListJobs;
import genepi.imputationbot.commands.RunImputationJob;
import genepi.imputationbot.commands.RunQualityControlJob;
import genepi.imputationbot.commands.ShowVersion;

public class App extends Toolbox {

	public static final String VERSION = "0.0.1";

	public App(String command, String[] args) {
		super(command, args);
		turnLoggingOff();
		printHeader();
	}

	private void printHeader() {
		System.out.println();
		System.out.println("Imputation Bot " + App.VERSION + " 🤖");
		System.out.println("https://imputationserver.sph.umich.edu");
		System.out.println("(c) 2019 Lukas Forer, Sebastian Schoenherr and Christian Fuchsberger");

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

	public void turnLoggingOff() {

		Slf4jLoggerFacade loggerFacade = new Slf4jLoggerFacade();
		Engine.getInstance().setLoggerFacade(loggerFacade);

		List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for (Logger logger : loggers) {
			logger.setLevel(Level.OFF);
		}
	}

	public static void main(String[] args) throws Exception {

		App toolbox = new App("imputationbot", args);
		toolbox.addTool("configure", ConfigCloudgeneClient.class);
		toolbox.addTool("run", RunImputationJob.class);
		toolbox.addTool("validate", RunQualityControlJob.class);
		toolbox.addTool("jobs", ListJobs.class);
		toolbox.addTool("download", DownloadResults.class);
		toolbox.addTool("version", ShowVersion.class);
		toolbox.start();

	}
}
