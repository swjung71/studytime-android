package kr.co.digitalanchor.studytime.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;

/**
 * Created by Thomas on 2015-06-12.
 */
public class FontTextView extends TextView {

    public FontTextView(Context context) {

        super(context);

        applyTypeface(context, null);

    }

    public FontTextView(Context context, AttributeSet attrs) {

        super(context, attrs);

        applyTypeface(context, attrs);

    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        applyTypeface(context, attrs);
    }

    private void applyTypeface(Context context, AttributeSet attrs) {

        try {

            // xml 어트리뷰트에서 textStyle을 얻어와야 함

            setTypeface(STApplication.getTypeface(StaticValues.FONT_NANUM_GOTHIC));

        } catch (Exception e) {

            // TODO Exception
        }
    }
}
