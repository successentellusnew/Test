package com.success.successEntellus.activity;

import android.annotation.SuppressLint;
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

public class OnboardingTextCampaign extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static ViewPager mViewPager;
    String keyString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_text_campaign);

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            keyString=bundle.getString("keyString");
            Log.d(Global.TAG, " keystring Text Camp: "+keyString);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),keyString);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }



    public static class CreateTextCampFragment extends Fragment {

        int position;
        private Button intro_btn_next,intro_btn_skip;
        private LinearLayout ll_add_email;
        ImageView iv_addemail0,iv_addemail1;
        String string_key;


        @SuppressLint("ValidFragment")
        public CreateTextCampFragment(int position) {
            this.position=position;
        }

        public CreateTextCampFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_onboarding_text_campaign, container, false);
            View rootView = inflater.inflate(R.layout.fragment_onboarding_add_email, container, false);

            ll_add_email=rootView.findViewById(R.id.ll_add_email);
            intro_btn_next=rootView.findViewById(R.id.intro_btn_next);
            intro_btn_skip=rootView.findViewById(R.id.intro_btn_skip);
            iv_addemail0=rootView.findViewById(R.id.iv_addemail0);
            iv_addemail1=rootView.findViewById(R.id.iv_addemail1);

            if (position==0){
                ll_add_email.setBackground(getResources().getDrawable(R.drawable.txt_camp1));
                iv_addemail0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==1){
                ll_add_email.setBackground(getResources().getDrawable(R.drawable.txt_camp2));
                iv_addemail1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                intro_btn_next.setText("Finish");
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

    public static class CreateTextMessageOnBoardingFragment extends Fragment {

        int position;
        private Button intro_btnt_next,intro_btnt_skip;
        private LinearLayout ll_create_text_template;
        ImageView iv_indt0,iv_indt1,iv_indt2,iv_indt3,iv_indt4,iv_indt5;
        String strKey;

        @SuppressLint("ValidFragment")
        public CreateTextMessageOnBoardingFragment(int position,String strkey ) {
            this.position=position;
            this.strKey=strkey;
        }

        public CreateTextMessageOnBoardingFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboarding_text_campaign, container, false);

            ll_create_text_template=rootView.findViewById(R.id.ll_create_text_template);
            intro_btnt_next=rootView.findViewById(R.id.intro_btnt_next);
            intro_btnt_skip=rootView.findViewById(R.id.intro_btnt_skip);
            iv_indt0=rootView.findViewById(R.id.iv_indtc0);
            iv_indt1=rootView.findViewById(R.id.iv_indtc1);
            iv_indt2=rootView.findViewById(R.id.iv_indtc2);
            iv_indt3=rootView.findViewById(R.id.iv_indtc3);
            iv_indt4=rootView.findViewById(R.id.iv_indtc4);


            if (strKey.equals("create_text_msg")){
                if (position==0){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.msg_1));
                    iv_indt0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==1){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.msg_2));
                    iv_indt1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==2){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.msg_3));
                    iv_indt2.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==3){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.msg_4));
                    iv_indt3.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==4){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.msg_5));
                    iv_indt4.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                    intro_btnt_next.setText("Finish");
                }
            }else if (strKey.equals("daily_checklist")){
                if (position==0){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.checklist_1));
                    iv_indt0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==1){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.checklist_2));
                    iv_indt1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==2){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.checklist_3));
                    iv_indt2.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==3){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.checklist_4));
                    iv_indt3.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==4){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.checklist_5));
                    iv_indt4.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                    intro_btnt_next.setText("Finish");
                }
            }else if (strKey.equals("cft_locator")){
                if (position==0){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.cft1));
                    iv_indt0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==1){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.cft2));
                    iv_indt1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==2){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.cft3));
                    iv_indt2.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==3){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.cft4));
                    iv_indt3.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                }else if (position==4){
                    ll_create_text_template.setBackground(getResources().getDrawable(R.drawable.cft5));
                    iv_indt4.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
                    intro_btnt_next.setText("Finish");
                }
            }



            intro_btnt_next.setOnClickListener(new View.OnClickListener() {
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
                        getActivity().finish();
                    }
                }
            });

            intro_btnt_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    getActivity().finish();
                }
            });

            return rootView;
        }

    }


    public static class AddMemberTextCampFragment extends Fragment {

        int position;
        private Button intro_btn_next,intro_btn_skip;
        private LinearLayout ll_create_campaign;
        ImageView iv_ind0,iv_ind1,iv_ind2;

        @SuppressLint("ValidFragment")
        public AddMemberTextCampFragment(int position) {
            this.position=position;
        }

        public AddMemberTextCampFragment() {

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
                ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.add_member1));
                iv_ind0.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==1){
                ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.add_member2));
                iv_ind1.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_solid));
            }else if (position==2){
                ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.add_member3));
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        String key;
        int count;
        public SectionsPagerAdapter(FragmentManager fm, String keyString) {
            super(fm);
            this.key=keyString;

            if (key.equals("create_text_camp")){
                count=2;
            }else if (key.equals("create_text_msg")){
                count=5;
            }else if (key.equals("add_member")){
                count=3;
            }else if (key.equals("daily_checklist")){
                count=5;
            }else if (key.equals("cft_locator")){
                count=5;
            }

        }

        @Override
        public Fragment getItem(int position) {
            if (key.equals("create_text_camp")){
                return new CreateTextCampFragment(position);
            }else if (key.equals("create_text_msg")){
                return new CreateTextMessageOnBoardingFragment(position,"create_text_msg");
            }else if (key.equals("add_member")){
                return new AddMemberTextCampFragment(position);
            }else if (key.equals("daily_checklist")){
                return new CreateTextMessageOnBoardingFragment(position,"daily_checklist");
            }else if (key.equals("cft_locator")){
                return new CreateTextMessageOnBoardingFragment(position,"cft_locator");
            }

            return new CreateTextCampFragment(position);
        }

        @Override
        public int getCount() {
            return count;
        }
    }
}
