package genepi.imputationbutler.client;

import java.io.IOException;
import java.util.Map;

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
	
	public JSONObject getServerDetails() throws IOException, JSONException, InterruptedException {

		ClientResource resourceStatus = createClientResource("/api/v2/server");

		resourceStatus.get();

		JSONObject object = new JSONObject(resourceStatus.getResponseEntity().getText());
		resourceStatus.release();

		return object;

	}

	public String submitJob(String app, Map<String, String> params) throws JSONException, IOException {

		FormDataSet form = new FormDataSet();
		for (String param : params.keySet()) {
			form.add(param, params.get(param));
		}

		ClientResource resource = createClientResource("/api/v2/jobs/submit/" + app);
		resource.post(form);

		JSONObject object = new JSONObject(resource.getResponseEntity().getText());
		resource.release();
		return (String) object.get("id");
	}

	public void waitForJob(String id) throws IOException, JSONException, InterruptedException {

		ClientResource resourceStatus = createClientResource("/api/v2/jobs/" + id + "/status");
		resourceStatus.get();

		JSONObject object = new JSONObject(resourceStatus.getResponseEntity().getText());
		resourceStatus.release();

		boolean running = object.getInt("state") == 1 || object.getInt("state") == 2 || object.getInt("state") == 3;
		if (running) {
			Thread.sleep(500);
			waitForJob(id);
		}
	}

	public JSONObject getJobDetails(String id) throws IOException, JSONException, InterruptedException {

		ClientResource resourceStatus = createClientResource("/api/v2/jobs/" + id);

		resourceStatus.get();

		JSONObject object = new JSONObject(resourceStatus.getResponseEntity().getText());
		resourceStatus.release();

		return object;

	}

	public JSONArray getJobs() throws JSONException, IOException {
		{

			ClientResource resourceJobs = createClientResource("/api/v2/jobs");

			try {
				resourceJobs.get();
			} catch (Exception e) {
			}

			JSONObject object = new JSONObject(resourceJobs.getResponseEntity().getText());
			JSONArray result = object.getJSONArray("data");
			resourceJobs.release();

			return result;

		}

	}

}
