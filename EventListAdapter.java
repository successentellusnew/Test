package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.ShowEventsCalenderActivity;
import com.success.successEntellus.model.CalenderDetails;
import com.success.successEntellus.viewholder.EventHolder;

import java.util.List;

/**
 * Created by user on 6/21/2018.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventHolder> {
    LayoutInflater inflater;
    List<CalenderDetails> eventList;
    ShowEventsCalenderActivity context;
    View layout;

    public EventListAdapter(ShowEventsCalenderActivity showEventsCalenderActivity, List<CalenderDetails> eventList) {
        this.eventList=eventList;
        this.context=showEventsCalenderActivity;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.events_row,parent,false);
        EventHolder eventHolder=new EventHolder(layout);

        return eventHolder;
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        holder.tv_event_title.setText("Title: "+eventList.get(position).getTitle());
        holder.tv_event_start.setText("Start: "+eventList.get(position).getStart());
        holder.tv_event_end.setText("End: "+eventList.get(position).getEnd());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
