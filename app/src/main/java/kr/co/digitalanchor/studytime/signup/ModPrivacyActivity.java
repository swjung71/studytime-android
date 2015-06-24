package kr.co.digitalanchor.studytime.signup;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;

/**
 * Created by Thomas on 2015-06-19.
 */
public class ModPrivacyActivity extends BaseActivity implements View.OnClickListener {

    TextView mLabelEmailAddr;

    EditText mEditPassword;

    EditText mEditPasswordA;

    EditText mEditParentName;

    EditText mEditBirthDate;

    RadioGroup mCheckGender;

    Button mButtonConfirm;

    DBHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_privacy);

        initView();

        setData();
    }

    private void initView() {

        mLabelEmailAddr = (TextView) findViewById(R.id.labelEmailAddr);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mEditPasswordA = (EditText) findViewById(R.id.editPasswordAgain);

        mEditParentName = (EditText) findViewById(R.id.editName);

        mEditBirthDate = (EditText) findViewById(R.id.editBirthDate);

        mCheckGender = (RadioGroup) findViewById(R.id.radioGender);

        mButtonConfirm = (Button) findViewById(R.id.buttonConfirm);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonConfirm:

                break;

            default:
                    break;
        }
    }

    private void setData() {

        mHelper = new DBHelper(getApplicationContext());

        Account account = mHelper.getAccountInfo();


    }
}
