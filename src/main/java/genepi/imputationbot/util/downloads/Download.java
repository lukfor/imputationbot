package genepi.imputationbot.util.downloads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

public class Download {

	private URL source;

	private File target;

	private long sourceSize = -1;

	private long targetSize = -1;

	private Map<String, String> httpHeader = new HashMap<String, String>();

	protected static final int DEFAULT_BUFFER_SIZE = 4 * 1024;
	protected static final int MAXIMUM_BUFFER_SIZE = 512 * 1024;
	protected static final int BUFFER_SEGMENT_SIZE = 4 * 1024;
	protected static final int MINIMUM_AMOUNT_OF_TRANSFER_CHUNKS = 100;

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

		OutputStream output = new FileOutputStream(target);
		HttpURLConnection conn = (HttpURLConnection) source.openConnection();
		for (String key : httpHeader.keySet()) {
			conn.setRequestProperty(key, httpHeader.get(key));
		}

		listener.downloadStarted(this);
		ReadableByteChannel input = Channels.newChannel(conn.getInputStream());
		ByteBuffer buffer = ByteBuffer.allocate(getBufferCapacityForTransfer(sourceSize));
		int halfBufferCapacity = buffer.capacity() / 2;

		long remaining = sourceSize < 0L ? Long.MAX_VALUE : sourceSize;
		while (remaining > 0L) {
			int read = input.read(buffer);
			if (read == -1) {
				// EOF, but some data has not been written yet.
				if (buffer.position() != 0) {
					buffer.flip();
					listener.downloadProgress(this, buffer.array(), buffer.limit());
					output.write(buffer.array(), 0, buffer.limit());
					buffer.clear();
				}

				break;
			}

			// Prevent minichunking / fragmentation: when less than half the buffer is
			// utilized,
			// read some more bytes before writing and firing progress.
			if (buffer.position() < halfBufferCapacity) {
				continue;
			}

			buffer.flip();
			listener.downloadProgress(this, buffer.array(), buffer.limit());
			output.write(buffer.array(), 0, buffer.limit());
			remaining -= buffer.limit();
			buffer.clear();

		}
		output.flush();

		input.close();
		output.close();

		listener.downloadCompleted(this);
	}

	protected int getBufferCapacityForTransfer(long numberOfBytes) {
		if (numberOfBytes <= 0L) {
			return DEFAULT_BUFFER_SIZE;
		}

		final long numberOfBufferSegments = numberOfBytes / (BUFFER_SEGMENT_SIZE * MINIMUM_AMOUNT_OF_TRANSFER_CHUNKS);
		final long potentialBufferSize = numberOfBufferSegments * BUFFER_SEGMENT_SIZE;
		if (potentialBufferSize > Integer.MAX_VALUE) {
			return MAXIMUM_BUFFER_SIZE;
		}
		return Math.min(MAXIMUM_BUFFER_SIZE, Math.max(DEFAULT_BUFFER_SIZE, (int) potentialBufferSize));
	}

}
