package com.success.successEntellus.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.success.successEntellus.adapter.AddGroupContactsAdapter;
import com.success.successEntellus.adapter.GroupCampaignAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.lib.csv.FileUtils;
import com.success.successEntellus.model.Campaign;
import com.success.successEntellus.model.Contact;
import com.success.successEntellus.model.ContactDetails;
import com.success.successEntellus.model.GetAllCampaign;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupActivity extends AppCompatActivity implements AddGroupContactsAdapter.NotifyInAddGroup {

    private static final int CSV_REQUEST = 8009;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =20005 ;
    Button btn_group_close, btn_save_group, btn_edit_group, btn_contact_cancel;
    SearchView search_add_group;
    RecyclerView rv_add_group;
    ToggleButton tv_my_contact, tv_my_prospect, tv_my_customers, tv_my_recruits;
    EditText edt_group_name,edt_group_desc;
    String user_id;
    SPLib spLib;
    TextView tv_addgroup_title;
    public static TextView tv_selected_contacts;
    public static int count;
    boolean editFlag = false;
    String group_name, group_id = "0",group_desc="";
    String[] campaignListforSpinner;
    String[] campaignIdListforSpinner;

    String selected_contacts_id = "";
    public static List<String> selected_group_contact = new ArrayList<>();
    public static List<String> selected_edit_group_contact = new ArrayList<>();
    public static List<String> selected_contact = new ArrayList<>();
    public static List<String> selected_prospects = new ArrayList<>();
    public static List<String> selected_customers = new ArrayList<>();
    public static List<String> selected_recruits = new ArrayList<>();
    MyValidator myValidator;
    List<Contact> addcontactList, search_list;
    public String crm_flag;
    NotifyGroupList notifyGroupList;
    RadioButton rb_from_system, rb_import_csv,rb_email_campaign;
    RadioButton rb_read_emails,rb_unread_emails;
    LinearLayout ll_import_csv, ll_system_contacts,ll_from_email_campaigns,ll_display_list_read_unread;
    Button btn_select_file;
    TextView tv_selected_csv,tv_click_here;
    RadioGroup rbg_select_from;
    File selected_csv_file;
    Spinner sp_campaign_in_group;
    RecyclerView rv_emails_from_campaigns;
    private String campaignId="";
    private String readFlag="1";
    public static CheckBox ch_select_all_contacts;
    public static boolean select_all,select_all_contacts=false,select_all_prospects=false,select_all_cust=false,select_all_recruits=false;

    public interface NotifyGroupList {
        public void refreshGroupList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group_layout);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            group_name = bundle.getString("group_name");
            group_desc = bundle.getString("group_desc");
            group_id = bundle.getString("group_id");
            editFlag = bundle.getBoolean("editFlag");
            Log.d(Global.TAG, "group_name: " + group_name);
        }

        if (editFlag) {
            btn_save_group.setVisibility(View.GONE);
            btn_edit_group.setVisibility(View.VISIBLE);
            edt_group_name.setText(group_name);
            edt_group_desc.setText(group_desc);
            tv_addgroup_title.setText("Edit Group Details");
            rb_email_campaign.setVisibility(View.GONE);

            selected_edit_group_contact.clear();
            selected_edit_group_contact.addAll(selected_contact);
            selected_edit_group_contact.addAll(selected_prospects);
            selected_edit_group_contact.addAll(selected_customers);
            selected_edit_group_contact.addAll(selected_recruits);
            Log.d(Global.TAG, "AddGroup:: " + selected_edit_group_contact);
        }

       /* if (!editFlag) {
            Log.d(Global.TAG, "editFlag clear: ");
            rb_email_campaign.setVisibility(View.VISIBLE);
            selected_group_contact.clear();
            selected_contact.clear();
            selected_prospects.clear();
            selected_recruits.clear();
            selected_customers.clear();

            Log.d(Global.TAG, "Selected Contacts: " + selected_contact.size());
            tv_selected_contacts.setText(selected_contact.size() + " Contacts Selected.");
        }*/

       /* tv_my_contact.setBackgroundColor(getResources().getColor(R.color.colorOragne));
        tv_my_prospect.setChecked(false);
        tv_my_customers.setChecked(false);
        tv_selected_contacts.setText(""+selected_contact.size()+" Contact Selected");
        getContactDetails("1");
        crm_flag="1";*/

       ch_select_all_contacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
               if (checked){
                   select_all=true;
                   if (crm_flag.equals("1")){
                       select_all_contacts=true;
                   }else  if (crm_flag.equals("2")){
                       select_all_cust=true;
                   }else  if (crm_flag.equals("3")){
                       select_all_prospects=true;
                   }else  if (crm_flag.equals("4")){
                       select_all_recruits=true;
                   }

                   if (addcontactList.size()>0){
                       for (int i=0;i<addcontactList.size();i++){
                           if (!editFlag){
                               if (!selected_group_contact.contains(addcontactList.get(i).getContact_id())){
                                   selected_group_contact.add(addcontactList.get(i).getContact_id());
                               }

                           }else{
                               if (!selected_edit_group_contact.contains(addcontactList.get(i).getContact_id())) {
                                   selected_edit_group_contact.add(addcontactList.get(i).getContact_id());
                               }
                           }
                           addContactToGroup(addcontactList.get(i).getContact_id());

                       }

                       AddGroupContactsAdapter adapter = new AddGroupContactsAdapter(AddGroupActivity.this, addcontactList, user_id, crm_flag, editFlag, AddGroupActivity.this,select_all);
                       adapter.notifyDataSetChanged();
                       rv_add_group.setAdapter(adapter);

                   }

                   Log.d(Global.TAG, "onCheckedChanged: select_all checked selected_contact:"+selected_contact.size());
                   Log.d(Global.TAG, "onCheckedChanged: select_all checked selected_group_contact:"+selected_group_contact.size());
                   Log.d(Global.TAG, "onCheckedChanged: select_all checked selected_edit_group_contact:"+selected_edit_group_contact.size());
               }else{
                   select_all=false;
                   if (crm_flag.equals("1")){
                       select_all_contacts=false;
                   }else  if (crm_flag.equals("2")){
                       select_all_cust=false;
                   }else  if (crm_flag.equals("3")){
                       select_all_prospects=false;
                   }else  if (crm_flag.equals("4")){
                       select_all_recruits=false;
                   }

                   for (int i=0;i<addcontactList.size();i++){
                       if (!editFlag){
                           if (selected_group_contact.contains(addcontactList.get(i).getContact_id())){
                               selected_group_contact.remove(addcontactList.get(i).getContact_id());
                           }
                       }else{
                           if (selected_edit_group_contact.contains(addcontactList.get(i).getContact_id())) {
                               selected_edit_group_contact.remove(addcontactList.get(i).getContact_id());
                           }
                       }
                      removeContactFromGroup(addcontactList.get(i).getContact_id());
                   }
                   AddGroupContactsAdapter adapter = new AddGroupContactsAdapter(AddGroupActivity.this, addcontactList, user_id, crm_flag, editFlag, AddGroupActivity.this,select_all);
                   adapter.notifyDataSetChanged();
                   rv_add_group.setAdapter(adapter);

                   Log.d(Global.TAG, "onCheckedChanged: select_all unchecked selected_contact:"+selected_contact.size());
                   Log.d(Global.TAG, "onCheckedChanged: select_all unchecked selected_group_contact:"+selected_group_contact.size());
                   Log.d(Global.TAG, "onCheckedChanged: select_all unchecked selected_edit_group_contact:"+selected_edit_group_contact.size());
               }


           }
       });
        tv_my_contact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_contact checked");
                    tv_my_contact.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                    tv_my_prospect.setChecked(false);
                    tv_my_customers.setChecked(false);
                    tv_my_recruits.setChecked(false);
                    Log.d(Global.TAG, "onCheckedChanged: select_all_contacts "+select_all_contacts);
                    //ch_select_all_contacts.setChecked(select_all_contacts);
                    tv_selected_contacts.setText("" + selected_contact.size() + " Contact Selected");
                    getContactDetails("1");
                    crm_flag = "1";
                } else {
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_contact unchecked");
                    tv_my_contact.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }

            }
        });
        tv_my_prospect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_prospect checked");
                    tv_my_prospect.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                    tv_my_contact.setChecked(false);
                    tv_my_customers.setChecked(false);
                    tv_my_recruits.setChecked(false);
                   // ch_select_all_contacts.setChecked(select_all_prospects);
                    tv_selected_contacts.setText("" + selected_prospects.size() + " Prospects Selected");
                    getContactDetails("3");
                    crm_flag = "3";
                } else {
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_prospect unchecked");
                    tv_my_prospect.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }
            }
        });
        tv_my_customers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked) {
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_customers checked");
                    tv_my_customers.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                    tv_my_contact.setChecked(false);
                    tv_my_prospect.setChecked(false);
                    tv_my_recruits.setChecked(false);
                   // ch_select_all_contacts.setChecked(select_all_cust);
                    tv_selected_contacts.setText("" + selected_customers.size() + " Customers Selected");
                    getContactDetails("2");
                    crm_flag = "2";
                } else {
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_customers unchecked");
                    tv_my_customers.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }
            }
        });
        tv_my_recruits.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked) {
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_customers checked");
                    tv_my_recruits.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                    tv_my_contact.setChecked(false);
                    tv_my_prospect.setChecked(false);
                    tv_my_customers.setChecked(false);
                  //  ch_select_all_contacts.setChecked(select_all_recruits);
                    tv_selected_contacts.setText("" + selected_recruits.size() + " Recruits Selected");
                    getContactDetails("4");
                    crm_flag = "4";
                } else {
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_customers unchecked");
                    tv_my_recruits.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }
            }
        });

        tv_my_contact.setChecked(true);

        btn_contact_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_contact.clear();
                selected_customers.clear();
                selected_prospects.clear();
                selected_recruits.clear();
                selected_group_contact.clear();
                selected_edit_group_contact.clear();
                finish();
            }
        });

        btn_group_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_contact.clear();
                selected_customers.clear();
                selected_prospects.clear();
                selected_recruits.clear();
                selected_group_contact.clear();
                selected_edit_group_contact.clear();
                finish();
            }
        });

        btn_save_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected_rb = rbg_select_from.getCheckedRadioButtonId();

                if (myValidator.isValidField(edt_group_name)) {
                    Log.d(Global.TAG, "selected_group_contact size: " + selected_group_contact.size());
                    if (selected_rb == R.id.rb_from_system) {
                        if (selected_group_contact.size() > 0) {
                            createGroup();
                        } else {
                            Toast.makeText(AddGroupActivity.this, "Please Select at least one Contact..!", Toast.LENGTH_LONG).show();
                        }
                    }else if (selected_rb == R.id.rb_import_csv){
                        if (selected_csv_file!=null){
                            importCSVToGroup(selected_csv_file);
                        }else{
                            Toast.makeText(AddGroupActivity.this, "Please select file to upload", Toast.LENGTH_LONG).show();
                        }
                    }else if(selected_rb==R.id.rb_email_campaign){
                        Log.d(Global.TAG, "Selected group contacts: "+selected_group_contact.size());
                        if (selected_group_contact.size() > 0) {
                            createGroup();
                        } else {
                            Toast.makeText(AddGroupActivity.this, "Please Select at least one Contact..!", Toast.LENGTH_LONG).show();
                        }
                    }


                }

            }
        });

        btn_edit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected_rb = rbg_select_from.getCheckedRadioButtonId();
                if (myValidator.isValidField(edt_group_name)) {
                    Log.d(Global.TAG, "selected_group_contact size: " + selected_group_contact.size());
                    if (selected_rb == R.id.rb_from_system) {
                        if (selected_edit_group_contact.size() > 0) {
                          editGroupDetails();
                        } else {
                            Toast.makeText(AddGroupActivity.this, "Please Select at least one Contact..!", Toast.LENGTH_LONG).show();
                        }
                    }else if (selected_rb == R.id.rb_import_csv){
                        if (selected_csv_file!=null){
                            importCSVToGroup(selected_csv_file);
                        }else{
                            Toast.makeText(AddGroupActivity.this, "Please select file to upload", Toast.LENGTH_LONG).show();
                        }
                    }/*else if(selected_rb==R.id.rb_email_campaign){
                        Log.d(Global.TAG, "Selected group contacts: "+selected_group_contact.size());
                        if (selected_edit_group_contact.size() > 0) {
                            editGroupDetails();
                        } else {
                            Toast.makeText(AddGroupActivity.this, "Please Select at least one Contact..!", Toast.LENGTH_SHORT).show();
                        }
                    }*/

                }
            }
        });

        search_add_group.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search_list.clear();
                for (int i = 0; i < addcontactList.size(); i++) {
                    String first_name = addcontactList.get(i).getContact_fname();
                    String last_name = addcontactList.get(i).getContact_lname();

                    if (Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(first_name).find()
                            || Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(last_name).find()) {
                        search_list.add(addcontactList.get(i));

                    }
                    Log.d(Global.TAG, "Search List:: " + search_list.size());
                    if (search_list.size() > 0) {
                        rv_add_group.setVisibility(View.VISIBLE);
                        AddGroupContactsAdapter adapter = new AddGroupContactsAdapter(AddGroupActivity.this, search_list, user_id, crm_flag, editFlag, AddGroupActivity.this,select_all);
                        adapter.notifyDataSetChanged();
                        rv_add_group.setAdapter(adapter);

                    } else {
                        rv_add_group.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

        rb_from_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    ll_system_contacts.setVisibility(View.VISIBLE);
                    ll_import_csv.setVisibility(View.GONE);
                    ll_from_email_campaigns.setVisibility(View.GONE);
                } /*else {
                    ll_system_contacts.setVisibility(View.GONE);
                    ll_import_csv.setVisibility(View.VISIBLE);
                }*/
            }
        });

        rb_import_csv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    ll_system_contacts.setVisibility(View.GONE);
                    ll_from_email_campaigns.setVisibility(View.GONE);
                    ll_import_csv.setVisibility(View.VISIBLE);
                } /*else {
                    ll_system_contacts.setVisibility(View.VISIBLE);
                    ll_import_csv.setVisibility(View.GONE);
                }*/
            }
        });

        rb_email_campaign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_system_contacts.setVisibility(View.GONE);
                    ll_import_csv.setVisibility(View.GONE);
                    ll_from_email_campaigns.setVisibility(View.VISIBLE);

                    getEmailCampaignsWithAssignedEmails();
                }
            }
        });

        btn_select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCSV();
            }
        });

        tv_click_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddGroupActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AddGroupActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(AddGroupActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    Toast.makeText(AddGroupActivity.this, "Write External Storage Permission Denied", Toast.LENGTH_LONG).show();
                } else {
                   downloadSampleCSV();
                    Log.d(Global.TAG, "requestRead: Permission Already Granted.");
                }

            }
        });



        rb_read_emails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    readFlag="1";
                    if (!campaignId.equals("")){
                        //if (!editFlag){
                            selected_group_contact.clear();
                        /*}else{
                            selected_edit_group_contact.clear();
                        }*/
                        getCampaignStatusList(campaignId,readFlag);
                    }else{
                        Toast.makeText(AddGroupActivity.this, "Please select campaign..!", Toast.LENGTH_LONG).show();
                    }

                }else{
                    readFlag="0";
                }
            }
        });
        rb_unread_emails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    readFlag="0";
                    if (!campaignId.equals("")){
                       // if (!editFlag){
                            selected_group_contact.clear();
                        /*}else{
                            selected_edit_group_contact.clear();
                        }*/
                        getCampaignStatusList(campaignId,readFlag);
                    }else{
                        Toast.makeText(AddGroupActivity.this, "Please select campaign..!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    readFlag="1";
                }
            }
        });

        sp_campaign_in_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_campaign_in_group.getSelectedItem().toString().contains("Select")){
                    campaignId="";
                }else{
                    campaignId=campaignIdListforSpinner[position-1];
                    Log.d(Global.TAG, "onItemSelected:campaignId: "+campaignId);
                    if (!readFlag.equals("")){
                        getCampaignStatusList(campaignId,readFlag);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getCampaignStatusList(String campaignId, String readFlag) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("campId",campaignId);
            paramObj.put("readFlag",readFlag);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getCampaignStatusList: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getCampaignStatusList: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(AddGroupActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive = APIClient.getRetrofit().create(APIService.class);
        Call<ContactDetails> call = servive.getCampaignStatusList(paramObj.toString());
        call.enqueue(new Callback<ContactDetails>() {
            @Override
            public void onResponse(Call<ContactDetails> call, Response<ContactDetails> response) {
                if (response.isSuccessful()){
                    ContactDetails contactDetails=response.body();
                    if ( contactDetails.isSuccess()){
                        List<Contact> contactList=contactDetails.getResult();
                        Log.d(Global.TAG, "onResponse:contactList: "+contactList.size());

                        if (contactList.size()>0){
                            rv_emails_from_campaigns.setVisibility(View.VISIBLE);
                            GroupCampaignAdapter adapter=new GroupCampaignAdapter(AddGroupActivity.this,contactList,user_id,editFlag);
                            rv_emails_from_campaigns.setAdapter(adapter);
                        }else{
                            rv_emails_from_campaigns.setVisibility(View.GONE);
                        }

                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<ContactDetails> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:getCampaignStatusList "+t);
                rv_emails_from_campaigns.setVisibility(View.GONE);
                Toast.makeText(AddGroupActivity.this, "No Emails Available..", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getEmailCampaignsWithAssignedEmails() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getEmailCampaignsWithAssignedEmails: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getEmailCampaignsWithAssignedEmails: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(AddGroupActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive = APIClient.getRetrofit().create(APIService.class);
        Call<GetAllCampaign> call = servive.getCampaignForGroup(paramObj.toString());
        call.enqueue(new Callback<GetAllCampaign>() {
            @Override
            public void onResponse(Call<GetAllCampaign> call, Response<GetAllCampaign> response) {
                if (response.isSuccessful()){
                    GetAllCampaign getAllCampaign=response.body();
                    if (getAllCampaign.isSuccess()){
                        List<Campaign> campaignList=getAllCampaign.getResult();
                        Log.d(Global.TAG, "campaignList: "+campaignList.size());
                        if (campaignList.size()>0){
                            campaignListforSpinner=new String[campaignList.size()];
                            campaignIdListforSpinner=new String[campaignList.size()];
                            for (int i=0;i<campaignList.size();i++){
                                campaignListforSpinner[i]=campaignList.get(i).getCampaignTitle();
                                campaignIdListforSpinner[i]=campaignList.get(i).getCampaignId();
                            }
                            applySpinner(campaignListforSpinner,sp_campaign_in_group,"Select_Campaign");
                        }

                    }else{
                        Toast.makeText(AddGroupActivity.this, ""+getAllCampaign.getResult(), Toast.LENGTH_LONG).show();
                    }
                    myLoader.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetAllCampaign> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "getEmailCampaignsWithAssignedEmails: "+t);
            }
        });
    }
    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(AddGroupActivity.this, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }
    private void downloadSampleCSV() {
        final Dialog myLoader = Global.showDialog(AddGroupActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        String url = "https://www.successentellus.com/assets/prospect.csv";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Some descrition");
        request.setTitle("prospect.csv");
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"prospect.csv");

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        myLoader.dismiss();
        Toast.makeText(AddGroupActivity.this, "File Downloaded Successfully..!", Toast.LENGTH_LONG).show();

    }

    private void selectCSV() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), CSV_REQUEST);
    }

    private void editGroupDetails() {
        selected_contacts_id = "";
        if (selected_edit_group_contact.size() > 0) {
            for (String contact : selected_edit_group_contact) {
                selected_contacts_id += contact + ",";
            }
            if (selected_contacts_id.endsWith(",")) {
                selected_contacts_id = selected_contacts_id.substring(0, selected_contacts_id.length() - 1);
            }
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("group_id", group_id);
            paramObj.put("group_name", edt_group_name.getText().toString());
            paramObj.put("group_description", edt_group_desc.getText().toString());
            paramObj.put("group_contact_id", selected_contacts_id);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "editGroupDetails: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "editGroupDetails: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(AddGroupActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.edit_group(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result = response.body();
                if (result.isSuccess()) {
                    Toast.makeText(AddGroupActivity.this, "Group Updated SuccessFully..!", Toast.LENGTH_LONG).show();
                    selected_contact.clear();
                    selected_customers.clear();
                    selected_prospects.clear();
                    selected_recruits.clear();
                    selected_group_contact.clear();
                    selected_edit_group_contact.clear();
                    finish();
                } else {
                    Toast.makeText(AddGroupActivity.this, " Error in Updating Group.!", Toast.LENGTH_LONG).show();
                    finish();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, " createGroup onFailure: " + t);
                myLoader.dismiss();
                Toast.makeText(AddGroupActivity.this, " Error in Updating Group.!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void createGroup() {
        if (selected_group_contact.size() > 0) {
            for (String contact : selected_group_contact) {
                selected_contacts_id += contact + ",";
            }
            if (selected_contacts_id.endsWith(",")) {
                selected_contacts_id = selected_contacts_id.substring(0, selected_contacts_id.length() - 1);
            }
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("group_name", edt_group_name.getText().toString());
            paramObj.put("group_description", edt_group_desc.getText().toString());
            paramObj.put("group_contact_id", selected_contacts_id);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "createGroup: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "createGroup: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(AddGroupActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.add_group(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result = response.body();
                if (result.isSuccess()) {
                    Toast.makeText(AddGroupActivity.this, "Group Created SuccessFully..!", Toast.LENGTH_LONG).show();
                    selected_contact.clear();
                    selected_customers.clear();
                    selected_prospects.clear();
                    selected_recruits.clear();
                    selected_group_contact.clear();
                    selected_edit_group_contact.clear();
                    finish();
                } else {
                    Toast.makeText(AddGroupActivity.this, " Error in Creating Group.!", Toast.LENGTH_LONG).show();
                    finish();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, " createGroup onFailure: " + t);
                myLoader.dismiss();
                Toast.makeText(AddGroupActivity.this, " Error in Creating Group.!", Toast.LENGTH_LONG).show();
                finish();
            }
        });


    }

    private void init() {
        tv_addgroup_title = (TextView) findViewById(R.id.tv_addgroup_title);
        tv_my_contact = (ToggleButton) findViewById(R.id.tv_my_contact);
        tv_my_prospect = (ToggleButton) findViewById(R.id.tv_my_prospect);
        tv_my_customers = (ToggleButton) findViewById(R.id.tv_my_customers);
        tv_my_recruits = (ToggleButton) findViewById(R.id.tv_my_recruits);
        tv_selected_contacts = (TextView) findViewById(R.id.tv_selected_contacts);
        tv_addgroup_title = (TextView) findViewById(R.id.tv_addgroup_title);

        rb_from_system = (RadioButton) findViewById(R.id.rb_from_system);
        rb_import_csv = (RadioButton) findViewById(R.id.rb_import_csv);
        rb_email_campaign = (RadioButton) findViewById(R.id.rb_email_campaign);
        rb_read_emails = (RadioButton) findViewById(R.id.rb_read_emails);
        rb_unread_emails = (RadioButton) findViewById(R.id.rb_unread_emails);

        rbg_select_from = (RadioGroup) findViewById(R.id.rbg_select_from);
        ll_system_contacts = (LinearLayout) findViewById(R.id.ll_system_contacts);
        ll_import_csv = (LinearLayout) findViewById(R.id.ll_import_csv);
        ll_from_email_campaigns = (LinearLayout) findViewById(R.id.ll_from_email_campaigns);
        ll_display_list_read_unread = (LinearLayout) findViewById(R.id.ll_display_list_read_unread);
        btn_select_file = (Button) findViewById(R.id.btn_select_file);
        tv_selected_csv = (TextView) findViewById(R.id.tv_selected_csv);
        tv_click_here = (TextView) findViewById(R.id.tv_click_here);
        //btn_import_csv = (Button) findViewById(R.id.btn_import_csv);

        btn_group_close = (Button) findViewById(R.id.btn_group_close);
        btn_save_group = (Button) findViewById(R.id.btn_save_group);
        btn_edit_group = (Button) findViewById(R.id.btn_edit_group);
        btn_contact_cancel = (Button) findViewById(R.id.btn_group_cancel);

        edt_group_name = (EditText) findViewById(R.id.edt_group_name);
        edt_group_desc = (EditText) findViewById(R.id.edt_group_desc);

        search_add_group = (SearchView) findViewById(R.id.search_add_group);
        rv_add_group = (RecyclerView) findViewById(R.id.rv_add_group);
        rv_emails_from_campaigns = (RecyclerView) findViewById(R.id.rv_emails_from_campaigns);
        rv_add_group.setLayoutManager(new LinearLayoutManager(AddGroupActivity.this));
        rv_emails_from_campaigns.setLayoutManager(new LinearLayoutManager(AddGroupActivity.this));
        spLib = new SPLib(AddGroupActivity.this);
        user_id = spLib.getPref(SPLib.Key.USER_ID);
        selected_group_contact = new ArrayList<>();
        search_list = new ArrayList<>();
        sp_campaign_in_group=findViewById(R.id.sp_campaign_in_group);
        ch_select_all_contacts=findViewById(R.id.ch_select_all_contacts);

//        edt_group_name.setFocusable(false);
//        edt_group_name.setFocusableInTouchMode(true);
    }

    public void getContactDetails(final String crm_flag) {
        JSONObject paramObj = new JSONObject();
        try {
            Log.d(Global.TAG, "getContactDetails: user_id" + user_id);
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("crmFlag", crm_flag);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getContactDetails: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getContactDetails: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(AddGroupActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive = APIClient.getRetrofit().create(APIService.class);
        Call<ContactDetails> call = servive.get_contact_details(paramObj.toString());
        call.enqueue(new Callback<ContactDetails>() {
            @Override
            public void onResponse(Call<ContactDetails> call, Response<ContactDetails> response) {
                ContactDetails contactDetails = response.body();
                if (contactDetails != null) {
                    if (contactDetails.isSuccess()) {
                        myLoader.dismiss();
                        addcontactList = contactDetails.getResult();
                        Log.d(Global.TAG, "getContactDetails: List " + addcontactList.size());

                        if (addcontactList.size() > 0) {
                            rv_add_group.setVisibility(View.VISIBLE);
                            ch_select_all_contacts.setVisibility(View.VISIBLE);

                           /* for (int i=0;i<addcontactList.size();i++){
                                if (!editFlag){
                                    selected_group_contact.add(addcontactList.get(i).getContact_id());
                                }else{
                                    selected_edit_group_contact.add(addcontactList.get(i).getContact_id());
                                }
                                selected_contact.add(addcontactList.get(i).getContact_id());
                            }*/
                            AddGroupContactsAdapter adapter = new AddGroupContactsAdapter(AddGroupActivity.this, addcontactList, user_id, crm_flag, editFlag, AddGroupActivity.this,select_all);
                            adapter.notifyDataSetChanged();
                            rv_add_group.setAdapter(adapter);

                        } else {
                            rv_add_group.setVisibility(View.GONE);
                            ch_select_all_contacts.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "No Contacts Available", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ContactDetails> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:getContactDetails " + t);
                myLoader.dismiss();
                ch_select_all_contacts.setVisibility(View.GONE);
                rv_add_group.setVisibility(View.GONE);
                if (crm_flag.equals("1"))
                    Toast.makeText(getApplicationContext(), "No Contacts Available", Toast.LENGTH_LONG).show();
                else if (crm_flag.equals("2"))
                    Toast.makeText(getApplicationContext(), "No Customers Available", Toast.LENGTH_LONG).show();
                else if (crm_flag.equals("3"))
                    Toast.makeText(getApplicationContext(), "No Prospects Available", Toast.LENGTH_LONG).show();
                else if (crm_flag.equals("4"))
                    Toast.makeText(getApplicationContext(), "No Recruits Available", Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void addContactToGroup(String contact_id) {
        // selected_group_contact.add(contact_id);
        if (crm_flag.equals("1")) {
            if (!selected_contact.contains(contact_id)) {
                selected_contact.add(contact_id);
                tv_selected_contacts.setText(selected_contact.size() + " Contacts Selected");
            }
        } else if (crm_flag.equals("2")) {
            if (!selected_customers.contains(contact_id)) {
                selected_customers.add(contact_id);
                tv_selected_contacts.setText(selected_customers.size() + " Customers Selected");
            }
        } else if (crm_flag.equals("3")) {
            if (!selected_prospects.contains(contact_id)) {
                selected_prospects.add(contact_id);
                tv_selected_contacts.setText(selected_prospects.size() + " Prospects Selected");
            }
        } else if (crm_flag.equals("4")) {
            if (!selected_recruits.contains(contact_id)) {
                selected_recruits.add(contact_id);
                tv_selected_contacts.setText(selected_recruits.size() + " Recruits Selected");
            }
        }
    }

    @Override
    public void removeContactFromGroup(String contact_id) {
        //selected_group_contact.remove(contact_id);
        if (crm_flag.equals("1")) {
            selected_contact.remove(contact_id);
            tv_selected_contacts.setText(selected_contact.size() + " Contacts Selected");
        } else if (crm_flag.equals("2")) {
            selected_customers.remove(contact_id);
            tv_selected_contacts.setText(selected_customers.size() + " Customers Selected");
        } else if (crm_flag.equals("3")) {
            selected_prospects.remove(contact_id);
            tv_selected_contacts.setText(selected_prospects.size() + " Prospects Selected");
        } else if (crm_flag.equals("4")) {
            selected_recruits.remove(contact_id);
            tv_selected_contacts.setText(selected_recruits.size() + " Recruits Selected");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CSV_REQUEST) {

                final Uri uri = data.getData();
                // Get the File path from the Uri
                String path = FileUtils.getPath(AddGroupActivity.this, uri);
                Log.d("mytag", "" + path);

                if (path != null && FileUtils.isLocal(path)) {
                    File csv_file = new File(path);
                    if (csv_file!=null){
                        selected_csv_file=csv_file;
                        Log.e("mytag", "Exist" + selected_csv_file.getName());
                        tv_selected_csv.setText("" + selected_csv_file.getName());
                    }

                    //importCSVToGroup(csv_file);
                } else {
                    Log.e("mytag", "Not Exiast");
                }
            }


        }
    }

    private void importCSVToGroup(File csv_file) {
        Toast.makeText(this, "Importing CSV...", Toast.LENGTH_LONG).show();

        RequestBody reqFile = RequestBody.create(MediaType.parse("text/csv"), csv_file);
        MultipartBody.Part file = MultipartBody.Part.createFormData("upload", csv_file.getName(), reqFile);

        String group_name = edt_group_name.getText().toString();
        RequestBody groupName = RequestBody.create(MediaType.parse("text/plain"), group_name);
        RequestBody groupId = RequestBody.create(MediaType.parse("text/plain"), group_id);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody platform = RequestBody.create(MediaType.parse("text/plain"), "2");

        final Dialog myLoader = Global.showDialog(AddGroupActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(false);
        Log.d(Global.TAG, "importCSVToGroup: user_id:" + user_id + " group_id:" + group_id + " group_name:" + group_name);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.import_group_csv(userId, platform, groupId, groupName, file);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()) {
                    JsonResult jsonResult = response.body();
                    if (jsonResult.isSuccess()) {
                        Toast.makeText(AddGroupActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(AddGroupActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: importGroupCSV: " + t);
            }
        });

    }
}
