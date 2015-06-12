package kr.co.digitalanchor.studytime.control;


import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.widget.QuickAction;
import kr.co.digitalanchor.widget.QuickActionWidget;

/**
 * Created by Thomas on 2015-06-12.
 */
public class MenuPopup extends QuickActionWidget {

    protected DisplayMetrics metrics;
    protected LinearLayout content;

    public MenuPopup(Context context) {

        super(context);

        setContentView(R.layout.popup_main_menu);

        metrics = context.getResources().getDisplayMetrics();

        setFocusable(true);
        setTouchable(true);
    }

    @Override
    protected void populateQuickActions(List<QuickAction> quickActions) {

    }

    @Override
    protected void onMeasureAndLayout(Rect anchorRect, View contentView) {

    }
}
