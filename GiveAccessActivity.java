package com.success.successEntellus.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.MentorListAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.Mentor;
import com.success.successEntellus.model.MentorList;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiveAccessActivity extends AppCompatActivity implements MentorListAdapter.NotifyRefreshMenterList {
    EditText edt_access_email;
    CheckBox ch_cal, ch_tracking;
    Button btn_send_request, btn_cancel_req;
    RecyclerView rv_mentor_access;
    SPLib spLib;
    String moduleName="";
    List<Mentor> mentorList;
    Toolbar toolbar_access;
    TextView tv_toolbar_name;
    ImageButton ib_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_access);
        getSupportActionBar().hide();
        init();
        getMentorList();
        btn_cancel_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidEmailAdd(edt_access_email)){
                    if (!ch_cal.isChecked() && !ch_tracking.isChecked()){
                        Toast.makeText(GiveAccessActivity.this, "Please Select At least One Access", Toast.LENGTH_SHORT).show();
                    }else{
                        sendRequest();
                    }
                }

            }
        });
    }

    private void getMentorList() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getMentorList: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(GiveAccessActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<MentorList> call=service.getMentorList(paramObj.toString());
        call.enqueue(new Callback<MentorList>() {
            @Override
            public void onResponse(Call<MentorList> call, Response<MentorList> response) {
                MentorList mentorres=response.body();
                if (mentorres.isSuccess()){
                    mentorList=mentorres.getResult();
                    Log.d(Global.TAG, "MentorList: "+mentorList.size());
                    if (mentorList.size()>0){
                        rv_mentor_access.setVisibility(View.VISIBLE);
                        MentorListAdapter adapter=new MentorListAdapter(GiveAccessActivity.this,mentorList,false,GiveAccessActivity.this);
                        rv_mentor_access.setAdapter(adapter);
                    }

                }else{
                    rv_mentor_access.setVisibility(View.GONE);
                    Toast.makeText(GiveAccessActivity.this, "No Mentors Available", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<MentorList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:MentorList "+t);
                rv_mentor_access.setVisibility(View.GONE);
            }
        });

    }

    private void sendRequest() {
        if (ch_cal.isChecked() && ch_tracking.isChecked()){
            moduleName="1,2";
        }else if (ch_cal.isChecked()){
            moduleName="2";
        }else if (ch_tracking.isChecked()){
            moduleName="1";
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("managerEmail", edt_access_email.getText().toString());
            paramObj.put("moduleName", moduleName);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "sendRequest: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(GiveAccessActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=servive.send_access_request(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(GiveAccessActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    edt_access_email.setText("");
                    ch_cal.setChecked(true);
                    ch_tracking.setChecked(true);
                }else{
                    Toast.makeText(GiveAccessActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "sendRequest onFailure: "+t);
            }
        });

    }

    private void init() {
        spLib=new SPLib(GiveAccessActivity.this);
        edt_access_email=findViewById(R.id.edt_access_email);
        ch_cal=findViewById(R.id.ch_cal);
        ch_tracking=findViewById(R.id.ch_tracking);
        btn_send_request=findViewById(R.id.btn_send_request);
        btn_cancel_req=findViewById(R.id.btn_cancel_req);
        rv_mentor_access=findViewById(R.id.rv_mentor_access);
        rv_mentor_access.setLayoutManager(new LinearLayoutManager(GiveAccessActivity.this));

        toolbar_access=(Toolbar)findViewById(R.id.toolbar_give_access);
        ib_back=(ImageButton)findViewById(R.id.ib_gback);
        tv_toolbar_name=(TextView) findViewById(R.id.tv_mentor_gname);

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              finish();
            }
        });

    }

    @Override
    public void refreshMenterList() {
        getMentorList();
    }
}
