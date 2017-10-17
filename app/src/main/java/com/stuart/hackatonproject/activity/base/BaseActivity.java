package com.stuart.hackatonproject.activity.base;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.constant.ColorTypeDef;
import com.stuart.hackatonproject.constant.Constant;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    @CallSuper
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolbar();
    }

    protected void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(hasBackButton());
            getSupportActionBar().setTitle(getTitle());
        }
    }

    @Override
    public Resources.Theme getTheme() {
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        if (TextUtils.isEmpty(firebaseRemoteConfig.getString(Constant.REMOTE_CONFIG_APP_COLOR_THEME))) {
            return super.getTheme();
        }
        @ColorTypeDef int colorTheme = Integer.valueOf(firebaseRemoteConfig.getString(Constant.REMOTE_CONFIG_APP_COLOR_THEME));
        switch (colorTheme) {
            case ColorTypeDef.GREEN:
                setTheme(R.style.GreenAppTheme);
                break;
            case ColorTypeDef.RED:
                setTheme(R.style.RedAppTheme);
                break;
            case ColorTypeDef.PURPLE:
                setTheme(R.style.PurpleAppTheme);
                break;
            default:
                break;
        }
        long cacheExpiration = 3600; // 1 hour in seconds.
        if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        firebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // firebaseRemoteConfig.activateFetched();
                }
            }
        });
        return super.getTheme();
    }

    protected boolean hasBackButton(){
        return true;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackCount == 1) {
                setUpTitleByTag(null); // set default
            } else { //2 or more
                setUpTitleByTag(getSupportFragmentManager()
                        .getBackStackEntryAt(backStackCount - 2)
                        .getName());
            }
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //hideProgressDialog();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackstack) {
        replaceFragment(fragment, addToBackstack, fragment.getClass().getSimpleName());
    }

    public void replaceFragment(Fragment fragment, boolean addToBackstack, String tag) {
        if (!addToBackstack) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment, tag).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment, tag).addToBackStack(tag).commit();
        }
        setUpTitleByTag(tag);
    }

    public void replaceAndHideOldFragment(Fragment fragment, boolean addToBackstack, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment currentVisibileFragment = getCurrentVisibleFragment(fragmentManager);
        if (currentVisibileFragment != null) {
            ft.hide(currentVisibileFragment);
        }
        if (!addToBackstack) {
            ft.add(R.id.fragment, fragment, tag).show(fragment).commit();
        } else {
            ft.add(R.id.fragment, fragment, tag).addToBackStack(tag).show(fragment).commit();
        }
        setUpTitleByTag(tag);
    }

    public boolean showFragment(String tag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(tag);
        if (f == null) {
            return false;
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment currentVisibileFragment = getCurrentVisibleFragment(fragmentManager);
            if (currentVisibileFragment != null) {
                ft.hide(currentVisibileFragment);
            }
            ft.show(f).commit();
            setUpTitleByTag(tag);
            return true;
        }
    }

    // assume only 1 fragment visible
    private Fragment getCurrentVisibleFragment(FragmentManager fragmentManager) {
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (int i = 0, sizei = fragmentList.size(); i < sizei; i++) {
            Fragment f = fragmentList.get(i);
            if (f != null && f.isVisible()) {
                return f;
            }
        }
        return null;
    }

    protected void setUpTitleByTag(String tag) {
        // no operation
    }

}
