package kr.co.digitalanchor.studytime.intro;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.igaworks.IgawCommon;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;

/**
 * Created by Thomas on 2015-06-10.
 */
public class IntroActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // TODO Application class에서 호출 하면 안됨?
        IgawCommon.startApplication(this);

//        setContentView(R.layout.activity_intro);

//        initialize();

        setContentView(R.layout.activity_control_child);
    }

    private void initialize() {

        ((TextView) findViewById(R.id.labelModeSelect))
                .setTypeface(STApplication.getTypeface(StaticValues.FONT_NANUM_GOTHIC));

        ((TextView) findViewById(R.id.labelUseInfo))
                .setTypeface(STApplication.getTypeface(StaticValues.FONT_NANUM_GOTHIC));

        ((TextView) findViewById(R.id.labelParentInfo))
                .setTypeface(STApplication.getTypeface(StaticValues.FONT_NANUM_GOTHIC));

        ((TextView) findViewById(R.id.labelKid))
                .setTypeface(STApplication.getTypeface(StaticValues.FONT_NANUM_GOTHIC));

        ((TextView) findViewById(R.id.labelKidInfo))
                .setTypeface(STApplication.getTypeface(StaticValues.FONT_NANUM_GOTHIC));

        findViewById(R.id.buttonModeKids).setOnClickListener(this);

        findViewById(R.id.buttonModeParent).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonModeKids:

                Toast.makeText(getApplicationContext(), "buttonModeKids", Toast.LENGTH_SHORT).show();

                break;

            case R.id.buttonModeParent:

                Toast.makeText(getApplicationContext(), "buttonModeParent", Toast.LENGTH_SHORT).show();

                break;

            default:

                Toast.makeText(getApplicationContext(), "selected default in onClick()", Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
