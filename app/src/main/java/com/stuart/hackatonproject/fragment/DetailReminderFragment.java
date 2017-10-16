package com.stuart.hackatonproject.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.activity.ListFriendsActivity;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.model.UserDB;
import com.stuart.hackatonproject.util.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nathan on 10/11/17.
 */

public class DetailReminderFragment extends Fragment {

    public static final String EXTRA_REMINDER = "EXTRA_REMINDER";
    private static final int REQUEST_CODE_GET_LIST_FRIEND = 3;

    public static Fragment instance(Context context) {
        return new DetailReminderFragment();
    }

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView reminderAtTextView;
    private TextView friendTextList;
    private View contentLabelFriendList;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    private ReminderDB reminderDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminderDB = getActivity().getIntent().getParcelableExtra(EXTRA_REMINDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_detail, container, false);
        titleTextView = view.findViewById(R.id.edit_text_title);
        contentTextView = view.findViewById(R.id.edit_text_content);
        reminderAtTextView = view.findViewById(R.id.edit_text_reminder_at_time);
        friendTextList = view.findViewById(R.id.content_text_view);
        contentLabelFriendList = view.findViewById(R.id.content_label_view);
        contentLabelFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ListFriendsActivity.createIntent(getActivity());
                startActivityForResult(intent, REQUEST_CODE_GET_LIST_FRIEND);
            }
        });
        loadData();
        return view;
    }

    private void loadData() {
        if (reminderDB != null) {
            titleTextView.setText(reminderDB.getTitle());
            contentTextView.setText(reminderDB.getContent());
            friendTextList.setText(reminderDB.getFromUserId());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CODE_GET_LIST_FRIEND){
                UserDB userDB = data.getParcelableExtra(ListFriendsFragment.EXTRA_USER_CHOOSEN);
                friendTextList.setText(userDB.getName());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    private void saveData() {
        String title = titleTextView.getText().toString();
        String description = contentTextView.getText().toString();
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
            return;
        }
        if (reminderDB == null) {
            reminderDB = new ReminderDB();
        }
        reminderDB.setFromUserId(FirebaseUtils.getCurrentUniqueUserId());
        reminderDB.setTitle(title);
        reminderDB.setContent(description);
        reminderDB.setCreatedAt(System.currentTimeMillis() + 10000000);

        List<String> toUserList = new ArrayList<>();
        toUserList.add(FirebaseUtils.getCurrentUniqueUserId());
        for (String toUser : toUserList) {
            sendTo(toUser);
        }
    }

    private void sendTo(String toUserId) {
        reminderDB.setToUserId(toUserId);
        reminderDB.save();
    }
}
