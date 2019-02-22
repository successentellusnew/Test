package com.success.successEntellus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.AddTemplateActivity;
import com.success.successEntellus.activity.CreateCampaignActivity;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.fragment.CampaignStepsFragment;
import com.success.successEntellus.fragment.CustomCampaignFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.model.Campaign;
import com.success.successEntellus.model.CampaignEmailTemplates;
import com.success.successEntellus.model.CampaignTemplate;
import com.success.successEntellus.model.GetAllCampaign;
import com.success.successEntellus.model.GetRemoveMemberListEmail;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.RemoveEmail;
import com.success.successEntellus.model.RemoveEmailDetails;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.CustomCampaignHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 5/14/2018.
 */


public class CustomCampaignAdapter extends RecyclerView.Adapter<CustomCampaignHolder> {
    DashboardActivity context;
    List<Campaign> campaignsList;
    LayoutInflater inflater;
    List<Campaign> unImportedcampaignList;
    String unassingStepID="";
    String[] intervalType={"Days","Week","Month"};
    int send_by;
    int lastPosition=-1;
    List<CampaignTemplate> campaignTemplatesToImport;

    View layout;
    String user_id;
    private String[] import_campaign_array;
    private String import_campaignIds="";
    private String selected_import_campaign="";
    private String selected_interval_type="";
    CreateCampaignActivity createCampaignActivity=new CreateCampaignActivity();
    CustomCampaignFragment customCampaignFragment=new CustomCampaignFragment();
    List<CampaignTemplate> templateSelectionList;
    Dialog dialog;
    RecyclerView rv_remove_members,rv_added_email_list;
    TextView tv_rcampaign_name,tv_added_email_cam_name,tv_text_rtemplate_created_date;
    public static List<String> removeMemberIds=new ArrayList<>();
    String scheduleMsg="";

    public interface NotifyRefreshCampaigns{
        void refreshCampaigns();
    }
    private NotifyRefreshCampaigns notifyRefreshCamp;

    public CustomCampaignAdapter(@NonNull DashboardActivity context, List<Campaign> campaignsList, String user_id,NotifyRefreshCampaigns notifyRefreshCamp) {
        this.context=context;
        this.campaignsList=campaignsList;
        this.user_id=user_id;
        this.notifyRefreshCamp=notifyRefreshCamp;
    }

    public CustomCampaignAdapter(Context context) {
        //this.context=context;
    }

    @Override
    public CustomCampaignHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.custom_campaign_row,parent,false);
        CustomCampaignHolder customCampaignHolder=new CustomCampaignHolder(context,layout);

        return customCampaignHolder;
    }

    @Override
    public void onBindViewHolder(final CustomCampaignHolder holder, final int position) {
        holder.tv_company_campaign_name.setText(campaignsList.get(position).getCampaignTitle());
        holder.campaign_id=campaignsList.get(position).getCampaignId();
        holder.user_id=user_id;
        Log.d(Global.TAG, "ImageUrl:: "+campaignsList.get(position).getCampaignImage());
        Picasso.with(context)
                .load(campaignsList.get(position).getCampaignImage())
                .resize(400, 400)
                .into( holder.iv_company_campaign);
        Log.d(Global.TAG, "CampaignColor: "+campaignsList.get(position).getCampaignColor());
        List<Integer> colorList=campaignsList.get(position).getCampaignColor();
        if (colorList.size()>0){
            holder.ll_campaign.setCardBackgroundColor(Color.rgb(colorList.get(0),colorList.get(1),colorList.get(2)));
        }

        holder.ib_campaign_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpMenu(holder,position);
            }
        });

        holder.ib_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkTemplateForAddEmail(position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCampaignTemplateOnId(position,campaignsList.get(position).getCampaignTitle());
            }
        });

        setAnimation(holder.itemView,position);
    }
    private void setAnimation(View viewToAnimate, int position)
    {

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public void onViewDetachedFromWindow(CustomCampaignHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }




    public void getCampaignTemplateOnId(final int position, final String campaignTitle) {
        Log.d(Global.TAG, "getCampaignTemplateOnId: ");
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId", campaignsList.get(position).getCampaignId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getCampaignTemplateOnId: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CampaignEmailTemplates> call=service.getEmailTemplateOnId(paramObj.toString());
        call.enqueue(new Callback<CampaignEmailTemplates>() {
            @Override
            public void onResponse(Call<CampaignEmailTemplates> call, Response<CampaignEmailTemplates> response) {
                CampaignEmailTemplates campaignEmailTemplates=response.body();
                if (campaignEmailTemplates.isSuccess()){
                    List<CampaignTemplate> campaignTemplates=campaignEmailTemplates.getResult();
                    Log.d(Global.TAG, "campaignTemplates: "+campaignTemplates.size());

                    if (campaignTemplates.size()>0){
                        context.replaceFragments(new CampaignStepsFragment(campaignsList.get(position).getCampaignId(),campaignTitle,false));
                    }else{
                        Toast.makeText(context, "No Templates Added Yet..!", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(context, "No Templates Added Yet..!", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, " getCampaignTemplateOnId onFailure: "+t);
            }
        });
    }

    public void checkTemplateForAddEmail(final int position) {
        Log.d(Global.TAG, "getCampaignTemplateOnId: ");
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId", campaignsList.get(position).getCampaignId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getCampaignTemplateOnId: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CampaignEmailTemplates> call=service.getEmailTemplateOnId(paramObj.toString());
        call.enqueue(new Callback<CampaignEmailTemplates>() {
            @Override
            public void onResponse(Call<CampaignEmailTemplates> call, Response<CampaignEmailTemplates> response) {
               if (response.isSuccessful()){
                   CampaignEmailTemplates campaignEmailTemplates=response.body();
                   if (campaignEmailTemplates.isSuccess()){
                       List<CampaignTemplate> campaignTemplates=campaignEmailTemplates.getResult();
                       Log.d(Global.TAG, "campaignTemplates: "+campaignTemplates.size());

                       if (campaignTemplates.size()>0){
                          // getAddedEmailToStartCampaign(position,tv_no_emails_to_start,btn_start_campaign,tv_added_email_camp_date);
                           customCampaignFragment.openDialogAddEmailBoth(position,scheduleMsg);
                           //context.replaceFragments(new CampaignStepsFragment(campaignsList.get(position).getCampaignId(),campaignTitle,false));
                       }else{
                           Toast.makeText(context, "No Templates Added Yet..!", Toast.LENGTH_LONG).show();
                       }

                   }else{
                       Toast.makeText(context, "No Templates Added Yet..!", Toast.LENGTH_LONG).show();
                   }
               }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, " getCampaignTemplateOnId onFailure: "+t);
            }
        });
    }

    public void checkTemplateForStartCampaign(final int position) {
        Log.d(Global.TAG, "getCampaignTemplateOnId: ");
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId", campaignsList.get(position).getCampaignId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getCampaignTemplateOnId: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CampaignEmailTemplates> call=service.getEmailTemplateOnId(paramObj.toString());
        call.enqueue(new Callback<CampaignEmailTemplates>() {
            @Override
            public void onResponse(Call<CampaignEmailTemplates> call, Response<CampaignEmailTemplates> response) {
                if (response.isSuccessful()){
                    CampaignEmailTemplates campaignEmailTemplates=response.body();
                    if (campaignEmailTemplates.isSuccess()){
                        List<CampaignTemplate> campaignTemplates=campaignEmailTemplates.getResult();
                        Log.d(Global.TAG, "campaignTemplates: "+campaignTemplates.size());

                        if (campaignTemplates.size()>0){
                           openDialogStartCampaign(position);
                        }else{
                            Toast.makeText(context, "No Templates Added Yet..!", Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(context, "No Templates Added Yet..!", Toast.LENGTH_LONG).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, " getCampaignTemplateOnId onFailure: "+t);
            }
        });
    }
    private void showPopUpMenu(final CustomCampaignHolder holder, final int position) {
        PopupMenu popup = new PopupMenu(context, holder.ib_campaign_menu);
        popup.getMenuInflater().inflate(R.menu.campaign_context_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_create_template:
                       // Toast.makeText(context, "Create Template", Toast.LENGTH_SHORT).show();
                        openCreateTemplateDialog(position);
                        break;
                    case R.id.action_import_campaign:
                        //Toast.makeText(context, "import Template", Toast.LENGTH_SHORT).show();
                        openImportDialog(position);
                        break;
                   /* case R.id.action_start_campaign:
                        checkTemplateForStartCampaign(position);
                        break;*/
                    case R.id.action_self_reminder:
                       // Toast.makeText(context, "Self Reminder", Toast.LENGTH_SHORT).show();
                        openDialogsetSelfReminder(position);
                        break;
                    case R.id.action_remove_email:
                       // Toast.makeText(context, "Remove Member", Toast.LENGTH_SHORT).show();
                        checkTemplateForRemoveEmail(position);
                       // openDialogsetSelfReminder(position);
                        break;
                    case R.id.action_edit_campaign:
                        //Toast.makeText(context, "Edit Campaign", Toast.LENGTH_SHORT).show();
                        openeditCampaignDialog(holder,position);
                        break;
                    case R.id.action_delete_campaign:
                        //Toast.makeText(context, "Delete Campaign", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(context)
                                    .setMessage("Are you sure you want to Delete this Campaign..?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            deleteCampaign(position);
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        break;
                }
                return false;
            }
        });

        popup.show();//showing popup menu
    }

    private void checkTemplateForRemoveEmail(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId",campaignsList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getRemoveMemberListEmailCampaigns: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getRemoveMemberListEmailCampaigns: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetRemoveMemberListEmail> call=service.getAssignedEmails(paramObj.toString());
       call.enqueue(new Callback<GetRemoveMemberListEmail>() {
           @Override
           public void onResponse(Call<GetRemoveMemberListEmail> call, Response<GetRemoveMemberListEmail> response) {
               GetRemoveMemberListEmail GetRemoveMemberListEmail=response.body();
               if (GetRemoveMemberListEmail!=null){
                   if (GetRemoveMemberListEmail.isSuccess()){
                       RemoveEmail removeEmail=GetRemoveMemberListEmail.getResult();
                       List<RemoveEmailDetails> rMemberList=removeEmail.getMemberDetails();
                       Log.d(Global.TAG, "rMemberList email: "+rMemberList.size());

                       if (rMemberList.size()>0){
                           openDialogRemoveMember(position);
                       }else{
                           Toast.makeText(context, "No Emails available to remove..!", Toast.LENGTH_SHORT).show();
                       }

                   }else{
                       Toast.makeText(context, "No Emails available to remove..!", Toast.LENGTH_SHORT).show();
                   }
               }
               myLoader.dismiss();
           }

           @Override
           public void onFailure(Call<GetRemoveMemberListEmail> call, Throwable t) {
               myLoader.dismiss();
               Log.d(Global.TAG, "GetRemoveMemberList onFailure: "+t);
               Toast.makeText(context, "No Emails available to remove..!", Toast.LENGTH_LONG).show();
           }
       });

    }

    private void openDialogRemoveMember(final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.remove_member_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        Button btn_remove_member=dialog.findViewById(R.id.btn_remove_member);
        rv_remove_members=dialog.findViewById(R.id.rv_remove_members);
        Button btn_rmember_close=dialog.findViewById(R.id.btn_rmember_close);
        tv_rcampaign_name=dialog.findViewById(R.id.tv_text_rtemplate_name);
        tv_text_rtemplate_created_date=dialog.findViewById(R.id.tv_text_rtemplate_created_date);
        rv_remove_members.setLayoutManager(new LinearLayoutManager(context));


        getRemoveMemberListEmailCampaigns(position);

        btn_rmember_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_remove_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removeMemberIds.size()>0){
                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to unassigned email(s) from current campaign?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    removeMemberFromList(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }else{
                    Toast.makeText(context, "Please select at least one checkbox to unassign email..!", Toast.LENGTH_LONG).show();
                }


            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void removeMemberFromList(final int position) {
        Log.d(Global.TAG, "removeMemberFromList: "+removeMemberIds.size());
        String removeMemberString="";
        if (removeMemberIds.size()>0){
            for (String member : removeMemberIds)
            {
                removeMemberString += member + ",";
            }
            if (removeMemberString.endsWith(",")) {
                removeMemberString = removeMemberString.substring(0, removeMemberString.length() - 1);
            }
            Log.d(Global.TAG, "removeMemberFromList: "+removeMemberString);
        }
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contactCampaignIds",removeMemberString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "removeMemberFromList: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "removeMemberFromList: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.remove_member_from_email_campaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult!=null){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        removeMemberIds.clear();
                        getRemoveMemberListEmailCampaigns(position);
                    }else{
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: removeMemberFromList:"+t);
            }
        });
    }

    private void startEmailCampaign(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId",campaignsList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "startEmailCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "startEmailCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.startEmailCampaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult!=null){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: startEmailCampaign:"+t);
            }
        });


    }

    private void getRemoveMemberListEmailCampaigns(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId",campaignsList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getRemoveMemberListEmailCampaigns: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getRemoveMemberListEmailCampaigns: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetRemoveMemberListEmail> call=service.getAssignedEmails(paramObj.toString());
        call.enqueue(new Callback<GetRemoveMemberListEmail>() {
            @Override
            public void onResponse(Call<GetRemoveMemberListEmail> call, Response<GetRemoveMemberListEmail> response) {
                GetRemoveMemberListEmail GetRemoveMemberListEmail=response.body();
                if (GetRemoveMemberListEmail!=null){
                    if (GetRemoveMemberListEmail.isSuccess()){
                        RemoveEmail removeEmail=GetRemoveMemberListEmail.getResult();
                        List<RemoveEmailDetails> rMemberList=removeEmail.getMemberDetails();
                        String camp_name=removeEmail.getCampaignTitle();
                        String camp_created_date=removeEmail.getCampaignDateTime();
                        Log.d(Global.TAG, "rMemberList email: "+rMemberList.size());
                        Log.d(Global.TAG, "rMemberList: camp_name:"+camp_name);
                        if (camp_name!=null){
                            tv_rcampaign_name.setText(camp_name);
                        }
                        if (camp_created_date!=null){
                            tv_text_rtemplate_created_date.setText(camp_created_date);
                        }

                        if (rMemberList.size()>0){
                            removeMemberIds.clear();
                            RemoveMemberAdapterEmail adapter=new RemoveMemberAdapterEmail(context,rMemberList,"0");
                            rv_remove_members.setAdapter(adapter);
                        }else{
                            dialog.dismiss();
                            Toast.makeText(context, "No members available to remove..!", Toast.LENGTH_LONG).show();
                        }

                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetRemoveMemberListEmail> call, Throwable t) {
                myLoader.dismiss();
                dialog.dismiss();
                Toast.makeText(context, "No members available to remove..!", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "GetRemoveMemberList onFailure: "+t);
            }
        });
    }

    private void openDialogStartCampaign(final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.start_email_campaign_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        TextView tv_no_emails_to_start=dialog.findViewById(R.id.tv_no_emails_to_start);
        TextView tv_added_email_camp_date=dialog.findViewById(R.id.tv_added_email_camp_date);
        Button btn_start_campaign=dialog.findViewById(R.id.btn_start_campaign);
        Button btn_start_camp_close=dialog.findViewById(R.id.btn_start_camp_close);
        rv_added_email_list=dialog.findViewById(R.id.rv_added_email_list);
        tv_added_email_cam_name=dialog.findViewById(R.id.tv_added_email_cam_name);
        rv_added_email_list.setLayoutManager(new LinearLayoutManager(context));


        getAddedEmailToStartCampaign(position,tv_no_emails_to_start,btn_start_campaign,tv_added_email_camp_date);

        btn_start_camp_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_start_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!scheduleMsg.equals("")){
                    new AlertDialog.Builder(context)
                            .setMessage("Email will be sent at '"+scheduleMsg+"' Do you want to proceed ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startEmailCampaign(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }else{
                    new AlertDialog.Builder(context)
                            .setMessage("Email will be sent at scheduled time. Do you want to proceed ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startEmailCampaign(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }


            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void getAddedEmailToStartCampaign(int position, final TextView tv_no_emails_to_start, final Button btn_start_campaign, final TextView tv_added_email_camp_date) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId",campaignsList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAddedEmailToStartCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAddedEmailToStartCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetRemoveMemberListEmail> call=service.getAddedEmailToStartCamp(paramObj.toString());
        call.enqueue(new Callback<GetRemoveMemberListEmail>() {
            @Override
            public void onResponse(Call<GetRemoveMemberListEmail> call, Response<GetRemoveMemberListEmail> response) {
                if (response.isSuccessful()){
                    GetRemoveMemberListEmail GetRemoveMemberListEmail=response.body();
                    if (GetRemoveMemberListEmail.isSuccess()){
                        RemoveEmail removeEmail=GetRemoveMemberListEmail.getResult();
                        List<RemoveEmailDetails> addeddEmailList=removeEmail.getCampaignEmails();
                        String camp_name=removeEmail.getCampaignTitle();
                        String camp_date=removeEmail.getCampaignDateTime();
                        scheduleMsg=removeEmail.getScheduleMessage();
                        Log.d(Global.TAG, "addeddEmailList email: "+addeddEmailList.size());
                        Log.d(Global.TAG, "addeddEmailList: camp_name:"+camp_name);
                        if (camp_name!=null){
                            tv_added_email_cam_name.setText(camp_name);
                        }
                        if (camp_date!=null){
                            tv_added_email_camp_date.setText(camp_date);
                        }

                        if (addeddEmailList.size()>0){
                            btn_start_campaign.setVisibility(View.VISIBLE);
                            rv_added_email_list.setVisibility(View.VISIBLE);
                            tv_no_emails_to_start.setVisibility(View.GONE);
                            RemoveMemberAdapterEmail adapter=new RemoveMemberAdapterEmail(context,addeddEmailList,"1");
                            rv_added_email_list.setAdapter(adapter);
                        }else{
                            rv_added_email_list.setVisibility(View.GONE);
                            tv_no_emails_to_start.setVisibility(View.VISIBLE);
                            btn_start_campaign.setVisibility(View.GONE);
                            Toast.makeText(context, "No Emails available !", Toast.LENGTH_LONG).show();
                        }

                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetRemoveMemberListEmail> call, Throwable t) {
                myLoader.dismiss();
                rv_added_email_list.setVisibility(View.GONE);
                tv_no_emails_to_start.setVisibility(View.VISIBLE);
                btn_start_campaign.setVisibility(View.GONE);
                Toast.makeText(context, "No Emails available !", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "GetRemoveMemberList onFailure: "+t);
            }
        });
    }

    private void openCreateTemplateDialog(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.create_template_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        Button btn_select_template_dissmiss = (Button) dialog.findViewById(R.id.btn_select_template_dissmiss);
        ImageView iv_blanck_template = (ImageView) dialog.findViewById(R.id.iv_blanck_template);
        ImageView iv_background_template = (ImageView) dialog.findViewById(R.id.iv_background_template);
        TextView tv_blank_template = (TextView) dialog.findViewById(R.id.tv_blank_template);
        TextView tv_back_template = (TextView) dialog.findViewById(R.id.tv_back_template);
        Button btn_template_cancel = (Button) dialog.findViewById(R.id.btn_template_cancel);
       
        LinearLayout ll_blank_template = (LinearLayout) dialog.findViewById(R.id.ll_blank_template);
        LinearLayout ll_background_template = (LinearLayout) dialog.findViewById(R.id.ll_background_template);

        getTemplateSelection(iv_blanck_template,iv_background_template,tv_blank_template,tv_back_template);
        btn_select_template_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_template_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ll_blank_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, AddTemplateActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("campaign_id",campaignsList.get(position).getCampaignId());
                bundle.putString("template_id",templateSelectionList.get(0).getCampaignTemplateId());
                intent.putExtras(bundle);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        ll_background_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, AddTemplateActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("campaign_id",campaignsList.get(position).getCampaignId());
                bundle.putString("template_id",templateSelectionList.get(1).getCampaignTemplateId());
                intent.putExtras(bundle);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void getTemplateSelection(final ImageView iv_blanck_template, final ImageView iv_background_template, final TextView tv_blank_template, final TextView tv_back_template) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "importCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "importCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CampaignEmailTemplates> call=service.getTemplateSelection(paramObj.toString());
        call.enqueue(new Callback<CampaignEmailTemplates>() {
            @Override
            public void onResponse(Call<CampaignEmailTemplates> call, Response<CampaignEmailTemplates> response) {
                CampaignEmailTemplates campaignEmailTemplates=response.body();
                if (campaignEmailTemplates.isSuccess()){
                    templateSelectionList=campaignEmailTemplates.getResult();
                    Log.d(Global.TAG, "templateSelectionList: "+templateSelectionList.size());

                    tv_blank_template.setText(templateSelectionList.get(0).getLabelTitle());
                    tv_back_template.setText(templateSelectionList.get(1).getLabelTitle());

                    Picasso.with(context)
                            .load(templateSelectionList.get(0).getImageUrl())
                            .placeholder(R.drawable.place)   // optional
                            .error(R.drawable.error)      // optional
                            .resize(400, 400)
                            .into(iv_blanck_template);

                    Picasso.with(context)
                            .load(templateSelectionList.get(1).getImageUrl())
                            .placeholder(R.drawable.place)   // optional
                            .error(R.drawable.error)      // optional
                            .resize(400, 400)
                            .into(iv_background_template);

                }else{
                    Toast.makeText(context, "Error in getting Templates", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();


            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in getting Templates", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure: getTemplateSelection "+t);
            }
        });

    }

    private void openImportDialog(final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.import_template_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        Button btn_import_cdissmiss = (Button) dialog.findViewById(R.id.btn_import_cdissmiss);
        Button btn_import_campaign_cancel = (Button) dialog.findViewById(R.id.btn_import_campaign_cancel);
        Button btn_import_campaign = (Button) dialog.findViewById(R.id.btn_import_campaign);
        CheckBox ch_select_all_templates = (CheckBox) dialog.findViewById(R.id.ch_select_all_templates);
        final Spinner sp_import_campaign = (Spinner) dialog.findViewById(R.id.sp_import_campaign);

        final LinearLayout ll_campaign_steps = (LinearLayout) dialog.findViewById(R.id.ll_campaign_steps);
        final RecyclerView lv_import_campaign_templates = (RecyclerView) dialog.findViewById(R.id.lv_import_campaign_templates);
        lv_import_campaign_templates.setLayoutManager(new LinearLayoutManager(context));

        getUnImportedTemplates(sp_import_campaign,position);

        btn_import_campaign_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_import_cdissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_import_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selected_import_campaign.equals("")){
                    importCampaign(position);
                }else{
                    Toast.makeText(context, "Please select campaign to import..!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ch_select_all_templates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    createCampaignActivity.importCampaignIds.clear();
                    Log.d(Global.TAG, "campaignTemplatesToImport size: "+campaignTemplatesToImport.size());


                    if (campaignTemplatesToImport.size()>0){
                        Log.d(Global.TAG, "campaignTemplatesToImport size: "+campaignTemplatesToImport.size());
                        for (int i=0;i<campaignTemplatesToImport.size();i++){
                            createCampaignActivity.importCampaignIds.add(campaignTemplatesToImport.get(i).getCampaignStepId());
                        }
                        Log.d(Global.TAG, "importCampaignIds size: "+createCampaignActivity.importCampaignIds.size());
                    }
                    TemplateListAdapter templateListAdapter=new TemplateListAdapter(context,campaignTemplatesToImport, true,true);
                    lv_import_campaign_templates.setAdapter(templateListAdapter);

                }else{

                    TemplateListAdapter templateListAdapter=new TemplateListAdapter(context,campaignTemplatesToImport, true,false);
                    lv_import_campaign_templates.setAdapter(templateListAdapter);
                    createCampaignActivity.importCampaignIds.clear();
                    Log.d(Global.TAG, "importCampaignIds size: "+createCampaignActivity.importCampaignIds.size());
                   /* if (campaignTemplatesToImport.size()>0){
                        for (int i=0;i<=campaignTemplatesToImport.size();i++){
                            createCampaignActivity.importCampaignIds.remove(campaignTemplatesToImport.get(position).getCampaignStepId());
                        }
                    }*/
                }

            }
        });

        sp_import_campaign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_import_campaign.getSelectedItem().toString().contains("Select")) {
                    unassingStepID = "";
                    selected_import_campaign="";
                } else {
                    Log.d(Global.TAG, "onItemSelected:unassingStepID: "+unassingStepID.length()+"position-1:"+(position-1));
                    unassingStepID = unImportedcampaignList.get(position-1).getUnassingStepID();
                    selected_import_campaign=unImportedcampaignList.get(position-1).getCampaignId();
                    getunAssignStepId(lv_import_campaign_templates);
                    ll_campaign_steps.setVisibility(View.VISIBLE);
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

    private void importCampaign(final int position) {
        import_campaignIds="";
        if (createCampaignActivity.importCampaignIds.size()>0){
            for (String campaignStep : createCampaignActivity.importCampaignIds)
            {
                import_campaignIds += campaignStep + ",";
            }
            if (import_campaignIds.endsWith(",")) {
                import_campaignIds = import_campaignIds.substring(0, import_campaignIds.length() - 1);
            }
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("listCampaignId",selected_import_campaign);
            paramObj.put("listTemplateIds",import_campaignIds );
            paramObj.put("parentCampaignId",campaignsList.get(position).getCampaignId() );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "importCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "importCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.import_campaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Campaign Imported Successfully...!", Toast.LENGTH_LONG).show();
                    createCampaignActivity.importCampaignIds.clear();
                    notifyRefreshCamp.refreshCampaigns();
                  //  customCampaignFragment.getAllCustomCampaign();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, "Error in importing Campaign", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in importing Campaign", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure: import Campaign "+t);
            }
        });
    }

    private void openDialogsetSelfReminder(final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.self_reminder_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        Button btn_reminder_dissmiss = (Button) dialog.findViewById(R.id.btn_reminder_dissmiss);
        Button btn_set_reminder = (Button) dialog.findViewById(R.id.btn_set_reminder);
        Button btn_reminder_cancel = (Button) dialog.findViewById(R.id.btn_reminder_cancel);
        RadioGroup rbg_rem_send_type = (RadioGroup) dialog.findViewById(R.id.rbg_rem_send_type);
        RadioButton rb_email = (RadioButton) dialog.findViewById(R.id.rb_email);
        RadioButton rb_smsemail = (RadioButton) dialog.findViewById(R.id.rb_smsemail);

        final EditText edt_time_interval = (EditText) dialog.findViewById(R.id.edt_time_interval);
        final EditText edt_self_rem_note = (EditText) dialog.findViewById(R.id.edt_self_rem_note);
        final Spinner sp_select_interval = (Spinner) dialog.findViewById(R.id.sp_select_interval);


        /*int selected_rb=rbg_rem_send_type.getCheckedRadioButtonId();
        if (selected_rb==R.id.rb_email){
            send_by=1;
        }else if (selected_rb==R.id.rb_smsemail){
            send_by=2;
        }*/

        rb_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    send_by=1;
                }else{
                    send_by=2;
                }
            }
        });

        rb_smsemail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    send_by=2;
                }else{
                    send_by=1;
                }
            }
        });

        btn_reminder_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_reminder_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_set_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_time_interval,"Enter Time Interval")){
                    if (!sp_select_interval.getSelectedItem().toString().contains("Select")){
                        if (!edt_time_interval.getText().toString().equals("0")){
                            setSelfReminder(edt_time_interval.getText().toString(),edt_self_rem_note.getText().toString(),position);
                        }else{
                            Toast.makeText(context, "Interval Must be greater than 0..!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        MyValidator.setSpinnerError(sp_select_interval,"Select Type..!");
                    }

                }else{
                    edt_time_interval.requestFocus();
                }

               //setSelfReminder(edt_time_interval.getText().toString(),edt_self_rem_note.getText().toString(),position);
            }
        });
        applySpinner(intervalType,sp_select_interval,"--Select--");

        sp_select_interval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_select_interval.getSelectedItem().toString().contains("Select")) {
                    selected_interval_type = "";
                } else {
                    selected_interval_type = intervalType[position-1];
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

    private void setSelfReminder(String timeInterval, String note_content, final int position) {
        Log.d(Global.TAG, "setSelfReminder: Send By: "+send_by);
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignStepCamId", campaignsList.get(position).getCampaignId());
            paramObj.put("campaignStepSendTo",send_by );
            paramObj.put("campaignStepSendInterval",timeInterval );
            paramObj.put("campaignStepSendIntervalType",selected_interval_type.toLowerCase() );
            paramObj.put("campaignStepContent",note_content );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "setSelfReminder: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "setSelfReminder: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.set_self_reminder(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Self Reminder Set Successfully..!", Toast.LENGTH_LONG).show();
                    //customCampaignFragment.getAllCustomCampaign();
                    notifyRefreshCamp.refreshCampaigns();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, "Error in Setting Self Reminder...!", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in Setting Self Reminder...!", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure: Self Reminder: "+t);
            }
        });
    }

    private void openeditCampaignDialog(final CustomCampaignHolder holder, final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.edit_campaign_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        Button btn_edit_campaign_dissmiss = (Button) dialog.findViewById(R.id.btn_edit_campaign_dissmiss);
        Button btn_edit_campaign = (Button) dialog.findViewById(R.id.btn_edit_campaign);
        Button btn_edit_campaign_cancel = (Button) dialog.findViewById(R.id.btn_edit_campaign_cancel);
        final EditText edt_edit_campaign_name = (EditText) dialog.findViewById(R.id.edt_edit_campaign_name);

        edt_edit_campaign_name.setText(campaignsList.get(position).getCampaignTitle());
        btn_edit_campaign_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_edit_campaign_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_edit_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCampaign(edt_edit_campaign_name.getText().toString(),position,dialog);
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void editCampaign(String title, final int position, final Dialog dialog) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId", campaignsList.get(position).getCampaignId());
            paramObj.put("campaignTitle",title );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "editCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "editCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.edit_campaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Campaign Updated Successfully..!", Toast.LENGTH_LONG).show();
                    notifyRefreshCamp.refreshCampaigns();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, "Error in Updating Campaign!", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in Updating Campaign!", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, " editCampaign onFailure: "+t);
            }
        });

    }

    private void getunAssignStepId(final RecyclerView lv_import_campaign_templates) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("unassingStepID", unassingStepID);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getCampaignTemplateOnIdWithoutSelfReminder: "+paramObj.toString());
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CampaignEmailTemplates> call=service.get_unimported_templates(paramObj.toString());
        call.enqueue(new Callback<CampaignEmailTemplates>() {
            @Override
            public void onResponse(Call<CampaignEmailTemplates> call, Response<CampaignEmailTemplates> response) {
                CampaignEmailTemplates campaignEmailTemplates=response.body();
                if (campaignEmailTemplates.isSuccess()){
                    campaignTemplatesToImport=campaignEmailTemplates.getResult();
                    Log.d(Global.TAG, "campaignTemplates: "+campaignTemplatesToImport.size());
                    createCampaignActivity.importCampaignIds.clear();
                    TemplateListAdapter templateListAdapter=new TemplateListAdapter(context,campaignTemplatesToImport, true,false);
                    lv_import_campaign_templates.setAdapter(templateListAdapter);
//                    CampaignTemplateAdapter adapter=new CampaignTemplateAdapter(getActivity(),campaignTemplates);
//                    rv_campaign_templates.setAdapter(adapter);

                }else{
                    Log.d(Global.TAG, "Error in getting templates: ");
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                Log.d(Global.TAG, " getCampaignTemplateOnId onFailure: "+t);
                myLoader.dismiss();
            }
        });
    }

    private void deleteCampaign(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campIdToDelete", campaignsList.get(position).getCampaignId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_campaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Campaign Deleted Successfully...!", Toast.LENGTH_LONG).show();
                    notifyRefreshCamp.refreshCampaigns();
                   // customCampaignFragment.getAllCustomCampaign();
                }else{
                    Toast.makeText(context, "Error in deleting contact...!", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in deleting contact...!", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "deleteCampaign onFailure: "+t);
            }
        });
    }
    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(context, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }


    @Override
    public int getItemCount() {
        return campaignsList.size();
    }

   public void getUnImportedTemplates(final Spinner sp_import_campaign,int position){
       JSONObject paramObj = new JSONObject();
       try {
           paramObj.put("userId",user_id);
           paramObj.put("platform", "2");
           paramObj.put("parentCampainId", campaignsList.get(position).getCampaignId());

       } catch (JSONException e) {
           // TODO Auto-generated catch block
           Log.d(Global.TAG, "getUnImportedTemplates: "+e);
           e.printStackTrace();
       }
       Log.d(Global.TAG, "getUnImportedTemplates: "+paramObj.toString());

       final Dialog myLoader = Global.showDialog(context);
       myLoader.show();
       myLoader.setCanceledOnTouchOutside(true);
       APIService service= APIClient.getRetrofit().create(APIService.class);
       Call<GetAllCampaign> call=service.get_unImported_Campaigns(paramObj.toString());
       call.enqueue(new Callback<GetAllCampaign>() {
           @Override
           public void onResponse(Call<GetAllCampaign> call, Response<GetAllCampaign> response) {
               GetAllCampaign getAllCampaign=response.body();
               if (getAllCampaign.isSuccess()){
                   unImportedcampaignList=getAllCampaign.getResult();
                   Log.d(Global.TAG, "unImportedcampaignList: "+unImportedcampaignList.size());


                   import_campaign_array=new String[unImportedcampaignList.size()];
                   for (int i=0;i<unImportedcampaignList.size();i++){
                       import_campaign_array[i]=unImportedcampaignList.get(i).getCampaignTitle();
                   }
                   applySpinner(import_campaign_array,sp_import_campaign,"--Select Campaign--");

               }else{
                   Toast.makeText(context, "Error in getting campaigns..!", Toast.LENGTH_LONG).show();
               }
               myLoader.dismiss();
           }

           @Override
           public void onFailure(Call<GetAllCampaign> call, Throwable t) {
               myLoader.dismiss();
               Toast.makeText(context, "Error in getting campaigns..!", Toast.LENGTH_LONG).show();
               Log.d(Global.TAG, "onFailure: getAllCampaign"+t);
           }
       });

   }
}
