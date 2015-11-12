package kr.co.digitalanchor.studytime.model.api;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.MultipartRequest;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import kr.co.digitalanchor.studytime.model.AddPackageModel;
import kr.co.digitalanchor.studytime.model.AdultFileResult;
import kr.co.digitalanchor.studytime.model.AllPackage;
import kr.co.digitalanchor.studytime.model.AllPackageResult;
import kr.co.digitalanchor.studytime.model.AllPackageResultForParent;
import kr.co.digitalanchor.studytime.model.Board;
import kr.co.digitalanchor.studytime.model.ChangePassModel;
import kr.co.digitalanchor.studytime.model.ChatRead;
import kr.co.digitalanchor.studytime.model.ChatReadResult;
import kr.co.digitalanchor.studytime.model.ChatSend;
import kr.co.digitalanchor.studytime.model.ChatSendResult;
import kr.co.digitalanchor.studytime.model.CheckPackageResult;
import kr.co.digitalanchor.studytime.model.ChildLoginResult;
import kr.co.digitalanchor.studytime.model.ChildRegResult;
import kr.co.digitalanchor.studytime.model.ChildRegister;
import kr.co.digitalanchor.studytime.model.CoinResult;
import kr.co.digitalanchor.studytime.model.Delete;
import kr.co.digitalanchor.studytime.model.ExceptionApp;
import kr.co.digitalanchor.studytime.model.ExceptionAppResult;
import kr.co.digitalanchor.studytime.model.FAQResult;
import kr.co.digitalanchor.studytime.model.GCMUpdate;
import kr.co.digitalanchor.studytime.model.GPSRequest;
import kr.co.digitalanchor.studytime.model.GPSResult;
import kr.co.digitalanchor.studytime.model.GPSResultParent;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.GetAdultDB;
import kr.co.digitalanchor.studytime.model.GetNotificationResult;
import kr.co.digitalanchor.studytime.model.GetVersion;
import kr.co.digitalanchor.studytime.model.GoodsResult;
import kr.co.digitalanchor.studytime.model.IconModel;
import kr.co.digitalanchor.studytime.model.LoginModel;
import kr.co.digitalanchor.studytime.model.NewNoticeResult;
import kr.co.digitalanchor.studytime.model.NewPassword;
import kr.co.digitalanchor.studytime.model.NoticesResult;
import kr.co.digitalanchor.studytime.model.OutParentRegister;
import kr.co.digitalanchor.studytime.model.ParentInfoChange;
import kr.co.digitalanchor.studytime.model.ParentLogin;
import kr.co.digitalanchor.studytime.model.ParentLoginResult;
import kr.co.digitalanchor.studytime.model.ParentModel;
import kr.co.digitalanchor.studytime.model.ParentOnOff;
import kr.co.digitalanchor.studytime.model.ParentPhoneInfo;
import kr.co.digitalanchor.studytime.model.ParentPrivacyInfo;
import kr.co.digitalanchor.studytime.model.ParentPrivacyInfoResult;
import kr.co.digitalanchor.studytime.model.ParentRegResult;
import kr.co.digitalanchor.studytime.model.ParentRegister;
import kr.co.digitalanchor.studytime.model.ParentWithdraw;
import kr.co.digitalanchor.studytime.model.SetCoin;
import kr.co.digitalanchor.studytime.model.db.VersionResult;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Created by Thomas on 2015-06-17.
 */
public class HttpHelper {

  /**
   * true : dev ; false : real
   */
  public static boolean isDev = true;

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

  public static String getImageURL(String path) {

    if (isDev) {

      return PROTOCOL_DEV + DOMAIN_DEV + path;

    } else {

      return PROTOCOL + DOMAIN + path;
    }
  }

  /**
   * Parent Login
   *
   * @param model params
   * @param listener response listener
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
   * 부모 정보 조회 -> 부모 개인 정보를 변경하기 위해 조회를 먼저 해야한다.
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

  public static SimpleXmlRequest getSyncChildData(LoginModel model, Listener listener,
                                                  ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<CheckPackageResult>(getURL() + "sync",
          CheckPackageResult.class, map, listener, errorListener);

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

  public static SimpleXmlRequest getSyncParentData(ParentModel model, Listener listener,
                                                   ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<ParentLoginResult>(getURL() + "parent/sync",
          ParentLoginResult.class, map, listener, errorListener);

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

  public static SimpleXmlRequest getUpdate(GCMUpdate model, Listener listener,
                                           ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<GeneralResult>(getURL() + "updateGCM",
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

  /**
   * package 목록 보내기 최초 앱을 깔았을 때 보내는 것
   */
  public static SimpleXmlRequest getAddAppList(AddPackageModel model, Listener listener,
                                               ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<AllPackageResult>(getURL() + "addAppList",
          AllPackageResult.class, map, listener, errorListener);

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
   * package 목록 업데이트하기 <p> 앱을 추가로 설치하거나, 삭제하거나, 업데이트할 때, 그리고 local db의 앱목록과 설치된 앱목록을 비교하여 cleaning 할
   * 때 보낼 필요가 있으면 보냄
   */
  public static SimpleXmlRequest getUpdateAppList(AllPackage model, Listener listener,
                                                  ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<AllPackageResult>(getURL() + "updateAppList",
          AllPackageResult.class, map, listener, errorListener);

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
   * 부모용 자식 앱의 설치된 앱 목록 받아오기
   */
  public static SimpleXmlRequest getPackageForParent(LoginModel model, Listener listener,
                                                     ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<AllPackageResultForParent>(getURL() + "parent/getAllPackage",
          AllPackageResultForParent.class, map, listener, errorListener);

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
   * 부모용 자녀 폰의 예외 앱 설정
   */
  public static SimpleXmlRequest getSettingExceptionApp(ExceptionApp model, Listener listener,
                                                        ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<GeneralResult>(getURL() + "parent/setExceptionApp",
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

  public static SimpleXmlRequest getExceptionApp(LoginModel model, Listener listener,
                                                 ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<ExceptionAppResult>(getURL() + "getExceptionApp",
          ExceptionAppResult.class, map, listener, errorListener);

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

  public static MultipartRequest getUploadIcon(IconModel model, File file, Listener listener,
                                               ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new MultipartRequest<GeneralResult>(getURL() + "updateIcon",
          GeneralResult.class, file, map, listener, errorListener);

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

  public static SimpleXmlRequest getAdultFileList(GetAdultDB model, Listener listener,
                                                  ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<AdultFileResult>(getURL() + "getAdultFile",
          AdultFileResult.class, map, listener, errorListener);

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

  public static SimpleXmlRequest getAlarmList(ParentModel model, Listener listener,
                                              ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<GetNotificationResult>(getURL() + "/parent/getAlarm",
          GetNotificationResult.class, map, listener, errorListener);

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


  public static SimpleXmlRequest getGoodItems(ParentModel model, Listener listener,
                                              ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<GoodsResult>(getURL() + "parent/getGoodItems",
          GoodsResult.class, map, listener, errorListener);

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
   * 위치 요청 부모 -> 자녀
   */
  public static SimpleXmlRequest getRequestGPS(GPSRequest model, Listener listener,
                                               ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<GeneralResult>(getURL() + "parent/requestGPS",
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

  /**
   * 자녀 결과 SPG 결과 보내기
   */
  public static SimpleXmlRequest getSendGPS(GPSResult model, Listener listener,
                                            ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<GeneralResult>(getURL() + "sendGPS",
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

  /**
   * 부모가 GPS 결과 받기
   */
  public static SimpleXmlRequest getGPS(GPSRequest model, Listener listener,
                                        ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<GPSResultParent>(getURL() + "parent/getGPS",
          GPSResultParent.class, map, listener, errorListener);

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

  public static SimpleXmlRequest getNewNotice(ParentModel model, Listener listener,
                                              ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<NewNoticeResult>(getURL() + "parent/newNotices",
          NewNoticeResult.class, map, listener, errorListener);

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

  public static SimpleXmlRequest getExternalLogin(OutParentRegister model, Listener listener,
                                                  ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<ParentLoginResult>(getURL() + "parent/outRegister",
          ParentLoginResult.class, map, listener, errorListener);

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

  public static SimpleXmlRequest getExternalPassword(ChangePassModel model, Listener listener,
                                                     ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<GeneralResult>(getURL() + "parent/setPass",
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

  public static SimpleXmlRequest getChildList(ParentModel model, Listener listener,
                                                     ErrorListener errorListener) {

    StringWriter writer = null;

    try {

      Serializer serializer = new Persister();

      writer = new StringWriter();

      serializer.write(model, writer);

      HashMap<String, String> map = new HashMap<>();

      map.put("xml", writer.toString());

      return new SimpleXmlRequest<ParentLoginResult>(getURL() + "getChildList",
          ParentLoginResult.class, map, listener, errorListener);

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
