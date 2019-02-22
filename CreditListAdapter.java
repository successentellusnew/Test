package com.success.successEntellus.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.success.successEntellus.R;
import com.success.successEntellus.model.Credits;
import com.success.successEntellus.viewholder.CreditHolder;

import java.util.List;

/**
 * Created by user on 12/19/2018.
 */

public class CreditListAdapter extends RecyclerView.Adapter<CreditHolder>{
    Activity context;
    List<Credits> creditList;
    LayoutInflater inflater;
    View layout;

    public CreditListAdapter(Activity activity, List<Credits> creditList) {
        this.context=activity;
        this.creditList=creditList;
    }

    @NonNull
    @Override
    public CreditHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.earn_credits_score_row,parent,false);
        CreditHolder creditHolder=new CreditHolder(layout);

        return creditHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CreditHolder holder, int position) {
        holder.tv_credit_name.setText(creditList.get(position).getName());
        holder.tv_credit_email.setText(creditList.get(position).getEmail());
        holder.tv_credit_score.setText("$"+creditList.get(position).getReferCreditAmt());
        holder.tv_credit_date.setText(creditList.get(position).getCretedDate());
    }

    @Override
    public int getItemCount() {
        return creditList.size();
    }
}
