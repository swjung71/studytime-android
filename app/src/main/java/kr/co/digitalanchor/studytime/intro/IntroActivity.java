package kr.co.digitalanchor.studytime.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.igaworks.IgawCommon;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-06-10.
 */
public class IntroActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // TODO Application class에서 호출 하면 안됨?
        IgawCommon.startApplication(this);

        setContentView(R.layout.activity_intro);

        initialize();

    }

    private void initialize() {

        findViewById(R.id.buttonModeKids).setOnClickListener(this);

        findViewById(R.id.buttonModeParent).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (isDuplicateRuns()) {

            return;
        }

        switch (v.getId()) {

            case R.id.buttonModeKids:

                Toast.makeText(getApplicationContext(), "buttonModeKids", Toast.LENGTH_SHORT).show();

                showChildIntro();

                break;

            case R.id.buttonModeParent:

                Toast.makeText(getApplicationContext(), "buttonModeParent", Toast.LENGTH_SHORT).show();

                showParentIntro();

                break;

            default:

                Toast.makeText(getApplicationContext(), "selected default in onClick()", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void showParentIntro() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), SplashParentActivity.class);

        startActivity(intent);

        finish();
    }

    private void showChildIntro() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), SplashChildActivity.class);

        startActivity(intent);

        finish();
    }
}
