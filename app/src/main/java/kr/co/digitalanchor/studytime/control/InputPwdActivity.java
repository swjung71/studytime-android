package kr.co.digitalanchor.studytime.control;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.CoinResult;
import kr.co.digitalanchor.studytime.model.ParentOnOff;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-29.
 */
public class InputPwdActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_OFF = 50001;

    Button mButtonConfirm;

    EditText mEditPassword;

    DBHelper mHelper;

    Child mChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_parent_input_pwd);

        mButtonConfirm = (Button) findViewById(R.id.buttonConfirm);
        mButtonConfirm.setOnClickListener(this);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mHelper = new DBHelper(getApplicationContext());

        getData();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonConfirm:

                sendEmptyMessage(REQUEST_OFF);

                break;

            default:

                break;
        }
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_OFF:

                requestOnOff();

                break;

            default:

                break;
        }
    }

    private void requestOnOff() {

        showLoading();

        final Account account = mHelper.getAccountInfo();

        ParentOnOff model = new ParentOnOff();

        model.setParentID(account.getID());
        model.setChildID(mChild.getChildID());
        model.setIsOff(mChild.getIsOFF() == 0 ? "1" : "0");
        model.setName(account.getName());
        model.setPassword(mEditPassword.getText().toString());

        SimpleXmlRequest request = HttpHelper.getParentOnOff(model,
                new Response.Listener<CoinResult>() {
                    @Override
                    public void onResponse(CoinResult response) {

                        Logger.d(response.toString());

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                if (mChild.getIsOFF() == 0) {

                                    mHelper.updateChildToggle(mChild.getChildID(), 1);

                                    mHelper.updateCoin(account.getID(), response.getCoin());

                                    mChild.setIsOFF(1);

                                } else {

                                    mHelper.updateChildToggle(mChild.getChildID(), 0);

                                    mChild.setIsOFF(0);

                                }

                                dismissLoading();

                                finish();

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

    private void getData() {

        Bundle data = getIntent().getExtras();

        if (data == null) {

            finish();

            Logger.e("Extra is null");

            return;
        }

        if (data.containsKey("ChildID")) {

            mChild = mHelper.getChild(data.getString("ChildID"));

            if (mChild == null)
                finish();
        }
    }
}
