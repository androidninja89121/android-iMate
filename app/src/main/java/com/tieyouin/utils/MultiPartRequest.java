package com.tieyouin.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Morning on 11/25/2015.
 */

@SuppressWarnings("deprecation")
public class MultiPartRequest extends Request<String> {

    public Response.Listener<String> mListener = null;
    public Response.ErrorListener mEListener;

    private final File mFilePart;
    private final String mStringPart;
    private Map<String, String> parameters;
    private Map<String, String> headerParameters;

    MultipartEntity entity = new MultipartEntity();

    public MultiPartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> responseListener,
                            File file, String stringPart, Map<String, String> param, Map<String, String> hParam) {
        super(Method.POST, url, errorListener);

        mListener = responseListener;
        mEListener = errorListener;
        mFilePart = file;
        mStringPart = stringPart;
        parameters = param;
        headerParameters = hParam;

        buildMultipartEntity();
    }

    public MultiPartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> responseListener,
                            File file, String stringPart, Map<String, String> param) {
        super(Method.POST, url, errorListener);

        mListener = responseListener;
        mEListener = errorListener;
        mFilePart = file;
        mStringPart = stringPart;
        parameters = param;

        buildMultipartEntity();
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headerParameters;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {

            entity.writeTo(new CountingOutputStream(bos, mFilePart.length(), null));

        } catch ( IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }

        return bos.toByteArray();
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {

        mEListener.onErrorResponse(volleyError);
        return super.parseNetworkError(volleyError);
    }

    @Override
    protected void deliverResponse(String response) {

        mListener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {

        String resStr = "";
        try {
            resStr = new String(networkResponse.data);
        } catch (Exception e) {
        }
        return Response.success(resStr, getCacheEntry());
    }

    private void buildMultipartEntity() {
        entity.addPart(mStringPart, (ContentBody)new FileBody(mFilePart));
        try {
            for(String key : parameters.keySet())
                entity.addPart(key,
                        (ContentBody) new StringBody(URLEncoder.encode(parameters.get(key))));

        }catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    public static interface MultipartProgressListener {
        void tranferred(long transferred, int progress);
    }

    public static class  CountingOutputStream extends FilterOutputStream {

        private final MultipartProgressListener progListener;
        private long transfrered;
        private long fileLength;

        public CountingOutputStream(final OutputStream out, long fileLength, final MultipartProgressListener listner) {

            super(out);
            this.fileLength = fileLength;
            this.transfrered = 0;
            this.progListener = listner;
        }
    }

}
