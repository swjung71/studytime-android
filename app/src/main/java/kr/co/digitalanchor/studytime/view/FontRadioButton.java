package kr.co.digitalanchor.studytime.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;

/**
 * Created by Thomas on 2015-06-18.
 */
public class FontRadioButton extends RadioButton {

    public FontRadioButton(Context context) {

        super(context);

        applyTypeface(context, null);

    }

    public FontRadioButton(Context context, AttributeSet attrs) {

        super(context, attrs);

        applyTypeface(context, attrs);
    }

    public FontRadioButton(Context context, AttributeSet attrs, int defStyle) {

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
