package com.success.successEntellus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.Scratch_NotificationList_Activity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.Scratch_Note;
import com.success.successEntellus.viewholder.ScratchNotificationHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 10/9/2018.
 */

public class ScratchNotificationAdapter extends RecyclerView.Adapter<ScratchNotificationHolder> {
    Scratch_NotificationList_Activity context;
    List<Scratch_Note> NoteList;
    LayoutInflater inflater;
    View layout;
    private String fdate,ftime;

    public ScratchNotificationAdapter(Scratch_NotificationList_Activity context, List<Scratch_Note> todaysNoteList) {
        this.context=context;
        this.NoteList=todaysNoteList;
    }

    @Override
    public ScratchNotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.scratch_notification_row,parent,false);
        ScratchNotificationHolder scratchNotificationHolder=new ScratchNotificationHolder(layout);
        return scratchNotificationHolder;
    }

    @Override
    public void onBindViewHolder(final ScratchNotificationHolder holder, final int position) {

        String reminder_date=NoteList.get(position).getScratchNoteCreatedDate();
        Log.d(Global.TAG, "Created Date: "+reminder_date);

        final String rtime = reminder_date.split("\\s")[1].split("\\.")[0];
        final String rdate = reminder_date.substring(0,reminder_date.indexOf(" "));
        try {
            fdate = convertDateTomdy(rdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ftime=calculateTime(rtime);
        Log.d(Global.TAG, "from_time:"+ftime);
        Log.d(Global.TAG, "from_date:"+fdate);

        holder.tv_noteCreated.setText("Created On "+fdate+" at "+ftime);
        holder.tv_noteContents.setText(Html.fromHtml(NoteList.get(position).getScratchNoteText()));
        setColortoNote(NoteList.get(position).getScratchNoteColor(),holder.cv_snotification);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogViewNote(position,holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return NoteList.size();
    }

    private String convertDateTomdy(String date) throws ParseException {
        //Date date1 = new Date();
        Log.d(Global.TAG, "convertDateTomdy Date-->" + date);
        //SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(date);
        SimpleDateFormat format = new SimpleDateFormat("MMM d yyyy");
        // String format = new SimpleDateFormat("MMM d, yyyy").format();
        //Date dateObj=format.parse(date);
        String final_date=format.format(date1);
        Log.d(Global.TAG, "convertDateTomdy mdy Date-->" + final_date);
        return final_date;
    }

    private String calculateTime(String time) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
            final Date dateObj = sdf.parse(time);
            System.out.println(dateObj);
            String final_time=new SimpleDateFormat("K:mm a").format(dateObj);
            System.out.println(final_time);
            return final_time;
        } catch (final ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void openDialogViewNote(int position, ScratchNotificationHolder holder) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.view_note_layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WebView tvv_note_contents=(WebView)dialog.findViewById(R.id.tvv_note_contents);
        TextView tvv_reminder_date=dialog.findViewById(R.id.tvv_reminder_date);
        TextView tvv_reminder_time=dialog.findViewById(R.id.tvv_reminder_time);
        TextView tvv_created_on=dialog.findViewById(R.id.tvv_created_on);
        TextView tv_repeat_type_view_note = dialog.findViewById(R.id.tv_repeat_type_view_note);
        TextView tv_repeat_on_weekdays = dialog.findViewById(R.id.tv_repeat_on_weekdays);
        TextView tv_ends_on_date = dialog.findViewById(R.id.tv_ends_on_date);
        Button btn_vnote_dismiss=dialog.findViewById(R.id.btn_vnote_dismiss);
        LinearLayout ll_view_note=dialog.findViewById(R.id.ll_view_note);
        LinearLayout ll_ends_on_daily = dialog.findViewById(R.id.ll_ends_on_daily);
        LinearLayout ll_repeat_on_weekly = dialog.findViewById(R.id.ll_repeat_on_weekly);

        tvv_note_contents.setBackgroundColor(Color.TRANSPARENT);
        tvv_note_contents.loadDataWithBaseURL(null, NoteList.get(position).getScratchNoteText(), "text/html", "utf-8", null);
        //tvv_note_contents.setText(Html.fromHtml(NoteList.get(position).getScratchNoteText()));
        tvv_created_on.setText("Created On: "+fdate+" at "+ftime);

        String reminder_date_time=NoteList.get(position).getScratchNoteReminderDate();
        Log.d(Global.TAG, "openDialogViewNote: "+reminder_date_time);

        final String rdate = reminder_date_time.substring(0, reminder_date_time.indexOf(" "));
        final String rtime = reminder_date_time.split("\\s")[1].split("\\.")[0];

        tvv_reminder_date.setText(rdate);
        tvv_reminder_time.setText(rtime);

     /*   String reminder_time = reminder_date_time.split("\\s")[1].split("\\.")[0];
        String reminder_date = reminder_date_time.substring(0,reminder_date_time.indexOf(" "));
        Log.d(Global.TAG, "from_time:"+reminder_time);
        Log.d(Global.TAG, "from_date:"+reminder_date);*/

        String repeat_type = NoteList.get(position).getScratchNoteReminderRepeat();
        Log.d(Global.TAG, "openDialogViewNote: repeat_type:" + repeat_type);
        if (repeat_type.equals("1")) {
            tv_repeat_type_view_note.setText("Daily");
            ll_ends_on_daily.setVisibility(View.VISIBLE);
            ll_repeat_on_weekly.setVisibility(View.GONE);
        } else if (repeat_type.equals("2")) {
            tv_repeat_type_view_note.setText("Weekly");
            ll_ends_on_daily.setVisibility(View.GONE);
            ll_repeat_on_weekly.setVisibility(View.VISIBLE);
        } else {
            tv_repeat_type_view_note.setText("Not Repeat");
            ll_ends_on_daily.setVisibility(View.GONE);
            ll_repeat_on_weekly.setVisibility(View.GONE);
        }

        if (!NoteList.get(position).getScratchNoteReminderDailyEndDate().equals("")){
            String ends_on_date=NoteList.get(position).getScratchNoteReminderDailyEndDate();
            String end_date=ends_on_date.substring(0,ends_on_date.indexOf(" "));
            tv_ends_on_date.setText(end_date);
        }

        if (!NoteList.get(position).getScratchNoteReminderWeeklyDays().equals("")){
            tv_repeat_on_weekdays.setText(NoteList.get(position).getScratchNoteReminderWeeklyDays());
        }


        String note_color=NoteList.get(position).getScratchNoteColor();
        setColortoNote1(note_color,ll_view_note);

        btn_vnote_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void setColortoNote(String note_color, CardView ll_note_row_layout) {
        if (note_color.equalsIgnoreCase("blue")){
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color1));

        }else if (note_color.equalsIgnoreCase("pink")){
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color2));
        }else if (note_color.equalsIgnoreCase("green")){
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color3));
        }else if (note_color.equalsIgnoreCase("orange")){
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color4));
        }else if (note_color.equalsIgnoreCase("white")){
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color5));
        }else if (note_color.equalsIgnoreCase("violet")){
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color6));
        }
    }

    private void setColortoNote1(String note_color, LinearLayout ll_note_row_layout) {
        if (note_color.equalsIgnoreCase("blue")){
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color1));

        }else if (note_color.equalsIgnoreCase("pink")){
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color2));
        }else if (note_color.equalsIgnoreCase("green")){
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color3));
        }else if (note_color.equalsIgnoreCase("orange")){
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color4));
        }else if (note_color.equalsIgnoreCase("white")){
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color5));
        }else if (note_color.equalsIgnoreCase("violet")){
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color6));
        }
    }
}
