package kr.co.digitalanchor.studytime.intro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.widget.pagerindicator.IconPagerAdapter;

/**
 * Created by Thomas on 2015-07-06.
 */
public class GuideAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    private static final int[] CONTENT = new int[]{

            R.drawable.img_intro1, R.drawable.img_intro2, R.drawable.img_intro3,
            R.drawable.img_intro4
    };

    private int mCount = CONTENT.length;

    public GuideAdapter(FragmentManager fm) {

        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        return GuideFragment.newInstance(CONTENT[position]);
    }

    @Override
    public int getIconResId(int index) {
        return R.drawable.indicator_guide;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }


}
