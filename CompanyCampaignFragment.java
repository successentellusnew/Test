package com.success.successEntellus.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.Campaign;
import com.success.successEntellus.model.GetAllCampaign;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 5/7/2018.
 */

public class CompanyCampaignFragment extends Fragment {
    View layout;
    RecyclerView rv_company_campaign;
    String user_id;
    SPLib spLib;
    DashboardActivity dashboardActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_company_campaign,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        setHasOptionsMenu(true);
        init();

        getAllCompanyCampaign();
        return layout;
    }

    private void getAllCompanyCampaign() {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("companyCampaign", "1");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAllCampaign: "+paramObj.toString());
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllCampaign> call=service.get_company_Campaigns(paramObj.toString());
        call.enqueue(new Callback<GetAllCampaign>() {
            @Override
            public void onResponse(Call<GetAllCampaign> call, Response<GetAllCampaign> response) {
                GetAllCampaign getAllCampaign=response.body();
                if (getAllCampaign.isSuccess()){
                    List<Campaign> campaignList=getAllCampaign.getResult();
                    Log.d(Global.TAG, "campaignList: "+campaignList.size());

                    if (campaignList.size()>0){
                        CompanyGridAdapter adapter=new CompanyGridAdapter(dashboardActivity,campaignList,user_id);
                        rv_company_campaign.setAdapter(adapter);
                    }
                }else{
                    Toast.makeText(getActivity(), "Error in getting campaigns..!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetAllCampaign> call, Throwable t) {
                Toast.makeText(getActivity(), "Error in getting campaigns..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure: getAllCampaign"+t);
            }
        });
    }

    private void init() {
        rv_company_campaign=(RecyclerView) layout.findViewById(R.id.rv_company_campaign);
        rv_company_campaign.setLayoutManager(new GridLayoutManager(getActivity(),2));
        spLib=new SPLib(getActivity());
        user_id=spLib.getPref(SPLib.Key.USER_ID);
       // dashboardActivity= (DashboardActivity) getActivity();
        dashboardActivity= (DashboardActivity) this.getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Email Campaigns");
    }
}
