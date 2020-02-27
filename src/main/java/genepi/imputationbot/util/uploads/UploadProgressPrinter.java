package genepi.imputationbot.util.uploads;

public class UploadProgressPrinter implements IUploadProgressListener {

	private float currentProgress = -1;

	public UploadProgressPrinter() {
		System.out.print("  Uploading files...\r");
	}

	@Override
	public void progress(float progress) {

		if (currentProgress != progress) {
			System.out.print("  Uploading files" + " [" + (int) progress + "%] \r");
			currentProgress = progress;
		}

	}

}
