package kr.co.digitalanchor.studytime.model.api;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.SimpleXmlRequest;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.ParentInfoChange;
import kr.co.digitalanchor.studytime.model.ParentLogin;
import kr.co.digitalanchor.studytime.model.ParentLoginResult;
import kr.co.digitalanchor.studytime.model.ParentOnOff;
import kr.co.digitalanchor.studytime.model.ParentPhoneInfo;
import kr.co.digitalanchor.studytime.model.ParentPrivacyInfo;
import kr.co.digitalanchor.studytime.model.ParentPrivacyInfoResult;
import kr.co.digitalanchor.studytime.model.ParentRegResult;
import kr.co.digitalanchor.studytime.model.ParentRegister;
import kr.co.digitalanchor.studytime.model.ParentWithdraw;
import kr.co.digitalanchor.studytime.model.SetCoin;

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

    public static final int SUCCESS = 0;

    private static String getURL() {

        if (isDev) {

            return PROTOCOL_DEV + DOMAIN_DEV + PATH_DEV;

        } else {

            return PROTOCOL + DOMAIN + PATH;
        }
    }

    /**
     * Parent Login
     *
     * @param model         params
     * @param listener      response listener
     * @param errorListener error listener
     * @return request
     */
    public static SimpleXmlRequest getParentLogin(ParentLogin model,
                                                  Listener<ParentLoginResult> listener,
                                                  ErrorListener errorListener) {

        StringWriter writer = new StringWriter();;

        try {

            Serializer serializer = new Persister();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<ParentLoginResult>(getURL() + "parent/login",
                    ParentLoginResult.class, map, listener, errorListener);

        } catch (Exception e) {

            return null;

        } finally {

            if (writer != null) {

                try {

                    writer.close();

                } catch (IOException e) {


                }

                writer = null;
            }
        }
    }

    /**
     * Parent Register
     *
     * @param model
     * @param listener
     * @param errorListener
     * @return
     */
    public static SimpleXmlRequest getParentRegister(ParentRegister model,
                                                    Listener<ParentRegResult> listener,
                                                    ErrorListener errorListener) {

        StringWriter writer = new StringWriter();

        try {

            Serializer serializer = new Persister();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<ParentRegResult>(getURL() + "parent/register",
                    ParentRegResult.class, map, listener, errorListener);

        } catch (Exception e) {

            return null;

        } finally {

            if (writer != null) {

                try {

                    writer.close();

                } catch (IOException e) {

                }

                writer = null;
            }
        }
    }

    public static SimpleXmlRequest getParentPhoneInfo(ParentPhoneInfo model,
                                                      Listener<GeneralResult> listener,
                                                      ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<GeneralResult>(getURL() + "parent/phoneInfo",
                    GeneralResult.class, map, listener, errorListener);

        } catch (Exception e) {

            return null;

        } finally {

            if (writer != null) {

                try {

                    writer.close();

                } catch (IOException e) {


                }

                writer = null;
            }
        }
    }

    /**
     * 부모 회원 탈퇴
     *
     * @param model
     * @param listener
     * @param errorListener
     * @return
     */
    public static SimpleXmlRequest getParentWithdraw(ParentWithdraw model,
                                                     Listener<GeneralResult> listener,
                                                     ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<GeneralResult>(getURL() + "parent/withdraw",
                    GeneralResult.class, map, listener, errorListener);

        } catch (Exception e) {

            return null;

        } finally {

            if (writer != null) {

                try {

                    writer.close();

                } catch (IOException e) {


                }

                writer = null;
            }
        }
    }

    /**
     * 부모 하트 변경
     *
     * @param model
     * @param listener
     * @param errorListener
     * @return
     */
    public static SimpleXmlRequest getManageCoin(SetCoin model,
                                                 Listener<GeneralResult> listener,
                                                 ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<GeneralResult>(getURL() + "parent/setCoin",
                    GeneralResult.class, map, listener, errorListener);

        } catch (Exception e) {

            return null;

        } finally {

            if (writer != null) {

                try {

                    writer.close();

                } catch (IOException e) {


                }

                writer = null;
            }
        }
    }

    /**
     * 부모 정보 조회
     * -> 부모 개인 정보를 변경하기 위해 조회를 먼저 해야한다.
     *
     * @param model
     * @param listener
     * @param errorListener
     * @return
     */
    public static SimpleXmlRequest getParentInfo(ParentPrivacyInfo model,
                                                 Listener<ParentPrivacyInfoResult> listener,
                                                 ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<ParentPrivacyInfoResult>(getURL() + "parent/getPrivacyInfo",
                    ParentPrivacyInfoResult.class, map, listener, errorListener);

        } catch (Exception e) {

            return null;

        } finally {

            if (writer != null) {

                try {

                    writer.close();

                } catch (IOException e) {


                }

                writer = null;
            }
        }
    }

    /**
     * 부모 정보 변경
     *
     * @param model
     * @param listener
     * @param errorListener
     * @return
     */
    public static SimpleXmlRequest getParentModifyInfo(ParentInfoChange model,
                                                       Listener<GeneralResult> listener,
                                                       ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<GeneralResult>(getURL() + "parent/changePrivacyInfo",
                    GeneralResult.class, map, listener, errorListener);

        } catch (Exception e) {

            return null;

        } finally {

            if (writer != null) {

                try {

                    writer.close();

                } catch (IOException e) {


                }

                writer = null;
            }
        }
    }

    public static SimpleXmlRequest<GeneralResult> getParentOnOff(ParentOnOff model,
                                                                 Listener<GeneralResult> listener,
                                                                 ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<GeneralResult>(getURL() + "parent/onOff",
                    GeneralResult.class, map, listener, errorListener);

        } catch (Exception e) {

            return null;

        } finally {

            if (writer != null) {

                try {

                    writer.close();

                } catch (IOException e) {


                }

                writer = null;
            }
        }
    }
}
