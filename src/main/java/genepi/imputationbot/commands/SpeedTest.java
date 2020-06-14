package genepi.imputationbot.commands;

import java.io.File;
import java.net.URL;

import genepi.base.Tool;
import genepi.imputationbot.util.downloads.Download;
import genepi.imputationbot.util.downloads.DownloadProgressPrinter;
import genepi.imputationbot.util.downloads.Downloader;

public class SpeedTest extends BaseCommand {

	public SpeedTest(String... args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addOptionalParameter("output", "Folder where to store all downloaded files (default location: ./JOB_ID)",
				Tool.STRING);
	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		String[] files = getRemainingArgs();

		if (files.length == 0) {
			error("Please provide a http link to test download speed.");
			return 1;
		}

		long start = System.currentTimeMillis();

		File file = null;
		if (getValue("output") != null) {
			file = new File(getValue("output").toString());
		} else {
			file = new File("download.zip");
		}

		Download download = new Download(new URL(files[0]), file.getAbsoluteFile());

		Downloader downloader = new Downloader();
		downloader.addDownload(download);
		downloader.addListener(new DownloadProgressPrinter());
		downloader.downloadAll();

		long end = System.currentTimeMillis();

		System.out.println("\nExecution Time: " + formatTime((end - start) / 1000) + "\n\n");

		return 0;

	}

	public String formatTime(long timeInSeconds) {
		return String.format("%d min, %d sec", (timeInSeconds / 60), (timeInSeconds % 60));
	}

}
