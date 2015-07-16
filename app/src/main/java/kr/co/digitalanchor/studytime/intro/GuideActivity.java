package kr.co.digitalanchor.studytime.intro;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.view.ViewPagerEX;
import kr.co.digitalanchor.widget.pagerindicator.IconPageIndicator;

/**
 * Created by Thomas on 2015-07-06.
 */
public class GuideActivity extends FragmentActivity implements ViewPagerEX.OnSwipeOutListener {

    GuideAdapter mAdapter;
    ViewPagerEX mPager;

    IconPageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);

        mAdapter = new GuideAdapter(getSupportFragmentManager());

        mPager = (ViewPagerEX) findViewById(R.id.pager);
        mPager.setOnSwipeOutListener(this);
        mPager.setAdapter(mAdapter);

        mIndicator = (IconPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

    }

    @Override
    public void onSwipeOutAtEnd() {

        finish();
    }
}
