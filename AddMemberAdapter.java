package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.Contact;
import com.success.successEntellus.viewholder.AddMemberHolder;

import java.util.List;

/**
 * Created by user on 7/3/2018.
 */

public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberHolder> {
    FragmentActivity context;
    List<Contact> contactList;
    LayoutInflater inflater;
    TextCampaignsAdapter textCampaignsAdapter;
    View layout;
    String crm_flag;
   public  interface NotifyAddRemoveMember{
        void addMember(String contact_id);
        void removeMember(String contact_id);
    }
    private NotifyAddRemoveMember notifyAddRemoveMember;

    public AddMemberAdapter(FragmentActivity context, List<Contact> contact_list, String crm_flag,NotifyAddRemoveMember notifyAddRemoveMember) {
        this.context=context;
        this.contactList=contact_list;
        this.crm_flag=crm_flag;
        this.notifyAddRemoveMember=notifyAddRemoveMember;
    }



    @Override
    public AddMemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        textCampaignsAdapter=new TextCampaignsAdapter(context);
        layout=inflater.inflate(R.layout.add_member_row,parent,false);
        AddMemberHolder addMemberHolder=new AddMemberHolder(layout);
        return addMemberHolder;
    }

    @Override
    public void onBindViewHolder(final AddMemberHolder holder, final int position) {
        holder.tv_member_contat_name.setText(contactList.get(position).getContact_fname()+" "+contactList.get(position).getContact_lname());

        holder.member_contact_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    if ( !textCampaignsAdapter.selected_all_contact_list.contains(contactList.get(position).getContact_id())){
                        //textCampaignsAdapter.selected_all_contact_list.add(contactList.get(position).getContact_id());
                       notifyAddRemoveMember.addMember(contactList.get(position).getContact_id());
                        Log.d(Global.TAG, "Add Member: "+textCampaignsAdapter.selected_all_contact_list);
                    }

                }else{
                    if ( textCampaignsAdapter.selected_all_contact_list.contains(contactList.get(position).getContact_id())){
                       // textCampaignsAdapter.selected_all_contact_list.remove(contactList.get(position).getContact_id());
                        notifyAddRemoveMember.removeMember(contactList.get(position).getContact_id());
                        Log.d(Global.TAG, "remove Member: "+textCampaignsAdapter.selected_all_contact_list);
                    }

                }
            }
        });


        if (crm_flag.equals("1")){
            Log.d(Global.TAG, ".selected_contact.size: "+textCampaignsAdapter.selected_contact_list.size());
            for(int i=0;i<textCampaignsAdapter.selected_contact_list.size();i++){
                if (textCampaignsAdapter.selected_contact_list.get(i).equals(contactList.get(position).getContact_id())){
                    holder.member_contact_check.setChecked(true);

                }
            }
        }else if (crm_flag.equals("2")){
            for(int i=0;i<textCampaignsAdapter.selected_customers_list.size();i++){
                if (textCampaignsAdapter.selected_customers_list.contains(contactList.get(position).getContact_id())){
                    holder.member_contact_check.setChecked(true);
                }
            }
        }else if (crm_flag.equals("3")){
            for(int i=0;i<textCampaignsAdapter.selected_prospect_list.size();i++){
                if (textCampaignsAdapter.selected_prospect_list.contains(contactList.get(position).getContact_id())){
                    holder.member_contact_check.setChecked(true);
                }
            }
        }else if (crm_flag.equals("4")){
            for(int i=0;i<textCampaignsAdapter.selected_recruits_list.size();i++){
                Log.d(Global.TAG, ".selected_recruit.size: "+textCampaignsAdapter.selected_recruits_list.size());
                if (textCampaignsAdapter.selected_recruits_list.contains(contactList.get(position).getContact_id())){
                    holder.member_contact_check.setChecked(true);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
