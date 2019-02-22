package com.success.successEntellus.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.CampaignTemplateAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.CampaignEmailTemplates;
import com.success.successEntellus.model.CampaignTemplate;
import com.success.successEntellus.model.Emails;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.success.successEntellus.fragment.CustomCampaignFragment.dashboardActivity;

/**
 * Created by user on 5/9/2018.
 */

public class CampaignStepsFragment extends Fragment implements CampaignTemplateAdapter.RefreshAllEmailCampaigns {
    View layout;
    SPLib spLib;
    static Activity activity;
    static String user_id;
    static String campaign_id;
    static RecyclerView rv_campaign_templates;
    String title;
    static Context context;
    Toolbar toolbarsteps;
    ImageButton ib_back;
    public static List<Emails> emailsList=new ArrayList<>();
    public static JSONArray emailDetails=new JSONArray();
    static boolean campanyFlag=false;
    TextView tv_campaign_step_name;
    public static String campaignStepFooterFlag ="1";
    @SuppressLint("ValidFragment")
    public CampaignStepsFragment(String campaign_id, String title,boolean campanyFlag) {
        this.campaign_id=campaign_id;
        this.title=title;
        this.campanyFlag=campanyFlag;
    }
    public CampaignStepsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_campaign_templates,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        setHasOptionsMenu(true);
        init();
        tv_campaign_step_name.setText(title);
        getCampaignTemplateOnId();
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "ib_back:");
               getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        return layout;
    }

    private void init() {
        spLib=new SPLib(getActivity());
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        rv_campaign_templates=layout.findViewById(R.id.rv_campaign_templates);
       toolbarsteps=(Toolbar)layout.findViewById(R.id.toolbarsteps);
        ib_back=(ImageButton)layout.findViewById(R.id.ib_back);
        tv_campaign_step_name=(TextView) layout.findViewById(R.id.tv_campaign_step_name);
       rv_campaign_templates.setLayoutManager(new GridLayoutManager(getActivity(),2));
        context=getActivity();
        activity=getActivity();


    }

    public void getCampaignTemplateOnId() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId", campaign_id);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getCampaignTemplateOnId: "+paramObj.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CampaignEmailTemplates> call=service.getEmailTemplateOnId(paramObj.toString());
        call.enqueue(new Callback<CampaignEmailTemplates>() {
            @Override
            public void onResponse(Call<CampaignEmailTemplates> call, Response<CampaignEmailTemplates> response) {
                CampaignEmailTemplates campaignEmailTemplates=response.body();
                if (campaignEmailTemplates.isSuccess()){
                    List<CampaignTemplate> campaignTemplates=campaignEmailTemplates.getResult();
                    Log.d(Global.TAG, "campaignTemplates: "+campaignTemplates.size());
                        CampaignTemplateAdapter adapter=new CampaignTemplateAdapter(context,CampaignStepsFragment.this,campaignTemplates,user_id,campaign_id,campanyFlag,CampaignStepsFragment.this);
                        rv_campaign_templates.setAdapter(adapter);

                }else{
                    Toast.makeText(context, "No Templates Added Yet..!", Toast.LENGTH_SHORT).show();
                    dashboardActivity.getSupportFragmentManager().popBackStackImmediate();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, " getCampaignTemplateOnId onFailure: "+t);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==103){
            getCampaignTemplateOnId();
        }
    }

    @Override
    public void refreshCampaigns() {
        getCampaignTemplateOnId();
    }
}
