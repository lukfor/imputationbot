package genepi.imputationbot.client;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import genepi.imputationbot.util.AnsiColors;
import genepi.io.FileUtil;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class CloudgeneJob {

	private JSONObject job;

	public CloudgeneJob(JSONObject job) {
		this.job = job;
	}

	public void downloadAll(CloudgeneClient client, String outputFolder)
			throws JSONException, IOException, InterruptedException, ZipException {
		downloadAll(client, outputFolder, null);
	}

	public void downloadAll(CloudgeneClient client, String outputFolder, String password)
			throws JSONException, IOException, InterruptedException, ZipException {

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
			System.out.println("  Downloading file " + path + " (" + (i + 1) + "/" + urls.size() + ")");

			if (outputFolder != null) {
				localPath = localPath.replaceAll(getId(), outputFolder);
			}
			File file = new File(localPath);
			FileUtil.createDirectory(file.getParent());
			client.downloadResults(path, localPath);

			// encrypt if file is zip
			if (password != null && localPath.endsWith(".zip")) {
				System.out.println("  Decrypting file " + path + "...");
				File localFile = new File(localPath);
				ZipFile zipFile = new ZipFile(localFile);
				zipFile.setPassword(password);
				zipFile.extractAll(localFile.getParent());
			}

		}

	}

	public String getId() {
		return job.getString("id");
	}

	public String getApplication() {
		return job.getString("application");
	}

	public int getQueuePosition() {
		return job.getInt("positionInQueue");
	}

	public Date getSubmittedOn() {
		return new Date(job.getLong("submittedOn"));
	}

	public long getExecutionTime() {
		return ((job.getLong("endTime") - job.getLong("startTime")) / 1000);
	}

	public boolean isRunning() {
		return job.getInt("state") == 1 || job.getInt("state") == 2 || job.getInt("state") == 3;
	}

	public String getJobStateAsText() {
		int state = job.getInt("state");
		switch (state) {
		case 1:
			return AnsiColors.makeBlue("Waiting");
		case 2:
			return AnsiColors.makeBlue("Running");
		case 3:
			return AnsiColors.makeBlue("Exporting");
		case 4:
			return AnsiColors.makeGreen("Success");
		case 5:
			return AnsiColors.makeRed("Failed");
		case 6:
			return AnsiColors.makeRed("Canceled");
		case 7:
			return AnsiColors.makeGray("Retired");
		case 8:
			return AnsiColors.makeGreen("Success");
		case 9:
			return AnsiColors.makeRed("Failed");
		case 10:
			return AnsiColors.makeGray("Deleted");
		case -1:
			return AnsiColors.makeGray("Dead");
		}
		return AnsiColors.makeGray("?");
	}

	@Override
	public String toString() {
		String content = "Job " + getId() + "\n";
		return content;
	}

}
