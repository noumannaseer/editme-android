package com.example.editme.activities;

import androidx.appcompat.app.AppCompatActivity;
import lombok.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.utils.AndroidUtil;
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
                          // Sign in success, update UI with the signed-in user's information
                          FirebaseUser user = EditMe.instance()
                                                    .getMAuth()
                                                    .getCurrentUser();
                          AndroidUtil.toast(false, user.getDisplayName() + " " + user.getEmail());
                          GoogleSignInActivity.super.onBackPressed();
                      }
                      else
                      {
                          // If sign in fails, display a message to the user.
                      }

                      // ...
                  }
              });
    }

}