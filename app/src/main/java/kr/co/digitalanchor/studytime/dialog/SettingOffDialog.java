package kr.co.digitalanchor.studytime.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.thomas.seekbarhint.SeekBarHint;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-12-02.
 */
public class SettingOffDialog extends Dialog implements SeekBarHint.OnSeekBarHintProgressChangeListener,
        View.OnClickListener {

    public interface OnSimpleCallback {

        void onClickConfirm(int select, String password);

        void onClickCancel();
    }

    private final String[] hint = new String[]{"계속 ON", "10분", "20분", "30분", "40분", "50분",
            "1시간", "1시간 30분", "2시간", "2시간 30분", "3시간", "3시간 30분", "4시간"};

    private final int[] selection = new int[]{0, 10, 20, 30, 40, 50, 60, 90, 120, 150, 180, 210, 240};

    private SeekBarHint mSeekBar;

    private EditText mEditText;

    private Button mConfirm;
    private Button mCancel;

    private OnSimpleCallback callback;

    public SettingOffDialog(Context context) {

        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_off_setting);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().setAttributes(params);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mSeekBar = (SeekBarHint) findViewById(R.id.seekbar);
        mSeekBar.setMax(hint.length - 1);

        mSeekBar.updatePopupText(hint[0]);

        mSeekBar.setOnProgressChangeListener(this);

        mEditText = (EditText) findViewById(R.id.editPassword);

        mConfirm = (Button) findViewById(R.id.confirm);
        mConfirm.setOnClickListener(this);

        mCancel = (Button) findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);

    }

    @Override
    public String onHintTextChanged(SeekBarHint seekBarHint, int progress) {

        return hint[progress];
    }

    public void setCallback(OnSimpleCallback callback) {

        this.callback = callback;
    }

    private int getSelected() {

        int result = mSeekBar.getProgress();

        return selection[result];
    }

    @Override
    public void onClick(View v) {

        if (callback != null)

            switch (v.getId()) {

                case R.id.confirm:

                    callback.onClickConfirm(getSelected(), mEditText.getText().toString());

                    break;

                case R.id.cancel:

                    callback.onClickCancel();

                    break;
            }
    }
}
