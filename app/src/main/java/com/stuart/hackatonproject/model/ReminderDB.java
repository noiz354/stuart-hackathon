package com.stuart.hackatonproject.model;

import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stuart.hackatonproject.constant.Constant;

import java.util.UUID;

/**
 * Created by nathan on 10/13/17.
 */

public class ReminderDB {

    public final static String TABLE_NAME = "reminder";
    public final static String FIELD_REMINDER_FROM = "reminder_from";
    public final static String FIELD_REMINDER_TO = "reminder_to";

    private String fromUserId;
    private String toUserId;
    private String title;
    private String content;
    private long createdAt;
    private long notifyAt;
    private String uniqueId;

    public ReminderDB() {

    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getNotifyAt() {
        return notifyAt;
    }

    public void setNotifyAt(long notifyAt) {
        this.notifyAt = notifyAt;
    }

    public String getReminderFromUniqueKey() {
        return FIELD_REMINDER_FROM + Constant.DELIMITER + System.currentTimeMillis();
    }

    public String getReminderToUniqueKey() {
        return FIELD_REMINDER_TO + Constant.DELIMITER + System.currentTimeMillis();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void save() {
        if (TextUtils.isEmpty(uniqueId)) {
            uniqueId = UUID.randomUUID().toString();
        }
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(UserDB.TABLE_NAME)
                .child(toUserId).child(ReminderDB.FIELD_REMINDER_FROM);
        mDatabase.child(uniqueId).setValue(this);
    }
}
