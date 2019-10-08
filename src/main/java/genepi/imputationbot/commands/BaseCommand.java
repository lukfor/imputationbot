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
import genepi.imputationbot.util.AnsiColors;

public abstract class BaseCommand extends Tool {

	public static String CONFIG_FILENAME = "imputationbot.config";

	public BaseCommand(String[] args) {
		super(args);
	}

	public void info(String message) {
		System.out.println(message);
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

}
