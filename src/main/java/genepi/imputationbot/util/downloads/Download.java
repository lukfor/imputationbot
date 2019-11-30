package genepi.imputationbot.util.downloads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;

public class Download {

	private URL source;

	private File target;

	private long sourceSize = -1;

	private long targetSize = -1;

	private Map<String, String> httpHeader = new HashMap<String, String>();

	public Download(URL source, File target) {
		this.source = source;
		this.target = target;
	}

	public void setHttpHeader(Map<String, String> httpHeader) {
		this.httpHeader = httpHeader;
	}

	public URL getSource() {
		return source;
	}

	public File getTarget() {
		return target;
	}

	public long getSourceSize() throws URISyntaxException {

		if (sourceSize == -1) {

			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) source.openConnection();
				for (String key : httpHeader.keySet()) {
					conn.setRequestProperty(key, httpHeader.get(key));
				}
				conn.setRequestMethod("HEAD");
				sourceSize = conn.getContentLengthLong();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

		}
		return sourceSize;
	}

	public long getTargetSize() {
		if (targetSize == -1 && target.exists()) {
			targetSize = target.length();
		}
		return targetSize;
	}

	public boolean existsTarget() {
		return target.exists();
	}

	public boolean isDownloadComplete() throws URISyntaxException {
		if (getSourceSize() == -1 || getTargetSize() == -1) {
			return false;
		} else {
			return (getSourceSize() == getTargetSize());
		}
	}

	public void resumeTransfer(IDownloadProgressListener listener)
			throws URISyntaxException, FileNotFoundException, IOException {
		// listener.downloadResumed(this);
		restartTransfer(listener);
		// listener.downloadCompleted(this);
	}

	public void restartTransfer(IDownloadProgressListener listener)
			throws URISyntaxException, FileNotFoundException, IOException {

		// delete target on restart
		if (target.exists()) {
			target.delete();
		}

		// create parent directory
		if (!target.getParentFile().exists()) {
			target.getParentFile().mkdirs();
		}

		listener.downloadStarted(this);
		ClientResource resource = new ClientResource(source.toURI());

		Series<Header> requestHeader = (Series<Header>) resource.getRequest().getAttributes()
				.get(HeaderConstants.ATTRIBUTE_HEADERS);
		if (requestHeader == null) {
			requestHeader = new Series(Header.class);
			resource.getRequest().getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS, requestHeader);
		}
		for (String key : httpHeader.keySet()) {
			requestHeader.add(key, httpHeader.get(key));
		}

		resource.get();
		resource.getResponseEntity().write(new FileOutputStream(target));
		resource.release();

		listener.downloadCompleted(this);
	}

}
