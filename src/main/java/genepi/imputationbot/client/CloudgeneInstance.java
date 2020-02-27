package genepi.imputationbot.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbot.util.CommandlineOptionsUtil;

public class CloudgeneInstance {

	private String hostname;

	private String token;

	private String name = null;

	private List<String> referencePanels = null;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() throws CloudgeneException {
		if (name == null) {
			CloudgeneClient client = new CloudgeneClient(new Vector<CloudgeneInstance>());
			JSONObject server = client.getServerDetails(this);
			name = server.getString("name");
		}
		return name;
	}

	public List<String> getReferencePanels() throws CloudgeneException, CloudgeneAppException {
		if (referencePanels == null) {
			CloudgeneClient client = new CloudgeneClient(new Vector<CloudgeneInstance>());
			JSONObject app = client.getDefaultApp(this);
			JSONArray params = app.getJSONArray("params");

			referencePanels = new Vector<String>();

			for (int i = 0; i < params.length(); i++) {

				JSONObject param = params.getJSONObject(i);
				String id = param.getString("id");

				if (id.equals("refpanel")) {

					JSONArray values = param.getJSONArray("values");
					for (int j = 0; j < values.length(); j++) {
						String key = values.getJSONObject(j).getString("key");
						referencePanels.add(CommandlineOptionsUtil.prettyAppId(key));
					}

				}

			}

		}
		return referencePanels;
	}

	public String[][] getReferencePanelsWithDetails() throws CloudgeneException, CloudgeneAppException {

		CloudgeneClient client = new CloudgeneClient(new Vector<CloudgeneInstance>());

		JSONObject app = client.getDefaultApp(this);
		JSONArray params = app.getJSONArray("params");

		Map<String, String> populations = new HashMap<String, String>();

		for (int i = 0; i < params.length(); i++) {

			JSONObject param = params.getJSONObject(i);
			String id = param.getString("id");

			if (id.equals("population")) {

				JSONArray values = param.getJSONArray("values");
				for (int j = 0; j < values.length(); j++) {
					String key = values.getJSONObject(j).getString("key");
					JSONArray valuesKey = values.getJSONObject(j).getJSONArray("values");
					String description = "";
					for (int k = 0; k < valuesKey.length(); k++) {
						String keyPop = valuesKey.getJSONObject(k).getString("key");
						String value = valuesKey.getJSONObject(k).getString("value");

						String temp = keyPop;
						while (temp.length() < 10) {
							temp += " ";
						}

						description +=  temp + value + "\n";
					}
					populations.put(key, description);
				}

			}

		}

		for (int i = 0; i < params.length(); i++) {

			JSONObject param = params.getJSONObject(i);
			String id = param.getString("id");

			// ignore mode!

			// remove html tags from decription (e.g. links)

			if (id.equals("refpanel")) {

				// System.out.println("Reference Panels:");

				JSONArray values = param.getJSONArray("values");
				String[][] data = new String[values.length()][4];

				for (int j = 0; j < values.length(); j++) {
					String key = values.getJSONObject(j).getString("key");
					String value = values.getJSONObject(j).getString("value");

					data[j][0] = CommandlineOptionsUtil.prettyAppId(key);
					data[j][1] = value;
					data[j][2] = populations.get(key);
					data[j][3] = getName();
					
				}
				
				return data;
			}

		}
		
		return new String[0][4]; 

	}

	public Map<String, String> getHttpHeader() {
		Map<String, String> httpHeader = new HashMap<String, String>();
		httpHeader.put("X-Auth-Token", token);
		return httpHeader;
	}

}
