package com.success.successEntellus.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.fragment.MyContactFragment;
import com.success.successEntellus.lib.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OnboardingImportContactActivity extends AppCompatActivity {
Button btn_import_phone_contacts,btn_import_next,btn_import_skip;
    private static final int REQUEST_PERMISSIONS = 101;
    private static final int ACTIVITY_CHOOSE_FILE = 1 ;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    Cursor cursor;
    Dialog Loader;
    int counter;
    JSONArray usersContact;
    ArrayList<String> contactList,contactMobile,contactEmail;
    private boolean boolean_permission;
    boolean onboardFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_import_contact);
        getSupportActionBar().hide();
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            onboardFlag=bundle.getBoolean("onboardFlag");
            Log.d(Global.TAG, "OnboardingImportContactActivity: onboardFlag "+onboardFlag);
        }

        init();
        fn_permission();

        btn_import_phone_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                }else{
                    Intent intent=new Intent(OnboardingImportContactActivity.this,ContactSelectionActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("contact_flag","1");
                    bundle.putBoolean("onboardFlag",true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Importing Contacts..Please Wait..", Toast.LENGTH_SHORT).show();
                }
               // showContacts();

            }
        });

        btn_import_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                }else{
                    Intent intent=new Intent(OnboardingImportContactActivity.this,ContactSelectionActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("contact_flag","1");
                    bundle.putBoolean("onboardFlag",true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Importing Contacts..Please Wait..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_import_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OnboardingImportContactActivity.this,MainOnboardingActivity.class);
                Bundle bundle=new Bundle();
                bundle.putBoolean("onboardFlag",onboardFlag);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }
        });
    }

    private void init() {
        btn_import_phone_contacts=findViewById(R.id.btn_import_phone_contacts);
        btn_import_next=findViewById(R.id.btn_import_next);
        btn_import_skip=findViewById(R.id.btn_import_skip);
    }
    private void showContacts() {

        Log.d(Global.TAG, "showContacts: ");
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            if(Global.isNetworkAvailable(OnboardingImportContactActivity.this)) {
               getContactsClass contacts=new getContactsClass();
                contacts.execute();
                Log.d(Global.TAG, "showContacts: "+contacts);
            }else{
                Intent intent = new Intent(OnboardingImportContactActivity.this, NetworkCheckActivity.class);
                startActivity(intent);
            }
        }
    }

    private class getContactsClass extends AsyncTask<String, Void, JSONArray> {
        JSONObject user;
        ProgressDialog dialog=new ProgressDialog(OnboardingImportContactActivity.this);
        boolean isSuccess;

        @Override
        protected void onPreExecute (){
            Log.d(Global.TAG, "onPreExecute: ");
            Loader = Global.showDialog(OnboardingImportContactActivity.this);
            Loader.show();
            Loader.setCanceledOnTouchOutside(true);
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
            ContentResolver contentResolver =getContentResolver();

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
                        String[] name_array=name.split(" ");
                       /* String fname=name_array[0];
                        String lname=name_array[1];
                        Log.d(Global.TAG, "Fname: "+fname+" Lname: "+lname);
*/
                        String lname="",fname="";
                        if (name_array.length>0){

                            if (name_array.length==0){
                                fname=name_array[0];
                            }else if (name_array.length==2){
                                fname=name_array[0];
                                lname=name_array[1];
                            }else if (name_array.length==3){
                                fname=name_array[0];
                                lname=name_array[1];
                            }
                            Log.d(Global.TAG, "Fname: "+fname+" Lname: "+lname);
                        }

                        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                        if (hasPhoneNumber > 0) {
                            try {
                                // user.put("firstName", name);
                                user.put("contact_fname", fname);
                                user.put("contact_lname", lname);
                                Log.d(Global.TAG, "Contact Name: " + name);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            try {
                                user.put("contact_address","");
                                user.put("contact_city","");
                                user.put("contact_state","");
                                user.put("contact_country","");
                                user.put("contact_zip","");
                                user.put("contact_skype_id","");
                                user.put("contact_twitter_name","");
                                user.put("contact_facebookurl","");
                                user.put("contact_linkedinurl","");
                                user.put("contact_description","");
                                user.put("contact_company_name","");
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

                                if (contactMobile.size()>0){
                                    Log.d(Global.TAG, "phone: " + contactMobile);
                                    Log.d(Global.TAG, "Contact Mobile length: "+contactMobile.size());

                                    if (contactMobile.size()==1){
                                        user.put("contact_phone",contactMobile.get(0));
                                    }else if (contactMobile.size()==2){
                                        user.put("contact_phone",contactMobile.get(0));
                                        user.put("contact_work_phone",contactMobile.get(1));
                                    }else if (contactMobile.size()==3){
                                        user.put("contact_phone",contactMobile.get(0));
                                        user.put("contact_work_phone", contactMobile.get(1));
                                        user.put("contact_other_phone", contactMobile.get(2));
                                    }
                                }else{
                                    user.put("contact_phone","");
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
                                if (contactEmail.size()>0){
                                    Log.d(Global.TAG, "Email: " + contactEmail);
                                    Log.d(Global.TAG, "Contact Email length: "+contactEmail.size());

                                    if (contactEmail.size()==1){
                                        user.put("contact_email",contactEmail.get(0));
                                    }else if (contactEmail.size()==2){
                                        user.put("contact_email",contactEmail.get(0));
                                        user.put("contact_work_email",contactEmail.get(1));
                                    }else if (contactEmail.size()==3){
                                        user.put("contact_email",contactEmail.get(0));
                                        user.put("contact_work_email", contactEmail.get(1));
                                        user.put("contact_other_email", contactEmail.get(2));
                                    }
                                }else{
                                    user.put("contact_email","");
                                }
                                //user.put("email", new JSONArray(contactEmail));
                                //user.put("email", contactEmail);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            emailCursor.close();


                        }
                        usersContact.put(user);
                    }
                } else {
                    isSuccess = false;
                    //Toast.makeText(getActivity(), "No Contacts Found..", Toast.LENGTH_SHORT).show();
                    //Log.d(Global.TAG, "No COntacts Found..");
                }
            }
            Log.d(Global.TAG, "User Contact Array: "+usersContact);
            return usersContact;
        }

        @Override
        protected void onPostExecute (JSONArray result){
            super.onPostExecute(result);
            Log.e("eventIdPost", String.valueOf(result));
            Loader.dismiss();
            if (isSuccess){
                Intent intent=new Intent(OnboardingImportContactActivity.this, ContactSelectionActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("all_contacts",result.toString());
                bundle.putString("contact_flag","1");
                bundle.putBoolean("onboardFlag",onboardFlag);
                intent.putExtras(bundle);
                startActivityForResult(intent,200);
                //uploadPhoneContactToDB(result);
            }else{
                Toast.makeText(OnboardingImportContactActivity.this, "No Contacts Found..!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    /*  private class getContactsClass extends AsyncTask<String, Void, JSONArray> {
        JSONObject user;
        ProgressDialog dialog=new ProgressDialog(OnboardingImportContactActivity.this);
        boolean isSuccess;

        @Override
        protected void onPreExecute (){
            Log.d(Global.TAG, "onPreExecute: ");
            Loader = Global.showDialog(OnboardingImportContactActivity.this);
            Loader.show();
            Loader.setCanceledOnTouchOutside(true);
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

                        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                        if (hasPhoneNumber > 0) {
                            try {
                                user.put("firstName", name);
                                Log.d(Global.TAG, "Contact Name: " + name);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //This is to read multiple phone numbers associated with the same contact
                            Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                            while (phoneCursor.moveToNext()) {
                                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                                contactMobile.add(phoneNumber);
                                try {
                                    user.put("phone", new JSONArray(contactMobile));
                                    //user.put("phone",contactMobile);
                                    Log.d(Global.TAG, "phone: " + contactMobile);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            phoneCursor.close();
                            // Read every email id associated with the contact
                            Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                            while (emailCursor.moveToNext()) {
                                email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                                contactEmail.add(email);
                                try {
                                    user.put("email", new JSONArray(contactEmail));
                                    Log.d(Global.TAG, "contactEmail: " + contactEmail);
                                    //user.put("email", contactEmail);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            emailCursor.close();
                        }
                        usersContact.put(user);
                    }
                } else {
                    isSuccess = false;
                    //Toast.makeText(getActivity(), "No Contacts Found..", Toast.LENGTH_SHORT).show();
                    //Log.d(Global.TAG, "No COntacts Found..");
                }
            }
            return usersContact;
        }

        @Override
        protected void onPostExecute (JSONArray result){
            super.onPostExecute(result);
            Log.e("eventIdPost", String.valueOf(result));
            Loader.dismiss();
            if (isSuccess){
                Intent intent=new Intent(OnboardingImportContactActivity.this, ContactSelectionActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("all_contacts",result.toString());
                bundle.putBoolean("onboardFlag",onboardFlag);
                intent.putExtras(bundle);
                startActivityForResult(intent,200);
                finish();
                //uploadPhoneContactToDB(result);
            }else{
                Toast.makeText(OnboardingImportContactActivity.this, "No Contacts Found..!", Toast.LENGTH_SHORT).show();
            }

        }
    }*/
    private boolean fn_permission() {
        if ((ContextCompat.checkSelfPermission(OnboardingImportContactActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(OnboardingImportContactActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(OnboardingImportContactActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(OnboardingImportContactActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
            if ((ActivityCompat.shouldShowRequestPermissionRationale(OnboardingImportContactActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(OnboardingImportContactActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
            boolean_permission = false;
        } else {
            boolean_permission = true;
        }
        return boolean_permission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==PERMISSIONS_REQUEST_READ_CONTACTS){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(Global.TAG, "onRequestPermissionsResult: Granted ");
                Intent intent=new Intent(OnboardingImportContactActivity.this,ContactSelectionActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("contact_flag","1");
                bundle.putBoolean("onboardFlag",onboardFlag);
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Importing Contacts..Please Wait..", Toast.LENGTH_SHORT).show();
            } else {
                // Permission Denied
                Toast.makeText(OnboardingImportContactActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
