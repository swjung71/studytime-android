package com.thomas.seekbarhint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Thomas on 2015-12-02.
 */
public class SeekBarHint extends SeekBar implements SeekBar.OnSeekBarChangeListener {

    public static final int POPUP_FIXED = 1;
    public static final int POPUP_FOLLOW = 0;

    public interface OnSeekBarHintProgressChangeListener {
        public String onHintTextChanged(SeekBarHint seekBarHint, int progress);
    }

    private int mPopupWidth;
    private int mPopupStyle;

    private PopupWindow mPopup;
    private TextView mPopupTextView;
    private int mYLocationOffset;

    private OnSeekBarChangeListener mInternalListener;
    private OnSeekBarChangeListener mExternalListener;

    private OnSeekBarHintProgressChangeListener mProgressChangeListener;

    public SeekBarHint(Context context) {
        super(context);
    }

    public SeekBarHint(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarHint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public SeekBarHint(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs) {

        setOnSeekBarChangeListener(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarHint);

        mPopupWidth = (int) a.getDimension(R.styleable.SeekBarHint_popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        mYLocationOffset = (int) a.getDimension(R.styleable.SeekBarHint_yOffset, 0);
        mPopupStyle = a.getInt(R.styleable.SeekBarHint_popupHintStyle, POPUP_FOLLOW);

        a.recycle();

        initHintPopup();
    }

    public void setPopupStyle(int style) {

        mPopupStyle = style;
    }

    public int getPopupStyle() {

        return mPopupStyle;
    }

    private void initHintPopup() {

        String popupText = null;

        if (mProgressChangeListener != null) {

            popupText = mProgressChangeListener.onHintTextChanged(this, getProgress());
        }

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View undoView = inflater.inflate(R.layout.popup, null);

        mPopupTextView = (TextView) undoView.findViewById(R.id.text);

        mPopupTextView.setText(popupText != null ? popupText : String.valueOf(getProgress()));

        mPopup = new PopupWindow(undoView, mPopupWidth, ViewGroup.LayoutParams.WRAP_CONTENT, false);

        mPopup.setAnimationStyle(R.style.fade_animation);

        attachSeekBar();
    }

    private void attachSeekBar() {

        final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                if (getVisibility() != VISIBLE) {

                    hidePopup();

                } else {

                    showPopup();
                }
            }
        };

        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {

            @Override
            public void onViewAttachedToWindow(View v) {

                getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {

                    getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);

                } else {

                    getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                }

                hidePopup();
            }
        });
    }

    private void showPopup() {

        if (mPopup.isShowing())
            return;

        if (mPopupStyle == POPUP_FOLLOW) {
            mPopup.showAtLocation(this,
                    Gravity.NO_GRAVITY,
                    (int) (this.getX() + (int) getXPosition(this)),
                    (int) (this.getY() + mYLocationOffset + this.getHeight()));
        }

        if (mPopupStyle == POPUP_FIXED) {
            mPopup.showAtLocation(this,
                    Gravity.CENTER | Gravity.BOTTOM, 0,
                    (int) (this.getY() + mYLocationOffset + this.getHeight()));
        }
    }

    private void hidePopup() {

        if (mPopup.isShowing()) {

            mPopup.dismiss();
        }
    }

    public void setHintView(View view) {

        // TODO
        // initHintPopup();
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {

        if (mInternalListener == null) {

            mInternalListener = l;

            super.setOnSeekBarChangeListener(l);

        } else {

            mExternalListener = l;
        }
    }

    public void setOnProgressChangeListener(OnSeekBarHintProgressChangeListener l) {

        mProgressChangeListener = l;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        String popupText = null;

        if (mProgressChangeListener != null) {

            popupText = mProgressChangeListener.onHintTextChanged(this, getProgress());
        }

        if (mExternalListener != null) {

            mExternalListener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        if (mExternalListener != null) {

            mExternalListener.onStopTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        if (mExternalListener != null) {

            mExternalListener.onStopTrackingTouch(seekBar);
        }
    }

    private float getXPosition(SeekBar seekBar) {

        float val = (((float) seekBar.getProgress() * (float) (seekBar.getWidth()
                - 2 * seekBar.getThumbOffset())) / seekBar.getMax());

        float offset = seekBar.getThumbOffset();

        int textWidth = mPopupWidth;
        float textCenter = (textWidth / 2.0f);

        float newX = val + offset - textCenter;

        return newX;
    }
}
