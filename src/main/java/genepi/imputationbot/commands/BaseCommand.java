package genepi.imputationbot.commands;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import genepi.base.Tool;
import genepi.imputationbot.client.CloudgeneClientConfig;

public abstract class BaseCommand extends Tool {

	public static String CONFIG_FILENAME = "imputationbot.config"; 
	
	public BaseCommand(String[] args) {
		super(args);
	}

	public void info(String message) {
		System.out.println(message);
	}

	public void error(String message) {
		printlnInRed("\nError: " + message);
		System.out.println();
	}

	public void printlnInRed(String text) {
		System.out.println(makeRed(text));
	}

	public String makeRed(String text) {
		return ((char) 27 + "[31m" + text + (char) 27 + "[0m");
	}

	public void printlnInGreen(String text) {
		System.out.println(makeGreen(text));
	}

	public String makeGreen(String text) {
		return ((char) 27 + "[32m" + text + (char) 27 + "[0m");
	}

	public String makeBlue(String text) {
		return ((char) 27 + "[34m" + text + (char) 27 + "[0m");
	}

	public String makeGray(String text) {
		return ((char) 27 + "[90m" + text + (char) 27 + "[0m");
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

	public String getJobStateAsText(int state) {
		switch (state) {
		case 1:
			return makeBlue("Waiting");
		case 2:
			return makeBlue("Running");
		case 3:
			return makeBlue("Exporting");
		case 4:
			return makeGreen("Success");
		case 5:
			return makeRed("Failed");
		case 6:
			return makeRed("Canceled");
		case 7:
			return makeGray("Retired");
		case 8:
			return makeGreen("Success");
		case 9:
			return makeRed("Failed");
		case 10:
			return makeGray("Deleted");
		case -1:
			return makeGray("Dead");
		}
		return makeGray("?");
	}

}
