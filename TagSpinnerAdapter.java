package com.success.successEntellus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.model.Contact_Tag;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 4/11/2018.
 */

public class TagSpinnerAdapter extends ArrayAdapter {
    Context context;
    List<Contact_Tag> tagList;
    LayoutInflater inflater;
    String user_id;
    RefreashTags refreashTags;
String tag_name;
    public interface RefreashTags{
        public void refreashTagList();
    }
    public TagSpinnerAdapter(@NonNull Context context, List<Contact_Tag> tagList,String user_id, RefreashTags refreashTags,String tag_name) {
        super(context, 0);
        this.context=context;
        this.tagList=tagList;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.user_id=user_id;
        this.refreashTags=refreashTags;
        this.tag_name=tag_name;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView=inflater.inflate(R.layout.tag_spinner_row,parent,false);
        ImageView iv_spinner_tag=convertView.findViewById(R.id.iv_spinner_tag);
        TextView tv_spinner_tag=convertView.findViewById(R.id.tv_spinner_tag);
        ImageButton ib_remove_tag=convertView.findViewById(R.id.ib_remove_tag);
        ImageButton ib_edit_tag=convertView.findViewById(R.id.ib_edit_tag);
        final LinearLayout ll_edit_tag_view=convertView.findViewById(R.id.ll_edit_tag_view);
        final LinearLayout ll_tag_row=convertView.findViewById(R.id.ll_tag_row);
        Button btn_update_custom_tag=convertView.findViewById(R.id.btn_update_custom_tag);
        final EditText edt_update_tag=convertView.findViewById(R.id.edt_update_tag);

        if (tagList.get(position).getUserTagName().equals(tag_name)){
            ll_tag_row.setBackgroundColor(context.getResources().getColor(R.color.colorTag));
        }

        if (tagList.get(position).getUserTagUserId().equals("-1")){
            ib_remove_tag.setVisibility(View.GONE);
            ib_edit_tag.setVisibility(View.GONE);
        }else{
            ib_remove_tag.setVisibility(View.VISIBLE);
            ib_edit_tag.setVisibility(View.VISIBLE);
        }

        if (tagList.get(position).getUserTagName().equals("Red Apple")){
            iv_spinner_tag.setImageResource(R.drawable.red_apple);
        }else if (tagList.get(position).getUserTagName().equals("Green Apple")){
            iv_spinner_tag.setImageResource(R.drawable.green_apple);
        }else if (tagList.get(position).getUserTagName().equals("Brown Apple")){
            iv_spinner_tag.setImageResource(R.drawable.brown_apple);
        }else if (tagList.get(position).getUserTagName().equals("Rotten Apple")){
            iv_spinner_tag.setImageResource(R.drawable.rotten_apple);
        }else{
            iv_spinner_tag.setImageResource(R.mipmap.custom_tag);
        }

        tv_spinner_tag.setText(tagList.get(position).getUserTagName());

        ib_remove_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to remove this tag? If assign to other records will also get removed.")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeCustomTag(tagList.get(position).getUserTagId());

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        ib_edit_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ll_edit_tag_view.getVisibility()==View.VISIBLE){
                    ll_edit_tag_view.setVisibility(View.GONE);
                }else{
                    ll_edit_tag_view.setVisibility(View.VISIBLE);
                    edt_update_tag.setText(tagList.get(position).getUserTagName());
                }

            }
        });

        btn_update_custom_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_update_tag,"Enter New Tag to update"))
                updateCustomTag(tagList.get(position).getUserTagId(),edt_update_tag.getText().toString());
            }
        });


        return convertView;
    }

    private void updateCustomTag(String tag_id,String tag_name) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userTagId",tag_id);
            paramObj.put("contact_users_id", user_id);
            paramObj.put("tagName", tag_name);
            paramObj.put("contact_platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "updateCustomTag: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "updateCustomTag: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.updateCustomTag(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()){
                    JsonResult result=response.body();
                    if (result.isSuccess()){
                        Toast.makeText(context, ""+result.getResult(), Toast.LENGTH_SHORT).show();
                        if (refreashTags!=null){
                            refreashTags.refreashTagList();
                        }
                    }else{
                        Toast.makeText(context, ""+result.getResult(), Toast.LENGTH_SHORT).show();
                    }
                    myLoader.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: updateCustomTag :"+t);
                myLoader.dismiss();
            }
        });


    }

    private void removeCustomTag(String userTagId) {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userTagId",userTagId);
            paramObj.put("contact_users_id", user_id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "removeCustomTag: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "removeCustomTag: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.removeCustomTag(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()){
                    JsonResult result=response.body();
                    if (result.isSuccess()){
                        Toast.makeText(context, ""+result.getResult(), Toast.LENGTH_SHORT).show();
                        if (refreashTags!=null){
                            refreashTags.refreashTagList();
                        }

                    }else{
                        Toast.makeText(context, ""+result.getResult(), Toast.LENGTH_SHORT).show();
                    }
                    myLoader.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: removeCustomTag :"+t);
                myLoader.dismiss();
            }
        });



    }

    @Override
    public int getCount() {
        return tagList.size();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position,convertView,parent);
    }
}
