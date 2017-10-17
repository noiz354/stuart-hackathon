package com.stuart.hackatonproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.model.UserDB;

/**
 * Created by zulfikarrahman on 10/16/17.
 */

public class FriendsHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private TextView contentTextView;

    public FriendsHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.text_view_title);
        contentTextView = itemView.findViewById(R.id.text_view_content);
    }

    public void bind(UserDB userDB) {
        titleTextView.setText(userDB.getName());
        contentTextView.setText(userDB.getEmail());
    }
}
