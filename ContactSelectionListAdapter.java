package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.ContactSelectionActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.Contact;
import com.success.successEntellus.viewholder.ContactSelectionHolder;

import java.util.List;

/**
 * Created by user on 6/15/2018.
 */

public class ContactSelectionListAdapter extends RecyclerView.Adapter<ContactSelectionHolder> {
    ContactSelectionActivity context;
    List<Contact> contact_list;
    String user_id;
    boolean select_all;
    LayoutInflater inflater;
    View layout;
    ContactSelectionActivity contactSelectionActivity = new ContactSelectionActivity();

    public ContactSelectionListAdapter(ContactSelectionActivity context, List<Contact> contact_list, String user_id, boolean select_all) {
        this.context = context;
        this.contact_list = contact_list;
        this.select_all = select_all;
        this.user_id = user_id;
    }

    @Override
    public ContactSelectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.contact_selection_row, parent, false);
        ContactSelectionHolder selectionHolder = new ContactSelectionHolder(layout);

        return selectionHolder;
    }

    @Override
    public void onBindViewHolder(ContactSelectionHolder holder, final int position) {


        holder.tv_cname.setText(contact_list.get(position).getContact_fname() + " " + contact_list.get(position).getContact_lname());
        if (contact_list.get(position).getContact_phone() != null) {
            holder.tv_cphone.setText(contact_list.get(position).getContact_phone());
        }
        if (contact_list.get(position).getContact_email() != null) {
            holder.tv_cemail.setText(contact_list.get(position).getContact_email());
        }

        Log.d(Global.TAG, "onBindViewHolder:contact_list " + contact_list.size());
        if (select_all) {
            holder.ch_ccheck.setChecked(true);

           /* if (!ContactSelectionActivity.selected_contact_list.contains(contact_list.get(position))) {
                ContactSelectionActivity.selected_contact_list.add(contact_list.get(position));
            } else {
                Log.d(Global.TAG, "onBindViewHolder: Contact exists..!");
            }
*/
          /*  for (int i = 0; i < contact_list.size(); i++) {
                if (!ContactSelectionActivity.selected_contact_list.contains(contact_list.get(i))) {
                    ContactSelectionActivity.selected_contact_list.add(contact_list.get(i));
                } else {
                    Log.d(Global.TAG, "onBindViewHolder: Contact exists..!");
                }

            }*/
            // ContactSelectionActivity.selected_contact_list.add(contact_list.get(position));
            Log.d(Global.TAG, " Select All selected Contacts import: " + ContactSelectionActivity.selected_contact_list.size());
        } else {
            holder.ch_ccheck.setChecked(false);
            /*if (ContactSelectionActivity.selected_contact_list.contains(contact_list.get(position))) {
                ContactSelectionActivity.selected_contact_list.remove(contact_list.get(position));
            } else {
                Log.d(Global.TAG, "onBindViewHolder: Contact Not exists..!");
            }*/
           /* for (int i = 0; i < contact_list.size(); i++) {
                if (ContactSelectionActivity.selected_contact_list.contains(contact_list.get(i))) {
                    ContactSelectionActivity.selected_contact_list.remove(contact_list.get(i));
                } else {
                    Log.d(Global.TAG, "onBindViewHolder: Contact Not exists..!");
                }

            }*/
            //ContactSelectionActivity.selected_contact_list.remove(contact_list.get(position));
            Log.d(Global.TAG, "selected uncelect All Contacts import: " + ContactSelectionActivity.selected_contact_list.size());
        }

        holder.ch_ccheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    if(!ContactSelectionActivity.selected_contact_list.contains(contact_list.get(position))){
                        ContactSelectionActivity.selected_contact_list.add(contact_list.get(position));
                        Log.d(Global.TAG, "selected Contacts import: " + ContactSelectionActivity.selected_contact_list.size());
                    }
                } else {
                    if(ContactSelectionActivity.selected_contact_list.contains(contact_list.get(position))){
                        ContactSelectionActivity.selected_contact_list.remove(contact_list.get(position));
                        Log.d(Global.TAG, "selected Contacts import: " + ContactSelectionActivity.selected_contact_list.size());
                    }

                }
            }
        });

        if(ContactSelectionActivity.selected_contact_list.contains(contact_list.get(position))){
            holder.ch_ccheck.setChecked(true);
        }else{
            holder.ch_ccheck.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return contact_list.size();
    }
}

