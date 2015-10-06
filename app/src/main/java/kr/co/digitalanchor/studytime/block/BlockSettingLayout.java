package kr.co.digitalanchor.studytime.block;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.utils.AndroidUtils;

/**
 * Created by Thomas on 2015-10-01.
 */
public class BlockSettingLayout extends FrameLayout implements View.OnClickListener {

    private Context context;

    private Button buttonSettings;

    private TextView textInfo;

    public BlockSettingLayout(Context context) {
        super(context);

        this.context = context;

        inflate(context, R.layout.layout_block_setting, this);

        buttonSettings = (Button) findViewById(R.id.buttonSettings);

        buttonSettings.setOnClickListener(this);

        textInfo = (TextView) findViewById(R.id.text);

        String str = context.getString(R.string.txt_accessibility_setting_1);

        String sub = context.getString(R.string.txt_accessibility_1);

        SpannableStringBuilder builder = new SpannableStringBuilder(str);

        int i = str.indexOf(sub);

        builder.setSpan(new ForegroundColorSpan(Color.rgb(0xa6, 0x5e, 0xa8)), i, i + sub.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textInfo.append(builder);

        textInfo.append(" ");

        str = context.getString(R.string.txt_accessibility_setting_2);

        sub = context.getString(R.string.txt_accessibility_2);

        builder = new SpannableStringBuilder(str);

        i = str.indexOf(sub);

        builder.setSpan(new ForegroundColorSpan(Color.rgb(0xa6, 0x5e, 0xa8)), i, i + sub.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textInfo.append(builder);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonSettings:

                STApplication.putLong(StaticValues.SHOW_SETTING, System.currentTimeMillis());

                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                break;

            default:

                break;

        }

    }
}
