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

	public List<String> getReferencePanels() throws CloudgeneException, CloudgeneAppException{
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
	
	public Map<String, String> getHttpHeader() {
		Map<String, String> httpHeader = new HashMap<String, String>();
		httpHeader.put("X-Auth-Token", token);
		return httpHeader;
	}
	
}
