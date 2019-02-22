package com.success.successEntellus.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.success.successEntellus.R;
import com.success.successEntellus.fragment.MyContactFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.AllGroups;
import com.success.successEntellus.model.Contact_For;
import com.success.successEntellus.model.Contact_Result;
import com.success.successEntellus.model.Contact_Source;
import com.success.successEntellus.model.Contact_Spinner;
import com.success.successEntellus.model.Contact_Status;
import com.success.successEntellus.model.GetSubScriptionDate;
import com.success.successEntellus.model.GroupInfo;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.SubscriptionDates;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddContactActivity extends AppCompatActivity{

    EditText   edt_companyName, edt_address, edt_city, edt_zipcode, edt_state, edt_country, edt_description;

    EditText edt_phone,edt_phone2,edt_phone3;
    String[] contact_status_array;
    String[] contact_source_array;

    EditText et_industry, et_policy_company;
    EditText edt_firstName,edt_lastName;

    EditText edt_email,edt_email_work2,edt_email_other;

    Button btn_add_contact, btn_contact_cancel, btn_edit_contact,btn_adddissmiss,btn_addcontact_back;
    TextView  tv_add_email,tv_add_phone,tv_addContactTitle;
    EditText edt_brithDate;
    Spinner spin_tag,sp_contact_for,sp_contact_status,sp_contact_source,sp_select_group;
    Calendar cal;
    int day,month,year,hour,min;
    String[] taglist = {"Green Apple", "Red Apple", "Brown Apple", "Rotten Apple"};
    LinearLayout ll_one, ll_two, ll_three, ll_four, ll_five, ll_add_email,ll_add_phone,ll_recruit_info,ll_recruits_id;
    ToggleButton toggle, toggle2, toggle3, toggle4, toggle5,toggle_recruit;
    LinearLayout iv_birthDate, iv_anniDate;
    final HashMap<String, String> params = new HashMap<>();
    SPLib spLib;
    String user_id,contact_id;
    String category="";
    private boolean isContactEditable=false;
    private Bundle extras;
    DashboardActivity mainActivity;
    String[] contactForList;
     String contact_for_id;
     String edit_contact_for;
     String contact_source_id;
     String contact_status_id,selected_group;
     String cust_policy_no;
    List<GroupInfo> groupList;
    String crm_flag;
    List<Contact_Status> contactStatusList;
    List<Contact_Source> contactSourceList;
    String[] group_array;
    MyContactFragment myContactFragment;
    String contact_title;
    boolean editFlag;
    MyValidator myValidator;
    public String contact_status;
    public String contact_source;
    int contact_forSp;
    private int contactsourcepos=0;
    private String selected_tag;
    String sub_start_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        init();


        extras=getIntent().getExtras();
        if (extras!=null) {
            if (extras.containsKey("crm_flag")) {
                crm_flag = extras.getString("crm_flag");
                Log.d(Global.TAG, "AddContact: crmFlag " + crm_flag);
                if (crm_flag.equals("4")){
                    ll_recruits_id.setVisibility(View.VISIBLE);
                    toggle_recruit.setVisibility(View.VISIBLE);
                }else{
                    ll_recruits_id.setVisibility(View.GONE);
                    toggle_recruit.setVisibility(View.GONE);
                }


            }
            contact_id = extras.getString("contact_id");
        }

        applySpinner(taglist,spin_tag, "--Select Tag--");
        getSpinnerData();

        if (extras!=null){
           editFlag=extras.getBoolean("ContactEditable");
            if (editFlag){
                contact_title=extras.getString("contact_title");
                btn_edit_contact.setVisibility(View.VISIBLE);
                btn_add_contact.setVisibility(View.GONE);
                fillContactEditText();
            }else{
                btn_edit_contact.setVisibility(View.GONE);
            }
        }
        myContactFragment=new MyContactFragment(crm_flag);

        if (editFlag){
            sp_select_group.setVisibility(View.GONE);
            //ll_add_email.setVisibility(View.VISIBLE);
            //ll_add_phone.setVisibility(View.VISIBLE);
            if (crm_flag.equals("1")){
                tv_addContactTitle.setText("Edit Contact Details");
                setTitle("Edit Contact Details");
            }else if (crm_flag.equals("2")){
                tv_addContactTitle.setText("Edit Customers Details");
                setTitle("Edit Customers Details");
            }else if (crm_flag.equals("3")){
                tv_addContactTitle.setText("Edit Prospects Details");
                setTitle("Edit Prospects Details");
            }else if (crm_flag.equals("4")){
                tv_addContactTitle.setText("Edit Recruits Details");
                setTitle("Edit Recruits Details");
            }

        }else{
            sp_select_group.setVisibility(View.VISIBLE);
            //ll_add_email.setVisibility(View.GONE);
            //ll_add_phone.setVisibility(View.GONE);
            if (crm_flag.equals("1")){
                tv_addContactTitle.setText("Add new Contact Details");
                setTitle("Add new Contact Details");
            }else if (crm_flag.equals("2")){
                tv_addContactTitle.setText("Add new Customer Details");
                setTitle("Add new Customer Details");
            }else if (crm_flag.equals("3")){
                tv_addContactTitle.setText("Add new Prospect Details");
                setTitle("Add new Prospect Details");
            }else if (crm_flag.equals("4")){
                tv_addContactTitle.setText("Add new Recruits Details");
                setTitle("Add new Recruits Details");
            }

        }
        callToaggleButtonForExapndable();
        getAllGroups();
        spinnerSelection();


       /* sp_contact_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int position=i+1;
                Log.d(Global.TAG, "onItemSelected: position"+position);
                if(sp_contact_status.getSelectedItem().equals("Select Contact Status")){
                    contact_status_id="";
                }else{
                    contact_status_id=contactStatusList.get(position).getZo_lead_status_id();
                    Log.d(Global.TAG, "onItemSelected contact_status_id: "+contact_status_id);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                contact_status_id="";
            }
        });
*/
        btn_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.isNetworkAvailable(getApplicationContext())) {
                    boolean nameFlag=false,emailphoneFlag=false;

                   /* String primary_email=edt_email.getText().toString();
                    if (edt_email_work2.getText().toString().equals(primary_email)) {
                        edt_email_work2.setText("");
                    }
                    if (edt_email_other.getText().toString().equals(primary_email)) {
                        edt_email_other.setText("");
                    }else{
                        addUserContact();
                    }

                    String primary_phone=edt_phone.getText().toString();
                    if (edt_phone2.getText().toString().equals(primary_phone)){
                        edt_phone2.setText("");
                    }
                    if (edt_phone3.getText().toString().equals(primary_phone)){
                        edt_phone3.setText("");
                    }*/

                    if (!edt_firstName.getText().toString().equals("") || !edt_lastName.getText().toString().equals("")){
                        nameFlag=true;
                        if (!edt_email.getText().toString().equals("") || !edt_email_work2.getText().toString().equals("") || !edt_email_other.getText().toString().equals("")){
                           emailphoneFlag=true;
                           if (myValidator.isValidEmail(edt_email) && myValidator.isValidEmail(edt_email_work2) && myValidator.isValidEmail(edt_email_other)){
                              // addUserContact();

                           }
                        }else{
                            if (!edt_phone.getText().toString().equals("") || !edt_phone2.getText().toString().equals("") || !edt_phone3.getText().toString().equals("")){
                                emailphoneFlag=true;
                                if (myValidator.isValidMobile(edt_phone) && myValidator.isValidMobile(edt_phone2) && myValidator.isValidMobile(edt_phone3)){
                                  //  addUserContact();
                                }
                            }else{
                                emailphoneFlag=false;
                                toggle.setChecked(true);
                                toggle2.setChecked(false);
                                toggle3.setChecked(false);
                                toggle4.setChecked(false);
                                edt_firstName.clearFocus();
                                edt_email.setFocusable(true);
                                edt_email.requestFocus();
                                edt_email.setError("Please Enter Email or Phone..!");
                            }
                        }
                    }else{
                        toggle.setChecked(true);
                        toggle2.setChecked(false);
                        toggle3.setChecked(false);
                        toggle4.setChecked(false);
                        edt_firstName.setError("Please Enter Either FirstName or LastName...!");
                        nameFlag=false;
                    }

                   /* if (!edt_firstName.getText().toString().equals("") || !edt_lastName.getText().toString().equals("")){
                        if ((!edt_email.getText().toString().equals("") || !edt_email_work2.getText().toString().equals("") || !edt_email_other.getText().toString().equals("")) || (!edt_phone.getText().toString().equals("") || !edt_phone2.getText().toString().equals("") || !edt_phone3.getText().toString().equals(""))){
                            validator.validate();
                        }else{
                            edt_email.setError("Please Enter Either Email or Phone");
                        }
                    }else{
                        edt_firstName.setError("Please Enter Either FirstName or LastName");
                    }

*/
                }else{
                    Intent intent = new Intent(getApplicationContext(), NetworkCheckActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        btn_addcontact_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_adddissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_edit_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //editUserContactOnContactId();
            }
        });
        edt_brithDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(102);
//                DatePickerFragment fragment = new DatePickerFragment();
//                fragment.show(getFragmentManager(),"Theme 4");
            }
        });

        /*iv_birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(102);

            }
        });*/
        //Gettting birth date of contact
       /* iv_anniDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(101);

            }
        });*/


        btn_contact_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void spinnerSelection() {
        sp_select_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (group_array.length==1){
                    //Toast.makeText(AddContactActivity.this, "Groups Not Available..!", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d(Global.TAG, "Position: " + position);
                    if (sp_select_group.getSelectedItem().toString().contains("Select")) {
                        selected_group = "";
                    } else {
                        selected_group = group_array[position - 1];
                        Log.d(Global.TAG, "onItemSelected: sp_select_group " + selected_group);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        spin_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (spin_tag.getSelectedItem().toString().contains("Select")) {
                    category = "";
                } else {
                    category = taglist[position-1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        sp_contact_for.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_contact_for.getSelectedItem().toString().contains("Select")) {
                    contact_for_id = "";
                } else {
                    contact_for_id = contactForList[position-1];
                    Log.d(Global.TAG, "onItemSelected:sp_contact_for "+contact_for_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        sp_contact_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_contact_status.getSelectedItem().toString().contains("Select")) {
                    contact_status_id = "";
                } else {
                    contact_status_id = contactStatusList.get(position-1).getZo_lead_status_id();
                    Log.d(Global.TAG, "onItemSelected:sp_contact_status "+contact_status_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        sp_contact_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_contact_source.getSelectedItem().toString().contains("Select")) {
                    contact_source_id = "";
                } else {

                    contact_source_id = contactSourceList.get(position-1).getZo_lead_source_id();
                    Log.d(Global.TAG, "onItemSelected:sp_contact_source "+contact_source_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    private void getAllGroups() {

        JSONObject param=new JSONObject();
        try {
            param.put("userId",user_id);
            param.put("platform",2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(Global.TAG, "getAllGroups:param "+param.toString());

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<AllGroups> call=service.getAllGroups(param.toString());
        call.enqueue(new Callback<AllGroups>() {
            @Override
            public void onResponse(Call<AllGroups> call, Response<AllGroups> response) {
                AllGroups allGroups=response.body();
                if (allGroups.isSuccess()){
                    groupList=allGroups.getResult();
                    Log.d(Global.TAG, "groupList: "+groupList.size());
                    group_array=new String[groupList.size()];
                   // group_array[0]="Select Group";
                    for(int i=0;i<groupList.size();i++){
                        group_array[i]=groupList.get(i).getGroup_name();
                    }
                    applySpinner(group_array,sp_select_group,"--Select Group--");
//                    ArrayAdapter group_adapter=new ArrayAdapter(AddContactActivity.this,android.R.layout.simple_list_item_1,group_array);
//                   // group_adapter.add("Select Group");
//                    sp_select_group.setAdapter(group_adapter);

                    //fillGroupSpinner();
                }else{
                    group_array=new String[1];
                    applySpinner(group_array,sp_select_group,"--Select Group--");
                    Log.d(Global.TAG, "Groups Not Available..!: ");
                    sp_select_group.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<AllGroups> call, Throwable t) {
                Log.d(Global.TAG, "Groups Not Available..!: "+t);
            }
        });


    }

 /*   private void fillGroupSpinner() {
        GroupSpinner adapter=new GroupSpinner(AddContactActivity.this,contactForList);
        sp_select_group.setAdapter(adapter);

    }
*/
    /*private void editUserContactOnContactId() {
            Log.d(Global.TAG, "editUserContactOnContactId: ");
            final Dialog myLoader = Global.showDialog(AddContactActivity.this);
            myLoader.show();
            myLoader.setCanceledOnTouchOutside(true);

            APIService service= APIClient.getRetrofit().create(APIService.class);
             Call<JsonResult> call=service.update_contact(user_id,crm_flag,"2",contact_id,edt_firstName.getText().toString(),
                edt_lastName.getText().toString(),
                edt_email.getText().toString(),
                edt_email_work2.getText().toString(),
                edt_email_other.getText().toString(),
                edt_phone.getText().toString(),
                edt_phone2.getText().toString(),
                edt_phone3.getText().toString(),
                contact_for_id,
                contact_source_id,
                contact_status_id,
                et_industry.getText().toString(),
                edt_brithDate.getText().toString(),
                edt_companyName.getText().toString(),
                edt_address.getText().toString(),category,
                edt_city.getText().toString(),
                edt_state.getText().toString(),
                edt_zipcode.getText().toString(),
                edt_country.getText().toString(),
                edt_description.getText().toString()
        );

            call.enqueue(new Callback<JsonResult>() {
                @Override
                public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                    JsonResult result=response.body();
                    if (result.isSuccess()){
                        Toast.makeText(AddContactActivity.this, ""+result.getResult(), Toast.LENGTH_LONG).show();
                        Log.d(Global.TAG, "Contact Updated Successfully...!: ");
                        //myContactFragment.getContactDetails(AddContactActivity.this);
                        setResult(RESULT_OK);
                        finish();
                    }else {
                        Toast.makeText(AddContactActivity.this, "Error in Updating Contact...!", Toast.LENGTH_LONG).show();
                        Log.d(Global.TAG, "Error in Updating Contact...!");
                        finish();
                    }
                    myLoader.dismiss();
                }

                @Override
                public void onFailure(Call<JsonResult> call, Throwable t) {
                    myLoader.dismiss();
                    Log.d(Global.TAG, "Update Contact onFailure: "+t);
                    Toast.makeText(AddContactActivity.this, "Update Contact"+t, Toast.LENGTH_LONG).show();
                    finish();
                }
            });


    }*/

    private void fillContactEditText() {
        edt_firstName.setText(extras.getString("c_fname"));
      //  edt_middleName.setText(extras.getString("c_mname"));
        edt_lastName.setText(extras.getString("c_lname"));
        edt_email.setText(extras.getString("c_email"));
        edt_email_work2.setText(extras.getString("c_email1"));
        edt_email_other.setText(extras.getString("c_email2"));
        edt_phone.setText(extras.getString("phone"));
        edt_phone2.setText(extras.getString("phone1"));
        edt_phone3.setText(extras.getString("phone2"));
        edt_brithDate.setText(extras.getString("date_of_birth"));

        edit_contact_for=extras.getString("contact_for");
        contact_status=extras.getString("contact_status");
        contact_source=extras.getString("contact_source");
        selected_tag=extras.getString("tag");

        et_policy_company.setText(extras.getString("policy_company"));
        et_industry.setText(extras.getString("industry"));

        edt_companyName.setText(extras.getString("company_name"));
        edt_address.setText(extras.getString("contact_address"));
        edt_city.setText(extras.getString("contact_city"));
        edt_zipcode.setText(extras.getString("contact_zip"));
        edt_state.setText(extras.getString("contact_state"));
        edt_country.setText(extras.getString("contact_country"));
        edt_description.setText(extras.getString("contact_description"));


        Log.d(Global.TAG, "Contact_For: "+edit_contact_for);
        Log.d(Global.TAG, "Contact_status: "+contact_status);
        Log.d(Global.TAG, "Contact_source: "+contact_source);

    }
    private void getSpinnerData() {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        Log.d(Global.TAG, "getSpinnerData: param:"+paramObj);
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<Contact_Spinner> call= service.get_contact_spinner(paramObj.toString());
        call.enqueue(new Callback<Contact_Spinner>() {
            @Override
            public void onResponse(Call<Contact_Spinner> call, Response<Contact_Spinner> response) {
                Contact_Spinner contact_spinner=response.body();
//                contactStatusList.clear();
//                contactSourceList.clear();
                if (contact_spinner.isSuccess()){
                    Log.d(Global.TAG, "Spinner data Get Successfully..!: ");
                    Contact_Result contact_result=contact_spinner.getResult();
                    Contact_For contact_for=contact_result.getContact_lead_prospecting_for();

                    contactStatusList=contact_result.getContact_lead_status_id();
                    contactSourceList=contact_result.getContact_lead_source_id();

                    contact_status_array=new String[contactStatusList.size()];
                    contact_source_array=new String[contactSourceList.size()];


                    for (int i=0;i<contactStatusList.size();i++){
                        contact_status_array[i]=contactStatusList.get(i).getZo_lead_status_name();
                    }
                    for (int i=0;i<contactStatusList.size();i++){
                        contact_status_array[i]=contactStatusList.get(i).getZo_lead_status_name();
                    }
                    for (int i=0;i<contactSourceList.size();i++){
                        contact_source_array[i]=contactSourceList.get(i).getZo_lead_source_name();
                    }
//
                    if(crm_flag.equals("1")){
                        applySpinner(contactForList,sp_contact_for,"--Select Contact For--");
                        applySpinner(contact_status_array,sp_contact_status,"--Select Contact Status--");
                        applySpinner(contact_source_array,sp_contact_source, "--Select Contact Source--");

                    }else if(crm_flag.equals("2")){
                        applySpinner(contactForList,sp_contact_for,"--Select Customer For--");
                        applySpinner(contact_status_array,sp_contact_status,"--Select Customer Status--");
                        applySpinner(contact_source_array,sp_contact_source, "--Select Customer Source--");

                    }else if(crm_flag.equals("3")){
                        applySpinner(contactForList,sp_contact_for,"--Select Prospect For--");
                        applySpinner(contact_status_array,sp_contact_status,"--Select Prospect Status--");
                        applySpinner(contact_source_array,sp_contact_source, "--Select Prospect Source--");
                    }else if(crm_flag.equals("4")){
                        applySpinner(contactForList,sp_contact_for,"--Select Recruit For--");
                        applySpinner(contact_status_array,sp_contact_status,"--Select Recruit Status--");
                        applySpinner(contact_source_array,sp_contact_source, "--Select Recruit Source--");
                    }

                    Log.d(Global.TAG, "contact_for: "+contact_for.getBusiness()+contact_for.getPolicy());
                    Log.d(Global.TAG, "contactStatusList: "+contactStatusList.size());
                    Log.d(Global.TAG, "contactSourceList: "+contactSourceList.size());

                    if (editFlag){
                        Log.d(Global.TAG, "editFlag contact_source: "+contact_source);
                        Log.d(Global.TAG, "contact_status: "+contact_status);
                        Log.d(Global.TAG, "contact_for: "+edit_contact_for);
                        Log.d(Global.TAG, "Tag: "+selected_tag);

                        sp_contact_status.setSelection(Integer.parseInt(contact_status));
                        sp_contact_source.setSelection(Integer.parseInt(contact_source));

                        if (edit_contact_for.equals("Business")){
                            sp_contact_for.setSelection(1);
                        }else if (edit_contact_for.equals("Policy")){
                            sp_contact_for.setSelection(2);
                        }else{
                            sp_contact_for.setSelection(0);
                        }

                        if (selected_tag.equalsIgnoreCase("Green Apple")){
                            spin_tag.setSelection(1);
                        }else if (selected_tag.equalsIgnoreCase("Red Apple")){
                            spin_tag.setSelection(2);
                        }else if (selected_tag.equalsIgnoreCase("Brown Apple")){
                            spin_tag.setSelection(3);
                        }else if (selected_tag.equalsIgnoreCase("Rotten Apple")){
                            spin_tag.setSelection(4);
                        }else{
                            spin_tag.setSelection(0);
                        }


                    }
                    //fillContactForSpinner();
//                    fillContactStatusSpinner();
//                    fillContactSourceSpinner();

                }else{
                    Log.d(Global.TAG, "Error in Getting Data..!");
                }
            }

            @Override
            public void onFailure(Call<Contact_Spinner> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: "+t);
            }
        });

    }


   /* private void fillContactSourceSpinner() {
        ContactSourceAdapter adapter=new ContactSourceAdapter(AddContactActivity.this, android.R.layout.simple_list_item_1,contactSourceList);
        sp_contact_source.setAdapter(adapter);
        sp_contact_source.setSelection(adapter.getCount());
        sp_contact_source.setEnabled(true);
    }

    private void fillContactStatusSpinner() {
        ContactStatusAdapter adapter=new ContactStatusAdapter(AddContactActivity.this,contactStatusList);

        adapter.add("please select");
        sp_contact_status.setAdapter(adapter);
        sp_contact_status.setSelection(adapter.getCount());
        sp_contact_status.setEnabled(true);
        *//*spin_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (spin_tag.getSelectedItem() == "please select") {
                    category = "";
                } else {
                    category = taglist[position - 1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });*//*
    }
*/
    private void init() {
        spLib=new SPLib(AddContactActivity.this);
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        spin_tag = (Spinner) findViewById(R.id.spin_tag);
        sp_contact_for = (Spinner) findViewById(R.id.sp_contact_for);
        sp_contact_source = (Spinner) findViewById(R.id.sp_contact_source);
        sp_contact_status = (Spinner) findViewById(R.id.sp_contact_status);
        sp_select_group = (Spinner) findViewById(R.id.sp_select_group);
        edt_firstName = (EditText) findViewById(R.id.edt_firstName);
        //edt_middleName = (EditText) findViewById(R.id.edt_middleName);
        edt_lastName = (EditText) findViewById(R.id.edt_lastName);

        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_email_work2 = (EditText) findViewById(R.id.edt_email_work2);
        edt_email_other = (EditText) findViewById(R.id.edt_email_other);

        edt_phone = (EditText) findViewById(R.id.edt_phone);
         edt_phone2= (EditText) findViewById(R.id.edt_phone2);
        edt_phone3 = (EditText) findViewById(R.id.edt_phone3);

        et_policy_company=(EditText)findViewById(R.id.et_policy_company);

        et_industry=(EditText)findViewById(R.id.et_industry);
        et_policy_company=(EditText)findViewById(R.id.et_policy_company);


        edt_description = (EditText) findViewById(R.id.edt_description);
        edt_brithDate = (EditText) findViewById(R.id.edt_brithDate);

        edt_address = (EditText) findViewById(R.id.edt_address);
        edt_city = (EditText) findViewById(R.id.edt_city);
        edt_zipcode = (EditText) findViewById(R.id.edt_zipcode);
        edt_state = (EditText) findViewById(R.id.edt_state);
        edt_country = (EditText) findViewById(R.id.edt_country);

//        iv_birthDate = (LinearLayout) findViewById(R.id.iv_birthDate);
//        iv_anniDate = (LinearLayout) findViewById(R.id.iv_anniDate);
        btn_add_contact = (Button) findViewById(R.id.btn_add_contact);
        btn_contact_cancel = (Button) findViewById(R.id.btn_contact_cancel);
        btn_edit_contact = (Button) findViewById(R.id.btn_edit_contact);
        btn_addcontact_back = (Button) findViewById(R.id.btn_addcontact_back);
        btn_adddissmiss = (Button) findViewById(R.id.btn_adddissmiss);
        edt_companyName = (EditText) findViewById(R.id.edt_companyName);
        mainActivity= new DashboardActivity();
        //tv_add_email=(TextView)findViewById(R.id.tv_add_email) ;
        //tv_add_phone=(TextView)findViewById(R.id.tv_add_phone) ;

        tv_addContactTitle=(TextView)findViewById(R.id.tv_addContactTitle) ;

        ll_one = (LinearLayout) findViewById(R.id.ll_one);
        ll_two = (LinearLayout) findViewById(R.id.ll_two);
        ll_three = (LinearLayout) findViewById(R.id.ll_three);
        ll_four = (LinearLayout) findViewById(R.id.ll_four);
        ll_five = (LinearLayout) findViewById(R.id.ll_five);
        ll_recruit_info = (LinearLayout) findViewById(R.id.ll_recruit_info);
        ll_recruits_id = (LinearLayout) findViewById(R.id.ll_recruits_id);

        //ll_add_email = (LinearLayout) findViewById(R.id.ll_add_email);
       // ll_add_phone = (LinearLayout) findViewById(R.id.ll_add_phone);

        toggle = (ToggleButton) findViewById(R.id.toggle);
        toggle2 = (ToggleButton) findViewById(R.id.toggle2);
        toggle3 = (ToggleButton) findViewById(R.id.toggle3);
        toggle4 = (ToggleButton) findViewById(R.id.toggle4);
        toggle5 = (ToggleButton) findViewById(R.id.toggle5);
        toggle_recruit = (ToggleButton) findViewById(R.id.toggle_recruit);

        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        hour = cal.get(Calendar.HOUR);
        min =cal.get(Calendar.MINUTE);


        contactForList=new String[]{ "Business","Policy"};
        contactSourceList=new ArrayList<>();
        contactStatusList=new ArrayList<>();
        group_array=new String[15];

        myValidator=new MyValidator();



    }

    private void callToaggleButtonForExapndable() { //Function call the button listner for visibility of views on plus/minus button

        if (crm_flag.equals("1")){
            toggle2.setText("Other Information");
            toggle2.setTextOn("Other Information");
            toggle2.setTextOff("Other Information");
        }else if (crm_flag.equals("2")){
            toggle2.setText("Other Information");
            toggle2.setTextOn("Customer Information");
            toggle2.setTextOff("Customer Information");
        }else if (crm_flag.equals("3")){
            toggle2.setText("Other Information");
            toggle2.setTextOn("Prospect Information");
            toggle2.setTextOff("Prospect Information");
        }

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    toggle.setTextColor(Color.parseColor("#d35400"));
                    ll_one.setVisibility(View.VISIBLE);
                    if (ll_two.getVisibility() == View.VISIBLE || ll_three.getVisibility() == View.VISIBLE || ll_four.getVisibility() == View.VISIBLE || ll_five.getVisibility() == View.VISIBLE) {
                        toggle2.setChecked(false);
                        toggle3.setChecked(false);
                        toggle4.setChecked(false);
                        toggle5.setChecked(false);

                        ll_two.setVisibility(View.GONE);
                        ll_three.setVisibility(View.GONE);
                        ll_four.setVisibility(View.GONE);
                        ll_five.setVisibility(View.GONE);

                    }
                } else {
                    ll_one.setVisibility(View.GONE);
                    toggle.setTextColor(Color.BLACK);
                }
            }
        });

      /*  tv_add_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ll_add_phone.getVisibility()==View.GONE){
                    ll_add_phone.setVisibility(View.VISIBLE);
                    tv_add_phone.setText("- Add Phone");
                }else{
                    ll_add_phone.setVisibility(View.GONE);
                    tv_add_phone.setText("+ Add Phone");
                }
            }
        });*/

        /*tv_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_add_email.getVisibility()==View.GONE){
                    if (!MyValidator.isValidEmailAdd(edt_email)){
                        edt_email.setError("Enter primary email & then try to add other emails!!");
                    }else {
                        ll_add_email.setVisibility(View.VISIBLE);
                        tv_add_email.setText("- Add Email");
                        edt_email.setError(null);
                    }

                }else{
                    ll_add_email.setVisibility(View.GONE);
                    tv_add_email.setText("+ Add Email");
                }
            }
        });*/



        toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    toggle2.setTextColor(Color.parseColor("#d35400"));
                    ll_two.setVisibility(View.VISIBLE);

                    if (ll_one.getVisibility() == View.VISIBLE || ll_three.getVisibility() == View.VISIBLE ||ll_four.getVisibility() == View.VISIBLE || ll_five.getVisibility() == View.VISIBLE) {
                        toggle.setChecked(false);
                        toggle3.setChecked(false);
                        toggle4.setChecked(false);
                        toggle5.setChecked(false);

                        ll_one.setVisibility(View.GONE);
                        ll_three.setVisibility(View.GONE);
                        ll_four.setVisibility(View.GONE);
                        ll_five.setVisibility(View.GONE);

                    }
                }else{
                    ll_two.setVisibility(View.GONE);
                    toggle2.setTextColor(Color.BLACK);

                }
            }
        });
        toggle3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    toggle3.setTextColor(Color.parseColor("#d35400"));
                    ll_three.setVisibility(View.VISIBLE);

                    if (ll_one.getVisibility() == View.VISIBLE ||ll_four.getVisibility() == View.VISIBLE || ll_five.getVisibility() == View.VISIBLE) {
                        toggle.setChecked(false);
                        toggle2.setChecked(false);
                        toggle4.setChecked(false);
                        toggle5.setChecked(false);

                        ll_one.setVisibility(View.GONE);
                        ll_two.setVisibility(View.GONE);
                        ll_four.setVisibility(View.GONE);
                        ll_five.setVisibility(View.GONE);

                    }
                }else{
                    ll_three.setVisibility(View.GONE);
                    toggle3.setTextColor(Color.BLACK);

                }
            }
        });

        toggle4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    toggle4.setTextColor(Color.parseColor("#d35400"));
                    ll_four.setVisibility(View.VISIBLE);

                    if (ll_one.getVisibility() == View.VISIBLE ||ll_four.getVisibility() == View.VISIBLE || ll_five.getVisibility() == View.VISIBLE) {
                        toggle.setChecked(false);
                        toggle2.setChecked(false);
                        toggle3.setChecked(false);
                        toggle5.setChecked(false);

                        ll_one.setVisibility(View.GONE);
                        ll_two.setVisibility(View.GONE);
                        ll_three.setVisibility(View.GONE);
                        ll_five.setVisibility(View.GONE);

                    }
                }else{
                    ll_four.setVisibility(View.GONE);
                    toggle4.setTextColor(Color.BLACK);

                }
            }
        });

        toggle5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    toggle5.setTextColor(Color.parseColor("#d35400"));
                    ll_five.setVisibility(View.VISIBLE);

                    if (ll_one.getVisibility() == View.VISIBLE ||ll_four.getVisibility() == View.VISIBLE || ll_five.getVisibility() == View.VISIBLE) {
                        toggle.setChecked(false);
                        toggle2.setChecked(false);
                        toggle3.setChecked(false);
                        toggle4.setChecked(false);

                        ll_one.setVisibility(View.GONE);
                        ll_two.setVisibility(View.GONE);
                        ll_three.setVisibility(View.GONE);
                        ll_four.setVisibility(View.GONE);

                    }
                }else{
                    ll_five.setVisibility(View.GONE);
                    toggle5.setTextColor(Color.BLACK);

                }
            }
        });

        toggle_recruit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    toggle_recruit.setTextColor(Color.parseColor("#d35400"));
                    ll_recruit_info.setVisibility(View.VISIBLE);

                    if (ll_one.getVisibility() == View.VISIBLE ||ll_four.getVisibility() == View.VISIBLE || ll_five.getVisibility() == View.VISIBLE) {
                        toggle.setChecked(false);
                        toggle2.setChecked(false);
                        toggle3.setChecked(false);
                        toggle4.setChecked(false);
                        toggle5.setChecked(false);

                        ll_one.setVisibility(View.GONE);
                        ll_two.setVisibility(View.GONE);
                        ll_three.setVisibility(View.GONE);
                        ll_four.setVisibility(View.GONE);
                        ll_five.setVisibility(View.GONE);

                    }
                }else{
                    ll_recruit_info.setVisibility(View.GONE);
                    toggle_recruit.setTextColor(Color.BLACK);

                }
            }
        });


    }



   /* private void addUserContact() {
        Log.d(Global.TAG, "addUserContact: ");
        final Dialog myLoader = Global.showDialog(AddContactActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Log.d(Global.TAG, "addUserContact:user_id: "+user_id+"crm_flag:"+crm_flag);
        Call<JsonResult> call=service.add_contact(user_id,crm_flag,"2",edt_firstName.getText().toString(),
                edt_lastName.getText().toString(),
                selected_group,
                edt_email.getText().toString(),
                edt_email_work2.getText().toString(),
                edt_email_other.getText().toString(),
                edt_phone.getText().toString(),
                edt_phone2.getText().toString(),
                edt_phone3.getText().toString(),
                contact_for_id,
                contact_source_id,
                contact_status_id,
                et_industry.getText().toString(),
                edt_brithDate.getText().toString(),
                edt_companyName.getText().toString(),
                edt_address.getText().toString(),category,
                edt_city.getText().toString(),
                edt_state.getText().toString(),
                edt_zipcode.getText().toString(),
                edt_country.getText().toString(),
                edt_description.getText().toString()
               );

        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult!=null){
                    if (jsonResult.isSuccess()){
                        //Toast.makeText(AddContactActivity.this, "Contact Added Successfully..!", Toast.LENGTH_LONG).show();
                        Toast.makeText(AddContactActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        Log.d(Global.TAG, "Contact Added Successfully..!: ");
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        Toast.makeText(AddContactActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        Log.d(Global.TAG, "This Email already Exists..! Please try with another Email.");

                    }
                }

                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                edt_email.setError("Email address already exist!!");
                Toast.makeText(AddContactActivity.this, "This Email already Exists..! Please try with another Email.", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure:addUserContact "+t);
            }
        });
    }*/

   /* @Override
    public void onValidationSucceeded() {
        if (Global.isNetworkAvailable(AddContactActivity.this)){
            Log.d(Global.TAG, "onValidationSucceeded: "+edt_firstName.getText().toString());
            addUserContact();


        }else{
            Intent intent = new Intent(AddContactActivity.this, NetworkCheckActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }*/

   /* @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages
            if (view instanceof Spinner) {
                Spinner sp = (Spinner) view;
                view = ((LinearLayout) sp.getSelectedView()).getChildAt(0);        // we are actually interested in the text view spinner has
            }

            if (view instanceof TextView) {
                TextView et = (TextView) view;
                et.setError(message);
            }
        }
    }*/


    public static class spinnerAdapter extends ArrayAdapter<String> {

        public spinnerAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            // TODO Auto-generated constructor stub
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            int count =super.getCount();
            return count>0 ? count-1 : count ;
        }
    }

    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        spinnerAdapter adapterRepeateDaily = new spinnerAdapter(AddContactActivity.this, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);

        /*if (isContactEditable) {//Check if Edit Contact is open then set the last selected spinner value
            if (!extras.getString("tag").equals("")){//Check if the category is not null then selected the get category spinner
                if (extras.getString("tag").equals("Green Apple")) {
                    spin_tag.setSelection(1);
                } else if (extras.getString("tag").equals("Red Apple")) {
                    spin_tag.setSelection(2);
                } else if (extras.getString("tag").equals("Brown Apple")) {
                    spin_tag.setSelection(3);
                } else if (extras.getString("tag").equals("Rotten Apple")) {
                    spin_tag.setSelection(4);
                }
            }
        }*/
    }

    public void ClickMeToOpenSpinnerTag(View view){
        spin_tag.performClick();
    }

    //Dialog box function for open the Date Picker Intent
    protected Dialog onCreateDialog(int id){
        if(id==102){
            DatePickerDialog dpd = new DatePickerDialog(AddContactActivity.this,AlertDialog.THEME_HOLO_LIGHT,userDateSetListener2,year,month,day);
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            return  dpd;
        }

        return  null;
    }
    //Manual Date Picker Fuction
    private DatePickerDialog.OnDateSetListener userDateSetListener2 = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            //edt_task_todate.setText(""+year+"-"+month+"-"+dayOfMonth);
            edt_brithDate.setText(year+"-"+((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString())));
        }
    };




  /*  public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT,this,year,month,day);

            return datepickerdialog;
//            return new DatePickerDialog(getActivity(),
//                    (DatePickerDialog.OnDateSetListener)
//                            getActivity(), year, month, day);
        }
        @Override
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            Calendar cal = new GregorianCalendar(year, month, day);
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            edt_brithDate.setText(dateFormat.format(cal.getTime()));
            String month = String.valueOf(m+1);
            String selectedDay = String.valueOf(d);
            //edt_task_todate.setText(""+year+"-"+month+"-"+dayOfMonth);
            edt_brithDate.setText(y+"-"+((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString())));
        }


    }*/
    private class ContactStatusAdapter extends ArrayAdapter {
        List<Contact_Status> contactStatusList;
        AddContactActivity context;
        LayoutInflater inflater;
        public ContactStatusAdapter(AddContactActivity context, List<Contact_Status> contactStatusList) {
            super(context,0);
            this.context=context;
            this.contactStatusList=contactStatusList;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            inflater= (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.spinner_row,parent,false);
            TextView tv_spinner=convertView.findViewById(R.id.tv_spinner);

            tv_spinner.setText(contactStatusList.get(position).getZo_lead_status_name());
            return convertView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position,convertView,parent);
        }

        @Override
        public int getCount() {
            return contactStatusList.size();
        }
    }

    private class ContactSourceAdapter extends ArrayAdapter{
        List<Contact_Source> contactSourceList;
        AddContactActivity context;
        LayoutInflater inflater;

        public ContactSourceAdapter(AddContactActivity context, List<Contact_Source> contactSourceList) {
            super(context,0);
            this.context=context;
            this.contactSourceList=contactSourceList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            inflater= (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.spinner_row,parent,false);
            TextView tv_spinner=convertView.findViewById(R.id.tv_spinner);

            tv_spinner.setText(contactSourceList.get(position).getZo_lead_source_name());
            return convertView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position,convertView,parent);
        }

        @Override
        public int getCount() {

            return contactSourceList.size();
        }
    }

    private class ContactForAdapter extends ArrayAdapter {
        List<String> contactForList;
        AddContactActivity context;
        LayoutInflater inflater;

        public ContactForAdapter(AddContactActivity context, int simple_list_item_1, List<String> contactForList) {
            super(context,0);
            this.context=context;
            this.contactForList=contactForList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            inflater= (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.spinner_row,parent,false);
            TextView tv_spinner=convertView.findViewById(R.id.tv_spinner);

            tv_spinner.setText(contactForList.get(position));
            return convertView;
        }

        @Override
        public int getCount() {
            return contactForList.size();
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }

    private class GroupSpinner extends ArrayAdapter {
        AddContactActivity context;
        List<String> contactForList;
        LayoutInflater inflater;


        public GroupSpinner(AddContactActivity context, List<String> contactForList) {
            super(context,0);
            this.context=context;
            this.contactForList=contactForList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            inflater= (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.spinner_row,parent,false);
            TextView tv_spinner=convertView.findViewById(R.id.tv_spinner);

            tv_spinner.setText(groupList.get(position).getGroup_name());
            return convertView;
        }

        @Override
        public int getCount() {
            return groupList.size();
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }
}
