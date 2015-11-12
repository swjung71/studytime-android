package kr.co.digitalanchor.studytime.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.orhanobut.logger.Logger;
import java.util.Arrays;
import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.control.ListChildActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.dialog.SettingPasswordDialog;
import kr.co.digitalanchor.studytime.model.ChangePassModel;
import kr.co.digitalanchor.studytime.model.FacebookProfile;
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
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-10.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private final int REQUEST_LOGIN = 50001;
  private final int COMPLETE_LOGIN = 50002;
  private final int REQUEST_PARENT_INFO = 50003;
  private final int REQUEST_FIND_PASSWORD = 50004;
  private final int REQUEST_EXTENAL_INPUT_PASSWORD = 50005;
  private final int REQUEST_NAVER_LOGIN = 50006;
  private final int REQUEST_FACEBOOK_LOGIN = 50007;
  private final int REQUEST_GOOGLE_LOGIN = 50008;

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

  /** Google+ */
  private SignInButton mPlusSignInButton;

  /* RequestCode for resolutions involving sign-in */
  private static final int RC_SIGN_IN = 1;

  /* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
  private static final int RC_PERM_GET_ACCOUNTS = 2;

  /* Keys for persisting instance variables in savedInstanceState */
  private static final String KEY_IS_RESOLVING = "is_resolving";
  private static final String KEY_SHOULD_RESOLVE = "should_resolve";

  /* Is there a ConnectionResult resolution in progress? */
  private boolean mIsResolving = false;
  /* Should we automatically resolve ConnectionResults when possible? */
  private boolean mShouldResolve = false;

  /* Request code used to invoke sign in user interactions. */
  private GoogleApiClient mGoogleApiClient;

  Button mButtonFindPwd;

  LoginButton mButtonFacebook;

  DBHelper mHelper;

  ParentLoginResult parentLoginResult;

  CallbackManager mCallbackManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {

      FacebookSdk.sdkInitialize(getApplicationContext());

      FacebookSdk.setIsDebugEnabled(true);

      Logger.d("is debug " + FacebookSdk.isDebugEnabled());

      mCallbackManager = CallbackManager.Factory.create();

      setContentView(R.layout.activity_parent_login_ext);

      if (savedInstanceState != null) {
        mIsResolving = savedInstanceState.getBoolean(KEY_IS_RESOLVING);
        mShouldResolve = savedInstanceState.getBoolean(KEY_SHOULD_RESOLVE);
      }

    } catch (Exception e) {

      Logger.e(e.getMessage());
    }

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
    mOAuthLoginButton.setBgResourceId(R.drawable.btn_naver_bg);
    mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);

    mButtonFacebook = (LoginButton) findViewById(R.id.buttonFacebookLogin);
    mButtonFacebook.setAlpha(0.0f);
    mButtonFacebook.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
    mButtonFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
      @Override
      public void onSuccess(LoginResult loginResult) {

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {

              @Override
              public void onCompleted(JSONObject object, GraphResponse response) {

                Logger.json(object.toString());

                Gson gson = new Gson();

                FacebookProfile profile = gson.fromJson(object.toString(), FacebookProfile.class);

                Logger.d(profile.getName());

                Bundle data = new Bundle();

                data.putParcelable("info", profile);

                sendMessage(REQUEST_FACEBOOK_LOGIN, data);
              }
            });

        Bundle parameters = new Bundle();

        parameters.putString("fields", "id,name,email,gender,birthday");

        request.setParameters(parameters);
        request.executeAsync();
      }

      @Override
      public void onCancel() {
//        Toast.makeText(LoginActivity.this, "User cancelled", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onError(FacebookException error) {
//        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();

        Logger.e(error.getMessage());
      }
    });

    mPlusSignInButton = (SignInButton) findViewById(R.id.buttonGoogleLogin);
    mPlusSignInButton.setSize(SignInButton.SIZE_WIDE);
    mPlusSignInButton.setAlpha(0.0f);
    mPlusSignInButton.setOnClickListener(this);

    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(new Scope(Scopes.PLUS_LOGIN))
        .addScope(new Scope(Scopes.PLUS_ME))
        .build();
  }

  @Override
  protected void onStart() {
    super.onStart();

  }

  @Override
  protected void onStop() {
    super.onStop();
    mGoogleApiClient.disconnect();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(KEY_IS_RESOLVING, mIsResolving);
    outState.putBoolean(KEY_SHOULD_RESOLVE, mShouldResolve);
  }

  @Override
  protected void onHandleMessage(Message msg) {

    Bundle bundle = null;

    switch (msg.what) {

      case REQUEST_GOOGLE_LOGIN:

        bundle = msg.getData();

        FacebookProfile google = bundle.getParcelable("info");

        Logger.d(google.getName());

        if (google == null) {

          LoginManager.getInstance().logOut();

          dismissLoading();

          signOut();

          break;

        }

        requestGoogleLogin(google);

        break;

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

        requestNaverLogin(info);

        break;

      case REQUEST_FACEBOOK_LOGIN:

        bundle = msg.getData();

        FacebookProfile profile = bundle.getParcelable("info");

        Logger.d(profile.getName());

        if (profile == null) {

          LoginManager.getInstance().logOut();

          dismissLoading();

          break;

        }

        requestFacebookLogin(profile);

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

  private void signOut() {
    // Clear the default account so that GoogleApiClient will not automatically
    // connect in the future.
    if (mGoogleApiClient.isConnected()) {
      Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
      mGoogleApiClient.disconnect();
    }

  }

  @Override
  public void onClick(View v) {

    if (isDuplicateRuns()) {

      return;
    }

    switch (v.getId()) {

      case R.id.buttonGoogleLogin:

        showLoading();

        new Thread(new Runnable() {
          @Override
          public void run() {

            try {

              mShouldResolve = true;

              mGoogleApiClient.connect();

            } catch (Exception e) {

              dismissLoading();
            }
          }
        }).start();

        break;

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

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    if (requestCode == RC_PERM_GET_ACCOUNTS) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        getAccountInfo();

      } else {
        Logger.d("GET_ACCOUNTS Permission Denied.");
      }
    }
  }

  private void getAccountInfo() {


  }

  @Override
  public void onConnected(Bundle bundle) {

    Logger.d("onConnected");

    mShouldResolve = false;

    dismissLoading();

    if (checkAccountsPermission()) {

      Person currentPerson = Plus.PeopleApi
          .getCurrentPerson(mGoogleApiClient);

      if (currentPerson == null) {

        mGoogleApiClient.disconnect();

        return;
      }


      String personName = currentPerson.getDisplayName();
      String personPhotoUrl = currentPerson.getImage().getUrl();
      String personGooglePlusProfile = currentPerson.getUrl();
      String birth = currentPerson.getBirthday();
      String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
      int gender = currentPerson.getGender();

      Logger.d("name " + personName + "\n" +
          "personPhotoUrl " + personPhotoUrl + "\n" +
          "personGooglePlusProfile " + personGooglePlusProfile + "\n" +
          "birth " + birth + "\n" +
          "email " + email + "\n" +
          "gender " + gender + "\n");

      Bundle data = new Bundle();

      FacebookProfile info = new FacebookProfile();

      info.setName(personName);
      info.setEmail(email);
      info.setBirthday(birth);
      info.setGender(String.valueOf(gender));

      data.putParcelable("info", info);

      sendMessage(REQUEST_GOOGLE_LOGIN, data);

    } else {

//      Toast.makeText(getApplicationContext(),
//          "Person information is null", Toast.LENGTH_LONG).show();

      mGoogleApiClient.disconnect();
    }

  }

  @Override
  public void onConnectionSuspended(int i) {

    Logger.d("onConnectionSuspended");

    dismissLoading();

  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {

    Logger.d("onConnectionFailed");

    dismissLoading();

    if (!mIsResolving && mShouldResolve) {
      if (connectionResult.hasResolution()) {
        try {
          connectionResult.startResolutionForResult(this, RC_SIGN_IN);
          mIsResolving = true;
        } catch (IntentSender.SendIntentException e) {
          Logger.e("Could not resolve ConnectionResult.", e);
          mIsResolving = false;
        }
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN) {
      // If the error resolution was not successful we should not resolve further.
      if (resultCode != RESULT_OK) {
        mShouldResolve = false;
      }

      mIsResolving = false;
      mGoogleApiClient.connect();

    } else {

      mCallbackManager.onActivityResult(requestCode, resultCode, data);
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

  private boolean checkAccountsPermission() {

    final String perm = Manifest.permission.GET_ACCOUNTS;
    int permissionCheck = ContextCompat.checkSelfPermission(this, perm);
    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
      // We have the permission
      return true;
    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
      // Need to show permission rationale, display a snackbar and then request
      // the permission again when the snackbar is dismissed.
      return false;
    } else {
      // No explanation needed, we can request the permission.
      ActivityCompat.requestPermissions(this,
          new String[]{perm},
          RC_PERM_GET_ACCOUNTS);
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

    model.setParentId(parentLoginResult.getParentID());

    model.setNewPass(pwd);

    SimpleXmlRequest request = HttpHelper.getExternalPassword(model,
        new Response.Listener<GeneralResult>() {
          @Override
          public void onResponse(GeneralResult res) {

            switch (res.getResultCode()) {

              case SUCCESS:

                mHelper.insertAccount(parentLoginResult.getParentID(), 1,
                    parentLoginResult.getName(), parentLoginResult.getCoin(),
                    parentLoginResult.getEmail());

                mHelper.insertChildren(parentLoginResult.getChildren());

                dismissLoading();

                sendEmptyMessage(COMPLETE_LOGIN);

                break;

              default:

                mOAuthLoginInstance.logout(mContext);

                LoginManager.getInstance().logOut();

                handleResultCode(res.getResultCode(), res.getResultMessage());

                break;
            }
          }
        }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

            mOAuthLoginInstance.logout(mContext);

            LoginManager.getInstance().logOut();

            handleError(error);
          }
        });

    addRequest(request);
  }

  private void completeLogin() {

    showMain();

  }

  private void requestGoogleLogin(FacebookProfile info) {

    dismissLoading();

    OutParentRegister model = new OutParentRegister();

    model.setEmail(info.getEmail());
    model.setSex(info.getGender());
    model.setName(info.getName());
    model.setPassword("");
    model.setBirthday(TextUtils.isEmpty(info.getBirthday()) ? "" : info.getBirthday());
    model.setPhoneNumber(STApplication.getPhoneNumber());
    model.setAppVersion(STApplication.getAppVersionName());
    model.setNationalCode(STApplication.getNationalCode());
    model.setGcm(STApplication.getString(StaticValues.GCM_REG_ID));
    model.setLang(STApplication.getLanguageCode());
    model.setOutCompanyName("Google");

    SimpleXmlRequest request = HttpHelper.getExternalLogin(model,
        new Response.Listener<ParentLoginResult>() {
          @Override
          public void onResponse(ParentLoginResult res) {

            switch (res.getResultCode()) {

              case SUCCESS:

                dismissLoading();

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
                    LoginManager.getInstance().logOut();
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

                signOut();

                handleResultCode(res.getResultCode(), res.getResultMessage());

                break;
            }

          }
        }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

            signOut();
            handleError(error);
          }
        });

    addRequest(request);
  }

  private void requestFacebookLogin(FacebookProfile info) {

    dismissLoading();

    OutParentRegister model = new OutParentRegister();

    model.setEmail(info.getEmail());
    model.setSex(info.getGender());
    model.setName(info.getName());
    model.setPassword("");
    model.setBirthday(TextUtils.isEmpty(info.getBirthday()) ? "" : info.getBirthday());
    model.setPhoneNumber(STApplication.getPhoneNumber());
    model.setAppVersion(STApplication.getAppVersionName());
    model.setNationalCode(STApplication.getNationalCode());
    model.setGcm(STApplication.getString(StaticValues.GCM_REG_ID));
    model.setLang(STApplication.getLanguageCode());
    model.setOutCompanyName("Facebook");

    SimpleXmlRequest request = HttpHelper.getExternalLogin(model,
        new Response.Listener<ParentLoginResult>() {
          @Override
          public void onResponse(ParentLoginResult res) {

            switch (res.getResultCode()) {

              case SUCCESS:

                dismissLoading();

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
                    LoginManager.getInstance().logOut();
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

                LoginManager.getInstance().logOut();

                handleResultCode(res.getResultCode(), res.getResultMessage());

                break;
            }

          }
        }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

            LoginManager.getInstance().logOut();

            handleError(error);
          }
        });

    addRequest(request);
  }

  private void requestNaverLogin(NaverUserInfo info) {

    dismissLoading();

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

            mOAuthLoginInstance.logout(mContext);

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
