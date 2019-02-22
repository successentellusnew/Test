package com.success.successEntellus.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.ScratchNotificationAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.CampaignEmailTemplates;
import com.success.successEntellus.model.GetAllScratchNotifications;
import com.success.successEntellus.model.Scratch_Note;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Scratch_NotificationList_Activity extends AppCompatActivity {
    RecyclerView rv_todays_notes,rv_upcoming_notes;
    String user_id;
    SPLib spLib;
    Button btn_sdissmiss,btn_sback;
    TextView tv_no_todays,tv_no_upcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch__notification_list_);
        getSupportActionBar().hide();
        init();
        getAllScratchNoticationsList();

        btn_sback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_sdissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getAllScratchNoticationsList() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllScratchNoticationsList: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(Scratch_NotificationList_Activity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getAllScratchNoticationsList: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllScratchNotifications> call=service.get_all_scratch_notifications(paramObj.toString());
        call.enqueue(new Callback<GetAllScratchNotifications>() {
            @Override
            public void onResponse(Call<GetAllScratchNotifications> call, Response<GetAllScratchNotifications> response) {
                GetAllScratchNotifications getAllScratchNotifications=response.body();
                if (getAllScratchNotifications!=null){
                    if (getAllScratchNotifications.isSuccess()){
                        List<Scratch_Note> todaysNoteList=getAllScratchNotifications.getTodayNotes();
                        Log.d(Global.TAG, "todaysNoteList: "+todaysNoteList.size());
                        if (todaysNoteList.size()>0){
                            rv_todays_notes.setVisibility(View.VISIBLE);
                            tv_no_todays.setVisibility(View.GONE);
                            ScratchNotificationAdapter adapter=new ScratchNotificationAdapter(Scratch_NotificationList_Activity.this,todaysNoteList);
                            rv_todays_notes.setAdapter(adapter);
                        }else{
                            rv_todays_notes.setVisibility(View.GONE);
                            tv_no_todays.setVisibility(View.VISIBLE);
                        }

                        List<Scratch_Note> upcomingNoteList=getAllScratchNotifications.getUpcomingNotes();
                        Log.d(Global.TAG, "upcomingNoteList: "+upcomingNoteList.size());
                        if (upcomingNoteList.size()>0){
                            rv_upcoming_notes.setVisibility(View.VISIBLE);
                            tv_no_upcoming.setVisibility(View.GONE);
                            ScratchNotificationAdapter adapter=new ScratchNotificationAdapter(Scratch_NotificationList_Activity.this,upcomingNoteList);
                            rv_upcoming_notes.setAdapter(adapter);
                        }else{
                            rv_upcoming_notes.setVisibility(View.GONE);
                            tv_no_upcoming.setVisibility(View.VISIBLE);
                        }
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllScratchNotifications> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:GetAllScratchNotifications "+t);
            }
        });
    }

    private void init() {
        rv_todays_notes=findViewById(R.id.rv_todays_notes);
        rv_upcoming_notes=findViewById(R.id.rv_upcoming_notes);
        btn_sdissmiss=findViewById(R.id.btn_sdissmiss);
        btn_sback=findViewById(R.id.btn_sback);
        tv_no_todays=findViewById(R.id.tv_no_todays);
        tv_no_upcoming=findViewById(R.id.tv_no_upcoming);
        spLib=new SPLib(Scratch_NotificationList_Activity.this);
        user_id=spLib.getPref(SPLib.Key.USER_ID);

        rv_todays_notes.setLayoutManager(new LinearLayoutManager(Scratch_NotificationList_Activity.this));
        rv_upcoming_notes.setLayoutManager(new LinearLayoutManager(Scratch_NotificationList_Activity.this));
    }
}
