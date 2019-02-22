package com.success.successEntellus.fragment;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;

/**
 * Created by user on 9/22/2018.
 */

public class CreateCampaignOnBoardingFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    int position;
    private Button intro_btn_next,intro_btn_skip;
    private LinearLayout ll_create_campaign;

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


        if (position==0){
            ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.create_camp1));
        }else if (position==1){
            ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.create_camp2));
        }else if (position==2){
            ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.create_camp3));
            intro_btn_next.setText("Finish");
        }


        intro_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "onClick: position "+position);
                if (position==0){
                    Log.d(Global.TAG, "Position 0 clicked: ");
                    replaceFragments(new CreateCampaignOnBoardingFragment(position+1));
                    ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.create_camp2));
                }else if (position==1){
                    Log.d(Global.TAG, "Position 1 clicked: ");
                    replaceFragments(new CreateCampaignOnBoardingFragment(position+1));
                     ll_create_campaign.setBackground(getResources().getDrawable(R.drawable.create_camp3));
                }else if (position==2){
                    Log.d(Global.TAG, "onClick: Finish Clicked. ");
                }
            }
        });

        intro_btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                   /* if (position==0){
                        ll_create_campaign.setBackground(getActivity().getResources().getDrawable(R.drawable.e2));
                    }else if (position==1){
                        ll_create_campaign.setBackground(getActivity().getResources().getDrawable(R.drawable.e3));
                    }*/
            }
        });

        return rootView;
    }
    public void replaceFragments(android.support.v4.app.Fragment fragment) {
        Log.d(Global.TAG, "replaceFragment: ");
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack("demo");
        transaction.commit();
        return;
    }
}
