package com.stuart.hackatonproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.model.ReminderDB;

public class ReminderHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private TextView contentTextView;

    public ReminderHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.text_view_title);
        contentTextView = itemView.findViewById(R.id.text_view_content);
    }

    public void bind(ReminderDB reminderDB) {
        titleTextView.setText(reminderDB.getTitle());
        contentTextView.setText(reminderDB.getContent());
    }
}
