package com.success.successEntellus.activity;

import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.success.successEntellus.R;
import com.success.successEntellus.fragment.CFTMentorFeedbackFragment;
import com.success.successEntellus.fragment.CFTRecruitsFeedbackFragment;
import com.success.successEntellus.lib.Global;

public class CFTFeedbackActivity extends AppCompatActivity {
    private static final String Tab_names[] = {"CFT/Mentor's Feedback", "Feedback For Recruits"};
    private ViewPager view_pager_cft_feedback;
    private DachshundTabLayout tab_cft_feedback;
    Toolbar toolbar_feedback;
    ImageButton ib_feedback_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.fragment_cft_feedback);

        init();
    }

    private void init() {

        view_pager_cft_feedback = (ViewPager)findViewById(R.id.view_pager_cft_feedback);
        view_pager_cft_feedback.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tab_cft_feedback = (DachshundTabLayout)findViewById(R.id.tab_cft_feedback);
        tab_cft_feedback.setupWithViewPager(view_pager_cft_feedback);
        toolbar_feedback=findViewById(R.id.toolbar_feedback);
        ib_feedback_back=(ImageButton)findViewById(R.id.ib_feedback_back);
        //tv_toolbar_name=(TextView) findViewById(R.id.tv_mentor_gname);

        ib_feedback_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // getActivity().getSupportFragmentManager().popBackStackImmediate();
                finish();
            }
        });
        //spLib=new SPLib(getActivity());
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if (position==0)
                return new CFTMentorFeedbackFragment();
            else if(position==1){
                return new CFTRecruitsFeedbackFragment();
            }
            return new CFTRecruitsFeedbackFragment();
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

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cft_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }*/

    /*@Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("CFT FeedBack");
    }*/

    public void replaceFragments(android.support.v4.app.Fragment fragment) {
        Log.d(Global.TAG, "replaceFragment: ");
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack("demo");
        transaction.commit();
        return;
    }

}
