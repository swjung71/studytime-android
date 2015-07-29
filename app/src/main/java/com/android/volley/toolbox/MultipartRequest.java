package com.android.volley.toolbox;

import com.android.volley.Response;

import java.util.Map;

/**
 * Created by Thomas on 2015-07-29.
 */
public class MultipartRequest extends SimpleXmlRequest {


    public MultipartRequest(String url, Class clazz, Map headers, Map params, Response.Listener listener, Response.ErrorListener errorListener) {
        super(url, clazz, headers, params, listener, errorListener);
    }
}
