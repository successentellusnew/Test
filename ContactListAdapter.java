package com.success.successEntellus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.fragment.MyContactFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.AssignCampaign;
import com.success.successEntellus.model.AssignUnAssign;
import com.success.successEntellus.model.AssignUnAssignCampaign;
import com.success.successEntellus.model.CRMDetail;
import com.success.successEntellus.model.Contact;
import com.success.successEntellus.model.ContactDetails;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.UnAssignCampaign;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.ContactViewHolder;
import com.vincent.filepicker.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 4/9/2018.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactViewHolder> {
    LayoutInflater inflater;
    Context context;
    List<Contact> contactList;
    //List<String> listDeleteChecklistContact;
    List<String> tagList;
    String user_id;
    boolean flag=false;
    String crm_flag;
    String selected_tag;
    private String selected_campaign;
    List<AssignCampaign> campaignassignCampaignList;
    private String[] assignCampaignArray;
    MyContactFragment myContactFragment;
    LinearLayout ll_added_campaigns,ll_assigned_campaigns;
     Spinner sp_assign_campaign;
     TextView tv_no_campaigns_added,tv_no_campaigns_assignes;

    public interface NotifyRefreshContact{
        void refreshContact();
    }
    NotifyRefreshContact notifyRefreshContact;

    public ContactListAdapter(Context context, MyContactFragment myContactFragment, List<Contact> contactList, String user_id, boolean flag, String crm_flag, NotifyRefreshContact notifyRefreshContact) {
        this.context=context;
        this.contactList=contactList;
        this.user_id=user_id;
        this.flag=flag;
        this.crm_flag=crm_flag;
        this.myContactFragment=myContactFragment;
        this.notifyRefreshContact=notifyRefreshContact;
        Log.d(Global.TAG, "ContactListAdapter: Flag "+flag);
        //this.myContactFragment =new MyContactFragment(crm_flag);

        tagList=new ArrayList<>();
        tagList.add("Select Tag");
        tagList.add("Red Apple");
        tagList.add("Green Apple");
        tagList.add("Brown Apple");
        tagList.add("Rotten Apple");
       // myContactFragment=new MyContactFragment();
        //listDeleteChecklistContact=new ArrayList<>();
        campaignassignCampaignList=new ArrayList<>();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout=inflater.inflate(R.layout.contact_row,parent,false);
        ContactViewHolder contactViewHolder=new ContactViewHolder(context,layout,contactList);

        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        holder.tv_cname.setText(contactList.get(position).getContact_fname()+" "+contactList.get(position).getContact_middle()+" "+contactList.get(position).getContact_lname());
        holder.tv_cemail.setText(contactList.get(position).getContact_email());
        holder.tv_cphone.setText(contactList.get(position).getContact_phone());
        holder.position=position;
        holder.crm_flag=crm_flag;
        holder.contact_id=contactList.get(position).getContact_id();


       /* if (contactList.get(position).getContact_category()!=""){
            holder.tv_ctag.setText(contactList.get(position).getContact_category());
        }else{
            holder.tv_ctag.setText("Add Tag");
        }*/
        holder.ch_ccheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){

                   // myContactFragment.checked=true;
                    if (myContactFragment.listDeleteChecklistContact.contains(contactList.get(position).getContact_id())){
                        Log.d(Global.TAG, "Already Exist..!: ");
                    }else{
                       // myContactFragment.checked=false;
                        myContactFragment.listDeleteChecklistContact.add(contactList.get(position).getContact_id());
                        Log.d(Global.TAG, " ch_ccheck onCheckedChanged: "+myContactFragment.listDeleteChecklistContact.size());
                        Log.d(Global.TAG, "onCheckedChanged:add List:"+myContactFragment.listDeleteChecklistContact);
                        myContactFragment.delete_contact.setVisible(true);
                        myContactFragment.move_contact.setVisible(true);
                        myContactFragment.assign_camp.setVisible(true);
                    }

                }else{
                    myContactFragment.listDeleteChecklistContact.remove(contactList.get(position).getContact_id());
                    Log.d(Global.TAG, "ch_ccheck onCheckedChanged: "+myContactFragment.listDeleteChecklistContact.size());
                    Log.d(Global.TAG, "onCheckedChanged:remove List:"+myContactFragment.listDeleteChecklistContact.toString());

                    if (myContactFragment.listDeleteChecklistContact.size()==0){
                        myContactFragment.delete_contact.setVisible(false);
                        myContactFragment.move_contact.setVisible(false);
                        myContactFragment.assign_camp.setVisible(false);
                    }

                }

            }
        });
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
        }else{
            if (myContactFragment.listDeleteChecklistContact.size()>0) {
                myContactFragment.listDeleteChecklistContact.clear();
            }
        }

        if (!contactList.get(position).getContact_category().equals("")) {
            holder.iv_tag.setVisibility(View.GONE);
            SpannableString content = new SpannableString(contactList.get(position).getContact_category());
            content.setSpan(new UnderlineSpan(), 0, contactList.get(position).getContact_category().length(), 0);
            holder.tv_ctag.setText(content);
            Log.d(Global.TAG, "Tag From List: "+content);
            Log.d(Global.TAG, "Tag From: "+contactList.get(position).getContact_category());

            String[] separated = contactList.get(position).getContact_category().split(" ");
            if (separated[0].equals("Green")) {
                holder.tv_ctag.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.green_apple, 0, 0, 0);
                holder.tv_ctag.setTextColor(context.getResources().getColor(R.color.colorGreen));
                //spin_tag[i].setSelection(1);
            } else if (separated[0].equals("Red")) {
                holder.tv_ctag.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.red_apple, 0, 0, 0);
                holder.tv_ctag.setTextColor(context.getResources().getColor(R.color.colorRed));
                //spin_tag[i].setSelection(2);
            } else if (separated[0].equals("Brown")) {
                holder.tv_ctag.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.brown_apple, 0, 0, 0);
                holder.tv_ctag.setTextColor(context.getResources().getColor(R.color.colorBrown));
                //spin_tag[i].setSelection(3);
            } else if (separated[0].equals("Rotten")) {
                holder.tv_ctag.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.rotten_apple, 0, 0, 0);
                holder.tv_ctag.setTextColor(context.getResources().getColor(R.color.colorRotten));
                //spin_tag[i].setSelection(4);
            }
        }else {
            holder.tv_ctag.setText("Add Tag");
            holder.tv_ctag.setTextColor(Color.BLACK);
        }

       /* holder.tv_ctag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.iv_tag.setVisibility(View.GONE);
                holder.tv_ctag.setVisibility(View.GONE);
                holder.ll_spinner.setVisibility(View.VISIBLE);
                TagSpinnerAdapter adapter=new TagSpinnerAdapter(context,tagList);
                holder.spin_ctag.setAdapter(adapter);

            }
        });*/
        holder.spin_ctag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            //    Log.d(Global.TAG, "Position: "+i+"Item"+adapterView.getSelectedItem());
//                if ( holder.spin_ctag.getSelectedItem().toString().contains("Select Tag")) {
//                    selected_tag = "";
//                } else {
                String tag=tagList.get(i);
                if (tag.contains("Select Tag")){
                    selected_tag="";
                    Log.d(Global.TAG, "onItemSelected SELECT: spin_ctag "+selected_tag);
                }else{
                    selected_tag = tagList.get(i);
                    Log.d(Global.TAG, "onItemSelected: spin_ctag "+selected_tag);
                   // updateTagtoDatabase(holder,position-1);
                }


              //  }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
       /* holder.ib_contact_menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ll_contact.setVisibility(View.GONE);
                holder.fl_contact_menu.setVisibility(View.VISIBLE);
            }
        });

        holder.iv_contact_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.fl_contact_menu.setVisibility(View.GONE);
                holder.ll_contact.setVisibility(View.VISIBLE);

            }
        });*/

       holder.ib_assign_campaign.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (!contactList.get(position).getContact_email().equals("")){
                   openDialogAssignCampaignContact(position);
               }else{
                   Toast.makeText(context, "Email-Id required for assign Campaign", Toast.LENGTH_LONG).show();
               }

           }
       });

       holder.ib_delete_contact.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               new AlertDialog.Builder(context)
                       .setMessage("Are you sure you want to Delete " + contactList.get(position).getContact_fname()+" "+contactList.get(position).getContact_lname())
                       .setCancelable(false)
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               deleteContact(contactList.get(position).getContact_id(),position);
                           }
                       })
                       .setNegativeButton("No", null)
                       .show();
           }
       });
     /*  holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              // OpenDialogForShowContactDetails(position);
           }
       });*/

       holder.ib_edit_contact.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              //sendDataToUpdateComman(position);
           }
       });
       /* holder.tv_cname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialogForShowContactDetails(position);
            }
        });
*/

      /* simpleItemTouchCallback=new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(context, "onMove.....", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //adapter.remove(viewHolder.getAdapterPosition());

                if (direction==8){
                    notifyItemChanged(viewHolder.getAdapterPosition());
                }
                Toast.makeText(context, "Swipe....."+direction, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                //View itemView = viewHolder.itemView;
                holder.fl_contact_menu.setY(viewHolder.itemView.getTop());
                if(isCurrentlyActive) {
                    holder.fl_contact_menu.setVisibility(View.VISIBLE);
                }else{
                    holder.fl_contact_menu.setVisibility(View.GONE);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                Toast.makeText(context, "onChildDraw...", Toast.LENGTH_SHORT).show();
            }
        };
*/
    }

    /*private void updateTagtoDatabase(ContactViewHolder holder, final int position) {
       // Toast.makeText(context, "Tag Updated", Toast.LENGTH_SHORT).show();
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=servive.update_tag(holder.contact_id,selected_tag);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result=response.body();
                if (result.isSuccess()){
                    Toast.makeText(context, "Tag Updated Successfully..!", Toast.LENGTH_LONG).show();
                    notifyItemChanged(position);
                    notifyRefreshContact.refreshContact();
                    //myContactFragment.getContactDetails(context);
                }else{
                    Toast.makeText(context, "Tag Updated Successfully..!", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: UpdateTag"+t);
            }
        });

    }*/

    private void deleteContact(String contact_id, final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contactIds", contact_id);
            Log.d(Global.TAG, "deleteContact: param:"+paramObj.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_contact(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "Contact Deleted Successfully..!: ");
                    //notifyItemRemoved(position);
                    //getContactDetails(context);
                    notifyRefreshContact.refreshContact();
                }else{
                    Toast.makeText(context, "Error in deleting Contact..!", Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "Error in deleting Contact....!: ");
                }
            }
            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: Delete"+t);
            }
        });
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }




  private void openDialogAssignCampaignContact(final int position) {
      final Dialog dialog = new Dialog(context);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setCancelable(false);
      dialog.setContentView(R.layout.group_assign_campaign);
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
      dialog.setTitle("Conformation");

      Button btn_assign_campaign_dissmiss = (Button) dialog.findViewById(R.id.btn_assign_campaign_dissmiss);
      Button btn_assign_campaign = (Button) dialog.findViewById(R.id.btn_assign_campaign);
      Button btn_assign_cancel = (Button) dialog.findViewById(R.id.btn_assign_cancel);
      TextView tv_ass_group_name = (TextView) dialog.findViewById(R.id.tv_ass_group_name);
      TextView tv_assign_dialog_title = (TextView) dialog.findViewById(R.id.tv_assign_dialog_title);
      TextView tv_assign_title = (TextView) dialog.findViewById(R.id.tv_assign_title);
       tv_no_campaigns_added = (TextView) dialog.findViewById(R.id.tv_no_campaigns_added);
       tv_no_campaigns_assignes = (TextView) dialog.findViewById(R.id.tv_no_campaigns_assignes);
      sp_assign_campaign = (Spinner) dialog.findViewById(R.id.sp_assign_campaign);
      ll_assigned_campaigns = (LinearLayout) dialog.findViewById(R.id.ll_assigned_campaigns);
     // ll_added_campaigns = (LinearLayout) dialog.findViewById(R.id.ll_added_campaigns);

      tv_assign_dialog_title.setText("Add Email to Email campaign");

      if (crm_flag.equals("1")){
          tv_assign_title.setText("Contact Name:");
      }else if (crm_flag.equals("2")){
          tv_assign_title.setText("Customer Name:");
      }else if (crm_flag.equals("3")){
          tv_assign_title.setText("Prospect Name:");
      }else if (crm_flag.equals("4")){
          tv_assign_title.setText("Recruit Name:");
      }

      tv_ass_group_name.setText(contactList.get(position).getContact_fname()+" "+contactList.get(position).getContact_lname());
      btn_assign_campaign_dissmiss.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              dialog.dismiss();
          }
      });

      btn_assign_cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              dialog.dismiss();
          }
      });



      getAssignedUnassignedCampaigns(position);

      sp_assign_campaign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view,
                                     int position, long id) {
              // TODO Auto-generated method stub

              if (sp_assign_campaign.getSelectedItem().toString().contains("Select")) {
                  selected_campaign = "";
              } else {
                  selected_campaign = campaignassignCampaignList.get(position-1).getCampaignId();
              }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
              // TODO Auto-generated method stub
          }
      });

      btn_assign_campaign.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if (!sp_assign_campaign.getSelectedItem().toString().contains("Select")){
                  assignCampaigntoContact(position);
              }else{
                  MyValidator.setSpinnerError(sp_assign_campaign,"select_campaign");
                  Toast.makeText(context, "Please Select Camapign to Add..!", Toast.LENGTH_LONG).show();
              }

          }
      });
      dialog.show();
      Window window = dialog.getWindow();
      window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
  }

    private void assignCampaigntoContact(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contactId", contactList.get(position).getContact_id());
            paramObj.put("campaignId", selected_campaign);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAssignedUnassignedCampaigns: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAssignedUnassignedCampaigns: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.assign_campaign_to_group(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    getAssignedUnassignedCampaigns(position);
                    //getContactDetails();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: assignCampaign "+t);
            }
        });
    }

    private void getAssignedUnassignedCampaigns(final int position) {
      ll_assigned_campaigns.removeAllViews();
      JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contactId",contactList.get(position).getContact_id());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAssignedUnassignedCampaigns: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAssignedUnassignedCampaigns: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<AssignUnAssignCampaign> call=service.getAssignUnAssignCampaign(paramObj.toString());
        call.enqueue(new Callback<AssignUnAssignCampaign>() {
            @Override
            public void onResponse(Call<AssignUnAssignCampaign> call, Response<AssignUnAssignCampaign> response) {
                AssignUnAssignCampaign assignUnAssignCampaign=response.body();
                if (assignUnAssignCampaign.isSuccess()){
                    AssignUnAssign assignUnAssign=assignUnAssignCampaign.getResult();
                    campaignassignCampaignList=assignUnAssign.getAssingCamp();
                    List<UnAssignCampaign> unAssignCampaignList=assignUnAssign.getUnAssingCamp();
                    List<UnAssignCampaign> addedCampaignList=assignUnAssign.getAddedCamp();

                    Log.d(Global.TAG, "assignCampaignList: "+campaignassignCampaignList.size());
                    Log.d(Global.TAG, "unAssignCampaignList: "+unAssignCampaignList.size());
                    Log.d(Global.TAG, "addedCampaignList: "+addedCampaignList.size());

                    assignCampaignArray=new String[campaignassignCampaignList.size()];
                    for (int i=0;i<campaignassignCampaignList.size();i++){
                        assignCampaignArray[i]=campaignassignCampaignList.get(i).getCampaignTitle();
                    }
                    applySpinner(assignCampaignArray,sp_assign_campaign,"--Select Campaign--");

                    if (unAssignCampaignList.size()>0){
                        tv_no_campaigns_assignes.setVisibility(View.GONE);
                        ll_assigned_campaigns.setVisibility(View.VISIBLE);
                        addAssignedCampaigns(unAssignCampaignList,position);
                    }else{
                        ll_assigned_campaigns.setVisibility(View.GONE);
                        tv_no_campaigns_assignes.setVisibility(View.VISIBLE);
                    }

                   /* if (addedCampaignList.size()>0){
                        ll_added_campaigns.setVisibility(View.VISIBLE);
                        tv_no_campaigns_added.setVisibility(View.GONE);
                        addAddedCampaigns(addedCampaignList,position);
                    }else{
                        ll_added_campaigns.setVisibility(View.GONE);
                        tv_no_campaigns_added.setVisibility(View.VISIBLE);
                    }*/

                }else{
                    Toast.makeText(context, ""+assignUnAssignCampaign.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<AssignUnAssignCampaign> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:AssignUnAssignCampaign "+t);

            }
        });
    }
    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        spinnerAdapter adapterRepeateDaily = new spinnerAdapter(context, android.R.layout.simple_list_item_1,campaignassignCampaignList);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);




    }
    public static class spinnerAdapter extends ArrayAdapter<String> {
        List<AssignCampaign> campaignassignCampaignList;

        public spinnerAdapter(Context context, int textViewResourceId, List<AssignCampaign> campaignassignCampaignList) {
            super(context, textViewResourceId);
            this.campaignassignCampaignList=campaignassignCampaignList;
            // TODO Auto-generated constructor stub
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            int count =super.getCount();
            return count>0 ? count-1 : count ;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) view;
                if(!isEnabled(position)) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }

            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            if (position!=0){
                if (campaignassignCampaignList.get(position-1).getCampaignTemplateExits()==1){
                    return true;
                }else  if (campaignassignCampaignList.get(position-1).getCampaignTemplateExits()==0){
                    return false;
                }else{
                    return false;
                }
            }else{
                return true;
            }

        }

    }
    private void addAssignedCampaigns(final List<UnAssignCampaign> unAssignCampaignList, final int position) {

      ll_assigned_campaigns.removeAllViews();
        for (int i=0;i<unAssignCampaignList.size();i++){
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View assignCampaignView = inflater.inflate(R.layout.assigned_campaigns_layout, null);
            TextView textView = (TextView) assignCampaignView.findViewById(R.id.tv_assigned_camp_name);
            Button btn_unassign_campaign=(Button) assignCampaignView.findViewById(R.id.btn_unassign_campaign);
            textView.setText(unAssignCampaignList.get(i).getCampaignTitle());
            final int finalI = i;

            btn_unassign_campaign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, "UnAssign Campaign..!", Toast.LENGTH_SHORT).show();
                    /*new android.app.AlertDialog.Builder(context)
                            .setMessage("All members from this contact is unassigned from the campaign. Are you sure you want to proceed?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();*/
                    unAssignCampaign(unAssignCampaignList.get(finalI).getContactCampaignId(),ll_assigned_campaigns,position,sp_assign_campaign,ll_assigned_campaigns);

                }
            });
            ll_assigned_campaigns.addView(assignCampaignView);
        }
        //ll_assigned_campaigns.addView(assignCampaignView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

    }

   /* private void addAddedCampaigns(final List<UnAssignCampaign> addedCampaignList, final int position) {

        ll_added_campaigns.removeAllViews();
        for (int i=0;i<addedCampaignList.size();i++){
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View assignCampaignView = inflater.inflate(R.layout.added_campaign_layout_crm, null);
            TextView textView = (TextView) assignCampaignView.findViewById(R.id.tv_added_camp_name);
            Button btn_remove_campaign=(Button) assignCampaignView.findViewById(R.id.btn_remove_campaign);
            textView.setText(addedCampaignList.get(i).getCampaignTitle());
            final int finalI = i;

            btn_remove_campaign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, "UnAssign Campaign..!", Toast.LENGTH_SHORT).show();
                    new android.app.AlertDialog.Builder(context)
                            .setMessage("Are you sure to Remove this email to added Email campaign?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    removeCampaign(addedCampaignList.get(finalI).getContactCampaignId(),position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }
            });
            ll_added_campaigns.addView(assignCampaignView);
        }
        //ll_assigned_campaigns.addView(assignCampaignView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

    }
*/

    private void unAssignCampaign(String contactCampaignId, LinearLayout llAssignedCampaigns, final int position, final Spinner sp_assign_campaign, final LinearLayout ll_assigned_campaigns) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contactCampaignId", contactCampaignId);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "unAssignCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "unAssignCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.unAssigncampaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    getAssignedUnassignedCampaigns(position);
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: UnAssign "+t);
            }
        });

    }

    private void removeCampaign(String contactCampaignId, final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contactCampaignId", contactCampaignId);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "removeCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "removeCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.removecampaignCrm(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    getAssignedUnassignedCampaigns(position);
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: removeCampaign "+t);
            }
        });

    }
    private void OpenDialogForShowContactDetails(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_view_contact);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");
        LinearLayout ll_customer_view = (LinearLayout) dialog.findViewById(R.id.ll_customer_view);
        LinearLayout ll_prospect_view = (LinearLayout) dialog.findViewById(R.id.ll_prospect_view);

       // getAssignedUnassignedCampaigns();
        Button dialogBack = (Button) dialog.findViewById(R.id.btn_contact_back);
        Button dialogUpdate = (Button) dialog.findViewById(R.id.btn_contact_edit);
        Button btn_dissmiss = (Button) dialog.findViewById(R.id.btn_dissmiss);
        TextView tv_view_contact_title = (TextView) dialog.findViewById(R.id.tv_view_contact_title);
        TextView tv_fname = (TextView) dialog.findViewById(R.id.tv_fname);
        //TextView tv_mname = (TextView) dialog.findViewById(R.id.tv_mname);
        TextView tv_lname = (TextView) dialog.findViewById(R.id.tv_lname);
        TextView tv_email = (TextView) dialog.findViewById(R.id.tv_email);
        TextView tv_email1 = (TextView) dialog.findViewById(R.id.tv_email1);
        TextView tv_email2 = (TextView) dialog.findViewById(R.id.tv_email2);
        TextView tv_phone = (TextView) dialog.findViewById(R.id.tv_phone);
        TextView tv_phone1 = (TextView) dialog.findViewById(R.id.tv_phone1);
        TextView tv_phone2 = (TextView) dialog.findViewById(R.id.tv_phone2);
        TextView tv_dob = (TextView) dialog.findViewById(R.id.tv_dob);
        TextView tv_doa = (TextView) dialog.findViewById(R.id.tv_doa);
        TextView tv_assigned_camp = (TextView) dialog.findViewById(R.id.tv_assigned_camp);
        TextView tv_assigned_groups = (TextView) dialog.findViewById(R.id.tv_assigned_groups);
        TextView tv_assigned_text_camp = (TextView) dialog.findViewById(R.id.tv_assigned_text_camp);

        TextView tv_ren_date = (TextView) dialog.findViewById(R.id.tv_ren_date);
        TextView tv_industry = (TextView) dialog.findViewById(R.id.tv_industry);
        TextView tv_poli_number = (TextView) dialog.findViewById(R.id.tv_poli_number);
        TextView tv_annual_income = (TextView) dialog.findViewById(R.id.tv_annual_income);
        TextView tv_current_policy = (TextView) dialog.findViewById(R.id.tv_current_policy);
        TextView tv_poli_cmny = (TextView) dialog.findViewById(R.id.tv_poli_cmny);
        TextView tv_cur_poli_amnt = (TextView) dialog.findViewById(R.id.tv_cur_poli_amnt);

        TextView tv_contact_for_title = (TextView) dialog.findViewById(R.id.tv_contact_for_title);
        TextView tv_contact_for = (TextView) dialog.findViewById(R.id.tv_contact_for);
        TextView tv_contact_status_title = (TextView) dialog.findViewById(R.id.tv_contact_status_title);
        TextView tv_contact_status = (TextView) dialog.findViewById(R.id.tv_contact_status);
        TextView tv_contact_source_title = (TextView) dialog.findViewById(R.id.tv_contact_source_title);
        TextView tv_contact_source = (TextView) dialog.findViewById(R.id.tv_contact_source);

        getCRMDetails(tv_assigned_camp,tv_assigned_groups,tv_assigned_text_camp,position);

        if (crm_flag.equals("1")){
            tv_contact_for_title.setText("Contact For");
            tv_contact_status_title.setText("Contact Status");
            tv_contact_source_title.setText("Contact Source");
            tv_view_contact_title.setText("View Contact Details");
        }else if (crm_flag.equals("2")){
            tv_contact_for_title.setText("Customer For");
            tv_contact_status_title.setText("Customer Status");
            tv_contact_source_title.setText("Customer Source");
            tv_view_contact_title.setText("View Customer Details");
        }else if(crm_flag.equals("3")){
            tv_contact_for_title.setText("Prospect For");
            tv_contact_status_title.setText("Prospect Status");
            tv_contact_source_title.setText("Prospect Source");
            tv_view_contact_title.setText("View Prospect Details");
        }else if(crm_flag.equals("4")){
            tv_contact_for_title.setText("Recruit For");
            tv_contact_status_title.setText("Recruit Status");
            tv_contact_source_title.setText("Recruit Source");
            tv_view_contact_title.setText("View Recruit Details");
        }

        //ViewAssignCampaigns(position,tv_assigned_camp);
        TextView tv_compny = (TextView) dialog.findViewById(R.id.tv_compny);
        TextView tv_tag = (TextView) dialog.findViewById(R.id.tv_tag);
        TextView tv_fb = (TextView) dialog.findViewById(R.id.tv_fb);
        TextView tv_skype = (TextView) dialog.findViewById(R.id.tv_skype);
        TextView tv_twit = (TextView) dialog.findViewById(R.id.tv_twit);
        TextView tv_linkin = (TextView) dialog.findViewById(R.id.tv_linkin);
        TextView tv_address = (TextView) dialog.findViewById(R.id.tv_address);
        TextView tv_city = (TextView) dialog.findViewById(R.id.tv_city);
        TextView tv_zip = (TextView) dialog.findViewById(R.id.tv_zip);
        TextView tv_state = (TextView) dialog.findViewById(R.id.tv_state);
        TextView tv_country = (TextView) dialog.findViewById(R.id.tv_country);
        TextView tv_description = (TextView) dialog.findViewById(R.id.tv_description);

        TextView tv_pfa_agent_id = (TextView) dialog.findViewById(R.id.tv_pfa_agent_id);
        TextView tv_nlg_agent_id = (TextView) dialog.findViewById(R.id.tv_nlg_agent_id);
        TextView tv_joining_date = (TextView) dialog.findViewById(R.id.tv_joining_date);
        LinearLayout ll_recruit_details = (LinearLayout) dialog.findViewById(R.id.ll_recruit_details);

        // ll_customer_view.setVisibility(View.GONE); //Hide the customer veiw fro dispaly the contact view
        ll_prospect_view.setVisibility(View.GONE);//Hide the prospect veiw fro dispaly the contact view

        if (crm_flag.equals("4")){
            ll_recruit_details.setVisibility(View.VISIBLE);
        }else{
            ll_recruit_details.setVisibility(View.GONE);
        }

        tv_fname.setText(contactList.get(position).getContact_fname());
        //tv_mname.setText(contactList.get(position).getContact_middle());
        tv_lname.setText(contactList.get(position).getContact_lname());
        tv_email.setText(contactList.get(position).getContact_email());
        tv_email1.setText(contactList.get(position).getContact_work_email());
        tv_email2.setText(contactList.get(position).getContact_other_email());
        tv_phone.setText(contactList.get(position).getContact_phone());
        tv_phone1.setText(contactList.get(position).getContact_work_phone());
        tv_phone2.setText(contactList.get(position).getContact_other_phone());
        tv_dob.setText(contactList.get(position).getContact_date_of_birth());
        tv_doa.setText(contactList.get(position).getContact_date_of_anniversary());

        tv_contact_for.setText(""+contactList.get(position).getContact_lead_prospecting_for());
        tv_contact_status.setText(""+contactList.get(position).getContact_lead_status());
        tv_contact_source.setText(""+contactList.get(position).getContact_lead_source());

        tv_ren_date.setText(contactList.get(position).getContact_customer_contract_renewal_date());
        tv_industry.setText(contactList.get(position).getContact_industry());
        tv_poli_number.setText(contactList.get(position).getContact_customer_policy_number());
        tv_annual_income.setText(contactList.get(position).getContact_annual_revenue());
        tv_current_policy.setText(contactList.get(position).getContact_customer_current_policy());
        tv_poli_cmny.setText(contactList.get(position).getContact_customer_policy_comp());
        tv_cur_poli_amnt.setText(contactList.get(position).getContact_customer_policy_amt());

        tv_compny.setText(contactList.get(position).getContact_company_name());
        tv_tag.setText(contactList.get(position).getContact_category());
        tv_skype.setText(contactList.get(position).getContact_skype_id());
        tv_twit.setText(contactList.get(position).getContact_twitter_name());
        tv_fb.setText(contactList.get(position).getContact_facebookurl());
        tv_linkin.setText(contactList.get(position).getContact_linkedinurl());
        tv_address.setText(contactList.get(position).getContact_address());
        tv_city.setText(contactList.get(position).getContact_city());
        tv_zip.setText(contactList.get(position).getContact_zip());
        tv_state.setText(contactList.get(position).getContact_state());
        tv_country.setText(contactList.get(position).getContact_country());
        tv_description.setText(contactList.get(position).getContact_description());

        tv_nlg_agent_id.setText(contactList.get(position).getContact_recruitsNLGAgentID());
        tv_pfa_agent_id.setText(contactList.get(position).getContact_recruitsPFAAgentID());
        tv_joining_date.setText(contactList.get(position).getContact_recruitsJoinDate());

        dialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendDataToUpdateComman(position);//Call comman function bcz need to call it from contact list
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void getCRMDetails(final TextView tv_assigned_camp, final TextView tv_assigned_groups, final TextView tv_assigned_text_camp, int position) {

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

                        if (assigned_email_campaigns.size()>0){
                            setListString(assigned_email_campaigns,tv_assigned_camp);
                        }

                        if (assigned_groups.size()>0){
                            setListString(assigned_groups,tv_assigned_groups);
                        }

                        if (assigned_text_campaigns.size()>0){
                            setListString(assigned_text_campaigns,tv_assigned_text_camp);
                        }


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

    private void setListString(List<String> assigned_email_campaigns, TextView tv_assigned_camp) {

        String assigned_campaigns="";
        for (int i=0;i<assigned_email_campaigns.size();i++){
            assigned_campaigns=assigned_campaigns+" "+assigned_email_campaigns.get(i)+",";
        }

        if (assigned_campaigns.endsWith(",")) {
            assigned_campaigns = assigned_campaigns.substring(0, assigned_campaigns.length() - 1);
        }
        Log.d(Global.TAG, "assigned_campaigns: "+assigned_campaigns);
        tv_assigned_camp.setText(assigned_campaigns);
    }

    /*private void sendDataToUpdateComman(int position) {
        Toast.makeText(context, "Edit Contact", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, AddContactActivity.class);
        Bundle bundle=new Bundle();
        bundle.putBoolean("ContactEditable",true);
        bundle.putString("crm_flag",crm_flag);
        bundle.putString("contact_id",contactList.get(position).getContact_id());
        bundle.putString("contact_title","Edit Contact Details");
        bundle.putString("c_fname",contactList.get(position).getContact_fname());
        bundle.putString("c_mname",contactList.get(position).getContact_middle());
        bundle.putString("c_lname",contactList.get(position).getContact_lname());
        bundle.putString("c_email",contactList.get(position).getContact_email());
        bundle.putString("c_email1",contactList.get(position).getContact_work_email());
        bundle.putString("c_email2",contactList.get(position).getContact_other_email());
        bundle.putString("phone",contactList.get(position).getContact_phone());
        bundle.putString("phone1",contactList.get(position).getContact_work_phone());
        bundle.putString("phone2",contactList.get(position).getContact_other_phone());
        bundle.putString("date_of_birth",contactList.get(position).getContact_date_of_birth());
        bundle.putString("date_of_anni",contactList.get(position).getContact_date_of_anniversary());

        bundle.putString("cust_policy_no",contactList.get(position).getContact_customer_policy_number());
        bundle.putString("contract_renewal_date",contactList.get(position).getContact_customer_contract_renewal_date());
        bundle.putString("contact_for",contactList.get(position).getContact_lead_prospecting_for());
        bundle.putString("contact_status",contactList.get(position).getContact_lead_status_id());
        bundle.putString("contact_source",contactList.get(position).getContact_lead_source_id());
        bundle.putString("current_policy",contactList.get(position).getContact_customer_current_policy());
        bundle.putString("policy_company",contactList.get(position).getContact_customer_policy_comp());
        bundle.putString("industry",contactList.get(position).getContact_industry());
        bundle.putString("current_policy_amt",contactList.get(position).getContact_customer_policy_amt());
        bundle.putString("annual_income",contactList.get(position).getContact_annual_revenue());

        bundle.putString("company_name",contactList.get(position).getContact_customer_policy_comp());
        bundle.putString("skype_id",contactList.get(position).getContact_skype_id());
        bundle.putString("twitter_name",contactList.get(position).getContact_twitter_name());
        bundle.putString("facebookurl",contactList.get(position).getContact_facebookurl());
        bundle.putString("linkedinurl",contactList.get(position).getContact_linkedinurl());
        bundle.putString("contact_address",contactList.get(position).getContact_address());
        bundle.putString("tag",contactList.get(position).getContact_category());
        bundle.putString("contact_city",contactList.get(position).getContact_city());
        bundle.putString("contact_state",contactList.get(position).getContact_state());
        bundle.putString("contact_zip",contactList.get(position).getContact_zip());
        bundle.putString("contact_country",contactList.get(position).getContact_country());
        bundle.putString("contact_description",contactList.get(position).getContact_description());
        bundle.putString("contact_id",contactList.get(position).getContact_id());

        if (crm_flag.equals("4")){
            bundle.putString("recruit_nlg_id",contactList.get(position).getContact_recruitsNLGAgentID());
            bundle.putString("recruit_pfa_id",contactList.get(position).getContact_recruitsPFAAgentID());
            bundle.putString("recruit_joining_date",contactList.get(position).getContact_recruitsJoinDate());
        }

        Log.d(Global.TAG, "edit contact_for"+contactList.get(position).getContact_lead_prospecting_for());
        Log.d(Global.TAG, "edit Tag"+contactList.get(position).getContact_category());
       // Log.d(Global.TAG,"edit contact_source"+contactList.get(position).getContact_lead_source_id());
        intent.putExtras(bundle);
        myContactFragment.startActivityForResult(intent,10);
    }
*/
    private void ViewAssignCampaigns(final int position, final TextView tv_assigned_camp) {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("contactId",contactList.get(position).getContact_id());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAssignedUnassignedCampaigns: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAssignedUnassignedCampaigns: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<AssignUnAssignCampaign> call=service.getAssignUnAssignCampaign(paramObj.toString());
        call.enqueue(new Callback<AssignUnAssignCampaign>() {
            @Override
            public void onResponse(Call<AssignUnAssignCampaign> call, Response<AssignUnAssignCampaign> response) {
                AssignUnAssignCampaign assignUnAssignCampaign=response.body();
                if (assignUnAssignCampaign.isSuccess()){
                    AssignUnAssign assignUnAssign=assignUnAssignCampaign.getResult();
                    campaignassignCampaignList=assignUnAssign.getAssingCamp();
                    List<UnAssignCampaign> unAssignCampaignList=assignUnAssign.getUnAssingCamp();

                    Log.d(Global.TAG, "assignCampaignList: "+campaignassignCampaignList.size());
                    Log.d(Global.TAG, "unAssignCampaignList: "+unAssignCampaignList.size());
                    String assigned_campaigns="";

                    for (int i=0;i<unAssignCampaignList.size();i++){
                        assigned_campaigns=assigned_campaigns+" "+unAssignCampaignList.get(i).getCampaignTitle()+",";
                    }
                    Log.d(Global.TAG, "assigned_campaigns: "+assigned_campaigns);
                    tv_assigned_camp.setText(assigned_campaigns);

                }else{
                    Toast.makeText(context, ""+assignUnAssignCampaign.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<AssignUnAssignCampaign> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:AssignUnAssignCampaign "+t);

            }
        });

    }


}
