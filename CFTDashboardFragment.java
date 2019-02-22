package com.success.successEntellus.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.activity.GiveAccessActivity;
import com.success.successEntellus.activity.SignUpActivity;
import com.success.successEntellus.activity.TopScoreActivity;
import com.success.successEntellus.adapter.MentorListAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.AccessUser;
import com.success.successEntellus.model.Approved;
import com.success.successEntellus.model.CFTAccessUsers;
import com.success.successEntellus.model.GetCustomizeModuleList;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.Mentor;
import com.success.successEntellus.model.RequestedApproved;
import com.success.successEntellus.model.RequestedRes;
import com.success.successEntellus.model.SingleModule;
import com.success.successEntellus.model.TopScoreRecruit;
import com.success.successEntellus.model.TopScoreRecruitList;
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
 * Created by user on 6/19/2018.
 */

public class CFTDashboardFragment extends Fragment implements TopScoreListAdapter.ResponseToFragment,MentorListAdapter.NotifyRefreshMenterList {
View layout;
    private static final String Tab_names[] = {"Weekly Score Card", "Calendar", "Weekly Graph"};
    private ViewPager view_pager_cft;
    TextView tv_no_users;
    private DachshundTabLayout tab_cft_dashboard;
    FrameLayout fl_no_cft_user;
    SPLib spLib;
    String[] usersArray;
    String[] usersIdArray;
    Spinner sp_select_user;
    List<String> accessModulesList;
    public static String from_user_id="-1";
    public static boolean tracking_access=false,calender_access=false;
    List<AccessUser> accessUserList;
    RecyclerView rv_approve_req,rv_top_score_recruits;
    Spinner sp_approved_recruits;
    Button btn_remove,btn_dissmiss,btn_top_dissmiss,btn_view_all;
    FrameLayout fl_no_req,fl_no_top_recruits;
    String[] approvedArray,approvedAccessIds;
    String remove_access_id;
    Dialog dialog,top_score_dialog;
    DashboardActivity dashboardActivity;
    TextView tv_approve_req_count,tv_feedback_message_count;
    int accessCount,messageCount;
    int selection;
    List<SingleModule> moduleList=new ArrayList<>();
    List<String> moduleIds=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_cft_dashboard,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        setHasOptionsMenu(true);

       /* if (!Global.isModulePresent(getActivity(),"8")){
            openDialogDisplayAlert();
        }*/

        init();
        getModuleDetails();
        getCftAccessUsers();
        sp_select_user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_select_user.getSelectedItem().toString().contains("Select")) {
                    from_user_id = "-1";
                    tracking_access=false;
                    calender_access=false;
                    view_pager_cft.setAdapter(new CFTDashboardFragment.PagerAdapter(getChildFragmentManager()));
                } else {
                    from_user_id = usersIdArray[position-1];
                    Log.d(Global.TAG, "onItemSelected:sp_select_user: "+from_user_id);
                    CFTWeeklyScoreCardFragment.from_user_id=from_user_id;
                    accessModulesList=accessUserList.get(position-1).getCftAccessModule();
                    Log.d(Global.TAG, "accessModulesList: "+accessModulesList);
                    if (accessModulesList.contains("1")){
                        tracking_access=true;
                    }else{
                        tracking_access=false;
                    }
                    if (accessModulesList.contains("2")){
                        calender_access=true;
                    }else{
                        calender_access=false;
                    }
                    Log.d(Global.TAG, "onItemSelected: Tracking Access: "+tracking_access+" Calender Access"+calender_access);
                    view_pager_cft.setAdapter(new CFTDashboardFragment.PagerAdapter(getChildFragmentManager()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return layout;
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
                        if (!moduleIds.contains("8")){
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

    private void getCftAccessUsers() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getCftAccessUsers: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<CFTAccessUsers> call=servive.get_cft_access_users(paramObj.toString());
        call.enqueue(new Callback<CFTAccessUsers>() {
            @Override
            public void onResponse(Call<CFTAccessUsers> call, Response<CFTAccessUsers> response) {
                CFTAccessUsers cftAccessUsers=response.body();
                if (cftAccessUsers.isSuccess()){
                    accessCount=cftAccessUsers.getAccessCount();
                    messageCount=cftAccessUsers.getMessageCount();
                    Log.d(Global.TAG, "onResponse: accessCount:"+accessCount+" messageCount:"+messageCount);

                    setupAccessCount();
                    setupMessageCount();

                    /*if (accessCount>0){
                       setupAccessCount();
                   }
                   if (messageCount>0){
                       setupMessageCount();
                   }*/

                   accessUserList=cftAccessUsers.getAccessUser();
                    Log.d(Global.TAG, "Access Users List: "+accessUserList.size());
                    if (accessUserList.size()>0){
                        usersArray=new String[accessUserList.size()];
                        usersIdArray=new String[accessUserList.size()];

                        for (int i=0;i<accessUserList.size();i++){
                            usersArray[i]=accessUserList.get(i).getUserName();
                            usersIdArray[i]=accessUserList.get(i).getCftAccessFromUserId();
                        }
                        Log.d(Global.TAG, "Users List: "+usersArray.length);
                        Log.d(Global.TAG, "Users ID List: "+usersIdArray.length);
                        applySpinner(usersArray,sp_select_user,"--Please Select--");
                    }else{
                        usersArray=new String[accessUserList.size()];
                       // usersIdArray=new String[accessUserList.size()];
                        applySpinner(usersArray,sp_select_user,"--Please Select--");
                        Toast.makeText(getActivity(), "No Users Available..!", Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CFTAccessUsers> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getCFTUsers:"+t);
            }
        });

    }
    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(getActivity(), android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }

    private void init() {
        fl_no_cft_user = (FrameLayout) layout.findViewById(R.id.fl_no_cft_user);
        tv_no_users = (TextView) layout.findViewById(R.id.tv_no_users);
        sp_select_user = (Spinner) layout.findViewById(R.id.sp_select_user);
        view_pager_cft = (ViewPager) layout.findViewById(R.id.view_pager_cft);
        view_pager_cft.setAdapter(new CFTDashboardFragment.PagerAdapter(getChildFragmentManager()));
        tab_cft_dashboard = (DachshundTabLayout) layout.findViewById(R.id.tab_cft_dashboard);
        tab_cft_dashboard.setupWithViewPager(view_pager_cft);
        spLib=new SPLib(getActivity());
        dashboardActivity= (DashboardActivity) getActivity();

    }

    @Override
    public void selectRecruit(String userId) {
        Log.d(Global.TAG, "selectRecruit: UserId:"+userId);
        top_score_dialog.dismiss();
        CFTWeeklyScoreCardFragment.from_user_id=userId;
       // view_pager_cft.setAdapter(new CFTDashboardFragment.PagerAdapter(getChildFragmentManager()));
        for (int i=0;i<usersIdArray.length;i++){
            if (usersIdArray[i].equals(userId)){
                Log.d(Global.TAG, "selectRecruit: Equals: "+i);
               selection=i;
            }
        }
        Log.d(Global.TAG, "selectRecruit:selection "+selection);
        sp_select_user.setSelection(selection+1);

    }

    @Override
    public void refreshMenterList() {
        getMentorRequestedAndApproved();

    }

    public static class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if (position==0)
                return new CFTWeeklyScoreCardFragment(from_user_id,tracking_access);
            else if(position==1){
                return new CFTCalenderFragment(from_user_id,calender_access);
            }else if(position==2){
                return new CFTWeeklyGraphFragment(from_user_id,tracking_access);
            }
            return new CFTWeeklyScoreCardFragment(from_user_id,tracking_access);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cft_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem menuItem = menu.findItem(R.id.action_approve_access);
        final MenuItem menuItem1 = menu.findItem(R.id.action_feedback);

        View actionView = MenuItemCompat.getActionView(menuItem);
        View actionView1 = MenuItemCompat.getActionView(menuItem1);
        tv_approve_req_count = actionView.findViewById(R.id.tv_approve_req_count);
        tv_feedback_message_count = actionView1.findViewById(R.id.tv_feedback_message_count);
        //setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        actionView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem1);
            }
        });
    }

    private void setupAccessCount() {
        Log.d(Global.TAG, "setupAccessCount: ");
        if (tv_approve_req_count != null) {
            if (accessCount == 0) {
                tv_approve_req_count.setVisibility(View.GONE);
                Log.d(Global.TAG, "setupAccessCount: "+accessCount);

            } else {
                    Log.d(Global.TAG, "setupAccessCount: "+accessCount);
                    tv_approve_req_count.setVisibility(View.VISIBLE);
                    tv_approve_req_count.setText(String.valueOf(Math.min(accessCount, 99)));
            }
        }
    }
    private void setupMessageCount() {
        Log.d(Global.TAG, "setupMessageCount: ");
        if (tv_feedback_message_count != null) {
            if (messageCount == 0) {
                    tv_feedback_message_count.setVisibility(View.GONE);
                    Log.d(Global.TAG, "setupMessageCount: 0 ");
            } else {
                tv_feedback_message_count.setVisibility(View.VISIBLE);
                tv_feedback_message_count.setText(String.valueOf(Math.min(messageCount, 99)));
                Log.d(Global.TAG, "setupMessageCount: "+messageCount);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_access_setting:
                startActivity(new Intent(getActivity(), GiveAccessActivity.class));
                break;
            case R.id.menu_top_recruit_score:
                openDialogTopScoreRecruits();
                break;
            case R.id.action_approve_access:
                openDialogApproveAccess();
                break;
            case R.id.action_feedback:
                 dashboardActivity.replaceFragments(new CFTFeedBackFragment());
                //startActivity(new Intent(getActivity(), CFTFeedbackActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialogTopScoreRecruits() {
        top_score_dialog = new Dialog(getActivity());
        top_score_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        top_score_dialog.setCancelable(false);
        top_score_dialog.setContentView(R.layout.dialog_top_score_recruits);
        top_score_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        top_score_dialog.setTitle("Confirmation");

        rv_top_score_recruits = (RecyclerView) top_score_dialog.findViewById(R.id.rv_top_recruits_list);
        rv_top_score_recruits.setLayoutManager(new LinearLayoutManager(getActivity()));
        btn_top_dissmiss = (Button) top_score_dialog.findViewById(R.id.btn_top_dissmiss);
        btn_view_all = (Button) top_score_dialog.findViewById(R.id.btn_view_all);
        fl_no_top_recruits = (FrameLayout) top_score_dialog.findViewById(R.id.fl_no_top_recruits);

        getTopScoreList();
        btn_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                top_score_dialog.dismiss();
               // dashboardActivity.replaceFragments(new TopScoreRecruitFragment());
                startActivity(new Intent(getActivity(), TopScoreActivity.class));
            }
        });

        btn_top_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                top_score_dialog.dismiss();
            }
        });

        top_score_dialog.show();
        Window window = top_score_dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void getTopScoreList() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("limit", "10");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getTopScoreList: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<TopScoreRecruitList> call=servive.get_top_score_list(paramObj.toString());
        call.enqueue(new Callback<TopScoreRecruitList>() {
            @Override
            public void onResponse(Call<TopScoreRecruitList> call, Response<TopScoreRecruitList> response) {
                TopScoreRecruitList topScoreRecruitList=response.body();
                if (topScoreRecruitList.isSuccess()){
                    List<TopScoreRecruit> topScoreRecruits=topScoreRecruitList.getResult();
                    Log.d(Global.TAG, "onResponse:topScoreRecruits: "+topScoreRecruits.size());
                    if (topScoreRecruits.size()>0){
                        fl_no_top_recruits.setVisibility(View.GONE);
                        TopScoreListAdapter topScoreListAdapter=new TopScoreListAdapter(getActivity(),topScoreRecruits,CFTDashboardFragment.this);
                        rv_top_score_recruits.setAdapter(topScoreListAdapter);
                    }else{
                        rv_top_score_recruits.setVisibility(View.GONE);
                        fl_no_top_recruits.setVisibility(View.VISIBLE);
                        btn_view_all.setEnabled(false);
                    }
                }else{
                    rv_top_score_recruits.setVisibility(View.GONE);
                    fl_no_top_recruits.setVisibility(View.VISIBLE);
                    btn_view_all.setEnabled(false);
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<TopScoreRecruitList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: tOPScore "+t);
                rv_top_score_recruits.setVisibility(View.GONE);
                fl_no_top_recruits.setVisibility(View.VISIBLE);
                btn_view_all.setEnabled(false);
            }
        });
    }

    private void openDialogApproveAccess() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.cft_approve_access_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        rv_approve_req = (RecyclerView) dialog.findViewById(R.id.rv_approve_req);
        rv_approve_req.setLayoutManager(new LinearLayoutManager(getActivity()));
        sp_approved_recruits = (Spinner) dialog.findViewById(R.id.sp_approved_recruits);
        btn_remove = (Button) dialog.findViewById(R.id.btn_remove);
        btn_dissmiss = (Button) dialog.findViewById(R.id.btn_dissmiss);
        fl_no_req = (FrameLayout) dialog.findViewById(R.id.fl_no_req);

        getMentorRequestedAndApproved();

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!remove_access_id.equals("")){
                    removeRecruit();
                }else{
                    Toast.makeText(dashboardActivity, "Please Select Valid User..!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getCftAccessUsers();
            }
        });

        sp_approved_recruits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_approved_recruits.getSelectedItem().toString().contains("Select")) {
                    remove_access_id = "";
                } else {
                    remove_access_id = approvedAccessIds[position - 1];
                    Log.d(Global.TAG, "onItemSelected:remove_access_id: " + remove_access_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void removeRecruit() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("cftUserId", remove_access_id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "removeRecruit: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=servive.removeAccessRecruit(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(getActivity(), ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    getCftAccessUsers();
                }else{
                    Toast.makeText(getActivity(), ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:removeRecruit "+t);
            }
        });
    }

    private void getMentorRequestedAndApproved() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getMentorRequestedAndApproved: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<RequestedApproved> call=servive.getRequestedApprovedRequests(paramObj.toString());
        call.enqueue(new Callback<RequestedApproved>() {
            @Override
            public void onResponse(Call<RequestedApproved> call, Response<RequestedApproved> response) {
                RequestedApproved requestedApproved=response.body();
                if (requestedApproved.isSuccess()){
                    RequestedRes requestedRes=requestedApproved.getResult();
                    List<Mentor> requestedList=requestedRes.getRequested();
                    List<Approved> approvedList=requestedRes.getApproved();
                    Log.d(Global.TAG, "requestedList: "+requestedList.size());
                    Log.d(Global.TAG, "approvedList: "+approvedList.size());

                    approvedArray=new String[approvedList.size()];
                    approvedAccessIds=new String[approvedList.size()];
                    if (requestedList.size()>0){
                        rv_approve_req.setVisibility(View.VISIBLE);
                        fl_no_req.setVisibility(View.GONE);
                        MentorListAdapter adapter=new MentorListAdapter(getActivity(),requestedList,true,CFTDashboardFragment.this);
                        rv_approve_req.setAdapter(adapter);
                    }else{
                        rv_approve_req.setVisibility(View.GONE);
                        fl_no_req.setVisibility(View.VISIBLE);
                    }
                    for (int i=0;i<approvedList.size();i++){
                        approvedArray[i]=approvedList.get(i).getUserName();
                        approvedAccessIds[i]=approvedList.get(i).getAccessId();
                    }
                    applySpinner(approvedArray,sp_approved_recruits,"--Please Select--");
                    if (approvedList.size()==0){
                        btn_remove.setEnabled(false);
                    }

                }else{
                    rv_approve_req.setVisibility(View.GONE);
                    fl_no_req.setVisibility(View.VISIBLE);
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<RequestedApproved> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: RequestedApproved:"+t);
                rv_approve_req.setVisibility(View.GONE);
                fl_no_req.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("CFT DashBoard");
    }
}
