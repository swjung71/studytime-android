package kr.co.digitalanchor.studytime.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.chat.ChildChatActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.AdultFileResult;
import kr.co.digitalanchor.studytime.model.ChildLoginResult;
import kr.co.digitalanchor.studytime.model.Files;
import kr.co.digitalanchor.studytime.model.GetAdultDB;
import kr.co.digitalanchor.studytime.model.ParentLogin;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-15.
 */
public class LoginChildActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_CHILD_LOGIN = 50001;
    private final int REQUEST_ADD_INFO = 50003;
    private final int COMPLETE_CHILD_LOGIN = 50002;
    private final int REQUEST_ADULT_FILE_LIST = 50005;
    private final int REQUEST_ADULT_FILE = 50004;

    private final int ACTIVITY_ADDITIONAL_INFO = 60001;

    EditText mEditEmailAddr;

    EditText mEditPassword;

    EditText mEditChildName;

    DBHelper mDBHelper;

    String parentId;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_login);

        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();

        dismissLoading();
    }

    private void initView() {

        mEditEmailAddr = (EditText) findViewById(R.id.editEmailAddr);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mEditChildName = (EditText) findViewById(R.id.editChildName);

        ((Button) findViewById(R.id.buttonLogin)).setOnClickListener(this);
    }

    @Override
    protected void onHandleMessage(Message msg) {

        Bundle data = null;

        switch (msg.what) {

            case REQUEST_CHILD_LOGIN:

                requestLogin();

                break;

            case REQUEST_ADD_INFO:

                showAddInfo(parentId, name);

                break;

            case REQUEST_ADULT_FILE:

                data = msg.getData();

                downloadAdultFile(data);

                break;

            case COMPLETE_CHILD_LOGIN:

                showMain();

                break;

            case REQUEST_ADULT_FILE_LIST:

                requestAdultFile();

                break;


            default:

                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonLogin:


                sendEmptyMessage(REQUEST_ADULT_FILE_LIST);

                if (isValidate()) {

//                    sendEmptyMessage(REQUEST_CHILD_LOGIN);
                }

                break;

            default:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_ADDITIONAL_INFO) {

            if (resultCode == RESULT_OK) {

                sendEmptyMessage(COMPLETE_CHILD_LOGIN);
            }
        }
    }

    private void showAddInfo(String parentId, String name) {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), AddInfoActivity.class);

        Logger.d(parentId + " " + name);

        intent.putExtra("ParentID", parentId);
        intent.putExtra("Name", name);

        startActivityForResult(intent, ACTIVITY_ADDITIONAL_INFO);
    }

    private void showMain() {

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ChildChatActivity.class);

        startActivity(intent);

        finish();
    }

    private boolean isValidate() {


        String tmp = null;

        String msg = null;

        do {

            tmp = mEditEmailAddr.getText().toString();

            if (TextUtils.isEmpty(tmp)) {

                msg = "이메일을 입력하세요.";

                break;
            }

            if (!StringValidator.isEmail(tmp)) {

                msg = " 이메일 형식에 맞지 않습니다.";

                break;
            }

            tmp = null;

            tmp = mEditPassword.getText().toString();

            if (TextUtils.isEmpty(tmp)) {

                msg = "비밀번호를 입력하세요.";

                break;
            }

            if (!StringValidator.isPassword(tmp)) {

                msg = "비밀번호 형식에 맞지 않습니다.";

                break;
            }

            tmp = null;

            tmp = mEditChildName.getText().toString();

            if (TextUtils.isEmpty(tmp)) {

                msg = "이름을 입력하세요.";

                break;
            }

        } while (false);

        if (TextUtils.isEmpty(msg)) {

            return true;

        } else {

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    private void requestLogin() {

        showLoading();

        String tmp = null;

        ParentLogin model = new ParentLogin();

        model.setEmail(mEditEmailAddr.getText().toString());

        model.setPassword(mEditPassword.getText().toString());

        SimpleXmlRequest request = HttpHelper.getChildLogin(model, new Response.Listener<ChildLoginResult>() {
            @Override
            public void onResponse(ChildLoginResult response) {

                Bundle data = null;

                switch (response.getResultCode()) {

                    case SUCCESS:

                        dismissLoading();

                        parentId = response.getParentID();

                        name = mEditChildName.getText().toString();

                        sendEmptyMessage(REQUEST_ADULT_FILE_LIST);

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

        addRequest(request);
    }

    private void downloadAdultFile(Bundle data) {

        Logger.d("downloadAdultFile");

        String file = data.getString("files");

        if (TextUtils.isEmpty(file)) {

            sendEmptyMessage(REQUEST_ADD_INFO);

            return;
        }

        Logger.d("downloadAdultFile 1" );

        new DownloadFileFromURL().execute(file);
    }

    private void requestAdultFile() {

        showLoading();

        GetAdultDB model = new GetAdultDB();

        String date = mDBHelper.getAdultFile();

        if (date != null) {

            model.setDate(date);
        }

        SimpleXmlRequest request = HttpHelper.getAdultFileList(model,
                new Response.Listener<AdultFileResult>() {

                    @Override
                    public void onResponse(AdultFileResult response) {

                        Bundle data = null;

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                dismissLoading();

                                data = new Bundle();

                                ArrayList<Files> files = response.getFileName();

                                mDBHelper.setAdultFile(response);

                                data.putString("files", response.getFileName().get(0).getFileName());
                                Logger.d(data.toString());

                                sendMessage(REQUEST_ADULT_FILE, data);

                                break;

                            default:

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

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Logger.d("onPreExecute");

            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Logger.d("http://wwww.dastudytime.kr/resources/studytime/" + params[0]);

                URL url = new URL("http://wwww.dastudytime.kr/resources/studytime/" + params[0]);

                URLConnection conn = url.openConnection();
                conn.connect();

                InputStream input = new BufferedInputStream(url.openStream());

                BufferedReader br = new BufferedReader(new InputStreamReader(input));

                mDBHelper.setTableAdultUrl(br);

                input.close();

                return null;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dismissLoading();

            sendEmptyMessage(REQUEST_ADD_INFO);
        }
    }

}
