package com.success.successEntellus.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddCalenderEventActivity;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.Scratch_Note;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.ScratchNoteHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 9/11/2018.
 */

public class ScratchNoteAdapter extends RecyclerView.Adapter<ScratchNoteHolder> {
    FragmentActivity context;
    List<Scratch_Note> scratchNoteList;
    View layout;
    SPLib spLib;
    LayoutInflater inflater;
    Calendar cal;
    int day, month, year, hour, min;
    EditText eedt_note_contents, edt_reminder_date, edt_reminder_time;
    Button btn_edit_note_dissmiss;
    LinearLayout ll_edit_note;
    RefreshNotes refreshNotes;
    String color_string;
    ImageButton ib_change_color;
    private String format;
    String fdate = "", ftime = "";
    private String cdate, ctime;
    private Spinner sp_repeate_type_scratch_note_edit;
    LinearLayout ll_ends_on_snote_edit, ll_ends_after_snote_edit, ll_repeat_weekly_days_edit_note;
    CheckBox ch_monday_note_edit, ch_tuesday_note_edit, ch_wednesday_note_edit, ch_thursday_note_edit, ch_friday_note_edit, ch_saturday_note_edit, ch_sunday_note_edit;
    EditText edt_ends_after_edit_note, edt_ends_on_edit_note;
    List<String> repeat_type = new ArrayList<>();
    private String reminder_repeat;
    String scratchNoteReminderRepeat = "0";
    List<String> dayList = new ArrayList<>();
    private String scratchNoteReminderWeeklyDays = "";
    private String currDayName = "";

    public ScratchNoteAdapter(FragmentActivity context, List<Scratch_Note> scratchNoteList, RefreshNotes scratchNoteFragment) {
        this.context = context;
        this.scratchNoteList = scratchNoteList;
        spLib = new SPLib(context);
        this.refreshNotes = scratchNoteFragment;
    }

    public interface RefreshNotes {
        public void refreshAllNotes();
    }

    @Override
    public ScratchNoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.scatch_note_row, parent, false);
        ScratchNoteHolder scratchNoteHolder = new ScratchNoteHolder(layout);
        return scratchNoteHolder;
    }

    @Override
    public void onBindViewHolder(ScratchNoteHolder holder, final int position) {
        holder.tv_note_details.setText(Html.fromHtml(scratchNoteList.get(position).getScratchNoteText()));
        //holder.tv_note_details.loadDataWithBaseURL(null, scratchNoteList.get(position).getScratchNoteText(), "text/html", "utf-8", null);

        String reminder_date = scratchNoteList.get(position).getScratchNoteReminderDate();
        String created_date = scratchNoteList.get(position).getScratchNoteCreatedDate();
        Log.d(Global.TAG, "Reminder Date: " + reminder_date);

        final String rtime = reminder_date.split("\\s")[1].split("\\.")[0];
        final String rdate = reminder_date.substring(0, reminder_date.indexOf(" "));

        final String cctime = created_date.split("\\s")[1].split("\\.")[0];
        final String ccdate = created_date.substring(0, reminder_date.indexOf(" "));


        try {
            fdate = convertDateTomdy(rdate);
            cdate = convertDateTomdy(ccdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        ftime = calculateTime(rtime);
        ctime = calculateTime(cctime);
        Log.d(Global.TAG, "from_time:" + ftime);
        Log.d(Global.TAG, "from_date:" + fdate);

        holder.tv_reminder_date.setText(" " + fdate + " at " + ftime);
        holder.tv_created_on.setText("Created On: " + " " + cdate + " at " + ctime);

        // holder.tv_note_details.setMovementMethod(new ScrollingMovementMethod());
        String note_color = scratchNoteList.get(position).getScratchNoteColor();

        setColortoNote(note_color, holder.ll_note_row_layout, false);

        holder.ib_edit_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openDialogEditNote(position);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.ib_delete_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new android.support.v7.app.AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to Delete this scratch note..? ")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteNote(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogViewNote(position);
            }
        });

    }

    private void openDialogViewNote(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.view_note_layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //TextView tvv_note_contents = dialog.findViewById(R.id.tvv_note_contents);
        WebView tvv_note_contents = dialog.findViewById(R.id.tvv_note_contents);
        TextView tv_repeat_on_weekdays = dialog.findViewById(R.id.tv_repeat_on_weekdays);
        TextView tv_ends_on_date = dialog.findViewById(R.id.tv_ends_on_date);
        TextView tvv_reminder_date = dialog.findViewById(R.id.tvv_reminder_date);
        TextView tvv_reminder_time = dialog.findViewById(R.id.tvv_reminder_time);
        TextView tvv_created_on = dialog.findViewById(R.id.tvv_created_on);
        TextView tv_repeat_type_view_note = dialog.findViewById(R.id.tv_repeat_type_view_note);
        Button btn_vnote_dismiss = dialog.findViewById(R.id.btn_vnote_dismiss);
        LinearLayout ll_view_note = dialog.findViewById(R.id.ll_view_note);
        LinearLayout ll_ends_on_daily = dialog.findViewById(R.id.ll_ends_on_daily);
        LinearLayout ll_repeat_on_weekly = dialog.findViewById(R.id.ll_repeat_on_weekly);

        //tvv_note_contents.setText(Html.fromHtml(scratchNoteList.get(position).getScratchNoteText()));
        tvv_note_contents.setBackgroundColor(Color.TRANSPARENT);
        tvv_note_contents.loadDataWithBaseURL(null, scratchNoteList.get(position).getScratchNoteText(), "text/html", "utf-8", null);
        tvv_created_on.setText("Created On: " + fdate + " at " + ftime);

        String reminder_date_time = scratchNoteList.get(position).getScratchNoteReminderDate();
        Log.d(Global.TAG, "openDialogViewNote: " + reminder_date_time);

        final String rdate = reminder_date_time.substring(0, reminder_date_time.indexOf(" "));
        final String rtime = reminder_date_time.split("\\s")[1].split("\\.")[0];

        tvv_reminder_date.setText(rdate);
        tvv_reminder_time.setText(rtime);

     /*   String reminder_time = reminder_date_time.split("\\s")[1].split("\\.")[0];
        String reminder_date = reminder_date_time.substring(0,reminder_date_time.indexOf(" "));
        Log.d(Global.TAG, "from_time:"+reminder_time);
        Log.d(Global.TAG, "from_date:"+reminder_date);*/

        String repeat_type = scratchNoteList.get(position).getScratchNoteReminderRepeat();
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

        if (!scratchNoteList.get(position).getScratchNoteReminderDailyEndDate().equals("")){
            String ends_on_date=scratchNoteList.get(position).getScratchNoteReminderDailyEndDate();
            String end_date=ends_on_date.substring(0,ends_on_date.indexOf(" "));
            tv_ends_on_date.setText(end_date);
        }

        if (!scratchNoteList.get(position).getScratchNoteReminderWeeklyDays().equals("")){
            tv_repeat_on_weekdays.setText(scratchNoteList.get(position).getScratchNoteReminderWeeklyDays());
        }


        String note_color = scratchNoteList.get(position).getScratchNoteColor();
        setColortoNote1(note_color, ll_view_note, false);

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

    private void setColortoNote(String note_color, CardView ll_note_row_layout, boolean change_color) {
        if (note_color.equalsIgnoreCase("blue")) {
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color1));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color1));
            }

        } else if (note_color.equalsIgnoreCase("pink")) {
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color2));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color2));
            }
        } else if (note_color.equalsIgnoreCase("green")) {
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color3));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color3));
            }
        } else if (note_color.equalsIgnoreCase("orange")) {
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color4));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color4));
            }
        } else if (note_color.equalsIgnoreCase("white")) {
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color5));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color5));
            }
        } else if (note_color.equalsIgnoreCase("violet")) {
            ll_note_row_layout.setCardBackgroundColor(context.getResources().getColor(R.color.color6));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color6));
            }
        }
    }

    private void setColortoNote1(String note_color, LinearLayout ll_note_row_layout, boolean change_color) {
        if (note_color.equalsIgnoreCase("blue")) {
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color1));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color1));
            }

        } else if (note_color.equalsIgnoreCase("pink")) {
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color2));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color2));
            }
        } else if (note_color.equalsIgnoreCase("green")) {
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color3));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color3));
            }
        } else if (note_color.equalsIgnoreCase("orange")) {
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color4));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color4));
            }
        } else if (note_color.equalsIgnoreCase("white")) {
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color5));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color5));
            }
        } else if (note_color.equalsIgnoreCase("violet")) {
            ll_note_row_layout.setBackgroundColor(context.getResources().getColor(R.color.color6));
            if (change_color) {
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color6));
            }
        }
    }

    private void deleteNote(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("scratchNoteId", scratchNoteList.get(position).getScratchNoteId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "addScratchNote: " + paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.delete_note(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult != null) {
                    if (jsonResult.isSuccess()) {
                        Toast.makeText(context, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                        refreshNotes.refreshAllNotes();
                    } else {
                        Toast.makeText(context, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:deleteNote " + t);
            }
        });


    }

    private void openDialogEditNote(final int position) throws ParseException {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.edit_note_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        eedt_note_contents = dialog.findViewById(R.id.eedt_note_contents);
        edt_reminder_date = dialog.findViewById(R.id.eedt_reminder_date);
        edt_reminder_time = dialog.findViewById(R.id.eedt_reminder_time);
        edt_ends_after_edit_note = dialog.findViewById(R.id.edt_ends_after_edit_note);
        edt_ends_on_edit_note = dialog.findViewById(R.id.edt_ends_on_edit_note);
        ll_edit_note = dialog.findViewById(R.id.ll_edit_note);
        ll_ends_after_snote_edit = dialog.findViewById(R.id.ll_ends_after_snote_edit);
        ll_ends_on_snote_edit = dialog.findViewById(R.id.ll_ends_on_snote_edit);
        ll_repeat_weekly_days_edit_note = dialog.findViewById(R.id.ll_repeat_weekly_days_edit_note);
        btn_edit_note_dissmiss = dialog.findViewById(R.id.btn_edit_note_dissmiss);
        sp_repeate_type_scratch_note_edit = dialog.findViewById(R.id.sp_repeate_type_scratch_note_edit);

        ch_monday_note_edit = dialog.findViewById(R.id.ch_monday_note_edit);
        ch_tuesday_note_edit = dialog.findViewById(R.id.ch_tuesday_note_edit);
        ch_wednesday_note_edit = dialog.findViewById(R.id.ch_wednesday_note_edit);
        ch_thursday_note_edit = dialog.findViewById(R.id.ch_thursday_note_edit);
        ch_friday_note_edit = dialog.findViewById(R.id.ch_friday_note_edit);
        ch_saturday_note_edit = dialog.findViewById(R.id.ch_saturday_note_edit);
        ch_sunday_note_edit = dialog.findViewById(R.id.ch_sunday_note_edit);
        final TextView tv_select_days_error_edit_note = dialog.findViewById(R.id.tv_select_days_error_edit_note);

        ib_change_color = dialog.findViewById(R.id.ib_change_color);
        Button btn_edit_note = dialog.findViewById(R.id.btn_edit_note);

        repeat_type.clear();
        repeat_type.add("Daily");
        repeat_type.add("Weekly");
        applySpinner(repeat_type,sp_repeate_type_scratch_note_edit,"-Select-");

        String reminder_date_time = scratchNoteList.get(position).getScratchNoteReminderDate();
        Log.d(Global.TAG, "openDialogViewNote: " + reminder_date_time);

        final String reminder_time = reminder_date_time.split("\\s")[1].split("\\.")[0];
        final String reminder_date = reminder_date_time.substring(0, reminder_date_time.indexOf(" "));


       /* String final_date=convertDateTomdy(reminder_date);
        String final_time=calculateTime(reminder_time);
        Log.d(Global.TAG, "from_time:"+final_time);
        Log.d(Global.TAG, "from_date:"+final_date);*/

        edt_reminder_date.setText(reminder_date);
        edt_reminder_time.setText(reminder_time);

        String repeat_type=scratchNoteList.get(position).getScratchNoteReminderRepeat();
        String week_days=scratchNoteList.get(position).getScratchNoteReminderWeeklyDays();
        String daily_ends_on=scratchNoteList.get(position).getScratchNoteReminderDailyEndDate();
        String ends_after=scratchNoteList.get(position).getScratchNoteReminderWeeklyEnds();

        Log.d(Global.TAG, "openDialogEditNote: repeat_type "+repeat_type);
        Log.d(Global.TAG, "openDialogEditNote: week_days "+week_days);
        Log.d(Global.TAG, "openDialogEditNote: daily_ends_on "+daily_ends_on);
        Log.d(Global.TAG, "openDialogEditNote: ends_after "+ends_after);

        if (!daily_ends_on.equals("")){
            String ends_on_date=daily_ends_on.substring(0,daily_ends_on.indexOf(" "));
            edt_ends_on_edit_note.setText(ends_on_date);
        }

        if (!ends_after.equals("")){
            edt_ends_after_edit_note.setText(ends_after);
        }

        if (!week_days.equals("")){
            String[] week_days_array=week_days.split(",");
            Log.d(Global.TAG, "week_days: "+week_days_array.length);
            for(int i=0;i<week_days_array.length;i++){
                Log.d(Global.TAG, "week_days: "+week_days_array[i]);
                String day_name=week_days_array[i];
                //String name=day_name.substring(0,day_name.length()-1);
                selectWeekDays(day_name);
            }
        }

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        hour = cal.get(Calendar.HOUR);
        min = cal.get(Calendar.MINUTE);
        int am_pm = cal.get(Calendar.AM_PM);
        if (am_pm == 0) {
            format = "AM";
        } else {
            format = "PM";
        }
        //edt_reminder_time.setText(hour+":"+min+" "+format);
        Log.d(Global.TAG, "Current Date: " + (month + 1) + "-" + day + "-" + year);
        //edt_reminder_date.setText((month+1)+"-"+day+"-"+year);
        //edt_reminder_time.setText("11:30");

        eedt_note_contents.setText(Html.fromHtml(scratchNoteList.get(position).getScratchNoteText()));
        color_string = scratchNoteList.get(position).getScratchNoteColor();
        setColortoNote1(color_string, ll_edit_note, true);

        sp_repeate_type_scratch_note_edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_repeate_type_scratch_note_edit.getSelectedItem().toString().contains("Select")){
                    reminder_repeat="";
                    ll_ends_on_snote_edit.setVisibility(View.GONE);
                    ll_ends_after_snote_edit.setVisibility(View.GONE);
                    ll_repeat_weekly_days_edit_note.setVisibility(View.GONE);
                    scratchNoteReminderRepeat="0";
                }else{
                    reminder_repeat=sp_repeate_type_scratch_note_edit.getSelectedItem().toString().toLowerCase();
                    Log.d(Global.TAG, "onItemSelected:reminder_repeat "+reminder_repeat);

                    if (reminder_repeat.equalsIgnoreCase("daily")){
                        ll_ends_on_snote_edit.setVisibility(View.VISIBLE);
                        ll_ends_after_snote_edit.setVisibility(View.GONE);
                        ll_repeat_weekly_days_edit_note.setVisibility(View.GONE);
                        scratchNoteReminderRepeat="1";
                        edt_ends_after_edit_note.setText("");
                    }else if (reminder_repeat.equalsIgnoreCase("weekly")){
                        ll_ends_on_snote_edit.setVisibility(View.GONE);
                        ll_ends_after_snote_edit.setVisibility(View.VISIBLE);
                        ll_repeat_weekly_days_edit_note.setVisibility(View.VISIBLE);
                        scratchNoteReminderRepeat="2";
                        edt_ends_on_edit_note.setText("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ib_change_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogColorPicker(ib_change_color);
            }
        });

        edt_reminder_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                String[] time_aaray = reminder_time.split(":");
                int edit_hours = 0, edit_min = 0;
                if (time_aaray.length > 0) {
                    edit_hours = Integer.parseInt(time_aaray[0]);
                    edit_min = Integer.parseInt(time_aaray[1]);
                }

                mTimePicker = new TimePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String format = "";
                        if (selectedHour == 0) {
                            selectedHour += 12;
                            format = "AM";
                        } else if (selectedHour == 12) {
                            format = "PM";
                        } else if (selectedHour > 12) {
                            selectedHour -= 12;
                            format = "PM";
                        } else {
                            format = "AM";
                        }
                        edt_reminder_time.setText(selectedHour + ":" + selectedMinute + " " + format);
                        edt_reminder_time.setText(selectedHour + ":" + selectedMinute + " " + format);
                    }
                }, edit_hours, edit_min, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        edt_reminder_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] date_array = reminder_date.split("-");
                Log.d(Global.TAG, "date array length: " + date_array.length);
                int year = Integer.parseInt(date_array[0]);
                int month = Integer.parseInt(date_array[1]);
                int day = Integer.parseInt(date_array[2]);
                Log.d(Global.TAG, "date array : " + year + "-" + month + "-" + day);

               /* Calendar edit_cal=Calendar.getInstance();
                edit_cal.set(year,month-1,day);
                long millisec=edit_cal.getTimeInMillis();*/

                DatePickerDialog dpd = new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, reminder_picker, year, month - 1, day);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                // dpd.getDatePicker().setMinDate(millisec);
                dpd.show();
            }
        });

        edt_ends_on_edit_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT,ends_on,month,day,year);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dpd.show();
            }
        });



        btn_edit_note_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_edit_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(eedt_note_contents, "Enter Contents")) {

                if (sp_repeate_type_scratch_note_edit.getSelectedItem().toString().equalsIgnoreCase("daily")){
                    if (!edt_ends_on_edit_note.getText().toString().equals("")){
                        String reminder_date=edt_reminder_date.getText().toString();
                        String ends_on=edt_ends_on_edit_note.getText().toString();
                        boolean isDateValid=compareDates(reminder_date,ends_on);
                        if (isDateValid){
                            editNote(position, dialog);
                        }else{
                            Toast.makeText(context, "Please enter Valid dates..!", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        edt_ends_on_edit_note.setError("Enter End date");
                    }
                }else if(sp_repeate_type_scratch_note_edit.getSelectedItem().toString().equalsIgnoreCase("weekly")){
                    if (!edt_ends_after_edit_note.getText().toString().equals("")){
                        if (!edt_ends_after_edit_note.getText().toString().equals("0")){
                            if (!ch_monday_note_edit.isChecked() && !ch_tuesday_note_edit.isChecked() && !ch_wednesday_note_edit.isChecked()
                                    && !ch_thursday_note_edit.isChecked() && !ch_friday_note_edit.isChecked() && !ch_saturday_note_edit.isChecked()
                                    && !ch_sunday_note_edit.isChecked()){
                                tv_select_days_error_edit_note.setVisibility(View.VISIBLE);
                            }else{
                                tv_select_days_error_edit_note.setVisibility(View.GONE);
                                editNote(position, dialog);
                            }
                        }else{
                            Toast.makeText(context, "End weeks should be greater than 0.", Toast.LENGTH_LONG).show();
                        }

                    }else{
                        edt_ends_after_edit_note.setError("Enter Ends After");
                    }
                }else{
                    editNote(position, dialog);
                }
                }

            }
        });


        if (repeat_type.equals("1")){
            sp_repeate_type_scratch_note_edit.setSelection(1);
        }else if (repeat_type.equals("2")){
            sp_repeate_type_scratch_note_edit.setSelection(2);
        }else{
            sp_repeate_type_scratch_note_edit.setSelection(0);
        }


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private boolean compareDates(String reminder_date, String ends_on) {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            Date reminder_date1 = formatter.parse(reminder_date);
            Date ends_on1 = formatter.parse(ends_on);

            if (reminder_date1.compareTo(ends_on1)<0)
            {
                Log.d(Global.TAG, "ends on greater: ");
                return true;
            }else{
                Log.d(Global.TAG, "ends on less: ");
                return false;
            }

        }catch (ParseException e1){
            e1.printStackTrace();
            return false;
        }
    }

    private void selectWeekDays(String name) {
        if (!name.equals("")){
            if (name.equalsIgnoreCase("mon")){
                ch_monday_note_edit.setChecked(true);
            }else if (name.equalsIgnoreCase("tue")){
                ch_tuesday_note_edit.setChecked(true);
            }else if (name.equalsIgnoreCase("wed")){
                ch_wednesday_note_edit.setChecked(true);
            }else if (name.equalsIgnoreCase("thu")){
                ch_thursday_note_edit.setChecked(true);
            }else if (name.equalsIgnoreCase("fri")){
                ch_friday_note_edit.setChecked(true);
            }else if (name.equalsIgnoreCase("sat")){
                ch_saturday_note_edit.setChecked(true);
            }else if (name.equalsIgnoreCase("sun")){
                ch_sunday_note_edit.setChecked(true);
            }
        }
    }

    private DatePickerDialog.OnDateSetListener ends_on = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            edt_ends_on_edit_note.setText(year+"-"+((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString())));
        }
    };

    private String convertDateTomdy(String date) throws ParseException {
        //Date date1 = new Date();
        Log.d(Global.TAG, "convertDateTomdy Date-->" + date);
        //SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        SimpleDateFormat format = new SimpleDateFormat("MMM d yyyy");
        // String format = new SimpleDateFormat("MMM d, yyyy").format();
        //Date dateObj=format.parse(date);
        String final_date = format.format(date1);
        Log.d(Global.TAG, "convertDateTomdy mdy Date-->" + final_date);
        return final_date;
    }

    private void applySpinner(final List<String> taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(context, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }
    private String calculateTime(String time) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
            final Date dateObj = sdf.parse(time);
            System.out.println(dateObj);
            String final_time = new SimpleDateFormat("K:mm a").format(dateObj);
            System.out.println(final_time);
            return final_time;
        } catch (final ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void editNote(int position, final Dialog dialog) {

        dayList.clear();

        if (ch_monday_note_edit.isChecked()){
            dayList.add("mon");
        }
        if (ch_tuesday_note_edit.isChecked()){
            dayList.add("tue");
        }
        if (ch_wednesday_note_edit.isChecked()){
            dayList.add("wed");
        }
        if (ch_thursday_note_edit.isChecked()){
            dayList.add("thu");
        }
        if (ch_friday_note_edit.isChecked()){
            dayList.add("fri");
        }
        if (ch_saturday_note_edit.isChecked()){
            dayList.add("sat");
        }
        if (ch_sunday_note_edit.isChecked()){
            dayList.add("sun");
        }

        if (dayList.size()>0){
            scratchNoteReminderWeeklyDays="";
            for (String s : dayList)
            {
                scratchNoteReminderWeeklyDays += s + ",";
            }
            if (scratchNoteReminderWeeklyDays.endsWith(",")) {
                scratchNoteReminderWeeklyDays = scratchNoteReminderWeeklyDays.substring(0, scratchNoteReminderWeeklyDays.length() - 1);
            }
        }else{
            scratchNoteReminderWeeklyDays="";
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("scratchNoteId", scratchNoteList.get(position).getScratchNoteId());
            paramObj.put("scratchNoteText", eedt_note_contents.getText().toString());
            paramObj.put("scratchNoteColor", color_string);
            paramObj.put("scratchNoteReminderDate", edt_reminder_date.getText().toString());
            paramObj.put("scratchNoteReminderTime", edt_reminder_time.getText().toString());
            paramObj.put("scratchNoteReminderRepeat",scratchNoteReminderRepeat);
            paramObj.put("scratchNoteReminderWeeklyDays", scratchNoteReminderWeeklyDays);
            paramObj.put("scratchNoteReminderWeeklyEnds", edt_ends_after_edit_note.getText().toString());
            paramObj.put("scratchNoteReminderDailyEndDate", edt_ends_on_edit_note.getText().toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "editNote: " + paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.edit_scratch_note(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult != null) {
                    if (jsonResult.isSuccess()) {
                        Toast.makeText(context, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                        refreshNotes.refreshAllNotes();
                        dialog.dismiss();
                        //ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                    } else {
                        Toast.makeText(context, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:editNote " + t);

            }
        });

    }

    private DatePickerDialog.OnDateSetListener reminder_picker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            String month = String.valueOf(monthOfYear + 1);
            String selectedDay = String.valueOf(dayOfMonth);
            edt_reminder_date.setText(year + "-" + (month.length() == 1 ? "0" + month.toString() : month.toString()) + "-" + ((selectedDay.toString().length() == 1 ? "0" + selectedDay.toString() : selectedDay.toString())));
            /*String selected_date=year+"-"+month+"-"+dayOfMonth;
            String final_date="";
            try {
                final_date=convertDateTomdy(selected_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            edt_reminder_date.setText(final_date);*/
        }
    };

    @Override
    public int getItemCount() {
        return scratchNoteList.size();
    }

    private void openDialogColorPicker(final ImageButton ib_change_color) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.color_button_layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        FloatingActionButton fab_color1 = dialog.findViewById(R.id.fab_color1);
        FloatingActionButton fab_color2 = dialog.findViewById(R.id.fab_color2);
        FloatingActionButton fab_color3 = dialog.findViewById(R.id.fab_color3);
        FloatingActionButton fab_color4 = dialog.findViewById(R.id.fab_color4);
        FloatingActionButton fab_color5 = dialog.findViewById(R.id.fab_color5);
        FloatingActionButton fab_color6 = dialog.findViewById(R.id.fab_color6);

        fab_color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_string = "blue";
                dialog.dismiss();
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color1));
            }
        });

        fab_color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_string = "pink";
                dialog.dismiss();
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color2));
            }
        });

        fab_color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_string = "green";
                dialog.dismiss();
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color3));
            }
        });

        fab_color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_string = "orange";
                dialog.dismiss();
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color4));
            }
        });

        fab_color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_string = "white";
                dialog.dismiss();
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color5));
            }
        });

        fab_color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_string = "violet";
                dialog.dismiss();
                ib_change_color.setBackgroundColor(context.getResources().getColor(R.color.color6));
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }
}
