package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

/**
 * Created by Thomas on 2015-08-25.
 */
public class GsonRequest<T> extends Request<T> {

    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private final Gson gson;

    private final Class<T> clazz;

    private final Response.Listener<T> listener;
    private final String requestBody;

    public GsonRequest(String url, Class<T> clazz, String requestBody, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {

        this(Method.DEPRECATED_GET_OR_POST, url, clazz, requestBody, listener, errorListener);

    }

    public GsonRequest(int method, String url, Class<T> clazz, String requestBody,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {

        super(method, url, errorListener);

        this.listener = listener;
        this.requestBody = requestBody;
        this.clazz = clazz;

        this.gson = new Gson();

    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        try {

            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {

            return Response.error(new ParseError(e));

        } catch (JsonSyntaxException e) {

            return Response.error(new ParseError(e));
        }
    }

    /**
     * @deprecated Use {@link #getBodyContentType()}.
     */
    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * @deprecated Use {@link #getBody()}.
     */
    @Override
    public byte[] getPostBody() {
        return getBody();
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return requestBody == null ? null : requestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    requestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}
