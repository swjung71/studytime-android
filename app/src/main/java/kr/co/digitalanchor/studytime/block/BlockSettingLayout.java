package kr.co.digitalanchor.studytime.block;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;

/**
 * Created by Thomas on 2015-10-01.
 */
public class BlockSettingLayout extends FrameLayout implements View.OnClickListener {

    private Context context;

    private Button buttonSettings;

    public BlockSettingLayout(Context context) {
        super(context);

        this.context = context;

        inflate(context, R.layout.layout_block_setting, this);

        buttonSettings = (Button) findViewById(R.id.buttonSettings);

        buttonSettings.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonSettings:

                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                break;

            default:

                break;

        }

    }
}
