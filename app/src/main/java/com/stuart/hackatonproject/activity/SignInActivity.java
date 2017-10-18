package com.stuart.hackatonproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.activity.base.BaseActivity;
import com.stuart.hackatonproject.helper.LoginHelper;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.model.UserDB;
import com.stuart.hackatonproject.util.FirebaseUtils;

/**
 * Created by User on 10/10/2017.
 */

public class SignInActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = SignInActivity.class.getSimpleName();

    public static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    public static void start(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    public static void startNewTask(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setUpGoogleApiClient();

        View buttonSignIn = findViewById(R.id.btn_google_sign_in);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void setUpGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                onErrorLoginGoogle();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        LoginHelper.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUserDatabase();
                        } else {
                            onErrorLoginGoogle();
                        }
                    }
                });
    }


    private void onSuccessLoginGoogle() {
        hideProgressDialog();
        HomeActivity.start(SignInActivity.this);
    }

    private void updateUserDatabase() {
        DatabaseReference query = FirebaseDatabase.getInstance().getReference(UserDB.TABLE_NAME);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.app_topics));
                FirebaseMessaging.getInstance().subscribeToTopic(LoginHelper.getAuth().getUid());

                UserDB user;
                dataSnapshot.child(FirebaseUtils.getCurrentUniqueUserId()).getValue(UserDB.class);
                if (!dataSnapshot.hasChild(FirebaseUtils.getCurrentUniqueUserId())) {
                    user = new UserDB();
                } else {
                    user = dataSnapshot.child(FirebaseUtils.getCurrentUniqueUserId()).getValue(UserDB.class);
                }
                user.setEmail(LoginHelper.getAuth().getCurrentUser().getEmail());
                user.setName(LoginHelper.getAuth().getCurrentUser().getDisplayName());
                user.setuID(LoginHelper.getAuth().getUid());
                user.setFcmToken(FirebaseInstanceId.getInstance().getToken());
                user.save();
                onSuccessLoginGoogle();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO clear cache, logout
            }
        });
    }

    private void onErrorLoginGoogle() {
        hideProgressDialog();
        Toast.makeText(SignInActivity.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
