package genepi.imputationbot.commands;

import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneApiToken;
import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneException;
import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.client.CloudgeneUser;
import genepi.imputationbot.util.AnsiColors;
import genepi.imputationbot.util.FlipTable;

public class ListInstances extends BaseCommand {

	private String[][] data;

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
		if (instances.isEmpty()) {
			error("No instances found. Please run 'imputationbot add-instance' and enter your API Token");
			return 1;
		}
		
		CloudgeneClient client = getClient(false);

		String[] header = new String[6];
		header[0] = "ID";
		header[1] = "Name";
		header[2] = "Hostname";
		header[3] = "Username";
		header[4] = "Version";
		header[5] = "Token expires on";

		data = new String[instances.size()][header.length];

		for (int i = 0; i < instances.size(); i++) {

			CloudgeneInstance instance = instances.get(i);

			data[i][0] = (i + 1) + "";

			try {
			CloudgeneApiToken token = client.verifyToken(instance, instance.getToken());
			if (token.isValid() && !token.isExpired()) {
				JSONObject server = client.getServerDetails(instance);
				JSONObject app = client.getDefaultApp(instance);
				CloudgeneUser user = client.getAuthUser(instance);
				data[i][1] = server.getString("name");
				data[i][2] = instance.getHostname();
				data[i][3] = user.getUsername();
				data[i][4] = app.getString("version");
				data[i][5] = AnsiColors.green(token.getExpire().toString());
			} else {
				data[i][1] = "-";
				data[i][2] = instance.getHostname();
				data[i][3] = token.getUsername();
				data[i][4] = "-";
				data[i][5] = AnsiColors.red(token.getExpire().toString());
			}
			}catch (CloudgeneException e) {
				data[i][1] = "-";
				data[i][2] = instance.getHostname();
				data[i][3] = "-";
				data[i][4] = "-";
				data[i][5] = AnsiColors.red(e.getMessage());
			}
		}

		String table = FlipTable.of(header, data) + "\n";
		println(table);

		return 0;

	}

	public String[][] getData() {
		return data;
	}

}
