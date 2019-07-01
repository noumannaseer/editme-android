package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.model.User;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
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
import com.google.firebase.auth.UserInfo;

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
                            Arrays.asList(Constants.EMAIL));
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
                                    UIUtils.testToast(false, exception.getLocalizedMessage());
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
                                  FirebaseUser user = EditMe.instance()
                                                            .getMAuth()
                                                            .getCurrentUser();

                                  signUpFaceBook(user.getDisplayName(), user.getEmail(),
                                                 user.getUid(),
                                                 user.getPhotoUrl() + "?height=500");
                              }

                          }
                      });
    }

    //*********************************************************************
    private String getProfileImage(FirebaseUser user)
    //*********************************************************************
    {
        String facebookUserId = "";
        // find the Facebook profile and get the user's id
        for (UserInfo profile : user.getProviderData())
        {
            // check if the provider id matches "facebook.com"
            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId()))
            {
                facebookUserId = profile.getUid();
            }
        }
        String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
        return photoUrl;
    }

    //**********************************************************************************************
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    //**********************************************************************************************
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //**********************************************************************************************
    private void signUpFaceBook(String displayName, String email, String userId, String photoUrl)
    //**********************************************************************************************
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
                              FaceBookLoginActivity.super.onBackPressed();
                          }
                      });
    }

}
