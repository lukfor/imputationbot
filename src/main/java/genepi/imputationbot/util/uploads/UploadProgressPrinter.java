package genepi.imputationbot.util.uploads;

public class UploadProgressPrinter implements IUploadProgressListener {

	private int currentProgress = -1;

	private long lastUpdate = 0;

	public static final int FRAME_TIME = 250;

	public UploadProgressPrinter() {
		System.out.print("  Uploading files...\r");
	}

	@Override
	public void progress(float progress) {

		if (currentProgress != (int) progress & ((System.currentTimeMillis() - lastUpdate) > FRAME_TIME)) {
			System.out.print("  Uploading files" + " [" + (int) progress + "%] \r");
			currentProgress = (int) progress;
			lastUpdate = System.currentTimeMillis();
		}

	}

}
