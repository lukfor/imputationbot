package genepi.imputationbot.commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import genepi.base.Tool;
import genepi.imputationbot.App;
import genepi.imputationbot.client.CloudgeneApiToken;
import genepi.imputationbot.client.CloudgeneAppException;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneException;
import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.client.CloudgeneInstanceList;
import genepi.imputationbot.model.ProjectList;
import genepi.imputationbot.util.AnsiColors;
import genepi.io.FileUtil;

public abstract class BaseCommand extends Tool {

	public static String USER_HOME = System.getProperty("user.home");

	public static String APP_HOME = FileUtil.path(USER_HOME, ".imputationbot");
	
	public static String INSTANCES_FILENAME = "imputationbot.instances";

	public static String PROJECTS_FILENAME = "imputationbot.projects";

	private static Scanner scanner = new Scanner(System.in);

	private CloudgeneInstanceList instanceList;

	private ProjectList projectList;

	public BaseCommand(String[] args) {
		super(args);
		FileUtil.createDirectory(APP_HOME);
		printHeader();
	}

	public BaseCommand(String[] args, boolean header) {
		super(args);
		FileUtil.createDirectory(APP_HOME);
		if (header) {
			printHeader();
		}
	}

	private void printHeader() {
		System.out.println();
		System.out.println("Imputation Bot " + App.VERSION + " 🤖");
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

	public void println() {
		System.out.println();
	}

	public void println(String message) {
		System.out.println(message);
	}

	public void error(Exception e) {
		if (e instanceof CloudgeneException || e instanceof CloudgeneAppException) {
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

	public CloudgeneClient getClient() throws Exception {

		CloudgeneClient client = new CloudgeneClient(getInstanceList().getAll());

		for (CloudgeneInstance instance : getInstanceList().getAll()) {

			try {
				CloudgeneApiToken token = client.verifyToken(instance, instance.getToken());

				if (!token.isValid()) {
					throw new CloudgeneException(100, token.toString());
				}

				if (token.getExpiresInDays() < 7) {
					println();
					println("💡 Warning! Your API Token expires in " + token.getExpiresInDays() + " days");
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

		return client;

	}

	public CloudgeneInstanceList getInstanceList() throws IOException, CloudgeneAppException {
		return getInstanceList(true);
	}

	public CloudgeneInstanceList getInstanceList(boolean check) throws IOException, CloudgeneAppException {

		if (instanceList == null) {
			File file = new File(FileUtil.path(APP_HOME, INSTANCES_FILENAME));

			if (!file.exists() && check) {
				throw new CloudgeneAppException(
						"No instance found. Please run 'imputationbot add-instance' and enter your API Token");
			}

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

	public void saveProjects() throws IOException {
		getProjectList().save(FileUtil.path(APP_HOME, PROJECTS_FILENAME));
	}

	@Override
	public int run() {
		try {

			return runAndHandleErrors();

		} catch (Exception e) {
			error(e);
			e.printStackTrace();
			return 1;
		}
	}

	public abstract int runAndHandleErrors() throws Exception;

}
