package kr.co.digitalanchor.widget.popup;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by Thomas on 2015-06-12.
 */
public class QuickAction {

    public Drawable mDrawable;
    public CharSequence mTitle;

    WeakReference<View> mView;

    public QuickAction(Drawable drawable, CharSequence title) {

        mDrawable = drawable;
        mTitle = title;
    }

    public Drawable getIcon() {

        return mDrawable;
    }

    public void setIcon(Drawable drawable) {

        mDrawable = drawable;
    }

    public CharSequence getTitle() {

        return mTitle;
    }

    public void setTitle(CharSequence title) {

        mTitle = title;
    }
}
