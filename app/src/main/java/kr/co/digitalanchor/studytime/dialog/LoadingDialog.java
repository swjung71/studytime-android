package kr.co.digitalanchor.studytime.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.industry.thomas.circleprogres.CircleProgressView;
import com.industry.thomas.circleprogres.TextMode;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.StaticValues;

/**
 * Created by Thomas on 2015-09-09.
 */
public class LoadingDialog extends Dialog {

    CircleProgressView progressView;

    public LoadingDialog(Context context) {

        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_loading_location);

        LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;

        getWindow().setAttributes(params);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        progressView = (CircleProgressView) findViewById(R.id.progressView);

        progressView.setMaxValue(StaticValues.MILLIS_IN_FUTURE / StaticValues.COUNTDOWN_IN_INTERVAL);
        progressView.setValue(StaticValues.MILLIS_IN_FUTURE / StaticValues.COUNTDOWN_IN_INTERVAL);
        progressView.setValueAnimated(1);

        //show unit
        progressView.setUnit("초");
        progressView.setShowUnit(true);

        progressView.setTextSize(20); // text size set, auto text size off
        progressView.setUnitSize(15); // if i set the text size i also have to set the unit size
        progressView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        //if you want the calculated text sizes to be bigger/smaller you can do so via
        progressView.setUnitScale(0.9f);
        progressView.setTextScale(0.9f);

        progressView.setTextMode(TextMode.VALUE);

//        image = (ImageView) findViewById(R.id.blink);
//        animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
    }

    @Override
    public void show() {

        super.show();

        progressView.setValue(StaticValues.MILLIS_IN_FUTURE / StaticValues.COUNTDOWN_IN_INTERVAL);

//        image.startAnimation(animation);
    }

    @Override
    public void dismiss() {

        super.dismiss();

//        image.clearAnimation();
    }

    public void onTick(long counter) {

        progressView.setValueAnimated(counter / StaticValues.COUNTDOWN_IN_INTERVAL,
                StaticValues.COUNTDOWN_IN_INTERVAL);
    }
}
