package genepi.imputationbutler.commands;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import genepi.base.Tool;
import genepi.imputationbutler.client.CloudgeneClientConfig;

public abstract class BaseCommand extends Tool {

	public BaseCommand(String[] args) {
		super(args);
	}

	public void info(String message) {
		System.out.println(message);
	}
	
	public void error(String message) {
		printlnInRed("\nError: " + message);
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

	public void writeConfig(CloudgeneClientConfig config) throws IOException {
		YamlWriter writer = new YamlWriter(new FileWriter("imputationbutler.config"));
		writer.write(config);
		writer.close();
	}

	public CloudgeneClientConfig readConfig() throws FileNotFoundException, YamlException {
		YamlReader reader = new YamlReader(new FileReader("imputationbutler.config"));
		CloudgeneClientConfig config = reader.read(CloudgeneClientConfig.class);
		return config;
	}

}
