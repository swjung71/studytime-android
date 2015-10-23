package kr.co.digitalanchor.studytime.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.orhanobut.logger.Logger;
import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.control.ListChildActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.dialog.SettingPasswordDialog;
import kr.co.digitalanchor.studytime.model.ChangePassModel;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.NaverUserInfo;
import kr.co.digitalanchor.studytime.model.NewPassword;
import kr.co.digitalanchor.studytime.model.OutParentRegister;
import kr.co.digitalanchor.studytime.model.ParentLogin;
import kr.co.digitalanchor.studytime.model.ParentLoginResult;
import kr.co.digitalanchor.studytime.model.ParentPhoneInfo;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.signup.SignUpActivity;
import kr.co.digitalanchor.utils.StringValidator;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-10.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

  private final int REQUEST_LOGIN = 50001;
  private final int COMPLETE_LOGIN = 50002;
  private final int REQUEST_PARENT_INFO = 50003;
  private final int REQUEST_FIND_PASSWORD = 50004;
  private final int REQUEST_EXTENAL_INPUT_PASSWORD = 50005;
  private final int REQUEST_NAVER_LOGIN = 50006;

  private Context mContext;

  /** 네이버 로그인 인스턴스 */
  private OAuthLogin mOAuthLoginInstance;

  /** 네이버 로그인 버튼 */
  private OAuthLoginButton mOAuthLoginButton;

  EditText mEditEmailAddr;

  EditText mEditPassword;

  View mContainerButtons;

  View mContainerBoxes;

  Button mButtonSignup;

  Button mButtonLogin;

  Button mButtonFindPwd;

  DBHelper mHelper;

  ParentLoginResult parentLoginResult;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_parent_login_ext);

    OAuthLoginDefine.DEVELOPER_VERSION = true;

    mContext = this;

    initData();

    initView();

    mHelper = new DBHelper(mContext);

  }

  private void initData() {

    mOAuthLoginInstance = OAuthLogin.getInstance();

    mOAuthLoginInstance.init(mContext,
        StaticValues.OAUTH_NAVER_CLIENT_ID,
        StaticValues.OAUTH_NAVER_CLIENT_SECRET,
        StaticValues.OAUTH_NAVER_CLIENT_NAME);
  }

  private void initView() {

    mEditEmailAddr = (EditText) findViewById(R.id.editEmailAddr);

    mEditPassword = (EditText) findViewById(R.id.editPassword);

    mButtonLogin = (Button) findViewById(R.id.buttonLogin);
    mButtonLogin.setOnClickListener(this);

    mButtonSignup = (Button) findViewById(R.id.buttonSignUp);
    mButtonSignup.setOnClickListener(this);

    mButtonFindPwd = (Button) findViewById(R.id.buttonFindPwd);
    mButtonFindPwd.setOnClickListener(this);

    mContainerButtons = findViewById(R.id.container_login_buttons);

    mContainerBoxes = findViewById(R.id.container_login_textboxes);

    mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
    mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
  }

  @Override
  protected void onHandleMessage(Message msg) {

    Bundle bundle = null;

    switch (msg.what) {

      case REQUEST_EXTENAL_INPUT_PASSWORD:

        bundle = msg.getData();
        String pwd = bundle.getString("pwd");

        Logger.d(pwd);

        requestInputExternalPass(pwd);

        break;

      case REQUEST_NAVER_LOGIN:

        bundle = msg.getData();

        NaverUserInfo info = bundle.getParcelable("info");

        Logger.d(info.getName());

        if (info == null) {

          dismissLoading();

          break;

        }

        requestExternalLogin(info);

        break;

      case REQUEST_LOGIN:

        requestParentLogin();

        break;

      case COMPLETE_LOGIN:

        completeLogin();

        break;

      case REQUEST_PARENT_INFO:

        requestPhoneInfo();

        break;

      case REQUEST_FIND_PASSWORD:

        requestParentFindPwd();

        break;

      default:

        break;
    }
  }

  @Override
  public void onBackPressed() {

    if (mContainerButtons.getVisibility() == View.VISIBLE) {

      super.onBackPressed();

    } else {

      mContainerButtons.setVisibility(View.VISIBLE);

      mContainerBoxes.setVisibility(View.GONE);

      mButtonSignup.setVisibility(View.VISIBLE);

      mButtonFindPwd.setVisibility(View.GONE);
    }
  }


  @Override
  public void onClick(View v) {

    if (isDuplicateRuns()) {

      return;
    }

    switch (v.getId()) {

      case R.id.buttonSignUp:

        showSignUp();

        break;

      case R.id.buttonLogin:

        if (mContainerButtons.getVisibility() == View.VISIBLE) {

          mContainerButtons.setVisibility(View.GONE);

          mContainerBoxes.setVisibility(View.VISIBLE);

          mButtonSignup.setVisibility(View.GONE);

          mButtonFindPwd.setVisibility(View.VISIBLE);

        } else if (isValidateLoginInfo()) {

          sendEmptyMessage(REQUEST_LOGIN);

        }

        break;

      case R.id.buttonFindPwd:

        if (isValidateEmailInfo()) {

          sendEmptyMessage(REQUEST_FIND_PASSWORD);
        }

        break;

      default:

        break;
    }
  }

  private boolean isValidateLoginInfo() {

    String msg = null;
    String temp = null;

    do {

      temp = mEditEmailAddr.getText().toString();

      if (TextUtils.isEmpty(temp)) {

        msg = "경고 문구 : 이메일 미 입력";

        break;

      }

      if (!StringValidator.isEmail(temp)) {

        msg = "경고 문구 : 이메일 형식 틀림";

        break;
      }

      temp = mEditPassword.getText().toString();

      if (TextUtils.isEmpty(temp)) {

        msg = "경고 문구 : 비밀번호 미 입력";

        break;
      }

    } while (false);

    if (TextUtils.isEmpty(msg)) {

      return true;

    } else {

      Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

      return false;
    }
  }

  private void requestUserInfoFromNaver() {

    showLoading();

    new RequestNaverApiTask().execute();
  }

  private void requestParentLogin() {

    showLoading();

    ParentLogin model = new ParentLogin();

    model.setEmail(mEditEmailAddr.getText().toString());

    model.setPassword(mEditPassword.getText().toString());

    SimpleXmlRequest request = HttpHelper.getParentLogin(model, new Response.Listener<ParentLoginResult>() {

      @Override
      public void onResponse(ParentLoginResult res) {

        Logger.d(res.toString());

        switch (res.getResultCode()) {

          case SUCCESS:

            mHelper.insertAccount(res.getParentID(), 1, res.getName(), res.getCoin(), res.getEmail());

            mHelper.insertChildren(res.getChildren());

            sendEmptyMessage(REQUEST_PARENT_INFO);

            break;

          default:

            handleResultCode(res.getResultCode(), res.getResultMessage());

            break;
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {

        handleError(error);

      }
    });

    if (request != null) {

      mQueue.add(request);

    } else {

      dismissLoading();
    }
  }

  private void requestInputExternalPass(String pwd) {

    showLoading();

    ChangePassModel model = new ChangePassModel();

    model.setEmail(parentLoginResult.getEmail());

    model.setNewPass(pwd);

    SimpleXmlRequest request = HttpHelper.getExternalPassword(model,
        new Response.Listener<GeneralResult>() {
          @Override
          public void onResponse(GeneralResult res) {

            switch (res.getResultCode()) {

              case SUCCESS:

                mHelper.insertAccount(parentLoginResult.getParentID(), 1, parentLoginResult.getName(), parentLoginResult.getCoin(), parentLoginResult.getEmail());

                mHelper.insertChildren(parentLoginResult.getChildren());

                dismissLoading();

                sendEmptyMessage(COMPLETE_LOGIN);

                break;

              default:

                mOAuthLoginInstance.logout(mContext);

                handleResultCode(res.getResultCode(), res.getResultMessage());

                break;
            }
          }
        }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

            handleError(error);
          }
        });

    addRequest(request);
  }

  private void completeLogin() {

    showMain();

  }

  private void requestExternalLogin(NaverUserInfo info) {

    dismissLoading();

    Toast.makeText(getApplicationContext(), info.getEmail(), Toast.LENGTH_SHORT).show();

    OutParentRegister model = new OutParentRegister();

    model.setEmail(info.getEmail());
    model.setSex(info.getGender());
    model.setName(info.getName());
    model.setPassword(" ");
    model.setAge(info.getAge());
    model.setBirthday(info.getBirthday());
    model.setPhoneNumber(STApplication.getPhoneNumber());
    model.setAppVersion(STApplication.getAppVersionName());
    model.setNationalCode(STApplication.getNationalCode());
    model.setGcm(STApplication.getString(StaticValues.GCM_REG_ID));
    model.setLang(STApplication.getLanguageCode());
    model.setOutCompanyName("Naver");

    SimpleXmlRequest request = HttpHelper.getExternalLogin(model,
        new Response.Listener<ParentLoginResult>() {
          @Override
          public void onResponse(ParentLoginResult res) {

            switch (res.getResultCode()) {

              case SUCCESS:

                dismissLoading();

                Toast.makeText(getApplicationContext(), res.getName(), Toast.LENGTH_SHORT).show();

                mHelper.insertAccount(res.getParentID(), 1, res.getName(), res.getCoin(), res.getEmail());

                mHelper.insertChildren(res.getChildren());

                sendEmptyMessage(COMPLETE_LOGIN);

                break;

              case 1028:

                dismissLoading();

                parentLoginResult = res;

//                mHelper.insertAccount(res.getParentID(), 1, res.getName(), res.getCoin(), res.getEmail());

//                mHelper.insertChildren(res.getChildren());

//                sendEmptyMessage(REQUEST_PASSWORD);

                SettingPasswordDialog dialog = new SettingPasswordDialog(LoginActivity.this);
                dialog.setListener(new SettingPasswordDialog.OnPasswordDialogListener() {
                  @Override
                  public void OnCancel() {
                    mOAuthLoginInstance.logout(mContext);
                  }

                  @Override
                  public void OnConfirm(String password) {

                    Bundle bundle = new Bundle();
                    bundle.putString("pwd", password);

                    sendMessage(REQUEST_EXTENAL_INPUT_PASSWORD, bundle);
                  }
                });
                dialog.show();

                break;

              default:

                mOAuthLoginInstance.logout(getApplicationContext());

                handleResultCode(res.getResultCode(), res.getResultMessage());

                break;
            }

          }
        }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

            handleError(error);
          }
        });

    addRequest(request);
  }

  private void requestPhoneInfo() {

    ParentPhoneInfo model = new ParentPhoneInfo();

    Account account = mHelper.getAccountInfo();

    // ParentID
    model.setParentID(account.getID());

    model.setLang(STApplication.getLanguageCode());

    model.setPhoneNumber(STApplication.getPhoneNumber());

    model.setAppVersion(STApplication.getAppVersionName());

    // GCM
    model.setGcm(STApplication.getString(StaticValues.GCM_REG_ID));

    model.setNationCode(STApplication.getNationalCode());

    SimpleXmlRequest request = HttpHelper.getParentPhoneInfo(model,
        new Response.Listener<GeneralResult>() {

          @Override
          public void onResponse(GeneralResult response) {

            dismissLoading();

            switch (response.getResultCode()) {

              case SUCCESS:

                sendEmptyMessage(COMPLETE_LOGIN);

                break;

              default:

                handleResultCode(response.getResultCode(), response.getResultMessage());

                break;
            }
          }

        }, new Response.ErrorListener() {

          @Override
          public void onErrorResponse(VolleyError error) {

            handleError(error);
          }
        });

    if (request != null) {

      mQueue.add(request);

    } else {

      dismissLoading();
    }
  }

  private boolean isValidateEmailInfo() {

    String msg = null;
    String temp = null;

    do {

      temp = mEditEmailAddr.getText().toString();

      if (TextUtils.isEmpty(temp)) {

        msg = "이메일을 입력하세요.";

        break;

      }

      if (!StringValidator.isEmail(temp)) {

        msg = "이메일 형식에 맞지 않습니다.";

        break;
      }

    } while (false);

    if (TextUtils.isEmpty(msg)) {

      return true;

    } else {

      Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

      return false;
    }
  }

  private void requestParentFindPwd() {

    showLoading();

    NewPassword model = new NewPassword();

    model.setEmail(mEditEmailAddr.getText().toString());

    SimpleXmlRequest request = HttpHelper.getTemporaryPassword(model,
        new Response.Listener<GeneralResult>() {
          @Override
          public void onResponse(GeneralResult response) {

            switch (response.getResultCode()) {

              case SUCCESS:

                dismissLoading();

                MaterialDialog.Builder buidler = new MaterialDialog.Builder(LoginActivity.this);

                buidler.title("비밀번호 찾기")
                    .content("회원가입하신 이메일 주소로 임시 비밀번호를 전송해 드립니다.")
                    .positiveText("확인").callback(new MaterialDialog.SimpleCallback() {
                  @Override
                  public void onPositive(MaterialDialog materialDialog) {

                    materialDialog.dismiss();

                  }
                }).build().show();

                break;

              default:

                handleResultCode(response.getResultCode(),
                    response.getResultMessage());

                break;
            }
          }
        }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

            handleError(error);

          }
        });

    addRequest(request);
  }


  private void showSignUp() {

    Intent intent = new Intent();

    intent.setClass(mContext, SignUpActivity.class);

    startActivity(intent);
  }

  private void showMain() {

    Intent intent = new Intent();

    intent.setClass(mContext, ListChildActivity.class);

    startActivity(intent);

    finish();
  }

  private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
    @Override
    public void run(boolean success) {

      Logger.d("is " + success);

      if (success) {

        String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
        String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
        long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
        String tokenType = mOAuthLoginInstance.getTokenType(mContext);

        requestUserInfoFromNaver();

      } else {

        String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
        String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);

        Logger.e("errorCode:" + errorCode + ", errorDesc:" + errorDesc);
      }
    }
  };

  /** 로그인 삭제 */
  private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(mContext);

      if (!isSuccessDeleteToken) {
      }

      return null;
    }

    protected void onPostExecute(Void v) {

      // update View
    }
  }

  /** 유저 정보 요청 */
  private class RequestNaverApiTask extends AsyncTask<Void, Void, NaverUserInfo> {

    @Override
    protected NaverUserInfo doInBackground(Void... params) {

      String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
      String at = mOAuthLoginInstance.getAccessToken(mContext);

      String result = mOAuthLoginInstance.requestApi(mContext, at, url);

      Serializer serializer = new Persister();

      NaverUserInfo info = new NaverUserInfo();

      Logger.xml(result);

      try {

        info = serializer.read(NaverUserInfo.class, result);

      } catch (Exception e) {

        Logger.e(e.getMessage());

        info = null;
      }

      return info;
    }

    protected void onPostExecute(NaverUserInfo info) {

      if (info == null || info.getResultCode() != 0) {

        dismissLoading();

      } else {


        Bundle bundle = new Bundle();

        bundle.putParcelable("info", info);

        sendMessage(REQUEST_NAVER_LOGIN, bundle);
      }
    }
  }

  /** 토큰 갱신 */
  private class RefreshTokenTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
      return mOAuthLoginInstance.refreshAccessToken(mContext);
    }

    protected void onPostExecute(String res) {

      // update View
    }
  }
}
