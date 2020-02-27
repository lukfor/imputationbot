package genepi.imputationbot.util.uploads;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

public class ProgressEntityWrapper extends HttpEntityWrapper {
    private IUploadProgressListener listener;
 
    public ProgressEntityWrapper(HttpEntity entity, IUploadProgressListener listener) {
        super(entity);
        this.listener = listener;
    }
 
    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        super.writeTo(new CountingOutputStream(outstream, listener, getContentLength()));
    }
}