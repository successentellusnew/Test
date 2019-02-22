package com.success.successEntellus.activity;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
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
import com.success.successEntellus.fragment.CreateCampaignOnBoardingFragment;
import com.success.successEntellus.lib.Global;

public class OnboardingEmailCampaign extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static  ViewPager mViewPager;
    int count;
    String keystring;
    //static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_email_campaign);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            keystring=bundle.getString("keyString");
            Log.d(Global.TAG, " keystring: "+keystring);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),keystring);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        int count;
        String key;
        public SectionsPagerAdapter(FragmentManager fm, String keyString) {
            super(fm);
            this.key=keyString;

            if (key.equals("create_campaign")){
                count=3;
            }else if (key.equals("create_template")){
                count=6;
            }else if (key.equals("add_email")){
                count=2;
            }else if (key.equals("import_template")){
                count=2;
            }else if (key.equals("self_reminder")){
                count=2;
            }
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(Global.TAG, "getItem: "+key);
            if (key.equals("create_campaign")){
                return new CreateCampaignOnBoardingFragment(position);
            }else if (key.equals("create_template")){
                return new CreateTemplateOnBoardingFragment(position);
            }else if (key.equals("add_email")){
                return new AddEmailOnBoardingFragment(position,"add_email");
            }else if (key.equals("import_template")){
                return new AddEmailOnBoardingFragment(position,"import_template");
            }else if (key.equals("self_reminder")){
                return new AddEmailOnBoardingFragment(position,"self_rem");
            }

            return new CreateCampaignOnBoardingFragment(position);
        }

        @Override
        public int getCount() {
            return count;
        }
    }
    public static class CreateCampaignOnBoardingFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        int position;
        private Button intro_btn_next,intro_btn_skip;
        private LinearLayout ll_create_campaign;
        ImageView iv_ind0,iv_ind1,iv_ind2;

        @SuppressLint("ValidFragment")
        public CreateCampaignOnBoardingFragment(int position) {
            this.position=position;
        }

        public CreateCampaignOnBoardingFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboarding_email_campaign, container, false);

            ll_create_campaign=rootView.findViewById(R.id.ll_create_campaign);
            intro_btn_next=rootView.findViewById(R.id.intro_btn_next);
            intro_btn_skip=rootView.findViewById(R.id.intro_btn_skip);
            iv_ind0=rootView.findViewById(R.id.iv_ind0);
            iv_ind1=rootView.findViewById(R.id.iv_ind1);
            iv_ind2=rootView.findViewById(R.id.iv_ind2);

            if (position==0){
                ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.create_camp1));
                iv_ind0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==1){
                ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.create_camp2));
                iv_ind1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==2){
                ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.create_camp3));
                iv_ind2.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                intro_btn_next.setText("Finish");
            }


            intro_btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(Global.TAG, "onClick: position "+position);
                    if (position==0){
                        Log.d(Global.TAG, "Position 0 clicked: ");
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==1){
                        Log.d(Global.TAG, "Position 1 clicked: ");
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==2){
                        Log.d(Global.TAG, "onClick: Finish Clicked. ");
                        getActivity().finish();
                    }
                }
            });

            intro_btn_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    getActivity().finish();
                }
            });

            return rootView;
        }

    }

    public static class CreateTemplateOnBoardingFragment extends Fragment {

        int position;
        private Button intro_btn_next,intro_btn_skip;
        private LinearLayout ll_create_template;
        ImageView iv_indt0,iv_indt1,iv_indt2,iv_indt3,iv_indt4,iv_indt5;

        @SuppressLint("ValidFragment")
        public CreateTemplateOnBoardingFragment(int position) {
            this.position=position;
        }

        public CreateTemplateOnBoardingFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboarding_create_template, container, false);

            ll_create_template=rootView.findViewById(R.id.ll_create_template);
            intro_btn_next=rootView.findViewById(R.id.intro_btn_next);
            intro_btn_skip=rootView.findViewById(R.id.intro_btn_skip);
            iv_indt0=rootView.findViewById(R.id.iv_indt0);
            iv_indt1=rootView.findViewById(R.id.iv_indt1);
            iv_indt2=rootView.findViewById(R.id.iv_indt2);
            iv_indt3=rootView.findViewById(R.id.iv_indt3);
            iv_indt4=rootView.findViewById(R.id.iv_indt4);
            iv_indt5=rootView.findViewById(R.id.iv_indt5);

            if (position==0){
                ll_create_template.setBackground(getResources().getDrawable(R.drawable.temp_1));
                iv_indt0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==1){
                ll_create_template.setBackground(getResources().getDrawable(R.drawable.temp_2));
                iv_indt1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==2){
                ll_create_template.setBackground(getResources().getDrawable(R.drawable.temp_3));
                iv_indt2.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==3){
                ll_create_template.setBackground(getResources().getDrawable(R.drawable.temp_4));
                iv_indt3.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==4){
                ll_create_template.setBackground(getResources().getDrawable(R.drawable.temp_5));
                iv_indt4.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==5){
                ll_create_template.setBackground(getResources().getDrawable(R.drawable.temp_6));
                iv_indt5.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                intro_btn_next.setText("Finish");
            }


            intro_btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(Global.TAG, "onClick: position "+position);
                    if (position==0){
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==1){
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==2){
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==3){
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==4){
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==5){
                        getActivity().finish();
                    }
                }
            });

            intro_btn_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    getActivity().finish();
                }
            });

            return rootView;
        }

    }

    public static class AddEmailOnBoardingFragment extends Fragment {

        int position;
        private Button intro_btn_next,intro_btn_skip;
        private LinearLayout ll_add_email;
        ImageView iv_addemail0,iv_addemail1;
        String string_key;

        @SuppressLint("ValidFragment")
        public AddEmailOnBoardingFragment(int position,String string_key) {
            this.position=position;
            this.string_key=string_key;

        }

        public AddEmailOnBoardingFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboarding_add_email, container, false);
            Log.d(Global.TAG, "AddEmail: "+position);
            ll_add_email=rootView.findViewById(R.id.ll_add_email);
            intro_btn_next=rootView.findViewById(R.id.intro_btn_next);
            intro_btn_skip=rootView.findViewById(R.id.intro_btn_skip);
            iv_addemail0=rootView.findViewById(R.id.iv_addemail0);
            iv_addemail1=rootView.findViewById(R.id.iv_addemail1);


            if (string_key.equals("add_email")){
                if (position==0){
                    ll_add_email.setBackground(getResources().getDrawable(R.drawable.addmail_1));
                    iv_addemail0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==1){
                    ll_add_email.setBackground(getResources().getDrawable(R.drawable.addmail_2));
                    iv_addemail1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                    intro_btn_next.setText("Finish");
                }
            }else if (string_key.equals("import_template")){
                if (position==0){
                    ll_add_email.setBackground(getResources().getDrawable(R.drawable.import_1));
                    iv_addemail0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==1){
                    ll_add_email.setBackground(getResources().getDrawable(R.drawable.import_2));
                    iv_addemail1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                    intro_btn_next.setText("Finish");
                }
            }else if (string_key.equals("self_rem")){
                if (position==0){
                    ll_add_email.setBackground(getResources().getDrawable(R.drawable.reminder_1));
                    iv_addemail0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==1){
                    ll_add_email.setBackground(getResources().getDrawable(R.drawable.reminder_2));
                    iv_addemail1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                    intro_btn_next.setText("Finish");
                }
            }



            intro_btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(Global.TAG, "onClick: position "+position);
                    if (position==0){
                        mViewPager.setCurrentItem(position+1);
                    }else if (position==1){
                        getActivity().finish();
                    }
                }
            });

            intro_btn_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    getActivity().finish();
                }
            });

            return rootView;
        }

    }


}


