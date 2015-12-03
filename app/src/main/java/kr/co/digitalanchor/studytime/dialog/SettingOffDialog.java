package kr.co.digitalanchor.studytime.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import com.thomas.seekbarhint.SeekBarHint;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-12-02.
 */
public class SettingOffDialog extends Dialog implements SeekBarHint.OnSeekBarHintProgressChangeListener {

    private SeekBarHint mSeekBar;

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

        mSeekBar.setOnProgressChangeListener(this);


    }

    @Override
    public String onHintTextChanged(SeekBarHint seekBarHint, int progress) {
        return String.valueOf(progress);
    }
}
