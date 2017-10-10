package com.stuart.hackatonproject.application;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

/**
 *
 */

public class BaseApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
