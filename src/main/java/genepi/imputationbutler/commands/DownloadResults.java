package genepi.imputationbutler.commands;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbutler.client.CloudgeneClient;
import genepi.imputationbutler.client.CloudgeneClientConfig;
import genepi.io.FileUtil;

public class DownloadResults extends BaseCommand {

	public DownloadResults(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addParameter("job", "job id");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public int run() {
		
		String id = getValue("job").toString();
		
		try {
			CloudgeneClientConfig config = readConfig();
			CloudgeneClient client = new CloudgeneClient(config);
			
			JSONObject job = client.getJobDetails(id);

			List<String> urls = new Vector<String>();
			
			JSONArray outputs = job.getJSONArray("outputParams");
			for (int i = 0; i < outputs.length(); i++) {
				JSONObject output = outputs.getJSONObject(i);
				JSONArray files = output.getJSONArray("files");
				for (int j = 0; j < files.length(); j++) {
					String path = files.getJSONObject(j).getString("path");
					urls.add(path);
				}
			}
		
			
			for (int i = 0; i < urls.size(); i++) {
				String path = urls.get(i);
				String localPath = path.replaceAll("/local/", "/vcfs/").replaceAll("/logfile/", "/logs/").replaceAll("/statisticDir/", "/statistics/").replaceAll("/qcreport/", "/statistics/");
				System.out.println("Downloading file " + path +  " (" + i + "/" + urls.size() + ")");
				File file = new File(localPath);
				FileUtil.createDirectory(file.getParent());
				client.downloadResults(path, localPath);
			}
			
			
			return 0;
		} catch (Exception e) {

			error(e.toString());

			return 1;

		}
	}

}
