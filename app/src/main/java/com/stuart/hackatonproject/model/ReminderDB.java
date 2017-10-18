package com.stuart.hackatonproject.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stuart.hackatonproject.constant.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by nathan on 10/13/17.
 */

public class ReminderDB implements Parcelable {

    public final static String TABLE_NAME = "reminder";
    public final static String FIELD_REMINDER_FROM = "reminder_from";
    public final static String FIELD_REMINDER_TO = "reminder_to";
    public final static String FIELD_IMAGES = "images";

    private String fromUserId;

    private String title;
    private String content;
    private long createdAt;
    private long notifyAt;
    private String uniqueId;
    private Map<Integer, Object> imageIds = new HashMap<>();
    private String imageOne;
    private String imageTwo;
    private boolean containRudeWord;

    public void put(Integer position, String fileName){
        imageIds.put(position, fileName);
    }

    public ReminderDB() {

    }

    public String getImageOne() {
        return imageOne;
    }

    public void setImageOne(String imageOne) {
        this.imageOne = imageOne;
    }

    public String getImageTwo() {
        return imageTwo;
    }

    public void setImageTwo(String imageTwo) {
        this.imageTwo = imageTwo;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
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

    public boolean isContainRudeWord() {
        return containRudeWord;
    }

    public void setContainRudeWord(boolean containRudeWord) {
        this.containRudeWord = containRudeWord;
    }

    public void saveImage(){
        if(!imageIds.isEmpty()){
            if(imageIds.get(0) != null){
                imageOne = (String) imageIds.get(0);
            }

            if(imageIds.get(1) != null){
                imageTwo = (String) imageIds.get(1);
            }
        }
    }

    public void save(String toUserId) {
        generateUniqueId();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(UserDB.TABLE_NAME)
                .child(toUserId).child(ReminderDB.FIELD_REMINDER_FROM);
        mDatabase.child(uniqueId)
                .setValue(this);
    }

    public void generateUniqueId() {
        if (TextUtils.isEmpty(uniqueId)) {
            uniqueId = UUID.randomUUID().toString();
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fromUserId);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeLong(this.createdAt);
        dest.writeLong(this.notifyAt);
        dest.writeString(this.uniqueId);
        dest.writeInt(this.imageIds.size());
        dest.writeString(this.imageOne);
        dest.writeString(this.imageTwo);
        dest.writeByte(this.containRudeWord ? (byte) 1 : (byte) 0);
    }

    protected ReminderDB(Parcel in) {
        this.fromUserId = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.createdAt = in.readLong();
        this.notifyAt = in.readLong();
        this.uniqueId = in.readString();
        int imageIdsSize = in.readInt();
        this.imageOne = in.readString();
        this.imageTwo = in.readString();
        this.containRudeWord = in.readByte() != 0;
    }

    public static final Creator<ReminderDB> CREATOR = new Creator<ReminderDB>() {
        @Override
        public ReminderDB createFromParcel(Parcel source) {
            return new ReminderDB(source);
        }

        @Override
        public ReminderDB[] newArray(int size) {
            return new ReminderDB[size];
        }
    };
}
