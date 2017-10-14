package com.stuart.hackatonproject.util;

import android.util.Base64;

import com.stuart.hackatonproject.helper.LoginHelper;

import java.util.UUID;

/**
 * Created by nathan on 10/13/17.
 */

public class FirebaseUtils {

    public static String getCurrentUniqueUserId() {
        return getUniqueUserId(LoginHelper.getAuth().getCurrentUser().getEmail());
    }

    public static String getUniqueUserId(String email) {
        return email.replace(".", "");
    }

    private static String getBase64Encoded(String text) {
        try {
            byte[] data = text.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }
}
