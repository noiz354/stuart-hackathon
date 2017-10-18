package com.stuart.hackatonproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.util.GeneralUtils;

import java.util.Date;

public class ReminderHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView notifyTimeTextView;
    private TextView reminderFrom;

    public ReminderHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.text_view_title);
        contentTextView = itemView.findViewById(R.id.text_view_content);
        notifyTimeTextView = itemView.findViewById(R.id.notify_text_view_time);
        reminderFrom = itemView.findViewById(R.id.text_reminder_from);
    }

    public void bind(ReminderDB reminderDB) {
        titleTextView.setText(reminderDB.getTitle());
        contentTextView.setText(reminderDB.getContent());
        notifyTimeTextView.setText(GeneralUtils.generateReadableTime(reminderDB.getNotifyAt()));
        reminderFrom.setText(reminderDB.getFromUserId());
    }
}