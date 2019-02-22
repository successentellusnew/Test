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
 * Created by user on 11/15/2018.
 */

public class GroupCampaignAdapter extends RecyclerView.Adapter<AddGroupHolder>{
    LayoutInflater inflater;
    Context context;
    List<Contact> contactList;
    String user_id;
    AddGroupActivity addGroupActivity=new AddGroupActivity();
    boolean editFlag;

    public GroupCampaignAdapter(AddGroupActivity addGroupActivity, List<Contact> contactList, String user_id, boolean editFlag) {
        this.contactList=contactList;
        context=addGroupActivity;
        this.user_id=user_id;
        this.editFlag=editFlag;

    }

    @Override
    public AddGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout=inflater.inflate(R.layout.add_group_row,parent,false);
        AddGroupHolder addGroupHolder=new AddGroupHolder(context,layout,contactList);
        return addGroupHolder;
    }

    @Override
    public void onBindViewHolder(AddGroupHolder holder, final int position) {
        holder.tv_group_contat_name.setText(contactList.get(position).getContact_fname()+" "+contactList.get(position).getContact_lname());

        holder.group_contact_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    if (!editFlag){
                        if (!addGroupActivity.selected_group_contact.contains(contactList.get(position).getContact_id())){
                            addGroupActivity.selected_group_contact.add(contactList.get(position).getContact_id());
                            Log.d(Global.TAG, "onCheckedChanged: checked: "+addGroupActivity.selected_group_contact);
                        }
                    }else{
                        if (!addGroupActivity.selected_edit_group_contact.contains(contactList.get(position).getContact_id())){
                            addGroupActivity.selected_edit_group_contact.add(contactList.get(position).getContact_id());
                            Log.d(Global.TAG, "onCheckedChanged: checked: "+addGroupActivity.selected_edit_group_contact);
                        }
                    }


                }else{
                    if (!editFlag){
                        if (addGroupActivity.selected_group_contact.contains(contactList.get(position).getContact_id())){
                            addGroupActivity.selected_group_contact.remove(contactList.get(position).getContact_id());
                            Log.d(Global.TAG, "onCheckedChanged: unchecked: "+addGroupActivity.selected_group_contact);
                        }
                    }else{
                        if (!addGroupActivity.selected_edit_group_contact.contains(contactList.get(position).getContact_id())){
                            addGroupActivity.selected_edit_group_contact.remove(contactList.get(position).getContact_id());
                            Log.d(Global.TAG, "onCheckedChanged: checked: "+addGroupActivity.selected_edit_group_contact);
                        }
                    }
                }
            }
        });

       /* if (editFlag){
            if (addGroupActivity.selected_group_contact.contains(contactList.get(position))){
                holder.group_contact_check.setChecked(true);
            }
        }*/

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
