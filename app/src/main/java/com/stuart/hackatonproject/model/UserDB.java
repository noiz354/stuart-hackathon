package com.stuart.hackatonproject.model;

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

public class UserDB {

    public final static String TABLE_NAME = "user";
    public String name;
    public String email;


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

    public UserDB() {

    }

    public void save() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(UserDB.TABLE_NAME);
        databaseReference.child(FirebaseUtils.getUniqueUserId(email)).setValue(this);
    }

}
