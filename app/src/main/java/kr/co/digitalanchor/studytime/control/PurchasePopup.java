package kr.co.digitalanchor.studytime.control;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.widget.popup.QuickAction;
import kr.co.digitalanchor.widget.popup.QuickActionWidget;

/**
 * Created by Thomas on 2015-08-25.
 */
public class PurchasePopup extends QuickActionWidget implements View.OnClickListener {

    public interface OnClickPurchaseItemListener {

        void onClickPurchase();

        void onClickFreePay();

    }

    private OnClickPurchaseItemListener mListener;

    public PurchasePopup(Context context) {

        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.popup_purchase_menu, null);

        setContentView(view);

        setFocusable(true);
        setTouchable(true);

        setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void populateQuickActions(List<QuickAction> quickActions) {

    }

    @Override
    protected void onMeasureAndLayout(Rect anchorRect, View contentView) {

    }
}
