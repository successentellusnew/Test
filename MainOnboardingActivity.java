package com.success.successEntellus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;

public class MainOnboardingActivity extends AppCompatActivity {


    boolean onboardFlag;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_onboarding);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            onboardFlag=bundle.getBoolean("onboardFlag");
            Log.d(Global.TAG, "onCreate: onboardFlag "+onboardFlag);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),onboardFlag);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    public static class OnboardFlowFragment extends Fragment {

        int position;
        private Button intro_btno_next,intro_btno_skip;
        private LinearLayout ll_main_container;
        ImageView iv_indt0,iv_indt1,iv_indt2;
        boolean onboardFlag;

        @SuppressLint("ValidFragment")
        public OnboardFlowFragment(int position, boolean onboardFlag) {
            this.position=position;
            this.onboardFlag=onboardFlag;
        }

        public OnboardFlowFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_onboarding, container, false);

            ll_main_container=rootView.findViewById(R.id.ll_main_container);
            intro_btno_next=rootView.findViewById(R.id.intro_btno_next);
            intro_btno_skip=rootView.findViewById(R.id.intro_btno_skip);

            iv_indt0=rootView.findViewById(R.id.iv_oind0);
            iv_indt1=rootView.findViewById(R.id.iv_oind1);
            iv_indt2=rootView.findViewById(R.id.iv_oind2);


            if (position==0){
                ll_main_container.setBackground(getResources().getDrawable(R.drawable.onboard3));
                iv_indt0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==1){
                ll_main_container.setBackground(getResources().getDrawable(R.drawable.onboard4));
                iv_indt1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==2){
                ll_main_container.setBackground(getResources().getDrawable(R.drawable.onboard5));
                iv_indt2.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                intro_btno_next.setText("Finish");
            }

            intro_btno_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(Global.TAG, "onClick: position "+position);
                    if (position==0){
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==1){
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==2){
                        Intent intent=new Intent(getActivity(),DashboardActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putBoolean("onboardFlag",onboardFlag);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        getActivity().finish();

                    }
                }
            });

            intro_btno_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Intent intent=new Intent(getActivity(),DashboardActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putBoolean("onboardFlag",onboardFlag);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            return rootView;
        }

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        boolean onboardFlag;
        public SectionsPagerAdapter(FragmentManager fm, boolean onboardFlag) {
            super(fm);
            this.onboardFlag=onboardFlag;
        }

        @Override
        public Fragment getItem(int position) {

            return new OnboardFlowFragment(position,onboardFlag);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
