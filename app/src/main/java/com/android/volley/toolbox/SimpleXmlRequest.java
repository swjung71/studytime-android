package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Thomas on 2015-06-16.
 */
public class SimpleXmlRequest<T> extends Request<T> {

    private static final Serializer serializer = new Persister();

    private final Class<T> clazz;

    private final Map<String, String> headers;

    private final Map<String, String> params;

    private final Listener<T> listener;

    /**
     * Make HTTP request and return a parsed object from Response
     * Invokes the other constructor.
     *
     * @see SimpleXmlRequest#SimpleXmlRequest(String, Class, Map, Listener, ErrorListener)
     */
    public SimpleXmlRequest(String url, Class<T> clazz, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {

        this(url, clazz, null, params, listener, errorListener);
    }

    /**
     * Make HTTP request and return a parsed object from XML Response
     *
     * @param url           URL of the request to make
     * @param clazz         Relevant class object
     * @param headers       Map of request headers
     * @param params        Map of request params
     * @param listener
     * @param errorListener
     */
    public SimpleXmlRequest(String url, Class<T> clazz, Map<String, String> headers, Map<String, String> params,
                            Listener<T> listener, ErrorListener errorListener) {

        super(Method.POST, url, errorListener);

        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {

        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        try {

            String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            System.out.println(data);

            return Response.success(serializer.read(clazz, data), HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {

            return Response.error(new ParseError(e));

        } catch (Exception e) {

            return Response.error(new VolleyError(e.getMessage()));
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        return params != null ? params : super.getParams();
    }
}
