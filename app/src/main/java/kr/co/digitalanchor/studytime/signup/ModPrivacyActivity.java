package kr.co.digitalanchor.studytime.signup;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.ParentInfoChange;
import kr.co.digitalanchor.studytime.model.ParentPrivacyInfo;
import kr.co.digitalanchor.studytime.model.ParentPrivacyInfoResult;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-19.
 */
public class ModPrivacyActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_GET_INFO = 50001;

    private final int REQUEST_MODIFY_INFO = 50002;

    private final int COMPLETE_MODIFY_INFO = 50003;

    TextView mLabelEmailAddr;

    EditText mEditPassword;

    EditText mEditPasswordA;

    EditText mEditParentName;

    EditText mEditBirthDate;

    RadioGroup mCheckGender;

    Button mButtonConfirm;

    DBHelper mHelper;

    ParentPrivacyInfoResult mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_privacy);

        initView();

        mHelper = new DBHelper(getApplicationContext());

        sendEmptyMessage(REQUEST_GET_INFO);
    }

    private void initView() {

        mLabelEmailAddr = (TextView) findViewById(R.id.labelEmailAddr);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mEditPasswordA = (EditText) findViewById(R.id.editPasswordAgain);

        mEditParentName = (EditText) findViewById(R.id.editName);

        mEditBirthDate = (EditText) findViewById(R.id.editBirthDate);

        mCheckGender = (RadioGroup) findViewById(R.id.radioGender);

        mButtonConfirm = (Button) findViewById(R.id.buttonConfirm);
        mButtonConfirm.setOnClickListener(this);
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_GET_INFO:

                requestGetInfo();

                break;

            case REQUEST_MODIFY_INFO:

                requestModifyInfo();

                break;

            case COMPLETE_MODIFY_INFO:

                Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();

                finish();

                break;

            default:

                break;
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonConfirm:

                if (isValidate()) {

                    sendEmptyMessage(REQUEST_MODIFY_INFO);
                }

                break;

            default:
                break;
        }
    }


    public boolean isValidate() {

        String temp = null;

        String msg = null;

        do {


            String temp2 = mEditPassword.getText().toString();

            temp = mEditPasswordA.getText().toString();

            if (!TextUtils.isEmpty(temp) && !StringValidator.isPassword(temp)) {

                msg = "비밀번호 형시에 맞지 않습니다.";

                break;
            }

            if (!TextUtils.isEmpty(temp2) && TextUtils.isEmpty(temp)) {

                msg = "새로운 비밀번호를 입력하세요.";

                break;
            }

            if (TextUtils.isEmpty(temp2) && !TextUtils.isEmpty(temp)) {

                msg = "현재 비밀번호를 입력하세요.";

                break;
            }

            if (!TextUtils.isEmpty(temp2) && !TextUtils.isEmpty(temp) && temp.compareTo(temp2) == 0) {

                msg = "새로운 비밀번호가 이전 비밀번호와 같습니다.";

                break;
            }

            temp = mEditBirthDate.getText().toString();

            if (!TextUtils.isEmpty(temp) && !StringValidator.isBirthDay(temp)) {

                msg = "생년월일 형식에 맞지 않습니다.";

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

    public void setInfo(ParentPrivacyInfoResult info) {

        mResult = info;

        mLabelEmailAddr.setText(info.getEmail() + "(이메일 변경안됨)");
    }

    private void requestGetInfo() {

        showLoading();

        Account account = mHelper.getAccountInfo();

        ParentPrivacyInfo model = new ParentPrivacyInfo(account.getID());

        SimpleXmlRequest request = HttpHelper.getParentInfo(model,
                new Response.Listener<ParentPrivacyInfoResult>() {

                    @Override
                    public void onResponse(ParentPrivacyInfoResult response) {

                        dismissLoading();

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                setInfo(response);

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

    private void requestModifyInfo() {

        showLoading();

        String tmp = null;

        final Account account = mHelper.getAccountInfo();

        final ParentInfoChange model = new ParentInfoChange();

        model.setParentID(account.getID());

        tmp = mEditParentName.getText().toString();

        if (TextUtils.isEmpty(tmp)) {

            model.setName(mResult.getName());

        } else {

            model.setName(tmp);
        }

        tmp = null;

        tmp = mEditPassword.getText().toString();

        if (TextUtils.isEmpty(tmp)) {

            model.setOldPwd(null);

        } else {

            model.setOldPwd(tmp);
        }

        tmp = null;

        tmp = mEditPasswordA.getText().toString();

        if (TextUtils.isEmpty(tmp)) {

            model.setNewPwd(null);

        } else {

            model.setNewPwd(tmp);
        }

        switch (mCheckGender.getCheckedRadioButtonId()) {

            case R.id.male:

                model.setSex("0");

                break;

            case R.id.female:

                model.setSex("1");

                break;

            default:

                model.setSex(mResult.getSex());

                break;
        }

        model.setEmail(account.getEmail());

        SimpleXmlRequest request = HttpHelper.getParentModifyInfo(model,
                new Response.Listener<GeneralResult>() {
                    @Override
                    public void onResponse(GeneralResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                setModifiedInfo(model, account);

                                mLoading.dismiss();

                                sendEmptyMessage(COMPLETE_MODIFY_INFO);

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

    private void setModifiedInfo(ParentInfoChange model, Account account) {

        mHelper.updateAccount(account.getID(), 1,
                TextUtils.isEmpty(model.getName()) ? account.getName() : model.getName(),
                account.getCoin(), account.getEmail());
    }
}
