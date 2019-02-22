package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddGroupActivity;

import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.Contact;
import com.success.successEntellus.viewholder.AddGroupHolder;

import java.util.List;

/**
 * Created by user on 5/1/2018.
 */

public class AddGroupContactsAdapter extends RecyclerView.Adapter<AddGroupHolder> {
    LayoutInflater inflater;
    Context context;
    List<Contact> contactList;
    String user_id;
    String crm_flag;
    AddGroupActivity addGroupActivityNew=new AddGroupActivity();
    boolean editFlag;
    NotifyInAddGroup notifyInAddGroup;
    boolean select_all=false;

    public interface NotifyInAddGroup{
        public void addContactToGroup(String contact_id);
        public void removeContactFromGroup(String contact_id);
    }

    public AddGroupContactsAdapter(Context context, List<Contact> contactList, String user_id, String crm_flag, boolean editFlag, NotifyInAddGroup notifyInAddGroup,boolean select_all) {
        this.context=context;
        this.contactList=contactList;
        this.user_id=user_id;
        this.crm_flag=crm_flag;
        this.editFlag=editFlag;
        this.notifyInAddGroup=notifyInAddGroup;
        this.select_all=select_all;
        Log.d(Global.TAG, "AddGroupContactsAdapter: COnstructor ");

    }

    @Override
    public AddGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(Global.TAG, "onCreateViewHolder: ");
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout=inflater.inflate(R.layout.add_group_row,parent,false);
        AddGroupHolder addGroupHolder=new AddGroupHolder(context,layout,contactList);
        return addGroupHolder;
    }

    @Override
    public void onBindViewHolder(final AddGroupHolder holder, final int position) {

        if (editFlag){
            Log.d(Global.TAG, "onBindViewHolder: Selected edit group Contact "+AddGroupActivity.selected_edit_group_contact);
            Log.d(Global.TAG, "onBindViewHolder: Selected Contact "+AddGroupActivity.selected_contact);
            Log.d(Global.TAG, "onBindViewHolder: Selected Customer "+AddGroupActivity.selected_customers);
            Log.d(Global.TAG, "onBindViewHolder: Selected Prospects "+AddGroupActivity.selected_prospects);
            Log.d(Global.TAG, "onBindViewHolder: Selected Recruits "+AddGroupActivity.selected_recruits);
        }
        holder.tv_group_contat_name.setText(contactList.get(position).getContact_fname()+" "+contactList.get(position).getContact_lname());


        //holder.group_contact_check.setOnCheckedChangeListener(null);

        if (crm_flag.equals("1")){
            Log.d(Global.TAG, "addGroupActivity.selected_contact.size: "+AddGroupActivity.selected_contact.size());
            for(int i=0;i<AddGroupActivity.selected_contact.size();i++){
                if (AddGroupActivity.selected_contact.contains(contactList.get(position).getContact_id())){
                    holder.group_contact_check.setChecked(true);
                    Log.d(Global.TAG, "Contact Id:"+contactList.get(position).getContact_id());
                }else{
                    holder.group_contact_check.setChecked(false);
                }
            }

            AddGroupActivity.tv_selected_contacts.setText(AddGroupActivity.selected_contact.size()+" Contacts Selected.");
        }else if (crm_flag.equals("2")){
            Log.d(Global.TAG, "addGroupActivity.selected_customers.size: "+AddGroupActivity.selected_customers.size());
            for(int i=0;i<AddGroupActivity.selected_customers.size();i++){
                if (AddGroupActivity.selected_customers.contains(contactList.get(position).getContact_id())){
                    holder.group_contact_check.setChecked(true);
                }else{
                    holder.group_contact_check.setChecked(false);
                }
            }

            AddGroupActivity.tv_selected_contacts.setText(AddGroupActivity.selected_customers.size()+" Customers Selected.");
        }else if (crm_flag.equals("3")){
            Log.d(Global.TAG, "addGroupActivity.selected_prospects.size: "+AddGroupActivity.selected_prospects.size());
            for(int i=0;i<AddGroupActivity.selected_prospects.size();i++){
                if (AddGroupActivity.selected_prospects.contains(contactList.get(position).getContact_id())){
                    holder.group_contact_check.setChecked(true);
                }else{
                    holder.group_contact_check.setChecked(false);
                }
            }
            AddGroupActivity.tv_selected_contacts.setText(AddGroupActivity.selected_prospects.size()+" Prospects Selected.");
        }else if (crm_flag.equals("4")){
            Log.d(Global.TAG, "addGroupActivity.selected_recruits.size: "+AddGroupActivity.selected_recruits.size());
            for(int i=0;i<AddGroupActivity.selected_recruits.size();i++){
                if (AddGroupActivity.selected_recruits.contains(contactList.get(position).getContact_id())){
                    holder.group_contact_check.setChecked(true);
                }else{
                    holder.group_contact_check.setChecked(false);
                }
            }

            AddGroupActivity.tv_selected_contacts.setText(AddGroupActivity.selected_recruits.size()+" Recruits Selected.");
        }


        holder.group_contact_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
              Log.d(Global.TAG, "onCheckedChanged: EditFlag:"+editFlag);
              if (checked){

                  if (!editFlag){
                      if ( !AddGroupActivity.selected_group_contact.contains(contactList.get(position).getContact_id()) ){
                          AddGroupActivity.selected_group_contact.add(contactList.get(position).getContact_id());
                          notifyInAddGroup.addContactToGroup(contactList.get(position).getContact_id());
                          Log.d(Global.TAG, "checked: Selected Contacts "+AddGroupActivity.selected_group_contact);
                      }else{
                          Log.d(Global.TAG, "Already Added: ");
                      }
                  }else{
                      if ( !AddGroupActivity.selected_edit_group_contact.contains(contactList.get(position).getContact_id()) ){
                          AddGroupActivity.selected_edit_group_contact.add(contactList.get(position).getContact_id());
                          notifyInAddGroup.addContactToGroup(contactList.get(position).getContact_id());
                          Log.d(Global.TAG, "onCheckedChanged: Selected Contacts "+AddGroupActivity.selected_edit_group_contact);
                      }else{
                          Log.d(Global.TAG, "Already Added: ");
                      }
                  }

              }else{
                  Log.d(Global.TAG, "UnCheck: "+AddGroupActivity.selected_group_contact);
                  if (!editFlag){
                      if ( AddGroupActivity.selected_group_contact.contains(contactList.get(position).getContact_id()) ){
                          AddGroupActivity.selected_group_contact.remove(contactList.get(position).getContact_id());
                          notifyInAddGroup.removeContactFromGroup(contactList.get(position).getContact_id());
                          Log.d(Global.TAG, "onCheckedChanged: Selected Contacts "+AddGroupActivity.selected_group_contact);
                      }else{
                          Log.d(Global.TAG, "Already Added: ");
                      }
                  }else{
                      if ( AddGroupActivity.selected_edit_group_contact.contains(contactList.get(position).getContact_id()) ){
                          AddGroupActivity.selected_edit_group_contact.remove(contactList.get(position).getContact_id());
                          notifyInAddGroup.removeContactFromGroup(contactList.get(position).getContact_id());
                          Log.d(Global.TAG, "onCheckedChanged: Selected Contacts "+AddGroupActivity.selected_edit_group_contact);
                      }else{
                          Log.d(Global.TAG, "Already Added: ");
                      }
                  }

              }
          }
      });

       /* if (select_all){
            holder.group_contact_check.setChecked(true);
        }else{
            holder.group_contact_check.setChecked(false);
        }*/

      //if (editFlag){




    }
    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
