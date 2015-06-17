package kr.co.digitalanchor.studytime.api;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.SimpleXmlRequest;

import java.util.HashMap;

import kr.co.digitalanchor.studytime.model.ParentLoginResult;
import kr.co.digitalanchor.studytime.model.ParentRegResult;

/**
 * Created by Thomas on 2015-06-17.
 */
public class HttpHelper {

    public static boolean isDev = true;

    /**
     * Dev Server url http://14.63.225.89/studytime-server
     */

    public static final String DOMAIN = "";

    public static final String PROTOCOL = "https://";

    public static final String PATH = "studytime-server/";

    public static final String DOMAIN_DEV = "14.63.225.89/";

    public static final String PROTOCOL_DEV = "http://";

    public static final String PATH_DEV = "studytime-server/";

    private static String getURL() {

        if (isDev) {

            return PROTOCOL_DEV + DOMAIN_DEV + PATH_DEV;

        } else {

            return PROTOCOL + DOMAIN + PATH;
        }
    }

    /**
     * @param map           params
     * @param listener      response listener
     * @param errorListener error listener
     * @return request
     */
    public static SimpleXmlRequest getParentLogin(HashMap map, Listener<ParentLoginResult> listener,
                                                  ErrorListener errorListener) {

        return new SimpleXmlRequest<ParentLoginResult>(getURL() + "parent/register",
                ParentLoginResult.class, map, listener, errorListener);
    }

    public static SimpleXmlRequest getParentRegister(HashMap map, Listener<ParentRegResult> listener,
                                                     ErrorListener errorListener) {

        return new SimpleXmlRequest<ParentRegResult>(getURL() + "parent/register",
                ParentRegResult.class, map, listener, errorListener);
    }
}
