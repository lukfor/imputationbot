package genepi.imputationbot.client;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbot.util.AnsiColors;
import genepi.imputationbot.util.downloads.Download;
import genepi.imputationbot.util.downloads.DownloadProgressPrinter;
import genepi.imputationbot.util.downloads.Downloader;
import genepi.io.FileUtil;
import net.lingala.zip4j.core.ZipFile;

public class CloudgeneJob {

	private JSONObject job;

	private CloudgeneInstance instance;

	public CloudgeneJob(JSONObject job, CloudgeneInstance instance) {
		this.job = job;
		this.instance = instance;
	}

	public void downloadAll(CloudgeneClient client, String outputFolder) throws Exception {
		downloadAll(client, outputFolder, null);
	}

	public void downloadAll(CloudgeneClient client, String outputFolder, String password) throws Exception {

		if (isRetired()) {
			throw new CloudgeneException(400, "File could not be downloaded. Job is retired."); 
		}
		
		if (!isSuccessful()) {
			throw new CloudgeneException(400, "File could not be downloaded. Job execution was not successfull."); 
		}
		
		JSONArray outputs = job.getJSONArray("outputParams");

		Downloader downloader = new Downloader();
		downloader.addListener(new DownloadProgressPrinter());

		List<File> zipFiles = new Vector<File>();

		for (int i = 0; i < outputs.length(); i++) {
			JSONObject output = outputs.getJSONObject(i);
			JSONArray files = output.getJSONArray("files");
			for (int j = 0; j < files.length(); j++) {

				JSONObject file = files.getJSONObject(j);

				String hash = file.getString("hash");
				String name = file.getString("name");

				String localPath = FileUtil.path(outputFolder, output.getString("name"), name);

				URL source = new URL(instance.getHostname() + "/share/results/" + hash + "/" + name);
				File target = new File(localPath);
				downloader.addDownload(new Download(source, target));

				if (password != null && localPath.endsWith(".zip")) {
					zipFiles.add(target);
				}

			}
		}

		downloader.downloadAll();

		// encrypt if file is zip
		for (File file : zipFiles) {
			System.out.println("  Decrypting file " + file.getAbsolutePath() + "...");
			ZipFile zipFile = new ZipFile(file);
			zipFile.setPassword(password);
			zipFile.extractAll(file.getParent());
		}

	}

	public String getId() {
		return job.getString("id");
	}

	public String getName() {
		return job.getString("name");
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
		if (isRunning()) {
			return ((System.currentTimeMillis() - job.getLong("startTime")) / 1000);
		} else {
			return ((job.getLong("endTime") - job.getLong("startTime")) / 1000);
		}
	}

	public boolean isRunning() {
		return job.getInt("state") == 1 || job.getInt("state") == 2 || job.getInt("state") == 3;
	}
	
	public boolean isSuccessful() {
		return job.getInt("state") == 4;
	}

	public boolean isRetired() {
		return job.getInt("state") == 7;
	}

	
	public CloudgeneInstance getInstance() {
		return instance;
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
