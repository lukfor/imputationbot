package genepi.imputationbot.util.downloads;

public interface IDownloadProgressListener {

	public void downloadStarted(Download download);

	public void downloadResumed(Download download);
	
	public void downloadSkipped(Download download);
	
	public void downloadCompleted(Download download);
	
	public void downloadError(Download download, Exception exception);
	
	public void downloadProgress(Download download, byte[] buffer, int length);
	
}
