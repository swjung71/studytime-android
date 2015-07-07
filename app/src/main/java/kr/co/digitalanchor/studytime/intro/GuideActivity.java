package kr.co.digitalanchor.studytime.intro;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.widget.pagerindicator.IconPageIndicator;

/**
 * Created by Thomas on 2015-07-06.
 */
public class GuideActivity extends FragmentActivity {

    GuideAdapter mAdapter;
    ViewPager mPager;

    IconPageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);

        mAdapter = new GuideAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setAdapter(mAdapter);

        mIndicator = (IconPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
}
