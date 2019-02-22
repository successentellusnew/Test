package com.success.successEntellus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.CreateVisionBoardActivity;
import com.success.successEntellus.fragment.VisionBoardFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.Visions;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.VisionsHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 11/12/2018.
 */

public class VisionListAdapter extends RecyclerView.Adapter<VisionsHolder> {
    LayoutInflater inflater;
    View layout;
    FragmentActivity context;
    List<Visions> visionsList;
    String user_id;
    NotifyVisions notifyVisions;
    VisionBoardFragment visionBoardFragment;

    public interface NotifyVisions{
        public void refreshVisions();
    }

    public VisionListAdapter(FragmentActivity activity, List<Visions> visionsList, String user_id, NotifyVisions notifyVisions,VisionBoardFragment visionBoardFragment) {
        this.context=activity;
        this.visionsList=visionsList;
        this.user_id=user_id;
        this.notifyVisions=notifyVisions;
        this.visionBoardFragment=visionBoardFragment;
    }

    @Override
    public VisionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.vision_board_row,parent,false);
        VisionsHolder holder=new VisionsHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(final VisionsHolder holder, final int position) {
        holder.tv_vision_title.setText(visionsList.get(position).getVboardTitle());
        holder.tv_vision_category.setText(visionsList.get(position).getVbCategory());
        holder.tv_vision_desc.setText(visionsList.get(position).getVboardDescription());
        holder.tv_vision_created.setText(visionsList.get(position).getVboardCreated());

        if (visionsList.get(position).getFilePath()!=null && visionsList.get(position).getVbAttachmentFile()!=null){
            if (!visionsList.get(position).getFilePath().equals("") && !visionsList.get(position).getVbAttachmentFile().equals("")){
                String image_path=visionsList.get(position).getFilePath()+user_id+"/"+visionsList.get(position).getVbAttachmentFile();
                Log.d(Global.TAG, "onBindViewHolder: image_path : "+image_path);
                Picasso.with(context)
                        .load(image_path)
                        .resize(400, 180)
                        .into( holder.iv_vision_image);
            }else{
                holder.iv_vision_image.setImageResource(R.drawable.no_image);
            }
        }else{
            holder.iv_vision_image.setImageResource(R.drawable.no_image);
        }


        holder.ib_delete_vision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strDelete="Are you sure, you want to delete this Vision?";
                new AlertDialog.Builder(context)
                        .setMessage(strDelete)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteVision(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        holder.ib_edit_vision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, CreateVisionBoardActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("vision_id",visionsList.get(position).getVboardId());
                bundle.putString("vision_title",visionsList.get(position).getVboardTitle());
                bundle.putString("vision_category",visionsList.get(position).getVbCategory());
                bundle.putString("vision_desc",visionsList.get(position).getVboardDescription());
                bundle.putString("vision_emotion",visionsList.get(position).getVboardEmotion());
                intent.putExtras(bundle);
                visionBoardFragment.startActivityForResult(intent,6008);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogViewVision(position);
            }
        });

    }

    private void openDialogViewVision(int position) {
        final Dialog dialog1 = new Dialog(context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setCancelable(false);
        dialog1.setContentView(R.layout.view_vision_board_layout);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog1.setTitle("Confirmation");

        Button btn_vvb_dissmiss=dialog1.findViewById(R.id.btn_vvb_dissmiss);
        TextView tv_vision_title=dialog1.findViewById(R.id.tv_vision_title);
        TextView tv_vision_category=dialog1.findViewById(R.id.tv_vision_category);
        TextView tv_vision_desc=dialog1.findViewById(R.id.tv_vision_desc);
        TextView tv_emotion=dialog1.findViewById(R.id.tv_emotion);
        ImageView iv_vision_image=dialog1.findViewById(R.id.iv_vision_image);

        btn_vvb_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        tv_vision_title.setText(visionsList.get(position).getVboardTitle());
        tv_vision_category.setText(visionsList.get(position).getVbCategory());
        tv_vision_desc.setText(visionsList.get(position).getVboardDescription());
        tv_emotion.setText(visionsList.get(position).getVboardEmotion());

        if (visionsList.get(position).getFilePath()!=null && visionsList.get(position).getVbAttachmentFile()!=null){
            if (!visionsList.get(position).getFilePath().equals("") && !visionsList.get(position).getVbAttachmentFile().equals("")){
                String image_path=visionsList.get(position).getFilePath()+user_id+"/"+visionsList.get(position).getVbAttachmentFile();
                Log.d(Global.TAG, "onBindViewHolder: image_path : "+image_path);
                Picasso.with(context)
                        .load(image_path)
                        .resize(300, 180)
                        .into( iv_vision_image);
            }else {
                iv_vision_image.setImageResource(R.drawable.no_image);
            }
        }else {
            iv_vision_image.setImageResource(R.drawable.no_image);
        }


        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void deleteVision(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("vboardId", visionsList.get(position).getVboardId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteVision: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteVision: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.deleteVisionBoard(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()){
                    JsonResult jsonResult=response.body();
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        notifyVisions.refreshVisions();
                    }else{
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:deleteVision "+t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return visionsList.size();
    }
}
