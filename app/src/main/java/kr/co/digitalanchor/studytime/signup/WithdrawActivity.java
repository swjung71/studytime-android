package kr.co.digitalanchor.studytime.signup;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.ParentWithdraw;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-19.
 */
public class WithdrawActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_WITHDRAW = 50001;

    EditText mEditPassword;

    Button mButtonWithdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_withdraw);

        initView();
    }

    private void initView() {

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mButtonWithdraw = (Button) findViewById(R.id.buttonWithdraw);
        mButtonWithdraw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonWithdraw:

                if (!TextUtils.isEmpty(mEditPassword.getText().toString())) {

                    sendEmptyMessage(REQUEST_WITHDRAW);

                } else {

                    Toast.makeText(getApplicationContext(), "경고 : 비밀번호 미 입력", Toast.LENGTH_SHORT).show();
                }

                break;

            default:

                break;
        }
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_WITHDRAW:

                requestParentWithdraw();

                break;

            default:

                break;
        }
    }

    private void requestParentWithdraw() {

        DBHelper helper = new DBHelper(getApplicationContext());

        Account account = helper.getAccountInfo();

        mQueue.add(HttpHelper.getParentWithdraw(
                new ParentWithdraw(account.getID(),
                        mEditPassword.getText().toString()),

                new Response.Listener<GeneralResult>() {

                    @Override
                    public void onResponse(GeneralResult response) {

                        Logger.d(response.toString());

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                Toast.makeText(getApplicationContext(), "회원 탈퇴", Toast.LENGTH_SHORT).show();

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
                }));

    }
}
