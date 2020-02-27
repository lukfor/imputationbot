package genepi.imputationbot.util.uploads;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends FilterOutputStream {
    private IUploadProgressListener listener;
    private long transferred;
    private long totalBytes;
 
    public CountingOutputStream(
      OutputStream out, IUploadProgressListener listener, long totalBytes) {
        super(out);
        this.listener = listener;
        transferred = 0;
        this.totalBytes = totalBytes;
    }
 
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        transferred += len;
        listener.progress(getCurrentProgress());
    }
 
    @Override
    public void write(int b) throws IOException {
        out.write(b);
        transferred++;
        listener.progress(getCurrentProgress());
    }
 
    private float getCurrentProgress() {
        return ((float) transferred / totalBytes) * 100;
    }
}