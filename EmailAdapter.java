package com.success.successEntellus.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.success.successEntellus.R;
import com.success.successEntellus.fragment.CampaignStepsFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.Emails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by user on 5/18/2018.
 */

public class EmailAdapter extends ArrayAdapter{
    List<Emails> emailsList;
    Context context;
    LayoutInflater inflater;
    String schdule_date;
    CampaignStepsFragment campaignStepsFragment=new CampaignStepsFragment();
    public EmailAdapter(@NonNull Context context, List<Emails> emailsList,String scedule_date) {
        super(context,0);
        this.context=context;
        this.emailsList=emailsList;
        this.schdule_date=scedule_date;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.email_details_row,parent,false);

        CheckBox ch_checkEmail=(CheckBox)convertView.findViewById(R.id.ch_checkEmail);
        TextView tv_email_name=(TextView) convertView.findViewById(R.id.tv_email_name);
        TextView tv_eemail=(TextView) convertView.findViewById(R.id.tv_eemail);
       // TextView tv_ephone=(TextView) convertView.findViewById(R.id.tv_ephone);
        TextView tv_schedule_date=(TextView) convertView.findViewById(R.id.tv_schedule_date);
        TextView tv_email_status=(TextView) convertView.findViewById(R.id.tv_email_status);
        TextView tv_email_readunread=(TextView) convertView.findViewById(R.id.tv_email_readunread);
        final LinearLayout ll_expand_email_details=(LinearLayout) convertView.findViewById(R.id.ll_expand_email_details);
        final ImageButton ib_expand_details=(ImageButton) convertView.findViewById(R.id.ib_expand_details);
        ll_expand_email_details.setVisibility(View.GONE);

        tv_email_name.setText(emailsList.get(position).getContactName());
        tv_eemail.setText(emailsList.get(position).getContactEmail());
        tv_email_status.setText(emailsList.get(position).getSent());
        tv_email_readunread.setText(emailsList.get(position).getReadImg());
        tv_schedule_date.setText(emailsList.get(position).getScheduleDate());


        Log.d(Global.TAG, " Email: "+emailsList.get(position).getContactEmail());
        Log.d(Global.TAG, " Send Status: "+emailsList.get(position).getSent());
        Log.d(Global.TAG, " Read Status: "+emailsList.get(position).getReadImg());



     /*  if (emailsList.get(position).getReadImg().equals("Not Read")){
            tv_email_readunread.setText(emailsList.get(position).getReadImg());
        }else{
            tv_email_readunread.setText("Read");
        }

        if (emailsList.get(position).getSent().equals("Not Sent")){
            tv_schedule_date.setText(schdule_date);
            Log.d(Global.TAG, "Not Send: "+schdule_date);
        }else{
            tv_schedule_date.setText(emailsList.get(position).getScheduleDate());
        }
*/
        if (ll_expand_email_details.getVisibility()==View.VISIBLE){
            ib_expand_details.setBackground(context.getResources().getDrawable(R.mipmap.arrow_up1));
        }else{
            ib_expand_details.setBackground(context.getResources().getDrawable(R.mipmap.arrow_down1));
        }

        ib_expand_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_expand_email_details.getVisibility()==View.VISIBLE){
                    ll_expand_email_details.setVisibility(View.GONE);
                    ib_expand_details.setBackground(context.getResources().getDrawable(R.mipmap.arrow_down1));
                }else if (ll_expand_email_details.getVisibility()==View.GONE){
                    ll_expand_email_details.setVisibility(View.VISIBLE);
                    ib_expand_details.setBackground(context.getResources().getDrawable(R.mipmap.arrow_up1));
                }

            }
        });

        ch_checkEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                JSONObject emailObj = new JSONObject();
                try {
                    emailObj.put("email",emailsList.get(position).getContactEmail());
                    emailObj.put("contactID", emailsList.get(position).getContactCampaignId());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Log.d(Global.TAG, "getAllCampaign: "+e);
                    e.printStackTrace();
                }

                boolean addFlag=true;
                for (int i=0;i< campaignStepsFragment.emailDetails.length();i++){
                    try {
                        JSONObject emailObj1 =campaignStepsFragment.emailDetails.getJSONObject(i);

                        if (emailObj.get("contactID").equals(emailObj1.get("contactID"))){
                            addFlag=false;
                            Log.d(Global.TAG, "addFlag false: ");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (checked){
                    if (addFlag){
                        campaignStepsFragment.emailDetails.put(emailObj);
                        Log.d(Global.TAG, "emailDetails: "+campaignStepsFragment.emailDetails);
                    }

                }else{
                    campaignStepsFragment.emailDetails.remove(position);
                    Log.d(Global.TAG, "emailDetails: "+campaignStepsFragment.emailDetails);
                }
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return emailsList.size();
    }
}
