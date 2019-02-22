package com.success.successEntellus.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.fragment.CampaignStepsFragment;
import com.success.successEntellus.fragment.CustomCampaignFragment;
import com.success.successEntellus.lib.FilePath;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.AttachmentDetails;
import com.success.successEntellus.model.AttachmentResult;
import com.success.successEntellus.model.CampaignEmailTemplates;
import com.success.successEntellus.model.CampaignTemplate;
import com.success.successEntellus.model.EmailAttachment;
import com.success.successEntellus.model.ImageResponse;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTemplateActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =4 ;
    EditText edt_email_subject, edt_email_heading, edt_time_interval_template;
    Spinner sp_select_time_interval,sp_ec_repeate_every;
    Button btn_save_template,btn_template_cancel,btn_add_template_dissmiss,btn_attach_file_campaign;
    String[] intervalType={"Days","Week","Month"};
    public static List<String> deleteAttachmentIds=new ArrayList<>();
    SPLib spLib;
    private String selected_interval_type="";
    private String user_id;
    private String campaign_id,template_selection_id,campaignStepId="0";
    private String editEmailSubject,editEmailHeading,editEmailContents,editEmailInterval,editEmailIntervalType;
    boolean editFlag=false;
    RichEditor edt_email_contents;
    TextView mPreview,tv_from_saved_document,tv_add_template_title;
    Bitmap bmp;
    Uri picuri;
    String image_data;
    CustomCampaignFragment customCampaignFragment;
    private String attached_file_name;
    LinearLayout ll_campaign_attached_files;
    public static List<String> attached_files_names=new ArrayList<>();
    //public static List<String> attached_files_ids=new ArrayList<>();
    RadioButton rb_ec_immediately,rb_ec_schedule,rb_repeat_email_camp;
    LinearLayout ll_email_schedule,ll_repeat_email_camp;
    TextView tv_ec_check_days_error;
    EditText edt_ec_ends_after;
    String selectType,repeat_every_weeks="",repeat_on_days_string="",currDayName,repeat_occurences,repeatFlag;
    List<String> repeat_every_list=new ArrayList<>();
    int day,month,year;
    Calendar cal;
    CheckBox ch_ec_monday,ch_ec_tuesday,ch_ec_wednesday,ch_ec_thursday,ch_ec_friday,ch_ec_saturday,ch_ec_sunday;
    List<String> dayList=new ArrayList<>();
    RadioButton rb_footer_yes,rb_footer_no;
    LinearLayout ll_footer_view;
    CampaignStepsFragment campaignStepsFragment;
    String footerFlag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
        editorFunctions();
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            campaign_id=bundle.getString("campaign_id");
            template_selection_id=bundle.getString("template_id");
            if (bundle.containsKey("editFlag")){
                editFlag=bundle.getBoolean("editFlag");
            }
        }
        //getTemplateDetails();
        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        if (editFlag){
            btn_save_template.setText("Update");
            campaignStepId=bundle.getString("campaignStepId");
            editEmailSubject=bundle.getString("email_subject");
            editEmailHeading=bundle.getString("email_heading");
            editEmailContents=bundle.getString("email_contents");
            editEmailInterval=bundle.getString("email_interval");
            editEmailIntervalType=bundle.getString("email_interval_type");
            footerFlag=bundle.getString("footerFlag");
            Log.d(Global.TAG, "onCreate:footerFlag "+footerFlag);
           //Repeat

            repeatFlag=bundle.getString("email_repeat_flag");
            repeat_on_days_string=bundle.getString("email_repeat_days");
            repeat_occurences=bundle.getString("email_repeat_occurrence");
            repeat_every_weeks=bundle.getString("email_repeat_week");

            if (repeatFlag!=null){
                if (repeatFlag.equals("1")){
                    rb_repeat_email_camp.setChecked(true);
                    ll_repeat_email_camp.setVisibility(View.VISIBLE);
                    fillSpinnerwith12();

                    edt_ec_ends_after.setText(repeat_occurences);
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
                }else if (editEmailInterval.equals("0")){
                    rb_ec_immediately.setChecked(true);
                }else{
                    rb_ec_schedule.setChecked(true);
                    ll_email_schedule.setVisibility(View.VISIBLE);

                    if (editEmailIntervalType.equals("days")){
                        sp_select_time_interval.setSelection(1);
                    }else if (editEmailIntervalType.equals("week")){
                        sp_select_time_interval.setSelection(2);
                    }else if (editEmailIntervalType.equals("month")){
                        sp_select_time_interval.setSelection(3);
                    }
                }
            }

            edt_email_heading.setText(editEmailHeading);
            edt_email_contents.setHtml(editEmailContents);
            mPreview.setText(editEmailContents);
            edt_time_interval_template.setText(editEmailInterval);
            tv_add_template_title.setText("Edit Custom Template");
            Log.d(Global.TAG, "edt_email_subject: "+editEmailSubject);
            if (footerFlag.equals("1")){
                rb_footer_yes.setChecked(true);
            }else{
                rb_footer_no.setChecked(true);
            }

            if (editEmailSubject.equals("")){
                edt_email_subject.setText("Follow Up");
            }else{
                edt_email_subject.setText(editEmailSubject);
            }


            getTemplateDetailsForEditAttachment();
        }else{
            tv_add_template_title.setText("Add Custom Template");
        }

        String blank_content="Hi {firstName} <br><br><br><br><br><br><br><br><br><br><br>" +
                ""+spLib.getPref(SPLib.Key.USER_FIRST_NAME)+" "+spLib.getPref(SPLib.Key.USER_LAST_NAME)+"<br>"+
                ""+spLib.getPref(SPLib.Key.USER_MOBILE);


        if (!editFlag){
            edt_email_subject.setText("Follow Up");
            edt_email_contents.setHtml(blank_content);
            mPreview.setText(blank_content);
        }

        tv_from_saved_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddTemplateActivity.this,UploadFromSavedDocumentActivity.class);
               Bundle bundle=new Bundle();
               bundle.putBoolean("emailFlag",true);
               bundle.putString("campaign_id",campaign_id);
               bundle.putString("campaign_step_id",campaignStepId);
               intent.putExtras(bundle);
               startActivityForResult(intent,1001);
            }
        });
        btn_attach_file_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRead();
            }
        });

        btn_template_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_add_template_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_save_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editFlag){
                    if (MyValidator.isValidFieldE(edt_email_subject,"Enter Email Subject..!")){
                        edt_email_subject.clearFocus();
                        if (ll_email_schedule.getVisibility()==View.VISIBLE){
                            if (MyValidator.isValidFieldE(edt_time_interval_template,"Enter Time Interval..!")) {
                                if (!sp_select_time_interval.getSelectedItem().toString().contains("Select")){
                                    editEmailTemplate();
                                }else{
                                    MyValidator.setSpinnerErrorE(sp_select_time_interval,"Select Interval Type..!",AddTemplateActivity.this);
                                }
                            }else{
                                edt_time_interval_template.setFocusable(true);
                                edt_time_interval_template.requestFocus();
                            }
                        }else if (ll_repeat_email_camp.getVisibility()==View.VISIBLE){
                            if (!edt_ec_ends_after.getText().toString().equals("") ){
                                if (!ch_ec_monday.isChecked() && !ch_ec_tuesday.isChecked() && !ch_ec_wednesday.isChecked() && !ch_ec_thursday.isChecked() && !ch_ec_friday.isChecked()
                                        && !ch_ec_saturday.isChecked() && !ch_ec_sunday.isChecked()) {
                                    tv_ec_check_days_error.setVisibility(View.VISIBLE);
                                }else{
                                    tv_ec_check_days_error.setVisibility(View.GONE);
                                    editEmailTemplate();
                                }
                            }else{
                                edt_ec_ends_after.setError("Enter Occurences");
                            }
                        }else{
                            editEmailTemplate();
                        }

                    }

                }else{
                    if (MyValidator.isValidFieldE(edt_email_subject,"Enter Email Subject..!")){
                        edt_email_subject.clearFocus();

                        if (ll_email_schedule.getVisibility()==View.VISIBLE){
                            if (MyValidator.isValidFieldE(edt_time_interval_template,"Enter Time Interval..!")) {
                                if (!sp_select_time_interval.getSelectedItem().toString().contains("Select")) {
                                    saveEmailTemplate();
                                } else {
                                    MyValidator.setSpinnerError(sp_select_time_interval, "Select Type..!");
                                }
                            }
                        }else if (ll_repeat_email_camp.getVisibility()==View.VISIBLE){
                            if (!edt_ec_ends_after.getText().toString().equals("") ){
                                if (!ch_ec_monday.isChecked() && !ch_ec_tuesday.isChecked() && !ch_ec_wednesday.isChecked() && !ch_ec_thursday.isChecked() && !ch_ec_friday.isChecked()
                                        && !ch_ec_saturday.isChecked() && !ch_ec_sunday.isChecked()) {
                                    tv_ec_check_days_error.setVisibility(View.VISIBLE);
                                }else{
                                    tv_ec_check_days_error.setVisibility(View.GONE);
                                    saveEmailTemplate();
                                }
                            }else{
                                edt_ec_ends_after.setError("Enter Occurences");
                            }
                        }else{
                            saveEmailTemplate();
                        }


                    }
                }
            }
        });

        sp_select_time_interval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_select_time_interval.getSelectedItem().toString().contains("Select")) {
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

        sp_ec_repeate_every.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_ec_repeate_every.getSelectedItem().toString().contains("Select")) {
                    repeat_every_weeks = "";
                } else {
                    repeat_every_weeks = repeat_every_list.get(position);
                    Log.d(Global.TAG, "onItemSelected: repeat_every_weeks:"+repeat_every_weeks);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        rb_ec_immediately.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_email_schedule.setVisibility(View.GONE);
                    ll_repeat_email_camp.setVisibility(View.GONE);
                }
            }
        });

        rb_ec_schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_email_schedule.setVisibility(View.VISIBLE);
                    ll_repeat_email_camp.setVisibility(View.GONE);
                }else{
                    ll_email_schedule.setVisibility(View.GONE);
                }
            }
        });

        rb_repeat_email_camp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_email_schedule.setVisibility(View.GONE);
                    ll_repeat_email_camp.setVisibility(View.VISIBLE);
                    fillSpinnerwith12();
                    if (!editFlag){
                        selectCurrentDay();
                    }

                }else{
                    ll_repeat_email_camp.setVisibility(View.GONE);
                }
            }
        });

        rb_footer_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    campaignStepsFragment.campaignStepFooterFlag ="1";
                }else{
                    campaignStepsFragment.campaignStepFooterFlag ="0";
                }
            }
        });

        rb_footer_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    campaignStepsFragment.campaignStepFooterFlag ="0";
                }else{
                    campaignStepsFragment.campaignStepFooterFlag ="1";
                }
            }
        });

    }
    private void selectWeekDays(String name) {
        if (!name.equals("")){
            if (name.equalsIgnoreCase("mon")){
                ch_ec_monday.setChecked(true);
            }else if (name.equalsIgnoreCase("tue")){
                ch_ec_tuesday.setChecked(true);
            }else if (name.equalsIgnoreCase("wed")){
                ch_ec_wednesday.setChecked(true);
            }else if (name.equalsIgnoreCase("thu")){
                ch_ec_thursday.setChecked(true);
            }else if (name.equalsIgnoreCase("fri")){
                ch_ec_friday.setChecked(true);
            }else if (name.equalsIgnoreCase("sat")){
                ch_ec_saturday.setChecked(true);
            }else if (name.equalsIgnoreCase("sun")){
                ch_ec_sunday.setChecked(true);
            }
        }
    }

    private void getDaysSelection() {

        dayList.clear();

        if (ch_ec_monday.isChecked()){
            dayList.add("Mon");
        }
        if (ch_ec_tuesday.isChecked()){
            dayList.add("Tue");
        }
        if (ch_ec_wednesday.isChecked()){
            dayList.add("Wed");
        }
        if (ch_ec_thursday.isChecked()){
            dayList.add("Thu");
        }
        if (ch_ec_friday.isChecked()){
            dayList.add("Fri");
        }
        if (ch_ec_saturday.isChecked()){
            dayList.add("Sat");
        }
        if (ch_ec_sunday.isChecked()){
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


    /*private void displayAttachment(String fileName) {
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View attachmentView = inflater.inflate(R.layout.uploaded_attachment_layout, null);
        TextView tv_attachment_name = (TextView) attachmentView.findViewById(R.id.tv_attachment_name);
        ImageButton ib_cancel_attachment=(ImageButton) attachmentView.findViewById(R.id.ib_cancel_attachment);

        tv_attachment_name.setText(fileName);

        ib_cancel_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index=getViewIndex(ll_campaign_attached_files,attachmentView);
                Log.d(Global.TAG, " Index in View: "+index);
                deleteAttachment(index,attachmentView);
                // ll_attached_files.removeView(attachmentView);

            }
        });
        ll_campaign_attached_files.addView(attachmentView);
    }*/

    private void getTemplateDetailsForEditAttachment() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("stepId",campaignStepId);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getTemplateDetailsOnId: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getTemplateDetailsOnId: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(AddTemplateActivity.this);
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

                        if (attachMentList.size()>0) {
                            for (int i = 0; i < attachMentList.size(); i++) {
//                                String file_path = attachMentList.get(i).getFilePath();
//                                String file_name = file_path.substring(file_path.lastIndexOf("/") + 1, file_path.length());
                                Log.d(Global.TAG, "onResponse: " + attachMentList.get(i).getFileName());
                                deleteAttachmentIds.add(attachMentList.get(i).getAttachmentId());
                                displayAttachment(attachMentList.get(i).getFileName());
                            }
                        }
                        /*String attached_names="";
                        if (attachMentList.size()>0){
                            for (int i=0;i<attachMentList.size();i++){
                                String file_path=attachMentList.get(i).getFilePath();
                                String file_name=file_path.substring(file_path.lastIndexOf("/")+1,file_path.length());
                                Log.d(Global.TAG, "onResponse: "+file_name);
                                if (attached_names.equals("")){
                                    attached_names=attached_names+file_name;
                                }else{
                                    attached_names=attached_names+"\n"+file_name;
                                }

                            }
                            tv_attached_files.setText("" + attached_names);
                        }

                        linkList=messageDetails.get(0).getLinks();
                        Log.d(Global.TAG, "linkList size: "+linkList.size());

                        String link_names="";
                        if (linkList.size()>0){
                            for (int i=0;i<linkList.size();i++){

                                if (link_names.equals("")){
                                    link_names=link_names+linkList.get(i).toString();
                                }else{
                                    link_names=link_names+"\n"+linkList.get(i).toString();
                                }

                            }
                            tv_all_links.setText("" + link_names);
                        }*/
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getDetails:"+t);
            }
        });

    }

    public void requestRead() {
        if (ContextCompat.checkSelfPermission(AddTemplateActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AddTemplateActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            openDocuments();
            Log.d(Global.TAG, " Permission already granted: ");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(Global.TAG, "onRequestPermissionsResult: ");
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(Global.TAG, "onRequestPermissionsResult: Granted ");
                openDocuments();
            } else {
                // Permission Denied
                Toast.makeText(AddTemplateActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openDocuments() {
        Log.d(Global.TAG, "Select File: ");
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, 4);
    }

    private void editorFunctions() {
        edt_email_contents = (RichEditor) findViewById(R.id.edt_email_contents);
        edt_email_contents.setEditorHeight(200);
        edt_email_contents.setEditorFontSize(14);
        //edt_email_contents.startNestedScroll(R.layout.edt_email_contents);
        //edt_email_contents.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        edt_email_contents.setPadding(10, 20, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        edt_email_contents.setPlaceholder("Email Contents..");
        //edt_email_contents.setInputEnabled(false);

        mPreview = (TextView) findViewById(R.id.preview);

        edt_email_contents.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setUnderline();
            }
        });


        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                edt_email_contents.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                edt_email_contents.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setAlignRight();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.setNumbers();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
//                edt_email_contents.insertImage(" https://successentellus.com/check/assets/uploads/ckEditorImage/841/2-watercolor-painting-by-vilas_kulkarni.jpg",
//                        "ckEditorImage");
                selectImage(AddTemplateActivity.this);
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                openDialogInsertLink();

            }
        });
        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edt_email_contents.insertTodo();

            }
        });
    }

    private void openDialogInsertLink() {

        final Dialog dialog1 = new Dialog(AddTemplateActivity.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setCancelable(true);
        dialog1.setContentView(R.layout.add_link_email_campaign);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog1.setTitle("Confirmation");

        final EditText edt_add_link_email=dialog1.findViewById(R.id.edt_add_link_email);
        Button btn_add_link=dialog1.findViewById(R.id.btn_add_link);


        btn_add_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edt_add_link_email.getText().toString().equals("")){
                    if (MyValidator.isValidUrl(edt_add_link_email.getText().toString())){
                        edt_email_contents.insertLink(edt_add_link_email.getText().toString(), edt_add_link_email.getText().toString());
                        dialog1.dismiss();
                    }else{
                        Toast.makeText(AddTemplateActivity.this, "Please Enter Valid Link..!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AddTemplateActivity.this, "Enter Link to Add..!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }


    private void editEmailTemplate() {
        if (ll_email_schedule.getVisibility()==View.VISIBLE){
            selectType="2";
            repeat_every_weeks="";
        }else if (ll_repeat_email_camp.getVisibility()==View.VISIBLE){
            selectType="3";
            getDaysSelection();
        }else{
            selectType="1";
            repeat_every_weeks="";
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignStepCamId",campaign_id);
            paramObj.put("campaignStepId",campaignStepId);
            paramObj.put("campaignStepFooterFlag",campaignStepsFragment.campaignStepFooterFlag );
            paramObj.put("campaignStepSubject",edt_email_subject.getText().toString());
            paramObj.put("campaignStepTitle",edt_email_heading.getText().toString());
            paramObj.put("campaignStepContent",mPreview.getText().toString());
            paramObj.put("campaignStepSendInterval",edt_time_interval_template.getText().toString());
            paramObj.put("campaignStepSendIntervalType",selected_interval_type.toLowerCase());
            paramObj.put("selectType",selectType);
            paramObj.put("repeat_every_weeks",repeat_every_weeks);
            paramObj.put("repeat_on",repeat_on_days_string);
            paramObj.put("repeat_ends_after",edt_ec_ends_after.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "editEmailTemplate: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "editEmailTemplate: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(AddTemplateActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.edit_email_template(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult!=null){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(AddTemplateActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                        finish();
                        //customCampaignFragment.getAllCustomCampaign();
                    }else{
                        Toast.makeText(AddTemplateActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
                finish();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(AddTemplateActivity.this, " Error in Updating Template..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure:editEmailTemplate "+t);
                finish();
            }
        });
    }

    private void selectImage(Context context) {
        //this.iv_category_image = iv_category_image;
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Please Select ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    try {
                        Log.d("mytag", "Take Photo");
                        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
                        File file = new File(imageFilePath);
                        picuri = Uri.fromFile(file);
                        CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picuri);
                        startActivityForResult(CameraIntent, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Log.d("mytag", "Choose from Gallery");
                    Intent GalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(GalleryIntent, 2);

                } else if (options[item].equals("Cancel")) {
                    Log.d("mytag", "Cancel");
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = picuri;
            File image_file=new File(picuri.getPath());
            Log.d("picUri", uri.toString());
            //insertImage(image_file);
            //performCrop();

        } else if (resultCode == RESULT_OK && requestCode == 2) {
            picuri = data.getData();
            String Path=getRealPathFromURI(AddTemplateActivity.this,picuri);
//            String filename=Path.substring(Path.lastIndexOf("/")+1);
//            Log.d(Global.TAG, "Path filename : "+filename);

            File image_file=new File(Path);
            Log.d(Global.TAG, "Image File: "+image_file);
            Log.d("mytag", picuri.toString());
            insertImage(image_file);
            // performCrop();
        } else if (requestCode == 3) {
            Bundle extras = data.getExtras();
            bmp = (Bitmap) extras.get("data");
            Log.d("mytag", "performCrop");
            //
            if (bmp != null) {
                Log.d(Global.TAG, "Bitmat Image set to Imageview: ");
                // edt_email_contents.insertImage(bmp);
                //iv_category_image.setImageBitmap(bmp);
                image_data = ImagrDecode(bmp);
                //File image_file= bmp.compress(Bitmap.CompressFormat.PNG, 100, image);
                Log.d(Global.TAG, "Image Data: "+image_data);
                //insertImage(image_file);

            }
        }else if (resultCode== RESULT_OK && requestCode==4){
            Uri uri = data.getData();
            String selectedFilePath = FilePath.getPath(AddTemplateActivity.this, uri);
            Log.d(Global.TAG, "onActivityResult: selectedFilePath:"+selectedFilePath);
            if (selectedFilePath!=null){
                final File file = new File(selectedFilePath);
                Log.d(Global.TAG, "onActivityResult: File:" + file);
                String filename=getFileName(file);
                attached_file_name=filename;
                if (file != null) {
                    if (MyValidator.isValidFileSize(file)){
                        uploadEmailAttachment(file);
                    }else {
                        Toast.makeText(AddTemplateActivity.this, "File Size must be less than 5Mb", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }else if (resultCode== RESULT_OK && requestCode==1001)
            Log.d(Global.TAG, "onActivityResult: Display Files:  "+attached_files_names.size());
            Log.d(Global.TAG, "onActivityResult: Display Ids:  "+deleteAttachmentIds.size());
            if (attached_files_names.size()>0){
                for(int i=0;i<attached_files_names.size();i++){
                    displayAttachment(attached_files_names.get(i).toString());
                   // attached_files_names.remove(i);
                    Log.d(Global.TAG, "attached_files_names: "+attached_files_names.size());
                }
                attached_files_names.clear();
            }
        }

    private void uploadEmailAttachment(File file) {
        Log.d(Global.TAG, "uploadEmailAttachment: "+file);
        RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody platform = RequestBody.create(MediaType.parse("text/plain"), "2");
        //RequestBody txtCampAttachId = RequestBody.create(MediaType.parse("text/plain"), "0");
        RequestBody campaignId = RequestBody.create(MediaType.parse("text/plain"), campaign_id);
        RequestBody step_id = RequestBody.create(MediaType.parse("text/plain"), campaignStepId);

        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Uploading...");
        dialog.setMessage("Uploading... Please Wait..");
        dialog.show();

        Log.d(Global.TAG, "Step Id:: "+campaignStepId);
        Log.d(Global.TAG, "userId:: "+userId);
        Log.d(Global.TAG, "platform:: "+platform);
        Log.d(Global.TAG, "File: "+body);
        Log.d(Global.TAG, "campaignId: "+campaignId);
        Log.d(Global.TAG, "campaignStepId: "+step_id);

        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<AttachmentResult> call = service.uploadEmailAttachment(userId,platform,body,campaignId,step_id);
        call.enqueue(new Callback<AttachmentResult>() {
            @Override
            public void onResponse(Call<AttachmentResult> call, Response<AttachmentResult> response) {
                AttachmentResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(AddTemplateActivity.this, "File Uploaded Successfully.!", Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "File Uploaded Successfully..! ");
                    AttachmentDetails attachmentDetails=jsonResult.getResult();
                    deleteAttachmentIds.add(attachmentDetails.getAttachmentId());

                    displayAttachment(attached_file_name);
                }else{
                    Log.d(Global.TAG, " Error in File Upload..! ");
                    Toast.makeText(AddTemplateActivity.this, " Error in File Upload..! ", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AttachmentResult> call, Throwable t) {
                dialog.dismiss();
                Log.d(Global.TAG, "onFailure: Upload "+t);
                Toast.makeText(AddTemplateActivity.this, "Error in Uploading File!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteAttachment(final int index, final View attachmentView) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("attachmentId", deleteAttachmentIds.get(index).toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteAttachment: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteAttachment: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(AddTemplateActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.delete_email_attachment(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(AddTemplateActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, ""+jsonResult.getResult());
                    ll_campaign_attached_files.removeView(attachmentView);
                    deleteAttachmentIds.remove(index);
                }else{
                    Toast.makeText(AddTemplateActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, ""+jsonResult.getResult());
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: deleteAttachment:"+t);
            }
        });

    }
    private void displayAttachment(String file_name) {
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View attachmentView = inflater.inflate(R.layout.uploaded_attachment_layout, null);
        TextView tv_attachment_name = (TextView) attachmentView.findViewById(R.id.tv_attachment_name);
        ImageButton ib_cancel_attachment=(ImageButton) attachmentView.findViewById(R.id.ib_cancel_attachment);
        tv_attachment_name.setText(file_name);

        ib_cancel_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index=getViewIndex(ll_campaign_attached_files,attachmentView);
                Log.d(Global.TAG, " Index in View: "+index);
                deleteAttachment(index,attachmentView);
                //ll_campaign_attached_files.removeView(attachmentView);

            }
        });
        ll_campaign_attached_files.addView(attachmentView);
    }
    private int getViewIndex (ViewGroup viewGroup, View view)
    {
        return viewGroup.indexOfChild(view);
    }
    private String getFileName(File file) {
        String path=file.getPath();
        Log.d(Global.TAG, "getFileName:Path: "+file.getPath());
        String filename = path.substring(path.lastIndexOf("/")+1);

        Log.d(Global.TAG, "Real Path: " + path);
        Log.d(Global.TAG, "Filename With Extension: " + filename);
        return  filename;
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private void insertImage(File image_file) {

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), image_file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("editorImage", image_file.getName(), reqFile);

        //Log.d(Global.TAG, "insertImage: "+image_file);
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Uploading...");
        dialog.setMessage("Uploading... Please Wait..");
        dialog.show();

//        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image_data);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("editorImage", "image.jpg", requestFile);

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody platform = RequestBody.create(MediaType.parse("text/plain"), "2");

        Log.d(Global.TAG, "insertImage: "+userId+" platform:"+platform+" body:"+body);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<ImageResponse> call = service.attach_image( userId,platform,body);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                ImageResponse imageResponse=response.body();
                if (imageResponse.isSuccess()){
                    Toast.makeText(AddTemplateActivity.this, "Image Uploaded SuccessFully..!", Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "Image Uploaded..! "+imageResponse.getResult());
                    edt_email_contents.insertImage(imageResponse.getResult(),"dachshund");
                }else{
                    Log.d(Global.TAG, " Error in Image Upload..! ");
                    Toast.makeText(AddTemplateActivity.this, "Error..!"+imageResponse.getResult(), Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                dialog.dismiss();
                Log.d(Global.TAG, "onFailure: Upload "+t);
                Toast.makeText(AddTemplateActivity.this, "Error in Uploading Image!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String ImagrDecode(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        return Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
    }

    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picuri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, 3);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(AddTemplateActivity.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private void fillSpinnerwith12() {
        repeat_every_list.clear();
        for (int i=1;i<=13;i++){
            repeat_every_list.add(String.valueOf(i));
        }
        applySpinner1(repeat_every_list,sp_ec_repeate_every,"-Select-");

        if (editFlag){
            if (repeat_every_weeks!=null){
                sp_ec_repeate_every.setSelection(Integer.parseInt(repeat_every_weeks)-1);
            }
        }else{
            sp_ec_repeate_every.setSelection(0);
        }
    }
    private void selectCurrentDay() {
        Date curDate = null;
        try {
            curDate = new SimpleDateFormat("MM-dd-yyyy").parse((month+1)+"-"+day+"-"+year);
            Log.d(Global.TAG, "Current Date: "+curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currDayName = new SimpleDateFormat("EEE").format(curDate);
        Log.d(Global.TAG, "Current day name: "+currDayName+" Input Date:"+(month+1)+"-"+day+"-"+year);

        if (!currDayName.equals("")){
            if (currDayName.equalsIgnoreCase("mon")){
                ch_ec_monday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("tue")){
                ch_ec_tuesday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("wed")){
                ch_ec_wednesday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("thu")){
                ch_ec_thursday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("fri")){
                ch_ec_friday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("sat")){
                ch_ec_saturday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("sun")){
                ch_ec_sunday.setChecked(true);
            }
        }

    }
    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(AddTemplateActivity.this, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }

    private void applySpinner1(final List<String> taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(AddTemplateActivity.this, android.R.layout.simple_list_item_1);
        // adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        //adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }

    private void saveEmailTemplate() {

        if (ll_email_schedule.getVisibility()==View.VISIBLE){
            selectType="2";
            repeat_every_weeks="";
        }else if (ll_repeat_email_camp.getVisibility()==View.VISIBLE){
            selectType="3";
            getDaysSelection();
        }else{
            selectType="1";
            repeat_every_weeks="";
        }


        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignStepCamId",campaign_id);
            paramObj.put("campaignStepFooterFlag",campaignStepsFragment.campaignStepFooterFlag );
            paramObj.put("campaignStepSubject",edt_email_subject.getText().toString());
            paramObj.put("campaignStepTitle",edt_email_heading.getText().toString());
            paramObj.put("campaignStepContent",mPreview.getText().toString());
            paramObj.put("campaignStepSendInterval",edt_time_interval_template.getText().toString());
            paramObj.put("campaignStepSendIntervalType",selected_interval_type.toLowerCase());
            paramObj.put("campaignTemplateId",template_selection_id);
            paramObj.put("selectType",selectType);
            paramObj.put("repeat_every_weeks",repeat_every_weeks);
            paramObj.put("repeat_on",repeat_on_days_string);
            paramObj.put("repeat_ends_after",edt_ec_ends_after.getText().toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "saveEmailTemplate: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "saveEmailTemplate: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(AddTemplateActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.add_email_template(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult!=null){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(AddTemplateActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                        //customCampaignFragment.getAllCustomCampaign();
                    }else{
                        Toast.makeText(AddTemplateActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }

                myLoader.dismiss();
                finish();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(AddTemplateActivity.this, " Error Template..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure:saveEmailTemplate "+t);
                finish();
            }
        });

    }

    private void init() {
        campaignStepsFragment=new CampaignStepsFragment();
        edt_email_subject=(EditText)findViewById(R.id.edt_email_subject);
        edt_email_heading=(EditText)findViewById(R.id.edt_email_heading);
        //edt_email_contents=(EditText)findViewById(R.id.edt_email_contents);
        edt_time_interval_template=(EditText)findViewById(R.id.edt_time_interval_template);
        tv_from_saved_document=(TextView) findViewById(R.id.tv_from_saved_document);
        tv_add_template_title=(TextView) findViewById(R.id.tv_add_template_title);

        sp_select_time_interval=(Spinner)findViewById(R.id.sp_select_time_interval);
        sp_ec_repeate_every=(Spinner)findViewById(R.id.sp_ec_repeate_every);
        btn_save_template=(Button)findViewById(R.id.btn_save_template);
        btn_template_cancel=(Button)findViewById(R.id.btn_template_cancel);
        btn_attach_file_campaign=(Button)findViewById(R.id.btn_attach_file_campaign);
        btn_add_template_dissmiss=(Button)findViewById(R.id.btn_add_template_dissmiss);

        ch_ec_monday=(CheckBox) findViewById(R.id.ch_ec_monday);
        ch_ec_tuesday=(CheckBox) findViewById(R.id.ch_ec_tuesday);
        ch_ec_wednesday=(CheckBox) findViewById(R.id.ch_ec_wednesday);
        ch_ec_thursday=(CheckBox) findViewById(R.id.ch_ec_thursday);
        ch_ec_friday=(CheckBox) findViewById(R.id.ch_ec_friday);
        ch_ec_saturday=(CheckBox) findViewById(R.id.ch_ec_saturday);
        ch_ec_sunday=(CheckBox) findViewById(R.id.ch_ec_sunday);

        rb_ec_immediately=(RadioButton) findViewById(R.id.rb_ec_immediately);
        rb_ec_schedule=(RadioButton) findViewById(R.id.rb_ec_schedule);
        rb_repeat_email_camp=(RadioButton) findViewById(R.id.rb_repeat_email_camp);
        rb_footer_no=(RadioButton) findViewById(R.id.rb_footer_no);
        rb_footer_yes=(RadioButton) findViewById(R.id.rb_footer_yes);

        ll_email_schedule=(LinearLayout) findViewById(R.id.ll_email_schedule);
        ll_repeat_email_camp=(LinearLayout) findViewById(R.id.ll_repeat_email_camp);
        tv_ec_check_days_error=(TextView) findViewById(R.id.tv_ec_check_days_error);
        edt_ec_ends_after=(EditText) findViewById(R.id.edt_ec_ends_after);

        spLib=new SPLib(this);
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        customCampaignFragment=new CustomCampaignFragment();
        applySpinner(intervalType,sp_select_time_interval,"Select");
        ll_campaign_attached_files=(LinearLayout)findViewById(R.id.ll_campaign_attached_files);
        deleteAttachmentIds=new ArrayList<>();


    }
}
