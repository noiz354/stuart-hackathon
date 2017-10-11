package com.stuart.hackatonproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stuart.hackatonproject.R;

/**
 * Created by nathan on 10/11/17.
 */

public class DetailReminderFragment extends Fragment {

    public static Fragment instance(Context context) {
        return new DetailReminderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_detail, container, false);
        return view;
    }


}
