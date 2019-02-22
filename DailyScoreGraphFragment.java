package com.success.successEntellus.fragment;


import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.success.successEntellus.model.CurrentWeekScore;
import com.success.successEntellus.model.WeekScore;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bhagyashri on 3/27/2018.
 */

public class DailyScoreGraphFragment extends Fragment {
    View layout;
    String user_id;
    ArrayList<Entry> yAXESScore=new ArrayList<>();
    Float[] tempScore;
    LineChart lineCharCurrentWeek;
    SPLib spLib;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_daily_scoregraph,container,false);
        setHasOptionsMenu(true);
        spLib=new SPLib(getActivity());
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        tempScore= new Float[10];
        lineCharCurrentWeek = (LineChart) layout.findViewById(R.id.lineCharCurrentWeek);
        getUserCurrentWeekScoreForGraph();

        return layout;
    }
    private void getUserCurrentWeekScoreForGraph() {
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getUserCurrentWeekScoreForGraph: "+user_id);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CurrentWeekScore> call=service.getCurrentWeekScore(user_id);
        call.enqueue(new Callback<CurrentWeekScore>() {
            @Override
            public void onResponse(Call<CurrentWeekScore> call, Response<CurrentWeekScore> response) {
                CurrentWeekScore currentWeekScore=response.body();
                if (currentWeekScore.isSuccess()){
                    myLoader.dismiss();
                    List<WeekScore> weekScorelist=currentWeekScore.getResult();
                    Log.d(Global.TAG, "weekScorelist: "+weekScorelist.size());
                    for(int i=0;i<weekScorelist.size();i++){
                        WeekScore weekScore=weekScorelist.get(i);
                        Log.d(Global.TAG, "onResponse: "+weekScore.getMon());

                        tempScore[0] =(float)weekScore.getMon();
                        tempScore[1] =(float)weekScore.getTue();
                        tempScore[2] =(float) weekScore.getWed();
                        tempScore[3] =(float) weekScore.getThu();
                        tempScore[4] =(float)weekScore.getFri();
                        tempScore[5] =(float)weekScore.getSat();
                        tempScore[6] =(float)weekScore.getSun();

                    }
                    plotAGraph();

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CurrentWeekScore> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: "+t);
                myLoader.dismiss();
            }
        });
    }
    private void plotAGraph() {
        yAXESScore = new ArrayList<>();
        for (int i=0;i<7;i++){
            yAXESScore.add(new Entry(tempScore[i], i));
        }

        String[] xaxes = new String[7];
        xaxes[0] ="Mon";
        xaxes[1] ="Tue";
        xaxes[2] ="Wed";
        xaxes[3] ="Thu";
        xaxes[4] ="Fri";
        xaxes[5] ="Sat";
        xaxes[6] ="Sun";

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(yAXESScore,"Score");
        lineDataSet1.setValueTypeface(Typeface.MONOSPACE);
        lineDataSet1.setDrawCircles(true);
        lineDataSet1.setCircleColor(getResources().getColor(R.color.colorOragne));
        lineDataSet1.setValueTextColor(getResources().getColor(R.color.colorGraphText));
        lineDataSet1.setDrawCubic(true);
        lineDataSet1.setColor(getResources().getColor(R.color.colorGraphLine));
        lineDataSets.add(lineDataSet1);

        YAxis leftAxis = lineCharCurrentWeek.getAxisLeft();
        lineCharCurrentWeek.setDescription("");
        leftAxis.setTextColor(getResources().getColor(R.color.colorGraphYText));
        leftAxis.setAxisMinValue(0f);
        lineCharCurrentWeek.getAxisRight().setEnabled(false);
        lineCharCurrentWeek.setData(new LineData(xaxes,lineDataSets));
        lineCharCurrentWeek.setVisibleXRangeMaximum(65f);
        lineCharCurrentWeek.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineCharCurrentWeek.getXAxis().setTextColor(getResources().getColor(R.color.colorGraphText));
        lineCharCurrentWeek.animateY(5000);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

}
