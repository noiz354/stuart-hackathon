package com.stuart.hackatonproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.model.ReminderDB;

public class ReminderHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView notifyTimeimeTextView;

    public ReminderHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.text_view_title);
        contentTextView = itemView.findViewById(R.id.text_view_content);
        notifyTimeimeTextView = itemView.findViewById(R.id.notify_text_view_time);
    }

    public void bind(ReminderDB reminderDB) {
        titleTextView.setText(reminderDB.getTitle());
        contentTextView.setText(reminderDB.getContent());
        notifyTimeimeTextView.setText(DateUtils.getRelativeTimeSpanString(System.currentTimeMillis() - reminderDB.getNotifyAt(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
    }
}
