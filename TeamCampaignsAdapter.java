package com.success.successEntellus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.CreateNewTextMessageActivity;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.fragment.CampaignStepsFragment;
import com.success.successEntellus.fragment.TeamCampaignStepsFragment;
import com.success.successEntellus.fragment.TeamCampaignsfragment;
import com.success.successEntellus.fragment.TextCampaignHolder;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.model.CampaignEmailTemplates;
import com.success.successEntellus.model.CampaignTemplate;
import com.success.successEntellus.model.GetAllTeamCampaigns;
import com.success.successEntellus.model.GetAllTeamTemplates;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.TeamCampaign;
import com.success.successEntellus.model.TeamTemplate;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.TeamCampaignsHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 10/15/2018.
 */

public class TeamCampaignsAdapter extends RecyclerView.Adapter<TeamCampaignsHolder> {
    LayoutInflater inflater;
    View layout;
    DashboardActivity context;
    List<TeamCampaign> teamCampaignList;
    String user_id;
    NotifyTeamCampaigns notifyTeamCampaigns;
    private int send_by;
    String[] intervalType={"Days","Week","Month"};
    private String selected_interval_type;

    public TeamCampaignsAdapter(DashboardActivity activity, List<TeamCampaign> teamCampaignList, String user_id, TeamCampaignsfragment teamCampaignsfragment) {
        this.context = activity;
        this.teamCampaignList = teamCampaignList;
        this.user_id=user_id;
        this.notifyTeamCampaigns=teamCampaignsfragment;
    }

    public interface NotifyTeamCampaigns{
        public void refreshTeamCampaigns();
    }

    @Override
    public TeamCampaignsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.team_campaign_row, parent, false);
        TeamCampaignsHolder teamCampaignsHolder = new TeamCampaignsHolder(layout);
        return teamCampaignsHolder;
    }

    @Override
    public void onBindViewHolder(final TeamCampaignsHolder holder, final int position) {
        holder.tv_team_campaign_name.setText(teamCampaignList.get(position).getTeamCampaignTitle());

        Picasso.with(context)
                .load(teamCampaignList.get(position).getCampaignImage())
                .resize(400, 400)
                .into(holder.iv_team_campaign);


        List<Integer> colorList = teamCampaignList.get(position).getCampaignColor();
        if (colorList.size() > 0) {
            holder.ll_team_campaign.setCardBackgroundColor(Color.rgb(colorList.get(0), colorList.get(1), colorList.get(2)));
        }

        holder.ib_team_campaign_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTeamCampAllMenu(position,holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTeamCampaignTemplateOnId(position,teamCampaignList.get(position).getTeamCampaignTitle());
            }
        });
    }

    private void showTeamCampAllMenu(final int position, final TeamCampaignsHolder holder) {
        PopupMenu popup = new PopupMenu(context, holder.ib_team_campaign_menu);
        popup.getMenuInflater().inflate(R.menu.team_campaign_context_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_create_team_template:
                       /* Intent intent = new Intent(context, CreateNewTextMessageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("txtCampId", textCampaignList.get(position).getTxtCampId());
                        intent.putExtras(bundle);
                        textFragment.startActivityForResult(intent, 10);*/
                        Toast.makeText(context, "Create Template..!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_import_team_template:
                       // CheckMessagesForAddMember(position);
                        Toast.makeText(context, "Import Templates", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_create_team_self_reminder:
                        openDialogsetSelfReminder(position);
                        break;
                    case R.id.action_edit_team_campaign:
                        openDialogEditTeamCampaign( position);
                        break;
                    case R.id.action_delete_team_campaign:
                        new AlertDialog.Builder(context)
                                .setMessage("Are you sure you want to Delete this Text Campaign..?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        deleteTeamCampaign(position);
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

    public void getTeamCampaignTemplateOnId(final int position, final String campaignTitle) {
        Log.d(Global.TAG, "getTeamCampaignTemplateOnId: ");
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("teamCampaignId", teamCampaignList.get(position).getTeamCampaignId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getTeamCampaignTemplateOnId: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getTeamCampaignTemplateOnId: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllTeamTemplates> call=service.getAllTeamTemplate(paramObj.toString());
        call.enqueue(new Callback<GetAllTeamTemplates>() {
            @Override
            public void onResponse(Call<GetAllTeamTemplates> call, Response<GetAllTeamTemplates> response) {
                GetAllTeamTemplates getAllTeamTemplates=response.body();
                if (response.isSuccessful()){
                    if (getAllTeamTemplates.isSuccess()){
                        List<TeamTemplate> teamTemplateList=getAllTeamTemplates.getResult();
                        Log.d(Global.TAG, "teamTemplateList: "+teamTemplateList.size());

                        if (teamTemplateList.size()>0){
                            context.replaceFragments(new TeamCampaignStepsFragment(teamCampaignList.get(position).getTeamCampaignId(),campaignTitle));
                        }else{
                            Toast.makeText(context, "No Team Templates Added Yet..!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(context, "No Team Templates Added Yet..!", Toast.LENGTH_LONG).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllTeamTemplates> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: "+t);
            }
        });

    }


    private void deleteTeamCampaign(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("teamCampaignId", teamCampaignList.get(position).getTeamCampaignId());
            //  paramObj.put("campaignId", campaignsList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteTeamCampaign: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "deleteTeamCampaign: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.deleteTeamCampaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (response.isSuccessful()){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                        notifyTeamCampaigns.refreshTeamCampaigns();
                    }else{
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: deleteCampaign:"+t);
            }
        });

    }

    @Override
    public int getItemCount() {
        return teamCampaignList.size();
    }
    private void openDialogEditTeamCampaign(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.create_team_campaign_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        final EditText edt_team_campaign_name=dialog.findViewById(R.id.edt_team_campaign_name);
        final EditText edt_team_campaign_description=dialog.findViewById(R.id.edt_team_campaign_description);
        TextView tv_create_team_camp_title=dialog.findViewById(R.id.tv_create_team_camp_title);
        Button btn_save_team_campaign=dialog.findViewById(R.id.btn_save_team_campaign);
        Button btn_cancel_team_campaign=dialog.findViewById(R.id.btn_cancel_team_campaign);
        Button btn_team_create_dissmiss=dialog.findViewById(R.id.btn_team_create_dissmiss);

        getTeamCampaignById(position,edt_team_campaign_description);
        tv_create_team_camp_title.setText("Edit Team Campaign");
        edt_team_campaign_name.setText(teamCampaignList.get(position).getTeamCampaignTitle());
       // edt_team_campaign_description.setText(teamCampaignList.get(position));

        btn_save_team_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_team_campaign_name,"Enter Campaign Name")){
                    //createNewTextCampaign(dialog,edt_text_campaign_name);
                  editTeamCampaign(edt_team_campaign_name.getText().toString(),edt_team_campaign_description.getText().toString(),dialog,position);
                }
            }
        });
        btn_cancel_team_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_team_create_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void getTeamCampaignById(final int position, final EditText edt_team_campaign_description) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("teamCampaignId", teamCampaignList.get(position).getTeamCampaignId());
            //  paramObj.put("campaignId", campaignsList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getTeamCampaignById: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getTeamCampaignById: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllTeamCampaigns> call=service.getTeamCampaignById(paramObj.toString());
        call.enqueue(new Callback<GetAllTeamCampaigns>() {
            @Override
            public void onResponse(Call<GetAllTeamCampaigns> call, Response<GetAllTeamCampaigns> response) {
                GetAllTeamCampaigns getDetailCampaign=response.body();
                if (response.isSuccessful()){
                    if (getDetailCampaign.isSuccess()){
                        List<TeamCampaign> campaignDetail=getDetailCampaign.getResult();
                        Log.d(Global.TAG, "onResponse: campaignDetail:"+campaignDetail.size());
                        if (campaignDetail.size()==1){
                            if (campaignDetail.get(0).getTeamCampaignContent()!=null){
                                edt_team_campaign_description.setText(campaignDetail.get(0).getTeamCampaignContent());
                            }
                        }
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllTeamCampaigns> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: TeamCampDetail:"+t);
            }
        });
    }

    private void editTeamCampaign(String camp_name, String camp_desc, final Dialog dialog, int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("teamCampaignTitle", camp_name);
            paramObj.put("teamCampaignContent", camp_desc);
            paramObj.put("teamCampaignId", teamCampaignList.get(position).getTeamCampaignId());
            //  paramObj.put("campaignId", campaignsList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "editTeamCampaign: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "editTeamCampaign: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.editTeamCampaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> cal, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if ( response.isSuccessful()){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        notifyTeamCampaigns.refreshTeamCampaigns();
                    }else{
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: "+t);
            }
        });

    }
    private void openDialogsetSelfReminder(final int position) {
         final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.create_new_self_reminder_team_campaign);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        Button btn_team_reminder_dissmiss = (Button) dialog.findViewById(R.id.btn_team_reminder_dissmiss);
        Button btn_set_reminder_team = (Button) dialog.findViewById(R.id.btn_set_reminder_team);
        Button btn_reminder_cancel_team = (Button) dialog.findViewById(R.id.btn_reminder_cancel_team);
        RadioButton rb_temail = (RadioButton) dialog.findViewById(R.id.rb_temail);
        RadioButton rb_tsmsemail = (RadioButton) dialog.findViewById(R.id.rb_tsmsemail);

        final EditText edt_time_interval_team = (EditText) dialog.findViewById(R.id.edt_time_interval_team);
        final EditText edt_self_rem_note_team = (EditText) dialog.findViewById(R.id.edt_self_rem_note_team);
        final Spinner sp_select_interval_team = (Spinner) dialog.findViewById(R.id.sp_select_interval_team);


        /*int selected_rb=rbg_rem_send_type.getCheckedRadioButtonId();
        if (selected_rb==R.id.rb_email){
            send_by=1;
        }else if (selected_rb==R.id.rb_smsemail){
            send_by=2;
        }*/

        rb_temail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    send_by=1;
                }else{
                    send_by=2;
                }
            }
        });

        rb_tsmsemail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    send_by=2;
                }else{
                    send_by=1;
                }
            }
        });

        btn_reminder_cancel_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_team_reminder_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_set_reminder_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_time_interval_team,"Enter Time Interval")){
                    if (!sp_select_interval_team.getSelectedItem().toString().contains("Select")){
                        if (!edt_time_interval_team.getText().toString().equals("0")){
                            setTeamCampSelfReminder(edt_time_interval_team.getText().toString(),edt_self_rem_note_team.getText().toString(),position);
                        }else{
                            Toast.makeText(context, "Interval Must be greater than 0..!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        MyValidator.setSpinnerError(sp_select_interval_team,"Select Type..!");
                    }

                }else{
                    edt_time_interval_team.requestFocus();
                }

                //setSelfReminder(edt_time_interval.getText().toString(),edt_self_rem_note.getText().toString(),position);
            }
        });
        applySpinner(intervalType,sp_select_interval_team,"--Select--");

        sp_select_interval_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_select_interval_team.getSelectedItem().toString().contains("Select")) {
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


    private void setTeamCampSelfReminder(String time_interval, String note_contents, int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("teamCampaignId", teamCampaignList.get(position).getTeamCampaignId());
            paramObj.put("teamCampaignStepContent", note_contents);
            paramObj.put("teamCampaignStepSendTo", send_by);
            paramObj.put("teamCampaignStepSendInterval", time_interval);
            paramObj.put("teamCampaignStepSendIntervalType",selected_interval_type);
            //  paramObj.put("campaignId", campaignsList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "addSelfTemplate: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "addSelfTemplate: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.addSelfTemplate(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (response.isSuccessful()){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                        notifyTeamCampaigns.refreshTeamCampaigns();
                    }else{
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: addSelfTemplate: "+t);
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

}
