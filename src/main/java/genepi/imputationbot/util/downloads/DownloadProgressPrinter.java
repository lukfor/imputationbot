package genepi.imputationbot.util.downloads;

public class DownloadProgressPrinter implements IDownloadProgressListener {

	private long transferedBytes = 0;

	private long currentProgress = -1;

	private long startWindow = 0;

	private long transferedBytesWindow = 0;

	private String speed = "";

	public static final int WINDOW_SIZE_MS = 2000;

	public void downloadStarted(Download download) {
		currentProgress = -1;
		startWindow = 0;
		transferedBytes = 0;
		transferedBytesWindow = 0;
		System.out.print("  Downloading from " + download.getSource() + "...\r");

	}

	public void downloadSkipped(Download download) {
		System.out.println("  Skip file " + download.getSource());
	}

	public void downloadResumed(Download download) {
		// TODO Auto-generated method stub

	}

	public void downloadProgress(Download download, byte[] buffer, int length) {
		transferedBytes += length;
		long progress = -1;
		try {
			progress = (long) (transferedBytes * 100.0) / download.getSourceSize();
		} catch (Exception e) {

		}

		long currentTime = System.currentTimeMillis();
		if (startWindow + WINDOW_SIZE_MS < currentTime) {
			long kbytes = (transferedBytesWindow / 1024) / ((currentTime - startWindow) / 1000);
			if (kbytes > 1024) {
				speed = String.format("%.1f MB/s", (kbytes / 1024.0));
			} else {
				speed = String.format("%d KB/s", kbytes);

			}
			transferedBytesWindow = 0;
			startWindow = currentTime;
		} else {
			transferedBytesWindow += length;
		}

		if (currentProgress != progress || transferedBytesWindow == 0) {
			System.out.print("  Downloading from " + download.getSource() + " [" + progress + "%, " + speed + "] \r");
			currentProgress = progress;
		}

	}

	public void downloadError(Download download, Exception exception) {
		System.out.println("  Downloading from " + download.getSource() + " failed.");
		//exception.printStackTrace();
	}

	public void downloadCompleted(Download download) {
		System.out.println("  Downloaded from " + download.getSource() + " ");
	}
}
