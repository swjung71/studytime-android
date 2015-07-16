package kr.co.digitalanchor.studytime.model.api;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import kr.co.digitalanchor.studytime.model.Board;
import kr.co.digitalanchor.studytime.model.ChatRead;
import kr.co.digitalanchor.studytime.model.ChatReadResult;
import kr.co.digitalanchor.studytime.model.ChatSend;
import kr.co.digitalanchor.studytime.model.ChatSendResult;
import kr.co.digitalanchor.studytime.model.ChildLoginResult;
import kr.co.digitalanchor.studytime.model.ChildRegResult;
import kr.co.digitalanchor.studytime.model.ChildRegister;
import kr.co.digitalanchor.studytime.model.CoinResult;
import kr.co.digitalanchor.studytime.model.Delete;
import kr.co.digitalanchor.studytime.model.FAQResult;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.GetVersion;
import kr.co.digitalanchor.studytime.model.NewPassword;
import kr.co.digitalanchor.studytime.model.NoticesResult;
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
import kr.co.digitalanchor.studytime.model.db.VersionResult;

/**
 * Created by Thomas on 2015-06-17.
 */
public class HttpHelper {

    public static boolean isDev = false;

    /**
     * Dev Server url http://14.63.225.89/studytime-server
     */

    public static final String PROTOCOL = "https://";

    public static final String DOMAIN = "www.dastudytime.kr/";

    public static final String PATH = "studytime-server/";

    public static final String PROTOCOL_DEV = "http://";

    public static final String DOMAIN_DEV = "14.63.225.89/";

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

        StringWriter writer = new StringWriter();

        try {

            Serializer serializer = new Persister();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            Logger.e(getURL());

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

            serializer.write(model, writer);

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
    public static SimpleXmlRequest getUpdateCoin(SetCoin model,
                                                 Listener<CoinResult> listener,
                                                 ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<CoinResult>(getURL() + "parent/setCoin",
                    CoinResult.class, map, listener, errorListener);

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

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<ParentPrivacyInfoResult>(getURL() + "parent/getPrivacyInfo",
                    ParentPrivacyInfoResult.class, map, listener, errorListener);

        } catch (Exception e) {

            Logger.e(e.toString());

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

            serializer.write(model, writer);

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

    public static SimpleXmlRequest<CoinResult> getParentOnOff(ParentOnOff model,
                                                              Listener<CoinResult> listener,
                                                              ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<CoinResult>(getURL() + "parent/onOff",
                    CoinResult.class, map, listener, errorListener);

        } catch (Exception e) {

            Logger.e(e.toString());

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
     * Child
     */


    public static SimpleXmlRequest<ChildLoginResult> getChildLogin(ParentLogin model,
                                                                   Listener<ChildLoginResult> listener,
                                                                   ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<ChildLoginResult>(getURL() + "login",
                    ChildLoginResult.class, map, listener, errorListener);

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

    public static SimpleXmlRequest<ChildRegResult> getChildRegister(ChildRegister model,
                                                                    Listener<ChildRegResult> listener,
                                                                    ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<ChildRegResult>(getURL() + "childReg",
                    ChildRegResult.class, map, listener, errorListener);

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
     * 채팅 메세지 보내기
     *
     * @param model
     * @param listener
     * @param errorListener
     * @return
     */
    public static SimpleXmlRequest getSendChat(ChatSend model, Listener<ChatSendResult> listener,
                                               ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<ChatSendResult>(getURL() + "chat",
                    ChatSendResult.class, map, listener, errorListener);

        } catch (Exception e) {

            Logger.e(e.toString());

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

    public static SimpleXmlRequest getReadChat(ChatRead model, Listener<ChatReadResult> listener,
                                               ErrorListener errorListener) {
        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<ChatReadResult>(getURL() + "readChat",
                    ChatReadResult.class, map, listener, errorListener);

        } catch (Exception e) {

            Logger.e(e.getMessage());

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

    public static SimpleXmlRequest getVersion(GetVersion model, Listener<VersionResult> listener,
                                              ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<VersionResult>(getURL() + "getVersion",
                    VersionResult.class, map, listener, errorListener);

        } catch (Exception e) {

            Logger.e(e.toString());

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

    public static SimpleXmlRequest getNotice(Board model, Listener<NoticesResult> listener,
                                             ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<NoticesResult>(getURL() + "parent/getNotices",
                    NoticesResult.class, map, listener, errorListener);

        } catch (Exception e) {

            Logger.e(e.toString());

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

    public static SimpleXmlRequest getFAQ(Board model, Listener<FAQResult> listener,
                                          ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<FAQResult>(getURL() + "parent/getFAQ",
                    FAQResult.class, map, listener, errorListener);

        } catch (Exception e) {

            Logger.e(e.toString());

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

    public static SimpleXmlRequest getTemporaryPassword(NewPassword model,
                                                        Listener<GeneralResult> listener,
                                                        ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<GeneralResult>(getURL() + "parent/getNewPass",
                    GeneralResult.class, map, listener, errorListener);

        } catch (Exception e) {

            Logger.e(e.toString());

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

    public static SimpleXmlRequest getAllowDelete(Delete model, Listener listener,
                                                  ErrorListener errorListener) {

        StringWriter writer = null;

        try {

            Serializer serializer = new Persister();

            writer = new StringWriter();

            serializer.write(model, writer);

            HashMap<String, String> map = new HashMap<>();

            map.put("xml", writer.toString());

            return new SimpleXmlRequest<GeneralResult>(getURL() + "delete",
                    GeneralResult.class, map, listener, errorListener);

        } catch (Exception e) {

            Logger.e(e.toString());

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
