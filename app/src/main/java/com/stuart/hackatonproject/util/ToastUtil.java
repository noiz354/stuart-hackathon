package com.stuart.hackatonproject.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 10/17/2017.
 */

public class ToastUtil {
    private static final int TOAST_LONG_SIZE = 20; //20 characters

    public static void showToast(Context context, Throwable e){
        String message = e.getMessage();
        if (!TextUtils.isEmpty(message)) {
            showToast(context, message);
        }
    }

    public static void showToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, message.length()> TOAST_LONG_SIZE ? Toast.LENGTH_LONG:Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
