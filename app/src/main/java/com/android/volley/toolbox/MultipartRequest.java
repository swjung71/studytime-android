package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orhanobut.logger.Logger;

import org.apache.http.HttpEntity;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.CharsetUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Thomas on 2015-07-29.
 */
public class MultipartRequest<T> extends Request<T> {

    private static final Serializer serializer = new Persister();

    private final Class<T> clazz;

    MultipartEntityBuilder entity = MultipartEntityBuilder.create();

    HttpEntity httpentity;

    private String FILE_PART_NAME = "TransferFile";

    private final Response.Listener<T> mListener;
    private final File mFilePart;
    private final Map<String, String> mStringPart;


    public MultipartRequest(String url, Class<T> clazz, File file, Map<String, String> params,
                            Response.Listener<T> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mFilePart = file;
        mStringPart = params;
        this.clazz = clazz;

        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        try {

            entity.setCharset(CharsetUtils.get("UTF-8"));

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        buildMultipartEntity();
        httpentity = entity.build();

        Logger.d(url);

        Logger.xml(params.get("xml"));
    }

    private void buildMultipartEntity() {

        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart, ContentType.create("image/jpeg"),
                mFilePart.getName()));

        if (mStringPart != null) {

            for (Map.Entry<String, String> entry : mStringPart.entrySet()) {

                entity.addTextBody(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public String getBodyContentType() {

        return httpentity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {

            httpentity.writeTo(bos);

        } catch (IOException e) {

            Logger.e("IOException writing to ByteArrayOutputStream");
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        try {

            String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            Logger.xml(data);

            return Response.success(serializer.read(clazz, data), HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {

            return Response.error(new ParseError(e));

        } catch (Exception e) {

            return Response.error(new VolleyError(e.getMessage()));
        }
    }

    @Override
    protected void deliverResponse(T response) {

        mListener.onResponse(response);
    }
}
