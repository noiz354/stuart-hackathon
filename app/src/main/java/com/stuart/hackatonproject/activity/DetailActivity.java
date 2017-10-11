package com.stuart.hackatonproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.activity.base.BaseActivity;
import com.stuart.hackatonproject.fragment.DetailReminderFragment;

/**
 * Created by nathan on 10/11/17.
 */

public class DetailActivity extends BaseActivity {

    public static void startNew(Context context) {
        Intent intent = new Intent(context, DetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_fragment);
        setUpToolbar();
        replaceFragment(DetailReminderFragment.instance(this), false);
    }
}
