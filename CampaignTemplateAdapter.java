package com.success.successEntellus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.AddTemplateActivity;
import com.success.successEntellus.fragment.CampaignStepsFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.model.CampaignEmailTemplates;
import com.success.successEntellus.model.CampaignTemplate;
import com.success.successEntellus.model.EmailAttachment;
import com.success.successEntellus.model.EmailDetail;
import com.success.successEntellus.model.EmailDetails;
import com.success.successEntellus.model.Emails;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.CampaignTemplateHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.success.successEntellus.fragment.CustomCampaignFragment.dialog;

/**
 * Created by user on 5/9/2018.
 */

public class CampaignTemplateAdapter extends RecyclerView.Adapter<CampaignTemplateHolder>{
    LayoutInflater inflater;
    Context context;
    View layout;
    List<CampaignTemplate> campaignTemplates;
    String[] intervalType={"Days","Week","Month"};
    private String selected_interval_type="";
    String user_id;
    String campaign_id;
    boolean companyFlag;
    int send_by;
    TextView tv_template_name;
    TextView tv_template_created_on;
    ListView lv_email_details;
    Button btn_delete_email,btn_delete_all_following;
    CampaignStepsFragment campaignStepsFragment=new CampaignStepsFragment();
    CampaignStepsFragment campaignStepsFragment1;
    List<String> deleteAttachmentIds;
    LinearLayout ll_view_attachments;
    //Repeat
    RadioButton rb_ecti_immediately,rb_ecti_schedule,rb_repeat_email_campti;
    LinearLayout ll_email_ti_schedule,ll_repeat_email_camp_ti,ll_footer_view;
    TextView tv_ecti_check_days_error;
    EditText edt_ecti_ends_after;
    String selectType,repeat_every_weeks="",repeat_on_days_string="",currDayName,repeat_occurences,repeatFlag;
    List<String> repeat_every_list=new ArrayList<>();
    int day,month,year;
    Calendar cal;
    RadioGroup rbg_ecti_interval;
    CheckBox ch_ecti_monday,ch_ecti_tuesday,ch_ecti_wednesday,ch_ecti_thursday,ch_ecti_friday,ch_ecti_saturday,ch_ecti_sunday;
    List<String> dayList=new ArrayList<>();
    private int lastposition=-1;

    public  interface RefreshAllEmailCampaigns{
        void refreshCampaigns();

    }
    private RefreshAllEmailCampaigns refreshAllEmailCampaigns;

    public CampaignTemplateAdapter(Context context, CampaignStepsFragment campaignStepsFragment, List<CampaignTemplate> campaignTemplates, String user_id, String campaign_id, boolean companyFlag, CampaignStepsFragment stepsFragment) {
     this.context=context;
     this.campaignTemplates=campaignTemplates;
     this.user_id=user_id;
     this.campaign_id=campaign_id;
     this.companyFlag=companyFlag;
     this.campaignStepsFragment1=campaignStepsFragment;
     this.refreshAllEmailCampaigns=stepsFragment;
    }

    @Override
    public CampaignTemplateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        deleteAttachmentIds=new ArrayList<>();
        layout=inflater.inflate(R.layout.campaign_template_row,parent,false);
        CampaignTemplateHolder campaignTemplateHolder=new CampaignTemplateHolder(context,layout);
        return campaignTemplateHolder;
    }

    @Override
    public void onBindViewHolder(final CampaignTemplateHolder holder, final int position) {
        holder.tv_campaign_template_name.setText(campaignTemplates.get(position).getCampaignStepTitle());
        holder.tv_campaign_days.setText(campaignTemplates.get(position).getCampaignStepSendInterval()+" "+campaignTemplates.get(position).getCampaignStepSendIntervalType());
        holder.campaignTemplates=campaignTemplates;
        holder.position=position;

        Log.d(Global.TAG, "companyFlag: "+companyFlag);
        if (companyFlag){
            holder.ib_template_edit.setVisibility(View.GONE);
            holder.ib_template_delete.setVisibility(View.GONE);
            holder.tv_email_details.setVisibility(View.GONE);
           // holder.tv_campaign_days.setVisibility(View.GONE);
            holder.iv_time_ic.setVisibility(View.GONE);
        }
        if (campaignTemplates.get(position).getCampiagnEndStepEmailReminder().equals("1")){
            holder.tv_email_details.setVisibility(View.INVISIBLE);
           // holder.iv_campaign_template.setVisibility(View.GONE);
        }else{
            if(!companyFlag){
                holder.tv_email_details.setVisibility(View.VISIBLE);
                holder.tv_campaign_days.setVisibility(View.VISIBLE);
               // holder.iv_campaign_template.setVisibility(View.VISIBLE);
            }

        }
        holder.tv_campaign_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!companyFlag){
                    openSetTimeIntervalDialog(position);
                }
            }
        });

        holder.tv_email_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmailDetails(position);
               // openEmailDetailsDialog(position);
            }
        });

        holder.ib_template_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strDelete="";
                if (campaignTemplates.get(position).getCampiagnEndStepEmailReminder().equals("1")){
                    strDelete="Are you sure, you want to delete this self-reminder?";
                }else{
                    strDelete="Are you sure, you want to delete this template?";
                }
                Toast.makeText(context, "Delete Campaign", Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(context)
                        .setMessage(strDelete)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteEmailTemplate(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogViewTemplate(position);
            }
        });

        holder.ib_template_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (campaignTemplates.get(position).getCampiagnEndStepEmailReminder().equals("1")){
                    openDialogsetSelfReminder(position);
                }else{
                    Intent intent=new Intent(context, AddTemplateActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putBoolean("editFlag",true);
                    bundle.putString("campaignStepId",campaignTemplates.get(position).getCampaignStepId());
                    bundle.putString("footerFlag",campaignTemplates.get(position).getCampaignStepFooterFlag());
                    bundle.putString("campaign_id",campaign_id);
                    bundle.putString("email_subject",campaignTemplates.get(position).getCampaignStepSubject());
                    bundle.putString("email_heading",campaignTemplates.get(position).getCampaignStepTitle());
                    bundle.putString("email_contents",campaignTemplates.get(position).getCampaignStepContent());
                    bundle.putString("email_interval",campaignTemplates.get(position).getCampaignStepSendInterval());
                    bundle.putString("email_interval_type",campaignTemplates.get(position).getCampaignStepSendIntervalType());
                    bundle.putString("email_repeat_flag",campaignTemplates.get(position).getCampaignStepRepeat());
                    bundle.putString("email_repeat_days",campaignTemplates.get(position).getCampRepeatDays());
                    bundle.putString("email_repeat_occurrence",campaignTemplates.get(position).getCampRepeatEndOccurrence());
                    bundle.putString("email_repeat_week",campaignTemplates.get(position).getCampRepeatWeeks());
                    intent.putExtras(bundle);

                    if (campaignStepsFragment1!=null){
                        campaignStepsFragment1.startActivityForResult(intent,103);
                    }

                }

            }
        });

        Log.d(Global.TAG, "StepImage: "+campaignTemplates.get(position).getStepImage());
        Picasso.with(context)
                .load(campaignTemplates.get(position).getStepImage())
                .placeholder(R.drawable.place)   // optional
                .error(R.drawable.error)      // optional
                .resize(400, 400)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.d(Global.TAG, "onBitmapLoaded: ");
                        holder.ll_campaign_template.setBackground(new BitmapDrawable(bitmap));

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.d(Global.TAG, "onBitmapFailed: ");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.d(Global.TAG, "onPrepareLoad: ");
                    }
                });
        setAnimation(holder.itemView,position);
    }

    private void checkEmailDetails(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignStepId", campaignTemplates.get(position).getCampaignStepId());
            paramObj.put("campaignId", campaign_id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getEmailDetails: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getEmailDetails: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<EmailDetails> call=service.getEmailDetails(paramObj.toString());
        call.enqueue(new Callback<EmailDetails>() {
            @Override
            public void onResponse(Call<EmailDetails> call, Response<EmailDetails> response) {
                EmailDetails emailDetails=response.body();
                if (emailDetails.isSuccess()){

                    EmailDetail emailDetail=emailDetails.getResult();

                    List<Emails> emailsList=emailDetail.getEmailDetails();
                    Log.d(Global.TAG, "emailsList: "+emailsList.size());
                    if (emailsList.size()>0){
                        openEmailDetailsDialog(position);
                    }else{
                        Toast.makeText(context, "No emails added yet..!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context, ""+emailDetails.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<EmailDetails> call, Throwable t) {
                myLoader.dismiss();
                btn_delete_all_following.setVisibility(View.GONE);
                btn_delete_email.setVisibility(View.GONE);
                lv_email_details.setVisibility(View.GONE);
                Toast.makeText(context, "No emails added yet..!", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onViewDetachedFromWindow(CampaignTemplateHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private void setAnimation(View viewToAnimate, int position)
    {

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastposition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        viewToAnimate.startAnimation(animation);
        lastposition = position;
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

        edt_time_interval.setText(campaignTemplates.get(position).getCampaignStepSendInterval());
        edt_self_rem_note.setText(campaignTemplates.get(position).getCampaignStepContent());
        send_by=campaignTemplates.get(position).getCampaignStepSendTo();
        Log.d(Global.TAG, "openDialogsetSelfReminder: SendTo: "+campaignTemplates.get(position).getCampaignStepSendTo());

        if (send_by==1){
            rb_email.setChecked(true);
        }else if(send_by==2){
            rb_smsemail.setChecked(true);
        }
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
                    if (!edt_time_interval.getText().toString().equals("0")) {
                        if (!sp_select_interval.getSelectedItem().toString().contains("Select")) {
                            setSelfReminder(edt_time_interval.getText().toString(), edt_self_rem_note.getText().toString(), position);
                        } else {
                            MyValidator.setSpinnerError(sp_select_interval, "Select Type..!");
                        }
                    }else{
                        Toast.makeText(context, "Interval must be greater than 0..!", Toast.LENGTH_LONG).show();
                        edt_time_interval.setFocusable(true);
                        edt_time_interval.requestFocus();
                    }
                }else{
                    edt_time_interval.requestFocus();
                }

            }
        });
        applySpinner(intervalType,sp_select_interval,"--Select--");

        String intervalTypes=campaignTemplates.get(position).getCampaignStepSendIntervalType();
        Log.d(Global.TAG, "openDialogsetSelfReminder: intervalTypes:"+intervalTypes);

        if (intervalTypes.equals("days")){
            sp_select_interval.setSelection(1);
        }else if (intervalTypes.equals("week")){
            sp_select_interval.setSelection(2);
        }else if (intervalTypes.equals("month")){
            sp_select_interval.setSelection(3);
        }
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

        rbg_rem_send_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selected_rb=radioGroup.getCheckedRadioButtonId();
                if (selected_rb==R.id.rb_email){
                    send_by=1;
                    Log.d(Global.TAG, "onCheckedChanged:selected_rb:rb_email");
                }else if (selected_rb==R.id.rb_smsemail){
                    send_by=2;
                    Log.d(Global.TAG, "onCheckedChanged:selected_rb:rb_smsemail");
                }
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void setSelfReminder(String timeInterval, String note_content, final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignStepId", campaignTemplates.get(position).getCampaignStepId());
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
        Call<JsonResult> call=service.update_self_reminder(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Self Reminder Updated Successfully..!", Toast.LENGTH_LONG).show();
                    //campaignStepsFragment.getCampaignTemplateOnId();
                    refreshAllEmailCampaigns.refreshCampaigns();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, "Error in Updating Self Reminder", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in Updating Self Reminder...!", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure: Self Reminder: "+t);
            }
        });
    }


    private void deleteEmailTemplate(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignStepId", campaignTemplates.get(position).getCampaignStepId());
            paramObj.put("campaignStepCamId", campaign_id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteEmailTemplate: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteEmailTemplate: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_email_template(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    if (campaignTemplates.get(position).getCampiagnEndStepEmailReminder().equals("1")){
                        Toast.makeText(context, "Self Reminder is Deleted Successfully ..!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context, "'"+campaignTemplates.get(position).getCampaignStepTitle()+"'"+" email template Deleted Successfully..!", Toast.LENGTH_LONG).show();
                    }
                    notifyItemRemoved(position);
                    //campaignStepsFragment.getCampaignTemplateOnId();
                    refreshAllEmailCampaigns.refreshCampaigns();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in deleting template", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void openSetTimeIntervalDialog(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.set_time_interval_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        final EditText edt_time_intervald=(EditText)dialog.findViewById(R.id.edt_time_intervald);
        final Spinner sp_select_intervald=(Spinner)dialog.findViewById(R.id.sp_select_intervald);
        final Spinner sp_ec_repeate_every_ti=(Spinner)dialog.findViewById(R.id.sp_ec_repeate_every_ti);
        Button btn_set_interval=(Button) dialog.findViewById(R.id.btn_set_interval);
        Button btn_interval_cancel=(Button) dialog.findViewById(R.id.btn_timeinterval_cancel);
        Button btn_interval_dissmiss=(Button) dialog.findViewById(R.id.btn_interval_dissmiss);

        ch_ecti_monday=(CheckBox) dialog.findViewById(R.id.ch_ecti_monday);
        ch_ecti_tuesday=(CheckBox) dialog.findViewById(R.id.ch_ecti_tuesday);
        ch_ecti_wednesday=(CheckBox) dialog.findViewById(R.id.ch_ecti_wednesday);
        ch_ecti_thursday=(CheckBox) dialog.findViewById(R.id.ch_ecti_thursday);
        ch_ecti_friday=(CheckBox) dialog.findViewById(R.id.ch_ecti_friday);
        ch_ecti_saturday=(CheckBox) dialog.findViewById(R.id.ch_ecti_saturday);
        ch_ecti_sunday=(CheckBox) dialog.findViewById(R.id.ch_ecti_sunday);

        rb_ecti_immediately=(RadioButton) dialog.findViewById(R.id.rb_ecti_immediately);
        rb_ecti_schedule=(RadioButton) dialog.findViewById(R.id.rb_ecti_schedule);
        rb_repeat_email_campti=(RadioButton) dialog.findViewById(R.id.rb_repeat_email_campti);
        rbg_ecti_interval=(RadioGroup) dialog.findViewById(R.id.rbg_ecti_interval);

        ll_email_ti_schedule=(LinearLayout) dialog.findViewById(R.id.ll_email_ti_schedule);
        ll_repeat_email_camp_ti=(LinearLayout) dialog.findViewById(R.id.ll_repeat_email_camp_ti);
        tv_ecti_check_days_error=(TextView) dialog.findViewById(R.id.tv_ecti_check_days_error);
        edt_ecti_ends_after=(EditText) dialog.findViewById(R.id.edt_ecti_ends_after);


        if (campaignTemplates.get(position).campiagnEndStepEmailReminder.equals("1")){
            rbg_ecti_interval.setVisibility(View.GONE);
            ll_email_ti_schedule.setVisibility(View.VISIBLE);
        }else{
            rbg_ecti_interval.setVisibility(View.VISIBLE);
        }


        rb_ecti_immediately.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_repeat_email_camp_ti.setVisibility(View.GONE);
                    ll_email_ti_schedule.setVisibility(View.GONE);
                }
            }
        });

        rb_ecti_schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_email_ti_schedule.setVisibility(View.VISIBLE);
                    ll_repeat_email_camp_ti.setVisibility(View.GONE);
                }else{
                    ll_email_ti_schedule.setVisibility(View.GONE);
                }
            }
        });

        rb_repeat_email_campti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_email_ti_schedule.setVisibility(View.GONE);
                    ll_repeat_email_camp_ti.setVisibility(View.VISIBLE);
                    fillSpinnerwith12(sp_ec_repeate_every_ti);
                    //selectCurrentDay();

                }else{
                    ll_repeat_email_camp_ti.setVisibility(View.GONE);
                }
            }
        });



        btn_interval_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_interval_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_set_interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (campaignTemplates.get(position).getCampiagnEndStepEmailReminder().equals("1")){
                    if (MyValidator.isValidFieldE(edt_time_intervald,"Enter Time Interval")){
                        if (!edt_time_intervald.getText().toString().equals("0")){
                            if (!sp_select_intervald.getSelectedItem().toString().contains("Select")){
                                setTimeIntervalReminder(position,edt_time_intervald.getText().toString(),dialog);
                            }else{
                                MyValidator.setSpinnerError(sp_select_intervald,"Select Type");
                            }

                        }else{
                            Toast.makeText(context, "Interval must be greater than 0..!", Toast.LENGTH_LONG).show();
                            edt_time_intervald.setFocusable(true);
                            edt_time_intervald.requestFocus();
                        }
                    }
                }else{
                    if (ll_email_ti_schedule.getVisibility()==View.VISIBLE){
                        if (MyValidator.isValidFieldE(edt_time_intervald,"Enter Time Interval")){
                            if (!edt_time_intervald.getText().toString().equals("0")){
                                if (!sp_select_intervald.getSelectedItem().toString().contains("Select")){
                                    setTimeIntervalTemplate(position,edt_time_intervald.getText().toString(),dialog);
                                }else{
                                    MyValidator.setSpinnerError(sp_select_intervald,"Select Type");
                                }

                            }else{
                                Toast.makeText(context, "Interval must be greater than 0..!", Toast.LENGTH_LONG).show();
                                edt_time_intervald.setFocusable(true);
                                edt_time_intervald.requestFocus();
                            }
                        }
                    }else if (ll_repeat_email_camp_ti.getVisibility()==View.VISIBLE){
                        if (!edt_ecti_ends_after.getText().toString().equals("") ){
                            if (!ch_ecti_monday.isChecked() && !ch_ecti_tuesday.isChecked() && !ch_ecti_wednesday.isChecked() && !ch_ecti_thursday.isChecked() && !ch_ecti_friday.isChecked()
                                    && !ch_ecti_saturday.isChecked() && !ch_ecti_sunday.isChecked()) {
                                tv_ecti_check_days_error.setVisibility(View.VISIBLE);
                            }else{
                                tv_ecti_check_days_error.setVisibility(View.GONE);
                                setTimeIntervalTemplate(position,edt_time_intervald.getText().toString(),dialog);
                            }
                        }else{
                            edt_ecti_ends_after.setError("Enter Occurences");
                        }

                    }else{
                        setTimeIntervalTemplate(position,edt_time_intervald.getText().toString(),dialog);
                    }

                }


            }
        });


        applySpinner(intervalType,sp_select_intervald,"--Select--");

       if (campaignTemplates.get(position).getCampaignStepRepeat()!=null){
           if (campaignTemplates.get(position).getCampaignStepRepeat().equals("1")){

               rb_repeat_email_campti.setChecked(true);
               String repeat_every=campaignTemplates.get(position).getCampRepeatWeeks();
               repeat_on_days_string=campaignTemplates.get(position).getCampRepeatDays();
               Log.d(Global.TAG, "openSetTimeIntervalDialog: Repeat Every: "+repeat_every);

               sp_ec_repeate_every_ti.setSelection(Integer.parseInt(repeat_every)-1);
               edt_ecti_ends_after.setText(campaignTemplates.get(position).getCampRepeatEndOccurrence());
               if (!repeat_on_days_string.equals("")){
                   String[] week_days=repeat_on_days_string.split(",");
                   Log.d(Global.TAG, "week_days: "+week_days.length);
                   for(int i=0;i<week_days.length;i++){
                       Log.d(Global.TAG, "week_days: "+week_days[i]);
                       String day_name=week_days[i];
                       //String name=day_name.substring(0,day_name.length()-1);
                       selectWeekDays(day_name);
                   }
               }
           }else if (campaignTemplates.get(position).getCampaignStepSendInterval().equals("0")){
               rb_ecti_immediately.setChecked(true);
           }else{
               edt_time_intervald.setText(campaignTemplates.get(position).getCampaignStepSendInterval());
               rb_ecti_schedule.setChecked(true);
               String intervaltype=campaignTemplates.get(position).getCampaignStepSendIntervalType();
               Log.d(Global.TAG, "openSetTimeIntervalDialog: intervaltype:"+intervaltype);
               if (intervaltype.equals("days")){
                   sp_select_intervald.setSelection(1);
               }else if (intervaltype.equals("week")){
                   sp_select_intervald.setSelection(2);
               }else if (intervaltype.equals("month")){
                   sp_select_intervald.setSelection(3);
               }
           }
       }


        if (companyFlag){
            edt_time_intervald.setEnabled(false);
            edt_time_intervald.setClickable(false);
            sp_select_intervald.setEnabled(false);
            sp_select_intervald.setClickable(false);
            btn_set_interval.setClickable(false);
        }

        sp_select_intervald.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_select_intervald.getSelectedItem().toString().contains("Select")) {
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

        sp_ec_repeate_every_ti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_ec_repeate_every_ti.getSelectedItem().toString().contains("Select")) {
                    repeat_every_weeks = "";
                } else {
                    repeat_every_weeks = repeat_every_list.get(position);
                    Log.d(Global.TAG, "onItemSelected:repeat_every_weeks: "+repeat_every_weeks);
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
    private void selectWeekDays(String name) {
        if (!name.equals("")){
            if (name.equalsIgnoreCase("mon")){
                ch_ecti_monday.setChecked(true);
            }else if (name.equalsIgnoreCase("tue")){
                ch_ecti_tuesday.setChecked(true);
            }else if (name.equalsIgnoreCase("wed")){
                ch_ecti_wednesday.setChecked(true);
            }else if (name.equalsIgnoreCase("thu")){
                ch_ecti_thursday.setChecked(true);
            }else if (name.equalsIgnoreCase("fri")){
                ch_ecti_friday.setChecked(true);
            }else if (name.equalsIgnoreCase("sat")){
                ch_ecti_saturday.setChecked(true);
            }else if (name.equalsIgnoreCase("sun")){
                ch_ecti_sunday.setChecked(true);
            }
        }
    }
    private void fillSpinnerwith12(Spinner sp_ec_repeate_every_ti) {
        repeat_every_list.clear();
        for (int i=1;i<=13;i++){
            repeat_every_list.add(String.valueOf(i));
        }
        applySpinner1(repeat_every_list,sp_ec_repeate_every_ti,"-Select-");
        sp_ec_repeate_every_ti.setSelection(0);

        /*if (editFlag){
            if (repeat_every_weeks!=null){
                sp_ec_repeate_every_ti.setSelection(Integer.parseInt(repeat_every_weeks)-1);
            }
        }else{
            sp_ec_repeate_every.setSelection(0);
        }*/
    }
    private void applySpinner1(final List<String> taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(context, android.R.layout.simple_list_item_1);
        // adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        //adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }

    private void openEmailDetailsDialog(final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.email_details_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        tv_template_name=(TextView) dialog.findViewById(R.id.tv_template_name);
        TextView tv_delete_note=(TextView) dialog.findViewById(R.id.tv_delete_note);
        TextView tv_delete_note1=(TextView) dialog.findViewById(R.id.tv_delete_note1);
        TextView tv_delete_note2=(TextView) dialog.findViewById(R.id.tv_delete_note2);
        tv_template_created_on=(TextView) dialog.findViewById(R.id.tv_template_created_on);
        lv_email_details=(ListView) dialog.findViewById(R.id.lv_email_details);
        Button btn_email_dissmiss=(Button) dialog.findViewById(R.id.btn_email_dissmiss);
        btn_delete_email=(Button) dialog.findViewById(R.id.btn_delete_email);
        btn_delete_all_following=(Button) dialog.findViewById(R.id.btn_delete_all_following);

        if (campaignTemplates.get(position).getCampaignStepRepeat().equals("1")){
            btn_delete_all_following.setVisibility(View.GONE);
        }else{
            btn_delete_all_following.setVisibility(View.VISIBLE);
        }
        getEmailDetails(position);

        tv_delete_note.setText(Html.fromHtml(context.getResources().getString(R.string.email_details_note)));
        tv_delete_note1.setText(Html.fromHtml(context.getResources().getString(R.string.email_details_note1)));
        tv_delete_note2.setText(Html.fromHtml(context.getResources().getString(R.string.email_details_note2)));

        btn_email_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                campaignStepsFragment.emailDetails=new JSONArray();
                dialog.dismiss();
            }
        });

        btn_delete_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (campaignStepsFragment.emailDetails.length()>0){
                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete this email from current email Template only.?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteEmail(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }else{
                    Toast.makeText(context, "Please Select Email to Delete..!", Toast.LENGTH_LONG).show();
                }

            }
        });

        btn_delete_all_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (campaignStepsFragment.emailDetails.length()>0){
                            new AlertDialog.Builder(context)
                                    .setMessage(" Are you sure you want to delete this email from current and following email Templates?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            deleteAllFollowing(position);
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();


                }else{
                    Toast.makeText(context, "Please Select Email to Delete..!", Toast.LENGTH_LONG).show();
                }

            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void deleteAllFollowing(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("stepid", campaignTemplates.get(position).getCampaignStepId());
            paramObj.put("stepCamId", campaign_id);
            paramObj.put("emailDetails", campaignStepsFragment.emailDetails);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteAllFollowing: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteAllFollowing: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_all_following(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Email deleted successfully from this & all future templates.", Toast.LENGTH_LONG).show();
                    campaignStepsFragment.emailDetails=new JSONArray();
                    getEmailDetails(position);
                }else{
                    Toast.makeText(context, "Error in Deleting Email", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in Deleting Email", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteEmail(final int position) {
        Log.d(Global.TAG, "deleteEmail: ");
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("stepid", campaignTemplates.get(position).getCampaignStepId());
            paramObj.put("stepCamId", campaign_id);
            paramObj.put("emailDetails", campaignStepsFragment.emailDetails);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteEmail: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteEmail: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_email(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Email(s) deleted successfully from this email template only", Toast.LENGTH_LONG).show();
                    campaignStepsFragment.emailDetails=new JSONArray();
                    getEmailDetails(position);
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in Deleting Email", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getEmailDetails(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignStepId", campaignTemplates.get(position).getCampaignStepId());
            paramObj.put("campaignId", campaign_id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getEmailDetails: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getEmailDetails: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<EmailDetails> call=service.getEmailDetails(paramObj.toString());
        call.enqueue(new Callback<EmailDetails>() {
            @Override
            public void onResponse(Call<EmailDetails> call, Response<EmailDetails> response) {
                EmailDetails emailDetails=response.body();
                if (emailDetails.isSuccess()){

                    EmailDetail emailDetail=emailDetails.getResult();
                    //btn_delete_all_following.setVisibility(View.VISIBLE);
                    btn_delete_email.setVisibility(View.VISIBLE);
                    lv_email_details.setVisibility(View.VISIBLE);
                    tv_template_name.setText(emailDetail.getCampaignStepTitle());
                    tv_template_created_on.setText(emailDetail.getCampaignStepAddDate());
                    Log.d(Global.TAG, "tv_template_name: "+emailDetail.getCampaignStepTitle()+"tv_template_created_on"+emailDetail.getCampaignStepAddDate());

                    List<Emails> emailsList=emailDetail.getEmailDetails();
                    Log.d(Global.TAG, "emailsList: "+emailsList.size());
                    if (emailsList.size()>0){
                        EmailAdapter adapter=new EmailAdapter(context,emailsList,emailDetail.getCampaignStepAddDate());
                        lv_email_details.setAdapter(adapter);
                    }else{
                        //btn_delete_all_following.setVisibility(View.GONE);
                        dialog.dismiss();
                        btn_delete_email.setVisibility(View.GONE);
                        lv_email_details.setVisibility(View.GONE);
                        Toast.makeText(context, "No Emails Available..!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    dialog.dismiss();
                    Toast.makeText(context, ""+emailDetails.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<EmailDetails> call, Throwable t) {
                dialog.dismiss();
                myLoader.dismiss();
                btn_delete_all_following.setVisibility(View.GONE);
                btn_delete_email.setVisibility(View.GONE);
                lv_email_details.setVisibility(View.GONE);
                Toast.makeText(context, "No Emails Available..!", Toast.LENGTH_LONG).show();
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

    private void setTimeIntervalReminder(int position, String time_interval, final Dialog dialog) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignStepId", campaignTemplates.get(position).getCampaignStepId());
            paramObj.put("campaignStepSendInterval", time_interval);
            paramObj.put("campaignStepSendIntervalType", selected_interval_type.toLowerCase());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "setTimeInterval: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "setTimeIntervalReminder: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.set_time_interval_reminder(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    //campaignStepsFragment.getCampaignTemplateOnId();
                    refreshAllEmailCampaigns.refreshCampaigns();
                    notifyDataSetChanged();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in setting Time Interval", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure setTimeInterval: "+t);
            }
        });
    }
    private void setTimeIntervalTemplate(int position, String time_interval, final Dialog dialog) {
        if (ll_email_ti_schedule.getVisibility()==View.VISIBLE){
            selectType="2";
            repeat_every_weeks="";
        }else if (ll_repeat_email_camp_ti.getVisibility()==View.VISIBLE){
            selectType="3";
            getDaysSelection();
            time_interval="";
            selected_interval_type="";
        }else{
            selectType="1";
            repeat_every_weeks="";
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignStepId", campaignTemplates.get(position).getCampaignStepId());
            paramObj.put("campaignStepSendInterval", time_interval);
            paramObj.put("campaignStepSendIntervalType", selected_interval_type.toLowerCase());
            paramObj.put("selectType", selectType);
            paramObj.put("repeat_every_weeks", repeat_every_weeks);
            paramObj.put("repeat_on",repeat_on_days_string);
            paramObj.put("repeat_ends_after", edt_ecti_ends_after.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "setTimeInterval: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "setTimeIntervalTemplate: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.set_time_interval_template(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                   // campaignStepsFragment.getCampaignTemplateOnId();
                    refreshAllEmailCampaigns.refreshCampaigns();
                    notifyDataSetChanged();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in setting Time Interval", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure setTimeInterval: "+t);
            }
        });
    }
    private void getDaysSelection() {

        dayList.clear();

        if (ch_ecti_monday.isChecked()){
            dayList.add("Mon");
        }
        if (ch_ecti_tuesday.isChecked()){
            dayList.add("Tue");
        }
        if (ch_ecti_wednesday.isChecked()){
            dayList.add("Wed");
        }
        if (ch_ecti_thursday.isChecked()){
            dayList.add("Thu");
        }
        if (ch_ecti_friday.isChecked()){
            dayList.add("Fri");
        }
        if (ch_ecti_saturday.isChecked()){
            dayList.add("Sat");
        }
        if (ch_ecti_sunday.isChecked()){
            dayList.add("Sun");
        }

        repeat_on_days_string="";
        for (String s : dayList)
        {
            repeat_on_days_string += s + ",";
        }
        if (repeat_on_days_string.endsWith(",")) {
            repeat_on_days_string = repeat_on_days_string.substring(0, repeat_on_days_string.length() - 1);
        }
        Log.d(Global.TAG, "getDaysSelection: "+repeat_on_days_string);
    }
    @Override
    public int getItemCount() {
        return campaignTemplates.size();
    }
    private void getTemplateDetailsForViewAttachment(int position, final LinearLayout ll_attachment_title) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("stepId",campaignTemplates.get(position).getCampaignStepId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getTemplateDetailsOnId: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getTemplateDetailsOnId: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CampaignEmailTemplates> call=service.getTemplateDetailsonId(paramObj.toString());
        call.enqueue(new Callback<CampaignEmailTemplates>() {
            @Override
            public void onResponse(Call<CampaignEmailTemplates> call, Response<CampaignEmailTemplates> response) {
                CampaignEmailTemplates getEmailTemplate=response.body();
                if (getEmailTemplate.isSuccess()){

                    List<CampaignTemplate> templateList=getEmailTemplate.getResult();
                    deleteAttachmentIds.clear();
                    if (templateList.size()==1){
                        List<EmailAttachment> attachMentList=templateList.get(0).getAttachements();
                        Log.d(Global.TAG, "AttachmentList: "+attachMentList.size());

                        if (templateList.get(0).getCampaignStepFooterFlag().equals("1")){
                            ll_footer_view.setVisibility(View.VISIBLE);
                            Log.d(Global.TAG, "openDialogViewTemplate: Footer flag 1");
                        }else{
                            ll_footer_view.setVisibility(View.GONE);
                            Log.d(Global.TAG, "openDialogViewTemplate: Footer flag 0");
                        }

                        String attachments="";
                        ll_view_attachments.removeAllViews();
                        if (attachMentList.size()>0) {
                            ll_attachment_title.setVisibility(View.VISIBLE);
                            for (int i = 0; i < attachMentList.size(); i++) {
                                String file_path = attachMentList.get(i).getFilePath();
                                String file_name = file_path.substring(file_path.lastIndexOf("/") + 1, file_path.length());
                                Log.d(Global.TAG, "onResponse: " + file_name);
                                deleteAttachmentIds.add(attachMentList.get(i).getAttachmentId());
                                Log.d(Global.TAG, "Attachment: "+file_name);
                                displayEmailAttachment(file_name);
/*
                                if (attachments.equals("")){
                                    attachments=attachments+file_name;
                                }else{
                                    attachments=attachments+"\n"+file_name;
                                }*/
                            }
                           // tv_email_attachments.setText(attachments);

                        }else{
                            ll_attachment_title.setVisibility(View.GONE);
                        }

                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                myLoader.dismiss();
                ll_attachment_title.setVisibility(View.GONE);
                Log.d(Global.TAG, "onFailure: getDetails:"+t);
            }
        });

    }

    private void displayEmailAttachment(String file_name) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View attachmentView = inflater.inflate(R.layout.attchment_view, null);
        TextView tv_attachment_name = (TextView) attachmentView.findViewById(R.id.tv_view_attachment);
        tv_attachment_name.setText(file_name);
        ll_view_attachments.addView(attachmentView);
    }

    private void openDialogViewTemplate(final int position) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.view_template);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");


        TextView tv_template_content = (TextView) dialog.findViewById(R.id.tv_template_content);
        LinearLayout ll_attachment_title = (LinearLayout) dialog.findViewById(R.id.ll_attachment_title);
         ll_footer_view = (LinearLayout) dialog.findViewById(R.id.ll_footer_view);
        final WebView wv_template_content = (WebView) dialog.findViewById(R.id.wv_template_content);
        TextView tv_email_subject = (TextView) dialog.findViewById(R.id.tv_email_subject);
      //  TextView tv_email_attachments = (TextView) dialog.findViewById(R.id.tv_email_attachments);
        TextView tv_attachment_title = (TextView) dialog.findViewById(R.id.tv_attachment_title);
        TextView tv_view_template_title = (TextView) dialog.findViewById(R.id.tv_view_template_title);
        TextView tv_preview_title = (TextView) dialog.findViewById(R.id.tv_preview_title);
        final LinearLayout ll_view_template = (LinearLayout) dialog.findViewById(R.id.ll_view_template);
        // Button btn_view_template_back = (Button) dialog.findViewById(R.id.btn_view_template_back);
        Button btn_view_template_dissmiss = (Button) dialog.findViewById(R.id.btn_view_template_dissmiss);
        //WebView wbv_template = (WebView) dialog.findViewById(R.id.wbv_template);
        ll_view_attachments=dialog.findViewById(R.id.ll_view_attachments);



        getTemplateDetailsForViewAttachment(position,ll_attachment_title);

        btn_view_template_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.gravity = Gravity.CENTER;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin=50;
        params.topMargin=50;

        Log.d(Global.TAG, "openDialogViewTemplate: tv_view_template_title:" + campaignTemplates.get(position).getCampaignStepContent());

        if (campaignTemplates.get(position).getCampiagnEndStepEmailReminder().equals("1")){
            tv_template_content.setVisibility(View.VISIBLE);
            //ll_view_template.setLayoutParams(params);
            tv_template_content.setLayoutParams(params);
            tv_template_content.setText(Html.fromHtml(campaignTemplates.get(position).getCampaignStepContent()));
            tv_preview_title.setText("Reminder Preview");
        }else{
            tv_template_content.setVisibility(View.GONE);
            wv_template_content.setBackgroundColor(Color.TRANSPARENT);
            wv_template_content.loadDataWithBaseURL(null, campaignTemplates.get(position).getCampaignStepContent(), "text/html", "utf-8", null);
            tv_preview_title.setText("Template Preview");
        }

        tv_view_template_title.setText(campaignTemplates.get(position).getCampaignStepTitle());
        tv_email_subject.setText(campaignTemplates.get(position).getCampaignStepSubject());

        Picasso.with(context)
                .load(campaignTemplates.get(position).getStepImage())
                .placeholder(R.drawable.place)   // optional
                .error(R.drawable.error)      // optional
                .resize(400, 400)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        //ll_view_template.setBackground(new BitmapDrawable(bitmap));
                        if (campaignTemplates.get(position).getCampiagnEndStepEmailReminder().equals("1")){
                            ll_view_template.setBackground(new BitmapDrawable(bitmap));
                        }else{
                            wv_template_content.setBackground(new BitmapDrawable(bitmap));
                        }

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

}
