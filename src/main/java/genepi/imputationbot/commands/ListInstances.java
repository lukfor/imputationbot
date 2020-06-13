package genepi.imputationbot.commands;

import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneApiToken;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.client.CloudgeneUser;
import genepi.imputationbot.util.FlipTable;

public class ListInstances extends BaseCommand {

	public ListInstances(String... args) {
		super(args);
	}

	@Override
	public void createParameters() {

	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		List<CloudgeneInstance> instances = new Vector<CloudgeneInstance>(getInstanceList().getAll());

		CloudgeneClient client = getClient();
		
		String[] header = new String[6];
		header[0] = "ID";
		header[1] = "Name";
		header[2] = "Hostname";
		header[3] = "Username";
		header[4] = "Version";
		header[5] = "Token expires on";
		
		String[][] data = new String[instances.size()][header.length];
		
		for (int i = 0; i < instances.size(); i++) {
			
			CloudgeneInstance instance = instances.get(i);
			
			JSONObject server = client.getServerDetails(instance);
			JSONObject app = client.getDefaultApp(instance);
			CloudgeneUser user = client.getAuthUser(instance);
			CloudgeneApiToken token = client.verifyToken(instance, instance.getToken());

			data[i][0] = (i+1)+"";
			data[i][1] = server.getString("name");
			data[i][2] = instance.getHostname();
			data[i][3] = user.getUsername();
			data[i][4] = app.getString("version");
			data[i][5] =token.getExpire().toString();
		}

		String table = FlipTable.of(header, data) + "\n";		
		println(table);
		
		return 0;

	}

}
