package com.success.successEntellus.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.success.successEntellus.R;
import com.success.successEntellus.fragment.AddEditGoalsFragment;
import com.success.successEntellus.fragment.DailyCheckListFragment;
import com.success.successEntellus.fragment.DailyCheckListFragmentNew;
import com.success.successEntellus.fragment.DailyCheckListNew;
import com.success.successEntellus.fragment.DailyScoreGraphFragment;
import com.success.successEntellus.fragment.WeeklyScorecardFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.GetCustomizeModuleList;
import com.success.successEntellus.model.SingleModule;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 3/27/2018.
 */

public class DashboardFragment extends Fragment {
    private static final String Tab_names[] = {"Daily CheckList", "Daily Score Graph", "Weekly Scorecard"};
    private ViewPager viewPager;
    private DachshundTabLayout tabLayout;
    View layout;
    boolean golSetFlag;
    AddEditGoalsFragment addEditGoalsFragment;
    SPLib spLib;
    List<SingleModule> moduleList=new ArrayList<>();
    List<String> moduleIds=new ArrayList<>();

    @SuppressLint("ValidFragment")
    public DashboardFragment(boolean golSetFlag) {
        this.golSetFlag=golSetFlag;
    }

    public DashboardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.activity_home,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        setHasOptionsMenu(true);
        addEditGoalsFragment=new AddEditGoalsFragment(false);
        viewPager = (ViewPager) layout.findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));

        tabLayout = (DachshundTabLayout) layout.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        spLib=new SPLib(getActivity());

       /* if (!Global.isModulePresent(getActivity(),"17")){
            openDialogDisplayAlert();
        }*/
        getModuleDetails();

       /* if (!golSetFlag){
            addEditGoalsFragment.setUserGoalsOnBoard();
        }*/
       /* LineMoveIndicator lineMoveIndicator = new LineMoveIndicator(tabLayout);
        tabLayout.setAnimatedIndicator(new LineMoveIndicator(tabLayout));

        lineMoveIndicator.setSelectedTabIndicatorColor(Color.parseColor("#d35400"));
        lineMoveIndicator.setSelectedTabIndicatorHeight(HelperUtils.dpToPx(2));
        lineMoveIndicator.setEdgeRadius(2);*/
        return layout;
    }

    public void openDialogDisplayAlert() {
        Button btn_upgrade_dissmiss,btn_upgrade_ok;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.upgrade_package_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        btn_upgrade_dissmiss=(Button) dialog.findViewById(R.id.btn_upgrade_dissmiss);
       // btn_upgrade_ok=(Button) dialog.findViewById(R.id.btn_upgrade_ok);

        btn_upgrade_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });

       /* btn_upgrade_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SignUpActivity.class));
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });*/

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private void getModuleDetails() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getModuleDetails: "+paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCustomizeModuleList> call = service.getModuleDetails(paramObj.toString());
        call.enqueue(new Callback<GetCustomizeModuleList>() {
            @Override
            public void onResponse(Call<GetCustomizeModuleList> call, Response<GetCustomizeModuleList> response) {
                GetCustomizeModuleList getCustomizeModuleList=response.body();
                if (getCustomizeModuleList!=null){
                    if (getCustomizeModuleList.isSuccess()){
                        moduleList=getCustomizeModuleList.getResult();
                        Log.d(Global.TAG, "onResponse: Module List:"+moduleList.size());
                        moduleIds.clear();
                        for(int i=0;i<moduleList.size();i++){
                            moduleIds.add(moduleList.get(i).getModuleId());
                        }
                        Log.d(Global.TAG, "moduleIds: "+moduleIds.size());
                        spLib.saveArrayList(moduleList,SPLib.Key.MODULELIST);
                        //moduleList=spLib.getArrayList(SPLib.Key.MODULELIST);
                        // Log.d(Global.TAG, "ModuleList from spLib: "+moduleList.size());
                        if (!moduleIds.contains("17")){
                            openDialogDisplayAlert();
                        }
                    }else{
                        Toast.makeText(getActivity(), ""+getCustomizeModuleList.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }

                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetCustomizeModuleList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:getModuleDetails: "+t);
            }
        });

    }
    public static class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if (position==0)
                return new DailyCheckListFragmentNew();
            else if(position==1){
                return new DailyScoreGraphFragment();
            }else if(position==2){
                return new WeeklyScorecardFragment();
            }
            return new DailyCheckListFragmentNew();
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

    @Override
    public void onPause() {
        super.onPause();
        Log.d(Global.TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Dashboard");
//        viewPager = (ViewPager) layout.findViewById(R.id.view_pager);
//        viewPager.setAdapter(new PagerAdapter(getActivity().getSupportFragmentManager()));
//        Log.d(Global.TAG, "onResume: ");
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
