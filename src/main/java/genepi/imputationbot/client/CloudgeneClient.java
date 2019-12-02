package genepi.imputationbot.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.ext.html.FormDataSet;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

import genepi.imputationbot.model.Project;
import genepi.imputationbot.model.ProjectJob;
import genepi.imputationbot.util.Version;

public class CloudgeneClient {

	public static final String[] IMPUTATIONSERVER_ID = { "minimac4", "imputationserver" };

	public static final Version IMPUTATIONSERVER_MIN_VERSION = Version.parse("1.2.1");

	private Collection<CloudgeneInstance> instances;

	public CloudgeneClient(Collection<CloudgeneInstance> instances) {
		this.instances = instances;
	}

	public ClientResource createClientResource(CloudgeneInstance instance, String path) {

		ClientResource resource = new ClientResource(instance.getHostname() + path);

		Series<Header> requestHeader = (Series<Header>) resource.getRequest().getAttributes()
				.get(HeaderConstants.ATTRIBUTE_HEADERS);
		if (requestHeader == null) {
			requestHeader = new Series(Header.class);
			resource.getRequest().getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS, requestHeader);
		}
		requestHeader.add("X-Auth-Token", instance.getToken());

		return resource;

	}

	public CloudgeneUser getAuthUser(CloudgeneInstance instance) throws CloudgeneException {

		ClientResource resource = createClientResource(instance, "/api/v2/users/me/profile");

		String content = get(resource);
		JSONObject object = new JSONObject(content);
		return new CloudgeneUser(object);

	}

	public CloudgeneApiToken verifyToken(CloudgeneInstance instance, String token) throws CloudgeneException {

		ClientResource resource = createClientResource(instance, "/api/v2/tokens/verify");

		FormDataSet form = new FormDataSet();
		form.add("token", token);
		String content = post(resource, form);
		JSONObject object = new JSONObject(content);
		return new CloudgeneApiToken(object);

	}

	public JSONObject getServerDetails(CloudgeneInstance instance) throws CloudgeneException {

		ClientResource resource = createClientResource(instance, "/api/v2/server");

		String content = get(resource);
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

		ClientResource resource = createClientResource(instance, "/api/v2/server/apps/" + app);

		String content = get(resource);
		JSONObject object = new JSONObject(content);
		return object;

	}

	public JSONObject getDefaultApp(CloudgeneInstance instance) throws CloudgeneException, CloudgeneAppException {
		return getAppByIds(instance, IMPUTATIONSERVER_ID, IMPUTATIONSERVER_MIN_VERSION);
	}

	public CloudgeneJob submitJob(CloudgeneInstance instance, String app, FormDataSet form) throws CloudgeneException {

		ClientResource resource = createClientResource(instance, "/api/v2/jobs/submit/" + app);

		String content = post(resource, form);
		JSONObject object = new JSONObject(content);

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

	public void waitForJob(String id) throws CloudgeneException, InterruptedException {
		waitForJob(id, 10000);
	}

	public void waitForJob(String id, int pollingTime) throws CloudgeneException, InterruptedException {

		CloudgeneInstance instance = getInstanceByJobId(id);

		ClientResource resource = createClientResource(instance, "/api/v2/jobs/" + id + "/status");

		String content = get(resource);
		JSONObject object = new JSONObject(content);
		CloudgeneJob job = new CloudgeneJob(object, instance);

		if (job.isRunning()) {
			Thread.sleep(pollingTime);
			waitForJob(id, pollingTime);
		}
	}

	public CloudgeneJob getJobDetails(String id) throws CloudgeneException {

		CloudgeneInstance instance = getInstanceByJobId(id);
		ClientResource resource = createClientResource(instance, "/api/v2/jobs/" + id);

		String content = get(resource);
		JSONObject object = new JSONObject(content);
		return new CloudgeneJob(object, instance);

	}

	public CloudgeneJobList getJobs() throws CloudgeneException {

		CloudgeneJobList result = new CloudgeneJobList();

		for (CloudgeneInstance instance : instances) {
			ClientResource resource = createClientResource(instance, "/api/v2/jobs");

			String content = get(resource);
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

		return result;

	}

	public void downloadResults(CloudgeneInstance instance, String remotePath, String localPath)
			throws IOException, JSONException, InterruptedException {

		ClientResource resourceDownload = createClientResource(instance, "/results/" + remotePath);

		resourceDownload.get();
		resourceDownload.getResponseEntity().write(new FileOutputStream(new File(localPath)));
		resourceDownload.release();

	}

	public CloudgeneInstance getInstanceByJobId(String id) throws CloudgeneException {
		for (CloudgeneInstance instance : instances) {
			try {
				ClientResource resource = createClientResource(instance, "/api/v2/jobs/" + id);
				String content = get(resource);
				JSONObject object = new JSONObject(content);
				return instance;
			} catch (Exception e) {
				// ignore not found (404)
			}
		}
		throw new CloudgeneException(404, "Job '" + id + "' not found.");
	}

	public String get(ClientResource resource) throws CloudgeneException {

		try {
			resource.get();
			String content = resource.getResponseEntity().getText();
			resource.release();
			return content;

		} catch (JSONException e) {

			throw new CloudgeneException(120, "Parsing JSON object failed. " + e.getMessage());

		} catch (IOException e) {

			throw new CloudgeneException(100, "IO Exception");

		} catch (ResourceException e) {

			try {

				if (resource.getResponseEntity() == null) {
					throw new CloudgeneException(1000, "The provided server is not responding.");
				}
				String content = resource.getResponseEntity().getText();

				JSONObject object = new JSONObject(content);
				resource.release();
				throw new CloudgeneException(e.getStatus().getCode(), object.getString("message"));
			} catch (JSONException e2) {

				if (e.getStatus().getCode() == 401) {
					throw new CloudgeneException(e.getStatus().getCode(),
							"The provided Token is invalid. Please check if your token is correct and not expired");
				} else {
					throw new CloudgeneException(e.getStatus().getCode(), e.toString());
				}

			} catch (IOException e2) {

				throw new CloudgeneException(100, e2.toString());
			}
		}
	}

	public String post(ClientResource resource, FormDataSet form) throws CloudgeneException {

		try {
			resource.post(form);
			String content = resource.getResponseEntity().getText();
			resource.release();
			return content;

		} catch (JSONException e) {

			throw new CloudgeneException(120, "Parsing JSON object failed. " + e.getMessage());

		} catch (IOException e) {

			throw new CloudgeneException(100, "IO Exception");

		} catch (ResourceException e) {

			try {

				if (resource.getResponseEntity() == null) {
					throw new CloudgeneException(1000, "The provided server is not responding.");
				}
				String content = resource.getResponseEntity().getText();

				JSONObject object = new JSONObject(content);
				resource.release();
				throw new CloudgeneException(e.getStatus().getCode(), object.getString("message"));
			} catch (JSONException e2) {

				if (e.getStatus().getCode() == 401) {
					throw new CloudgeneException(e.getStatus().getCode(),
							"The provided Token is invalid. Please check if your token is correct and not expired");
				} else {
					throw new CloudgeneException(e.getStatus().getCode(), e.toString());
				}

			} catch (IOException e2) {

				throw new CloudgeneException(100, e2.toString());
			}
		}
	}

}
