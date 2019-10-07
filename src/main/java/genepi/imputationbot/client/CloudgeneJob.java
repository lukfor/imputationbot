package genepi.imputationbot.client;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import genepi.io.FileUtil;

public class CloudgeneJob {

	private JSONObject job;

	public CloudgeneJob(JSONObject job) {
		this.job = job;
	}

	public void downloadAll(CloudgeneClient client) throws JSONException, IOException, InterruptedException {

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
			String localPath = path.replaceAll("/local/", "/vcfs/").replaceAll("/logfile/", "/logs/")
					.replaceAll("/statisticDir/", "/statistics/").replaceAll("/qcreport/", "/statistics/");
			System.out.println("Downloading file " + path + " (" + i + "/" + urls.size() + ")");
			File file = new File(localPath);
			FileUtil.createDirectory(file.getParent());
			client.downloadResults(path, localPath);
		}

	}

}
