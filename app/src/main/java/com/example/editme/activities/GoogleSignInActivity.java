package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.model.User;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import lombok.NonNull;


//*****************************************************************
public class GoogleSignInActivity
        extends AppCompatActivity
//*****************************************************************
{

    private static final int RC_SIGN_IN = 140;
    private static final String TAG = "GoogleSignInActivity";
    private GoogleSignInClient mGoogleSignInClient;

    //*****************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //*****************************************************************
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);
        initControls();
    }


    //*****************************************************************
    private void initControls()
    //*****************************************************************
    {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AndroidUtil.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();

    }

    //*****************************************************************
    private void signIn()
    //*****************************************************************
    {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //***********************************************************************************
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    //***********************************************************************************
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                fireBaseAuthWithGoogle(account);
            }
            catch (ApiException e)
            {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                UIUtils.testToast(false, AndroidUtil.getString(R.string.google_signin_failed));
                super.onBackPressed();
                // ...
            }
        }
    }


    //*******************************************************
    @Override
    //*******************************************************
    public void onStart()
    {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = EditMe.instance()
                                         .getMAuth()
                                         .getCurrentUser();
    }

    //***********************************************************************************
    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct)
    //***********************************************************************************
    {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        EditMe.instance()
              .getMAuth()
              .signInWithCredential(credential)
              .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
              {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task)
                  {
                      if (task.isSuccessful())
                      {
                          FirebaseUser user = EditMe.instance()
                                                    .getMAuth()
                                                    .getCurrentUser();
                          signUpGoogle(user.getDisplayName(), user.getEmail(),
                                       user.getUid(),
                                       user.getPhotoUrl()
                                           .toString());

                      }
                      else
                      {
                          // If sign in fails, display a message to the user.
                          AndroidUtil.toast(false, task.getException()
                                                       .getLocalizedMessage()
                                                       .toString());
                          GoogleSignInActivity.super.onBackPressed();
                      }

                      // ...
                  }
              });
    }

    //******************************************************************************************
    private void signUpGoogle(String displayName, String email, String userId, String photoUrl)
    //******************************************************************************************
    {
        EditMe.instance()
              .getMFireStore()
              .collection(Constants.Users)
              .document(userId)
              .set(new User(displayName, email, userId, photoUrl, null))
              .addOnCompleteListener(
                      new OnCompleteListener<Void>()
                      {
                          @Override
                          public void onComplete(@androidx.annotation.NonNull Task<Void> task)
                          {
                              EditMe.instance()
                                    .loadUserDetail();


                              String instanceId = FirebaseInstanceId.getInstance()
                                                                    .getToken();
                              Map<String, Object> fcmToken = new HashMap<>();
                              fcmToken.put(Constants.FCM_TOKEN, instanceId);
                              EditMe.instance()
                                    .loadUserDetail();
                              EditMe.instance()
                                    .getMFireStore()
                                    .collection(Constants.Users)
                                    .document(EditMe.instance()
                                                    .getMUserId())
                                    .update(fcmToken)
                                    .addOnCompleteListener(
                                            new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@androidx.annotation.NonNull Task<Void> task)
                                                {
                                                    GoogleSignInActivity.super.onBackPressed();
                                                }
                                            });

                          }
                      });
    }

}
