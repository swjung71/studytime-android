package kr.co.digitalanchor.studytime.control;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.widget.QuickAction;
import kr.co.digitalanchor.widget.QuickActionWidget;

/**
 * Created by Thomas on 2015-06-12.
 */
public class MenuPopup extends QuickActionWidget implements View.OnClickListener {

    public interface OnClickMenuItemListener {

        void onClickPoint();

        void onClickFAQ();

        void onClickInquiry();

        void onClickWithdraw();

        void onClickLogOut();
    }

    private OnClickMenuItemListener mListener;

    private TextView mLabelName;

    public MenuPopup(Context context, String name) {

        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.popup_main_menu, null);

        view.findViewById(R.id.menuFAQ).setOnClickListener(this);
        view.findViewById(R.id.menuPoint).setOnClickListener(this);
        view.findViewById(R.id.menuInquiry).setOnClickListener(this);
        view.findViewById(R.id.menuWithdraw).setOnClickListener(this);
        view.findViewById(R.id.buttonLogOut).setOnClickListener(this);

        mLabelName = (TextView) view.findViewById(R.id.labelName);

        setContentView(view);

        setFocusable(true);
        setTouchable(true);

        setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));

        setName(name);
    }

    @Override
    public void onClick(View v) {

        dismiss();

        switch (v.getId()) {

            case R.id.menuPoint:

                if (mListener != null) {

                    mListener.onClickPoint();
                }

                break;

            case R.id.menuFAQ:

                if (mListener != null) {

                    mListener.onClickFAQ();
                }

                break;

            case R.id.menuInquiry:

                if (mListener != null) {

                    mListener.onClickInquiry();
                }

                break;

            case R.id.menuWithdraw:

                if (mListener != null) {

                    mListener.onClickWithdraw();
                }

                break;

            case R.id.buttonLogOut:

                if (mListener != null) {

                    mListener.onClickLogOut();
                }

                break;

            default:

                break;
        }
    }

    private void setName(String name) {

        if (!TextUtils.isEmpty(name) && mLabelName != null) {

            mLabelName.setText(name);
        }
    }

    public void setOnClickMenuItemListener(OnClickMenuItemListener listener) {

        mListener = listener;
    }

    @Override
    protected void populateQuickActions(List<QuickAction> quickActions) {

    }

    @Override
    protected void onMeasureAndLayout(Rect anchorRect, View contentView) {

        contentView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentView.measure(View.MeasureSpec.makeMeasureSpec(getScreenWidth(), View.MeasureSpec.EXACTLY),
                ViewGroup.LayoutParams.WRAP_CONTENT);

        int rootHeight = contentView.getMeasuredHeight();

        int offsetY = getArrowOffsetY();
        int dyTop = anchorRect.top;
        int dyBottom = getScreenHeight() - anchorRect.bottom;

        boolean onTop = (dyTop > dyBottom);
        int popupY = (onTop) ? anchorRect.top - rootHeight + offsetY : anchorRect.bottom - offsetY;

        setWidgetSpecs(0, onTop);
    }
}
