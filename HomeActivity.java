package com.success.successEntellus.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.kekstudio.dachshundtablayout.HelperUtils;
import com.kekstudio.dachshundtablayout.indicators.LineFadeIndicator;
import com.success.successEntellus.R;
import com.success.successEntellus.fragment.DailyCheckListFragment;
import com.success.successEntellus.fragment.DailyScoreGraphFragment;
import com.success.successEntellus.fragment.WeeklyScorecardFragment;

public class HomeActivity extends AppCompatActivity {
    private static final String Tab_names[] = {"Daily CheckList", "Daily ScoreGraph", "Weekly Scorecard"};
    private ViewPager viewPager;
    private DachshundTabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        tabLayout = (DachshundTabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        LineFadeIndicator lineFadeIndicator = new LineFadeIndicator(tabLayout);
        tabLayout.setAnimatedIndicator(lineFadeIndicator);

        lineFadeIndicator.setSelectedTabIndicatorColor(Color.WHITE);
        lineFadeIndicator.setSelectedTabIndicatorHeight(HelperUtils.dpToPx(2));
        lineFadeIndicator.setEdgeRadius(0);
    }


    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position==0)
            return new DailyCheckListFragment();
            else if(position==1){
                return new DailyScoreGraphFragment();
            }else if(position==2){
                return new WeeklyScorecardFragment();
            }
            return new DailyCheckListFragment();
        }

        @Override
        public int getCount() {
            return Tab_names.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Tab_names[position];
        }
    }

}
