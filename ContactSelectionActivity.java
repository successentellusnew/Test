package com.success.successEntellus.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.ContactSelectionListAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.Contact;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactSelectionActivity extends AppCompatActivity {
    JSONArray all_contacts_array;
    List<Contact> contact_list, search_list;
    // String all_contact_string;
    CheckBox ch_all;
    SPLib spLib;
    String user_id;
    SearchView contact_search;
    RecyclerView rv_allcontactList;
    ImageButton ib_import, ib_back;
    Toolbar toolbar_import;
    TextView tv_import;
    JSONArray contactArray;
    boolean onboardFlag=false;
    String crm_flag;
    Cursor cursor;
    int counter;
    Dialog Loader;
    JSONArray usersContact;
    ArrayList<String> contactList, contactMobile, contactEmail;
    public static List<Contact> selected_contact_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Import Phone Contact");
        setContentView(R.layout.activity_contact_selection);
        Bundle bundle = getIntent().getExtras();
        getSupportActionBar().hide();
        init();
        if (bundle != null) {
            //all_contact_string = bundle.getString("all_contacts");
            crm_flag = bundle.getString("contact_flag");

            // Log.d(Global.TAG, "All Contacts length: " + all_contact_string.length());
            Log.d(Global.TAG, "CRM Flag: " + crm_flag);

            if (bundle.containsKey("onboardFlag")) {
                onboardFlag = bundle.getBoolean("onboardFlag");
                Log.d(Global.TAG, "onboardFlag:ContactSelection: "+onboardFlag);
            }
        }


        getContactsClass contacts = new getContactsClass();
        contacts.execute();


        ch_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                selected_contact_list.clear();
                Log.d(Global.TAG, "onCheckedChanged: contact_list:"+contact_list.size());

                if(checked){
                    for (int i=0;i<contact_list.size();i++){
                        if (!selected_contact_list.contains(contact_list.get(i))){
                            selected_contact_list.add(contact_list.get(i));
                        }

                    }
                    Log.d(Global.TAG, "Select All checked: selected_contact_list "+selected_contact_list.size());
                }else{
                    for (int i=0;i<contact_list.size();i++){
                        if (selected_contact_list.contains(contact_list.get(i))){
                            selected_contact_list.remove(contact_list.get(i));
                        }

                    }
                    Log.d(Global.TAG, "Select All unchecked: selected_contact_list "+selected_contact_list.size());
                }


                ContactSelectionListAdapter adapter = new ContactSelectionListAdapter(ContactSelectionActivity.this, contact_list, user_id, checked);
                rv_allcontactList.setAdapter(adapter);
            }
        });


        ib_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.d(Global.TAG, "selected_contats List:"+contactArray.length());
                createJsonArraytoUpload();

                if (onboardFlag) {
                    if (contactArray.length() > 0) {
                        Log.d(Global.TAG, "contactArray: "+contactArray.length());
                        Toast.makeText(ContactSelectionActivity.this, "Importing contacts.. Please wait...", Toast.LENGTH_LONG).show();
                        uploadPhoneContactToDBWithoutValidations(contactArray);
                    } else {
                        Toast.makeText(ContactSelectionActivity.this, "Please Select Contact to import..!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (contactArray.length() > 0) {
                        Log.d(Global.TAG, "contactArray: "+contactArray.length());
                        Toast.makeText(ContactSelectionActivity.this, "Importing contacts.. Please wait...", Toast.LENGTH_LONG).show();
                        uploadPhoneContactToDB(contactArray);
                    } else {
                        Toast.makeText(ContactSelectionActivity.this, "Please Select Contact to import..!", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });


        contact_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                search_list.clear();
                for (int i = 0; i < contact_list.size(); i++) {

                    String first_name = contact_list.get(i).getContact_fname();
                    String last_name = contact_list.get(i).getContact_lname();
                    String mo_no = contact_list.get(i).getContact_phone();
                    if (first_name == null) {
                        first_name = "";
                    } else if (last_name == null) {
                        last_name = "";
                    } else if (mo_no == null) {
                        mo_no = "";
                    }

                    Log.d(Global.TAG, "onQueryTextChange: newText:" + newText);
                    Log.d(Global.TAG, "onQueryTextChange: first_name:" + first_name);
                    Log.d(Global.TAG, "onQueryTextChange: last_name:" + last_name);
                    Log.d(Global.TAG, "onQueryTextChange: mo_no:" + mo_no);


                    if (Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(first_name).find()
                            || Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(last_name).find()
                            || Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(mo_no).find()) {
                        search_list.add(contact_list.get(i));
                    }

                }
                Log.d(Global.TAG, "Search List:: " + search_list.size());
                ContactSelectionListAdapter adapter = new ContactSelectionListAdapter(ContactSelectionActivity.this, search_list, user_id, false);
                rv_allcontactList.setAdapter(adapter);

                return false;
            }
        });

        selected_contact_list.clear();
        ContactSelectionListAdapter adapter = new ContactSelectionListAdapter(ContactSelectionActivity.this, contact_list, user_id, false);
        rv_allcontactList.setAdapter(adapter);

    }


    private void createContactList(String all_contact_string) {

        try {
            all_contacts_array = new JSONArray(all_contact_string);
            Log.d(Global.TAG, " All Contact List: " + all_contacts_array.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < all_contacts_array.length(); i++) {
            try {
                JSONObject contact = all_contacts_array.getJSONObject(i);
                Contact contact1 = new Contact();
                contact1.setContact_fname(contact.getString("contact_fname"));
                contact1.setContact_lname(contact.getString("contact_lname"));

                contact1.setContact_address(contact.getString("contact_address"));
                contact1.setContact_city(contact.getString("contact_city"));
                contact1.setContact_state(contact.getString("contact_state"));
                contact1.setContact_country(contact.getString("contact_country"));
                contact1.setContact_zip(contact.getString("contact_zip"));
               // contact1.setContact_skype_id(contact.getString("contact_skype_id"));
               // contact1.setContact_twitter_name(contact.getString("contact_twitter_name"));
                //contact1.setContact_facebookurl(contact.getString("contact_facebookurl"));
                //contact1.setContact_linkedinurl(contact.getString("contact_linkedinurl"));
                contact1.setContact_description(contact.getString("contact_description"));
                contact1.setContact_company_name(contact.getString("contact_company_name"));

                if (contact.has("contact_phone")) {
                    contact1.setContact_phone(contact.getString("contact_phone"));
                }
                if (contact.has("contact_work_phone")) {
                    contact1.setContact_work_phone(contact.getString("contact_work_phone"));
                }
                if (contact.has("contact_other_phone")) {
                    contact1.setContact_other_phone(contact.getString("contact_other_phone"));
                }

                if (contact.has("contact_email")) {
                    contact1.setContact_email(contact.getString("contact_email"));
                }
                if (contact.has("contact_work_email")) {
                    contact1.setContact_work_email(contact.getString("contact_work_email"));
                }
                if (contact.has("contact_other_email")) {
                    contact1.setContact_other_email(contact.getString("contact_other_email"));
                }



               /* JSONArray phoneList=contact.getJSONArray("phone");
                Log.d(Global.TAG, "Phone length: "+phoneList.length());
                if (phoneList.length()==1){
                    contact1.setContact_phone(phoneList.get(0).toString());
                }else  if (phoneList.length()==2){
                    contact1.setContact_phone(phoneList.get(0).toString());
                    contact1.setContact_work_phone(phoneList.get(1).toString());
                    //contact1.setContact_other_phone(phoneList.get(2).toString());
                }else  if (phoneList.length()==3){
                    contact1.setContact_phone(phoneList.get(0).toString());
                    contact1.setContact_work_phone(phoneList.get(1).toString());
                    contact1.setContact_other_phone(phoneList.get(2).toString());
                }*/

                /*if (contact.has("email")) {
                    JSONArray emailList = contact.getJSONArray("email");
                    if (emailList.length() > 0) {
                        contact1.setContact_email(emailList.get(0).toString());
                    }
                }*/
                contact_list.add(contact1);
                Log.d(Global.TAG, " Contact List: " + contact_list.size());

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(Global.TAG, "createContactList: Exc:" + e);
            }
        }
        Log.d(Global.TAG, " All Contact List: " + contact_list.size());

        selected_contact_list.clear();
        ContactSelectionListAdapter adapter = new ContactSelectionListAdapter(ContactSelectionActivity.this, contact_list, user_id, false);
        rv_allcontactList.setAdapter(adapter);
        Log.d(Global.TAG, "contact_list after creating list: "+contact_list.size());
    }

    private void createJsonArraytoUpload() {
        contactArray = new JSONArray();
        for (int i = 0; i < selected_contact_list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("contact_platform", "2");
                jsonObject.put("contact_users_id", user_id);
                jsonObject.put("contact_flag", crm_flag);

                jsonObject.put("contact_fname", selected_contact_list.get(i).getContact_fname());
                jsonObject.put("contact_lname", selected_contact_list.get(i).getContact_lname());

                jsonObject.put("contact_address", "");
                jsonObject.put("contact_city", "");
                jsonObject.put("contact_state", "");
                jsonObject.put("contact_country", "");
                jsonObject.put("contact_zip", "");
                //jsonObject.put("contact_skype_id", "");
                //jsonObject.put("contact_twitter_name", "");
                //jsonObject.put("contact_facebookurl", "");
                //jsonObject.put("contact_linkedinurl", "");
                jsonObject.put("contact_description", "");
                jsonObject.put("contact_company_name", "");

                if (selected_contact_list.get(i).getContact_phone() != null) {
                    jsonObject.put("contact_phone", selected_contact_list.get(i).getContact_phone());
                } else {
                    jsonObject.put("contact_phone", "");
                }

                if (selected_contact_list.get(i).getContact_work_phone() != null) {
                    jsonObject.put("contact_work_phone", selected_contact_list.get(i).getContact_work_phone());
                } else {
                    jsonObject.put("contact_work_phone", "");
                }

                if (selected_contact_list.get(i).getContact_other_phone() != null) {
                    jsonObject.put("contact_other_phone", selected_contact_list.get(i).getContact_other_phone());
                } else {
                    jsonObject.put("contact_other_phone", "");
                }


                if (selected_contact_list.get(i).getContact_email() != null) {
                    jsonObject.put("contact_email", selected_contact_list.get(i).getContact_email());
                } else {
                    jsonObject.put("contact_email", "");
                }

                if (selected_contact_list.get(i).getContact_work_email() != null) {
                    jsonObject.put("contact_work_email", selected_contact_list.get(i).getContact_work_email());
                } else {
                    jsonObject.put("contact_work_email", "");
                }

                if (selected_contact_list.get(i).getContact_other_email() != null) {
                    jsonObject.put("contact_other_email", selected_contact_list.get(i).getContact_other_email());
                } else {
                    jsonObject.put("contact_other_email", "");
                }



               /* List<String> phoneList=new ArrayList<>();
                if (selected_contact_list.get(i).getContact_phone()!=null)
                    phoneList.add(selected_contact_list.get(i).getContact_phone());
                if (selected_contact_list.get(i).getContact_work_phone()!=null)
                    phoneList.add(selected_contact_list.get(i).getContact_work_phone());
                if (selected_contact_list.get(i).getContact_other_phone()!=null)
                    phoneList.add(selected_contact_list.get(i).getContact_other_phone());
*/

            /*
                List<String> emailList=new ArrayList<>();
                if (selected_contact_list.get(i).getContact_email()!=null)
                emailList.add(selected_contact_list.get(i).getContact_email());
                // emailList.add(selected_contact_list.get(i).getContact_work_email());
                // emailList.add(selected_contact_list.get(i).getContact_other_email());
                JSONArray emailArray=new JSONArray(emailList);
                jsonObject.put("email",emailArray);*/

                contactArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(Global.TAG, "selected_contats: ib_import:" + contactArray);
        //uploadPhoneContactToDB(contactArray);


    }

    private class getContactsClass extends AsyncTask<String, Void, JSONArray> {
        JSONObject user;
        ProgressDialog dialog = new ProgressDialog(ContactSelectionActivity.this);
        boolean isSuccess;

        @Override
        protected void onPreExecute() {
            Log.d(Global.TAG, "onPreExecute: ");
            Loader = Global.showDialog(ContactSelectionActivity.this);
            Loader.show();
            Loader.setCanceledOnTouchOutside(false);
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            contactList = new ArrayList<String>();
            contactMobile = new ArrayList<String>();
            contactEmail = new ArrayList<String>();
            usersContact = new JSONArray();
            //updateBarHandler =new Handler();

            String phoneNumber = null;
            String email = null;

            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Log.d(Global.TAG, "DISPLAY_NAME: " + DISPLAY_NAME);
            Log.d(Global.TAG, "DISPLAY_NAME: " + DISPLAY_NAME);

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

            Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;

            Uri PostalCONTENT_URI = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            String PostalCONTACT_ID = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
            String PostalDATA = ContactsContract.CommonDataKinds.StructuredPostal.DATA;

            //StringBuffer output;
            ContentResolver contentResolver = getContentResolver();

            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            // Iterate every contact in the phone
            if (cursor != null) {
                Log.d(Global.TAG, "Cursor: " + cursor.getCount());
                if (cursor.getCount() > 0) {
                    counter = 0;
                    isSuccess = true;
                    while (cursor.moveToNext()) {
                        user = new JSONObject();
                        contactMobile.clear();
                        contactEmail.clear();
                        // Update the progress message

                        String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                        Log.d(Global.TAG, "contact_id: " + contact_id + "name:" + name);
                        String[] name_array = name.split(" ");
                        Log.d(Global.TAG, "doInBackground: name_array: " + name_array.length);
                        String lname = "", fname = "";
                        if (name_array.length > 0) {

                            if (name_array.length == 0) {
                                fname = name_array[0];
                            } else if (name_array.length == 2) {
                                fname = name_array[0];
                                lname = name_array[1];
                            } else if (name_array.length == 3) {
                                fname = name_array[0];
                                lname = name_array[1];
                            }
                            Log.d(Global.TAG, "Fname: " + fname + " Lname: " + lname);
                        }


                        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                        //  if (hasPhoneNumber > 0) {
                        try {
                            // user.put("firstName", name);
                            user.put("contact_fname", fname);
                            user.put("contact_lname", lname);
                            Log.d(Global.TAG, "Contact Name: " + name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
                            user.put("contact_address", "");
                            user.put("contact_city", "");
                            user.put("contact_state", "");
                            user.put("contact_country", "");
                            user.put("contact_zip", "");
                            //user.put("contact_skype_id", "");
                           // user.put("contact_twitter_name", "");
                           // user.put("contact_facebookurl", "");
                          //  user.put("contact_linkedinurl", "");
                            user.put("contact_description", "");
                            user.put("contact_company_name", "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //This is to read multiple phone numbers associated with the same contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            contactMobile.add(phoneNumber);
                        }
                        try {

                            if (contactMobile.size() > 0) {
                                Log.d(Global.TAG, "phone: " + contactMobile);
                                Log.d(Global.TAG, "Contact Mobile length: " + contactMobile.size());

                                if (contactMobile.size() == 1) {
                                    user.put("contact_phone", contactMobile.get(0));
                                } else if (contactMobile.size() == 2) {
                                    user.put("contact_phone", contactMobile.get(0));
                                    user.put("contact_work_phone", contactMobile.get(1));
                                } else if (contactMobile.size() == 3) {
                                    user.put("contact_phone", contactMobile.get(0));
                                    user.put("contact_work_phone", contactMobile.get(1));
                                    user.put("contact_other_phone", contactMobile.get(2));
                                }
                            } else {
                                user.put("contact_phone", "");
                            }
                            //user.put("phone",contactMobile);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        phoneCursor.close();
                        // Read every email id associated with the contact
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (emailCursor.moveToNext()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            contactEmail.add(email);

                        }
                        try {
                            if (contactEmail.size() > 0) {
                                Log.d(Global.TAG, "Email: " + contactEmail);
                                Log.d(Global.TAG, "Contact Email length: " + contactEmail.size());

                                if (contactEmail.size() == 1) {
                                    user.put("contact_email", contactEmail.get(0));
                                } else if (contactEmail.size() == 2) {
                                    user.put("contact_email", contactEmail.get(0));
                                    user.put("contact_work_email", contactEmail.get(1));
                                } else if (contactEmail.size() == 3) {
                                    user.put("contact_email", contactEmail.get(0));
                                    user.put("contact_work_email", contactEmail.get(1));
                                    user.put("contact_other_email", contactEmail.get(2));
                                }
                            } else {
                                user.put("contact_email", "");
                            }
                            //user.put("email", new JSONArray(contactEmail));
                            //user.put("email", contactEmail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        emailCursor.close();


                        //  }
                        usersContact.put(user);
                    }
                } else {
                    isSuccess = false;
                    //Toast.makeText(getActivity(), "No Contacts Found..", Toast.LENGTH_SHORT).show();
                    //Log.d(Global.TAG, "No COntacts Found..");
                }
            }
            Log.d(Global.TAG, "User Contact Array: " + usersContact);
            return usersContact;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            super.onPostExecute(result);
            Log.e("eventIdPost", String.valueOf(result));
            Loader.dismiss();
            if (isSuccess) {
                createContactList(result.toString());
                /*Intent intent=new Intent(Conta, ContactSelectionActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("all_contacts",result.toString());
                bundle.putString("contact_flag",crm_flag);
                intent.putExtras(bundle);
                startActivityForResult(intent,200);*/
                //uploadPhoneContactToDB(result);
            } else {
                Toast.makeText(ContactSelectionActivity.this, "No Contacts Found..!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void init() {
        spLib = new SPLib(ContactSelectionActivity.this);
        user_id = spLib.getPref(SPLib.Key.USER_ID);
        rv_allcontactList = findViewById(R.id.rv_allcontactList);
        ch_all = findViewById(R.id.ch_all);
        ib_import = (ImageButton) findViewById(R.id.ib_import);
        toolbar_import = (Toolbar) findViewById(R.id.toolbar_import);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        tv_import = (TextView) findViewById(R.id.tv_import);
        contact_list = new ArrayList<>();
        rv_allcontactList.setLayoutManager(new LinearLayoutManager(ContactSelectionActivity.this));
        contact_search = findViewById(R.id.contact_search);
        contact_search.setIconified(false);
        contact_search.setFocusable(false);
        contact_search.clearFocus();
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        search_list = new ArrayList<>();

        if (onboardFlag) {
            ib_back.setVisibility(View.GONE);
        } else {
            ib_back.setVisibility(View.VISIBLE);
        }

        int itemViewType = 0;
        rv_allcontactList.getRecycledViewPool().setMaxRecycledViews(itemViewType, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    private void uploadPhoneContactToDB(JSONArray contacts) {
        Log.d(Global.TAG, "uploadContactsToDB: contacts" + contacts);
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("contact_details", contacts);
            Log.d(Global.TAG, "uploadContactsToDB: " + paramObj.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final Dialog myLoader = Global.showDialog(ContactSelectionActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(false);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.import_phone_contact(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result = response.body();
                if (result != null) {
                    if (result.isSuccess()) {
                        Toast.makeText(ContactSelectionActivity.this, ""+result.getResult(), Toast.LENGTH_SHORT).show();
                        Log.d(Global.TAG, " Contact Imported Successfully..! ");
                        //adapter.notifyDataSetChanged();
                        // getContactDetails(ContactSelectionActivity.this);
                        /*if (onboardFlag){
                            Log.d(Global.TAG, "Onboard true ContactSelection: ");
                            Intent intent=new Intent(ContactSelectionActivity.this,MainOnboardingActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putBoolean("onboardFlag",onboardFlag);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else{
                            Log.d(Global.TAG, "Onboard Flow false: ");
                        }*/
                        setResult(RESULT_OK);
                        finish();

                    } else {
                        Toast.makeText(ContactSelectionActivity.this, ""+result.getResult(), Toast.LENGTH_SHORT).show();
                        Log.d(Global.TAG, "Contact imported Successfully..! else");
                        finish();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(ContactSelectionActivity.this, "Contacts are importing in background..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure: importContact " + t);
                finish();
            }
        });

    }

    private void uploadPhoneContactToDBWithoutValidations(JSONArray contacts) {
        Log.d(Global.TAG, "uploadContactsToDB: contacts" + contacts);
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("contact_details", contacts);
            Log.d(Global.TAG, "uploadPhoneContactToDBWithoutValidations: " + paramObj.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final Dialog myLoader = Global.showDialog(ContactSelectionActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.import_phone_contact_without_validation(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result = response.body();
                if (result != null) {
                    if (result.isSuccess()) {
                        Toast.makeText(ContactSelectionActivity.this, "Contact Imported Successfully..!", Toast.LENGTH_SHORT).show();
                        Log.d(Global.TAG, " Contact Imported Successfully..! ");
                        deleteDuplicateContacts();
                        //displayAlertforDeleteDuplicate();

                        //adapter.notifyDataSetChanged();
                        // getContactDetails(ContactSelectionActivity.this);

                        //finish();

                    } else {
                        Toast.makeText(ContactSelectionActivity.this, "Contact Imported Successfully..", Toast.LENGTH_SHORT).show();
                        Log.d(Global.TAG, "Contact imported Successfully..! else");
                        finish();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(ContactSelectionActivity.this, "Contacts are importing in background..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure: importContact " + t);
                finish();
            }
        });

    }

    private void displayAlertforDeleteDuplicate() {
        new AlertDialog.Builder(ContactSelectionActivity.this)
                .setMessage("Please click 'Ok' to delete duplicate contacts..")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .show();
    }

    private void deleteDuplicateContacts() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("crmFlag", crm_flag);

            Log.d(Global.TAG, "deleteDuplicateContacts: " + paramObj.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final Dialog myLoader = Global.showDialog(ContactSelectionActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.delete_duplicate_contacts(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult != null) {
                    if (jsonResult.isSuccess()) {
                        Log.d(Global.TAG, "onResponse: Delete true " + jsonResult.getResult());
                        Toast.makeText(ContactSelectionActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        if (onboardFlag) {
                            Log.d(Global.TAG, "Onboard true ContactSelection: ");
                            Intent intent = new Intent(ContactSelectionActivity.this, MainOnboardingActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("onboardFlag", true);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Log.d(Global.TAG, "Onboard Flow false: ");
                        }
                        finish();
                    } else {
                        Log.d(Global.TAG, "onResponse: Delete false " + jsonResult.getResult());
                        Toast.makeText(ContactSelectionActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "Delete Duplicate onFailure: " + t);
                Toast.makeText(ContactSelectionActivity.this, "Error..! Please try again later..!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
