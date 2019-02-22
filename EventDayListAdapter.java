package com.success.successEntellus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.fragment.TopScoreListAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.EventForDay;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.EventDayHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 6/27/2018.
 */

public class EventDayListAdapter extends RecyclerView.Adapter<EventDayHolder> {
    ShowEventsCalenderActivity context;
    List<EventForDay> eventdayList;
    View layout;
    LayoutInflater inflater;
    SPLib spLib;
    String deleteFlag;
    SimpleDateFormat date_format = new SimpleDateFormat("MM/dd/yyyy");
    SimpleDateFormat time_format = new SimpleDateFormat("HH:MM");

    public interface RefreshEventList {
        void refresh();
    }
    RefreshEventList mCallBack;
    private TopScoreListAdapter.ResponseToFragment mCallback;
    public EventDayListAdapter(ShowEventsCalenderActivity showEventsCalenderActivity, List<EventForDay> eventdayList, RefreshEventList mCallBack) {
        this.context=showEventsCalenderActivity;
        this.eventdayList=eventdayList;
        this.mCallBack=mCallBack;
    }

    @Override
    public EventDayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        spLib=new SPLib(context);
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.event_of_day_row_layout,parent,false);
        EventDayHolder eventDayHolder=new EventDayHolder(layout);
        return eventDayHolder;
    }

    @Override
    public void onBindViewHolder(EventDayHolder holder, final int position) {
        Log.d(Global.TAG, "Events List Edit id: "+eventdayList.get(position).getEdit_id());
        Log.d(Global.TAG, "Events List User id: "+spLib.getPref(SPLib.Key.USER_ID));

        String from_date=eventdayList.get(position).getTask_fromdt();
        String to_date=eventdayList.get(position).getTask_todt();

        String from_time = from_date.split("\\s")[1].split("\\.")[0];
        String to_time = to_date.split("\\s")[1].split("\\.")[0];
        Log.d(Global.TAG, "from_time:"+from_time);
        Log.d(Global.TAG, "to_time:"+to_time);

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(from_time);
            final Date dateObj1 = sdf.parse(to_time);
            Log.d(Global.TAG, "dateObj: "+dateObj);
            Log.d(Global.TAG, "dateObj1: "+dateObj1);

            from_time=new SimpleDateFormat("hh:mm aa").format(dateObj);
            to_time=new SimpleDateFormat("hh:mm aa").format(dateObj1);
            Log.d(Global.TAG, "new From Time: "+from_time);
            Log.d(Global.TAG, "new To Time: "+to_time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }



       /* String from_time = String.valueOf(from_date.getHours()+":"+from_date.getMinutes());
        String to_time = String.valueOf(to_date.getHours()+":"+to_date.getMinutes());*/

//        String newFromDate = from_date.getMonth()+"-"+from_date.getDate()+"-"+from_date.getYear();
//        String newtoDate = from_date.getMonth()+"-"+from_date.getDate()+"-"+from_date.getYear();

        holder.tv_event_name.setText(eventdayList.get(position).getGoal_name());
        holder.tv_event_details.setText(eventdayList.get(position).getTask_details());
        holder.tv_event_start_time.setText(from_time);
        holder.tv_event_end_time.setText(to_time);

        holder.ib_edit_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    editEventDetails(position);
            }
        });

        holder.ib_delete_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!eventdayList.get(position).getRandomNumber().equals("0")){
                    showDeleteOptions(position);
                }else{
                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteEvent(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }

            }
        });

    }

    private void showDeleteOptions(final int position) {

        final CharSequence[] options = {"Only This Event", "This & Following Events","All Events","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Recurring Event!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Only This Event")) {
                    deleteFlag="1";
                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteRecuuringEvent(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                } else if (options[item].equals("This & Following Events")) {
                    deleteFlag="2";
                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteRecuuringEvent(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }else if (options[item].equals("All Events")) {
                    deleteFlag="3";
                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteRecuuringEvent(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void deleteRecuuringEvent(int position) {
        Log.d(Global.TAG, "deleteRecuuringEvent: edit_id: "+eventdayList.get(position).getEdit_id()+" user_id:"+spLib.getPref(SPLib.Key.USER_ID));
        Log.d(Global.TAG, "deleteRecuuringEvent deleteFlag: "+deleteFlag+" googleEventFlag:"+eventdayList.get(position).getGoogleCalEventFlag());
        Log.d(Global.TAG, "deleteRecuuringEvent recurringEventId: "+eventdayList.get(position).getGoogleCalRecurEventId()+" randomNumberValue:"+eventdayList.get(position).getRandomNumber());
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_recurring_event(spLib.getPref(SPLib.Key.USER_ID),"2",deleteFlag,
                eventdayList.get(position).getEdit_id(),eventdayList.get(position).getGoogleCalEventFlag(),
                eventdayList.get(position).getGoogleCalRecurEventId(),eventdayList.get(position).getRandomNumber());

        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    mCallBack.refresh();
                }else {
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:deleteRecuuringEvent "+t);
            }
        });
    }

    private void deleteEvent(final int position) {
        Log.d(Global.TAG, "deleteEvent: edit_id: "+eventdayList.get(position).getEdit_id()+" user_id:"+spLib.getPref(SPLib.Key.USER_ID));
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_event(spLib.getPref(SPLib.Key.USER_ID),eventdayList.get(position).getEdit_id());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    //notifyItemRemoved(position);
                    mCallBack.refresh();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: deleteEvent:"+t);
            }
        });
    }

    private void editEventDetails(int position)  {
        Intent intent=new Intent(context,AddCalenderEventActivity.class);
        Bundle bundle=new Bundle();
        String from_date=eventdayList.get(position).getTask_fromdt();
        String to_date=eventdayList.get(position).getTask_todt();

        Log.d(Global.TAG, "editEventDetails:from_date "+from_date);
        Log.d(Global.TAG, "editEventDetails:To_date "+to_date);
        String newFromDate="",newtoDate="";
        newFromDate=from_date.substring(0,from_date.indexOf(" "));
        newtoDate=to_date.substring(0,to_date.indexOf(" "));

        String[] start_date=newFromDate.split("-");
        String year=start_date[0];
        String month=start_date[1];
        String day=start_date[2];

        String[] end_date=newtoDate.split("-");
        String year1=start_date[0];
        String month1=start_date[1];
        String day1=start_date[2];

        newFromDate=month+"-"+day+"-"+year;
        newtoDate=month1+"-"+day1+"-"+year1;

        Log.d(Global.TAG, "editEventDetails: newFromDate:"+newFromDate+" newtoDate:"+newtoDate);

      /*  try {
            Date f1 = date_format.parse(from_date);
            Date f2=date_format.parse(to_date);

            Log.d(Global.TAG, "editEventDetails:f1 "+f1);
            Log.d(Global.TAG, "editEventDetails:f2 "+f2);

            int day=f1.getDay();
            int month=f1.getMonth();
            int year=f1.getYear();
            Log.d(Global.TAG, "editEventDetails: day-month-year"+day+"-"+month+"-"+year);


            newFromDate =date_format.format(date_format.parse(from_date));
            newtoDate =date_format.format(date_format.parse(to_date));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(Global.TAG, "editEventDetails: "+e);
        }*/

        String from_time = from_date.split("\\s")[1].split("\\.")[0];
        String to_time = to_date.split("\\s")[1].split("\\.")[0];

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(from_time);
            final Date dateObj1 = sdf.parse(to_time);
            Log.d(Global.TAG, "dateObj: "+dateObj);
            Log.d(Global.TAG, "dateObj1: "+dateObj1);

            from_time=new SimpleDateFormat("hh:mm aa").format(dateObj);
            to_time=new SimpleDateFormat("hh:mm aa").format(dateObj1);
            Log.d(Global.TAG, "new From Time: "+from_time);
            Log.d(Global.TAG, "new To Time: "+to_time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        bundle.putBoolean("editFlag",true);
        bundle.putString("edit_id",eventdayList.get(position).getEdit_id());
        bundle.putString("event_name",eventdayList.get(position).getGoal_name());
        bundle.putString("tag_name",eventdayList.get(position).getTag());
        bundle.putString("event_start_date",newFromDate);
        bundle.putString("event_end_date",newtoDate);
        bundle.putString("from_time",from_time);
        bundle.putString("to_time",to_time);
        bundle.putString("reminder",eventdayList.get(position).getReminder());
        bundle.putString("receiveOn",eventdayList.get(position).getReciveOn());
        bundle.putString("goals",eventdayList.get(position).getCompletedGoals());
        bundle.putString("event_details",eventdayList.get(position).getTask_details());
        bundle.putString("random_no",eventdayList.get(position).getRandomNumber());
        bundle.putString("repeat_event_flag",eventdayList.get(position).getGoogleCalEventFlag());
        bundle.putString("google_cal_recurring_event",eventdayList.get(position).getGoogleCalRecurEventId());
        Log.d(Global.TAG, "editEventDetails: reminder:"+eventdayList.get(position).getReminder());
        Log.d(Global.TAG, "editEventDetails: receiveOn:"+eventdayList.get(position).getReciveOn());
        intent.putExtras(bundle);
        context.startActivityForResult(intent,20);
    }

    @Override
    public int getItemCount() {
        return eventdayList.size();
    }
}
