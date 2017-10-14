package com.stuart.hackatonproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.util.FirebaseUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nathan on 10/11/17.
 */

public class DetailReminderFragment extends Fragment {

    public static Fragment instance(Context context) {
        return new DetailReminderFragment();
    }

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView reminderAtTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_detail, container, false);
        titleTextView = view.findViewById(R.id.edit_text_title);
        descriptionTextView = view.findViewById(R.id.edit_text_description);
        reminderAtTextView = view.findViewById(R.id.edit_text_reminder_at_time);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    private void saveData(){
        String title = titleTextView.getText().toString();
        String description = descriptionTextView.getText().toString();
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
            return;
        }
        List<String> toUserList = new ArrayList<>();
        toUserList.add(FirebaseUtils.getCurrentUniqueUserId());
        for (String toUser: toUserList) {
            ReminderDB reminderDB = new ReminderDB();
            reminderDB.setFromUserId(FirebaseUtils.getCurrentUniqueUserId());
            reminderDB.setToUserId(toUser);
            reminderDB.setTitle(title);
            reminderDB.setDescription(description);
            reminderDB.setCreatedAt(System.currentTimeMillis() + 10000000);
            reminderDB.save();
        }

    }
}
