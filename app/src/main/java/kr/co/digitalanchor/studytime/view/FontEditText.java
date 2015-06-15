package kr.co.digitalanchor.studytime.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;

/**
 * Created by Thomas on 2015-06-15.
 */
public class FontEditText extends EditText {

    public FontEditText(Context context) {

        super(context);

        applyTypeface(context, null);
    }

    public FontEditText(Context context, AttributeSet attrs) {

        super(context, attrs);

        applyTypeface(context, attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        applyTypeface(context, attrs);
    }

    private void applyTypeface(Context context, AttributeSet attrs) {

        try {

            setTypeface(STApplication.getTypeface(StaticValues.FONT_NANUM_GOTHIC));

        } catch (Exception e) {

            // TODO Exception
        }
    }
}
