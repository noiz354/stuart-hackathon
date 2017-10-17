package com.stuart.hackatonproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.activity.base.BaseActivity;
import com.stuart.hackatonproject.fragment.ListFriendsFragment;

/**
 * Created by zulfikarrahman on 10/16/17.
 */

public class ListFriendsActivity extends BaseActivity {
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ListFriendsActivity.class);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_fragment);
        setUpToolbar();
        replaceFragment(ListFriendsFragment.createInstance(), false);
    }
}
