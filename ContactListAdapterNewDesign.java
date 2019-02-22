package com.success.successEntellus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivityNew;
import com.success.successEntellus.activity.ContactSelectionActivity;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.fragment.MyContactFragmentNew;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.RecyclerTouchListener;
import com.success.successEntellus.model.CRMDetail;
import com.success.successEntellus.model.Contact;
import com.success.successEntellus.model.Contact_Result;
import com.success.successEntellus.model.Contact_Spinner;
import com.success.successEntellus.model.Contact_Tag;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.ContactHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 12/6/2018.
 */

public class ContactListAdapterNewDesign extends RecyclerView.Adapter<ContactHolder> implements TagSpinnerAdapter.RefreashTags {
    LayoutInflater inflater;
    DashboardActivity context;
    List<Contact> contactList;
    String user_id;
    boolean flag=false;
    String crm_flag;
    MyContactFragmentNew myContactFragment;
    String initial="";
    Paint paint;
    View layout;
    Random rnd;
    int lastPosition=-1;
    String caldate="",calTime="";
    int eventDescripId=0;
    List<Contact_Tag> tagList;
    String selected_tag="";
    ListView lv_tag;
    int tag_position=0;

    @Override
    public void refreashTagList() {
        getAllTagList(tag_position);
    }

    public interface NotifyRefreshContact{
        void refreshContact();
    }
    ContactListAdapterNewDesign.NotifyRefreshContact notifyRefreshContact;


    public ContactListAdapterNewDesign(DashboardActivity context, MyContactFragmentNew myContactFragment, List<Contact> contactList, String user_id, boolean flag, String crm_flag, NotifyRefreshContact notifyRefreshContact) {
        this.context = context;
        this.contactList = contactList;
        this.user_id = user_id;
        this.flag = flag;
        this.crm_flag = crm_flag;
        this.myContactFragment = myContactFragment;
        this.notifyRefreshContact = notifyRefreshContact;
        paint=new Paint();
        rnd = new Random();
        tagList=new ArrayList<>();
        Log.d(Global.TAG, "ContactListAdapter: Flag " + flag);
        //this.myContactFragment =new MyContactFragment(crm_flag);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.new_contact_row,parent,false);
        ContactHolder contactViewHolder=new ContactHolder(layout);

        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, final int position) {

        holder.setIsRecyclable(false);
        holder.tv_cname.setText(contactList.get(position).getContact_fname()+" "+contactList.get(position).getContact_lname());
        //holder.tv_cemail.setText(contactList.get(position).getContact_email());
        tag_position=position;
        if (contactList.get(position).getContact_email()!=null){
            if (!contactList.get(position).getContact_email().equals("")){
                holder.tv_cemail.setTextSize(14);
                holder.tv_cemail.setText(contactList.get(position).getContact_email());
            }else{
                holder.tv_cemail.setText("Not Available");
                holder.tv_cemail.setTextSize(12);
            }
        }else{
            holder.tv_cemail.setText("Not Available");
        }

        if (contactList.get(position).getContact_phone()!=null){
            if (!contactList.get(position).getContact_phone().equals("")){
                holder.tv_cphone.setTextSize(14);
                holder.tv_cphone.setText(contactList.get(position).getContact_phone());
            }else{
                holder.tv_cphone.setText("Not Available");
                holder.tv_cphone.setTextSize(12);
            }
        }else{
            holder.tv_cphone.setText("Not Available");
        }

        String created_date=contactList.get(position).getContact_created();
        String[] date_time=created_date.split(" ");

        String cdate=date_time[0];
        try {
            String final_date=convertDateTomdy(cdate);
            holder.tv_contact_date.setText(final_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.iv_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Add Tag", Toast.LENGTH_SHORT).show();
                openDialogChangeTag(position);
            }
        });

        /*holder.ib_contact_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "Menu Clicked..: ");
                touchListener.openSwipeOptions(position);
                Log.d(Global.TAG, "position: "+position);
            }
        });*/

        if(flag) { //Check flag for set the all checkbox check and add the all contact id to arraylist to delete all the seelctd contact
            myContactFragment.listDeleteChecklistContact.clear();
            holder.ch_ccheck.setChecked(true);
           /* String contact_id=contactList.get(position).getContact_id();
            if (myContactFragment.listDeleteChecklistContact.contains(contact_id)){
                Log.d(Global.TAG, "Already Exist..!: ");
            }else{
                myContactFragment.listDeleteChecklistContact.add(contact_id);
            }*/
            if (contactList.size()>300){
                Log.d(Global.TAG, "onBindViewHolder: ");
                for(int i=0;i<300;i++){
                    String contact_id=contactList.get(i).getContact_id();
                    if (myContactFragment.listDeleteChecklistContact.contains(contact_id)){
                        Log.d(Global.TAG, "Already Exist..!: ");
                    }else{
                        myContactFragment.listDeleteChecklistContact.add(contact_id);
                    }
                    Log.d(Global.TAG, "listDeleteChecklistContact.add: "+contactList.get(i).getContact_id());
                }
            }else{
                for(int i=0;i<contactList.size();i++){
                    String contact_id=contactList.get(i).getContact_id();
                    if (myContactFragment.listDeleteChecklistContact.contains(contact_id)){
                        Log.d(Global.TAG, "Already Exist..!: ");
                    }else{
                        myContactFragment.listDeleteChecklistContact.add(contact_id);
                    }
                    Log.d(Global.TAG, "listDeleteChecklistContact.add: "+contactList.get(i).getContact_id());
                }
            }
            Log.d(Global.TAG, "listDeleteChecklistContact: "+myContactFragment.listDeleteChecklistContact.size());
        }/*else{
            if (myContactFragment.listDeleteChecklistContact.size()>0) {
                myContactFragment.listDeleteChecklistContact.clear();
            }
        }*/

        String tag=contactList.get(position).getContact_category();

        Log.d(Global.TAG, "onBindViewHolder: tag: "+tag);

        if (tag!=null){
            if (!tag.equals("")){
                if (tag.equals("Red Apple")){
                    holder.iv_add_tag.setImageResource(R.mipmap.red);
                }else if (tag.equals("Green Apple")){
                    holder.iv_add_tag.setImageResource(R.mipmap.green);
                }else if (tag.equals("Brown Apple")){
                    holder.iv_add_tag.setImageResource(R.mipmap.brown);
                }else if (tag.equals("Rotten Apple")){
                    holder.iv_add_tag.setImageResource(R.mipmap.rotten);
                }else{
                    holder.iv_add_tag.setImageResource(R.mipmap.custom_tag);
                }
            }else{
                holder.iv_add_tag.setImageResource(R.mipmap.no_tag);
            }
        }

        holder.rowFG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, " itemview onClick: ");
                getCRMDetails(position);

            }
        });

        // dynamic initials
        /*int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        paint.setColor(Color.WHITE);
        holder.iv_contact_image.setFillColor(color);


        if (contactList.get(position).getContact_fname()!=null){
            if (!contactList.get(position).getContact_fname().equals("")){
                String first_name=contactList.get(position).getContact_fname();
                initial=first_name.substring(0,1).toUpperCase();
                Log.d(Global.TAG, "inital: "+initial);

                Bitmap b=Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);
                c.drawText(initial,12,18,paint);
                holder.iv_contact_image.setImageBitmap(b);
            }
        }*/
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.iv_contact_image.setMonoColor(color,Color.WHITE);

        if (!contactList.get(position).getContact_fname().equals("")){
            holder.iv_contact_image.loadThumbForName( "",contactList.get(position).getContact_fname());
        }else{
            holder.iv_contact_image.loadThumbForName( "",contactList.get(position).getContact_lname());
        }


        /*if (myContactFragment.listDeleteChecklistContact.size()==0){
            myContactFragment.delete_contact.setVisible(false);
            myContactFragment.move_contact.setVisible(false);
            myContactFragment.assign_camp.setVisible(false);
        }*/

        holder.ch_ccheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){

                    // myContactFragment.checked=true;
                    if (MyContactFragmentNew.listDeleteChecklistContact.contains(contactList.get(position).getContact_id())){
                        Log.d(Global.TAG, "Already Exist..!: ");
                    }else if(!MyContactFragmentNew.listDeleteChecklistContact.contains(contactList.get(position).getContact_id())){
                        // myContactFragment.checked=false;
                        MyContactFragmentNew.listDeleteChecklistContact.add(contactList.get(position).getContact_id());
                        Log.d(Global.TAG, " ch_ccheck onCheckedChanged: "+myContactFragment.listDeleteChecklistContact.size());
                        Log.d(Global.TAG, "onCheckedChanged:add List:"+myContactFragment.listDeleteChecklistContact);
                        MyContactFragmentNew.delete_contact.setVisible(true);
                        MyContactFragmentNew.move_contact.setVisible(true);
                        MyContactFragmentNew.assign_camp.setVisible(true);
                        MyContactFragmentNew.action_add_to_group.setVisible(true);
                       // context.setTitle(""+myContactFragment.listDeleteChecklistContact.size());
                    }

                }else{
                    if (MyContactFragmentNew.listDeleteChecklistContact.contains(contactList.get(position).getContact_id())){
                        MyContactFragmentNew.listDeleteChecklistContact.remove(contactList.get(position).getContact_id());
                        Log.d(Global.TAG, "ch_ccheck onCheckedChanged: "+myContactFragment.listDeleteChecklistContact.size());
                        Log.d(Global.TAG, "onCheckedChanged:remove List:"+myContactFragment.listDeleteChecklistContact.toString());
                    }


                    if (MyContactFragmentNew.listDeleteChecklistContact.size()==0){
                        MyContactFragmentNew.delete_contact.setVisible(false);
                        MyContactFragmentNew.move_contact.setVisible(false);
                        MyContactFragmentNew.assign_camp.setVisible(false);
                        MyContactFragmentNew.action_add_to_group.setVisible(false);
                    }

                }
            }
        });

        if(MyContactFragmentNew.listDeleteChecklistContact.contains(contactList.get(position).getContact_id())){
            holder.ch_ccheck.setChecked(true);
        }else{
            holder.ch_ccheck.setChecked(false);
        }


        setAnimation(holder.itemView,position);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
    }
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    private void sendDataToToViewContact(int position, String strassign_email_campaigns, String strassigned_groups, String strassigned_text_campaigns) {


        Intent intent = new Intent(context, AddContactActivityNew.class);
        Bundle bundle=new Bundle();
        bundle.putBoolean("ContactEditable",false);
        bundle.putString("crm_flag",crm_flag);
        bundle.putBoolean("viewFlag",true);
        bundle.putString("contact_id",contactList.get(position).getContact_id());
        bundle.putString("contact_title","Edit Contact Details");
        bundle.putString("c_fname",contactList.get(position).getContact_fname());
        bundle.putString("c_lname",contactList.get(position).getContact_lname());
        bundle.putString("c_email",contactList.get(position).getContact_email());
        bundle.putString("c_email1",contactList.get(position).getContact_work_email());
        bundle.putString("c_email2",contactList.get(position).getContact_other_email());
        bundle.putString("phone",contactList.get(position).getContact_phone());
        bundle.putString("phone1",contactList.get(position).getContact_work_phone());
        bundle.putString("phone2",contactList.get(position).getContact_other_phone());
        bundle.putString("date_of_birth",contactList.get(position).getContact_date_of_birth());

        bundle.putString("contact_for",contactList.get(position).getContact_lead_prospecting_for());
        bundle.putString("contact_status",contactList.get(position).getContact_lead_status_id());
        bundle.putString("contact_source",contactList.get(position).getContact_lead_source_id());;
        bundle.putString("industry",contactList.get(position).getContact_industry());

        bundle.putString("company_name",contactList.get(position).getContact_company_name());
        bundle.putString("contact_address",contactList.get(position).getContact_address());
        bundle.putString("tag",contactList.get(position).getContact_category());
        bundle.putString("contact_city",contactList.get(position).getContact_city());
        bundle.putString("contact_state",contactList.get(position).getContact_state());
        bundle.putString("contact_zip",contactList.get(position).getContact_zip());
        bundle.putString("contact_country",contactList.get(position).getContact_country());
        bundle.putString("contact_description",contactList.get(position).getContact_description());
        bundle.putString("contact_id",contactList.get(position).getContact_id());

        bundle.putString("caldate",caldate);
        bundle.putString("calTime",calTime);
        bundle.putInt("eventDescripId",eventDescripId);

        bundle.putString("strassign_email_campaigns",strassign_email_campaigns);
        bundle.putString("strassigned_groups",strassigned_groups);
        bundle.putString("strassigned_text_campaigns",strassigned_text_campaigns);


        Log.d(Global.TAG, "edit contact_for"+contactList.get(position).getContact_lead_prospecting_for());
        Log.d(Global.TAG, "edit Tag"+contactList.get(position).getContact_category());
        // Log.d(Global.TAG,"edit contact_source"+contactList.get(position).getContact_lead_source_id());
        intent.putExtras(bundle);
        myContactFragment.startActivityForResult(intent,10);
    }
    private String convertDateTomdy(String date) throws ParseException {
        //Date date1 = new Date();
        Log.d(Global.TAG, "convertDateTomdy Date-->" + date);
        //SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        SimpleDateFormat format = new SimpleDateFormat("MMM d yyyy");
        // String format = new SimpleDateFormat("MMM d, yyyy").format();
        //Date dateObj=format.parse(date);
        String final_date = format.format(date1);
        Log.d(Global.TAG, "convertDateTomdy mdy Date-->" + final_date);
        return final_date;
    }
    private void getCRMDetails(final int position) {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contactId",contactList.get(position).getContact_id());


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getCRMDetails: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getCRMDetails: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<CRMDetail> call=servive.getCRMDetail(paramObj.toString());
        call.enqueue(new Callback<CRMDetail>() {
            @Override
            public void onResponse(Call<CRMDetail> call, Response<CRMDetail> response) {
                if (response.isSuccessful()){
                    CRMDetail crmDetail=response.body();
                    if (crmDetail.isSuccess()){
                        Contact contact=crmDetail.getResult();
                        List<String> assigned_email_campaigns=contact.getContact_campaignAssign();
                        List<String> assigned_text_campaigns=contact.getContact_txtCampaignAssign();
                        List<String> assigned_groups=contact.getContact_groupAssign();

                        Log.d(Global.TAG, "assigned_email_campaigns: "+assigned_email_campaigns.size());
                        Log.d(Global.TAG, "assigned_text_campaigns: "+assigned_text_campaigns.size());
                        Log.d(Global.TAG, "assigned_groups: "+assigned_groups.size());

                        caldate=contact.getCaldate();
                        calTime=contact.getCalTime();
                        eventDescripId=contact.getEventDescripId();

                        Log.d(Global.TAG, "caldate: "+caldate);
                        Log.d(Global.TAG, "calTime: "+calTime);
                        Log.d(Global.TAG, "eventDescripId: "+eventDescripId);

                        String strassign_email_campaigns="",strassigned_groups="",strassigned_text_campaigns="";
                        if (assigned_email_campaigns.size()>0){
                            strassign_email_campaigns= setListString(assigned_email_campaigns);
                        }

                        if (assigned_groups.size()>0){
                            strassigned_groups= setListString(assigned_groups);
                        }

                        if (assigned_text_campaigns.size()>0){
                            strassigned_text_campaigns=setListString(assigned_text_campaigns);
                        }

                        Log.d(Global.TAG, "strassign_email_campaigns: "+strassign_email_campaigns);
                        Log.d(Global.TAG, "strassigned_groups: "+strassigned_groups);
                        Log.d(Global.TAG, "strassigned_text_campaigns: "+strassigned_text_campaigns);

                        sendDataToToViewContact(position,strassign_email_campaigns,strassigned_groups,strassigned_text_campaigns);


                    }else{

                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CRMDetail> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: CRMDetail "+t);

            }
        });

    }
    private String setListString(List<String> assigned_email_campaigns) {

        String assigned_campaigns="";
        for (int i=0;i<assigned_email_campaigns.size();i++){
            assigned_campaigns=assigned_campaigns+" "+assigned_email_campaigns.get(i)+",";
        }

        if (assigned_campaigns.endsWith(",")) {
            assigned_campaigns = assigned_campaigns.substring(0, assigned_campaigns.length() - 1);
        }
        Log.d(Global.TAG, "assigned_campaigns: "+assigned_campaigns);
        return assigned_campaigns;
    }

    private void openDialogChangeTag(final int position) {
        Log.d(Global.TAG, "openDialogChangeTag: ");
        final Dialog dialog_change_tag = new Dialog(context);
        dialog_change_tag.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_change_tag.setCancelable(true);
        dialog_change_tag.setContentView(R.layout.update_tag_dialog);
        dialog_change_tag.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_change_tag.setTitle("Conformation");

        lv_tag=dialog_change_tag.findViewById(R.id.lv_tag);
        LinearLayout ll_add_custom_tag=dialog_change_tag.findViewById(R.id.ll_add_custom_tag);
        final LinearLayout ll_custom_new_tag=dialog_change_tag.findViewById(R.id.ll_custom_new_tag);
        final EditText edt_custom_tag=dialog_change_tag.findViewById(R.id.edt_custom_tag);
        Button btn_add_custom_tag=dialog_change_tag.findViewById(R.id.btn_add_custom_tag);


        getAllTagList(position);


        ll_add_custom_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_custom_new_tag.getVisibility()==View.VISIBLE){
                    ll_custom_new_tag.setVisibility(View.GONE);
                }else{
                    ll_custom_new_tag.setVisibility(View.VISIBLE);
                }
            }
        });

        lv_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                Log.d(Global.TAG, "Position in spinner: "+pos);

                //selected_tag=tagList.get(pos).getUserTagName();

                if (tagList.get(pos).getUserTagName().contains("Select")){
                    selected_tag="";
                    updateTagtoDatabase(contactList.get(position).getContact_id(),dialog_change_tag,"");
                }else{
                    selected_tag=tagList.get(pos).getUserTagName();
                    updateTagtoDatabase(contactList.get(position).getContact_id(),dialog_change_tag,tagList.get(pos).getUserTagId());
                }



            }
        });

        btn_add_custom_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_custom_tag,"Enter Tag to Add")){
                    selected_tag=edt_custom_tag.getText().toString();
                    updateTagtoDatabase(contactList.get(position).getContact_id(),dialog_change_tag,"0");
                    dialog_change_tag.dismiss();
                }
            }
        });

        dialog_change_tag.show();
        Window window = dialog_change_tag.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //dialog.show();
       /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,WindowManager.LayoutParams.WRAP_CONTENT , getResources().getDisplayMetrics());
        dialog.getWindow().setAttributes(lp);*/
    }

    private void getAllTagList(final int position) {

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
                if (response.isSuccessful()){
                    Contact_Spinner contact_spinner=response.body();
//                contactStatusList.clear();
//                contactSourceList.clear();
                    if (contact_spinner.isSuccess()){
                        Log.d(Global.TAG, "Spinner data Get Successfully..!: ");
                        Contact_Result contact_result=contact_spinner.getResult();
                        tagList=contact_result.getContact_category();

                        Contact_Tag contact_tag=new Contact_Tag();
                        contact_tag.setUserTagName("Select Tag");
                        contact_tag.setUserTagId("");
                        contact_tag.setUserTagUserId("-1");
                        tagList.add(0,contact_tag);

                        String tag_name=contactList.get(position).getContact_category();
                        TagSpinnerAdapter adapter=new TagSpinnerAdapter(context,tagList,user_id,ContactListAdapterNewDesign.this,tag_name);
                        lv_tag.setAdapter(adapter);

                        Log.d(Global.TAG, "contactTagList: "+tagList.size());

                    }else{
                        Log.d(Global.TAG, "Error in Getting Data..!");
                    }
                }

            }

            @Override
            public void onFailure(Call<Contact_Spinner> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:getSpinnerData "+t);
            }
        });

    }

    private void updateTagtoDatabase(String contact_id, final Dialog dialog,final String tag_id) {
        // Toast.makeText(context, "Tag Updated", Toast.LENGTH_SHORT).show();
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "updateTagtoDatabase:contact_id "+contact_id+ "user_id "+user_id);
        Log.d(Global.TAG, "updateTagtoDatabase:contact_category_title "+selected_tag+ "contact_category "+tag_id);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=servive.update_tag(user_id,"2",contact_id,tag_id,selected_tag);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result=response.body();
                if (result.isSuccess()){
                    Toast.makeText(context, ""+result.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "onResponse: Issuccess True:"+result.getResult());
                    dialog.dismiss();
                   notifyRefreshContact.refreshContact();
                    //  getContactDetails();

                    //myContactFragment.getContactDetails(context);
                }else{
                    Toast.makeText(context, ""+result.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "onResponse: Issuccess false:"+result.getResult());
                    //dialog.dismiss();
                    //getContactDetails();
                    // getContactDetails();

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: UpdateTag"+t);
            }
        });

    }

}
