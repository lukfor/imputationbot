package genepi.imputationbot.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbot.model.Project;
import genepi.imputationbot.model.ProjectJob;
import genepi.imputationbot.util.Version;
import genepi.imputationbot.util.uploads.ProgressEntityWrapper;
import genepi.imputationbot.util.uploads.UploadProgressPrinter;

public class CloudgeneClient {

	public static final String[] IMPUTATIONSERVER_ID = { "minimac4", "imputationserver" };

	public static final Version IMPUTATIONSERVER_MIN_VERSION = Version.parse("1.2.1");

	private Collection<CloudgeneInstance> instances;

	public CloudgeneClient(Collection<CloudgeneInstance> instances) {
		this.instances = instances;
	}

	public CloudgeneUser getAuthUser(CloudgeneInstance instance) throws CloudgeneException {

		String content = get(instance, "/api/v2/users/me/profile");
		JSONObject object = new JSONObject(content);
		return new CloudgeneUser(object);

	}

	public CloudgeneApiToken verifyToken(CloudgeneInstance instance, String token) throws CloudgeneException {

		List<NameValuePair> params = Form.form().add("token", token).build();
		String content;
		try {
			content = post(instance, "/api/v2/tokens/verify", new UrlEncodedFormEntity(params, "UTF-8"));
			JSONObject object = new JSONObject(content);
			return new CloudgeneApiToken(object);
		} catch (UnsupportedEncodingException e) {
			throw new CloudgeneException(22, "Unsupported encoding.");
		}

	}

	public JSONObject getServerDetails(CloudgeneInstance instance) throws CloudgeneException {

		String content = get(instance, "/api/v2/server");
		JSONObject object = new JSONObject(content);
		return object;

	}

	public JSONObject getAppByIds(CloudgeneInstance instance, String[] ids, Version minVersion)
			throws CloudgeneException, CloudgeneAppException {

		JSONObject server = getServerDetails(instance);
		JSONArray apps = server.getJSONArray("apps");

		// search for application by id
		for (int i = 0; i < apps.length(); i++) {
			JSONObject app = apps.getJSONObject(i);
			String id = app.get("id").toString();
			CloudgeneAppId appId = new CloudgeneAppId(id);

			for (String _id : ids) {
				if (appId.getId().equals(_id)) {

					JSONObject appDetails = getAppDetails(instance, id);
					String versionString = appDetails.getString("version");
					Version version = Version.parse(versionString);

					if (version.compareTo(minVersion) != -1) {
						return appDetails;
					} else {
						throw new CloudgeneAppException(
								"Found Imputationserver " + version + " but needs Imputationserver >= " + minVersion);
					}
				}
			}

		}

		throw new CloudgeneAppException("No imputationserver application found on " + instance.getHostname());

	}

	public JSONObject getAppDetails(CloudgeneInstance instance, String app) throws CloudgeneException {

		String content = get(instance, "/api/v2/server/apps/" + app);
		JSONObject object = new JSONObject(content);
		return object;

	}

	public JSONObject getDefaultApp(CloudgeneInstance instance) throws CloudgeneException, CloudgeneAppException {
		return getAppByIds(instance, IMPUTATIONSERVER_ID, IMPUTATIONSERVER_MIN_VERSION);
	}

	public CloudgeneJob submitJob(CloudgeneInstance instance, String app, HttpEntity form) throws CloudgeneException {

		ProgressEntityWrapper wrapper = new ProgressEntityWrapper(form, new UploadProgressPrinter());

		String content = post(instance, "/api/v2/jobs/submit/" + app, wrapper);
		JSONObject object = new JSONObject(content);

		System.out.println("  Uploading files" + " [100%]\n");

		return new CloudgeneJob(object, instance);
	}

	public void waitForProject(Project project) throws CloudgeneException, InterruptedException {
		waitForProject(project, 10000);
	}

	public void waitForProject(Project project, int pollingTime) throws CloudgeneException, InterruptedException {

		for (ProjectJob job : project.getJobs()) {
			waitForJob(job.getJob(), pollingTime);
		}

	}

	public void waitForJob(CloudgeneJob job) throws CloudgeneException, InterruptedException {
		waitForJob(job.getId());
	}

	public void waitForJob(String id) throws CloudgeneException, InterruptedException {
		waitForJob(id, 10000);
	}

	public void waitForJob(String id, int pollingTime) throws CloudgeneException, InterruptedException {

		CloudgeneInstance instance = getInstanceByJobId(id);

		String content = get(instance, "/api/v2/jobs/" + id + "/status");
		JSONObject object = new JSONObject(content);
		CloudgeneJob job = new CloudgeneJob(object, instance);

		if (job.isRunning()) {
			Thread.sleep(pollingTime);
			waitForJob(id, pollingTime);
		} else {
			Thread.sleep(5000);
		}

	}

	public CloudgeneJob getJobDetails(String id) throws CloudgeneException {

		CloudgeneInstance instance = getInstanceByJobId(id);

		String content = get(instance, "/api/v2/jobs/" + id);
		JSONObject object = new JSONObject(content);
		return new CloudgeneJob(object, instance);

	}

	public CloudgeneJobList getJobs() throws CloudgeneException {

		CloudgeneJobList result = new CloudgeneJobList();

		for (CloudgeneInstance instance : instances) {

			String content = get(instance, "/api/v2/jobs");
			JSONObject object = new JSONObject(content);

			JSONArray jobs = object.getJSONArray("data");
			for (int i = 0; i < jobs.length(); i++) {
				result.add(new CloudgeneJob(jobs.getJSONObject(i), instance));
			}
		}

		result.sortById();

		return result;

	}

	public CloudgeneJobList getJobs(Project project) throws CloudgeneException {

		CloudgeneJobList result = new CloudgeneJobList();

		for (ProjectJob job : project.getJobs()) {
			CloudgeneJob jobDetails = getJobDetails(job.getJob());
			result.add(jobDetails);
		}

		result.sortById();

		return result;

	}

	public CloudgeneInstance getInstanceByJobId(String id) throws CloudgeneException {
		for (CloudgeneInstance instance : instances) {
			try {
				String content = get(instance, "/api/v2/jobs/" + id);
				JSONObject object = new JSONObject(content);
				return instance;
			} catch (Exception e) {
				// ignore not found (404)
			}
		}
		throw new CloudgeneException(404, "Job '" + id + "' not found.");
	}

	public String get(CloudgeneInstance instance, String url) throws CloudgeneException {

		try {

			HttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(instance.getHostname() + url);
			httpget.addHeader("X-Auth-Token", instance.getToken());

			// Execute and get the response.
			HttpResponse response = httpclient.execute(httpget);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity responseEntity = response.getEntity();
			switch (statusCode) {
			case 200:
			case 201:
				return EntityUtils.toString(responseEntity);
			case 401:
				throw new CloudgeneException(401,
						"The provided Token is invalid. Please check if your token is correct and not expired");
			default:
				String content = EntityUtils.toString(responseEntity);
				try {
					JSONObject object = new JSONObject(content);
					throw new CloudgeneException(statusCode, object.getString("message"));
				} catch (Exception e) {
					throw new CloudgeneException(statusCode, content);
				}

			}
		} catch (IOException e2) {

			throw new CloudgeneException(100, e2.toString());
		}
	}

	public String post(CloudgeneInstance instance, String url, HttpEntity entity) throws CloudgeneException {

		try {

			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(instance.getHostname() + url);
			httppost.addHeader("X-Auth-Token", instance.getToken());
			httppost.setEntity(entity);

			// Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity responseEntity = response.getEntity();
			switch (statusCode) {
			case 200:
			case 201:
				return EntityUtils.toString(responseEntity);
			case 401:
				throw new CloudgeneException(401,
						"The provided Token is invalid. Please check if your token is correct and not expired");
			default:
				String content = EntityUtils.toString(responseEntity);
				try {
					JSONObject object = new JSONObject(content);
					throw new CloudgeneException(statusCode, object.getString("message"));
				} catch (Exception e) {
					throw new CloudgeneException(statusCode, content);
				}

			}
		} catch (IOException e2) {

			throw new CloudgeneException(100, e2.toString());
		}
	}

}
