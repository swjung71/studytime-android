package kr.co.digitalanchor.studytime.signup;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-06-11.
 */
public class SignUpActivity extends BaseActivity {

    EditText mEditEmailAddr;

    EditText mEditPassword;

    EditText mEditPasswordA;

    EditText mEditBirthDate;

    EditText mEditName;

    RadioGroup mRadioGender;

    CheckBox mCheckServiceInfo;

    CheckBox mCheckPersonalInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        initView();
    }

    private void initView() {

        mEditEmailAddr = (EditText)findViewById(R.id.editEmailAddr);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mEditPasswordA = (EditText) findViewById(R.id.editPasswordAgain);

        mEditName = (EditText) findViewById(R.id.editName);

        mEditBirthDate = (EditText) findViewById(R.id.editBirthDate);

        mRadioGender = (RadioGroup) findViewById(R.id.radioGender);

        mCheckServiceInfo = (CheckBox) findViewById(R.id.checkServiceInfo);
    }
}
