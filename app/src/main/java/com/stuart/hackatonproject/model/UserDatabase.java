package com.stuart.hackatonproject.model;

/**
 * Created by nathan on 10/13/17.
 */

public class UserDatabase {

    public String name;
    public String email;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public UserDatabase() {
    }

    public UserDatabase(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
