package com.success.successEntellus.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.FilePath;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.CheckTxtCampHasMember;
import com.success.successEntellus.model.ImageResponse;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.UploadRes;
import com.success.successEntellus.model.UploadResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.tooltip.SimpleTooltip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class CreateNewTextMessageActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =3 ;
    Button btn_text_dissmiss, btn_attach_file, btn_save_text_message, btn_cancel_text_message;
    EditText edt_text_message_name, edt_text_message, edt_time_interval_text_message,edt_add_link,edt_ends_after;
    Spinner sp_select_message_time_interval,sp_tc_repeate_every;
    CheckBox ch_tc_monday,ch_tc_tuesday,ch_tc_wednesday,ch_tc_thursday,ch_tc_friday,ch_tc_saturday,ch_tc_sunday;
    LinearLayout ll_attached_files,ll_add_link;
    String[] intervalType = {"Hours", "Days", "Week"};
    private String text_message_interval_type;
    SPLib spLib;
    private String user_id, txt_camp_id;
    private String attached_file_name;
    TextView tv_add_link;
    JSONArray linkArray;
    public static List<String> deleteIds=new ArrayList<>();
    public static List<String> attached_file_names=new ArrayList<>();
    TextView tv_text_from_saved_document,tv_check_days_error;
    List<EditText> allEds = new ArrayList<EditText>();
    RadioGroup rbg_interval;
    RadioButton rb_immediately,rb_schedule,rb_repeat_text_camp;
    LinearLayout ll_scheduled_interval,ll_repeat_text_camp,ll_link_layout;
    List<String> repeat_every=new ArrayList<>();
    private String currDayName;
    int month,days,year;
    private List<String> dayList=new ArrayList<>();
    private String dayString,repeat_every_string;
    Calendar cal;
    RadioButton rb_footer_yes,rb_footer_no,rb_signature_yes,rb_signature_no;
    String txtTemplateFooterFlag="1";
    private int memberAssignedFlag=0;
    public String txtTemplateAddSignature="0";

    LinearLayout ll_formated_text,ll_plain_text,ll_attchments_layout_text;
    RadioButton rb_plain_text,rb_formatted_text;

    RichEditor editor_text_msg_contents;
    TextView mPreview;
    String txtTemplateType="1";
    private Uri picuri;
    ImageButton ib_custom_help;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_text_campaign_message_dialog);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //setTitle("Add New Text Message");
        init();
        editorFunctions();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            txt_camp_id = bundle.getString("txtCampId");
        }
        checkMemberAssignedOrNot();
        applySpinner(intervalType, sp_select_message_time_interval, "--Select--");

        sp_select_message_time_interval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_select_message_time_interval.getSelectedItem().toString().contains("Select")) {
                    text_message_interval_type = "";
                } else {
                    text_message_interval_type = intervalType[position - 1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp_tc_repeate_every.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_tc_repeate_every.getSelectedItem().toString().contains("Select")) {
                    repeat_every_string = "";
                } else {
                    if (repeat_every.size()>0){
                        repeat_every_string = repeat_every.get(position).toString();
                        Log.d(Global.TAG, "onItemSelected: repeat_every_string:"+repeat_every_string);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        rb_immediately.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    memberAssignedFlag=checkMemberAssignedOrNot();
                    ll_scheduled_interval.setVisibility(View.GONE);
                    ll_repeat_text_camp.setVisibility(View.GONE);
                }else{
                    //ll_scheduled_interval.setVisibility(View.VISIBLE);
                }
            }
        });

        rb_schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_scheduled_interval.setVisibility(View.VISIBLE);
                }else{
                    ll_scheduled_interval.setVisibility(View.GONE);
                }
            }
        });

        rb_repeat_text_camp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                   // ll_scheduled_interval.setVisibility(View.GONE);
                    ll_repeat_text_camp.setVisibility(View.VISIBLE);
                    fillSpinnerwith12();
                    selectCurrentDay();
                }else{
                    ll_repeat_text_camp.setVisibility(View.GONE);
                }
            }
        });

        btn_cancel_text_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // ll_attached_files.removeAllViews();
                finish();
            }
        });

        tv_text_from_saved_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CreateNewTextMessageActivity.this,UploadFromSavedDocumentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("template_id","0");
                bundle.putBoolean("textFlag",true);
                intent.putExtras(bundle);
                startActivityForResult(intent,1010);
            }
        });

        btn_save_text_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean linkFlag=false;
                String linktoAdd=edt_add_link.getText().toString();
                linkArray=new JSONArray();
                if (MyValidator.isValidUrl(linktoAdd)){
                    if (!linktoAdd.equals("")) {
                        JSONObject link = new JSONObject();
                        try {
                            link.put("link", linktoAdd);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(Global.TAG, "add Link: "+link);
                        linkArray.put(link);
                    }

                    //linkFlag=true;
                    Log.d(Global.TAG, "Link Array: "+linkArray);
                }else{
                    //Toast.makeText(CreateNewTextMessageActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    //linkFlag=false;
                    edt_add_link.setError("Enter Valid URL");
                    edt_add_link.setFocusable(true);
                    edt_add_link.requestFocus();
                }

                List<String> linkFlagList=new ArrayList<>();
                linkFlagList.clear();
                for( int i=0;i<allEds.size();i++){
                    String linktoAdd1=allEds.get(i).getText().toString();
                    if (MyValidator.isValidUrl(linktoAdd1)){
                        if (!linktoAdd.equals("")) {
                            JSONObject link = new JSONObject();
                            try {
                                if (!linktoAdd1.equals("")) {
                                    link.put("link", linktoAdd1);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d(Global.TAG, "add Link: " + link);
                            linkArray.put(link);
                            linkFlagList.add("1");
                        }


                    }else{
                        linkFlagList.add("0");
                        allEds.get(i).setError("Enter valid URL..!");
                    }


                }
                if (linkFlagList.contains("0")){
                    linkFlag=false;
                }else{
                    linkFlag=true;
                }
                Log.d(Global.TAG, " Final Link Array: "+linkArray);


                if (MyValidator.isValidFieldE(edt_text_message_name, "Enter Message Title")) {
                    edt_text_message_name.clearFocus();

                    if (txtTemplateType.equals("1")) {

                        if (MyValidator.isValidFieldE(edt_text_message, "Enter Text Message Content")) {
                            if (edt_add_link.getError() == null) {

                                if (allEds.size() == 0 || allEds.size() > 0 && linkFlag) {

                                    if (ll_scheduled_interval.getVisibility() == View.VISIBLE) {
                                        if (MyValidator.isValidFieldE(edt_time_interval_text_message, "Enter Time Interval")) {
                                            if (!sp_select_message_time_interval.getSelectedItem().toString().contains("Select")) {
                                                addTextMessage();
                                            } else {
                                                MyValidator.setSpinnerError(sp_select_message_time_interval, "Select");
                                            }
                                        }
                                    } else {
                                        if (ll_repeat_text_camp.getVisibility() == View.VISIBLE) {
                                            if (!edt_ends_after.getText().toString().equals("")) {
                                                if (!ch_tc_monday.isChecked() && !ch_tc_tuesday.isChecked() && !ch_tc_wednesday.isChecked() && !ch_tc_thursday.isChecked() && !ch_tc_friday.isChecked()
                                                        && !ch_tc_saturday.isChecked() && !ch_tc_sunday.isChecked()) {
                                                    tv_check_days_error.setVisibility(View.VISIBLE);
                                                } else {
                                                    tv_check_days_error.setVisibility(View.GONE);
                                                    addTextMessage();
                                                }
                                            } else {
                                                edt_ends_after.setError("Enter Occurences");
                                            }
                                        } else {
                                            if (memberAssignedFlag == 1) {//if member assigned
                                                new AlertDialog.Builder(CreateNewTextMessageActivity.this)
                                                        .setMessage("This text message will send immediately to assigned members. Do you want continue..?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                addTextMessage();
                                                            }
                                                        })
                                                        .setNegativeButton("No", null)
                                                        .show();
                                            } else {
                                                addTextMessage();
                                            }

                                        }

                                    }
                                } else {
                                    if (allEds.size() > 0) {
                                        for (int i = 0; i < allEds.size(); i++) {
                                            if (allEds.get(i).getError() == null) {

                                            } else {
                                                allEds.get(i).setFocusable(true);
                                                allEds.get(i).requestFocus();
                                            }
                                        }
                                    }
                                }
                            } else {
                                edt_add_link.setFocusable(true);
                                edt_add_link.requestFocus();
                            }

                        }
                    }else if (txtTemplateType.equals("2")){

                        if (!editor_text_msg_contents.getHtml().equals("")) {

                            if (edt_add_link.getError() == null) {

                                if (allEds.size() == 0 || allEds.size() > 0 && linkFlag) {

                                    if (ll_scheduled_interval.getVisibility() == View.VISIBLE) {
                                        if (MyValidator.isValidFieldE(edt_time_interval_text_message, "Enter Time Interval")) {
                                            if (!sp_select_message_time_interval.getSelectedItem().toString().contains("Select")) {
                                                addTextMessage();
                                            } else {
                                                MyValidator.setSpinnerError(sp_select_message_time_interval, "Select");
                                            }
                                        }
                                    } else {
                                        if (ll_repeat_text_camp.getVisibility() == View.VISIBLE) {
                                            if (!edt_ends_after.getText().toString().equals("")) {
                                                if (!ch_tc_monday.isChecked() && !ch_tc_tuesday.isChecked() && !ch_tc_wednesday.isChecked() && !ch_tc_thursday.isChecked() && !ch_tc_friday.isChecked()
                                                        && !ch_tc_saturday.isChecked() && !ch_tc_sunday.isChecked()) {
                                                    tv_check_days_error.setVisibility(View.VISIBLE);
                                                } else {
                                                    tv_check_days_error.setVisibility(View.GONE);
                                                    addTextMessage();
                                                }
                                            } else {
                                                edt_ends_after.setError("Enter Occurences");
                                            }
                                        } else {
                                            if (memberAssignedFlag == 1) {//if member assigned
                                                new AlertDialog.Builder(CreateNewTextMessageActivity.this)
                                                        .setMessage("This text message will send immediately to assigned members. Do you want continue..?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                addTextMessage();
                                                            }
                                                        })
                                                        .setNegativeButton("No", null)
                                                        .show();
                                            } else {
                                                addTextMessage();
                                            }

                                        }

                                    }
                                } else {
                                    if (allEds.size() > 0) {
                                        for (int i = 0; i < allEds.size(); i++) {
                                            if (allEds.get(i).getError() == null) {

                                            } else {
                                                allEds.get(i).setFocusable(true);
                                                allEds.get(i).requestFocus();
                                            }
                                        }
                                    }
                                }
                            }else {
                                edt_add_link.setFocusable(true);
                                edt_add_link.requestFocus();
                            }
                        }else{
                            Toast.makeText(CreateNewTextMessageActivity.this, "Please Enter Text Message..!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        btn_attach_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRead();
            }
        });
        btn_text_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // ll_attached_files.removeAllViews();
                finish();
            }
        });


        tv_add_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLinkView();
            }
        });

        rb_footer_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    txtTemplateFooterFlag="1";
                }else{
                    txtTemplateFooterFlag="0";
                }
            }
        });

        rb_footer_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    txtTemplateFooterFlag="0";
                }else{
                    txtTemplateFooterFlag="1";
                }
            }
        });

        rb_signature_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
               if (checked){
                   txtTemplateAddSignature="1";
               }else{
                   txtTemplateAddSignature="0";
               }


            }
        });

        rb_signature_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    txtTemplateAddSignature="0";
                }else{
                    txtTemplateAddSignature="1";
                }


            }
        });

        rb_plain_text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_plain_text.setVisibility(View.VISIBLE);
                    ll_formated_text.setVisibility(View.GONE);
                    ll_attchments_layout_text.setVisibility(View.GONE);
                    ll_link_layout.setVisibility(View.GONE);
                    txtTemplateType="1";
                }/*else{
                    ll_formated_text.setVisibility(View.VISIBLE);
                    ll_plain_text.setVisibility(View.GONE);
                }*/
            }
        });

            rb_formatted_text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked){
                        ll_formated_text.setVisibility(View.VISIBLE);
                        ll_plain_text.setVisibility(View.GONE);
                        ll_attchments_layout_text.setVisibility(View.VISIBLE);
                        ll_link_layout.setVisibility(View.VISIBLE);
                        txtTemplateType="2";
                    }/*else{
                        ll_plain_text.setVisibility(View.GONE);
                    }*/
                }
            });

        ib_custom_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleTooltip.Builder(CreateNewTextMessageActivity.this)
                        .anchorView(v)
                        .text("By using custom message, you can create formatted text with adding images, links and attachments.")
                        .gravity(Gravity.BOTTOM)
                        .textColor(getResources().getColor(R.color.colorBlack))
                        .backgroundColor(getResources().getColor(R.color.colorOffWhite))
                        .arrowColor(getResources().getColor(R.color.colorOffWhite))
                        .animated(true)
                        .build()
                        .show();
            }
        });
    }

    private void selectCurrentDay() {
        Date curDate = null;
        try {
            curDate = new SimpleDateFormat("MM-dd-yyyy").parse((month+1)+"-"+days+"-"+year);
            Log.d(Global.TAG, "Current Date: "+curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currDayName = new SimpleDateFormat("EEE").format(curDate);
        Log.d(Global.TAG, "Current day name: "+currDayName+"Input Date:"+(month+1)+"-"+days+"-"+year);

        if (!currDayName.equals("")){
            if (currDayName.equalsIgnoreCase("mon")){
                ch_tc_monday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("tue")){
                ch_tc_tuesday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("wed")){
                ch_tc_wednesday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("thu")){
                ch_tc_thursday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("fri")){
                ch_tc_friday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("sat")){
                ch_tc_saturday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("sun")){
                ch_tc_sunday.setChecked(true);
            }
        }

    }

    private void selectImage(Context context) {
        //this.iv_category_image = iv_category_image;
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
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
                    Toast.makeText(CreateNewTextMessageActivity.this, "Image Uploaded SuccessFully..!", Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "Image Uploaded..! "+imageResponse.getResult());
                    editor_text_msg_contents.insertImage(imageResponse.getResult(),"Image");
                   // editor_text_msg_contents.insertImage(imageResponse.getResult(),"Image");
                }else{
                    Log.d(Global.TAG, " Error in Image Upload..! ");
                    Toast.makeText(CreateNewTextMessageActivity.this, "Error..!"+imageResponse.getResult(), Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                dialog.dismiss();
                Log.d(Global.TAG, "onFailure: Upload "+t);
                Toast.makeText(CreateNewTextMessageActivity.this, "Error in Uploading Image!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(CreateNewTextMessageActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CreateNewTextMessageActivity.this,
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
                Toast.makeText(CreateNewTextMessageActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void editorFunctions() {
        editor_text_msg_contents = (RichEditor) findViewById(R.id.edt_text_contents);
        editor_text_msg_contents.setEditorHeight(200);
        editor_text_msg_contents.setEditorFontSize(14);
        //edt_email_contents.startNestedScroll(R.layout.edt_email_contents);
        //edt_email_contents.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        editor_text_msg_contents.setPadding(10, 20, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        editor_text_msg_contents.setPlaceholder("Enter Text Message here..");
        //edt_email_contents.setInputEnabled(false);

        mPreview = (TextView) findViewById(R.id.previewt);
        editor_text_msg_contents.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

        findViewById(R.id.action_undot).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.undo();
            }
        });

        findViewById(R.id.action_redot).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.redo();
            }
        });

        findViewById(R.id.action_boldt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setBold();
            }
        });

        findViewById(R.id.action_italict).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setItalic();
            }
        });

        findViewById(R.id.action_subscriptt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setSubscript();
            }
        });

        findViewById(R.id.action_superscriptt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrought).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underlinet).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setUnderline();
            }
        });


        findViewById(R.id.action_txt_colort).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                editor_text_msg_contents.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_colort).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                editor_text_msg_contents.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_align_leftt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_centert).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_rightt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setAlignRight();
            }
        });

        findViewById(R.id.action_insert_bulletst).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setBullets();
            }
        });

        findViewById(R.id.action_insert_numberst).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setNumbers();
            }
        });

        findViewById(R.id.action_insert_imaget).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
//                edt_email_contents.insertImage(" https://successentellus.com/check/assets/uploads/ckEditorImage/841/2-watercolor-painting-by-vilas_kulkarni.jpg",
//                        "ckEditorImage");
                Toast.makeText(CreateNewTextMessageActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                selectImage(CreateNewTextMessageActivity.this);
            }
        });

        findViewById(R.id.action_insert_linkt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.insertLink("https://www.successentellus.com", "success");
            }
        });
        findViewById(R.id.action_insert_checkboxt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.insertTodo();

            }
        });


        //edt_email_contents.setHtml(editEmailContents);
        //mPreview.setText(editEmailContents);
    }

    private void openDocuments() {
        Log.d(Global.TAG, "Select File: ");
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, 100);
    }

    private void addLinkView() {
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View add_link_view = inflater.inflate(R.layout.add_link_view, null);
        final EditText edt_link_view = (EditText) add_link_view.findViewById(R.id.edt_link_view);
        allEds.add(edt_link_view);
        ImageButton ib_cancel_link=(ImageButton) add_link_view.findViewById(R.id.ib_cancel_link);
        //tv_attachment_name.setText(attached_file_name);

        ib_cancel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_add_link.removeView(add_link_view);
                allEds.remove(edt_link_view);

            }
        });
        ll_add_link.addView(add_link_view);
    }
    private void fillSpinnerwith12() {
        repeat_every.clear();
        for (int i=1;i<=13;i++){
            repeat_every.add(String.valueOf(i));
        }
        applySpinner1(repeat_every,sp_tc_repeate_every,"-Select-");
        sp_tc_repeate_every.setSelection(0);
    }
    private void init() {

        ll_link_layout = (LinearLayout) findViewById(R.id.ll_link_layout);
        ll_scheduled_interval = (LinearLayout) findViewById(R.id.ll_scheduled_interval);
        ll_repeat_text_camp = (LinearLayout) findViewById(R.id.ll_repeat_text_camp);
        rbg_interval = (RadioGroup) findViewById(R.id.rbg_interval);
        rb_immediately = (RadioButton) findViewById(R.id.rb_immediately);
        rb_schedule = (RadioButton) findViewById(R.id.rb_schedule);
        rb_repeat_text_camp = (RadioButton) findViewById(R.id.rb_repeat_text_camp);
        tv_check_days_error = (TextView) findViewById(R.id.tv_check_days_error);
        rb_signature_yes = (RadioButton) findViewById(R.id.rb_signature_yes);
        rb_signature_no = (RadioButton) findViewById(R.id.rb_signature_no);

        btn_text_dissmiss = (Button) findViewById(R.id.btn_text_add_dissmiss);
        tv_text_from_saved_document = (TextView) findViewById(R.id.tv_text_from_saved_document);
        edt_text_message_name = (EditText) findViewById(R.id.edt_text_message_name);
        edt_text_message = (EditText) findViewById(R.id.edt_text_message);
        edt_add_link = (EditText) findViewById(R.id.edt_add_link);
        edt_ends_after = (EditText) findViewById(R.id.edt_tc_ends_after);
        ll_attached_files = (LinearLayout) findViewById(R.id.ll_attached_files);
        ll_add_link = (LinearLayout) findViewById(R.id.ll_add_link);
        tv_add_link = (TextView) findViewById(R.id.tv_add_link);
        btn_attach_file = (Button) findViewById(R.id.btn_attach_file);
        edt_time_interval_text_message = (EditText) findViewById(R.id.edt_time_interval_text_message);
        sp_select_message_time_interval = (Spinner) findViewById(R.id.sp_select_message_time_interval);
        sp_tc_repeate_every = (Spinner) findViewById(R.id.sp_tc_repeate_every);
        btn_save_text_message = (Button) findViewById(R.id.btn_save_text_message);
        btn_cancel_text_message = (Button) findViewById(R.id.btn_cancel_text_message);

        ch_tc_monday = (CheckBox) findViewById(R.id.ch_tc_monday);
        ch_tc_tuesday = (CheckBox) findViewById(R.id.ch_tc_tuesday);
        ch_tc_wednesday = (CheckBox) findViewById(R.id.ch_tc_wednesday);
        ch_tc_thursday = (CheckBox) findViewById(R.id.ch_tc_thursday);
        ch_tc_friday = (CheckBox) findViewById(R.id.ch_tc_friday);
        ch_tc_saturday = (CheckBox) findViewById(R.id.ch_tc_saturday);
        ch_tc_sunday = (CheckBox) findViewById(R.id.ch_tc_sunday);

        rb_plain_text = (RadioButton) findViewById(R.id.rb_plain_text);
        rb_formatted_text = (RadioButton) findViewById(R.id.rb_formatted_text);
        ll_plain_text = (LinearLayout) findViewById(R.id.ll_plain_text);
        ll_formated_text = (LinearLayout) findViewById(R.id.ll_formated_text);
        ll_attchments_layout_text = (LinearLayout) findViewById(R.id.ll_attchments_layout_text);

        ib_custom_help = (ImageButton) findViewById(R.id.ib_custom_help);

        rb_footer_no = (RadioButton) findViewById(R.id.rb_footer_no);
        rb_footer_yes = (RadioButton) findViewById(R.id.rb_footer_yes);

        spLib = new SPLib(CreateNewTextMessageActivity.this);
        user_id = spLib.getPref(SPLib.Key.USER_ID);
        linkArray=new JSONArray();
        deleteIds=new ArrayList<>();

        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        days = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

    }

    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        spinnerAdapter adapterRepeateDaily = new spinnerAdapter(CreateNewTextMessageActivity.this, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }
    private void applySpinner1(final List<String> taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(CreateNewTextMessageActivity.this, android.R.layout.simple_list_item_1);
       // adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        //adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }
    public static class spinnerAdapter extends ArrayAdapter<String> {

        public spinnerAdapter(Activity context, int textViewResourceId) {
            super(context, textViewResourceId);
            // TODO Auto-generated constructor stub
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }
    }

    private void addTextMessage() {
        String interval="";
        String interval_type="";
        String select_type="";

        if (ll_scheduled_interval.getVisibility()==View.VISIBLE){
            interval=edt_time_interval_text_message.getText().toString();
            interval_type=text_message_interval_type.toLowerCase();
            select_type="2";
        }else if (ll_repeat_text_camp.getVisibility()==View.VISIBLE){
            interval="";
            interval_type="";
            select_type="3";
        }else{
            interval="0";
            interval_type="hours";
            select_type="1";
        }
        getDaysSelection();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateCampId", txt_camp_id);
            paramObj.put("txtTemplateFooterFlag", txtTemplateFooterFlag);
            paramObj.put("txtTemplateAddSignature", txtTemplateAddSignature);
            paramObj.put("txtTemplateTitle", edt_text_message_name.getText().toString());

            if (txtTemplateType.equals("1")){
                paramObj.put("txtTemplateMsg", edt_text_message.getText().toString());
            }else if (txtTemplateType.equals("2")){
                paramObj.put("txtTemplateMsg", mPreview.getText().toString());
            }

            paramObj.put("txtTemplateInterval", interval);
            paramObj.put("txtTemplateIntervalType",interval_type);
            paramObj.put("selectType", select_type);
            paramObj.put("repeat_every_weeks", repeat_every_string);
            paramObj.put("repeat_on", dayString);
            paramObj.put("txtTemplateType", txtTemplateType);
            paramObj.put("repeat_ends_after", edt_ends_after.getText().toString());
            paramObj.put("addLinkUrl",linkArray);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "addTextMessage: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "addTextMessage: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(CreateNewTextMessageActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.add_text_message(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult.isSuccess()) {
                    Toast.makeText(CreateNewTextMessageActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                   // ll_attached_files.removeAllViews();
                    finish();
                } else {
                    Toast.makeText(CreateNewTextMessageActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:addMessage: " + t);
            }
        });

    }

    private void getDaysSelection() {

        dayList.clear();

        if (ch_tc_monday.isChecked()){
            dayList.add("Mon");
        }
        if (ch_tc_tuesday.isChecked()){
            dayList.add("Tue");
        }
        if (ch_tc_wednesday.isChecked()){
            dayList.add("Wed");
        }
        if (ch_tc_thursday.isChecked()){
            dayList.add("Thu");
        }
        if (ch_tc_friday.isChecked()){
            dayList.add("Fri");
        }
        if (ch_tc_saturday.isChecked()){
            dayList.add("Sat");
        }
        if (ch_tc_sunday.isChecked()){
            dayList.add("Sun");
        }

        dayString="";
        for (String s : dayList)
        {
            dayString += s + ",";
        }
        if (dayString.endsWith(",")) {
            dayString = dayString.substring(0, dayString.length() - 1);
        }
        Log.d(Global.TAG, "getDaysSelection: "+dayString);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Global.TAG, "onActivityResult:");
        if (requestCode==100) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String selectedFilePath = FilePath.getPath(CreateNewTextMessageActivity.this, uri);
                if (selectedFilePath!=null){
                    final File file = new File(selectedFilePath);
                    Log.d(Global.TAG, "onActivityResult: File:" + file);
                    String filename=getFileName(file);
                    attached_file_name=filename;
                    Log.d(Global.TAG, "onActivityResult: File length in bytes: "+file.length());

                    if (file != null) {
                        if ( MyValidator.isValidFileSize(file)){
                            uploadAttachment(file);
                        }else{
                            Toast.makeText(this, "File size must be less than 5Mb..!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        }else if(requestCode==1010 && resultCode==RESULT_OK){

            Log.d(Global.TAG, "onActivityResult: CreateText delete Ids:  "+deleteIds.size());
            if (attached_file_names.size()>0){
                for(int i=0;i<attached_file_names.size();i++){
                    displayAttachment(attached_file_names.get(i).toString());
                    // attached_files_names.remove(i);
                    Log.d(Global.TAG, "attached_files_names: "+attached_file_names.size());
                }
                attached_file_names.clear();
            }
        }if (resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = picuri;
            File image_file=new File(picuri.getPath());
            Log.d("picUri", uri.toString());
            //insertImage(image_file);
            //performCrop();

        } else if (resultCode == RESULT_OK && requestCode == 2) {
            picuri = data.getData();
            String Path=getRealPathFromURI(picuri);
//            String filename=Path.substring(Path.lastIndexOf("/")+1);
//            Log.d(Global.TAG, "Path filename : "+filename);

            File image_file=new File(Path);
            Log.d(Global.TAG, "Image File: "+image_file);
            Log.d("mytag", picuri.toString());
            insertImage(image_file);
            // performCrop();
        }
/*

                if (requestCode == 100 && data != null) {
            Uri uri = data.getData();
            String selectedFilePath = FilePath.getPath(CreateNewTextMessageActivity.this, uri);
            final File file = new File(selectedFilePath);
            Log.d(Global.TAG, "onActivityResult: File:"+file);
            String filename=getFileName(file);
            attached_file_name=filename;
            if (file!=null  && filename!=null){
                uploadAttachment(file);
            }
*/



           /* Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            Log.d(Global.TAG, "onActivityResult: Path:" + path);
            String displayName = null;

            String Path = getRealPathFromURI(uri);
            File image_file = new File(Path);
            Log.d(Global.TAG, " File Real Path: " + image_file);

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.d(Global.TAG, "onActivityResult: content Display Name " + displayName);
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
                Log.d(Global.TAG, "onActivityResult: file displayName:" + displayName);
            }
            Log.d(Global.TAG, "onActivityResult: displayName:" + displayName);
            attached_file_name = displayName;
            Log.d(Global.TAG, "onActivityResult: attached_file_name:" + attached_file_name);
            // tv_attached_files.setText(attached_file_name);
            if (image_file != null) {
                uploadAttachment(image_file, attached_file_name);
            } else {
                Toast.makeText(this, "Error in Getting File", Toast.LENGTH_SHORT).show();
            }

*/

    }

    private boolean isValidFileSize(File file) {
        long fileSizeInBytes = file.length();//in bytes
        long fileSizeInKB = fileSizeInBytes / 1024;//in kb
        long fileSizeInMB = fileSizeInKB / 1024;//in mb
        Log.d(Global.TAG, "isValidFileSize:File Size: "+fileSizeInMB);
        if (fileSizeInMB>4){
            return false;
        }else{
            return true;
        }
    }

    private String getFileName(File file) {
        String path=file.getPath();
        Log.d(Global.TAG, "getFileName:Path: "+file.getPath());
        String filename = path.substring(path.lastIndexOf("/")+1);

        Log.d(Global.TAG, "Real Path: " + path);
        Log.d(Global.TAG, "Filename With Extension: " + filename);
        return  filename;
    }

    /* public String getRealPathFromURI(Context context, Uri contentUri) {
         Log.d(Global.TAG, "getRealPathFromURI: ");
         Cursor cursor = null;
         try {
             String[] proj = { MediaStore.Images.Media.DATA };
             cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
             int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
             cursor.moveToFirst();
             return cursor.getString(column_index);
         } catch(Exception e){
             Log.d(Global.TAG, "getRealPathFromURI: "+e);

         }finally {
             if (cursor != null) {
                 cursor.close();
             }
             return "0";
         }

     }*/
    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        int idx = 0;
        try {
            if (cursor == null) { // Source is Dropbox or other similar local file path
                return contentURI.getPath();
            } else {
                cursor.moveToFirst();
                idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                Log.d(Global.TAG, "getRealPathFromURI: index: "+idx);
                return cursor.getString(idx);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Global.TAG, "getRealPathFromURI:Exception: " + e);
            return cursor.getString(idx);
        }
    }

    private void uploadAttachment(File file) {
        Log.d(Global.TAG, "uploadAttachment: "+file);
        RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody platform = RequestBody.create(MediaType.parse("text/plain"), "2");
        //RequestBody txtCampAttachId = RequestBody.create(MediaType.parse("text/plain"), "0");
        RequestBody txtCampAttachTempId = RequestBody.create(MediaType.parse("text/plain"), "0");

        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Uploading...");
        dialog.setMessage("Uploading... Please Wait..");
        dialog.show();

        Log.d(Global.TAG, "userId:: "+userId);
        Log.d(Global.TAG, "platform:: "+platform);
        Log.d(Global.TAG, "File: "+body);
        Log.d(Global.TAG, "txtCampAttachTempId: "+txtCampAttachTempId);

        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<UploadResult> call = service.upload_text_message_attachment(userId,platform,body,txtCampAttachTempId);
        call.enqueue(new Callback<UploadResult>() {
            @Override
            public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                UploadResult uploadResult=response.body();
                if (uploadResult.isSuccess()){
                    Toast.makeText(CreateNewTextMessageActivity.this, "File Uploaded Successfully.!", Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "File Uploaded Successfully..! ");
                    UploadRes uploadRes=uploadResult.getResult();
                    deleteIds.add(String.valueOf(uploadRes.getTxtCampAttachTempId()));
                    displayAttachment(attached_file_name);
                }else{
                    Log.d(Global.TAG, " Error in File Upload..! ");
                    Toast.makeText(CreateNewTextMessageActivity.this, " File size must be less than 5mb per attachment..! ", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<UploadResult> call, Throwable t) {
                dialog.dismiss();
                Log.d(Global.TAG, "onFailure: Upload "+t);
                Toast.makeText(CreateNewTextMessageActivity.this, "File size must be less than 5mb per attachment..!", Toast.LENGTH_SHORT).show();
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
                    int index=getViewIndex(ll_attached_files,attachmentView);
                    Log.d(Global.TAG, " Index in View: "+index);
                    deleteAttachment(index,attachmentView);
                   // ll_attached_files.removeView(attachmentView);

                }
            });
            ll_attached_files.addView(attachmentView);
        }

    private void deleteAttachment(final int index, final View attachmentView) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampAttachId", deleteIds.get(index).toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteAttachment: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteAttachment: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(CreateNewTextMessageActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.delete_attachment(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(CreateNewTextMessageActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, ""+jsonResult.getResult());
                    ll_attached_files.removeView(attachmentView);
                    deleteIds.remove(index);
                }else{
                    Toast.makeText(CreateNewTextMessageActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
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

    private int checkMemberAssignedOrNot() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId", txt_camp_id);
            paramObj.put("stepId","0" );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "checkMemberAssignedOrNot: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "checkMemberAssignedOrNot: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(CreateNewTextMessageActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<CheckTxtCampHasMember> call = service.checkMemberAssignedOrNot(paramObj.toString());
        call.enqueue(new Callback<CheckTxtCampHasMember>() {
            @Override
            public void onResponse(Call<CheckTxtCampHasMember> call, Response<CheckTxtCampHasMember> response) {
                if (response.isSuccessful()){
                    CheckTxtCampHasMember checkTxtCampHasMember=response.body();
                    if (checkTxtCampHasMember.getResult()==1){
                        memberAssignedFlag=1;
                    }else if (checkTxtCampHasMember.getResult()==0){
                        memberAssignedFlag=0;
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CheckTxtCampHasMember> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:CheckTxtCampHasMember "+t);
            }
        });
        return memberAssignedFlag;
    }

    private int getViewIndex (ViewGroup viewGroup, View view)
    {
        return viewGroup.indexOfChild(view);
    }
    }
