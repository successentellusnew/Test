package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.CFTLocatorActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.GetCFTLocations;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.UserLocation;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.UserInfoHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 8/8/2018.
 */

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoHolder>{
    CFTLocatorActivity context;
    List<UserLocation> userList;
    LayoutInflater inflater;
    SPLib spLib;
    View layout;
    RefreshUserStatus refreshUserStatus;
    public UserInfoAdapter(CFTLocatorActivity cftLocatorActivity, List<UserLocation> userList,RefreshUserStatus refreshUserStatus) {
        this.context=cftLocatorActivity;
        this.userList=userList;
        this.refreshUserStatus=refreshUserStatus;
    }

    public interface RefreshUserStatus{
        public void refreshusercftList();
        public void changeSelectAll();
    }
    @Override
    public UserInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        spLib=new SPLib(context);
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.user_info_row,parent,false);
        UserInfoHolder userInfoHolder=new UserInfoHolder(layout);
        return userInfoHolder;
    }

    @Override
    public void onBindViewHolder(final UserInfoHolder holder, final int position) {
        //Log.d(Global.TAG, "onBindViewHolder: user id:"+userList.get(position).getUserId());
        if (!userList.get(position).getProfile_pic().equals("")){
            Picasso.with(context)
                    .load(userList.get(position).getProfile_pic())
                    .placeholder(R.drawable.place)   // optional
                    .error(R.mipmap.profile)      // optional
                    .resize(400, 400)
                    .into(holder.iv_cft_profile);

        }

        holder.tv_cft_name.setText(userList.get(position).getFirst_name()+" "+userList.get(position).getLast_name());

        if (userList.get(position).getShowStatus()==1){
            holder.sw_user_location_status.setOnCheckedChangeListener(null);
            holder.sw_user_location_status.setChecked(true);
        }else{
            holder.sw_user_location_status.setOnCheckedChangeListener(null);
            holder.sw_user_location_status.setChecked(false);
        }

        holder.sw_user_location_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    showHiddenCftList(position,holder);
                }else{
                    hiddenCftList(position,holder);
                }
            }
        });
    }

    private void hiddenCftList(int position, final UserInfoHolder holder) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("cftListUser", userList.get(position).getUserId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "hiddenCftList: " + paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.hiddenCftList(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    holder.sw_user_location_status.setChecked(false);
                    Log.d(Global.TAG, "Status Hidden: ");
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    refreshUserStatus.refreshusercftList();
                    refreshUserStatus.changeSelectAll();
                }else{
                    Log.d(Global.TAG, " "+jsonResult.getResult());
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:hiddenCftList "+t);
            }
        });
    }

    private void showHiddenCftList(int position, final UserInfoHolder holder) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("cftShowListUser", userList.get(position).getUserId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "showHiddenCftList: " + paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.showHiddenLocation(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                  holder.sw_user_location_status.setChecked(true);
                  Log.d(Global.TAG, "Status Shown: ");
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    refreshUserStatus.refreshusercftList();
                    refreshUserStatus.changeSelectAll();
                }else{
                    Log.d(Global.TAG, " "+jsonResult.getResult());
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:showHiddenCftList "+t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
