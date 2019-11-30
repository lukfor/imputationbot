package genepi.imputationbot.util.downloads;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Downloader implements IDownloadProgressListener {

	private List<Download> downloads = new Vector<Download>();

	private List<IDownloadProgressListener> listeners = new Vector<IDownloadProgressListener>();

	private Map<String, String> httpHeader = new HashMap<String, String>();

	public Downloader() {

	}

	public void setHttpHeader(Map<String, String> httpHeader) {
		this.httpHeader = httpHeader;
	}

	public void addDownload(Download download) {
		download.setHttpHeader(httpHeader);
		downloads.add(download);
	}

	public void downloadAll() throws Exception {
		for (Download download : downloads) {
			download(download);
		}
	}

	protected void download(Download download) throws Exception {

		try {

			if (download.existsTarget()) {

				if (download.isDownloadComplete()) {
					// skip download, file is already downloaded
					downloadSkipped(download);
				} else {
					// resume download and transfer remaining bytes
					download.resumeTransfer(this);
				}
			} else {
				// new download
				download.restartTransfer(this);
			}
		} catch (Exception e) {
			downloadError(download, e);
			throw e;
		}
	}

	public void addListener(IDownloadProgressListener listener) {
		listeners.add(listener);
	}

	public void downloadStarted(Download download) {
		for (IDownloadProgressListener listener : listeners) {
			listener.downloadStarted(download);
		}
	}

	public void downloadResumed(Download download) {
		for (IDownloadProgressListener listener : listeners) {
			listener.downloadResumed(download);
		}
	}

	public void downloadSkipped(Download download) {
		for (IDownloadProgressListener listener : listeners) {
			listener.downloadSkipped(download);
		}
	}

	public void downloadCompleted(Download download) {
		for (IDownloadProgressListener listener : listeners) {
			listener.downloadCompleted(download);
		}
	}

	public void downloadError(Download download, Exception exception) {
		for (IDownloadProgressListener listener : listeners) {
			listener.downloadError(download, exception);
		}
	}

	public void downloadProgress(Download download, byte[] buffer, int length) {
		for (IDownloadProgressListener listener : listeners) {
			listener.downloadProgress(download, buffer, length);
		}
	}

}
