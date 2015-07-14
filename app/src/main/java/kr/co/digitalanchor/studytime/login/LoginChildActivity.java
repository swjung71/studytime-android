package kr.co.digitalanchor.studytime.login;

import android.content.Intent;
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

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.chat.ChildChatActivity;
import kr.co.digitalanchor.studytime.model.ChildLoginResult;
import kr.co.digitalanchor.studytime.model.ChildRegister;
import kr.co.digitalanchor.studytime.model.ParentLogin;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.monitor.MonitorService;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-15.
 */
public class LoginChildActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_CHILD_LOGIN = 50001;
    private final int REQUEST_ADD_INFO = 50003;
    private final int COMPLETE_CHILD_LOGIN = 50002;

    private final int ACTIVITY_ADDITIONAL_INFO = 60001;

    EditText mEditEmailAddr;

    EditText mEditPassword;

    EditText mEditChildName;

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

        switch (msg.what) {

            case REQUEST_CHILD_LOGIN:

                requestLogin();

                break;

            case REQUEST_ADD_INFO:

                Bundle data = msg.getData();

                showAddInfo(data.getString("ParentID"), data.getString("Name"));

                break;

            case COMPLETE_CHILD_LOGIN:

                showMain();

                break;


            default:

                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonLogin:

                if (isValidate()) {

                    sendEmptyMessage(REQUEST_CHILD_LOGIN);
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

                        data = new Bundle();

                        data.putString("ParentID", response.getParentID());

                        data.putString("Name", mEditChildName.getText().toString());

                        Logger.d(data.toString());

                        sendMessage(REQUEST_ADD_INFO, data);

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

}
