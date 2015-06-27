package kr.co.digitalanchor.studytime.signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import kr.co.digitalanchor.studytime.R;

public class ClauseViewActivity extends FragmentActivity implements MaterialTabListener {

    TextView mLabelTitle;

    MaterialTabHost tabHost;

    ViewPager pager;

    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clause_view);

        mLabelTitle = (TextView) this.findViewById(R.id.labelTitle);

        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);

            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

        getIntentData();
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {

        pager.setCurrentItem(materialTab.getPosition(), true);
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }

    private void getIntentData() {

        Bundle data = getIntent().getExtras();

        if (data == null) {

            return;
        }

        boolean isParent = data.getBoolean("isParent", true);

        if (!isParent) {

            mLabelTitle.setBackgroundResource(R.color.bgColorType03);
        }

        int index = data.getInt("position", 0);

        pager.setCurrentItem(index);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {

            switch (num) {

                case 0:

                    return new TermsOfUseFragment();

                case 1:

                    return new PrivacyPolicyFragment();

                default:

                    return null;

            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {

                case 0:

                    return "이용약관";

                case 1:

                    return "개인정보취급정책";

                default:

                    return null;

            }
        }

    }
}
