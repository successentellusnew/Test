package com.success.successEntellus.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.TemplateListAdapter;
import com.success.successEntellus.fragment.CustomCampaignFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.Campaign;
import com.success.successEntellus.model.CampaignEmailTemplates;
import com.success.successEntellus.model.CampaignTemplate;
import com.success.successEntellus.model.GetAllCampaign;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCampaignActivity extends AppCompatActivity {

    EditText edt_campaign_name;
    RadioGroup rbg_campaign_type;
    RadioButton rb_predefine,rb_custom,rb_both,rb_none;
    LinearLayout ll_custom,ll_predefine;
    RelativeLayout ll_campaign_templates;
    Spinner sp_all_campaign;
    RecyclerView lv_campaign_templates;
    SPLib spLib;
    String user_id;
    List<Campaign> custom_campaignList;
    String[] campaign_array;
    String[] predefine_campaign_array;
    String[] all_campaign;
    String selected_campaign;
    List<Campaign> predefine_campaign_list,all_campaignsList;
    Button btn_save_campaign,btn_campaign_cancel,btn_campaign_dissmiss;
    public static List<String> createCampaignIds=new ArrayList<>();
    public static List<String> importCampaignIds=new ArrayList<>();
    String selectedCampaignIds="";
    CustomCampaignFragment customCampaignFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_campaign);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
        rb_none.setChecked(true);

        rb_predefine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    createCampaignIds.clear();
                    ll_predefine.setVisibility(View.VISIBLE);
                    ll_campaign_templates.setVisibility(View.GONE);
                    getPredefineCampaign();
                }
            }
        });

        rb_custom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    createCampaignIds.clear();
                    ll_predefine.setVisibility(View.VISIBLE);
                    ll_campaign_templates.setVisibility(View.GONE);
                    getAllCustomCampaign();
                }
            }
        });
        rb_both.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    createCampaignIds.clear();
                    ll_predefine.setVisibility(View.VISIBLE);
                    getAllCampaign();
                }
            }
        });
        rb_none.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_predefine.setVisibility(View.GONE);
                    ll_campaign_templates.setVisibility(View.GONE);
                }
            }
        });

        btn_save_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected_rb=rbg_campaign_type.getCheckedRadioButtonId();
                if (!edt_campaign_name.getText().toString().equals("")){
                    if (selected_rb!=R.id.rb_none){
                       // if (sp_all_campaign.getVisibility()==View.VISIBLE){
                        //if (!sp_all_campaign.getSelectedItem().toString().contains("Select")){
                            if (createCampaignIds.size()>0){
                                saveCampaign();
                            }else{
                                Toast.makeText(CreateCampaignActivity.this, "Please select at least 1 template..!", Toast.LENGTH_LONG).show();
                            }
                        /*}else{
                            MyValidator.setSpinnerError(sp_all_campaign,"Please Select Campaign..!");
                        }*/
                    //}
                    }else{
                        saveCampaign();
                    }

                }else{
                    edt_campaign_name.setError("Please Enter Campaign Name");
                }

            }
        });
        btn_campaign_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCampaignIds.clear();
                finish();
            }
        });

        btn_campaign_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCampaignIds.clear();
                finish();
            }
        });

        sp_all_campaign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_all_campaign.getSelectedItem().toString().contains("Select")) {
                    selected_campaign = "";
                } else {

                   int selected_rb= rbg_campaign_type.getCheckedRadioButtonId();
                   if (selected_rb==R.id.rb_predefine){
                       selected_campaign = predefine_campaign_list.get(position-1).getCampaignId();
                       getCampaignTemplateOnId();
                   }else if (selected_rb==R.id.rb_custom){
                       selected_campaign = custom_campaignList.get(position-1).getCampaignId();
                       getCampaignTemplateOnIdWithoutSelfReminder();
                   }else if (selected_rb==R.id.rb_both){
                       selected_campaign = all_campaignsList.get(position-1).getCampaignId();
                       String feature_campaign=all_campaignsList.get(position-1).getFeatureCampaign();
                       Log.d(Global.TAG, "Feature Campaign: "+feature_campaign);
                       if (feature_campaign.equals("1")){
                           getCampaignTemplateOnId();
                       }else if (feature_campaign.equals("0")){
                           getCampaignTemplateOnIdWithoutSelfReminder();
                       }
                   }
                    ll_campaign_templates.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });



    }

    private void saveCampaign() {
        selectedCampaignIds="";
        if (createCampaignIds.size()>0){
            for (String campaignStep : createCampaignIds)
            {
                selectedCampaignIds += campaignStep + ",";
            }
            if (selectedCampaignIds.endsWith(",")) {
                selectedCampaignIds = selectedCampaignIds.substring(0, selectedCampaignIds.length() - 1);
            }
        }
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignTitle", edt_campaign_name.getText().toString());
            paramObj.put("campaignSteps",selectedCampaignIds );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "saveCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "saveCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(CreateCampaignActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.create_campaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(CreateCampaignActivity.this, "Campaign Created Successfully..!", Toast.LENGTH_SHORT).show();
                    //customCampaignFragment.getAllCustomCampaign();
                    finish();
                    createCampaignIds.clear();
                }else{
                    Toast.makeText(CreateCampaignActivity.this, "Error in Creating Campaign..!", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:saveCampaign: "+t);
                Toast.makeText(CreateCampaignActivity.this, "Error in Creating Campaign..!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getPredefineCampaign() {

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
        Log.d(Global.TAG, "getPredefineCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(CreateCampaignActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllCampaign> call=service.get_company_Campaigns(paramObj.toString());
        call.enqueue(new Callback<GetAllCampaign>() {
            @Override
            public void onResponse(Call<GetAllCampaign> call, Response<GetAllCampaign> response) {
                GetAllCampaign getAllCampaign=response.body();
                if (getAllCampaign.isSuccess()){
                    predefine_campaign_list=getAllCampaign.getResult();
                    Log.d(Global.TAG, "campaignList: "+predefine_campaign_list.size());


                    predefine_campaign_array=new String[predefine_campaign_list.size()];
                    for (int i=0;i<predefine_campaign_list.size();i++){
                        if (predefine_campaign_list.get(i).getCampaignTitle().equals("")){
                            predefine_campaign_array[i]="No Heading";
                        }else{
                            predefine_campaign_array[i]=predefine_campaign_list.get(i).getCampaignTitle();
                        }

                    }

                    applySpinner(predefine_campaign_array,sp_all_campaign,"--Select Campaign--");
                    /*
                    if (campaignList.size()>0){
                        CompanyGridAdapter adapter=new CompanyGridAdapter(dashboardActivity,campaignList,user_id);
                        rv_company_campaign.setAdapter(adapter);
                    }*/
                }else{
                    Toast.makeText(CreateCampaignActivity.this, "Error in getting campaigns..!", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllCampaign> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(CreateCampaignActivity.this, "Error in getting campaigns..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure: getAllCampaign"+t);
            }
        });
    }

    private void getAllCampaign() {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAllCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(CreateCampaignActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllCampaign> call=service.get_all_campaign(paramObj.toString());
        call.enqueue(new Callback<GetAllCampaign>() {
            @Override
            public void onResponse(Call<GetAllCampaign> call, Response<GetAllCampaign> response) {
                GetAllCampaign getAllCampaign=response.body();
                if (getAllCampaign.isSuccess()){
                    all_campaignsList=getAllCampaign.getResult();
                    Log.d(Global.TAG, "campaignList: "+all_campaignsList.size());


                    all_campaign=new String[all_campaignsList.size()];
                    for (int i=0;i<all_campaignsList.size();i++){
                        //Log.d(Global.TAG, "onResponse:Title: "+all_campaignsList.get(i).getCampaignTitle());
                       if (all_campaignsList.get(i).getCampaignTitle().equals("")){
                           Log.d(Global.TAG, "onResponse: No heading");
                           all_campaign[i]="No Heading";
                       }else{
                           all_campaign[i]=all_campaignsList.get(i).getCampaignTitle();
                       }

                    }

                    applySpinner(all_campaign,sp_all_campaign,"--Select Campaign--");
                    /*
                    if (campaignList.size()>0){
                        CompanyGridAdapter adapter=new CompanyGridAdapter(dashboardActivity,campaignList,user_id);
                        rv_company_campaign.setAdapter(adapter);
                    }*/
                }else{
                    Toast.makeText(CreateCampaignActivity.this, "Error in getting campaigns..!", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllCampaign> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(CreateCampaignActivity.this, "Error in getting campaigns..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure: getAllCampaign"+t);
            }
        });
    }

    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(CreateCampaignActivity.this, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }

    private void getAllCustomCampaign() {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }

        final Dialog myLoader = Global.showDialog(CreateCampaignActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getAllCustomCampaign: "+paramObj.toString());
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllCampaign> call=service.get_company_Campaigns(paramObj.toString());
        call.enqueue(new Callback<GetAllCampaign>() {
            @Override
            public void onResponse(Call<GetAllCampaign> call, Response<GetAllCampaign> response) {
                GetAllCampaign getAllCampaign=response.body();
                if (getAllCampaign.isSuccess()){
                    custom_campaignList=getAllCampaign.getResult();
                    Log.d(Global.TAG, "campaignList: "+custom_campaignList.size());

                    campaign_array=new String[custom_campaignList.size()];
                    for (int i=0;i<custom_campaignList.size();i++){
                        if (custom_campaignList.get(i).getCampaignTitle().equals("")){
                            campaign_array[i]="No Heading";
                        }else{
                            campaign_array[i]=custom_campaignList.get(i).getCampaignTitle();
                        }
                    }
                    applySpinner(campaign_array,sp_all_campaign,"--Select Campaign--");

                   /* if (campaignList.size()>0){
                        CustomCampaignAdapter adapter=new CustomCampaignAdapter(dashboardActivity,campaignList, user_id);
                        rv_custom_campaign.setAdapter(adapter);
                    }*/
                }else{
                    Toast.makeText(CreateCampaignActivity.this, "Error in getting campaigns..!", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllCampaign> call, Throwable t) {
                Toast.makeText(CreateCampaignActivity.this, "Error in getting campaigns..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure: getAllCampaign"+t);
                myLoader.dismiss();
            }
        });
    }
    private void init() {
        edt_campaign_name=(EditText)findViewById(R.id.edt_campaign_name);
        rbg_campaign_type=(RadioGroup) findViewById(R.id.rbg_campaign_type);
        rb_predefine=(RadioButton) findViewById(R.id.rb_predefine);
        rb_custom=(RadioButton) findViewById(R.id.rb_custom);
        rb_both=(RadioButton)findViewById(R.id.rb_both);
        rb_none=(RadioButton)findViewById(R.id.rb_none);
        //ll_custom=(LinearLayout) findViewById(R.id.ll_custom);
        ll_predefine=(LinearLayout) findViewById(R.id.ll_predefine);
        ll_campaign_templates=(RelativeLayout) findViewById(R.id.ll_campaign_templates);
        sp_all_campaign=(Spinner) findViewById(R.id.sp_all_campaign);
        lv_campaign_templates=(RecyclerView) findViewById(R.id.lv_campaign_templates);
        lv_campaign_templates.setLayoutManager(new LinearLayoutManager(CreateCampaignActivity.this));
        customCampaignFragment=new CustomCampaignFragment();

        spLib=new SPLib(CreateCampaignActivity.this);
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        all_campaignsList=new ArrayList<>();
        predefine_campaign_list=new ArrayList<>();
        //createCampaignIds=new ArrayList<>();

        btn_save_campaign=(Button)findViewById(R.id.btn_save_campaign);
        btn_campaign_cancel=(Button)findViewById(R.id.btn_campaign_cancel);
        btn_campaign_dissmiss=(Button)findViewById(R.id.btn_campaign_dissmiss);

    }

    private void getCampaignTemplateOnId() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId", selected_campaign);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }

        final Dialog myLoader = Global.showDialog(CreateCampaignActivity.this);
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
                    TemplateListAdapter templateListAdapter=new TemplateListAdapter(CreateCampaignActivity.this,campaignTemplates,false,false);
                    lv_campaign_templates.setAdapter(templateListAdapter);
//                    CampaignTemplateAdapter adapter=new CampaignTemplateAdapter(getActivity(),campaignTemplates);
//                    rv_campaign_templates.setAdapter(adapter);

                }else{
                    Log.d(Global.TAG, "Error in getting templates: ");
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                Log.d(Global.TAG, " getCampaignTemplateOnId onFailure: "+t);
                myLoader.dismiss();
            }
        });
    }
    private void getCampaignTemplateOnIdWithoutSelfReminder() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId", selected_campaign);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllCampaign: "+e);
            e.printStackTrace();
        }

        final Dialog myLoader = Global.showDialog(CreateCampaignActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getCampaignTemplateOnIdWithoutSelfReminder: "+paramObj.toString());
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<CampaignEmailTemplates> call=service.get_custom_templateOnId(paramObj.toString());
        call.enqueue(new Callback<CampaignEmailTemplates>() {
            @Override
            public void onResponse(Call<CampaignEmailTemplates> call, Response<CampaignEmailTemplates> response) {
                CampaignEmailTemplates campaignEmailTemplates=response.body();
                if (campaignEmailTemplates.isSuccess()){
                    List<CampaignTemplate> campaignTemplates=campaignEmailTemplates.getResult();
                    Log.d(Global.TAG, "campaignTemplates: "+campaignTemplates.size());

                    TemplateListAdapter templateListAdapter=new TemplateListAdapter(CreateCampaignActivity.this,campaignTemplates, false,false);
                    lv_campaign_templates.setAdapter(templateListAdapter);
//                    CampaignTemplateAdapter adapter=new CampaignTemplateAdapter(getActivity(),campaignTemplates);
//                    rv_campaign_templates.setAdapter(adapter);

                }else{
                    Log.d(Global.TAG, "Error in getting templates: ");
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CampaignEmailTemplates> call, Throwable t) {
                Log.d(Global.TAG, " getCampaignTemplateOnId onFailure: "+t);
                myLoader.dismiss();
            }
        });
    }
}
