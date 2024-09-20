package genepi.imputationbutler;

import java.io.*;
import java.util.List;
import java.util.Vector;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import genepi.io.FileUtil;

public class ImputationServer {

	public static final String CLOUDGENE_VERSION = "3.0.0-rc3";

	public static final String IMPUTATIONSERVER_VERSION = "2.0.0-rc2";

	public static final String HAPMAP_REFPANEL = "https://imputationserver.sph.umich.edu/resources/ref-panels/imputationserver2-hapmap2.zip";

	public static final String HAPMAP_ID ="hapmap-2";

	private static ImputationServer instance;

	private String url;

	private String adminToken;

	private ImputationServer() {

		String directory = installCloudgene("cloudgene", CLOUDGENE_VERSION);
		installImputationserver2(directory, IMPUTATIONSERVER_VERSION);
		configureImputationserver2(directory, IMPUTATIONSERVER_VERSION);
		installHapMap(directory);
		cleanUp(directory);
		startCloudgene(directory);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		url = "http://localhost:8082";

		try {
			adminToken = createTokenForUser("admin", "admin1978");
			System.out.println("Admin token created.");
		} catch (Exception e) {
			e.printStackTrace();
			adminToken = null;
		}
	}

	private void cleanUp(String directory) {
		FileUtil.deleteDirectory(FileUtil.path(directory, "data"));
		FileUtil.deleteDirectory(FileUtil.path(directory, "workspace"));
		FileUtil.deleteDirectory(FileUtil.path(directory, "logs"));
		FileUtil.deleteDirectory(FileUtil.path(directory, "tmp"));
	}

	public String getUrl() {
		return url;
	}

	public static ImputationServer getInstance() {
		if (instance == null) {
			instance = new ImputationServer();
		}
		return instance;
	}

	public String getAdminToken() {
		return adminToken;
	}

	public void revokeTokenForUser(String user, String password) throws Exception {
		LoginToken loginToken = login(user, password);

		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpDelete httpDelete = new HttpDelete(url + "/api/v2/users/" + user + "/api-token");
		httpDelete.addHeader("X-Auth-Token", loginToken.getAccessToken());


		HttpResponse response = httpclient.execute(httpDelete);
		if (user.equals("admin")) {
			adminToken = "";
		}
		httpclient.close();
	}

	public String createTokenForUser(String user, String password) throws Exception {

		LoginToken loginToken = login(user, password);

		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httppost = new HttpPost(url + "/api/v2/users/" + user + "/api-token");
		httppost.addHeader("X-Auth-Token", loginToken.getAccessToken());

		List<NameValuePair> params = new Vector<NameValuePair>();
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity responseEntity = response.getEntity();
		String content = EntityUtils.toString(responseEntity);
		JSONObject object = new JSONObject(content);

		String token = object.getString("token");
		if (user.equals("admin")) {
			adminToken = token;
		}
		httpclient.close();

		return token;

	}

	public LoginToken login(String username, String password) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url + "/login");

		List<NameValuePair> params = Form.form().add("username", username).add("password", password).build();

		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity responseEntity = response.getEntity();

		String content = EntityUtils.toString(responseEntity);
		JSONObject object = new JSONObject(content);
		String accessToken = object.getString("access_token");

		LoginToken loginToken = new LoginToken();
		loginToken.setAccessToken(accessToken);

		return loginToken;

	}

	class LoginToken {

		private String accessToken;

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		public String getAccessToken() {
			return accessToken;
		}

	}



	public String installCloudgene(String destination, String version) {

		String directory = FileUtil.path(destination, version);

		if (new File(directory).exists()) {
			System.out.println("Already installed.");
			return directory;
		}

		FileUtil.createDirectory(directory);

		// Build the command to be executed
		String command = "curl -fsSL https://get.cloudgene.io | bash -s " + version;

		// Create a ProcessBuilder to execute the command
		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
		processBuilder.redirectErrorStream(true);

		// Set the working directory to the destination folder
		processBuilder.directory(new File(directory));

		try {
			// Start the process
			Process process = processBuilder.start();

			// Capture the output of the process
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
			}

			// Wait for the process to complete and get the exit code
			int exitCode = process.waitFor();
			if (exitCode == 0) {
				System.out.println("Cloudgene installed successfully.");
				return directory;
			} else {
				System.err.println("Error occurred during installation. Exit code: " + exitCode);
			}
			System.exit(1);
		} catch (IOException e) {
			System.err.println("An IOException occurred: " + e.getMessage());
			System.exit(1);
		} catch (InterruptedException e) {
			System.err.println("The installation process was interrupted: " + e.getMessage());
			Thread.currentThread().interrupt();
			System.exit(1);
		}
		System.exit(1);
        return directory;
    }

	private static Process cloudgeneProcess;

	public static void startCloudgene(String directory) {
		// Command to start the Cloudgene server
		String command = "./cloudgene server";

		// Create a ProcessBuilder to execute the command
		ProcessBuilder processBuilder = new ProcessBuilder(new File(directory + "/cloudgene").getAbsolutePath(), "server");
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(new File(directory));

		try {
			// Start the process in the background
			cloudgeneProcess = processBuilder.start();

			// Print the output of the process
			new Thread(() -> {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(cloudgeneProcess.getInputStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
				} catch (IOException e) {
					System.err.println("Error reading process output: " + e.getMessage());
				}
			}).start();

			// Add a shutdown hook to terminate the Cloudgene server when the JVM shuts down
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				if (cloudgeneProcess != null && cloudgeneProcess.isAlive()) {
					System.out.println("Stopping Cloudgene server...");
					cloudgeneProcess.destroy(); // Gracefully stop the process
					try {
						cloudgeneProcess.waitFor(); // Wait for the process to terminate
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt(); // Restore interrupted status
					}
					System.out.println("Cloudgene server stopped.");
				}
			}));

			System.out.println("Cloudgene server started in the background.");

		} catch (IOException e) {
			System.err.println("An IOException occurred: " + e.getMessage());
		}
	}

	public static void installImputationserver2(String directory, String version) {
		String app = FileUtil.path(directory, "apps","imputationserver2", version);
		if (new File(app).exists()) {
			System.out.println("Imputationserver2 already installed. Skip.");
			return;
		}
		installApp(directory, "github://genepi/imputationserver2@v" + version);
	}

	public static void configureImputationserver2(String directory, String version) {
		// Define the file path using the version
		String filePath = FileUtil.path(directory,"config", "imputationserver2", version, "nextflow.yaml");

		File file = new File(filePath);

		// Check if the file exists
		if (!file.exists()) {
			// Create directories if they do not exist
			file.getParentFile().mkdirs();

			// Create the file with specified content
			try (FileWriter writer = new FileWriter(file)) {
				writer.write("work: \"\"\nprofile: docker");
				System.out.println("File created at " + filePath + " with specified content.");
			} catch (IOException e) {
				System.err.println("An error occurred while creating the file: " + e.getMessage());
			}
		} else {
			System.out.println("File already exists at " + filePath + ".");
		}
	}

	private void installHapMap(String directory) {
		String app = FileUtil.path(directory, "apps", HAPMAP_ID, "2.0.0");
		if (new File(app).exists()) {
			System.out.println("Hapmap already installed. Skip.");
			return;
		}
		installApp(directory, HAPMAP_REFPANEL);
	}



	public static void installApp(String directory, String app) {
		// Command to start the Cloudgene server
		String command = "./cloudgene install " + app;

		// Create a ProcessBuilder to execute the command
		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(new File(directory));

		try {
			// Start the process in the background
			Process process = processBuilder.start();

			// Print the output of the process
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
			} catch (IOException e) {
				System.err.println("Error reading process output: " + e.getMessage());
			}

			System.out.println("Installed application " + app);

		} catch (IOException e) {
			System.err.println("An IOException occurred: " + e.getMessage());
		}
	}

}
