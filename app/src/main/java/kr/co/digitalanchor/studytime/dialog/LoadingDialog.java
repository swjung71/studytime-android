package kr.co.digitalanchor.studytime.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-09-09.
 */
public class LoadingDialog extends Dialog {

    ImageView image;

    Animation animation;

    public LoadingDialog(Context context) {

        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_loading_location);

        LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;

        getWindow().setAttributes( params);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

//        image = (ImageView) findViewById(R.id.blink);
//        animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
    }

    @Override
    public void show() {
        super.show();

//        image.startAnimation(animation);
    }

    @Override
    public void dismiss() {
        super.dismiss();

//        image.clearAnimation();
    }
}
