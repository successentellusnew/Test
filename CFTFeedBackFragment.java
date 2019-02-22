package com.success.successEntellus.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.success.successEntellus.R;

/**
 * Created by user on 6/22/2018.
 */

public class CFTFeedBackFragment extends Fragment{
    View layout;
    private static final String Tab_names[] = {"CFT/Mentor's Feedback", "Feedback For Recruits"};
    private ViewPager view_pager_cft_feedback;
    private DachshundTabLayout tab_cft_feedback;
    Toolbar toolbar_feedback;
    ImageButton ib_feedback_back;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        layout=inflater.inflate(R.layout.fragment_cft_feedback,container,false);
       // setHasOptionsMenu(true);
        init();
        return layout;
    }

    private void init() {

        view_pager_cft_feedback = (ViewPager) layout.findViewById(R.id.view_pager_cft_feedback);
        view_pager_cft_feedback.setAdapter(new PagerAdapter(getChildFragmentManager()));
        tab_cft_feedback = (DachshundTabLayout) layout.findViewById(R.id.tab_cft_feedback);
        tab_cft_feedback.setupWithViewPager(view_pager_cft_feedback);
        toolbar_feedback=layout.findViewById(R.id.toolbar_feedback);
        ib_feedback_back=(ImageButton)layout.findViewById(R.id.ib_feedback_back);
        //tv_toolbar_name=(TextView) findViewById(R.id.tv_mentor_gname);

        ib_feedback_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        //spLib=new SPLib(getActivity());
    }

    public static class PagerAdapter extends FragmentStatePagerAdapter {
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
}
