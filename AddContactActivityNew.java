package com.success.successEntellus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.hbb20.GThumb;
import com.success.successEntellus.R;
import com.success.successEntellus.adapter.TagSpinnerAdapter;
import com.success.successEntellus.fragment.MyContactFragment;
import com.success.successEntellus.fragment.MyContactFragmentNew;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.AllGroups;
import com.success.successEntellus.model.Contact_For;
import com.success.successEntellus.model.Contact_Result;
import com.success.successEntellus.model.Contact_Source;
import com.success.successEntellus.model.Contact_Spinner;
import com.success.successEntellus.model.Contact_Status;
import com.success.successEntellus.model.Contact_Tag;
import com.success.successEntellus.model.GetSubScriptionDate;
import com.success.successEntellus.model.GroupInfo;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.SubscriptionDates;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddContactActivityNew extends AppCompatActivity implements TagSpinnerAdapter.RefreashTags{

/*    EditText edt_middleName, edt_companyName, edt_skypeid, edt_tweeterid,et_cust_policy_no,et_contract_renewal_date,
            edt_facebook_profile, edt_linkedin_profile, edt_address, edt_city, edt_zipcode, edt_state, edt_country, edt_description,
            edt_nlgagent_id,edt_pfaagent_id,edt_joining_date;*/

    EditText  edt_companyName, edt_address, edt_city, edt_zipcode, edt_state, edt_country, edt_description;

    EditText edt_phone,edt_phone2,edt_phone3;
    String[] contact_status_array;
    String[] contact_source_array;
    List<String> contact_tag_array;
    int contactforpos=0;

    EditText et_industry,edt_estart_on,edt_estart_time;
    EditText edt_firstName,edt_lastName;

    EditText edt_email,edt_email_work2,edt_email_other;

    Button btn_adddissmiss,btn_addcontact_back;
    TextView tv_add_email,tv_add_phone,tv_addContactTitle;
    EditText edt_brithDate;
    Spinner spin_tag,sp_contact_for,sp_contact_status,sp_contact_source,sp_select_group;
    Calendar cal;
    int day,month,year,hour,min;
    String[] taglist = {"Green Apple", "Red Apple", "Brown Apple", "Rotten Apple"};
    LinearLayout ll_one, ll_two, ll_three, ll_four,ll_six;
    ToggleButton btn_personal_info,btn_social_info,btn_address_info,btn_other_info,btn_note_layout;
    ImageView image_arrow4,image_arrow3,image_arrow2,image_arrow1,image_arrow5,image_arrow6;
    LinearLayout iv_birthDate, iv_anniDate;
    final HashMap<String, String> params = new HashMap<>();
    SPLib spLib;
    String user_id,contact_id;
    String category="";
    private boolean isContactEditable=false;
    private Bundle extras;
    DashboardActivity mainActivity;
    String[] contactForList;
    String contact_for_id;
    String edit_contact_for;
    String contact_source_id;
    String contact_status_id,selected_group;
    String cust_policy_no;
    List<GroupInfo> groupList;
    String crm_flag;
    List<Contact_Status> contactStatusList;
    List<Contact_Source> contactSourceList;
    List<Contact_Tag> contactTagList;
    String[] group_array;
    MyContactFragment myContactFragment;
    String contact_title;
    boolean editFlag,viewFlag;
    MyValidator myValidator;
    public String contact_status;
    public String contact_source;
    int contact_forSp;
    private int contactsourcepos=0;
    private String selected_tag="";
    Toolbar toolbar_add_contact;
    ImageButton ib_add_contact_back;
    Button btn_add_contact_new;
   // CircleImageView iv_contact_profile;
    GThumb iv_contact_profile;
    //LinearLayout ll_recruit_layout;
    FloatingActionButton fab_edit_contact;
    Paint paint;
    Random rnd;
    String contact_name="",add_to_calendar="0",format="";
    CheckBox ch_add_to_calendar;
    LinearLayout ll_add_to_calendar;
    private int am_pm;
    int eventDescripId=0;
    ListView lv_tag;
    Dialog dialog;

    TextView tv_assigned_camp, tv_assigned_groups, tv_assigned_text_camp,tv_add_tag;
    LinearLayout ll_view_campaign_groups;
    private String sub_start_date="";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_new_design);
//        getSupportActionBar().hide();
        setSupportActionBar(toolbar_add_contact);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        init();

        getSubscriptionDate();
        extras=getIntent().getExtras();
        if (extras!=null) {
            if (extras.containsKey("crm_flag")) {
                crm_flag = extras.getString("crm_flag");
                Log.d(Global.TAG, "AddContact: crmFlag " + crm_flag);
            }
            contact_id = extras.getString("contact_id");
            editFlag=extras.getBoolean("ContactEditable");
            viewFlag=extras.getBoolean("viewFlag");
        }

        //applySpinner(taglist,spin_tag, "--Select Tag--");

        getSpinnerData();

        if (extras!=null){

           if (editFlag || viewFlag){
                contact_title=extras.getString("contact_title");
                fillContactEditText();
            }else{

            }
        }
        myContactFragment=new MyContactFragment(crm_flag);
        checkVisibleControls();

        getAllGroups();
        spinnerSelection();
        setInitialToProfile();

        image_arrow1.setVisibility(View.VISIBLE);

        btn_personal_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_one.setVisibility(View.VISIBLE);
                    ll_two.setVisibility(View.GONE);
                    ll_three.setVisibility(View.GONE);
                    ll_four.setVisibility(View.GONE);
                    //ll_five.setVisibility(View.GONE);
                    ll_six.setVisibility(View.GONE);

                    image_arrow1.setVisibility(View.VISIBLE);
                    image_arrow2.setVisibility(View.GONE);
                    image_arrow3.setVisibility(View.GONE);
                    image_arrow4.setVisibility(View.GONE);
                   // image_arrow5.setVisibility(View.GONE);
                    image_arrow6.setVisibility(View.GONE);

                }
            }
        });


        btn_other_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_one.setVisibility(View.GONE);
                    ll_two.setVisibility(View.VISIBLE);
                    ll_three.setVisibility(View.GONE);
                    ll_four.setVisibility(View.GONE);
                    //ll_five.setVisibility(View.GONE);
                    ll_six.setVisibility(View.GONE);

                    image_arrow1.setVisibility(View.GONE);
                    image_arrow2.setVisibility(View.VISIBLE);
                    image_arrow3.setVisibility(View.GONE);
                    image_arrow4.setVisibility(View.GONE);
                   // image_arrow5.setVisibility(View.GONE);
                    image_arrow6.setVisibility(View.GONE);

                }
            }
        });

        btn_social_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_one.setVisibility(View.GONE);
                    ll_two.setVisibility(View.GONE);
                    ll_three.setVisibility(View.VISIBLE);
                    ll_four.setVisibility(View.GONE);
                    //ll_five.setVisibility(View.GONE);
                    ll_six.setVisibility(View.GONE);

                    image_arrow1.setVisibility(View.GONE);
                    image_arrow2.setVisibility(View.GONE);
                    image_arrow3.setVisibility(View.VISIBLE);
                    image_arrow4.setVisibility(View.GONE);
                    //image_arrow5.setVisibility(View.GONE);
                    image_arrow6.setVisibility(View.GONE);

                }
            }
        });

        btn_address_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_one.setVisibility(View.GONE);
                    ll_two.setVisibility(View.GONE);
                    ll_three.setVisibility(View.GONE);
                    ll_four.setVisibility(View.VISIBLE);
                   // ll_five.setVisibility(View.GONE);
                    ll_six.setVisibility(View.GONE);

                    image_arrow1.setVisibility(View.GONE);
                    image_arrow2.setVisibility(View.GONE);
                    image_arrow3.setVisibility(View.GONE);
                    image_arrow4.setVisibility(View.VISIBLE);
                   // image_arrow5.setVisibility(View.GONE);
                    image_arrow6.setVisibility(View.GONE);

                }
            }
        });
        btn_note_layout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_one.setVisibility(View.GONE);
                    ll_two.setVisibility(View.GONE);
                    ll_three.setVisibility(View.GONE);
                    ll_four.setVisibility(View.GONE);
                    //ll_five.setVisibility(View.GONE);
                    ll_six.setVisibility(View.VISIBLE);


                    image_arrow1.setVisibility(View.GONE);
                    image_arrow2.setVisibility(View.GONE);
                    image_arrow3.setVisibility(View.GONE);
                    image_arrow4.setVisibility(View.GONE);
                    //image_arrow5.setVisibility(View.GONE);
                    image_arrow6.setVisibility(View.VISIBLE);

                }
            }
        });

       /* btn_recruit_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_one.setVisibility(View.GONE);
                    ll_two.setVisibility(View.GONE);
                    ll_three.setVisibility(View.GONE);
                    ll_four.setVisibility(View.GONE);
                    ll_five.setVisibility(View.VISIBLE);
                    ll_six.setVisibility(View.GONE);

                    image_arrow1.setVisibility(View.GONE);
                    image_arrow2.setVisibility(View.GONE);
                    image_arrow3.setVisibility(View.GONE);
                    image_arrow4.setVisibility(View.GONE);
                    image_arrow5.setVisibility(View.VISIBLE);
                    image_arrow6.setVisibility(View.GONE);
                }
            }
        });
*/
        ib_add_contact_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fab_edit_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewFlag && !editFlag){
                    setAllEditableTrue();
                    editFlag=true;
                    checkVisibleControls();
                    btn_add_contact_new.setVisibility(View.VISIBLE);
                    btn_add_contact_new.setText("Update");
                    fab_edit_contact.setVisibility(View.GONE);
                }
            }
        });


        btn_add_contact_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.isNetworkAvailable(getApplicationContext())) {

                   /* if (viewFlag && !editFlag){
                        setAllEditableTrue();
                        editFlag=true;
                        btn_add_contact_new.setText("Update");
                    }else */if (!edt_firstName.getText().toString().equals("") || !edt_lastName.getText().toString().equals("")){
                        if (!edt_email.getText().toString().equals("") || !edt_email_work2.getText().toString().equals("") || !edt_email_other.getText().toString().equals("")){

                            if (myValidator.isValidEmail(edt_email) && myValidator.isValidEmail(edt_email_work2) && myValidator.isValidEmail(edt_email_other)){

                                if (!editFlag){
                                    if (add_to_calendar.equals("1")){
                                        if (MyValidator.isValidFieldE(edt_estart_on,"Enter Date")){
                                            if (MyValidator.isValidFieldE(edt_estart_time,"Enter Time")){
                                                addUserContact();
                                            }
                                        }
                                    }else{
                                        addUserContact();
                                    }

                                }else{
                                    if (add_to_calendar.equals("1")){
                                        if (MyValidator.isValidFieldE(edt_estart_on,"Enter Date")){
                                            if (MyValidator.isValidFieldE(edt_estart_time,"Enter Time")){
                                                editUserContactOnContactId();
                                            }
                                        }
                                    }else{
                                        editUserContactOnContactId();
                                    }

                                }
                            }
                        }else{
                            if (!edt_phone.getText().toString().equals("") || !edt_phone2.getText().toString().equals("") || !edt_phone3.getText().toString().equals("")){

                                if (myValidator.isValidMobile(edt_phone) && myValidator.isValidMobile(edt_phone2) && myValidator.isValidMobile(edt_phone3)){
                                    if (!editFlag){
                                        addUserContact();
                                    }else{
                                        editUserContactOnContactId();
                                    }
                                }
                            }else{
                                edt_firstName.clearFocus();
                                edt_email.setFocusable(true);
                                edt_email.requestFocus();
                                edt_email.setError("Please Enter Email or Phone..!");
                            }
                        }
                    }else{
                        edt_firstName.setError("Please Enter Either FirstName or LastName...!");
                    }



                }else{
                    Intent intent = new Intent(getApplicationContext(), NetworkCheckActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


        if (!viewFlag) {
            edt_brithDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(102);
//                DatePickerFragment fragment = new DatePickerFragment();
//                fragment.show(getFragmentManager(),"Theme 4");
                }
            });
        }

        if (!viewFlag) {
            edt_estart_on.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(707);
                }
            });
        }

        if (!viewFlag){
            edt_estart_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(AddContactActivityNew.this,AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String format="";
                            if (selectedHour == 0) {
                                selectedHour += 12;
                                format = "AM";
                            }
                            else if (selectedHour == 12) {
                                format = "PM";
                            }
                            else if (selectedHour > 12) {
                                selectedHour -= 12;
                                format = "PM";
                            }
                            else {
                                format = "AM";
                            }
                            edt_estart_time.setText( selectedHour + ":" + selectedMinute+" "+format);
                        }
                    }, hour, min, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

        }

        ch_add_to_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_description,"Please Enter Note First..!")){
                    ch_add_to_calendar.setChecked(true);
                }else{
                    ch_add_to_calendar.setChecked(false);
                }
            }
        });

        ch_add_to_calendar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked){
                    add_to_calendar="1";
                    ll_add_to_calendar.setVisibility(View.VISIBLE);
                }else{
                    add_to_calendar="0";
                    ll_add_to_calendar.setVisibility(View.GONE);
                }
            }
        });


        if (!viewFlag){
            tv_add_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogChangeTag();
                }
            });
        }


        if (viewFlag && !editFlag){
            ch_add_to_calendar.setClickable(false);
        }


    }

    private void getSubscriptionDate() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        Log.d(Global.TAG, "getSubscriptionDate: param:"+paramObj);
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<GetSubScriptionDate> call= service.getSubscriptionDate(paramObj.toString());
        call.enqueue(new Callback<GetSubScriptionDate>() {
            @Override
            public void onResponse(Call<GetSubScriptionDate> call, Response<GetSubScriptionDate> response) {
                if (response.isSuccessful()){
                    GetSubScriptionDate getSubScriptionDate=response.body();
                    SubscriptionDates sub_dates=getSubScriptionDate.getResult();
                    sub_start_date=sub_dates.getsDate();

                    Log.d(Global.TAG, "onResponse:sub_start_date "+sub_start_date);
                }
            }

            @Override
            public void onFailure(Call<GetSubScriptionDate> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: GetSubScriptionDate:"+t);
            }
        });

    }



    private void checkVisibleControls() {

        if (editFlag){
            sp_select_group.setVisibility(View.GONE);
            btn_add_contact_new.setText("Update");
            fab_edit_contact.setVisibility(View.GONE);
            ll_view_campaign_groups.setVisibility(View.GONE);

            if (crm_flag.equals("1")){
                tv_addContactTitle.setText("Edit Contact");
            }else if (crm_flag.equals("2")){
                tv_addContactTitle.setText("Edit Customer");
            }else if (crm_flag.equals("3")){
                tv_addContactTitle.setText("Edit Prospect");
            }else if (crm_flag.equals("4")){
                tv_addContactTitle.setText("Edit Recruit");
            }

        }else{
            if (!viewFlag){
                sp_select_group.setVisibility(View.VISIBLE);
                fab_edit_contact.setVisibility(View.GONE);
                ll_view_campaign_groups.setVisibility(View.GONE);
                if (crm_flag.equals("1")){
                    tv_addContactTitle.setText("New Contact");
                }else if (crm_flag.equals("2")){
                    tv_addContactTitle.setText("New Customer");
                }else if (crm_flag.equals("3")){
                    tv_addContactTitle.setText("New Prospect");
                }else if (crm_flag.equals("4")){
                    tv_addContactTitle.setText("New Recruit");
                }
            }else if (viewFlag){

                if (crm_flag.equals("1")){
                    tv_addContactTitle.setText("View Contact");
                }else if (crm_flag.equals("2")){
                    tv_addContactTitle.setText("View Customer");
                }else if (crm_flag.equals("3")){
                    tv_addContactTitle.setText("View Prospect");
                }else if (crm_flag.equals("4")){
                    tv_addContactTitle.setText("View Recruit");
                }

                btn_add_contact_new.setVisibility(View.GONE);
                fab_edit_contact.setVisibility(View.VISIBLE);
                setAllEditableFalse();
                ll_view_campaign_groups.setVisibility(View.VISIBLE);
            }

        }
    }

    private void setAllEditableFalse() {
        sp_select_group.setVisibility(View.GONE);

        edt_firstName.setFocusable(false);
        edt_firstName.setFocusableInTouchMode(false);
        edt_firstName.setClickable(false);

        edt_lastName.setFocusable(false);
        edt_lastName.setFocusableInTouchMode(false);
        edt_lastName.setClickable(false);

        edt_email.setFocusable(false);
        edt_email.setFocusableInTouchMode(false);
        edt_email.setClickable(false);

        edt_phone.setFocusable(false);
        edt_phone.setFocusableInTouchMode(false);
        edt_phone.setClickable(false);

        edt_description.setFocusable(false);
        edt_description.setFocusableInTouchMode(false);
        edt_description.setClickable(false);

        edt_phone2.setFocusable(false);
        edt_phone2.setFocusableInTouchMode(false);
        edt_phone2.setClickable(false);

        edt_phone3.setFocusable(false);
        edt_phone3.setFocusableInTouchMode(false);
        edt_phone3.setClickable(false);

        edt_email_work2.setFocusable(false);
        edt_email_work2.setFocusableInTouchMode(false);
        edt_email_work2.setClickable(false);

        edt_email_other.setFocusable(false);
        edt_email_other.setFocusableInTouchMode(false);
        edt_email_other.setClickable(false);

        edt_email_other.setFocusable(false);
        edt_email_other.setFocusableInTouchMode(false);
        edt_email_other.setClickable(false);

        edt_brithDate.setFocusable(false);
        edt_brithDate.setFocusableInTouchMode(false);
        edt_brithDate.setClickable(false);
        edt_brithDate.setOnClickListener(null);


        et_industry.setFocusable(false);
        et_industry.setFocusableInTouchMode(false);
        et_industry.setClickable(false);

        sp_contact_for.setEnabled(false);
        sp_contact_for.setClickable(false);
        sp_contact_source.setEnabled(false);
        sp_contact_source.setClickable(false);
        sp_contact_status.setEnabled(false);
        sp_contact_status.setClickable(false);

        edt_companyName.setFocusable(false);
        edt_companyName.setFocusableInTouchMode(false);
        edt_companyName.setClickable(false);


//        spin_tag.setEnabled(false);
//        spin_tag.setClickable(false);

        edt_address.setFocusable(false);
        edt_address.setFocusableInTouchMode(false);
        edt_address.setClickable(false);

        edt_city.setFocusable(false);
        edt_city.setFocusableInTouchMode(false);
        edt_city.setClickable(false);

        edt_state.setFocusable(false);
        edt_state.setFocusableInTouchMode(false);
        edt_state.setClickable(false);

        edt_country.setFocusable(false);
        edt_country.setFocusableInTouchMode(false);
        edt_country.setClickable(false);

        edt_zipcode.setFocusable(false);
        edt_zipcode.setFocusableInTouchMode(false);
        edt_zipcode.setClickable(false);

        edt_estart_time.setFocusable(false);
        edt_estart_time.setFocusableInTouchMode(false);
        edt_estart_time.setClickable(false);
        edt_estart_time.setOnClickListener(null);

        edt_estart_on.setFocusable(false);
        edt_estart_on.setFocusableInTouchMode(false);
        edt_estart_on.setClickable(false);
        edt_estart_on.setOnClickListener(null);

        ch_add_to_calendar.setClickable(false);

    }

    private void setAllEditableTrue() {
        edt_firstName.setFocusable(true);
        edt_firstName.setFocusableInTouchMode(true);
        edt_firstName.setClickable(true);

        edt_lastName.setFocusable(true);
        edt_lastName.setFocusableInTouchMode(true);
        edt_lastName.setClickable(true);

        edt_email.setFocusable(true);
        edt_email.setFocusableInTouchMode(true);
        edt_email.setClickable(true);

        edt_phone.setFocusable(true);
        edt_phone.setFocusableInTouchMode(true);
        edt_phone.setClickable(true);

        edt_description.setFocusable(true);
        edt_description.setFocusableInTouchMode(true);
        edt_description.setClickable(true);

        edt_phone2.setFocusable(true);
        edt_phone2.setFocusableInTouchMode(true);
        edt_phone2.setClickable(true);

        edt_phone3.setFocusable(true);
        edt_phone3.setFocusableInTouchMode(true);
        edt_phone3.setClickable(true);

        edt_email_work2.setFocusable(true);
        edt_email_work2.setFocusableInTouchMode(true);
        edt_email_work2.setClickable(true);

        edt_email_other.setFocusable(true);
        edt_email_other.setFocusableInTouchMode(true);
        edt_email_other.setClickable(true);

        edt_email_other.setFocusable(true);
        edt_email_other.setFocusableInTouchMode(true);
        edt_email_other.setClickable(true);

        edt_brithDate.setFocusable(true);
        edt_brithDate.setFocusableInTouchMode(true);
        edt_brithDate.setClickable(true);

        et_industry.setFocusable(true);
        et_industry.setFocusableInTouchMode(true);
        et_industry.setClickable(true);

        sp_contact_for.setEnabled(true);
        sp_contact_for.setClickable(true);
        sp_contact_source.setEnabled(true);
        sp_contact_source.setClickable(true);
        sp_contact_status.setEnabled(true);
        sp_contact_status.setClickable(true);

        edt_companyName.setFocusable(true);
        edt_companyName.setFocusableInTouchMode(true);
        edt_companyName.setClickable(true);

        edt_address.setFocusable(true);
        edt_address.setFocusableInTouchMode(true);
        edt_address.setClickable(true);

        edt_city.setFocusable(true);
        edt_city.setFocusableInTouchMode(true);
        edt_city.setClickable(true);

        edt_state.setFocusable(true);
        edt_state.setFocusableInTouchMode(true);
        edt_state.setClickable(true);

        edt_country.setFocusable(true);
        edt_country.setFocusableInTouchMode(true);
        edt_country.setClickable(true);

        edt_zipcode.setFocusable(true);
        edt_zipcode.setFocusableInTouchMode(true);
        edt_zipcode.setClickable(true);

        edt_estart_time.setFocusable(true);
        edt_estart_time.setFocusableInTouchMode(true);
        edt_estart_time.setClickable(true);

        edt_estart_on.setFocusable(true);
        edt_estart_on.setFocusableInTouchMode(true);
        edt_estart_on.setClickable(true);

        ch_add_to_calendar.setClickable(true);

        ch_add_to_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_description,"Please Enter Note First..!")){
                    ch_add_to_calendar.setChecked(true);
                }else{
                    ch_add_to_calendar.setChecked(false);
                }
            }
        });

        ch_add_to_calendar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked){
                    add_to_calendar="1";
                    ll_add_to_calendar.setVisibility(View.VISIBLE);
                }else{
                    add_to_calendar="0";
                    ll_add_to_calendar.setVisibility(View.GONE);
                }
            }
        });

        edt_brithDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(102);
//                DatePickerFragment fragment = new DatePickerFragment();
//                fragment.show(getFragmentManager(),"Theme 4");
            }
        });

        edt_estart_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(707);
            }
        });

        edt_estart_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddContactActivityNew.this,AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String format="";
                        if (selectedHour == 0) {
                            selectedHour += 12;
                            format = "AM";
                        }
                        else if (selectedHour == 12) {
                            format = "PM";
                        }
                        else if (selectedHour > 12) {
                            selectedHour -= 12;
                            format = "PM";
                        }
                        else {
                            format = "AM";
                        }
                        edt_estart_time.setText( selectedHour + ":" + selectedMinute+" "+format);
                    }
                }, hour, min, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        tv_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogChangeTag();
            }
        });




    }

    private void addUserContact() {
        Log.d(Global.TAG, "addUserContact: ");
        final Dialog myLoader = Global.showDialog(AddContactActivityNew.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Log.d(Global.TAG, "addUserContact:user_id: "+user_id+"crm_flag:"+crm_flag);
        Log.d(Global.TAG, "addUserContact: start Time:"+edt_estart_time.getText().toString()) ;
        Log.d(Global.TAG, "addUserContact: selected tag:"+selected_tag+" Category:"+category) ;
        Log.d(Global.TAG, "addUserContact:add to cal: "+add_to_calendar+"start date:"+edt_estart_on.getText().toString());
        Call<JsonResult> call=service.add_contact(user_id,crm_flag,"2",edt_firstName.getText().toString(),
                edt_lastName.getText().toString(),
                selected_group,
                edt_email.getText().toString(),
                edt_email_work2.getText().toString(),
                edt_email_other.getText().toString(),
                edt_phone.getText().toString(),
                edt_phone2.getText().toString(),
                edt_phone3.getText().toString(),
                contact_for_id,
                contact_source_id,
                contact_status_id,
                et_industry.getText().toString(),
                edt_brithDate.getText().toString(),
                edt_companyName.getText().toString(),
                edt_address.getText().toString(),category,
                selected_tag,
                edt_city.getText().toString(),
                edt_state.getText().toString(),
                edt_zipcode.getText().toString(),
                edt_country.getText().toString(),
                edt_description.getText().toString(),
                eventDescripId,
                add_to_calendar,edt_estart_on.getText().toString(),edt_estart_time.getText().toString()
        );

        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult!=null){
                    if (jsonResult.isSuccess()){
                        //Toast.makeText(AddContactActivity.this, "Contact Added Successfully..!", Toast.LENGTH_LONG).show();
                        Toast.makeText(AddContactActivityNew.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        Log.d(Global.TAG, "Contact Added Successfully..!: ");
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        Toast.makeText(AddContactActivityNew.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        Log.d(Global.TAG, "This Email already Exists..! Please try with another Email.");

                    }
                }

                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                edt_email.setError("Email address already exist!!");
                Toast.makeText(AddContactActivityNew.this, "This Email already Exists..! Please try with another Email.", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure:addUserContact "+t);
            }
        });
    }


    private void init() {
        ll_one=findViewById(R.id.ll_one);
        ll_two=findViewById(R.id.ll_two);
        ll_three=findViewById(R.id.ll_three);
        ll_four=findViewById(R.id.ll_four);
       // ll_five=findViewById(R.id.ll_five);
        ll_six=findViewById(R.id.ll_six);

        btn_personal_info=findViewById(R.id.btn_personal_info);
        btn_social_info=findViewById(R.id.btn_social_info);
        btn_address_info=findViewById(R.id.btn_address_info);
        btn_other_info=findViewById(R.id.btn_other_info);
        //ll_recruit_layout=findViewById(R.id.ll_recruit_layout);
       // btn_recruit_info=findViewById(R.id.btn_recruit_info);
        btn_note_layout=findViewById(R.id.btn_note_layout);
        fab_edit_contact=findViewById(R.id.fab_edit_contact);
        iv_contact_profile=(GThumb) findViewById(R.id.iv_contact_profile);

        toolbar_add_contact=findViewById(R.id.toolbar_add_contact);
        ib_add_contact_back=findViewById(R.id.ib_add_contact_back);
        btn_add_contact_new=findViewById(R.id.btn_add_contact_new);

        image_arrow4=findViewById(R.id.image_arrow4);
        image_arrow3=findViewById(R.id.image_arrow3);
        image_arrow2=findViewById(R.id.image_arrow2);
        image_arrow1=findViewById(R.id.image_arrow1);
       // image_arrow5=findViewById(R.id.image_arrow5);
        image_arrow6=findViewById(R.id.image_arrow6);



        spLib=new SPLib(AddContactActivityNew.this);
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        //spin_tag = (Spinner) findViewById(R.id.spin_tag);
        sp_contact_for = (Spinner) findViewById(R.id.sp_contact_for);
        sp_contact_source = (Spinner) findViewById(R.id.sp_contact_source);
        sp_contact_status = (Spinner) findViewById(R.id.sp_contact_status);
        sp_select_group = (Spinner) findViewById(R.id.sp_select_group);
        edt_firstName = (EditText) findViewById(R.id.edt_firstName);
        edt_lastName = (EditText) findViewById(R.id.edt_lastName);

        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_email_work2 = (EditText) findViewById(R.id.edt_email_work2);
        edt_email_other = (EditText) findViewById(R.id.edt_email_other);

        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_phone2= (EditText) findViewById(R.id.edt_phone2);
        edt_phone3 = (EditText) findViewById(R.id.edt_phone3);

        et_industry=(EditText)findViewById(R.id.et_industry);

        edt_description = (EditText) findViewById(R.id.edt_description);
        edt_brithDate = (EditText) findViewById(R.id.edt_brithDate);
        //edt_anniDate = (EditText) findViewById(R.id.edt_anniDate);
        edt_address = (EditText) findViewById(R.id.edt_address);
        edt_city = (EditText) findViewById(R.id.edt_city);
        edt_zipcode = (EditText) findViewById(R.id.edt_zipcode);
        edt_state = (EditText) findViewById(R.id.edt_state);
        edt_country = (EditText) findViewById(R.id.edt_country);
        edt_estart_on = (EditText) findViewById(R.id.edt_estart_on);
        edt_estart_time = (EditText) findViewById(R.id.edt_estart_time);
        ll_add_to_calendar = (LinearLayout) findViewById(R.id.ll_add_to_calendar);
        ch_add_to_calendar = (CheckBox) findViewById(R.id.ch_add_to_calendar);
        edt_companyName = (EditText) findViewById(R.id.edt_companyName);
        tv_addContactTitle = (TextView) findViewById(R.id.add_contact_title);
        mainActivity= new DashboardActivity();

         tv_assigned_camp = (TextView) findViewById(R.id.tv_assigned_camp);
         tv_assigned_groups = (TextView) findViewById(R.id.tv_assigned_groups);
         tv_assigned_text_camp = (TextView) findViewById(R.id.tv_assigned_text_camp);
        ll_view_campaign_groups = (LinearLayout) findViewById(R.id.ll_view_campaign_groups);
        tv_add_tag = (TextView) findViewById(R.id.tv_add_tag);
        contact_tag_array=new ArrayList<>();


        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        hour = cal.get(Calendar.HOUR);
        min =cal.get(Calendar.MINUTE);
        contactForList=new String[]{ "Business","Policy"};
        contactSourceList=new ArrayList<>();
        contactStatusList=new ArrayList<>();
        paint=new Paint();
        rnd=new Random();
        am_pm =cal.get(Calendar.AM_PM);
        if (am_pm==0){
            format="AM";
        }else{
            format="PM";
        }


        edt_estart_on.setText((month+1)+"-"+day+"-"+year);
        edt_estart_time.setText(hour+":"+min+" "+format);
        Log.d(Global.TAG, "init: ");


    }
    private void openDialogChangeTag() {
        Log.d(Global.TAG, "openDialogChangeTag: ");
        dialog = new Dialog(AddContactActivityNew.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.update_tag_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        lv_tag=dialog.findViewById(R.id.lv_tag);
        LinearLayout ll_add_custom_tag=dialog.findViewById(R.id.ll_add_custom_tag);
        final LinearLayout ll_custom_new_tag=dialog.findViewById(R.id.ll_custom_new_tag);
        final EditText edt_custom_tag=dialog.findViewById(R.id.edt_custom_tag);
        Button btn_add_custom_tag=dialog.findViewById(R.id.btn_add_custom_tag);

        getAllTagList();

        ll_add_custom_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_custom_new_tag.getVisibility()==View.VISIBLE){
                    ll_custom_new_tag.setVisibility(View.GONE);
                }else{
                    ll_custom_new_tag.setVisibility(View.VISIBLE);
                }
            }
        });

        lv_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                if (contactTagList.get(pos).getUserTagName().contains("Select")){
                    selected_tag="";
                    category="";

                }else{
                    selected_tag=contactTagList.get(pos).getUserTagName();
                    category=contactTagList.get(pos).getUserTagId();

                }

                Log.d(Global.TAG, "Position in spinner: "+pos+"category:"+category);
                Log.d(Global.TAG, "selected_tag: "+selected_tag);

                tv_add_tag.setText(contactTagList.get(pos).getUserTagName());
                dialog.dismiss();

            }
        });

        btn_add_custom_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_custom_tag,"Enter Tag to Add")){
                    tv_add_tag.setText(edt_custom_tag.getText().toString());
                    selected_tag=edt_custom_tag.getText().toString();
                    category="0";
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //dialog.show();
       /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,WindowManager.LayoutParams.WRAP_CONTENT , getResources().getDisplayMetrics());
        dialog.getWindow().setAttributes(lp);*/
    }

    private void setInitialToProfile() {
        /*int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        paint.setColor(Color.WHITE);
        iv_contact_profile.setFillColor(color);

        if (editFlag || viewFlag){
            if (contact_name!=null){
                if (!contact_name.equals("")){
                    initial=contact_name.substring(0,1).toUpperCase();
                    Log.d(Global.TAG, "inital: "+initial);

                    Bitmap b=Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(b);
                    c.drawText(initial,12,18,paint);
                   iv_contact_profile.setImageBitmap(b);
                }
            }
        }else{
            initial="N";
            Log.d(Global.TAG, "inital: "+initial);

            Bitmap b=Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            c.drawText(initial,12,18,paint);
            iv_contact_profile.setImageBitmap(b);
        }
*/


        if (editFlag || viewFlag){
            iv_contact_profile.setMonoColor(getResources().getColor(R.color.colorEdit),Color.WHITE);
            if (!contact_name.equals("")){
                iv_contact_profile.loadThumbForName( "",contact_name);
            }
        }else{
            iv_contact_profile.setMonoColor(getResources().getColor(R.color.colordeDelete),Color.WHITE);
                iv_contact_profile.loadThumbForName( "","N");
        }
    }


    private void spinnerSelection() {
        sp_select_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (group_array.length==1){
                    //Toast.makeText(AddContactActivity.this, "Groups Not Available..!", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d(Global.TAG, "Position: " + position);
                    if (sp_select_group.getSelectedItem().toString().contains("Select")) {
                        selected_group = "";
                    } else {
                        selected_group = group_array[position - 1];
                        Log.d(Global.TAG, "onItemSelected: sp_select_group " + selected_group);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
       /* spin_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (spin_tag.getSelectedItem().toString().contains("Select")) {
                    category = "";
                } else {
                    category = taglist[position-1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });*/

        sp_contact_for.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_contact_for.getSelectedItem().toString().contains("Select")) {
                    contact_for_id = "";
                } else {
                    contact_for_id = contactForList[position-1];
                    Log.d(Global.TAG, "onItemSelected:sp_contact_for "+contact_for_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        sp_contact_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_contact_status.getSelectedItem().toString().contains("Select")) {
                    contact_status_id = "";
                } else {
                    contact_status_id = contactStatusList.get(position-1).getZo_lead_status_id();
                    Log.d(Global.TAG, "onItemSelected:sp_contact_status "+contact_status_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        sp_contact_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_contact_source.getSelectedItem().toString().contains("Select")) {
                    contact_source_id = "";
                } else {

                    contact_source_id = contactSourceList.get(position-1).getZo_lead_source_id();
                    Log.d(Global.TAG, "onItemSelected:sp_contact_source "+contact_source_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    private void getAllGroups() {

        JSONObject param=new JSONObject();
        try {
            param.put("userId",user_id);
            param.put("platform",2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(Global.TAG, "getAllGroups:param "+param.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<AllGroups> call=service.getAllGroups(param.toString());
        call.enqueue(new Callback<AllGroups>() {
            @Override
            public void onResponse(Call<AllGroups> call, Response<AllGroups> response) {
                AllGroups allGroups=response.body();
                if (allGroups.isSuccess()){
                    groupList=allGroups.getResult();
                    Log.d(Global.TAG, "groupList: "+groupList.size());
                    group_array=new String[groupList.size()];
                    // group_array[0]="Select Group";
                    for(int i=0;i<groupList.size();i++){
                        group_array[i]=groupList.get(i).getGroup_name();
                    }
                    applySpinner(group_array,sp_select_group,"--Select Group--");

                    if (viewFlag){
                        sp_select_group.setEnabled(false);
                    }
//                    ArrayAdapter group_adapter=new ArrayAdapter(AddContactActivity.this,android.R.layout.simple_list_item_1,group_array);
//                   // group_adapter.add("Select Group");
//                    sp_select_group.setAdapter(group_adapter);

                    //fillGroupSpinner();
                }else{
                    group_array=new String[1];
                    applySpinner(group_array,sp_select_group,"--Select Group--");
                    Log.d(Global.TAG, "Groups Not Available..!: ");
                    sp_select_group.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<AllGroups> call, Throwable t) {
                Log.d(Global.TAG, "Groups Not Available..!: "+t);
            }
        });


    }


    private void editUserContactOnContactId() {
        Log.d(Global.TAG, "editUserContactOnContactId: ");
        final Dialog myLoader = Global.showDialog(AddContactActivityNew.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        Log.d(Global.TAG, "addUserContact: start Time:"+edt_estart_time.getText().toString()+"eventDescripId: "+eventDescripId) ;
        Log.d(Global.TAG, "addUserContact:add to cal: "+add_to_calendar+"start date:"+edt_estart_on.getText().toString());
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.update_contact(user_id,crm_flag,"2",contact_id,edt_firstName.getText().toString(),
                edt_lastName.getText().toString(),
                edt_email.getText().toString(),
                edt_email_work2.getText().toString(),
                edt_email_other.getText().toString(),
                edt_phone.getText().toString(),
                edt_phone2.getText().toString(),
                edt_phone3.getText().toString(),
                contact_for_id,
                contact_source_id,
                contact_status_id,
                et_industry.getText().toString(),
                edt_brithDate.getText().toString(),
                edt_companyName.getText().toString(),
                edt_address.getText().toString(),category,
                selected_tag,
                edt_city.getText().toString(),
                edt_state.getText().toString(),
                edt_zipcode.getText().toString(),
                edt_country.getText().toString(),
                edt_description.getText().toString(),eventDescripId,
                add_to_calendar,edt_estart_on.getText().toString(),edt_estart_time.getText().toString()
        );
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result=response.body();
                if (result.isSuccess()){
                    Toast.makeText(AddContactActivityNew.this, ""+result.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "Contact Updated Successfully...!: ");
                    //myContactFragment.getContactDetails(AddContactActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }else {
                    Toast.makeText(AddContactActivityNew.this, ""+result.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "Error in Updating Contact...!");

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "Update Contact onFailure: "+t);
                Toast.makeText(AddContactActivityNew.this, "Please Try Again..!"+t, Toast.LENGTH_LONG).show();
                finish();
            }
        });


    }

    private void fillContactEditText() {
        edt_firstName.setText(extras.getString("c_fname"));
        contact_name=extras.getString("c_fname");
        //  edt_middleName.setText(extras.getString("c_mname"));
        edt_lastName.setText(extras.getString("c_lname"));
        edt_email.setText(extras.getString("c_email"));
        edt_email_work2.setText(extras.getString("c_email1"));
        edt_email_other.setText(extras.getString("c_email2"));
        edt_phone.setText(extras.getString("phone"));
        edt_phone2.setText(extras.getString("phone1"));
        edt_phone3.setText(extras.getString("phone2"));
        //edt_anniDate.setText(extras.getString("date_of_anni"));
        edt_brithDate.setText(extras.getString("date_of_birth"));
        edt_companyName.setText(extras.getString("company_name"));

        edit_contact_for=extras.getString("contact_for");
        contact_status=extras.getString("contact_status");
        contact_source=extras.getString("contact_source");
        selected_tag=extras.getString("tag");
        //category=selected_tag;

        et_industry.setText(extras.getString("industry"));
        edt_address.setText(extras.getString("contact_address"));
        edt_city.setText(extras.getString("contact_city"));
        edt_zipcode.setText(extras.getString("contact_zip"));
        edt_state.setText(extras.getString("contact_state"));
        edt_country.setText(extras.getString("contact_country"));
        edt_description.setText(extras.getString("contact_description"));

        if (selected_tag.equals("")){
            tv_add_tag.setText("Select Tag");
        }else{
            tv_add_tag.setText(selected_tag);
        }

        String caldate=extras.getString("caldate");
        if (!caldate.equals("")){
            edt_estart_on.setText(caldate);
        }

        String calTime=extras.getString("calTime");
        if (!caldate.equals("")){
            edt_estart_time.setText(calTime);
        }

        eventDescripId=extras.getInt("eventDescripId");

        Log.d(Global.TAG, "fillContactEditText: eventDescripId "+eventDescripId);
        Log.d(Global.TAG, "fillContactEditText: calTime "+extras.getString("calTime"));
        Log.d(Global.TAG, "fillContactEditText: caldate "+extras.getString("caldate"));

        if (eventDescripId>0){
            ch_add_to_calendar.setChecked(true);
            ll_add_to_calendar.setVisibility(View.VISIBLE);
            add_to_calendar="1";
        }else{
            ch_add_to_calendar.setChecked(false);
            ll_add_to_calendar.setVisibility(View.GONE);
            add_to_calendar="0";
        }

        Log.d(Global.TAG, "Contact_For: "+edit_contact_for);
        Log.d(Global.TAG, "Contact_status: "+contact_status);
        Log.d(Global.TAG, "Contact_source: "+contact_source);

        if (viewFlag){

            String strassign_email_campaigns=extras.getString("strassign_email_campaigns");
            String strassigned_groups=extras.getString("strassigned_groups");
            String strassigned_text_campaigns=extras.getString("strassigned_text_campaigns");

            if (!strassign_email_campaigns.equals("")){
                tv_assigned_camp.setText(strassign_email_campaigns);
            }

            if (!strassigned_groups.equals("")){
                tv_assigned_groups.setText(strassigned_groups);
            }

            if (!strassigned_text_campaigns.equals("")){
                tv_assigned_text_camp.setText(strassigned_text_campaigns);
            }

        }

    }
    private void getSpinnerData() {

        final JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getSpinnerData: param:"+paramObj);
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<Contact_Spinner> call= service.get_contact_spinner(paramObj.toString());
        call.enqueue(new Callback<Contact_Spinner>() {
            @Override
            public void onResponse(Call<Contact_Spinner> call, Response<Contact_Spinner> response) {
                if (response.isSuccessful()){
                    Contact_Spinner contact_spinner=response.body();
//                contactStatusList.clear();
//                contactSourceList.clear();
                    if (contact_spinner.isSuccess()){
                        Log.d(Global.TAG, "Spinner data Get Successfully..!: ");
                        Contact_Result contact_result=contact_spinner.getResult();
                        Contact_For contact_for=contact_result.getContact_lead_prospecting_for();

                        contactStatusList=contact_result.getContact_lead_status_id();
                        contactSourceList=contact_result.getContact_lead_source_id();
                        contactTagList=contact_result.getContact_category();
                        Log.d(Global.TAG, "contactStatusList: "+contactStatusList.size());
                        Log.d(Global.TAG, "contactSourceList: "+contactSourceList.size());
                        Log.d(Global.TAG, "contactTagList: "+contactTagList.size());


                        contact_status_array=new String[contactStatusList.size()];
                        contact_source_array=new String[contactSourceList.size()];



                        for (int i=0;i<contactStatusList.size();i++){
                            contact_status_array[i]=contactStatusList.get(i).getZo_lead_status_name();
                        }
                        for (int i=0;i<contactSourceList.size();i++){
                            contact_source_array[i]=contactSourceList.get(i).getZo_lead_source_name();
                        }
                       /* for (int i=0;i<contactTagList.size();i++){
                            contact_tag_array.add(contactTagList.get(i).getUserTagName());
                        }*/


//
                        if(crm_flag.equals("1")){
                            applySpinner(contactForList,sp_contact_for,"--Select Contact For--");
                            applySpinner(contact_status_array,sp_contact_status,"--Select Contact Status--");
                            applySpinner(contact_source_array,sp_contact_source, "--Select Contact Source--");

                        }else if(crm_flag.equals("2")){
                            applySpinner(contactForList,sp_contact_for,"--Select Customer For--");
                            applySpinner(contact_status_array,sp_contact_status,"--Select Customer Status--");
                            applySpinner(contact_source_array,sp_contact_source, "--Select Customer Source--");

                        }else if(crm_flag.equals("3")){
                            applySpinner(contactForList,sp_contact_for,"--Select Prospect For--");
                            applySpinner(contact_status_array,sp_contact_status,"--Select Prospect Status--");
                            applySpinner(contact_source_array,sp_contact_source, "--Select Prospect Source--");
                        }else if(crm_flag.equals("4")){
                            applySpinner(contactForList,sp_contact_for,"--Select Recruit For--");
                            applySpinner(contact_status_array,sp_contact_status,"--Select Recruit Status--");
                            applySpinner(contact_source_array,sp_contact_source, "--Select Recruit Source--");
                        }

                        Log.d(Global.TAG, "contact_for: "+contact_for.getBusiness()+contact_for.getPolicy());
                        Log.d(Global.TAG, "contactStatusList: "+contactStatusList.size());
                        Log.d(Global.TAG, "contactSourceList: "+contactSourceList.size());

                        if (editFlag || viewFlag){
                            Log.d(Global.TAG, "editFlag contact_source: "+contact_source);
                            Log.d(Global.TAG, "contact_status: "+contact_status);
                            Log.d(Global.TAG, "contact_for: "+edit_contact_for);
                            Log.d(Global.TAG, "Tag: "+selected_tag);

                            sp_contact_status.setSelection(Integer.parseInt(contact_status));
                            sp_contact_source.setSelection(Integer.parseInt(contact_source));

                            if (edit_contact_for.equals("Business")){
                                sp_contact_for.setSelection(1);
                            }else if (edit_contact_for.equals("Policy")){
                                sp_contact_for.setSelection(2);
                            }else{
                                sp_contact_for.setSelection(0);
                            }

                            for (int i=0;i<contactTagList.size();i++){
                                if (contactTagList.get(i).getUserTagName().equals(selected_tag)){
                                    category=contactTagList.get(i).getUserTagId();
                                    Log.d(Global.TAG, "category Tag Id: "+category);
                                }
                            }

                           /* if (selected_tag.equalsIgnoreCase("Green Apple")){
                                spin_tag.setSelection(1);
                            }else if (selected_tag.equalsIgnoreCase("Red Apple")){
                                spin_tag.setSelection(2);
                            }else if (selected_tag.equalsIgnoreCase("Brown Apple")){
                                spin_tag.setSelection(3);
                            }else if (selected_tag.equalsIgnoreCase("Rotten Apple")){
                                spin_tag.setSelection(4);
                            }else{
                                spin_tag.setSelection(0);
                            }*/


                        }

                        if (viewFlag){
                            sp_contact_for.setEnabled(false);
                            sp_contact_source.setEnabled(false);
                            sp_contact_status.setEnabled(false);
                            sp_select_group.setEnabled(false);
                        }


                    }else{
                        Log.d(Global.TAG, "Error in Getting Data..!");
                    }
                }

            }

            @Override
            public void onFailure(Call<Contact_Spinner> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: "+t);
            }
        });

    }

    @Override
    public void refreashTagList() {
        getAllTagList();
        //dialog.dismiss();
    }

    private void getAllTagList() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getSpinnerData: param:"+paramObj);
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<Contact_Spinner> call= service.get_contact_spinner(paramObj.toString());
        call.enqueue(new Callback<Contact_Spinner>() {
            @Override
            public void onResponse(Call<Contact_Spinner> call, Response<Contact_Spinner> response) {
                if (response.isSuccessful()){
                    Contact_Spinner contact_spinner=response.body();
//                contactStatusList.clear();
//                contactSourceList.clear();
                    if (contact_spinner.isSuccess()){
                        Log.d(Global.TAG, "Spinner data Get Successfully..!: ");
                        Contact_Result contact_result=contact_spinner.getResult();
                        contactTagList=contact_result.getContact_category();

                        TagSpinnerAdapter adapter=new TagSpinnerAdapter(AddContactActivityNew.this,contactTagList,user_id,AddContactActivityNew.this,tv_add_tag.getText().toString());
                        lv_tag.setAdapter(adapter);

                        Contact_Tag contact_tag=new Contact_Tag();
                        contact_tag.setUserTagName("Select Tag");
                        contact_tag.setUserTagId("0");
                        contact_tag.setUserTagUserId("-1");
                        contactTagList.add(0,contact_tag);

                        Log.d(Global.TAG, "contactTagList: "+contactTagList.size());

                    }else{
                        Log.d(Global.TAG, "Error in Getting Data..!");
                    }
                }

            }

            @Override
            public void onFailure(Call<Contact_Spinner> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:getSpinnerData "+t);
            }
        });

    }



    public static class spinnerAdapter extends ArrayAdapter<String> {

        public spinnerAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            // TODO Auto-generated constructor stub
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            int count =super.getCount();
            return count>0 ? count-1 : count ;
        }
    }

    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        spinnerAdapter adapterRepeateDaily = new spinnerAdapter(AddContactActivityNew.this, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);

        /*if (isContactEditable) {//Check if Edit Contact is open then set the last selected spinner value
            if (!extras.getString("tag").equals("")){//Check if the category is not null then selected the get category spinner
                if (extras.getString("tag").equals("Green Apple")) {
                    spin_tag.setSelection(1);
                } else if (extras.getString("tag").equals("Red Apple")) {
                    spin_tag.setSelection(2);
                } else if (extras.getString("tag").equals("Brown Apple")) {
                    spin_tag.setSelection(3);
                } else if (extras.getString("tag").equals("Rotten Apple")) {
                    spin_tag.setSelection(4);
                }
            }
        }*/
    }

    //Dialog box function for open the Date Picker Intent
    protected Dialog onCreateDialog(int id){
        if(id==102){
            DatePickerDialog dpd = new DatePickerDialog(AddContactActivityNew.this, AlertDialog.THEME_HOLO_LIGHT,userDateSetListener2,year,month,day);
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            return  dpd;
        }else if(id==707){
            DatePickerDialog dpd = new DatePickerDialog(AddContactActivityNew.this, AlertDialog.THEME_HOLO_LIGHT,addtoCalendarDate,year,month,day);

            String[] sub_date_array=sub_start_date.split("-");
            String sub_year="",sub_date="",sub_month="";

            if (sub_date_array.length>2){
                sub_year=sub_date_array[0];
                sub_date=sub_date_array[2];
                sub_month=sub_date_array[1];
            }
            Calendar calendar=Calendar.getInstance();
            calendar.set(Integer.parseInt(sub_year),Integer.parseInt(sub_month),Integer.parseInt(sub_date));
            dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());


            return  dpd;
        }

        return  null;
    }
    //Manual Date Picker Fuction
    private DatePickerDialog.OnDateSetListener userDateSetListener2 = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            //edt_task_todate.setText(""+year+"-"+month+"-"+dayOfMonth);
            edt_brithDate.setText(year+"-"+((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString())));
        }
    };

    private DatePickerDialog.OnDateSetListener addtoCalendarDate = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            //edt_task_todate.setText(""+year+"-"+month+"-"+dayOfMonth);
            edt_estart_on.setText(((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString()))+"-"+year);
        }
    };




}
