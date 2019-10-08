package genepi.imputationbot.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.ext.html.FormDataSet;
import org.restlet.resource.ClientResource;
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

	public JSONObject getAuthUser() throws IOException, JSONException, InterruptedException {

		ClientResource resourceStatus = createClientResource("/api/v2/users/me/profile");

		resourceStatus.get();

		JSONObject object = new JSONObject(resourceStatus.getResponseEntity().getText());
		resourceStatus.release();

		return object;

	}

	public JSONObject getServerDetails() throws IOException, JSONException, InterruptedException {

		ClientResource resourceStatus = createClientResource("/api/v2/server");

		resourceStatus.get();

		JSONObject object = new JSONObject(resourceStatus.getResponseEntity().getText());
		resourceStatus.release();

		return object;

	}

	public JSONObject getAppDetails(String app) throws IOException, JSONException, InterruptedException {

		ClientResource resourceStatus = createClientResource("/api/v2/server/apps/" + app);

		resourceStatus.get();

		JSONObject object = new JSONObject(resourceStatus.getResponseEntity().getText());
		resourceStatus.release();

		return object;

	}

	public CloudgeneJob submitJob(String app, FormDataSet form) throws JSONException, IOException {

		ClientResource resource = createClientResource("/api/v2/jobs/submit/" + app);
		resource.post(form);

		JSONObject object = new JSONObject(resource.getResponseEntity().getText());
		resource.release();
		return new CloudgeneJob(object);
	}

	public void waitForJob(String id) throws IOException, JSONException, InterruptedException {

		ClientResource resourceStatus = createClientResource("/api/v2/jobs/" + id + "/status");
		resourceStatus.get();

		JSONObject object = new JSONObject(resourceStatus.getResponseEntity().getText());
		resourceStatus.release();

		boolean running = object.getInt("state") == 1 || object.getInt("state") == 2 || object.getInt("state") == 3;
		if (running) {
			Thread.sleep(10000);
			waitForJob(id);
		}
	}

	public CloudgeneJob getJobDetails(String id) throws IOException, JSONException, InterruptedException {

		ClientResource resourceStatus = createClientResource("/api/v2/jobs/" + id);

		resourceStatus.get();

		JSONObject object = new JSONObject(resourceStatus.getResponseEntity().getText());
		resourceStatus.release();

		return new CloudgeneJob(object);

	}

	public List<CloudgeneJob> getJobs() throws JSONException, IOException {
		{

			ClientResource resourceJobs = createClientResource("/api/v2/jobs");

			resourceJobs.get();

			JSONObject object = new JSONObject(resourceJobs.getResponseEntity().getText());
			JSONArray jobs = object.getJSONArray("data");
			resourceJobs.release();

			List<CloudgeneJob> result = new Vector<CloudgeneJob>();
			for (int i = 0; i < jobs.length(); i++) {
				result.add(new CloudgeneJob(jobs.getJSONObject(i)));
			}
			return result;

		}

	}

	public void downloadResults(String remotePath, String localPath)
			throws IOException, JSONException, InterruptedException {

		ClientResource resourceDownload = createClientResource("/results/" + remotePath);

		resourceDownload.get();
		resourceDownload.getResponseEntity().write(new FileOutputStream(new File(localPath)));
		resourceDownload.release();

	}

}
