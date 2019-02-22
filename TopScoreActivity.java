package com.success.successEntellus.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.success.successEntellus.R;
import com.success.successEntellus.fragment.TopScoreListAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.TopScoreRecruit;
import com.success.successEntellus.model.TopScoreRecruitList;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopScoreActivity extends AppCompatActivity implements TopScoreListAdapter.ResponseToFragment{
    ImageButton ib_top_back;
    SearchView search_top_recruits;
    RecyclerView rv_top_recruits_list;
    SPLib spLib;
    DashboardActivity dashboardActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_top_score_recruits);
        getSupportActionBar().hide();
        init();
        getTopScoreList();

        ib_top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //getActivity().getSupportFragmentManager().popBackStackImmediate();
                //getActivity().getSupportFragmentManager().popBackStackImmediate();
               // dashboardActivity.replaceCampaignFragments(new WeeklyTrackingFragment());

            }
        });
    }
    private void init() {
       //dashboardActivity= (DashboardActivity) this.getActivity();
        ib_top_back=findViewById(R.id.ib_top_back);
        rv_top_recruits_list=findViewById(R.id.rv_top_recruits_list);
        rv_top_recruits_list.setLayoutManager(new LinearLayoutManager(TopScoreActivity.this));
        search_top_recruits=findViewById(R.id.search_top_recruits);
        search_top_recruits.setIconified(false);
        search_top_recruits.setFocusable(false);
        search_top_recruits.clearFocus();
        spLib=new SPLib(TopScoreActivity.this);
    }
    private void getTopScoreList() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("limit", "10");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getTopScoreList: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(TopScoreActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<TopScoreRecruitList> call=servive.get_top_score_list(paramObj.toString());
        call.enqueue(new Callback<TopScoreRecruitList>() {
            @Override
            public void onResponse(Call<TopScoreRecruitList> call, Response<TopScoreRecruitList> response) {
                TopScoreRecruitList topScoreRecruitList=response.body();
                if (topScoreRecruitList.isSuccess()){
                    List<TopScoreRecruit> topScoreRecruits=topScoreRecruitList.getResult();
                    Log.d(Global.TAG, "onResponse:topScoreRecruits: "+topScoreRecruits.size());
                    if (topScoreRecruits.size()>0){
                        TopScoreListAdapter topScoreListAdapter=new TopScoreListAdapter(TopScoreActivity.this,topScoreRecruits,TopScoreActivity.this);
                        rv_top_recruits_list.setAdapter(topScoreListAdapter);
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<TopScoreRecruitList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: tOPScore "+t);
            }
        });
    }

    @Override
    public void selectRecruit(String userId) {

    }
}
