package com.stuart.hackatonproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.model.ReminderDB;

import java.util.Date;

public class ReminderHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView notifyTimeTextView;

    public ReminderHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.text_view_title);
        contentTextView = itemView.findViewById(R.id.text_view_content);
        notifyTimeTextView = itemView.findViewById(R.id.notify_text_view_time);
    }

    public void bind(ReminderDB reminderDB) {
        titleTextView.setText(reminderDB.getTitle());
        contentTextView.setText(reminderDB.getContent());
        String timeFormat = DateTimeUtils.formatTime(new Date(reminderDB.getNotifyAt()), true);
        notifyTimeTextView.setText(timeFormat);
    }
}