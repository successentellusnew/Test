package com.success.successEntellus.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddCalenderEventActivity;
import com.success.successEntellus.activity.ShowEventsCalenderActivity;
import com.success.successEntellus.activity.SignUpActivity;
import com.success.successEntellus.lib.EventDecorator;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.CalenderDetails;
import com.success.successEntellus.model.EventForDay;
import com.success.successEntellus.model.GetCalenderData;
import com.success.successEntellus.model.GetCustomizeModuleList;
import com.success.successEntellus.model.GetEventsOnDay;
import com.success.successEntellus.model.SingleModule;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 6/26/2018.
 */

public class CalenderFragment extends Fragment {
    View layout;
    MaterialCalendarView calender_view;
    SPLib spLib;
    String user_id;
    List<CalenderDetails> eventList;
    List<CalendarDay> events = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    List<EventForDay> eventForDayList;
    EventDecorator eventDecorator;
    List<SingleModule> moduleList=new ArrayList<>();
    List<String> moduleIds=new ArrayList<>();
    Dialog myLoader;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_calender_layout,container,false);
        setHasOptionsMenu(true);

       /* if (!Global.isModulePresent(getActivity(),"5")){
            openDialogDisplayAlert();
        }*/

        init();
       // getModuleDetails();
        getCalenderDetails();
        calender_view.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                int month=date.getMonth()+1;
                String selected_date=date.getYear()+"-"+month+"-"+date.getDay();
                try {
                    String newDate =simpleDateFormat1.format(simpleDateFormat1.parse(selected_date));
                    Log.d(Global.TAG, "onDateSelected: newDate:"+newDate);
                    getEventListOnDate(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d(Global.TAG, "onDateSelected: Exc:newDate:"+e);
                }

            }
        });
        return layout;
    }

    private void getModuleDetails() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getModuleDetails: "+paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCustomizeModuleList> call = service.getModuleDetails(paramObj.toString());
        call.enqueue(new Callback<GetCustomizeModuleList>() {
            @Override
            public void onResponse(Call<GetCustomizeModuleList> call, Response<GetCustomizeModuleList> response) {
                GetCustomizeModuleList getCustomizeModuleList=response.body();
                if (getCustomizeModuleList!=null){
                    if (getCustomizeModuleList.isSuccess()){
                        moduleList=getCustomizeModuleList.getResult();
                        Log.d(Global.TAG, "onResponse: Module List:"+moduleList.size());
                        moduleIds.clear();
                        for(int i=0;i<moduleList.size();i++){
                            moduleIds.add(moduleList.get(i).getModuleId());
                        }
                        Log.d(Global.TAG, "moduleIds: "+moduleIds.size());
                        spLib.saveArrayList(moduleList,SPLib.Key.MODULELIST);
                        //moduleList=spLib.getArrayList(SPLib.Key.MODULELIST);
                        // Log.d(Global.TAG, "ModuleList from spLib: "+moduleList.size());
                        if (!moduleIds.contains("5")){
                            openDialogDisplayAlert();
                        }
                    }else{
                        Toast.makeText(getActivity(), ""+getCustomizeModuleList.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }

                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetCustomizeModuleList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:getModuleDetails: "+t);
            }
        });

    }
    public void openDialogDisplayAlert() {
        Button btn_upgrade_dissmiss,btn_upgrade_ok;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.upgrade_package_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        btn_upgrade_dissmiss=(Button) dialog.findViewById(R.id.btn_upgrade_dissmiss);
      //  btn_upgrade_ok=(Button) dialog.findViewById(R.id.btn_upgrade_ok);

        btn_upgrade_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });

       /* btn_upgrade_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SignUpActivity.class));
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });*/

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void getEventListOnDate(final String selected_date) {
        Log.d(Global.TAG, "getEventListOnDate: selected_date:"+selected_date);

        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<GetEventsOnDay> call=servive.get_event_list_onDate(user_id,"2",selected_date);
        call.enqueue(new Callback<GetEventsOnDay>() {
            @Override
            public void onResponse(Call<GetEventsOnDay> call, Response<GetEventsOnDay> response) {
                GetEventsOnDay getEventsOnDay=response.body();
                if (getEventsOnDay.IsSuccess){
                    eventForDayList=getEventsOnDay.getResult();
                    Log.d(Global.TAG, "eventForDayList: "+eventForDayList.size());
                    if (eventForDayList.size()>0){
                        Intent intent=new Intent(getActivity(), ShowEventsCalenderActivity.class);
                        //intent.putExtra("eventForDayList",(Serializable)eventForDayList);
                       Bundle bundle=new Bundle();
                       bundle.putBoolean("calenderFlag",true);
                       bundle.putString("date",selected_date);
                       intent.putExtras(bundle);
                       startActivityForResult(intent,200);
                    }else{
                        Toast.makeText(getActivity(), "No Events Available", Toast.LENGTH_LONG).show();
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetEventsOnDay> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:GetEventsOnDay: "+t);
                Toast.makeText(getActivity(), "No Events Available", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getCalenderDetails() {
        Log.d(Global.TAG, "getCalenderDetails: ");
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<GetCalenderData> call=servive.get_calender_details(user_id,"2");
        call.enqueue(new Callback<GetCalenderData>() {
            @Override
            public void onResponse(Call<GetCalenderData> call, Response<GetCalenderData> response) {
                GetCalenderData calenderData=response.body();
                if (calenderData.isSuccess()){
                    eventList=calenderData.getResult();
                    Log.d(Global.TAG, "eventList: "+eventList.size());
                    Log.d(Global.TAG, "events: "+events.size());

                    calender_view=layout.findViewById(R.id.calender_view);
                   /* if (events.size()>0){
                        for (int i=0;i<events.size();i++){
                            EventDecorator eventDecorator = new EventDecorator(Color.WHITE, events);
                            calender_view.addDecorator(eventDecorator);
                        }
                    }*/

                    events.clear();
                    for (int i=0;i<eventList.size();i++){
                        try {
                            String StartDate = eventList.get(i).getStart();
                            Date date = simpleDateFormat.parse(StartDate);
                            Log.d("mytag","Event Date::"+date);
                            CalendarDay day = CalendarDay.from(date);
                            events.add(day);
                        } catch (ParseException e) {
                            Log.d(Global.TAG, "CalendarDay:ParseException "+e);
                            e.printStackTrace();
                        }
                       /// new InputStreamOperation().execute();

                    }
                    eventDecorator = new EventDecorator(Color.RED, events);
                    calender_view.addDecorator(eventDecorator);
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetCalenderData> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getCalenderData "+t);
            }
        });
    }

   /* private class InputStreamOperation extends
            AsyncTask<String, Void, HashMap<String, CalendarDay>> {

        @Override
        protected void onPreExecute() {
            // show progress bar here
            Log.d(Global.TAG, "onPreExecute: ");
            myLoader = Global.showDialog(getActivity());
            myLoader.show();
            myLoader.setCanceledOnTouchOutside(true);

        }

        @Override
        protected HashMap<String, CalendarDay>
        doInBackground(String... params) {
            Log.d(Global.TAG, "doInBackground: ");
            eventDecorator = new EventDecorator(Color.RED, events);
            calender_view.addDecorator(eventDecorator);

            HashMap<String, CalendarDay> eventMap = new HashMap<String, CalendarDay>();
            for (CalendarDay day : events) {
                eventMap.put(String.valueOf(day.getDay()), day);
            }
           return eventMap;

        }

        @Override
        protected void onPostExecute(HashMap<String, CalendarDay> result) {
            // update UI here
            Log.d(Global.TAG, "onPostExecute: ");
            myLoader.dismiss();
        }
    }*/

    private void init() {
        calender_view=layout.findViewById(R.id.calender_view);
        Calendar calendar = Calendar.getInstance();
        calender_view.setDateSelected(calendar.getTime(), true);
        spLib=new SPLib(getActivity());
        user_id=spLib.getPref(SPLib.Key.USER_ID);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calender_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_event:
                Intent intent=new Intent(getActivity(),AddCalenderEventActivity.class);
                startActivityForResult(intent,10);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10){
            getCalenderDetails();
        }else if (requestCode==200){
            Log.d(Global.TAG, "onActivityResult: CalenderFragment DeleteEvent");
            calender_view.removeDecorators();
            calender_view.invalidateDecorators();
            getCalenderDetails();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Calendar");
    }
}
