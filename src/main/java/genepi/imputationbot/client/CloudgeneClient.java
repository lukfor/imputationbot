package genepi.imputationbot.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.ext.html.FormDataSet;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

public class CloudgeneClient {

	private CloudgeneClientConfig config;

	public CloudgeneClient(CloudgeneClientConfig config) {
		this.config = config;
	}

	public ClientResource createClientResource(String path) {

		ClientResource resource = new ClientResource(config.getHostname() + path);

		Series<Header> requestHeader = (Series<Header>) resource.getRequest().getAttributes()
				.get(HeaderConstants.ATTRIBUTE_HEADERS);
		if (requestHeader == null) {
			requestHeader = new Series(Header.class);
			resource.getRequest().getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS, requestHeader);
		}
		requestHeader.add("X-Auth-Token", config.getToken());

		return resource;

	}

	public JSONObject getAuthUser() throws CloudgeneException {

		ClientResource resource = createClientResource("/api/v2/users/me/profile");

		String content = get(resource);
		JSONObject object = new JSONObject(content);
		return object;

	}

	public JSONObject getServerDetails() throws CloudgeneException {

		ClientResource resource = createClientResource("/api/v2/server");

		String content = get(resource);
		JSONObject object = new JSONObject(content);
		return object;

	}

	public JSONObject getAppDetails(String app) throws CloudgeneException {

		ClientResource resource = createClientResource("/api/v2/server/apps/" + app);

		String content = get(resource);
		JSONObject object = new JSONObject(content);
		return object;

	}

	public CloudgeneJob submitJob(String app, FormDataSet form) throws JSONException, IOException {

		ClientResource resource = createClientResource("/api/v2/jobs/submit/" + app);
		resource.post(form);

		JSONObject object = new JSONObject(resource.getResponseEntity().getText());
		resource.release();
		return new CloudgeneJob(object);
	}

	public void waitForJob(String id) throws CloudgeneException, InterruptedException {

		ClientResource resource = createClientResource("/api/v2/jobs/" + id + "/status");

		String content = get(resource);
		JSONObject object = new JSONObject(content);
		CloudgeneJob job = new CloudgeneJob(object);

		if (job.isRunning()) {
			Thread.sleep(10000);
			waitForJob(id);
		}
	}

	public CloudgeneJob getJobDetails(String id) throws CloudgeneException {

		ClientResource resource = createClientResource("/api/v2/jobs/" + id);

		String content = get(resource);
		JSONObject object = new JSONObject(content);
		return new CloudgeneJob(object);

	}

	public CloudgeneJobList getJobs() throws CloudgeneException {

		ClientResource resource = createClientResource("/api/v2/jobs");

		String content = get(resource);
		JSONObject object = new JSONObject(content);

		JSONArray jobs = object.getJSONArray("data");
		CloudgeneJobList result = new CloudgeneJobList();
		for (int i = 0; i < jobs.length(); i++) {
			result.add(new CloudgeneJob(jobs.getJSONObject(i)));
		}
		return result;

	}

	public void downloadResults(String remotePath, String localPath)
			throws IOException, JSONException, InterruptedException {

		ClientResource resourceDownload = createClientResource("/results/" + remotePath);

		resourceDownload.get();
		resourceDownload.getResponseEntity().write(new FileOutputStream(new File(localPath)));
		resourceDownload.release();

	}

	public String get(ClientResource resource) throws CloudgeneException {

		try {
			resource.get();
			String content = resource.getResponseEntity().getText();
			resource.release();
			return content;

		} catch (JSONException e) {
			throw new CloudgeneException("Parsing JSON object failed. " + e.getMessage());
		} catch (IOException e) {
			throw new CloudgeneException("IO Exception");
		} catch (ResourceException e) {
			try {
				JSONObject object = new JSONObject(resource.getResponseEntity().getText());
				resource.release();
				throw new CloudgeneException(e.getStatus().getCode() + " - " + object.getString("message"));
			} catch (IOException e2) {
				e2.printStackTrace();
				throw new CloudgeneException(e2.toString());
			}
		}
	}

}
