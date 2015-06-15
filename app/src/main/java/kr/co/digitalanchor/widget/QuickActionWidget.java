package kr.co.digitalanchor.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2015-06-12.
 */
public abstract class QuickActionWidget extends PopupWindow {

    private static final int MEASURE_AND_LAYOUT_DONE = 1 << 1;

    private final int[] mLocation = new int[2];
    private final Rect mRect = new Rect();

    private int mPrivateFlags;

    private Context mContext;

    private View mAnchor;
    private boolean mIsMenuClick;

    private boolean mDismissOnClick;
    private int mArrowOffsetY;

    private int mPopupY;
    private boolean mIsOnTop;

    private int mScreenHeight;
    private int mScreenWidth;

    private boolean mIsDirty;

    private OnQuickActionClickListener mOnQuickActionClickListener;

    private ArrayList<QuickAction> mQuickActions = new ArrayList<QuickAction>();


    public interface OnQuickActionClickListener {

        void onQuickActionClicked(QuickActionWidget widget, int position);
    }

    public QuickActionWidget(Context context) {

        super(context);

        mContext = context;

        initializeDefault();

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);

        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        final WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        Point point = new Point();
        manager.getDefaultDisplay().getSize(point);

        mScreenWidth = point.x;
        mScreenHeight = point.y;

    }

    public void setContentView(int resId) {

        setContentView(LayoutInflater.from(mContext).inflate(resId, null));
    }

    private void initializeDefault() {

        mDismissOnClick = true;

        mArrowOffsetY = 0;
    }

    public QuickAction getQuickAction(int position) {

        if (position < 0 || position >= mQuickActions.size())
            return null;

        return mQuickActions.get(position);
    }

    public int getArrowOffsetY() {

        return mArrowOffsetY;
    }

    protected int getScreenWidth() {

        return mScreenWidth;
    }

    protected int getScreenHeight() {

        return mScreenHeight;
    }

    public void setDismissOnClick(boolean dismissOnClick) {

        mDismissOnClick = dismissOnClick;
    }

    public boolean getDismissOnClick() {

        return mDismissOnClick;
    }

    public void setOnQuickActionClickListener(OnQuickActionClickListener listener) {

        mOnQuickActionClickListener = listener;
    }

    public void addQuickAction(QuickAction action) {

        if (action != null) {

            mQuickActions.add(action);

            mIsDirty = true;
        }
    }

    public void clearAllQuickActions() {

        if (!mQuickActions.isEmpty()) {

            mQuickActions.clear();

            mIsDirty = true;
        }
    }

    public void show(View anchor) {

        showAsDropDown(anchor, 0, -10);
    }


    public void show(View anchor, boolean isMenuClick) {
        this.mIsMenuClick = isMenuClick;
        show(anchor);
    }

    protected void show() {
        if (mAnchor != null) show(mAnchor);
    }

    protected boolean isMenuClick() {
        return mIsMenuClick;
    }

    protected void setMenuClick(boolean isMenuClick) {
        this.mIsMenuClick = isMenuClick;
    }

    protected void clearQuickActions() {
        if (!mQuickActions.isEmpty()) {
            onClearQuickActions();
        }
    }

    protected void onClearQuickActions() {
    }

    protected abstract void populateQuickActions(List<QuickAction> quickActions);

    protected abstract void onMeasureAndLayout(Rect anchorRect, View contentView);

    protected void setWidgetSpecs(int popupY, boolean isOnTop) {
        mPopupY = popupY;
        mIsOnTop = isOnTop;

        mPrivateFlags |= MEASURE_AND_LAYOUT_DONE;
    }

    protected Context getContext() {
        return mContext;
    }

    protected OnQuickActionClickListener getOnQuickActionClickListener() {
        return mOnQuickActionClickListener;
    }
}
