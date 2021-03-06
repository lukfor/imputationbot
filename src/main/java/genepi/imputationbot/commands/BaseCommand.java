package genepi.imputationbot.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import genepi.base.Tool;
import genepi.imputationbot.client.CloudgeneApiToken;
import genepi.imputationbot.client.CloudgeneAppException;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneException;
import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.client.CloudgeneInstanceList;
import genepi.imputationbot.model.ProjectList;
import genepi.imputationbot.util.AnsiColors;
import genepi.imputationbot.util.Console;
import genepi.imputationbot.util.Emoji;
import genepi.io.FileUtil;

public abstract class BaseCommand extends Tool {

	public static String USER_HOME = System.getProperty("user.home");

	public static String APP_HOME = FileUtil.path(USER_HOME, ".imputationbot");

	public static String INSTANCES_FILENAME = "imputationbot.instances";

	public static String PROJECTS_FILENAME = "imputationbot.projects";

	private CloudgeneInstanceList instanceList;

	private ProjectList projectList;

	public BaseCommand(String[] args) {
		super(args);
		FileUtil.createDirectory(APP_HOME);
	}

	public void println() {
		System.out.println();
	}

	public void println(String message) {
		System.out.println(message);
	}

	public void error(Exception e) {
		if (e instanceof CloudgeneException || e instanceof CloudgeneAppException || e instanceof FileNotFoundException) {
			error(e.getMessage());
		} else {
			error(e.toString());
		}
	}

	public String read(String label, String defaultValue) {
		return Console.prompt(label, defaultValue);
	}

	public String read(String label) {
		return Console.prompt(label);
	}

	public void error(String message) {
		System.out.println();
		printlnInRed("Error: " + message);
		System.out.println();
	}

	public void printlnInRed(String text) {
		System.out.println(AnsiColors.red(text));
	}

	public void printlnInGreen(String text) {
		System.out.println(AnsiColors.green(text));
	}

	public CloudgeneClient getClient() throws Exception {
		return getClient(true);
	}

	public CloudgeneClient getClient(boolean verify) throws Exception {

		CloudgeneClient client = new CloudgeneClient(getInstanceList().getAll());

		for (CloudgeneInstance instance : getInstanceList().getAll()) {

			if (verify) {

				try {
					CloudgeneApiToken token = client.verifyToken(instance, instance.getToken());

					if (!token.isValid()) {
						throw new CloudgeneException(100, token.toString());
					}

					if (token.getExpiresInDays() < 7) {
						println();
						println(Emoji.LIGHT_BULB + " Warning! Your API Token expires in " + token.getExpiresInDays() + " days");
						println();
					}

				} catch (CloudgeneException e) {
					if (e.getCode() == 404) {
						throw new CloudgeneException(e.getCode(),
								"Token could not be verified. Are you sure Imputationserver is running on '"
										+ instance.getHostname() + "'?");
					} else {
						throw e;
					}
				}
			}
		}

		return client;

	}

	public CloudgeneInstanceList getInstanceList() throws IOException, CloudgeneAppException {

		if (instanceList == null) {
			File file = new File(FileUtil.path(APP_HOME, INSTANCES_FILENAME));
			if (file.exists()) {
				instanceList = CloudgeneInstanceList.load(FileUtil.path(APP_HOME, INSTANCES_FILENAME));
			} else {
				instanceList = new CloudgeneInstanceList();
			}

		}
		return instanceList;
	}

	public void saveInstanceList() throws IOException, CloudgeneAppException {
		getInstanceList().save(FileUtil.path(APP_HOME, INSTANCES_FILENAME));
	}

	public ProjectList getProjectList() throws IOException {

		if (projectList == null) {
			File file = new File(FileUtil.path(APP_HOME, PROJECTS_FILENAME));

			if (file.exists()) {
				projectList = ProjectList.load(FileUtil.path(APP_HOME, PROJECTS_FILENAME));
			} else {
				projectList = new ProjectList();
			}
		}
		return projectList;
	}

	public void saveProjects() throws IOException, CloudgeneException {
		getProjectList().save(FileUtil.path(APP_HOME, PROJECTS_FILENAME));
	}

	@Override
	public int run() {
		try {

			return runAndHandleErrors();

		} catch (Exception e) {
			error(e);
			//e.printStackTrace();
			return 1;
		}
	}

	public abstract int runAndHandleErrors() throws Exception;

}
