package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


//*******************************************************************
public class FaceBookLoginActivity
        extends AppCompatActivity
//*******************************************************************
{

    private CallbackManager callbackManager;

    //*******************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //*******************************************************************
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_book_login);
        initControls();
    }

    //*******************************************************************
    private void initControls()
    //*******************************************************************
    {
        LoginManager.getInstance()
                    .logInWithReadPermissions(
                            this,
                            Arrays.asList("email"));
        LoginManager.getInstance()
                    .setLoginBehavior(LoginBehavior.DEVICE_AUTH);

        addLoginSuccessListener();
    }

    //*******************************************************************
    private void addLoginSuccessListener()
    //*******************************************************************
    {
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        LoginManager.getInstance()
                    .registerCallback(
                            callbackManager,
                            new FacebookCallback<LoginResult>()
                            {
                                @Override
                                public void onSuccess(LoginResult loginResult)
                                {
                                    UIUtils.testToast(false, AndroidUtil.getString(
                                            R.string.fb_login_successs));
                                    handleFaceBookToken(loginResult.getAccessToken());
                                }

                                @Override
                                public void onCancel()
                                {
                                    UIUtils.testToast(false, AndroidUtil.getString(
                                            R.string.fb_login_cancel));
                                    FaceBookLoginActivity.super.onBackPressed();
                                }

                                @Override
                                public void onError(FacebookException exception)
                                {
                                    UIUtils.testToast(false, exception.getLocalizedMessage()
                                                                      .toString());
                                    FaceBookLoginActivity.super.onBackPressed();
                                }
                            }
                    );
    }

    //*********************************************************************
    private void handleFaceBookToken(AccessToken accessToken)
    //*********************************************************************
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        EditMe.instance()
              .getMAuth()
              .signInWithCredential(credential)
              .addOnCompleteListener(
                      new OnCompleteListener<AuthResult>()
                      {
                          @Override
                          public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task)
                          {
                              if (task.isSuccessful())
                              {
                                  FirebaseUser myuser = EditMe.instance()
                                                              .getMAuth()
                                                              .getCurrentUser();
                                  UIUtils.testToast(false,
                                                    myuser.getDisplayName() + " " + myuser.getEmail());
                                  FaceBookLoginActivity.super.onBackPressed();
                              }

                          }
                      });
    }

    //**********************************************************************************************
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    //**********************************************************************************************
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
