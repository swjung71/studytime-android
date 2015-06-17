package kr.co.digitalanchor.studytime.intro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.igaworks.IgawCommon;
import com.igaworks.adpopcorn.IgawAdpopcorn;
import com.igaworks.adpopcorn.style.AdPOPcornStyler;

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

                showChildIntro();

                break;

            case R.id.buttonModeParent:

                showParentIntro();

                // showOfferWall();

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

    /**
     * Only test
     */
//    private void showOfferWall() {
//
//
//        IgawCommon.setUserId("test100001");
//
//        AdPOPcornStyler.themeStyle.rewardThemeColor = Color.parseColor("#A65EA8");
//        AdPOPcornStyler.themeStyle.themeColor = Color.parseColor("#A65EA8");
//        AdPOPcornStyler.themeStyle.rewardCheckThemeColor = Color.parseColor("#A65EA8");
//
//        IgawAdpopcorn.openOfferWall(IntroActivity.this);
//    }
}
