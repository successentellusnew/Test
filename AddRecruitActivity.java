package com.success.successEntellus.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRecruitActivity extends AppCompatActivity {
EditText edt_rfirstName,edt_rlastName,edt_remail,edt_rmobile,edt_rbrithDate,edt_joining_date,edt_nlgagent_id,edt_pfaagent_id;
    Button btn_recruit_cancel,btn_edit_recruit,btn_add_recruit,btn_radddissmiss,btn_addrecruit_back;
    String user_id,contact_id;
    SPLib spLib;
    boolean editFlag=false;
    Calendar cal;
    int day,month,year;
    TextView tv_addRecruitsTitle;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recruit);
        getSupportActionBar().hide();
        init();

    cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
    day = cal.get(Calendar.DAY_OF_MONTH);
    month = cal.get(Calendar.MONTH);
    year = cal.get(Calendar.YEAR);
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            contact_id=bundle.getString("contact_id");
            editFlag=bundle.getBoolean("editFlag");
            edt_rfirstName.setText(bundle.getString("contact_fname"));
            edt_rlastName.setText(bundle.getString("contact_lname"));
            edt_remail.setText(bundle.getString("contact_email"));
            edt_rmobile.setText(bundle.getString("contact_mobile"));
            edt_nlgagent_id.setText(bundle.getString("nlgagent_id"));
            edt_pfaagent_id.setText(bundle.getString("pfaagent_id"));
            edt_rbrithDate.setText(bundle.getString("birthdate"));
            edt_joining_date.setText(bundle.getString("joining_date"));
            btn_edit_recruit.setVisibility(View.VISIBLE);
            btn_add_recruit.setVisibility(View.GONE);
        }

        if (editFlag){
            tv_addRecruitsTitle.setText("Edit Recruit Details");
        }else{
            tv_addRecruitsTitle.setText("Add new Recruit Details");
        }
        btn_add_recruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (MyValidator.isValidFieldE(edt_rfirstName,"Enter First Name") || !edt_rlastName.getText().toString().equals("")) {
                    if (!edt_remail.getText().toString().equals("") || !edt_rmobile.getText().toString().equals("")) {
                        if (MyValidator.isValidEmail(edt_remail)) {
                           addRecruit();
                        }
                    }
                }*/
                if (MyValidator.isValidFieldE(edt_rfirstName,"Enter First Name"))
                {
                    if (MyValidator.isValidMobile(edt_rmobile)){
                        if (MyValidator.isValidEmail(edt_remail)){
                            addRecruit();
                        }
                    }

                }
            }
        });

        btn_recruit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    btn_radddissmiss.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });
    btn_addrecruit_back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });

    btn_edit_recruit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editRecruit();
        }
    });

    edt_rbrithDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDialog(101);
        }
    });
    edt_joining_date.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDialog(102);
        }
    });

    }
    protected Dialog onCreateDialog(int id) {
        if (id == 102) {
            DatePickerDialog dpd = new DatePickerDialog(AddRecruitActivity.this, AlertDialog.THEME_HOLO_LIGHT, userDateSetListener2, year, month, day);
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dpd;
        }
        if (id == 101) {
            DatePickerDialog dpd = new DatePickerDialog(AddRecruitActivity.this, AlertDialog.THEME_HOLO_LIGHT, userDateSetListenerAnniversory, year, month, day);
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dpd;
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener userDateSetListener2 = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            //edt_task_todate.setText(""+year+"-"+month+"-"+dayOfMonth);
            edt_joining_date.setText(year+"-"+((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString())));
        }
    };

    //Date picker for anniversory
    private DatePickerDialog.OnDateSetListener userDateSetListenerAnniversory = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            //edt_task_todate.setText(""+year+"-"+month+"-"+dayOfMonth);
            edt_rbrithDate.setText(year+"-"+((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString())));
        }
    };

    private void editRecruit() {
        final Dialog myLoader = Global.showDialog(AddRecruitActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=servive.update_recruit(user_id,"4","2",contact_id,
                edt_rfirstName.getText().toString(),
                edt_rlastName.getText().toString(),
                edt_remail.getText().toString(),
                edt_rmobile.getText().toString(),
                edt_rbrithDate.getText().toString(),
                edt_joining_date.getText().toString(),
                edt_nlgagent_id.getText().toString(),
                edt_pfaagent_id.getText().toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(AddRecruitActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(AddRecruitActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG,"editRecruit: onFailure:"+t);
            }
        });
    }

    private void addRecruit() {
        final Dialog myLoader = Global.showDialog(AddRecruitActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=servive.add_recruit(user_id,"2","4",edt_rfirstName.getText().toString(),
                edt_rlastName.getText().toString(),
                edt_remail.getText().toString(),
                edt_rmobile.getText().toString(),
                edt_rbrithDate.getText().toString(),
                edt_joining_date.getText().toString(),
                edt_nlgagent_id.getText().toString(),
                edt_pfaagent_id.getText().toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(AddRecruitActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(AddRecruitActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: addRecruit "+t);
            }
        });

    }

    private void init() {

        tv_addRecruitsTitle=(TextView) findViewById(R.id.tv_addRecruitsTitle);
        edt_rfirstName=(EditText)findViewById(R.id.edt_rfirstName);
        edt_rlastName=(EditText)findViewById(R.id.edt_rlastName);
        edt_remail=(EditText)findViewById(R.id.edt_remail);
        edt_rmobile=(EditText)findViewById(R.id.edt_rmobile);
        edt_rbrithDate=(EditText)findViewById(R.id.edt_rbrithDate);
        edt_joining_date=(EditText)findViewById(R.id.edt_joining_date);
        edt_nlgagent_id=(EditText)findViewById(R.id.edt_nlgagent_id);
        edt_pfaagent_id=(EditText)findViewById(R.id.edt_pfaagent_id);

        btn_recruit_cancel=(Button) findViewById(R.id.btn_recruit_cancel);
        btn_radddissmiss=(Button) findViewById(R.id.btn_radddissmiss);
        btn_addrecruit_back=(Button) findViewById(R.id.btn_addrecruit_back);
        btn_edit_recruit=(Button)findViewById(R.id.btn_edit_recruit);
        btn_add_recruit=(Button)findViewById(R.id.btn_add_recruit);
        spLib=new SPLib(AddRecruitActivity.this);
        user_id=spLib.getPref(SPLib.Key.USER_ID);
    }


}
