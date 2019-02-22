package com.success.successEntellus.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.EventListAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.CalenderDetails;
import com.success.successEntellus.model.EventForDay;
import com.success.successEntellus.model.GetEventsOnDay;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowEventsCalenderActivity extends AppCompatActivity implements EventDayListAdapter.RefreshEventList{
    List<CalenderDetails> eventList;
    List<EventForDay> eventdayList;
    RecyclerView rv_calender_events;
    boolean calenderFlag=false;
    Toolbar toolbar_event;
    ImageButton ib_event_back;
    SPLib spLib;
    String selected_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events_calender);
        getSupportActionBar().hide();

        toolbar_event=findViewById(R.id.toolbar_events);
        spLib=new SPLib(ShowEventsCalenderActivity.this);
        ib_event_back=findViewById(R.id.ib_event_back);
        rv_calender_events=findViewById(R.id.rv_calender_events);
        rv_calender_events.setLayoutManager(new LinearLayoutManager(ShowEventsCalenderActivity.this));
        eventdayList=new ArrayList<>();
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            if (bundle.containsKey("calenderFlag")){
                calenderFlag=bundle.getBoolean("calenderFlag");
            }
            if (bundle.containsKey("date")){
                selected_date=bundle.getString("date");
            }
        }
        if (getIntent().hasExtra("eventList")){
            eventList = (List<CalenderDetails>)getIntent().getSerializableExtra("eventList");
            Log.d(Global.TAG, "eventList: ShowEventsCalenderActivity: "+eventList.size());
            EventListAdapter adapter=new EventListAdapter(ShowEventsCalenderActivity.this,eventList);
            rv_calender_events.setAdapter(adapter);
        }

        ib_event_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (calenderFlag){
            getEventListOnDate();
        }


       /* if (getIntent().hasExtra("calenderFlag")){
            calenderFlag=getIntent().getBooleanExtra("calenderFlag");
            //eventdayList = (List<EventForDay>)getIntent().getSerializableExtra("eventForDayList");
            Log.d(Global.TAG, "eventForDayList: ShowEventsCalenderActivity: "+eventdayList.size());
            EventDayListAdapter adapter=new EventDayListAdapter(ShowEventsCalenderActivity.this,eventdayList);
            rv_calender_events.setAdapter(adapter);
        }*/

    }
    private void getEventListOnDate() {
        Log.d(Global.TAG, "getEventListOnDate: selected_date:"+selected_date);

        final Dialog myLoader = Global.showDialog(ShowEventsCalenderActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<GetEventsOnDay> call=servive.get_event_list_onDate(spLib.getPref(SPLib.Key.USER_ID),"2",selected_date);
        call.enqueue(new Callback<GetEventsOnDay>() {
            @Override
            public void onResponse(Call<GetEventsOnDay> call, Response<GetEventsOnDay> response) {
                GetEventsOnDay getEventsOnDay=response.body();
                eventdayList.clear();
                if (getEventsOnDay.IsSuccess){
                    eventdayList=getEventsOnDay.getResult();
                    Log.d(Global.TAG, "eventForDayList: "+eventdayList.size());
                    EventDayListAdapter adapter=new EventDayListAdapter(ShowEventsCalenderActivity.this,eventdayList,ShowEventsCalenderActivity.this);
                    adapter.notifyDataSetChanged();
                    rv_calender_events.setAdapter(adapter);
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetEventsOnDay> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:GetEventsOnDay: "+t);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==20){
            getEventListOnDate();
        }
    }

    @Override
    public void refresh() {
        getEventListOnDate();
    }
}
