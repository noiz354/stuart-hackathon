package com.stuart.hackatonproject.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.activity.base.BaseActivity;
import com.stuart.hackatonproject.fragment.DetailReminderFragment;
import com.stuart.hackatonproject.fragment.SharedReminderFragment;
import com.stuart.hackatonproject.helper.LoginHelper;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.model.UserDB;
import com.stuart.hackatonproject.util.FirebaseUtils;

public class HomeActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = HomeActivity.class.getSimpleName();
    private static final int REQUEST_INVITE = 423;

    private GoogleApiClient mGoogleApiClient;

    private AdView mAdView;

    public static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    public static void startNewTask(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDynamicLink();
        setUpGoogleApiClient();
        setUpToolbar();

        if (savedInstanceState == null) {
            replaceFragment(new SharedReminderFragment(), false);
        }

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailActivity.startNew(HomeActivity.this);
            }
        });

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setDynamicLink() {
        Uri uri = getIntent().getData();
        if(uri != null) {
            String idReminder = uri.getQueryParameter("id");
            FirebaseDatabase.getInstance().getReference().child(UserDB.TABLE_NAME)
                    .child(FirebaseUtils.getCurrentUniqueUserId())
                    .child(ReminderDB.FIELD_REMINDER_FROM)
                    .child(idReminder)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ReminderDB reminderDB = dataSnapshot.getValue(ReminderDB.class);
                    Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                    intent.putExtra(DetailReminderFragment.EXTRA_REMINDER, reminderDB);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected boolean hasBackButton() {
        return false;
    }

    private void setUpGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                signOut();
                return true;
            case R.id.menu_invite:
                inviteFriends();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inviteFriends() {
            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.app_name))
                    .setMessage(getString(R.string.invitation_message))
                    .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                    .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                    .setCallToActionText(getString(R.string.app_name))
                    .build();
            startActivityForResult(intent, REQUEST_INVITE);
    }

    private void signOut() {
        if (!mGoogleApiClient.isConnected()) {
            SignInActivity.startNewTask(HomeActivity.this);
        } else {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        // Firebase sign out
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(getString(R.string.app_topics));
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(LoginHelper.getAuth().getUid());
                        LoginHelper.signOut();
                        SignInActivity.startNewTask(HomeActivity.this);
                    }
                }
            });
        }

    }

}
