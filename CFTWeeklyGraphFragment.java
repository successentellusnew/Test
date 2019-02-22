package com.success.successEntellus.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.CFTDashboard;
import com.success.successEntellus.model.GraphInfo;
import com.success.successEntellus.model.Weekly_Goal;
import com.success.successEntellus.model.Weekly_Score;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 6/19/2018.
 */

public class CFTWeeklyGraphFragment extends Fragment {
    View layout;
    String from_user_id;
    FrameLayout fl_no_users;
    TextView tv_no_users;
    boolean tracking_access=false;
    SPLib spLib;
    GraphInfo graphDetails;
    public List<Weekly_Goal> weekly_goals;
    public List<Weekly_Score> weekly_score;
    Float[] totalWeek,totalWeekGoalDone,totalWeekscore;
    float user_sum;
    float admin_sum;
    int totalWeekCount;
    ArrayList<GraphInfo> userAllWeekGoal;
    ArrayList<Entry> yAXESgoalDone,yAXEuserGoal,yAXESadminGoal;
    final HashMap<String, String> params = new HashMap<>();
    LineChart lineChart;
    LineChart lineChartWeeklyGoal;
    ArrayList<Entry> yAXESfirst;

    @SuppressLint("ValidFragment")
    public CFTWeeklyGraphFragment(String from_user_id,boolean tracking_access) {
        this.from_user_id=from_user_id;
        this.tracking_access=tracking_access;
    }

    public CFTWeeklyGraphFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_cft_weekly_graph,container,false);
        init();


        return layout;
    }

    private void init() {
        fl_no_users=layout.findViewById(R.id.fl_no_cft_user);
        tv_no_users=layout.findViewById(R.id.tv_no_users);
        spLib=new SPLib(getActivity());
        //iv_graph_goal_hint=(ImageView)layout.findViewById(R.id.iv_graph_goal_hint);
       // iv_graph_score_hint=(ImageView)layout.findViewById(R.id.iv_graph_score_hint);
        //bundleEffect = ActivityOptionsCompat.makeCustomAnimation(getActivity(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        lineChartWeeklyGoal = (LineChart) layout.findViewById(R.id.lineChartWeeklyGoal);
        userAllWeekGoal = new ArrayList<>();
        lineChart = (LineChart) layout.findViewById(R.id.lineChart);
        //userAllWeekScore = new ArrayList<>();
        //user_id=spLib.getPref(SPLib.Key.USER_ID);


        if (from_user_id.equals("-1")){
            fl_no_users.setVisibility(View.VISIBLE);
        }else{
            fl_no_users.setVisibility(View.GONE);
        }
        if (tracking_access){
            getCftGraphDetails();
        }else if (!from_user_id.equals("-1")){
            fl_no_users.setVisibility(View.VISIBLE);
            tv_no_users.setText("Don't have access permission from the selected Recruit.");
        }
    }
    private void getCftGraphDetails() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("fromUserId", from_user_id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getCftGraphDetails: "+paramObj.toString());
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CFTDashboard> call=service.getCftGraphDetails(paramObj.toString());
        call.enqueue(new Callback<CFTDashboard>() {
            @Override
            public void onResponse(Call<CFTDashboard> call, Response<CFTDashboard> response) {
                CFTDashboard cftDashboard=response.body();
                Log.d(Global.TAG, "onResponse: Response:"+response);
                if (cftDashboard.isSuccess()){
                    fl_no_users.setVisibility(View.GONE);
                    graphDetails=cftDashboard.getGraphDetails();
                        weekly_goals=graphDetails.getWeekly_goals();
                        weekly_score=graphDetails.getWeekly_score();
                        admin_sum =graphDetails.getAdmin_goal_sum();
                        user_sum = graphDetails.getUser_goal_sum();
                        totalWeekCount = graphDetails.getWeek_count();
                        totalWeekGoalDone = new Float[totalWeekCount];
                        totalWeekscore = new Float[totalWeekCount];
//                    weekData=trackingDetails.getWeek();
//                    goal_details=trackingDetails.getGoal_details();
                        Log.d(Global.TAG, "weekly_goals: "+weekly_goals.size());
                        Log.d(Global.TAG, "weekly_score List: "+weekly_score.size());
                        Log.d(Global.TAG, "week count: "+graphDetails.getWeek_count());


                        if (weekly_goals.size()>0) {
                                for (int i = 0; i < weekly_goals.size(); i++) {
                                    Weekly_Goal datarow = weekly_goals.get(i);

                                    if (graphDetails.getWeek_count()==13){

                                        totalWeekGoalDone[0] = (float)datarow.getOne();
                                        totalWeekGoalDone[1] = (float)datarow.getTwo();
                                        totalWeekGoalDone[2] = (float)datarow.getThree();
                                        totalWeekGoalDone[3] = (float)datarow.getFour();
                                        totalWeekGoalDone[4] = (float)datarow.getFive();
                                        totalWeekGoalDone[5] = (float)datarow.getSix();
                                        totalWeekGoalDone[6] = (float)datarow.getSeven();
                                        totalWeekGoalDone[7] = (float)datarow.getEight();
                                        totalWeekGoalDone[8] = (float)datarow.getNine();
                                        totalWeekGoalDone[9] = (float)datarow.getTen();
                                        totalWeekGoalDone[10] = (float)datarow.getEleven();
                                        totalWeekGoalDone[11] = (float)datarow.getTwelve();
                                        totalWeekGoalDone[12] = (float)datarow.getThirteen();

                                    }else if (graphDetails.getWeek_count()==14){

                                        totalWeekGoalDone[0] = (float)datarow.getOne();
                                        totalWeekGoalDone[1] = (float)datarow.getTwo();
                                        totalWeekGoalDone[2] = (float)datarow.getThree();
                                        totalWeekGoalDone[3] = (float)datarow.getFour();
                                        totalWeekGoalDone[4] = (float)datarow.getFive();
                                        totalWeekGoalDone[5] = (float)datarow.getSix();
                                        totalWeekGoalDone[6] = (float)datarow.getSeven();
                                        totalWeekGoalDone[7] = (float)datarow.getEight();
                                        totalWeekGoalDone[8] = (float)datarow.getNine();
                                        totalWeekGoalDone[9] = (float)datarow.getTen();
                                        totalWeekGoalDone[10] = (float)datarow.getEleven();
                                        totalWeekGoalDone[11] = (float)datarow.getTwelve();
                                        totalWeekGoalDone[12] = (float)datarow.getThirteen();
                                        totalWeekGoalDone[13] = (float)datarow.getForteen();

                                    }


                                }

                        }
                        if (weekly_score.size()>0){
                            for (int i = 0; i < weekly_score.size(); i++) {
                                Weekly_Score datascorerow = weekly_score.get(i);

                                if (graphDetails.week_count==13){
                                    totalWeekscore[0] =(float) datascorerow.getOnescore();
                                    totalWeekscore[1] = (float)datascorerow.getTwoscore();
                                    totalWeekscore[2] = (float)datascorerow.getThreescore();
                                    totalWeekscore[3] =(float) datascorerow.getFourscore();
                                    totalWeekscore[4] = (float)datascorerow.getFivescore();
                                    totalWeekscore[5] = (float)datascorerow.getSixscore();
                                    totalWeekscore[6] = (float)datascorerow.getSevenscore();
                                    totalWeekscore[7] = (float)datascorerow.getEightscore();
                                    totalWeekscore[8] = (float)datascorerow.getNinescore();
                                    totalWeekscore[9] = (float)datascorerow.getTenscore();
                                    totalWeekscore[10] = (float)datascorerow.getElevenscore();
                                    totalWeekscore[11] = (float)datascorerow.getTwelvescore();
                                    totalWeekscore[12] = (float)datascorerow.getThirteenscore();
                                }else if(graphDetails.getWeek_count()==14){
                                    totalWeekscore[0] =(float) datascorerow.getOnescore();
                                    totalWeekscore[1] = (float)datascorerow.getTwoscore();
                                    totalWeekscore[2] = (float)datascorerow.getThreescore();
                                    totalWeekscore[3] =(float) datascorerow.getFourscore();
                                    totalWeekscore[4] = (float)datascorerow.getFivescore();
                                    totalWeekscore[5] = (float)datascorerow.getSixscore();
                                    totalWeekscore[6] = (float)datascorerow.getSevenscore();
                                    totalWeekscore[7] = (float)datascorerow.getEightscore();
                                    totalWeekscore[8] = (float)datascorerow.getNinescore();
                                    totalWeekscore[9] = (float)datascorerow.getTenscore();
                                    totalWeekscore[10] = (float)datascorerow.getElevenscore();
                                    totalWeekscore[11] = (float)datascorerow.getTwelvescore();
                                    totalWeekscore[12] = (float)datascorerow.getThirteenscore();
                                    totalWeekscore[13] = (float)datascorerow.getForteenscore();
                                }

                            }
                        }


                    splitFunction();
                    splitFunctionForWeeklyScore();
                    }

                }

            @Override
            public void onFailure(Call<CFTDashboard> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:getCftGraphDetails "+t);
            }
        });


    }
    private void splitFunction() {
        // myLoader.dismiss();

        totalWeek = new Float[totalWeekCount];

        yAXESgoalDone = new ArrayList<>();
        yAXEuserGoal= new ArrayList<>();
        yAXESadminGoal= new ArrayList<>();

        for (int i = 0; i < totalWeekCount; i++) {

            yAXESadminGoal.add(new Entry(admin_sum, i));
            yAXEuserGoal.add(new Entry(user_sum,i));
            //Add data into yAxes entry to plot it into graph
            yAXESgoalDone.add(new Entry(totalWeekGoalDone[i], i));
        }
        plotAGraph();
    }
    private void plotAGraph() {
        String[] xaxes = new String[totalWeekCount];
        int j =1;
        for(int i=0; i<totalWeekCount;i++){
            xaxes[i] ="Week"+String.valueOf(j);
            j++;
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(yAXESadminGoal,"Program Goals");
        lineDataSet1.setValueTypeface(Typeface.MONOSPACE);
        lineDataSet1.setDrawCircles(true);
        lineDataSet1.setDrawCubic(true);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setCircleColor(getResources().getColor(R.color.colorOragne));
        lineDataSet1.setValueTextColor(getResources().getColor(R.color.colorGraphText));

        LineDataSet lineDataSet2 = new LineDataSet(yAXEuserGoal,"My Actual Goals");
        lineDataSet2.setValueTypeface(Typeface.MONOSPACE);
        //lineDataSet2.setColor(getResources().getColor(R.color.colorRed));
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawCubic(true);
        lineDataSet2.setColor(Color.RED);
        lineDataSet2.setCircleColor(getResources().getColor(R.color.colorOragne));
        lineDataSet2.setValueTextColor(getResources().getColor(R.color.colorGraphText));

        LineDataSet lineDataSet3 = new LineDataSet(yAXESgoalDone,"My Goals");
        lineDataSet3.setValueTypeface(Typeface.MONOSPACE);
        lineDataSet1.setColor(getResources().getColor(R.color.colorBlue));
        lineDataSet3.setDrawCircles(true);
        lineDataSet3.setDrawCubic(true);
        lineDataSet3.setColor(Color.GREEN);
        lineDataSet3.setCircleColor(getResources().getColor(R.color.colorOragne));
        lineDataSet3.setValueTextColor(getResources().getColor(R.color.colorGraphText));

        lineDataSets.add(lineDataSet1);
        lineDataSets.add(lineDataSet2);
        lineDataSets.add(lineDataSet3);

        YAxis leftAxis = lineChartWeeklyGoal.getAxisLeft();
        lineChartWeeklyGoal.setDescription("");
        leftAxis.setAxisMinValue(0f);

        lineChartWeeklyGoal.setData(new LineData(xaxes,lineDataSets));
        lineChartWeeklyGoal.setVisibleXRangeMaximum(65f);
        lineChartWeeklyGoal.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChartWeeklyGoal.getAxisRight().setEnabled(false);
        lineChartWeeklyGoal.animateY(5000);

    }

    private void splitFunctionForWeeklyScore() {
        yAXESfirst = new ArrayList<>();

        for (int i = 0; i < totalWeekCount; i++) {
            yAXESfirst.add(new Entry(totalWeekscore[i], i));
        }
        plotAGraphForWeeklyScore();

    }
    private void plotAGraphForWeeklyScore() {
        String[] xaxes = new String[totalWeekCount];
        int j =1;
        for(int i=0; i<totalWeekCount;i++){
            xaxes[i] ="Week"+String.valueOf(j);
            j++;
        }


        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(yAXESfirst,"Weekly Score");
        lineDataSet1.setDrawCircles(true);
        lineDataSet1.setDrawCubic(true);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setCircleColor(getResources().getColor(R.color.colorOragne));
        lineDataSet1.setValueTextColor(getResources().getColor(R.color.colorGraphText));

        lineDataSets.add(lineDataSet1);

        YAxis leftAxis = lineChart.getAxisLeft();
        lineChart.setDescription("");
        leftAxis.setAxisMinValue(0f);

        lineChart.setData(new LineData(xaxes,lineDataSets));
        lineChart.setVisibleXRangeMaximum(65f);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateY(5000);
        //myLoader.dismiss();
    }


}
