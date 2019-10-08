package genepi.imputationbot.commands;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import genepi.base.Tool;
import genepi.imputationbot.App;
import genepi.imputationbot.client.CloudgeneClientConfig;
import genepi.imputationbot.client.CloudgeneException;
import genepi.imputationbot.util.AnsiColors;

public abstract class BaseCommand extends Tool {

	public static String CONFIG_FILENAME = "imputationbot.config";

	private static Scanner scanner = new Scanner(System.in);

	public BaseCommand(String[] args) {
		super(args);
		printHeader();
	}

	public BaseCommand(String[] args, boolean header) {
		super(args);
		if (header) {
			printHeader();
		}
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

	public void println() {
		System.out.println();
	}

	public void println(String message) {
		System.out.println(message);
	}

	public void error(Exception e) {
		if (e instanceof CloudgeneException) {
			error(e.getMessage());
		} else {
			error(e.toString());
		}
	}

	public String read(String label, String defaultValue) {
		System.out.print(label + " [" + defaultValue + "]: ");
		String value = scanner.nextLine();
		if (value.isEmpty()) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public String read(String label) {
		System.out.print(label + " [None]: ");
		String value = scanner.nextLine();
		return value;
	}

	public void error(String message) {
		System.out.println();
		printlnInRed("Error: " + message);
		System.out.println();
	}

	public void printlnInRed(String text) {
		System.out.println(AnsiColors.makeRed(text));
	}

	public void printlnInGreen(String text) {
		System.out.println(AnsiColors.makeGreen(text));
	}

	public void writeConfig(CloudgeneClientConfig config) throws IOException {
		YamlWriter writer = new YamlWriter(new FileWriter(CONFIG_FILENAME));
		writer.getConfig().writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
		writer.write(config);
		writer.close();
	}

	public CloudgeneClientConfig readConfig() throws Exception {

		File file = new File(CONFIG_FILENAME);

		if (!file.exists()) {
			throw new Exception("No configuration found. Please run 'imputationbot configure'");
		}

		YamlReader reader = new YamlReader(new FileReader(CONFIG_FILENAME));
		CloudgeneClientConfig config = reader.read(CloudgeneClientConfig.class);
		return config;
	}

	@Override
	public int run() {
		try {

			return runAndHandleErrors();

		} catch (Exception e) {
			error(e);
			return 1;
		}
	}

	public abstract int runAndHandleErrors() throws Exception;

}
