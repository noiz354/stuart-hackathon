package com.stuart.hackatonproject.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stuart.hackatonproject.helper.LoginHelper;
import com.stuart.hackatonproject.util.FirebaseUtils;

/**
 * Created by nathan on 10/13/17.
 */

public class UserDB implements Parcelable {

    public final static String TABLE_NAME = "user";
    private String name;
    private String email;
    private String fcmToken;
    private String uID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public UserDB() {

    }

    public void save() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(UserDB.TABLE_NAME);
        databaseReference.child(FirebaseUtils.getUniqueUserId(email)).setValue(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.fcmToken);
        dest.writeString(this.uID);
    }

    protected UserDB(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.fcmToken = in.readString();
        this.uID = in.readString();
    }

    public static final Parcelable.Creator<UserDB> CREATOR = new Parcelable.Creator<UserDB>() {
        @Override
        public UserDB createFromParcel(Parcel source) {
            return new UserDB(source);
        }

        @Override
        public UserDB[] newArray(int size) {
            return new UserDB[size];
        }
    };
}
