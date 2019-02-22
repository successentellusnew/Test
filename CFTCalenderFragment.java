package com.success.successEntellus.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.ShowEventsCalenderActivity;
import com.success.successEntellus.lib.EventDecorator;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.CFTDashboard;
import com.success.successEntellus.model.CalenderDetails;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 6/19/2018.
 */

public class CFTCalenderFragment extends Fragment {
    View layout;
    String from_user_id;
    FrameLayout fl_no_users;
    TextView tv_no_users;
    boolean calender_access=false;
    MaterialCalendarView cft_calender_view;
    List<CalenderDetails> calenderDetails,eventList;
    SPLib spLib;
    String[] eventdates;
    List<CalendarDay> events = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    @SuppressLint("ValidFragment")
    public CFTCalenderFragment(String from_user_id, boolean calender_access) {
        this.from_user_id = from_user_id;
        this.calender_access = calender_access;
    }

    public CFTCalenderFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_cft_calender, container, false);
        init();
       // getCalenderDetails();
        cft_calender_view.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Toast.makeText(getActivity(), ""+date, Toast.LENGTH_SHORT).show();
                eventList.clear();
                for (int i=0;i<calenderDetails.size();i++){
                    try {
                        String StartDate = calenderDetails.get(i).getStart();
                        Date date1 = simpleDateFormat.parse(StartDate);
                        Log.d("mytag","Date::::"+date1);
                        CalendarDay day = CalendarDay.from(date1);
                        Log.d(Global.TAG, "onDateSelected: Day:"+day+"Date:"+date);
                        if (day.equals(date)){
                            eventList.add(calenderDetails.get(i));
                        }
                    } catch (ParseException e) {
                        Log.d(Global.TAG, "onDateSelected:ParseException "+e);
                        e.printStackTrace();
                    }
                }
                Log.d(Global.TAG, "onDateSelected: eventList"+eventList.size());
                if (eventList.size()>0){
                    Intent intent=new Intent(getActivity(), ShowEventsCalenderActivity.class);
                    intent.putExtra("eventList",(Serializable)eventList);
                    startActivity(intent);
                }

            }
        });

        return layout;
    }
    private void getCalenderDetails() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("fromUserId", from_user_id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getCalenderDetails: "+paramObj.toString());
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CFTDashboard> call=service.getCftCalenderDetails(paramObj.toString());
        call.enqueue(new Callback<CFTDashboard>() {
            @Override
            public void onResponse(Call<CFTDashboard> call, Response<CFTDashboard> response) {
                CFTDashboard cftDashboard=response.body();
                if (cftDashboard.isSuccess()){
                    fl_no_users.setVisibility(View.GONE);
                    calenderDetails=cftDashboard.getCalenderDetails();

                    Log.d(Global.TAG, "onResponse:calenderDetails "+calenderDetails.size());
                     for (int i=0;i<calenderDetails.size();i++){
                         try {
                             String StartDate = calenderDetails.get(i).getStart();
                             Date date = simpleDateFormat.parse(StartDate);
                             Log.d("mytag","Date::::"+date);
                             CalendarDay day = CalendarDay.from(date);
                             events.add(day);
                         } catch (ParseException e) {
                             Log.d(Global.TAG, "CalendarDay:ParseException "+e);
                             e.printStackTrace();
                         }
                         EventDecorator eventDecorator = new EventDecorator(Color.RED, events);
                         cft_calender_view.addDecorator(eventDecorator);

                     }
                    Log.d(Global.TAG, "onResponse:events "+events.size());

                    /*eventdates=new String[calenderDetails.size()];
                    for (int i=0;i<calenderDetails.size();i++){
                        eventdates[i]=calenderDetails.get(i).getOriginal_start();
                    }*/

                    //EventDecorates eventDecorator = new EventDecorates(Color.RED, ev);
                    //cft_calender_view.addDecorator((DayViewDecorator) eventDecorator);
                }
            }

            @Override
            public void onFailure(Call<CFTDashboard> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:getCalenderDetails "+t);
            }
        });


    }
    private void init() {
        fl_no_users = layout.findViewById(R.id.fl_no_cft_user);
        tv_no_users = layout.findViewById(R.id.tv_no_users);
        cft_calender_view = layout.findViewById(R.id.cft_calender_view);
        Calendar calendar = Calendar.getInstance();
        cft_calender_view.setDateSelected(calendar.getTime(), true);
        spLib=new SPLib(getActivity());
        eventList=new ArrayList<>();


        if (from_user_id.equals("-1")) {
            fl_no_users.setVisibility(View.VISIBLE);
        } else {
            fl_no_users.setVisibility(View.GONE);
        }
        if (calender_access){
            getCalenderDetails();
        }else if (!from_user_id.equals("-1")){
            fl_no_users.setVisibility(View.VISIBLE);
            tv_no_users.setText("Don't have access permission from the selected Recruit.");
        }
    }
}
