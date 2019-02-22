package com.success.successEntellus.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.AddGroupActivity;
import com.success.successEntellus.fragment.MyGroupsFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.model.AssignCampaign;
import com.success.successEntellus.model.AssignUnAssign;
import com.success.successEntellus.model.AssignUnAssignCampaign;
import com.success.successEntellus.model.ContactGroup;
import com.success.successEntellus.model.GroupDetails;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.SingleGroup;
import com.success.successEntellus.model.UnAssignCampaign;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.GroupViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 4/30/2018.
 */

public class MyGroupsAdapter extends RecyclerView.Adapter<GroupViewHolder>{
    Context context;
    List<SingleGroup> groupList;
    LayoutInflater inflater;
    List<String> assignCampaignList;
    List<String> groupMembersList;
    String group_members="",group_types="";
    String user_id;
    boolean checked;
    MyGroupsFragment myGroupsFragment=new MyGroupsFragment();
    private String[] assignCampaignArray;
    private String selected_campaign="";
    List<AssignCampaign> campaignassignCampaignList;
    GroupDetails groupDetails;
    List<String> contactnameList=new ArrayList<>();
    List<String> prospectnameList=new ArrayList<>();
    List<String> customersnameList=new ArrayList<>();
    List<String> recruitsnameList=new ArrayList<>();
    List<String> campList=new ArrayList<>();
    AddGroupActivity addGroupActivity=new AddGroupActivity();
    Dialog dialog;
    MyGroupsFragment myGroupsFragment1;
    public interface NotifyRefreshGroup{
        void refreshGroups();
    }
    NotifyRefreshGroup notifyRefreshGroup;
    Spinner sp_assign_campaign ;
    LinearLayout ll_assigned_campaigns,ll_added_campaigns;
    TextView tv_no_campaigns_added,tv_no_campaigns_assignes,tv_added_campaigns_group;


    public MyGroupsAdapter(Context context, MyGroupsFragment myGroupsFragment, List<SingleGroup> groupList, String user_id, boolean checked,NotifyRefreshGroup notifyRefreshGroup) {
        this.context=context;
        this.myGroupsFragment1=myGroupsFragment;
        this.user_id=user_id;
        this.groupList=groupList;
        inflater= (LayoutInflater) context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        groupMembersList=new ArrayList<>();
        this.checked=checked;
        this.notifyRefreshGroup=notifyRefreshGroup;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layout=inflater.inflate(R.layout.group_row,parent,false);
        GroupViewHolder contactViewHolder=new GroupViewHolder(context,layout,groupList);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, final int position) {

        holder.group_id=groupList.get(position).getGroup_id();
        holder.position=position;
        holder.user_id=user_id;

        holder.tv_group_name.setText(groupList.get(position).getGroup_name());
        holder.tv_group_type.setText(groupList.get(position).getCampaign_name());

        if (checked){
            holder.ch_ccheck.setChecked(true);
            if ( !myGroupsFragment.deleteGroupList.contains(groupList.get(position).getGroup_id())){
                myGroupsFragment.deleteGroupList.add(groupList.get(position).getGroup_id());
            }
        }else{
            holder.ch_ccheck.setChecked(false);
            if ( myGroupsFragment.deleteGroupList.contains(groupList.get(position).getGroup_id())){
                myGroupsFragment.deleteGroupList.remove(groupList.get(position).getGroup_id());
            }
        }
        Log.d(Global.TAG, "deleteGroupList: "+myGroupsFragment.deleteGroupList);
        holder.ch_ccheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    if (myGroupsFragment.deleteGroupList.contains(groupList.get(position).getGroup_id())){
                        Log.d(Global.TAG, "Already Exist..!: ");
                    }else{
                        myGroupsFragment.deleteGroupList.add(groupList.get(position).getGroup_id());
                        Log.d(Global.TAG, "onCheckedChanged:add List:"+myGroupsFragment.deleteGroupList);
                    }
                }else{
                    if (myGroupsFragment.deleteGroupList.contains(groupList.get(position).getGroup_id())){
                        myGroupsFragment.deleteGroupList.remove(groupList.get(position).getGroup_id());
                        Log.d(Global.TAG, "onCheckedChanged:remove List:"+myGroupsFragment.deleteGroupList);
                    }
                }

            }
        });

        //groupMembersList.clear();
        groupMembersList=groupList.get(position).getGroup_member_names();
        //group_type_list=groupList.get(position).getGroup_type();
        Log.d(Global.TAG, "Group Member List Adapter: "+groupMembersList.size());
        Log.d(Global.TAG, "Group Type List Adapter: "+groupMembersList.size());
        group_members="";
        group_types="";

        if (groupMembersList.size()>0){
            for (String member : groupMembersList)
            {
                group_members += member + ",";
            }
            if (group_members.endsWith(",")) {
                group_members = group_members.substring(0, group_members.length() - 1);
            }
        }
        /*if (group_type_list.size()>0){
            for (String type : group_type_list)
            {
                group_types += type + ",";
            }
            if (group_types.endsWith(",")) {
                group_types = group_types.substring(0, group_types.length() - 1);
            }
        }*/
        Log.d(Global.TAG, "Group Members: "+group_members);
        //Log.d(Global.TAG, "Group types: "+group_types);


//        holder.tv_group_members.setText(group_members);
//        holder.tv_group_type.setText(group_types);
        if (groupMembersList.size()>0){
            holder.tv_group_members.setText(group_members);
        }
       /* if (group_type_list.size()>0){
            holder.tv_group_type.setText(group_types);
        }*/


        holder.ib_delete_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure, want to delete this group..?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteGroup(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        holder.ib_assign_campaign_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAssignCampaign(position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGroupDetailsOnId(false,position);
            }
        });

        holder.ib_edit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editGroupDetails(position);
            }
        });
    }

    private void openDialogAssignCampaign(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.group_assign_campaign);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        Button btn_assign_campaign_dissmiss = (Button) dialog.findViewById(R.id.btn_assign_campaign_dissmiss);
        Button btn_assign_campaign = (Button) dialog.findViewById(R.id.btn_assign_campaign);
        Button btn_assign_cancel = (Button) dialog.findViewById(R.id.btn_assign_cancel);
        TextView tv_ass_group_name = (TextView) dialog.findViewById(R.id.tv_ass_group_name);
        TextView tv_assign_dialog_title = (TextView) dialog.findViewById(R.id.tv_assign_dialog_title);
        TextView tv_assign_title = (TextView) dialog.findViewById(R.id.tv_assign_title);
        sp_assign_campaign = (Spinner) dialog.findViewById(R.id.sp_assign_campaign);
        ll_assigned_campaigns = (LinearLayout) dialog.findViewById(R.id.ll_assigned_campaigns);
       // ll_added_campaigns = (LinearLayout) dialog.findViewById(R.id.ll_added_campaigns);
        tv_no_campaigns_added = (TextView) dialog.findViewById(R.id.tv_no_campaigns_added);
        tv_no_campaigns_assignes = (TextView) dialog.findViewById(R.id.tv_no_campaigns_assignes);
        tv_added_campaigns_group = (TextView) dialog.findViewById(R.id.tv_added_campaigns_group);
        //ll_added_campaigns.setVisibility(View.GONE);

        tv_assign_title.setText("Group Name:");
        tv_assign_dialog_title.setText("Add Group to Email campaign");
        tv_ass_group_name.setText(groupList.get(position).getGroup_name());
        btn_assign_campaign_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_assign_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_assign_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sp_assign_campaign.getSelectedItem().toString().contains("Select")){
                    assignCampaigntoGroup(position,sp_assign_campaign,ll_assigned_campaigns);
                }else{
                    MyValidator.setSpinnerError(sp_assign_campaign,"select_campaign");
                    Toast.makeText(context, "Please Select Camapign to Add to group..!", Toast.LENGTH_LONG).show();
                }

            }
        });

        getAssignedUnassignedCampaigns(position);

        sp_assign_campaign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_assign_campaign.getSelectedItem().toString().contains("Select")) {
                    selected_campaign = "";
                } else {
                    selected_campaign = campaignassignCampaignList.get(position-1).getCampaignId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void assignCampaigntoGroup(final int position, final Spinner sp_assign_campaign, final LinearLayout ll_assigned_campaigns) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("groupId", groupList.get(position).getGroup_id());
            paramObj.put("campaignId", selected_campaign);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "assignCampaigntoGroup: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "assignCampaigntoGroup: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.assign_campaign_to_group(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    //myGroupsFragment.getGroupsDetails();
                    notifyRefreshGroup.refreshGroups();
                    getAssignedUnassignedCampaigns(position);
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: assignCampaign "+t);
            }
        });
    }

    private void getAssignedUnassignedCampaigns(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("groupId", groupList.get(position).getGroup_id());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAssignedUnassignedCampaigns: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAssignedUnassignedCampaigns: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<AssignUnAssignCampaign> call=service.getAssignUnAssignCampaign(paramObj.toString());
        call.enqueue(new Callback<AssignUnAssignCampaign>() {
            @Override
            public void onResponse(Call<AssignUnAssignCampaign> call, Response<AssignUnAssignCampaign> response) {
                AssignUnAssignCampaign assignUnAssignCampaign=response.body();
                if (assignUnAssignCampaign.isSuccess()){
                    AssignUnAssign assignUnAssign=assignUnAssignCampaign.getResult();
                    campaignassignCampaignList=assignUnAssign.getAssingCamp();
                    List<UnAssignCampaign> unAssignCampaignList=assignUnAssign.getUnAssingCamp();
                    List<UnAssignCampaign> addedCampaignList=assignUnAssign.getAddedCamp();

                    Log.d(Global.TAG, "assignCampaignList: "+campaignassignCampaignList.size());
                    Log.d(Global.TAG, "unAssignCampaignList: "+unAssignCampaignList.size());
                    Log.d(Global.TAG, "addedCampaignList: "+addedCampaignList.size());

                    assignCampaignArray=new String[campaignassignCampaignList.size()];
                    for (int i=0;i<campaignassignCampaignList.size();i++){
                        assignCampaignArray[i]=campaignassignCampaignList.get(i).getCampaignTitle();
                    }
                    applySpinner(assignCampaignArray,sp_assign_campaign,"--Select Campaign--");

                    if (unAssignCampaignList.size()>0){
                        tv_no_campaigns_assignes.setVisibility(View.GONE);
                        ll_assigned_campaigns.setVisibility(View.VISIBLE);
                        tv_added_campaigns_group.setVisibility(View.GONE);
                        addAssignedCampaigns(unAssignCampaignList,ll_assigned_campaigns,position,sp_assign_campaign);
                    }else{
                        tv_added_campaigns_group.setVisibility(View.GONE);
                        ll_assigned_campaigns.removeAllViews();
                        ll_assigned_campaigns.setVisibility(View.GONE);
                        tv_no_campaigns_assignes.setVisibility(View.VISIBLE);
                    }

                    if (addedCampaignList.size()>0){
                        tv_no_campaigns_added.setVisibility(View.GONE);
                        tv_added_campaigns_group.setVisibility(View.VISIBLE);

                        String groupCampaigns = "";
                        for (int i=0;i<addedCampaignList.size();i++){
                            groupCampaigns += addedCampaignList.get(i).getCampaignTitle() + ",";
                        }

                        if (groupCampaigns.endsWith(",")) {
                            groupCampaigns = groupCampaigns.substring(0, groupCampaigns.length() - 1);
                        }
                        Log.d(Global.TAG, "groupCampaigns String: "+groupCampaigns);
                        tv_added_campaigns_group.setText(groupCampaigns);

                    }else{
                        tv_no_campaigns_added.setVisibility(View.VISIBLE);
                        tv_added_campaigns_group.setVisibility(View.GONE);
                    }

                }else{
                    Toast.makeText(context, ""+assignUnAssignCampaign.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<AssignUnAssignCampaign> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:AssignUnAssignCampaign "+t);

            }
        });

    }



    private void addAssignedCampaigns(final List<UnAssignCampaign> unAssignCampaignList, final LinearLayout ll_assigned_campaigns, final int position, final Spinner sp_assign_campaign) {

        ll_assigned_campaigns.removeAllViews();
        for (int i=0;i<unAssignCampaignList.size();i++){
            View assignCampaignView = inflater.inflate(R.layout.assigned_campaigns_layout, null);
            TextView textView = (TextView) assignCampaignView.findViewById(R.id.tv_assigned_camp_name);
            Button btn_unassign_campaign=(Button) assignCampaignView.findViewById(R.id.btn_unassign_campaign);
            textView.setText(unAssignCampaignList.get(i).getCampaignTitle());
            final int finalI = i;

            btn_unassign_campaign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(context)
                            .setMessage("All members from this group is unassigned from the campaign. Are you sure you want to proceed?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    unAssignCampaign(unAssignCampaignList.get(finalI).getContactCampaignId(),ll_assigned_campaigns,position,sp_assign_campaign,ll_assigned_campaigns);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }
            });
            ll_assigned_campaigns.addView(assignCampaignView);
        }
       // getAssignedUnassignedCampaigns(position,sp_assign_campaign,ll_assigned_campaigns);
        //ll_assigned_campaigns.addView(assignCampaignView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

    }

    private void unAssignCampaign(String contactCampaignId, LinearLayout llAssignedCampaigns, final int position, final Spinner sp_assign_campaign, final LinearLayout ll_assigned_campaigns) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contactCampaignId", contactCampaignId);
            paramObj.put("fromGroup", "1");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "unAssignCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "unAssignCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.unAssigncampaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    getAssignedUnassignedCampaigns(position);
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: UnAssign "+t);
            }
        });

    }

    private void deleteGroup(final int position) {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("groupIds", groupList.get(position).getGroup_id());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteGroup: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteGroup: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_group(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Group Deleted Successfully..!", Toast.LENGTH_LONG).show();
                    //notifyItemRemoved(position);
                    notifyRefreshGroup.refreshGroups();
                    //MyGroupsFragment.getGroupsDetails();
                }else{
                    Toast.makeText(context, "Error in Deleting Group..!", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Toast.makeText(context, "Error in Deleting Group..!", Toast.LENGTH_LONG).show();
                myLoader.dismiss();
            }
        });
    }
    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        spinnerAdapter adapterRepeateDaily = new spinnerAdapter(context, android.R.layout.simple_list_item_1,campaignassignCampaignList);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }

    public static class spinnerAdapter extends ArrayAdapter<String> {
        List<AssignCampaign> campaignassignCampaignList;

        public spinnerAdapter(Context context, int textViewResourceId, List<AssignCampaign> campaignassignCampaignList) {
            super(context, textViewResourceId);
            this.campaignassignCampaignList=campaignassignCampaignList;
            // TODO Auto-generated constructor stub
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            int count =super.getCount();
            return count>0 ? count-1 : count ;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) view;
            if(!isEnabled(position)) {
                // Set the disable item text color
                tv.setTextColor(Color.GRAY);
            }
            else {
                tv.setTextColor(Color.BLACK);
            }

            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            if (position!=0){
                if (campaignassignCampaignList.get(position-1).getCampaignTemplateExits()==1){
                    return true;
                }else  if (campaignassignCampaignList.get(position-1).getCampaignTemplateExits()==0){
                    return false;
                }else{
                    return false;
                }
            }else{
                return true;
            }

        }

    }
    @Override
    public int getItemCount() {
        return groupList.size();
    }

    private void getGroupDetailsOnId(final boolean editF, final int position) {
        Log.d(Global.TAG, "getGroupDetailsOnId: ");
        JSONObject paramObj = new JSONObject();
        try {
            Log.d(Global.TAG, "getGroupDetailsOnId: user_id"+user_id);
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("groupId", groupList.get(position).getGroup_id());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getGroupsDetails: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getGroupDetailsOnId: paramObj"+paramObj.toString());
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GroupDetails> call=service.getDetailGroup(paramObj.toString());
        call.enqueue(new Callback<GroupDetails>() {
            @Override
            public void onResponse(Call<GroupDetails> call, Response<GroupDetails> response) {
                if (response.isSuccessful()){
                    groupDetails=response.body();
                    if (groupDetails.isSuccess()){
                        Log.d(Global.TAG, "Group Details Get Successfully..! ");

                        SingleGroup result= groupDetails.getResultAndroid();
                        ContactGroup contact=result.getContact();
                        ContactGroup prospect=result.getProspect();
                        ContactGroup customer=result.getCustomer();
                        ContactGroup recruit=result.getRecruit();

                        contactnameList=contact.getName();
                        prospectnameList=prospect.getName();
                        customersnameList=customer.getName();
                        recruitsnameList=recruit.getName();
                        campList=result.getCampName();

                        addGroupActivity.selected_contact.clear();
                        addGroupActivity.selected_prospects.clear();
                        addGroupActivity.selected_customers.clear();
                        addGroupActivity.selected_recruits.clear();
                        addGroupActivity.selected_group_contact.clear();

                        Log.d(Global.TAG, "openDialogForViewGroupDetails:selected_contact "+addGroupActivity.selected_contact);
                        addGroupActivity.selected_contact=contact.getContact_id();
                        addGroupActivity.selected_prospects=prospect.getContact_id();
                        addGroupActivity.selected_customers=customer.getContact_id();
                        addGroupActivity.selected_recruits=recruit.getContact_id();
                        Log.d(Global.TAG, "openDialogForViewGroupDetails:selected_contact "+addGroupActivity.selected_contact);
                        if (!editF){
                            openDialogForViewGroupDetails(position);
                        }
                    }else{
                        Log.d(Global.TAG, "Error in getting group details ");
                    }
                }

                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GroupDetails> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "Error in getting group details onFailure"+t);
            }
        });
    }

    public void openDialogForViewGroupDetails(final int position) {
        /*customersnameList=new ArrayList<>();
        prospectnameList=new ArrayList<>();
        contactnameList=new ArrayList<>();*/


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.view_group_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");
/*
        SingleGroup result= groupDetails.getResultAndroid();
        ContactGroup contact=result.getContact();
        ContactGroup prospect=result.getProspect();
        ContactGroup customer=result.getCustomer();

        contactnameList=contact.getName();
        prospectnameList=prospect.getName();
        customersnameList=customer.getName();
        List<String> campList=result.getCampName();*/

       /* addGroupActivity.selected_contact.clear();
        addGroupActivity.selected_prospects.clear();
        addGroupActivity.selected_customers.clear();
        addGroupActivity.selected_group_contact.clear();

        Log.d(Global.TAG, "openDialogForViewGroupDetails:selected_contact "+addGroupActivity.selected_contact);
        addGroupActivity.selected_contact=contact.getContact_id();
        addGroupActivity.selected_prospects=prospect.getContact_id();
        addGroupActivity.selected_customers=customer.getContact_id();
        Log.d(Global.TAG, "openDialogForViewGroupDetails:selected_contact "+addGroupActivity.selected_contact);
*/

       /* group_contacts=contact.getContact_id();
        addToContactId(group_contacts);
        group_contacts=prospect.getContact_id();
        addToContactId(group_contacts);
        group_contacts=customer.getContact_id();
        addToContactId(group_contacts);*/
//        addGroupActivity.selected_group_contact.clear();
//        addGroupActivity.selected_group_contact=contact.getContact_id();
//        addGroupActivity.selected_group_contact.addAll(customer.getContact_id());
//        addGroupActivity.selected_group_contact.addAll(prospect.getContact_id());


//        group_contactIds.clear();
//        group_contactIds
//

//        addGroupActivity.selected_edit_group_contact.clear();
//        addGroupActivity.selected_edit_group_contact.addAll(contact.getContact_id());
//        addGroupActivity.selected_edit_group_contact.addAll(customer.getContact_id());
//        addGroupActivity.selected_edit_group_contact.addAll(prospect.getContact_id());


        // Log.d(Global.TAG, "addGroupActivity.selected_group_contact: "+addGroupActivity.selected_group_contact);
//        addGroupActivity.selected_edit_group_contact.clear();
//        addGroupActivity.selected_edit_group_contact.addAll( addGroupActivity.selected_group_contact);
        Log.d(Global.TAG, "addGroupActivity.selected_edit_group_contact: "+addGroupActivity.selected_group_contact);


        // Log.d(Global.TAG, "openDialogForViewGroupDetails:group_contacts:: "+group_contactIds);
        //AddGroupActivity.selected_group_contact=group_contactIds;
        Log.d(Global.TAG, "openDialogForViewGroupDetails:selected_contact:: "+addGroupActivity.selected_contact);

        Log.d(Global.TAG, "openDialogForViewGroupDetails:contactnameList :"+contactnameList.size());
        Log.d(Global.TAG, "openDialogForViewGroupDetails:prospectnameList :"+prospectnameList.size());
        Log.d(Global.TAG, "openDialogForViewGroupDetails:customersnameList :"+customersnameList.size());
        Log.d(Global.TAG, "openDialogForViewGroupDetails:recruits List :"+customersnameList.size());
        Log.d(Global.TAG, "openDialogForViewGroupDetails:campList "+campList.size());


        String contactNames="";
        String prospectNames="";
        String customersNames="";
        String recruitsNames="";
        String campaignNames="";

        if (campList.size()>0){
            for (String campaign : campList)
            {
                campaignNames += campaign + ",";
            }
            if (campaignNames.endsWith(",")) {
                campaignNames = campaignNames.substring(0, campaignNames.length() - 1);
            }
        }

        if (contactnameList.size()>0){
            for (String contact1 : contactnameList)
            {
                contactNames += contact1 + ",";
            }
            if (contactNames.endsWith(",")) {
                contactNames = contactNames.substring(0, contactNames.length() - 1);
            }
        }

        if (prospectnameList.size()>0){
            for (String prospect1 : prospectnameList)
            {
                prospectNames += prospect1 + ",";
            }
            if (prospectNames.endsWith(",")) {
                prospectNames = prospectNames.substring(0, prospectNames.length() - 1);
            }
        }

        if (customersnameList.size()>0){
            for (String customer1 : customersnameList)
            {
                customersNames += customer1 + ",";
            }
            if (customersNames.endsWith(",")) {
                customersNames = customersNames.substring(0, customersNames.length() - 1);
            }
        }

        if (recruitsnameList.size()>0){
            for (String recruit1 : recruitsnameList)
            {
                recruitsNames += recruit1 + ",";
            }
            if (recruitsNames.endsWith(",")) {
                recruitsNames = recruitsNames.substring(0, recruitsNames.length() - 1);
            }
        }

        Log.d(Global.TAG, "openDialogForViewGroupDetails:contactNames"+contactNames);
        Log.d(Global.TAG, "openDialogForViewGroupDetails:prospectNames"+prospectNames);
        Log.d(Global.TAG, "openDialogForViewGroupDetails:customersNames"+customersNames);
        Log.d(Global.TAG, "openDialogForViewGroupDetails:recruitsNames"+recruitsNames);



//        Button dialogBack = (Button) dialog.findViewById(R.id.btn_back);
        Button btn_edit_group = (Button) dialog.findViewById(R.id.btn_edit_vgroup);
        Button btn_dissmiss = (Button) dialog.findViewById(R.id.btn_dissmiss);

        TextView tv_group_vname = (TextView) dialog.findViewById(R.id.tv_group_vname);
        TextView tv_group_desc = (TextView) dialog.findViewById(R.id.tv_group_desc);
        TextView tv_contact_members = (TextView) dialog.findViewById(R.id.tv_contact_members);
        TextView tv_prospects_members = (TextView) dialog.findViewById(R.id.tv_prospects_members);
        TextView tv_customers_members = (TextView) dialog.findViewById(R.id.tv_customers_members);
        TextView tv_recruit_members = (TextView) dialog.findViewById(R.id.tv_recruit_members);
        TextView tv_vassign_campaign = (TextView) dialog.findViewById(R.id.tv_vassign_campaign);

        tv_group_vname.setText(groupList.get(position).getGroup_name());
        tv_group_desc.setText(groupList.get(position).getGroup_description());
        if (contactnameList.size()>0){
            tv_contact_members.setText(contactNames);
        }
        if (prospectnameList.size()>0){
            tv_prospects_members.setText(prospectNames);
        }
        if (customersnameList.size()>0){
            tv_customers_members.setText(customersNames);
        }
        if (recruitsnameList.size()>0){
            tv_recruit_members.setText(recruitsNames);
        }
        tv_vassign_campaign.setText(campaignNames);
        Log.d(Global.TAG, "editGroupDetails: selected_contact:"+addGroupActivity.selected_contact);

        btn_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_edit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addGroupActivity.selected_group_contact=group_contactIds;
                //Log.d(Global.TAG, "editGroupDetails: "+ addGroupActivity.selected_group_contact);
                editGroupDetails(position);
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    /*private void addToContactId(List<String> group_contacts) {
        if (group_contacts.size()>0){
            for (String contactid : group_contacts)
            {
                addGroupActivity.selected_group_contact.add(contactid);
            }
        }
    }*/

    private void editGroupDetails(int position) {
        getGroupDetailsOnId(true,position);
        Intent intent=new Intent(context, AddGroupActivity.class);
        Bundle bundle=new Bundle();
        bundle.putBoolean("editFlag",true);
        bundle.putString("group_name",groupList.get(position).getGroup_name());
        bundle.putString("group_desc",groupList.get(position).getGroup_description());
        bundle.putString("group_id",groupList.get(position).getGroup_id());
        intent.putExtras(bundle);
        myGroupsFragment1.startActivityForResult(intent,3003);
        if (dialog!=null){
            dialog.dismiss();
        }
    }
}
