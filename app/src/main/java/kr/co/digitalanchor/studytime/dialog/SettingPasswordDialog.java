package kr.co.digitalanchor.studytime.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-10-23.
 */
public class SettingPasswordDialog extends Dialog implements View.OnClickListener {

  public interface OnPasswordDialogListener {

    void OnCancel();

    void OnConfirm(String password);
  }

  EditText mEditPassword;

  EditText mEditPasswordAgain;

  Button mButtonCancel;

  Button mButtonConfirm;

  OnPasswordDialogListener mListener;

  public SettingPasswordDialog(Context context) {

    super(context);

    requestWindowFeature(Window.FEATURE_NO_TITLE);

    setContentView(R.layout.dialog_input_password);

    WindowManager.LayoutParams params = getWindow().getAttributes();
    params.width = WindowManager.LayoutParams.MATCH_PARENT;
    params.height = WindowManager.LayoutParams.WRAP_CONTENT;

    getWindow().setAttributes(params);

    getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    mEditPassword = (EditText) findViewById(R.id.editPassword);
    mEditPasswordAgain = (EditText) findViewById(R.id.editPasswordAgain);
    mButtonCancel = (Button) findViewById(R.id.cancel);
    mButtonConfirm = (Button) findViewById(R.id.confirm);

    mButtonCancel.setOnClickListener(this);
    mButtonConfirm.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {

    switch (v.getId()) {

      case R.id.cancel:

        if (mListener != null) {

          mListener.OnCancel();
        }

        cancel();

        break;

      case R.id.confirm:

        String password1 = mEditPassword.getText().toString();

        String password2 = mEditPasswordAgain.getText().toString();

        if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {

          Toast.makeText(getContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();

          break;
        }

        if (!password1.equals(password2)) {

          Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();

          break;
        }

        if (mListener != null) {

          mListener.OnConfirm(password2);
        }

        cancel();

        break;
    }
  }

  public void setListener(OnPasswordDialogListener listener) {

    mListener = listener;
  }
}
