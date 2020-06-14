package genepi.imputationbot.util.downloads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import genepi.imputationbot.client.CloudgeneException;

public class Download {

	private URL source;

	private File target;

	private long sourceSize = -1;

	private long targetSize = -1;

	protected static final int DEFAULT_BUFFER_SIZE = 4 * 1024;
	protected static final int MAXIMUM_BUFFER_SIZE = 512 * 1024;
	protected static final int BUFFER_SEGMENT_SIZE = 4 * 1024;
	protected static final int MINIMUM_AMOUNT_OF_TRANSFER_CHUNKS = 100;

	public Download(URL source, File target) {
		this.source = source;
		this.target = target;
	}

	public URL getSource() {
		return source;
	}

	public File getTarget() {
		return target;
	}

	protected long getSourceSize() throws URISyntaxException, ClientProtocolException, IOException, CloudgeneException {

		if (sourceSize == -1) {

			HttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(source.toString());
			HttpResponse response = httpclient.execute(httpget);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 204) {
				sourceSize = 0;
			} else if (statusCode == 200) {
				HttpEntity responseEntity = response.getEntity();
				sourceSize = responseEntity.getContentLength();
			} else {
				System.out.println("HTTP-Code: " + statusCode);
				System.out.println("HTTP-Content: " + EntityUtils.toString(response.getEntity()));
				throw new CloudgeneException(400, "File could not be downloaded. Max download limit exceeded.");
			}

		}
		return sourceSize;
	}

	protected long getTargetSize() {
		if (targetSize == -1 && target.exists()) {
			targetSize = target.length();
		}
		return targetSize;
	}

	public boolean existsTarget() {
		return target.exists();
	}

	public boolean isDownloadComplete()
			throws URISyntaxException, ClientProtocolException, IOException, CloudgeneException {
		if (getSourceSize() == -1 || getTargetSize() == -1) {
			return false;
		} else {
			return (getSourceSize() == getTargetSize());
		}
	}

	public void resumeTransfer(IDownloadProgressListener listener)
			throws URISyntaxException, FileNotFoundException, IOException, CloudgeneException {
		// listener.downloadResumed(this);
		restartTransfer(listener);
		// listener.downloadCompleted(this);
	}

	public void restartTransfer(IDownloadProgressListener listener)
			throws URISyntaxException, FileNotFoundException, IOException, CloudgeneException {

		// delete target on restart
		if (target.exists()) {
			target.delete();
		}

		// create parent directory
		if (!target.getParentFile().exists()) {
			target.getParentFile().mkdirs();
		}
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(source.toString());
		// httpget.addHeader("X-Auth-Token", instance.getToken());

		// Execute and get the response.
		HttpResponse response = httpclient.execute(httpget);
		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode == 204) {
			// write empty file.
			OutputStream output = new FileOutputStream(target);
			output.close();
			return;
		} else if (statusCode != 200) {
			System.out.println("HTTP-Code: " + statusCode);
			System.out.println("HTTP-Content: " + EntityUtils.toString(response.getEntity()));			
			throw new CloudgeneException(400, "File could not be downloaded. Max download limit exceeded.");
		}

		HttpEntity responseEntity = response.getEntity();
		long sourceSize = getSourceSize();

		listener.downloadStarted(this);
		ReadableByteChannel input = Channels.newChannel(responseEntity.getContent());
		ByteBuffer buffer = ByteBuffer.allocate(getBufferCapacityForTransfer(sourceSize));
		int halfBufferCapacity = buffer.capacity() / 2;

		OutputStream output = new FileOutputStream(target);

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
