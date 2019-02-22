package com.success.successEntellus.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.DailyTopTenAdapter;
import com.success.successEntellus.fragment.DailyCheckListFragmentNew;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.GetDailyTopWeek;
import com.success.successEntellus.model.TopScoreRecruit;
import com.success.successEntellus.model.TopScoreRecruitList;
import com.success.successEntellus.model.TopWeek;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
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

public class WeeklyArchiveActivity extends AppCompatActivity {
    Spinner sp_select_weekly_day,sp_select_week;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
   // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    String[]  weekDayName,weekDates;
    int day,year,hour,min,monthOfYear;
    List<TopWeek> weekList;
    LinearLayout ll_select_day,ll_display_top_ten,ll_days;
    SPLib spLib;
    String selected_day;
    FrameLayout fl_no_week_selected;
    private List<String> weekListDates;
    RecyclerView rv_weekly_top10;
    ImageButton ib_weekly_back;
    HorizontalScrollView horizontalScrollView;
   TextView tv_note_score;
   List<Button> buttonList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_archive);
        getSupportActionBar().hide();
        init();
        getDailyTopWeek();

       /* InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
*/
        sp_select_week.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_select_week.getSelectedItem().toString().contains("Select")){
                    Log.d(Global.TAG, "No Week Selected: ");
                    ll_select_day.setVisibility(View.GONE);
                    fl_no_week_selected.setVisibility(View.VISIBLE);

                }else{
                    String date1=changeDateFormat(weekList.get(position-1).getStart_date());
                    String date2=changeDateFormat(weekList.get(position-1).getEnd_date());
                    Log.d(Global.TAG, "onResponse: Date1:"+date1);
                    Log.d(Global.TAG, "onResponse: Date2:"+date2);
                    getAllDatesinWeek(date1,date2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
      /*  sp_select_weekly_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_select_weekly_day.getSelectedItem().toString().contains("Select")){
                    selected_day="";
                }else{
                    selected_day=weekDates[position-1];
                    Log.d(Global.TAG, "selected_day: date: "+selected_day);
                    Log.d(Global.TAG, "selected_day: : "+sp_select_weekly_day.getSelectedItem().toString());
                    ll_display_top_ten.setVisibility(View.VISIBLE);
                    getDailyTopTen(selected_day);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        ib_weekly_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getDailyTopTen(String selected_day) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("type", "weekly");
            paramObj.put("scoreDate", selected_day);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getDailyTopTen: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(WeeklyArchiveActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<TopScoreRecruitList> call=servive.getDailyTopTenScore(paramObj.toString());
        call.enqueue(new Callback<TopScoreRecruitList>() {
            @Override
            public void onResponse(Call<TopScoreRecruitList> call, Response<TopScoreRecruitList> response) {
                TopScoreRecruitList topScoreRecruitList=response.body();
                if (topScoreRecruitList.isSuccess()){
                    List<TopScoreRecruit> dailyscoreList=topScoreRecruitList.getResult();
                    Log.d(Global.TAG, "dailyscoreList: "+dailyscoreList.size());

                    if (dailyscoreList.size()>0){
                        DailyTopTenAdapter adapter=new DailyTopTenAdapter(WeeklyArchiveActivity.this,dailyscoreList);
                        rv_weekly_top10.setAdapter(adapter);
                        tv_note_score.setVisibility(View.VISIBLE);
                        tv_note_score.setText("Note: The score is calculated on the basis of mandatory 'Critical Activities'. Exclusion for 'Target Points', 'Personal Notes'.");
                    }else{
                        Toast.makeText(WeeklyArchiveActivity.this, ""+topScoreRecruitList.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(WeeklyArchiveActivity.this, "No Details Found..", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<TopScoreRecruitList> call, Throwable t) {

                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getDailyTopTen"+t);
                Toast.makeText(WeeklyArchiveActivity.this, "Please Try Again..!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getDailyTopWeek() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getDailyTopWeek: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(WeeklyArchiveActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<GetDailyTopWeek> call=servive.getDailyTopWeek(paramObj.toString());
        call.enqueue(new Callback<GetDailyTopWeek>() {
            @Override
            public void onResponse(Call<GetDailyTopWeek> call, Response<GetDailyTopWeek> response) {
                GetDailyTopWeek getDailyTopWeek=response.body();
                weekList=getDailyTopWeek.getResult();
                Log.d(Global.TAG, " weekList onResponse: "+weekList.size());
                weekListDates=new ArrayList<>();
                if (weekList.size()>0){
                    for(int i=0;i<weekList.size();i++){
                        weekListDates.add(weekList.get(i).getStart_date()+" to "+weekList.get(i).getEnd_date());
                        /*String date1=changeDateFormat(weekList.get(i).getStart_date());
                        String date2=changeDateFormat(weekList.get(i).getEnd_date());
                        Log.d(Global.TAG, "onResponse: Date1:"+date1);
                        Log.d(Global.TAG, "onResponse: Date2:"+date2);
                        getAllDatesinWeek(date1,date2);*/
                    }
                }
                applySpinner(weekListDates,sp_select_week,"Select Week");
                int selection_current_week=weekList.size();
                Log.d(Global.TAG, "selection_current_week: "+selection_current_week);
                sp_select_week.setSelection(selection_current_week);
                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<GetDailyTopWeek> call, Throwable t) {
                Log.d(Global.TAG, "GetDailyTopWeek onFailure: "+t);
                myLoader.dismiss();
            }
        });

    }
    private void addDayName(String day_name) {
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View buttonview = inflater.inflate(R.layout.weekly_days_button_layout, null);
        final Button btn_day = (Button) buttonview.findViewById(R.id.btn_day);
        btn_day.setText(day_name);
        buttonList.add(btn_day);


        btn_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index=getViewIndex(ll_days,buttonview);
                Log.d(Global.TAG, " Day Index in View: "+index);
                selected_day=weekDates[index];
                Log.d(Global.TAG, "selected_day: date: "+selected_day);
                Log.d(Global.TAG, "selected_day: : "+btn_day.getText().toString());
                ll_display_top_ten.setVisibility(View.VISIBLE);

                for (int i=0;i<buttonList.size();i++){
                    Button btn=buttonList.get(i);
                    btn.setBackground(getResources().getDrawable(R.drawable.rounded_button_grey_border));
                    btn.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                btn_day.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                btn_day.setTextColor(getResources().getColor(R.color.colorWhite));
                getDailyTopTen(selected_day);

            }
        });
        ll_days.addView(buttonview);
    }
    private int getViewIndex (ViewGroup viewGroup, View view)
    {
        return viewGroup.indexOfChild(view);
    }
    private String changeDateFormat(String start_date) {
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1=new Date(start_date);
        String outputDateStr = outputFormat.format(date1);
        Log.d(Global.TAG, "changeDateFormat: "+outputDateStr);
        return outputDateStr;
    }

    private void init() {
      //  sp_select_weekly_day=findViewById(R.id.sp_select_weekly_day);
        sp_select_week=findViewById(R.id.sp_select_week);
        ll_select_day=findViewById(R.id.ll_select_day);
        ll_display_top_ten=findViewById(R.id.ll_display_top_ten);
        ll_days=findViewById(R.id.ll_days);
        fl_no_week_selected=findViewById(R.id.fl_no_week_selected);
        spLib=new SPLib(WeeklyArchiveActivity.this);
        rv_weekly_top10=findViewById(R.id.rv_weekly_top10);
        rv_weekly_top10.setLayoutManager(new LinearLayoutManager(WeeklyArchiveActivity.this));
        ib_weekly_back=findViewById(R.id.ib_weekly_back);
        tv_note_score=findViewById(R.id.tv_note_score);
        buttonList=new ArrayList<>();

       /* horizontalScrollView = new HorizontalScrollView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        horizontalScrollView.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_days.setOrientation(LinearLayout.HORIZONTAL);
        ll_days.setLayoutParams(linearParams);

        horizontalScrollView.addView(ll_days);*/

    }

    private void applySpinner(final List<String> taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(WeeklyArchiveActivity.this, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        //sp_name.setSelection(0);
        sp_name.setEnabled(true);

    }

    private void getAllDatesinWeek(String start_date,String end_date) {
        Log.d(Global.TAG, "getAllDatesinWeek: ");
        try {
            Date date1 = simpleDateFormat.parse(start_date);
            Date date2 = simpleDateFormat.parse(end_date);

            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            weekDates = new String[(int) differenceDates];
            weekDayName = new String[(int) differenceDates];
            ArrayList<String> dayName = new ArrayList<>((int) differenceDates);

            String currDayName = "";
            for (int i = 0; i < differenceDates; i++) {

                day = calendar.get(Calendar.DAY_OF_MONTH);
                monthOfYear = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);
                hour = calendar.get(Calendar.HOUR);
                min = calendar.get(Calendar.MINUTE);

                String month = String.valueOf(monthOfYear + 1);
                String selectedDay = String.valueOf(day);

                String CurrentTestDate = year + "-" + ((month.length() == 1 ? "0" + month.toString() : month.toString())) + "-" + ((selectedDay.toString().length() == 1 ? "0" + selectedDay.toString() : selectedDay.toString()));
                Log.d(Global.TAG, "fillSpinnerOfDay:CurrentTestDate "+CurrentTestDate);
                weekDates[i] = CurrentTestDate;
                Date curDayDate = null;
                try {
                    curDayDate = new SimpleDateFormat("yyyy-MM-dd").parse(CurrentTestDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String currDay = new SimpleDateFormat("EEE").format(curDayDate);
                weekDayName[i] = currDay;
                dayName.add(i, weekDayName[i]);
                Log.d(Global.TAG, "fillSpinnerOfDay:currDayName: "+currDayName+" currDay"+currDay+"curDayDate:"+curDayDate);

                calendar.add(Calendar.DATE, 1);
            }
            Log.d(Global.TAG, dayName.toString());
            for (int i=0;i<weekDates.length;i++){
                Log.d(Global.TAG, "Dates:"+weekDates[i]);
                Log.d(Global.TAG, "Days:"+weekDayName[i]);
            }

            ll_select_day.setVisibility(View.VISIBLE);
            fl_no_week_selected.setVisibility(View.GONE);
            //applySpinner(dayName,sp_select_weekly_day,"Select Day");
            ll_days.removeAllViews();
            buttonList.clear();
            for (int i=0;i<dayName.size();i++){
                addDayName(dayName.get(i));
            }
        } catch (ParseException e) {
            Log.d(Global.TAG, "getAllDatesinWeek: Exc:"+e);
            e.printStackTrace();
        }
    }



}
