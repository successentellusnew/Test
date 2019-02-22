package com.success.successEntellus.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.CreateCampaignActivity;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.activity.OnboardingEmailCampaign;
import com.success.successEntellus.adapter.AddEmailAdapter;
import com.success.successEntellus.adapter.AddMemberAdapter;
import com.success.successEntellus.adapter.CustomCampaignAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.AllGroups;
import com.success.successEntellus.model.Campaign;
import com.success.successEntellus.model.Contact;
import com.success.successEntellus.model.ContactDetails;
import com.success.successEntellus.model.GetAllCampaign;
import com.success.successEntellus.model.GroupInfo;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 5/7/2018.
 */

public class CustomCampaignFragment extends Fragment implements CustomCampaignAdapter.NotifyRefreshCampaigns,AddEmailAdapter.NotifyAddRemoveEmail {

    private static String crm_flag="1";
    private static String schedule_msg="";
    View layout;
    public static Context context;
    public static RecyclerView rv_custom_campaign, rv_add_email;
    static String user_id;
    SPLib spLib;
    static DashboardActivity dashboardActivity;
    static List<Campaign> campaignList=new ArrayList<>();
    static String[] group_array;
    static Spinner sp_select_email_group;
    static String[] campaign_array;
    static String selected_group;
    static String selected_campaign;
    static List<GroupInfo> groupList;
    static EditText edt_email_firstName,edt_email_lastName,edt_add_email,edt_add_phone;
    public static Dialog dialog;
    public static MyValidator myValidator;
    public static List<String> selected_all_contact_list=new ArrayList<>();
    public static List<String> selected_contact_list=new ArrayList<>();
    public static List<String> selected_prospect_list=new ArrayList<>();
    public static List<String> selected_customers_list=new ArrayList<>();
    public static List<String> selected_recruits_list=new ArrayList<>();
    public static List<Contact> contactList=new ArrayList<>();
    public static TextView tv_selected_emails;
    private static String addEmailList="";
    public static CustomCampaignFragment customCampaignFragment;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=getActivity();
        layout=inflater.inflate(R.layout.fragment_custom_campaign,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        setHasOptionsMenu(true);
        init();
        getAllCustomCampaign();
        return layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        rv_custom_campaign=(RecyclerView) layout.findViewById(R.id.rv_custom_campaign);
        rv_custom_campaign.setLayoutManager(new LinearLayoutManager(getActivity()));
        spLib=new SPLib(getActivity());
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        dashboardActivity= (DashboardActivity) this.getActivity();
        group_array=new String[15];
        groupList=new ArrayList<>();
        myValidator=new MyValidator();
        customCampaignFragment=new CustomCampaignFragment();

        int resId = R.anim.layout_anim;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
        rv_custom_campaign.setLayoutAnimation(animation);

    }


    public void getAllCustomCampaign() {
        campaignList.clear();
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
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllCampaign> call=service.get_company_Campaigns(paramObj.toString());
        call.enqueue(new Callback<GetAllCampaign>() {
            @Override
            public void onResponse(Call<GetAllCampaign> call, Response<GetAllCampaign> response) {
                GetAllCampaign getAllCampaign=response.body();
                if (getAllCampaign.isSuccess()){
                    campaignList=getAllCampaign.getResult();
                    Log.d(Global.TAG, "campaignList: "+campaignList.size());

                    campaign_array=new String[campaignList.size()];
                    for (int i=0;i<campaignList.size();i++){
                        campaign_array[i]=campaignList.get(i).getCampaignTitle();
                    }

                    if (campaignList.size()>0){
                        CustomCampaignAdapter adapter=new CustomCampaignAdapter(dashboardActivity,campaignList, user_id,CustomCampaignFragment.this);
                        adapter.notifyDataSetChanged();
                        rv_custom_campaign.setAdapter(adapter);
                    }
                }else{
                    Toast.makeText(context, "No Custom Campaigns Available..!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetAllCampaign> call, Throwable t) {
                Toast.makeText(context, "No Custom Campaigns Available..!", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure: getAllCampaign"+t);
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.email_campaign_menu, menu);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Email Campaigns");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case R.id.action_add_email:
                Toast.makeText(dashboardActivity, "Add Email", Toast.LENGTH_SHORT).show();
                openDialogAddEmail(false);
                return true;*/
            case R.id.action_add_campaign:
                Intent intent=new Intent(getActivity(),CreateCampaignActivity.class);
                startActivityForResult(intent,300);
                Toast.makeText(dashboardActivity, "Add Campaign", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_email_camp_help:
                OpenDialogHelpOptions();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void OpenDialogHelpOptions() {
        final CharSequence[] options = {"How to create Email campaign..?", "How to add email template in Campaign..?",
                "How to add email to Email campaign", "How to import Email campaign..?",
                "How to set a Self reminder..?"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Help..!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("How to create Email campaign..?")) {
                    Intent intent=new Intent(getActivity(),OnboardingEmailCampaign.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("keyString","create_campaign");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    dialog.dismiss();
                } else if (options[item].equals("How to add email template in Campaign..?")) {
                    Intent intent=new Intent(getActivity(),OnboardingEmailCampaign.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("keyString","create_template");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    dialog.dismiss();
                } else if (options[item].equals("How to add email to Email campaign")) {
                    Intent intent=new Intent(getActivity(),OnboardingEmailCampaign.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("keyString","add_email");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    dialog.dismiss();
                } else if (options[item].equals("How to import Email campaign..?")) {
                    Intent intent=new Intent(getActivity(),OnboardingEmailCampaign.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("keyString","import_template");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    dialog.dismiss();
                } else if (options[item].equals("How to set a Self reminder..?")) {
                    Intent intent=new Intent(getActivity(),OnboardingEmailCampaign.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("keyString","self_reminder");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

   /* public static void openDialogAddEmail(final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_email_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        Button btn_add_email = (Button) dialog.findViewById(R.id.btn_add_email);
        TextView tv_campaign_name_add_email = (TextView) dialog.findViewById(R.id.tv_campaign_name_add_email);
        Button btn_email_cancel = (Button) dialog.findViewById(R.id.btn_email_cancel);
        Button btn_edissmiss = (Button) dialog.findViewById(R.id.btn_edissmiss);
       // LinearLayout ll_email_search = (LinearLayout) dialog.findViewById(R.id.ll_email_search);
        //SearchView search_email = (SearchView) dialog.findViewById(R.id.search_email);
        tv_campaign_name_add_email.setText(campaignList.get(position).getCampaignTitle());
        edt_email_firstName = (EditText) dialog.findViewById(R.id.edt_email_firstName);
        edt_email_lastName = (EditText) dialog.findViewById(R.id.edt_email_lastName);
        edt_add_email = (EditText) dialog.findViewById(R.id.edt_add_email);
        //edt_add_phone = (EditText) dialog.findViewById(R.id.edt_add_phone);

        //final Spinner sp_select_campaign = (Spinner) dialog.findViewById(R.id.sp_select_campaign);
        sp_select_email_group = (Spinner) dialog.findViewById(R.id.sp_select_email_group);

        getAllGroups();
        //applySpinner(campaign_array,sp_select_campaign,"--Select Campaign--");
        btn_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myValidator.isValidField(edt_email_firstName))
                {
                    if (myValidator.isValidField(edt_email_lastName)){

                        if (myValidator.isValidEmailAdd(edt_add_email)){

                           // if (myValidator.isValidMobile(edt_add_phone)){
                                addEmail(position);
                           // }
                        }
                    }else{
                        edt_email_lastName.setError("Enter Last Name..!");
                    }
                }else{
                    edt_email_firstName.setError("Enter First Name..!");
                }
            }
        });

        btn_edissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_email_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        sp_select_email_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (group_array.length==1){
                    Toast.makeText(context, "Groups Not Available..!", Toast.LENGTH_SHORT).show();
                    selected_group = "";
                }else {
                    Log.d(Global.TAG, "Position: " + position);
                    if (sp_select_email_group.getSelectedItem().toString().contains("Select")) {
                        selected_group = "";
                    } else {
                        //selected_group = group_array[position - 1];
                        selected_group = groupList.get(position-1).getGroup_id();
                        Log.d(Global.TAG, "onItemSelected: sp_select_group " + selected_group);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

       *//* sp_select_campaign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_select_campaign.getSelectedItem().toString().contains("Select")) {
                    selected_campaign = "";
                } else {
                    selected_campaign = campaignList.get(position-1).getCampaignId();
                    Log.d(Global.TAG, "selected_campaign: "+selected_campaign);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
*//*

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
*/
    public static void openDialogAddEmailBoth(final int position, final String scheduleMsg) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_email_to_email_campaign);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

       // Button btn_add_email = (Button) dialog.findViewById(R.id.btn_add_email);
        //TextView tv_campaign_name_add_email = (TextView) dialog.findViewById(R.id.tv_campaign_name_add_email);
        tv_selected_emails = (TextView) dialog.findViewById(R.id.tv_selected_emails);
        TextView tv_camp_name = (TextView) dialog.findViewById(R.id.tv_camp_name);
        Button btn_email_cancel = (Button) dialog.findViewById(R.id.btn_email_cancel);
        Button btn_add_email_start_camp = (Button) dialog.findViewById(R.id.btn_add_email_start_camp);
        Button btn_edissmiss = (Button) dialog.findViewById(R.id.btn_edissmiss);
        RadioButton rb_add_from_system_contacts=(RadioButton) dialog.findViewById(R.id.rb_add_from_system_contacts);
        RadioButton add_email_manually=(RadioButton) dialog.findViewById(R.id.add_email_manually);
        final RadioGroup rbg_add_email_from=(RadioGroup) dialog.findViewById(R.id.rbg_add_email_from);

        getScheduledMessage(position);

        final ToggleButton tv_my_contact_email=(ToggleButton) dialog.findViewById(R.id.tv_my_contact_email);
        final ToggleButton tv_my_prospect_email=(ToggleButton) dialog.findViewById(R.id.tv_my_prospect_email);
        final ToggleButton tv_my_customers_email=(ToggleButton) dialog.findViewById(R.id.tv_my_customers_email);
        final ToggleButton tv_my_recruit_email=(ToggleButton) dialog.findViewById(R.id.tv_my_recruit_email);
        SearchView search_add_email=(SearchView) dialog.findViewById(R.id.search_add_email);
         rv_add_email=(RecyclerView) dialog.findViewById(R.id.rv_add_email);
        rv_add_email.setLayoutManager(new LinearLayoutManager(context));

        final LinearLayout ll_add_manually=(LinearLayout)dialog.findViewById(R.id.ll_add_manually);
        final LinearLayout ll_add_from_system=(LinearLayout)dialog.findViewById(R.id.ll_add_from_system);

        edt_email_firstName = (EditText) dialog.findViewById(R.id.edt_email_firstName);
        edt_email_lastName = (EditText) dialog.findViewById(R.id.edt_email_lastName);
        edt_add_email = (EditText) dialog.findViewById(R.id.edt_add_email);
        sp_select_email_group = (Spinner) dialog.findViewById(R.id.sp_select_email_group);

        tv_camp_name.setText(campaignList.get(position).getCampaignTitle());
        rb_add_from_system_contacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_add_from_system.setVisibility(View.VISIBLE);
                    ll_add_manually.setVisibility(View.GONE);
                }else{
                    ll_add_from_system.setVisibility(View.GONE);
                    ll_add_manually.setVisibility(View.VISIBLE);
                }
            }
        });

        add_email_manually.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_add_from_system.setVisibility(View.GONE);
                    ll_add_manually.setVisibility(View.VISIBLE);
                }else{
                    ll_add_from_system.setVisibility(View.VISIBLE);
                    ll_add_manually.setVisibility(View.GONE);
                }
            }
        });

        tv_my_contact_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_contact checked");
                    tv_my_contact_email.setBackgroundColor(context.getResources().getColor(R.color.colorOragne));
                    tv_my_prospect_email.setChecked(false);
                    tv_my_customers_email.setChecked(false);
                    tv_my_recruit_email.setChecked(false);
                    //tv_selected_members.setText(""+selected_contact.size()+" Contact Selected");
                    getContactsListForEmailCampaign("1");
                    crm_flag="1";
                    tv_selected_emails.setText(selected_contact_list.size()+" Contacts Selected");
                }else{
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_contact unchecked");
                    tv_my_contact_email.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }

            }
        });
        tv_my_prospect_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_prospect checked");
                    tv_my_prospect_email.setBackgroundColor(context.getResources().getColor(R.color.colorOragne));
                    tv_my_contact_email.setChecked(false);
                    tv_my_customers_email.setChecked(false);
                    tv_my_recruit_email.setChecked(false);
                    //tv_selected_contacts.setText(""+selected_prospects.size()+" Prospects Selected");
                    getContactsListForEmailCampaign("3");
                    crm_flag="3";
                    tv_selected_emails.setText(selected_prospect_list.size()+" Prospects Selected");
                }else{
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_prospect unchecked");
                    tv_my_prospect_email.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }
            }
        });
        tv_my_customers_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_customers checked");
                    tv_my_customers_email.setBackgroundColor(context.getResources().getColor(R.color.colorOragne));
                    tv_my_contact_email.setChecked(false);
                    tv_my_prospect_email.setChecked(false);
                    tv_my_recruit_email.setChecked(false);
                    //tv_selected_contacts.setText(""+selected_customers.size()+" Customers Selected");
                    getContactsListForEmailCampaign("2");
                    crm_flag="2";
                    tv_selected_emails.setText(selected_customers_list.size()+" Customers Selected");
                }else{
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_customers unchecked");
                    tv_my_customers_email.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }
            }
        });
        tv_my_recruit_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_recruit_member checked");
                    tv_my_recruit_email.setBackgroundColor(context.getResources().getColor(R.color.colorOragne));
                    tv_my_contact_email.setChecked(false);
                    tv_my_prospect_email.setChecked(false);
                    tv_my_customers_email.setChecked(false);
                    //tv_selected_contacts.setText(""+selected_customers.size()+" Customers Selected");
                    getContactsListForEmailCampaign("4");
                    crm_flag="4";
                    tv_selected_emails.setText(selected_recruits_list.size()+" Recruits Selected");
                }else{
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_recruit_member unchecked");
                    tv_my_recruit_email.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }
            }
        });
        tv_my_contact_email.setChecked(true);


        //tv_campaign_name_add_email.setText(campaignList.get(position).getCampaignTitle());
        getAllGroups();


        /*btn_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected_rb=rbg_add_email_from.getCheckedRadioButtonId();
                if (selected_rb==R.id.rb_add_from_system_contacts){
                    if (selected_all_contact_list.size()>0){
                        assignEmailFromContacts(dialog,position,"0");
                    }else{
                        Toast.makeText(context, "Please Select at least One Member to Add..!", Toast.LENGTH_LONG).show();
                    }

                }else if (selected_rb==R.id.add_email_manually){
                    if (myValidator.isValidField(edt_email_firstName))
                    {
                        if (myValidator.isValidField(edt_email_lastName)){

                            if (myValidator.isValidEmailAdd(edt_add_email)){
                                addEmail(position,"0");
                            }
                        }else{
                            edt_email_lastName.setError("Enter Last Name..!");
                        }
                    }else{
                        edt_email_firstName.setError("Enter First Name..!");
                    }
                }

            }
        });
*/
        btn_add_email_start_camp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected_rb=rbg_add_email_from.getCheckedRadioButtonId();
                if (selected_rb==R.id.rb_add_from_system_contacts){
                    if (selected_all_contact_list.size()>0){
                        if (!schedule_msg.equals("")){
                            new android.support.v7.app.AlertDialog.Builder(context)
                                    .setMessage(schedule_msg)
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog1, int id) {
                                            assignEmailFromContacts(dialog,position,"1");
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();

                        }else{
                            new android.support.v7.app.AlertDialog.Builder(context)
                                    .setMessage("Email will be sent at scheduled time. Do you want to proceed ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog1, int id) {
                                            assignEmailFromContacts(dialog,position,"1");
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();

                        }


                    }else{
                        Toast.makeText(context, "Please Select at least One Member to Add..!", Toast.LENGTH_LONG).show();
                    }

                }else if (selected_rb==R.id.add_email_manually){
                    if (myValidator.isValidField(edt_email_firstName))
                    {
                        if (myValidator.isValidField(edt_email_lastName)){

                            if (myValidator.isValidEmailAdd(edt_add_email)){

                                if (!schedule_msg.equals("")){
                                    new android.support.v7.app.AlertDialog.Builder(context)
                                            .setMessage(schedule_msg)
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog1, int id) {
                                                    addEmail(position,"1");
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();

                                }else{
                                    new android.support.v7.app.AlertDialog.Builder(context)
                                            .setMessage("Email will be sent at scheduled time. Do you want to proceed ?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog1, int id) {
                                                    addEmail(position,"1");
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();

                                }

                            }
                        }else{
                            edt_email_lastName.setError("Enter Last Name..!");
                        }
                    }else{
                        edt_email_firstName.setError("Enter First Name..!");
                    }
                }
            }
        });

        btn_edissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_all_contact_list.clear();
                selected_contact_list.clear();
                selected_customers_list.clear();
                selected_prospect_list.clear();
                selected_recruits_list.clear();
                dialog.dismiss();
            }
        });

        btn_email_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_all_contact_list.clear();
                selected_contact_list.clear();
                selected_customers_list.clear();
                selected_prospect_list.clear();
                selected_recruits_list.clear();
                dialog.dismiss();
            }
        });

        sp_select_email_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (group_array.length==1){
                    Toast.makeText(context, "Groups Not Available..!", Toast.LENGTH_LONG).show();
                    selected_group = "";
                }else {
                    Log.d(Global.TAG, "Position: " + position);
                    if (sp_select_email_group.getSelectedItem().toString().contains("Select")) {
                        selected_group = "";
                    } else {
                        //selected_group = group_array[position - 1];
                        selected_group = groupList.get(position-1).getGroup_id();
                        Log.d(Global.TAG, "onItemSelected: sp_select_group " + selected_group);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

       /* sp_select_campaign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_select_campaign.getSelectedItem().toString().contains("Select")) {
                    selected_campaign = "";
                } else {
                    selected_campaign = campaignList.get(position-1).getCampaignId();
                    Log.d(Global.TAG, "selected_campaign: "+selected_campaign);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
*/

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private static void getScheduledMessage(int position) {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId",campaignList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getScheduledMessage: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getScheduledMessage: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.getEmailScheduleMessage(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()){
                    JsonResult jsonResult=response.body();
                    if (jsonResult.isSuccess()){
                        schedule_msg=jsonResult.getResult();
                        Log.d(Global.TAG, "schedule_msg: "+schedule_msg);
                    }else{
                        schedule_msg=jsonResult.getResult();
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: "+t);
            }
        });
    }

    private static void assignEmailFromContacts(final Dialog dialog, int position,String addAndAssinged) {
        addEmailList="";
        if (selected_all_contact_list.size()>0){
            for (String contact : selected_all_contact_list)
            {
                addEmailList += contact + ",";
            }
            if (addEmailList.endsWith(",")) {
                addEmailList = addEmailList.substring(0, addEmailList.length() - 1);
            }
        }
        Log.d(Global.TAG, "assignEmailFromContacts: "+selected_all_contact_list.size());
        Log.d(Global.TAG, "assignEmailFromContacts: "+addEmailList);

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contact_ids",addEmailList);
            paramObj.put("addAndAssinged",addAndAssinged);
            paramObj.put("contactCampaignAssignId",campaignList.get(position).getCampaignId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "assignEmailFromContacts: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "assignEmailFromContacts: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.add_email_from_contact(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    selected_all_contact_list.clear();
                    selected_contact_list.clear();
                    selected_customers_list.clear();
                    selected_prospect_list.clear();
                    selected_recruits_list.clear();
                    //getAllCustomCampaign();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure assignEmailFromContacts: "+t);
            }
        });
    }

    private static void getContactsListForEmailCampaign(final String crm_flag) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("crmFlag",crm_flag);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getContactsListForEmailCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getContactsListForEmailCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<ContactDetails> call=service.getContactListEmailCampaign(paramObj.toString());
        call.enqueue(new Callback<ContactDetails>() {
            @Override
            public void onResponse(Call<ContactDetails> call, Response<ContactDetails> response) {
                ContactDetails contactDetails=response.body();
                if (contactDetails.isSuccess()){
                    contactList=contactDetails.getResult();
                    Log.d(Global.TAG, "onResponse:contactList "+contactList.size());
                    if (contactList.size()>0){
                        rv_add_email.setVisibility(View.VISIBLE);
                       /* AddGroupContactsAdapter adapter=new AddGroupContactsAdapter(context,contactList,user_id,crm_flag,false);
                        adapter.notifyDataSetChanged();*/
                        AddEmailAdapter adapter=new AddEmailAdapter(context,contactList,crm_flag,customCampaignFragment);
                        rv_add_email.setAdapter(adapter);

                    }else{
                        rv_add_email.setVisibility(View.GONE);
                        Toast.makeText(context, "No Contacts Available", Toast.LENGTH_LONG).show();
                    }
                }else{
                    rv_add_email.setVisibility(View.GONE);
                    Toast.makeText(context, "No Contacts Available", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<ContactDetails> call, Throwable t) {
                rv_add_email.setVisibility(View.GONE);
                if (crm_flag.equals("1")){
                    Toast.makeText(context, "No Contacts Available", Toast.LENGTH_LONG).show();
                }else if (crm_flag.equals("2")){
                    Toast.makeText(context, "No Customers Available", Toast.LENGTH_LONG).show();
                }else if (crm_flag.equals("3")){
                    Toast.makeText(context, "No Prospect Available", Toast.LENGTH_LONG).show();
                }else if (crm_flag.equals("4")){
                    Toast.makeText(context, "No Recruits Available", Toast.LENGTH_LONG).show();
                }

                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: ");
            }
        });

    }

    private static void getAllGroups() {

        JSONObject param=new JSONObject();
        try {
            param.put("userId",user_id);
            param.put("platform",2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(Global.TAG, "Add Email:param "+param.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<AllGroups> call=service.getAllGroups(param.toString());
        call.enqueue(new Callback<AllGroups>() {
            @Override
            public void onResponse(Call<AllGroups> call, Response<AllGroups> response) {
                AllGroups allGroups=response.body();
                if (allGroups.isSuccess()){
                   groupList=allGroups.getResult();
                    Log.d(Global.TAG, "groupList: "+groupList.size());
                    String[] group_array=new String[groupList.size()];
                    // group_array[0]="Select Group";
                    for(int i=0;i<groupList.size();i++){
                        group_array[i]=groupList.get(i).getGroup_name();
                    }
                    applySpinner(group_array,sp_select_email_group,"--Select Group--");


                }else{
                    group_array=new String[1];
                    applySpinner(group_array,sp_select_email_group,"--Select Group--");
                    Log.d(Global.TAG, "Groups Not Available..!: ");
                    sp_select_email_group.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<AllGroups> call, Throwable t) {
                Log.d(Global.TAG, "Groups Not Available..!: "+t);
            }
        });


    }
    private static void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(context, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }

    private static void addEmail(int position,String addAndAssinged) {
        JSONObject param=new JSONObject();
        try {
            param.put("userId",user_id);
            param.put("platform",2);
            param.put("fname",edt_email_firstName.getText().toString());
            param.put("lname",edt_email_lastName.getText().toString());
            param.put("email",edt_add_email.getText().toString());
            param.put("campaignId",campaignList.get(position).getCampaignId());
            param.put("groupId",selected_group);
            param.put("addAndAssinged",addAndAssinged);
            param.put("phone","");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        Log.d(Global.TAG, "Add Email: "+param.toString());
        Log.d(Global.TAG, "Add Email groupId: "+selected_group);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.assign_email_campaign(param.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult!=null){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, "Email Added Successfully..!", Toast.LENGTH_LONG).show();
                        //getAllCustomCampaign();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(context, "Email Already Exists..!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Toast.makeText(context, "Email Already Exists..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure: Add Email "+t);
                myLoader.dismiss();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==300){
            getAllCustomCampaign();
        }
    }

    @Override
    public void refreshCampaigns() {
        getAllCustomCampaign();
    }

    @Override
    public void addEmail(String contact_id) {
        selected_all_contact_list.add(contact_id);
        if (crm_flag.equals("1")){
            selected_contact_list.add(contact_id);
            tv_selected_emails.setText(selected_contact_list.size()+" Contacts Selected");
        }else if (crm_flag.equals("2")){
            selected_customers_list.add(contact_id);
            tv_selected_emails.setText(selected_customers_list.size()+" Customers Selected");
        }else if (crm_flag.equals("3")){
            selected_prospect_list.add(contact_id);
            tv_selected_emails.setText(selected_prospect_list.size()+" Prospects Selected");
        }else if (crm_flag.equals("4")){
            selected_recruits_list.add(contact_id);
            tv_selected_emails.setText(selected_recruits_list.size()+" Recruits Selected");
        }
    }

    @Override
    public void removeEmail(String contact_id) {
        selected_all_contact_list.remove(contact_id);
        if (crm_flag.equals("1")){
            selected_contact_list.remove(contact_id);
            tv_selected_emails.setText(selected_contact_list.size()+" Contacts Selected");
        }else if (crm_flag.equals("2")){
            selected_customers_list.remove(contact_id);
            tv_selected_emails.setText(selected_customers_list.size()+" Customers Selected");
        }else if (crm_flag.equals("3")){
            selected_prospect_list.remove(contact_id);
            tv_selected_emails.setText(selected_prospect_list.size()+" Prospects Selected");
        }else if (crm_flag.equals("4")){
            selected_recruits_list.remove(contact_id);
            tv_selected_emails.setText(selected_recruits_list.size()+" Recruits Selected");
        }
    }
}
