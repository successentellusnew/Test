package com.success.successEntellus.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.Mentor;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.MentorHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 6/21/2018.
 */

public class MentorListAdapter extends RecyclerView.Adapter<MentorHolder> {
    Activity context;
    List<Mentor> mentorList;
    LayoutInflater inflater;
    View layout;
    SPLib spLib;
    String moduleName;
    boolean approveAccess;
    List<String> accessModules;
    public interface NotifyRefreshMenterList{
        void refreshMenterList();
    }
    NotifyRefreshMenterList notifyRefreshMenterList;

    public MentorListAdapter(Activity giveAccessActivity, List<Mentor> mentorList,boolean approveAccess,NotifyRefreshMenterList notifyRefreshMenterList) {
        this.context=giveAccessActivity;
        this.mentorList=mentorList;
        spLib=new SPLib(context);
        this.approveAccess=approveAccess;
        this.notifyRefreshMenterList=notifyRefreshMenterList;
    }


    @Override
    public MentorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (approveAccess){
            layout=inflater.inflate(R.layout.cft_approve_access_row,parent,false);
        }else{
            layout=inflater.inflate(R.layout.cft_mentor_access_row,parent,false);
        }

        MentorHolder mentorHolder=new MentorHolder(layout);
        return mentorHolder;
    }

    @Override
    public void onBindViewHolder(final MentorHolder holder, final int position) {
        holder.tv_mentor_name.setText(mentorList.get(position).getUserNameReq());
        holder.mentor_access_date.setText(mentorList.get(position).getCftAccessDate());
        accessModules=mentorList.get(position).getAccessModule();

       if (!mentorList.get(position).getImgPath().equals("")){
           Picasso.with(context)
                   .load(mentorList.get(position).getImgPath())
                   .placeholder(R.drawable.place)   // optional
                   .error(R.mipmap.profile)      // optional
                   .resize(400, 400)
                   .into(holder.iv_mentor_profile);

       }


       if (!approveAccess) {
           holder.ch_access_cal.setEnabled(false);
           holder.ch_access_tracking.setEnabled(false);

           if (accessModules.contains("1") && accessModules.contains("2")){
               holder.ch_access_tracking.setChecked(true);
               holder.ch_access_cal.setChecked(true);
           }else if (accessModules.contains("1")){
               holder.ch_access_tracking.setChecked(true);
           }else if (accessModules.contains("2")){
               holder.ch_access_cal.setChecked(true);
           }


           holder.btn_remove_mentor_access.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   new AlertDialog.Builder(context)
                           .setMessage("Are you sure you want to Remove Access")
                           .setCancelable(false)
                           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   removeAccess(position);
                               }
                           })
                           .setNegativeButton("No", null)
                           .show();

               }
           });

           holder.ib_edit_access.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    holder.ch_access_cal.setEnabled(true);
                    holder.ch_access_tracking.setEnabled(true);
                    holder.ll_accept_reject.setVisibility(View.VISIBLE);
                    holder.ib_edit_access.setVisibility(View.GONE);
               }
           });

           holder.ib_no.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   holder.ll_accept_reject.setVisibility(View.GONE);
                   holder.ib_edit_access.setVisibility(View.VISIBLE);
                   holder.ch_access_cal.setEnabled(false);
                   holder.ch_access_tracking.setEnabled(false);
               }
           });
           holder.ib_yes.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if (!holder.ch_access_tracking.isChecked() && !holder.ch_access_cal.isChecked()){
                       Toast.makeText(context, "Please Select Access Module", Toast.LENGTH_LONG).show();
                   }else{
                       changeAccess(position,holder);
                       holder.ll_accept_reject.setVisibility(View.GONE);
                       holder.ib_edit_access.setVisibility(View.VISIBLE);
                       holder.ch_access_cal.setEnabled(false);
                       holder.ch_access_tracking.setEnabled(false);
                   }

               }
           });
       }
       if (approveAccess){

            accessModules=mentorList.get(position).getAccessModule();
           Log.d(Global.TAG, "onBindViewHolder:accessModules "+accessModules);
            if (accessModules.contains("1")){
                holder.iv_access_tracking.setVisibility(View.VISIBLE);
            }else{
                holder.iv_access_tracking.setVisibility(View.GONE);
            }

            if (accessModules.contains("2")){
                holder.iv_access_cal.setVisibility(View.VISIBLE);
            }else{
                holder.iv_access_cal.setVisibility(View.GONE);
            }


           holder.ib_yes.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   acceptAccessRequest(position);

               }
           });

           holder.ib_no.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   rejectAccessRequest(position);
               }
           });
       }
    }

    private void changeAccess(int position, MentorHolder holder) {

        if (holder.ch_access_cal.isChecked() && holder.ch_access_tracking.isChecked()){
            moduleName="1,2";
        }else if (holder.ch_access_cal.isChecked()){
            moduleName="2";
        }else if (holder.ch_access_tracking.isChecked()){
            moduleName="1";
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("accessId", mentorList.get(position).getAccessId());
            paramObj.put("moduleName", moduleName);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "changeAccess: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.change_access(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:changeAccess:"+t);
            }
        });
    }

    private void acceptAccessRequest(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("accessId", mentorList.get(position).getAccessId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "acceptAccessRequest: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.approve_access(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                   notifyRefreshMenterList.refreshMenterList();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:acceptAccessRequest "+t);
            }
        });
    }

    private void rejectAccessRequest(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("accessId", mentorList.get(position).getAccessId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "rejectAccessRequest: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.reject_request(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    notifyRefreshMenterList.refreshMenterList();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:rejectAccess "+t);
            }
        });
    }


    private void removeAccess(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("accessId", mentorList.get(position).getAccessId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "removeAccess: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.remove_access(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    notifyRefreshMenterList.refreshMenterList();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: removeAccess "+t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mentorList.size();
    }
}
