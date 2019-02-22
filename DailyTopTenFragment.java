package com.success.successEntellus.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.TopScoreActivity;
import com.success.successEntellus.activity.WeeklyArchiveActivity;
import com.success.successEntellus.adapter.DailyTopTenAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.TopScoreRecruit;
import com.success.successEntellus.model.TopScoreRecruitList;
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
import java.util.TimeZone;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 7/30/2018.
 */

public class DailyTopTenFragment extends Fragment {
    View layout;
    SPLib spLib;
    String currDayName;
    Date curUSDate = null;
    String current_date;
    SearchView search_top_10;
    RecyclerView rv_top_10_list;
    List<TopScoreRecruit>  topScoreRecruits,search_list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_daily_top_ten,container,false);
        setHasOptionsMenu(true);
        init();
        //getCurrentDay();
       // getTodayScore();

        current_date=getCurrentDate();
        Log.d(Global.TAG, "Current Date: "+current_date);
        getTodayScore();

        search_top_10.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String Search_String) {
                if (topScoreRecruits.size()>0){
                    search_list.clear();
                    for (int i=0;i<topScoreRecruits.size();i++){
                        String name=topScoreRecruits.get(i).getRecruitUserName();
                        if ( Pattern.compile(Pattern.quote(Search_String), Pattern.CASE_INSENSITIVE).matcher(name).find()){
                            search_list.add(topScoreRecruits.get(i));
                        }
                    }
                    Log.d(Global.TAG, "onQueryTextChange: search_list:"+search_list.size());
                    if (search_list.size()>0){
                        DailyTopTenAdapter adapter=new DailyTopTenAdapter(getActivity(),search_list);
                        rv_top_10_list.setAdapter(adapter);
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String Search_String) {
                if (topScoreRecruits.size()>0){
                    search_list.clear();
                    for (int i=0;i<topScoreRecruits.size();i++){
                        String name=topScoreRecruits.get(i).getRecruitUserName();
                        if ( Pattern.compile(Pattern.quote(Search_String), Pattern.CASE_INSENSITIVE).matcher(name).find()){
                            search_list.add(topScoreRecruits.get(i));
                        }
                    }
                    Log.d(Global.TAG, "onQueryTextChange: search_list:"+search_list.size());
                    if (search_list.size()>0){
                        DailyTopTenAdapter adapter=new DailyTopTenAdapter(getActivity(),search_list);
                        rv_top_10_list.setAdapter(adapter);
                    }

                }

                return false;
            }
        });
        return layout;
    }

    private void init() {
        spLib=new SPLib(getActivity());
        search_top_10=(SearchView)layout.findViewById(R.id.search_top_10);
        search_top_10.setIconified(false);
        search_top_10.setFocusable(false);
        search_top_10.clearFocus();
        rv_top_10_list=(RecyclerView) layout.findViewById(R.id.rv_top_10_list);
        rv_top_10_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        topScoreRecruits=new ArrayList<>();
        search_list=new ArrayList<>();

    }
    private String getCurrentDate() {

        Calendar Curcal = Calendar.getInstance();
        Curcal.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        int CurrDay = Curcal.get(Calendar.DATE);
        int CurrMonth = Curcal.get(Calendar.MONTH);
        int CurrYear = Curcal.get(Calendar.YEAR);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = dateFormat.parse(CurrYear+"-"+(CurrMonth+1)+"-"+CurrDay);
            Log.d(Global.TAG, "curDate: "+date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(date);
    }

    private void getCurrentDay() {
        Calendar Curcal = Calendar.getInstance();
        Curcal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        int CurrDay = Curcal.get(Calendar.DATE);
        int CurrMonth = Curcal.get(Calendar.MONTH);
        int CurrYear = Curcal.get(Calendar.YEAR);

        try {
            curUSDate = new SimpleDateFormat("yyyy-mm-dd").parse((CurrMonth+1)+"-"+CurrDay+"-"+CurrYear);
            Log.d(Global.TAG, "curDate: "+curUSDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currDayName = new SimpleDateFormat("EEEE").format(curUSDate);
        Log.d(Global.TAG, "currDayName: "+currDayName);
    }

    private void getTodayScore() {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("type", "today");
            paramObj.put("scoreDate", current_date);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getTopScoreList: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<TopScoreRecruitList> call=servive.getDailyTopTenScore(paramObj.toString());
        call.enqueue(new Callback<TopScoreRecruitList>() {
            @Override
            public void onResponse(Call<TopScoreRecruitList> call, Response<TopScoreRecruitList> response) {
                TopScoreRecruitList today_top_list=response.body();
                if (today_top_list.isSuccess()) {
                    topScoreRecruits=today_top_list.getResult();
                    Log.d(Global.TAG, "topScoreRecruits: "+topScoreRecruits.size());
                   if (topScoreRecruits.size()>0){
                       DailyTopTenAdapter adapter=new DailyTopTenAdapter(getActivity(),topScoreRecruits);
                       rv_top_10_list.setAdapter(adapter);
                   }else{
                       Toast.makeText(getActivity(), ""+today_top_list.getResult(), Toast.LENGTH_SHORT).show();
                   }

                }else {

                    Toast.makeText(getActivity(), ""+today_top_list.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<TopScoreRecruitList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:Top  "+t);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.daily_top_ten_menu, menu);

        if (menu != null) {
            menu.findItem(R.id.action_weekly_archive).setVisible(true);
        }
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_weekly_archive){
            //Toast.makeText(getActivity(), "Weekly Archive..", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), WeeklyArchiveActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
