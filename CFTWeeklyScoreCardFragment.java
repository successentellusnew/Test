package com.success.successEntellus.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.NetworkCheckActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.CFTDashboard;
import com.success.successEntellus.model.SingleGoal;
import com.success.successEntellus.model.SingleGoal1;
import com.success.successEntellus.model.TrackingDetails;
import com.success.successEntellus.model.WeekDetails;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 6/19/2018.
 */

public class CFTWeeklyScoreCardFragment extends Fragment {
    View layout;
    SPLib spLib;
    public static String user_id,from_user_id="-1";
    Spinner sp_cft_weeks;
    TableLayout mainTrackingtable;
    public TableRow tr,tr1,tr2;
    ArrayList<SingleGoal1> userWeekGoalDetails;
    FrameLayout fl_no_cft_user;
    TextView goal_name, tv_my_goal,tv_completed_goal,tv_status,tv_my_score,tv_goal_remaining,tv_week_track_heading;
    Bundle bundleEffect;
    private boolean selected;
    String[] weekName,weekId,weekEndDate,weekStartDate,listdata;
    Float totalUserScor=0f;
    TrackingDetails trackingDetails;
    List<WeekDetails> weekData;
    List<SingleGoal1> goal_details;
    TextView tv_no_users;
    boolean tracking_access=false;
    CFTDashboard weekDetails;
    public double total_score_week=0;

    @SuppressLint("ValidFragment")
    public CFTWeeklyScoreCardFragment(String from_user_id,boolean tracking_access) {
        this.from_user_id=from_user_id;
        this.tracking_access=tracking_access;
    }

    public CFTWeeklyScoreCardFragment( ) {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_cft_weekly_scorecard,container,false);
        init();
        Log.d(Global.TAG, "tracking_accesstracking_access: "+tracking_access);

            if (from_user_id.equals("-1")){
                fl_no_cft_user.setVisibility(View.VISIBLE);
            }else{
                fl_no_cft_user.setVisibility(View.GONE);
                if (tracking_access){
                    getCftCheckList(1);
                }else if (!from_user_id.equals("-1")){
                    fl_no_cft_user.setVisibility(View.VISIBLE);
                    tv_no_users.setText("Don't have access permission from the selected Recruit.");
                }
            }





        return layout;
    }

    private void init() {
        spLib=new SPLib(getActivity());
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        sp_cft_weeks= (Spinner)layout.findViewById(R.id.sp_cft_weeks);
        tv_no_users= (TextView) layout.findViewById(R.id.tv_no_users);
        mainTrackingtable = (TableLayout) layout.findViewById(R.id.mainTrackingtablecft);
        mainTrackingtable.setVisibility(View.VISIBLE);
        weekData = new ArrayList<>();
        goal_details = new ArrayList<>();
        userWeekGoalDetails= new ArrayList<>();
        tv_week_track_heading=(TextView)layout.findViewById(R.id.tv_week_track_heading);
        fl_no_cft_user=layout.findViewById(R.id.fl_no_cft_user);


    }

    private void getCftCheckList(final int checkForFillSpinner) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("fromUserId", from_user_id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getCftCheckList: "+paramObj.toString());
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CFTDashboard> call=service.getCFTCheckList(paramObj.toString());
        call.enqueue(new Callback<CFTDashboard>() {
            @Override
            public void onResponse(Call<CFTDashboard> call, Response<CFTDashboard> response) {
                CFTDashboard cftDashboard=response.body();
                if (cftDashboard.isSuccess()){
                    fl_no_cft_user.setVisibility(View.GONE);
                    trackingDetails=cftDashboard.getTrackingDetails();
                    total_score_week=trackingDetails.getTotal_score();
                    Log.d(Global.TAG, "Total Score: "+total_score_week);
                    weekData=trackingDetails.getWeek();
                    goal_details=trackingDetails.getGoal_details();
                    Log.d(Global.TAG, "week List: "+weekData.size());
                    Log.d(Global.TAG, "goal_details List: "+goal_details.size());

                    if (checkForFillSpinner==1) {
                        if (tracking_access){
                            fillWeekSpinner();
                        }

                    }
                    if (tracking_access){
                        addCurrenData();
                    }

                }

            }

            @Override
            public void onFailure(Call<CFTDashboard> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:getCftCheckList "+t);
            }
        });

    }

    private void addCurrenData() {
        mainTrackingtable.removeAllViews();
        if (getActivity()!=null){
            if (tracking_access){
                addHeaders();
            }

        }

        totalUserScor=0f;
        Float tempCal;
        for (int i = 0; i < goal_details.size(); i++) {
            /** Create a TableRow dynamically **/

            try {
                tr = new TableRow(getActivity());
                tr.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tr.setGravity(View.TEXT_ALIGNMENT_CENTER);


                if ((i % 2) == 0) {
                    tr.setBackgroundResource(R.color.colorWhite);
                } else {
                    tr.setBackgroundResource(R.color.colorList);
                }

                /** Creating a TextView to add to the row **/

                /** Creating another textview **/
                goal_name = new TextView(getActivity());
                goal_name.setText(goal_details.get(i).getGoal_name());
                goal_name.setTextColor(Color.BLACK);
                goal_name.setPadding(20, 20, 20, 20);
                goal_name.setGravity(Gravity.LEFT);
                goal_name.setTypeface(Typeface.MONOSPACE);
                tr.addView(goal_name); // Adding textView to tablerow.

                tv_my_goal = new TextView(getActivity());
                tv_my_goal.setText(goal_details.get(i).getUser_done_goal_count());
                tv_my_goal.setTextColor(Color.BLACK);
                tv_my_goal.setTypeface(Typeface.SANS_SERIF);
                tv_my_goal.setBackgroundResource(R.drawable.row_border);
                tv_my_goal.setPadding(22, 22, 22, 22);
                tv_my_goal.setGravity(Gravity.LEFT);
                tr.addView(tv_my_goal);  // Adding textView to tablerow.

                /** Creating another textview **/
                tv_completed_goal = new TextView(getActivity());
                tv_completed_goal.setText("" + goal_details.get(i).getRemaining_goals());
                tv_completed_goal.setTextColor(Color.BLACK);
                tv_completed_goal.setPadding(22, 22, 22, 22);
                tv_completed_goal.setBackgroundResource(R.drawable.row_border);
                tv_completed_goal.setGravity(Gravity.CENTER);
                tv_completed_goal.setTypeface(Typeface.SANS_SERIF);
                tr.addView(tv_completed_goal); // Adding textView to tablerow.

                /** Creating another textview for Goal Point **/
                tv_goal_remaining = new TextView(getActivity());
                tv_goal_remaining.setText("" + goal_details.get(i).getRemainingGoalsfor90Days());
                tv_goal_remaining.setTextColor(Color.BLACK);
                tv_goal_remaining.setGravity(Gravity.CENTER);
                tv_goal_remaining.setPadding(22, 22, 22, 22);
                tv_goal_remaining.setBackgroundResource(R.drawable.row_border);
                tv_goal_remaining.setTypeface(Typeface.SANS_SERIF);
                tr.addView(tv_goal_remaining); // Adding textView to tablerow.

           /* if(userGoalDetails.get(i).getUser_score().equals("")){
                tempCal=0f;
            }else{
                tempCal=Float.parseFloat(userGoalDetails.get(i).getUser_score());
            }
            totalUserScor = totalUserScor + tempCal;*/
                tv_my_score = new TextView(getActivity());
                tv_my_score.setText("" + goal_details.get(i).getCompletedGoalsfor90Days());
                tv_my_score.setTextColor(Color.BLACK);
                tv_my_score.setGravity(Gravity.CENTER);
                tv_my_score.setBackgroundResource(R.drawable.row_border);
                tv_my_score.setPadding(20, 20, 20, 20);
                tv_my_score.setTypeface(Typeface.SANS_SERIF);
                tr.addView(tv_my_score); // Adding textView to tablerow.
                // Add the TableRow to the TableLayout
                mainTrackingtable.addView(tr, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(Global.TAG, "addCurrenData: Exception while adding table row..!");
            }
        }
        addResultRowLast();
    }
    public void addResultRowLast(){
        /** Create a TableRow dynamically **/
        try {
            tr2 = new TableRow(getActivity());
            tr2.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
            tr2.setGravity(View.TEXT_ALIGNMENT_CENTER);
            tr2.setBackgroundResource(R.color.colorWhite);

            /** Creating a TextView to add to the row **/
            TextView t1 = new TextView(getActivity());
            t1.setTextColor(Color.WHITE);
            t1.setGravity(Gravity.CENTER);
            t1.setPadding(20,20,20,20);
            t1.setTypeface(Typeface.MONOSPACE);
            t1.setPadding(20, 20, 20, 0);
            tr2.addView(t1);  // Adding textView to tablerow.

            /** Creating another textview **/
            TextView t2 = new TextView(getActivity());
            t2.setTextColor(Color.WHITE);
            t2.setGravity(Gravity.CENTER);
            t2.setPadding(20, 20, 20, 0);
            t2.setBackgroundResource(R.drawable.row_border);
            t2.setTypeface(Typeface.MONOSPACE);
            tr2.addView(t2); // Adding textView to tablerow.

            /** Creating another textview **/
            TextView t3 = new TextView(getActivity());
            t3.setTextColor(Color.WHITE);
            t3.setGravity(Gravity.CENTER);
            t3.setBackgroundResource(R.drawable.row_border);
            t3.setPadding(20, 20, 20, 0);
            t3.setTypeface(Typeface.MONOSPACE);
            tr2.addView(t3); // Adding textView to tablerow.

            /** Creating another textview **/
            TextView t4 = new TextView(getActivity());
            t4.setText("Total Score");
            t4.setTextColor(Color.BLACK);
            t4.setGravity(Gravity.CENTER);
            t4.setBackgroundResource(R.drawable.row_border);
            t4.setPadding(20, 20, 20, 0);
            t4.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            tr2.addView(t4); // Adding textView to tablerow.

            /** Creating another textview **/
            TextView t5 = new TextView(getActivity());
            // t5.setText(new DecimalFormat("##.##").format(totalUserScor));
            t5.setText(""+total_score_week);
            t5.setTextColor(Color.BLACK);
            t5.setPadding(20, 20, 20, 0);
            t5.setBackgroundResource(R.drawable.row_border);
            t5.setGravity(Gravity.CENTER);
            t5.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            tr2.addView(t5); // Adding textView to tablerow.


            // Add the TableRow to the TableLayout
            mainTrackingtable.addView(tr2, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Global.TAG, "addResultRowLast: Exception:"+e);
        }
    }
    public void noRecordFound(){
        mainTrackingtable.removeAllViews();
        addHeaders();
        totalUserScor=0f;
        Float tempCal;
        for (int i = 0; i < 1; i++)
        {
            /** Create a TableRow dynamically **/
            tr = new TableRow(getActivity());
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
            tr.setGravity(View.TEXT_ALIGNMENT_CENTER);

            if ((i % 2) == 0){tr.setBackgroundResource(R.color.colorWhite);}
            else {tr.setBackgroundResource(R.color.colorList);}

            /** Creating a TextView to add to the row **/

            /** Creating another textview **/
            goal_name = new TextView(getActivity());
            goal_name.setText("No details available for this week");
            goal_name.setTextColor(Color.BLACK);
            goal_name.setPadding(20, 20, 20, 20);
            goal_name.setGravity(Gravity.LEFT);
            goal_name.setTypeface(Typeface.MONOSPACE);
            tr.addView(goal_name); // Adding textView to tablerow.

            mainTrackingtable.addView(tr, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
        }
    }

    public void addHeaders(){

        /** Create a TableRow dynamically **/
        tr1 = new TableRow(getActivity());
        tr1.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.FILL_PARENT));
        tr1.setMinimumHeight(50);
        tr1.setBackgroundResource(R.color.colorPrimary);

        /** Creating a TextView to add to the row **/
        TextView companyTV = new TextView(getActivity());
        companyTV.setText("Goal Names\n");
        companyTV.setTextColor(Color.WHITE);
        companyTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        companyTV.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        companyTV.setPadding(20, 20, 20, 0);
        tr1.addView(companyTV);  // Adding textView to tablerow.

        /** Creating another textview **/
        TextView valueTV = new TextView(getActivity());
        valueTV.setText("My Completed\n Goals");
        valueTV.setTextColor(Color.WHITE);
        valueTV.setGravity(Gravity.LEFT);
        valueTV.setBackgroundResource(R.drawable.row_border);
        valueTV.setPadding(20, 20, 20, 0);
        valueTV.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tr1.addView(valueTV); // Adding textView to tablerow.

        /** Creating another textview **/
        TextView value3 = new TextView(getActivity());
        value3.setText("My Remaining\ngoals");
        value3.setTextColor(Color.WHITE);
        value3.setGravity(Gravity.CENTER);
        value3.setBackgroundResource(R.drawable.row_border);
        value3.setPadding(20, 20, 20, 0);
        value3.setTypeface(Typeface.MONOSPACE);
        tr1.addView(value3); // Adding textView to tablerow.

        /** Creating another textview **/
        TextView value2 = new TextView(getActivity());
        value2.setText("Remaining Goals\nFor 90 days");
        value2.setTextColor(Color.WHITE);
        value2.setGravity(Gravity.CENTER);
        value2.setBackgroundResource(R.drawable.row_border);
        value2.setPadding(20, 20, 20, 0);
        value2.setTypeface(Typeface.MONOSPACE);
        tr1.addView(value2); // Adding textView to tablerow.

        /** Creating another textview **/
        TextView value4 = new TextView(getActivity());
        value4.setText("Completed Goals \n For 90 days");
        value4.setTextColor(Color.WHITE);
        value4.setBackgroundResource(R.drawable.row_border);
        value4.setPadding(20, 20, 20, 0);
        value4.setGravity(Gravity.CENTER);
        value4.setTypeface(Typeface.MONOSPACE);
        tr1.addView(value4); // Adding textView to tablerow.


        // Add the TableRow to the TableLayout
        mainTrackingtable.addView(tr1, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.FILL_PARENT));
    }

    private void fillWeekSpinner() {
        selected = false;

        weekName=new String[weekData.size()];
        weekId=new String[weekData.size()];
        weekStartDate=new String[weekData.size()];
        weekEndDate=new String[weekData.size()];
        listdata=new String[weekData.size()];

        for (int i=0;i<weekData.size();i++){

            weekName[i]=weekData.get(i).getWeek_name();
            weekId[i]=weekData.get(i).getWeek_id();
            weekStartDate[i]=weekData.get(i).getWeek_start_date();
            weekEndDate[i]=weekData.get(i).getWeek_end_date();
            listdata[i]=weekData.get(i).getWeek_name()+" ( "+weekData.get(i).getWeek_start_date()+" To "+weekData.get(i).getWeek_end_date()+" )";
        }

        if (getActivity()!=null){

            SpinnerAdapter adapter =new SpinnerAdapter(getActivity(),android.R.layout.simple_list_item_1);
            adapter.add("Current Week");
            adapter.addAll(listdata);
            adapter.add("Current Week");
            sp_cft_weeks.setAdapter(adapter);
            sp_cft_weeks.setSelection(adapter.getCount());

            sp_cft_weeks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // TODO Auto-generated method stub
                    if(sp_cft_weeks.getSelectedItem() == "Current Week")
                    {
                        if(Global.isNetworkAvailable(getActivity())) {
//                        if(sp.getBoolean("IsBusinessStart",true)) {
//                            Toast.makeText(ActivityWeeklyTracking.this, "Business Date Not Started Yet", Toast.LENGTH_SHORT).show();
//                            noRecordFound();
//                        }else {
                            getCftCheckList(0);//Send 0 because to not fill the week spineer again
                            tv_week_track_heading.setText("Current Week");
//                        }
                        }else{
                            Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
                            startActivity(intent, bundleEffect);
                        }
                    }else{
                        if(Global.isNetworkAvailable(getActivity())) {
                            if(spLib.getPref(SPLib.Key.BusinessStart).equals(true)) {
                                Toast.makeText(getActivity(), "Business Date Not Started Yet", Toast.LENGTH_SHORT).show();
                                noRecordFound();
                            }else {
                                getWeekDataOnWeekId(weekId[position-1]);
                                tv_week_track_heading.setText("Week "+position+" Tracking Details");
                            }
                        }else{
                            Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
                            startActivity(intent, bundleEffect);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
        }

    }

    public class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context context, int simple_list_item_1) {
            super(context, simple_list_item_1);
            // TODO Auto-generated constructor stub

        }

        @Override
        public int getCount() {

            // TODO Auto-generated method stub
            int count = super.getCount();

            return count>0 ? count-1 : count ;


        }
    }
    private void getWeekDataOnWeekId(String weekId) {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("fromUserId", from_user_id);
            paramObj.put("weekId", weekId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getWeekDataOnWeekId: "+paramObj.toString());


        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getWeekDataOnWeekId: "+weekId+" User Id:"+user_id);
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Log.d(Global.TAG, "getWeekDataOnWeekId: from user id:"+from_user_id);
        Call<CFTDashboard> call=service.getWeekDetailsCFT(paramObj.toString());
        call.enqueue(new Callback<CFTDashboard>() {
            @Override
            public void onResponse(Call<CFTDashboard> call, Response<CFTDashboard> response) {
              weekDetails=response.body();
                if (weekDetails.isSuccess()){
                    Log.d(Global.TAG, "onResponse: Total Score:"+weekDetails.getTotalScore());
                    total_score_week=weekDetails.getTotalScore();
                    userWeekGoalDetails.clear();
                    fl_no_cft_user.setVisibility(View.GONE);
                    List<SingleGoal1> goalList=weekDetails.getTrackingDetailsCft();
                    Log.d(Global.TAG, "getWeekDataOnWeekId: goalList:"+goalList.size());
                    //goalList=trackingDetails.getGoal_details();
                    for (int i = 0; i < goalList.size(); i++) {
                        SingleGoal1 datarow = goalList.get(i);
                        SingleGoal1 goal = new SingleGoal1();
                        goal.setGoal_id(datarow.getZo_goal_id());
                        goal.setGoal_name(datarow.getGoal_name());
                        goal.setGoal_count(datarow.getGoal_count());
                        goal.setRemaining_goals(datarow.getRemaining_goals());
                        goal.setUser_done_goal_count(datarow.getUser_done_goal_count());
                        goal.setUser_score(datarow.user_score);

                        userWeekGoalDetails.add(goal);
                    }

                    if (userWeekGoalDetails.size()>0){
                        addSelectedWeekData();
                    }else{
                        noRecordFound();
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CFTDashboard> call, Throwable t) {
                myLoader.dismiss();
                noRecordFound();
                Log.d(Global.TAG, "onFailure: getWeekDataOnWeekId"+t);
            }
        });


    }
    public void addSelectedWeekData(){
        mainTrackingtable.removeAllViews();
        addHeaders();
        totalUserScor=0f;
        Float tempCal;
        for (int i = 0; i < userWeekGoalDetails.size(); i++)
        {
            //** Create a TableRow dynamically **//*
            tr = new TableRow(getActivity());
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
            tr.setGravity(View.TEXT_ALIGNMENT_CENTER);

            if ((i % 2) == 0){tr.setBackgroundResource(R.color.colorWhite);}
            else {tr.setBackgroundResource(R.color.colorList);}

            //** Creating a TextView to add to the row **//*

            //** Creating another textview **//*
            goal_name = new TextView(getActivity());
            goal_name.setText(userWeekGoalDetails.get(i).getGoal_name());
            goal_name.setTextColor(Color.BLACK);
            goal_name.setPadding(20, 20, 20, 20);
            goal_name.setGravity(Gravity.LEFT);
            goal_name.setTypeface(Typeface.MONOSPACE);
            tr.addView(goal_name); // Adding textView to tablerow.

            tv_my_goal = new TextView(getActivity());
            tv_my_goal.setText(userWeekGoalDetails.get(i).getUser_done_goal_count());
            tv_my_goal.setTextColor(Color.BLACK);
            tv_my_goal.setTypeface(Typeface.MONOSPACE);
            tv_my_goal.setBackgroundResource(R.drawable.row_border);
            tv_my_goal.setPadding(20, 20, 20, 20);
            tv_my_goal.setGravity(Gravity.LEFT);
            tr.addView(tv_my_goal);  // Adding textView to tablerow.

            //** Creating another textview **//*
            tv_completed_goal = new TextView(getActivity());
            tv_completed_goal.setText(""+userWeekGoalDetails.get(i).getRemaining_goals());
            tv_completed_goal.setTextColor(Color.BLACK);
            tv_completed_goal.setPadding(20, 20, 20, 20);
            tv_completed_goal.setBackgroundResource(R.drawable.row_border);
            tv_completed_goal.setGravity(Gravity.CENTER);
            tv_completed_goal.setTypeface(Typeface.MONOSPACE);
            tr.addView(tv_completed_goal); // Adding textView to tablerow.

            //** Creating another textview for Goal Point **//*
            tv_goal_remaining = new TextView(getActivity());
            tv_goal_remaining.setText(""+userWeekGoalDetails.get(i).getRemainingGoalsfor90Days());
            tv_goal_remaining.setTextColor(Color.BLACK);
            tv_goal_remaining.setGravity(Gravity.CENTER);
            tv_goal_remaining.setPadding(20, 20, 20, 20);
            tv_goal_remaining.setBackgroundResource(R.drawable.row_border);
            tv_goal_remaining.setTypeface(Typeface.MONOSPACE);
            tr.addView(tv_goal_remaining); // Adding textView to tablerow.

          /*  if(userWeekGoalDetails.get(i).getUser_score().equals("")){
                tempCal=0f;
            }else{
                tempCal=Float.parseFloat(userWeekGoalDetails.get(i).getUser_score());
            }
            totalUserScor = totalUserScor + tempCal;*/

            tv_my_score = new TextView(getActivity());
            tv_my_score.setText(""+userWeekGoalDetails.get(i).getCompletedGoalsfor90Days());
            tv_my_score.setTextColor(Color.BLACK);
            tv_my_score.setGravity(Gravity.CENTER);
            tv_my_score.setBackgroundResource(R.drawable.row_border);
            tv_my_score.setPadding(20, 20, 20, 20);
            tv_my_score.setTypeface(Typeface.MONOSPACE);
            tr.addView(tv_my_score); // Adding textView to tablerow.
            // Add the TableRow to the TableLayout
            mainTrackingtable.addView(tr, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
        }
        addResultRowLast();

    }

    @Override
    public void onResume() {
        super.onResume();
        //tracking_access=false;

    }
}
