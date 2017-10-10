package com.stuart.hackatonproject.helper;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by User on 10/10/2017.
 */

public class LoginHelper {
    private static FirebaseAuth auth;

    public static FirebaseAuth getAuth(){
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static boolean isLoggedIn (){
        return getAuth().getCurrentUser() != null;
    }

    public static void signOut(){
        getAuth().signOut();
    }
}
