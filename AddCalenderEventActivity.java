package com.success.successEntellus.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCalenderEventActivity extends AppCompatActivity {
    EditText edt_event_name, edt_event_tag, edt_event_start_date, edt_event_end_date, edt_event_start_time,
            edt_event_end_time, edt_no_of_goals, edt_event_details,edt_repeat_starts_on,edt_ends_on,edt_ends_after;
    CheckBox ch_add_to_google_cal, ch_reminder;
    RadioGroup rbg_event_send_type;
    RadioButton rb_send_sms, rb_send_both, rb_send_to_email;
    Button btn_save_event, btn_event_cancel,btn_event_back,btn_edit_only_this,btn_edit_this_and_following,btn_edit_all_events;
    SPLib spLib;
    TextView tv_addEventTitle;
    TextView tv_repeat_every;
    Spinner sp_repeate_type,sp_repeate_every;
    LinearLayout ll_repeat_weekly,ll_repeat_month,ll_repeat_start_end,ll_repeat_by_layout,ll_ends_on,ll_ends_after,ll_repeat_type;
    CheckBox ch_monday,ch_tuesday,ch_wednesday,ch_thursday,ch_friday,ch_saturday,ch_sunday;
    RadioGroup rbg_repeat_ends;
    RadioButton rb_never,rb_after,rb_on;
    List<String> repeat_every=new ArrayList<>();
    List<String> repeate_types=new ArrayList<>();
    private String user_id,selected_repeate_type;
    private String reminder="",receiveOn="",selection="",intervalVal="",day="",onDate="",ends="",repeat_by="",occurence="",result="";
    String recurringDataString="";
    int calenderGoogle=0;
    Calendar cal;
    int days,month,year,hour,min;
    boolean editFlag;
    Bundle data;
    int am_pm;
    String format;
    private String edit_id;
    List<String> dayList=new ArrayList<>();
    LinearLayout ll_edit_repeat;
    String random_no="0",applyAll="1",repeatEventFlag,googleCalRecurEvent;
    private String currDayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calender_event_repeat);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        init();

        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        days = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        hour = cal.get(Calendar.HOUR);
        min =cal.get(Calendar.MINUTE);
        am_pm =cal.get(Calendar.AM_PM);
        if (am_pm==0){
            format="AM";
        }else{
            format="PM";
        }

        Date curDate = null;
        try {
            curDate = new SimpleDateFormat("MM-dd-yyyy").parse((month+1)+"-"+days+"-"+year);
            Log.d(Global.TAG, "Current Date: "+curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currDayName = new SimpleDateFormat("EEE").format(curDate);
        Log.d(Global.TAG, "Current day name: "+currDayName);

        selectCurrentDay();

        edt_event_start_date.setText((month+1)+"-"+days+"-"+year);
        edt_event_end_date.setText((month+1)+"-"+days+"-"+year);
        edt_repeat_starts_on.setText((month+1)+"-"+days+"-"+year);
        edt_event_start_time.setText(hour+":"+min+" "+format);
        edt_event_end_time.setText((hour+1)+":"+min+" "+format);

        data=getIntent().getExtras();
        if (data!=null){
            editFlag=data.getBoolean("editFlag");
           // ll_edit_repeat.setVisibility(View.VISIBLE);
            ll_repeat_type.setVisibility(View.GONE);
            fillEditDetails();
        }else{
            tv_addEventTitle.setText("Add New Event Details");
            setTitle("Add New Event Details");
            ll_repeat_type.setVisibility(View.VISIBLE);
            ll_edit_repeat.setVisibility(View.GONE);
        }

        btn_event_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ch_reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    rbg_event_send_type.setVisibility(View.VISIBLE);
                }else{
                    rbg_event_send_type.setVisibility(View.GONE);
                }
            }
        });

       /* ch_add_to_google_cal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    calenderGoogle=1;
                }else{
                    calenderGoogle=0;
                }
            }
        });
*/
        sp_repeate_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_repeate_type.getSelectedItem().toString().contains("Select")) {
                    selection = "";
                    ll_repeat_by_layout.setVisibility(View.GONE);
                } else {
                    if (sp_repeate_type.getSelectedItem().toString().equals("For 90 days")){
                        selection="daily";
                    }else{
                        selection=sp_repeate_type.getSelectedItem().toString().toLowerCase();
                    }

                    Log.d(Global.TAG, "onItemSelected: sp_repeate_type: " + selection);
                    ll_repeat_by_layout.setVisibility(View.VISIBLE);

                    if (selection.equalsIgnoreCase("Weekly")){
                        ll_repeat_weekly.setVisibility(View.VISIBLE);
                        ll_repeat_month.setVisibility(View.GONE);
                        tv_repeat_every.setText("weeks");
                        fillSpinnerwith12();
                    }else if (selection.equalsIgnoreCase("Monthly")){
                        ll_repeat_month.setVisibility(View.VISIBLE);
                        ll_repeat_weekly.setVisibility(View.GONE);
                        tv_repeat_every.setText("months");
                        repeat_by="dayMonth";
                        fillSpinnerwith1();
                    }else if (selection.equalsIgnoreCase("daily")){
                        ll_repeat_weekly.setVisibility(View.GONE);
                        ll_repeat_month.setVisibility(View.GONE);
                        tv_repeat_every.setText("days");
                        fillSpinnerwith30();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_repeate_every.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_repeate_every.getSelectedItem().toString().contains("Select")){
                    intervalVal="";
                }else{
                    intervalVal=sp_repeate_every.getSelectedItem().toString();
                }
                Log.d(Global.TAG, "intervalVal: "+intervalVal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
       /* rbg_repeat_ends.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int selected_id) {
                Log.d(Global.TAG, "onCheckedChanged: RadioGroup: ");
                if (selected_id==R.id.rb_never){
                    rb_never.setChecked(true);
                    rb_after.setChecked(false);
                    rb_on.setChecked(false);
                }else if (selected_id==R.id.rb_after){
                    rb_never.setChecked(false);
                    rb_after.setChecked(true);
                    rb_on.setChecked(false);
                }else if (selected_id==R.id.rb_on){
                    rb_never.setChecked(false);
                    rb_after.setChecked(false);
                    rb_on.setChecked(true);
                }
            }
        });
*/

        rb_never.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: rb_never");
                    rb_never.setChecked(true);
                    rb_after.setChecked(false);
                    rb_on.setChecked(false);
                }else{
                    rb_never.setChecked(false);
                }
            }
        });

        rb_after.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: rb_after");
                    rb_never.setChecked(false);
                    rb_after.setChecked(true);
                    rb_on.setChecked(false);
                    ll_ends_after.setVisibility(View.VISIBLE);
                }else{
                    rb_after.setChecked(false);
                    ll_ends_after.setVisibility(View.GONE);
                }
            }
        });

        rb_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: rb_on");
                    rb_never.setChecked(false);
                    rb_after.setChecked(false);
                    rb_on.setChecked(true);
                    ll_ends_on.setVisibility(View.VISIBLE);
                }else{
                    rb_on.setChecked(false);
                    ll_ends_on.setVisibility(View.GONE);
                }
            }
        });

        btn_save_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "onClick editFlag: "+editFlag);

                if (editFlag){
                    if (MyValidator.isValidFieldE(edt_event_name,"Enter Event Name")){
                        if(checkDates()){
                            edt_event_end_date.setError(null);
                            edt_event_end_date.clearFocus();
                            int check=checkTime();
                            if (check==1) {
                                edt_event_start_time.setError(null);
                                edt_event_start_time.clearFocus();
                                edt_event_end_time.setError(null);
                                edt_event_end_time.clearFocus();
                                editEventDetails();
                            }else if(check==-1){
                                edt_event_start_time.setError("Enter Valid Time");
                                edt_event_start_time.setFocusable(true);
                                edt_event_start_time.requestFocus();
                                Toast.makeText(AddCalenderEventActivity.this, "From Time Should be less than To Time.!", Toast.LENGTH_LONG).show();
                            }else if(check==0){
                                edt_event_end_time.setError("Enter Valid Time");
                                edt_event_end_time.setFocusable(true);
                                edt_event_end_time.requestFocus();
                                Toast.makeText(AddCalenderEventActivity.this, "To Time Should be greater than From Time.!", Toast.LENGTH_LONG).show();
                            }

                        }else{
                            edt_event_end_date.setError("Enter Valid Date");
                            edt_event_end_date.setFocusable(true);
                            edt_event_end_date.requestFocus();
                            Toast.makeText(AddCalenderEventActivity.this, "To Date Should be after From Date..!", Toast.LENGTH_LONG).show();
                        }

                    }
                }else if(!editFlag){
                    if (MyValidator.isValidFieldE(edt_event_name,"Enter Event Name")) {
                        if(checkDates()){
                            edt_event_end_date.setError(null);
                            edt_event_end_date.clearFocus();
                            int check=checkTime();
                            if (check==1) {
                                edt_event_start_time.setError(null);
                                edt_event_start_time.clearFocus();
                                if (sp_repeate_type.getSelectedItem().toString().contains("Select")){
                                    saveEvent();
                                }else{
                                    if (!sp_repeate_every.getSelectedItem().toString().contains("Select")){
                                        saveEvent();
                                    }else{
                                        MyValidator.setSpinnerError(sp_repeate_every,"Select");
                                    }
                                }
                            }else if(check==-1){
                                edt_event_start_time.setError("Enter Valid Time");
                                edt_event_start_time.setFocusable(true);
                                edt_event_start_time.requestFocus();
                                Toast.makeText(AddCalenderEventActivity.this, "From Time Should be less than To Time.!", Toast.LENGTH_LONG).show();
                            }else if(check==0){
                                edt_event_end_time.setError("Enter Valid Time");
                                edt_event_end_time.setFocusable(true);
                                edt_event_end_time.requestFocus();
                                Toast.makeText(AddCalenderEventActivity.this, "To Time Should be greater than From Time.!", Toast.LENGTH_LONG).show();
                            }



                        }else{
                            edt_event_end_date.setError("Enter Valid Date");
                            edt_event_end_date.setFocusable(true);
                            edt_event_end_date.requestFocus();
                            Toast.makeText(AddCalenderEventActivity.this, "To Date Should be greater than From Date..!", Toast.LENGTH_LONG).show();
                        }

                    }
                }

            }
        });
        edt_event_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(100);
            }
        });
        edt_event_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(101);
            }
        });

        edt_ends_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(102);
            }
        });
        edt_event_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddCalenderEventActivity.this,AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
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
                        edt_event_start_time.setText( selectedHour + ":" + selectedMinute+" "+format);
                        edt_event_end_time.setText( selectedHour + ":" + selectedMinute+" "+format);
                    }
                }, hour, min, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        edt_event_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddCalenderEventActivity.this,AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
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
                        edt_event_end_time.setText( selectedHour + ":" + selectedMinute+" "+format);
                    }
                }, hour, min, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        btn_event_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_edit_only_this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyAll="1";
                btn_edit_only_this.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                btn_edit_only_this.setTextColor(getResources().getColor(R.color.colorWhite));
                btn_edit_this_and_following.setBackground(getResources().getDrawable(R.drawable.rounded_button_grey_border));
                btn_edit_this_and_following.setTextColor(getResources().getColor(R.color.colorBlack));
                btn_edit_all_events.setBackground(getResources().getDrawable(R.drawable.rounded_button_grey_border));
                btn_edit_all_events.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });

        btn_edit_this_and_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyAll="2";
                btn_edit_this_and_following.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                btn_edit_this_and_following.setTextColor(getResources().getColor(R.color.colorWhite));
                btn_edit_only_this.setBackground(getResources().getDrawable(R.drawable.rounded_button_grey_border));
                btn_edit_only_this.setTextColor(getResources().getColor(R.color.colorBlack));
                btn_edit_all_events.setBackground(getResources().getDrawable(R.drawable.rounded_button_grey_border));
                btn_edit_all_events.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });

        btn_edit_all_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyAll="3";
                btn_edit_all_events.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                btn_edit_all_events.setTextColor(getResources().getColor(R.color.colorWhite));
                btn_edit_only_this.setBackground(getResources().getDrawable(R.drawable.rounded_button_grey_border));
                btn_edit_only_this.setTextColor(getResources().getColor(R.color.colorBlack));
                btn_edit_this_and_following.setBackground(getResources().getDrawable(R.drawable.rounded_button_grey_border));
                btn_edit_this_and_following.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });


    }

    private int checkTime() {
        try {
            Date mToday = new Date();

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            String curTime = sdf.format(mToday);
            Date start = sdf.parse(edt_event_start_time.getText().toString());
            Date end = sdf.parse(edt_event_end_time.getText().toString());
            Date userDate = sdf.parse(curTime);

            if(end.before(start))
            {
                Calendar mCal = Calendar.getInstance();
                mCal.setTime(end);
                mCal.add(Calendar.DAY_OF_YEAR, 1);
                end.setTime(mCal.getTimeInMillis());
                Log.d("mytag", "end before start: ");

                Log.d("mytag", userDate.toString());
                Log.d("mytag", "Start time:"+start.toString());
                Log.d("mytag", "End time:"+end.toString());

                return -1;
            }else if(end.equals(start)){
                return 0;
            }else{
                Log.d("mytag", userDate.toString());
                Log.d("mytag", "Start time:"+start.toString());
                Log.d("mytag", "End time:"+end.toString());

                return 1;
            }



          /*  if (userDate.after(start) && userDate.before(end)) {
                Log.d("result", "falls between start and end , go to screen 1 ");
            }
            else{
                Log.d("result", "does not fall between start and end , go to screen 2 ");
            }*/
        } catch (ParseException e) {
            // Invalid date was entered
            Log.d("mytag", "checkTime: EXC: "+e);
        }
        return -1;
    }

    private void selectCurrentDay() {
        if (!currDayName.equals("")){
            if (currDayName.equalsIgnoreCase("mon")){
                ch_monday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("tue")){
                ch_tuesday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("wed")){
                ch_wednesday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("thu")){
                ch_thursday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("fri")){
                ch_friday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("sat")){
                ch_saturday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("sun")){
                ch_sunday.setChecked(true);
            }
        }
    }

    private void fillSpinnerwith30() {
        repeat_every.clear();
        for (int i=1;i<=30;i++){
            repeat_every.add(String.valueOf(i));
        }
        applySpinner(repeat_every,sp_repeate_every,"-Select-");
    }

    private void fillSpinnerwith1() {
        repeat_every.clear();
        for (int i=1;i==1;i++){
            repeat_every.add(String.valueOf(i));
        }
        applySpinner(repeat_every,sp_repeate_every,"-Select-");
    }

    private void fillSpinnerwith12() {
        repeat_every.clear();
         for (int i=1;i<=12;i++){
            repeat_every.add(String.valueOf(i));
        }
        applySpinner(repeat_every,sp_repeate_every,"-Select-");
    }

    private boolean checkDates() {
        String start_date=edt_event_start_date.getText().toString();
        String end_date=edt_event_end_date.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat( "MM-dd-yyyy" );
        Date d1 = null,d2=null;
        try {
           d1 = sdf.parse(start_date);
           d2 = sdf.parse( end_date );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if ( compareTo( d1, d2 ) > 0 )
        {
            Log.d(Global.TAG, "Start Date is before than end Date: ");
            return false;
        }else{
            return true;
        }

    }

    private long compareTo(Date d1, Date d2) {
        return d1.getTime()-d2.getTime();
    }

    private void editEventDetails() {
        Log.d(Global.TAG, "editEventDetails: ");
        if (ch_reminder.isChecked()){
            reminder="on";
        }else{
            reminder="";
        }
        int selected_rb=rbg_event_send_type.getCheckedRadioButtonId();
        if (selected_rb==R.id.rb_send_to_email){
            receiveOn=rb_send_to_email.getText().toString();
        }else if (selected_rb==R.id.rb_send_sms){
            receiveOn=rb_send_sms.getText().toString().toUpperCase();
        }else if (selected_rb==R.id.rb_send_both){
            receiveOn=rb_send_both.getText().toString();
        }

       /* int selected_repeate_ends=rbg_repeat_ends.getCheckedRadioButtonId();
        if (selected_rb==R.id.rb_never){
            receiveOn=rb_never.getText().toString();
        }else if (selected_rb==R.id.rb_after){
            receiveOn=rb_after.getText().toString();
        }else if (selected_rb==R.id.rb_on){
            receiveOn=rb_on.getText().toString();
        }
*/
        Log.d(Global.TAG, "editEventDetails: reminder: "+reminder+" receiveOn:"+receiveOn);
        Log.d(Global.TAG, "editEventDetails: edit_id: "+edit_id);
        Log.d(Global.TAG, "editEventDetails: applyAll: "+applyAll);
        Log.d(Global.TAG, "editEventDetails: Random_no: "+random_no);
        final Dialog myLoader = Global.showDialog(AddCalenderEventActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=servive.update_event(user_id,"2",applyAll,edit_id,
                edt_event_name.getText().toString(),
                edt_event_tag.getText().toString(),
                edt_event_start_date.getText().toString(),
                edt_event_end_date.getText().toString(),
                edt_event_start_time.getText().toString(),
                edt_event_end_time.getText().toString(),
                reminder,receiveOn,
                edt_no_of_goals.getText().toString(),
                edt_event_details.getText().toString(),repeatEventFlag,googleCalRecurEvent,random_no);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    myLoader.dismiss();
                    Toast.makeText(AddCalenderEventActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    myLoader.dismiss();
                    Toast.makeText(AddCalenderEventActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure editEvent: "+t);
            }
        });
    }

    private void fillEditDetails() {
        tv_addEventTitle.setText("Edit Event Details");
        setTitle("Edit Event Details");
        edt_event_name.setText(data.getString("event_name"));
        edt_event_tag.setText(data.getString("tag_name"));
        edt_event_start_date.setText(data.getString("event_start_date"));
        edt_event_end_date.setText(data.getString("event_end_date"));
        edt_event_start_time.setText(data.getString("from_time"));
        edt_event_end_time.setText(data.getString("to_time"));
        edt_no_of_goals.setText(data.getString("goals"));
        edt_event_details.setText(data.getString("event_details"));
        String reminder=data.getString("reminder");
        String receiveOn=data.getString("receiveOn");
        edit_id=data.getString("edit_id");

        random_no=data.getString("random_no");
        repeatEventFlag=data.getString("repeat_event_flag");
        googleCalRecurEvent=data.getString("google_cal_recurring_event");
        Log.d(Global.TAG, "Random No: "+random_no);
        Log.d(Global.TAG, "repeatEventFlag : "+repeatEventFlag);
        Log.d(Global.TAG, "googleCalRecurEvent: "+googleCalRecurEvent);
        Log.d(Global.TAG, "reminder: "+reminder);
        Log.d(Global.TAG, "receiveOn: "+receiveOn);

        if (random_no.equals("0")){
            ll_edit_repeat.setVisibility(View.GONE);
        }else{
            ll_edit_repeat.setVisibility(View.VISIBLE);

            btn_edit_only_this.setBackgroundColor(getResources().getColor(R.color.colorOragne));
            btn_edit_only_this.setTextColor(getResources().getColor(R.color.colorWhite));
            btn_edit_this_and_following.setBackground(getResources().getDrawable(R.drawable.rounded_button_grey_border));
            btn_edit_this_and_following.setTextColor(getResources().getColor(R.color.colorBlack));
            btn_edit_all_events.setBackground(getResources().getDrawable(R.drawable.rounded_button_grey_border));
            btn_edit_all_events.setTextColor(getResources().getColor(R.color.colorBlack));
        }

        if (reminder.equals("on")){
            rbg_event_send_type.setVisibility(View.VISIBLE);
            ch_reminder.setChecked(true);

        }else{
            rbg_event_send_type.setVisibility(View.GONE);
        }


        if (receiveOn.equalsIgnoreCase("Email")){
            rb_send_to_email.setChecked(true);
        }else if (receiveOn.equalsIgnoreCase("Sms")){
            rb_send_sms.setChecked(true);
        }else  if (receiveOn.equalsIgnoreCase("Both")){
            rb_send_both.setChecked(true);
        }



    }

    protected Dialog onCreateDialog(int id){
        if(id==102){
            DatePickerDialog dpd = new DatePickerDialog(AddCalenderEventActivity.this,AlertDialog.THEME_HOLO_LIGHT,eventEndsOn,year,month,days);
            //dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            return  dpd;
        }

        if(id==101){
            DatePickerDialog dpd = new DatePickerDialog(AddCalenderEventActivity.this,AlertDialog.THEME_HOLO_LIGHT,eventEndDate,year,month,days);
           // dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
            return  dpd;
        }
        if(id==100){
            DatePickerDialog dpd = new DatePickerDialog(AddCalenderEventActivity.this,AlertDialog.THEME_HOLO_LIGHT,eventStartDate,year,month,days);
            //dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
            return  dpd;
        }
        return  null;
    }

    private DatePickerDialog.OnDateSetListener eventStartDate = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            //edt_task_todate.setText(""+year+"-"+month+"-"+dayOfMonth);
            edt_event_start_date.setText(((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString()))+"-"+year);
            edt_event_end_date.setText(((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString()))+"-"+year);
            edt_repeat_starts_on.setText(((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString()))+"-"+year);
        }
    };


    private DatePickerDialog.OnDateSetListener eventEndDate = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            //edt_task_todate.setText(""+year+"-"+month+"-"+dayOfMonth);
            edt_event_end_date.setText(((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString()))+"-"+year);
        }
    };

    private DatePickerDialog.OnDateSetListener eventEndsOn = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            //edt_task_todate.setText(""+year+"-"+month+"-"+dayOfMonth);
            edt_ends_on.setText(((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString()))+"-"+year);
        }
    };
    private void saveEvent() {
        if (ch_reminder.isChecked()){
            reminder="on";
        }else{
            reminder="";
        }
        int selected_rb=rbg_event_send_type.getCheckedRadioButtonId();
        if (selected_rb==R.id.rb_send_to_email){
            receiveOn=rb_send_to_email.getText().toString();
        }else if (selected_rb==R.id.rb_send_sms){
            receiveOn=rb_send_sms.getText().toString().toUpperCase();
        }else if (selected_rb==R.id.rb_send_both){
            receiveOn=rb_send_both.getText().toString();
        }

        if (rb_never.isChecked()){
            ends=rb_never.getText().toString().toLowerCase();
        }else if (rb_after.isChecked()){
            ends=rb_after.getText().toString().toLowerCase();
        }else if (rb_on.isChecked()){
            ends=rb_on.getText().toString().toLowerCase();
        }


      //  if (ends.equals("After")){
            occurence=edt_ends_after.getText().toString();
            onDate=edt_ends_on.getText().toString();
      //  }
        dayList.clear();

        if (ch_monday.isChecked()){
            dayList.add("mon");
        }
        if (ch_tuesday.isChecked()){
            dayList.add("tue");
        }
        if (ch_wednesday.isChecked()){
            dayList.add("wed");
        }
        if (ch_thursday.isChecked()){
            dayList.add("thu");
        }
        if (ch_friday.isChecked()){
            dayList.add("fri");
        }
        if (ch_saturday.isChecked()){
            dayList.add("sat");
        }
        if (ch_sunday.isChecked()){
            dayList.add("sun");
        }

        day="";
        for (String s : dayList)
        {
            day += s + ",";
        }
        if (day.endsWith(",")) {
            day = day.substring(0, day.length() - 1);
        }

        Log.d(Global.TAG, "saveEvent:Start Date: "+edt_event_start_date.getText().toString());
        Log.d(Global.TAG, "saveEvent:Event Name: "+edt_event_name.getText().toString());
        Log.d(Global.TAG, "saveEvent:selection: "+edt_event_tag.getText().toString());
        Log.d(Global.TAG, "saveEvent:End Date: "+edt_event_end_date.getText().toString());
        Log.d(Global.TAG, "saveEvent:selection: "+selection);
        Log.d(Global.TAG, "saveEvent:selection: "+selection);
        Log.d(Global.TAG, "saveEvent:selection: "+selection);
        Log.d(Global.TAG, "saveEvent:occurence: "+occurence);
        Log.d(Global.TAG, "saveEvent:intervalVal: "+intervalVal);
        Log.d(Global.TAG, "saveEvent:ends: "+ends);
        Log.d(Global.TAG, "saveEvent:DaySrting: "+day);
        Log.d(Global.TAG, "saveEvent:repeat_by: "+repeat_by);
        Log.d(Global.TAG, "saveEvent:onDate: "+onDate);
        Log.d(Global.TAG, "saveEvent: reminder: "+reminder+" receiveOn:"+receiveOn);
        Log.d(Global.TAG, "saveEvent: Tag:"+edt_event_tag.getText().toString());
        final Dialog myLoader = Global.showDialog(AddCalenderEventActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=servive.add_new_event_calender(user_id,
                edt_event_name.getText().toString(),
                edt_event_tag.getText().toString(),
                edt_event_start_date.getText().toString(),
                edt_event_end_date.getText().toString(),
                edt_event_start_time.getText().toString(),
                edt_event_end_time.getText().toString(),
                reminder,receiveOn,
                edt_no_of_goals.getText().toString(),
                edt_event_details.getText().toString(),selection,intervalVal,
                ends,repeat_by,onDate,occurence,day,recurringDataString,calenderGoogle);
            call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(AddCalenderEventActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(AddCalenderEventActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: addEEvent "+t);
            }
        });

    }

    private void init() {
        tv_addEventTitle=(TextView) findViewById(R.id.tv_addEventTitle);
        edt_event_name=(EditText)findViewById(R.id.edt_event_name);
        edt_event_tag=(EditText)findViewById(R.id.edt_event_tag);
        edt_event_start_date=(EditText)findViewById(R.id.edt_event_start_date);
        edt_event_end_date=(EditText)findViewById(R.id.edt_event_end_date);
        edt_event_start_time=(EditText)findViewById(R.id.edt_event_start_time);
        edt_event_end_time=(EditText)findViewById(R.id.edt_event_end_time);
        edt_no_of_goals=(EditText)findViewById(R.id.edt_no_of_goals);
        edt_event_details=(EditText)findViewById(R.id.edt_event_details);
        btn_event_back=(Button) findViewById(R.id.btn_event_back);
        tv_repeat_every=(TextView) findViewById(R.id.tv_repeat_every);
        edt_repeat_starts_on=(EditText) findViewById(R.id.edt_repeat_starts_on);
        edt_ends_after=(EditText) findViewById(R.id.edt_ends_after);
        edt_ends_on=(EditText) findViewById(R.id.edt_ends_on);
        sp_repeate_type=(Spinner) findViewById(R.id.sp_repeate_type);
        sp_repeate_every=(Spinner) findViewById(R.id.sp_repeate_every);
        ll_repeat_weekly=(LinearLayout) findViewById(R.id.ll_repeat_weekly);
        ll_repeat_month=(LinearLayout) findViewById(R.id.ll_repeat_month);
        ll_repeat_start_end=(LinearLayout) findViewById(R.id.ll_repeat_start_end);
        ll_repeat_by_layout=(LinearLayout) findViewById(R.id.ll_repeat_by_layout);
        ll_ends_on=(LinearLayout) findViewById(R.id.ll_ends_on);
        ll_ends_after=(LinearLayout) findViewById(R.id.ll_ends_after);
        ll_repeat_type=(LinearLayout) findViewById(R.id.ll_repeat_type);

        ll_edit_repeat=(LinearLayout) findViewById(R.id.ll_edit_repeat);
        btn_edit_only_this=(Button) findViewById(R.id.btn_edit_only_this);
        btn_edit_this_and_following=(Button) findViewById(R.id.btn_edit_this_and_following);
        btn_edit_all_events=(Button) findViewById(R.id.btn_edit_all_events);

//        sp_repeate_type=(Spinner) findViewById(R.id.sp_repeate_type);
//        ch_add_to_google_cal=(CheckBox) findViewById(R.id.ch_add_to_google_cal);
        ch_reminder=(CheckBox) findViewById(R.id.ch_reminder);
      //  ch_add_to_google_cal=(CheckBox) findViewById(R.id.ch_add_to_google_cal);

        rbg_event_send_type=(RadioGroup) findViewById(R.id.rbg_event_send_type);
        rb_send_sms=(RadioButton) findViewById(R.id.rb_send_sms);
        rb_send_both=(RadioButton) findViewById(R.id.rb_send_both);
        rb_send_to_email=(RadioButton) findViewById(R.id.rb_send_to_email);

        rbg_repeat_ends=(RadioGroup) findViewById(R.id.rbg_repeat_ends);
        rb_never=(RadioButton) findViewById(R.id.rb_never);
        rb_after=(RadioButton) findViewById(R.id.rb_after);
        rb_on=(RadioButton) findViewById(R.id.rb_on);

        btn_save_event=(Button) findViewById(R.id.btn_save_event);
        btn_event_cancel=(Button) findViewById(R.id.btn_event_cancel);
        repeate_types.add("Weekly");
        repeate_types.add("Monthly");
        repeate_types.add("For 90 days");

        ch_monday=(CheckBox) findViewById(R.id.ch_monday);
        ch_tuesday=(CheckBox) findViewById(R.id.ch_tuesday);
        ch_wednesday=(CheckBox) findViewById(R.id.ch_wednesday);
        ch_thursday=(CheckBox) findViewById(R.id.ch_thursday);
        ch_friday=(CheckBox) findViewById(R.id.ch_friday);
        ch_saturday=(CheckBox) findViewById(R.id.ch_saturday);
        ch_sunday=(CheckBox) findViewById(R.id.ch_sunday);

        applySpinner(repeate_types,sp_repeate_type,"--Select Repeat Type--");
        spLib=new SPLib(AddCalenderEventActivity.this);
        user_id=spLib.getPref(SPLib.Key.USER_ID);


       /* for (int i=1;i<=12;i++){
            repeat_every.add(String.valueOf(i));
        }
        applySpinner(repeat_every,sp_repeate_every,"--Select Repeat--");*/
    }

    private void applySpinner(final List<String> taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(AddCalenderEventActivity.this, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }

}
