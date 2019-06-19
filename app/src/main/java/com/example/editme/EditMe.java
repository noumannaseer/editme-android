package com.example.editme;

import android.app.Application;

import com.example.editme.utils.AndroidUtil;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;


//********************************************************************
public class EditMe
        extends Application
//********************************************************************
{


    @Getter private FirebaseAuth mAuth;
    @Getter private FirebaseFirestore mFireStore;
    @Getter private AccessToken mFaceBookAccessToken;


    //**************************************************************************
    @Override
    public void onCreate()
    //**************************************************************************
    {
        super.onCreate();
        AndroidUtil.setContext(this);
        AndroidUtil.setContext(this);
        val app = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        mFireStore.setFirestoreSettings(settings);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }

    //*********************************************************************
    public boolean isFaceBookLogin()
    //*********************************************************************
    {
        mFaceBookAccessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = mFaceBookAccessToken != null && !mFaceBookAccessToken.isExpired();
        if (isLoggedIn)
            return true;
        else
            return false;
    }

    //*********************************************************************
    public static @NonNull
    EditMe instance()
    //*********************************************************************
    {
        return (EditMe)AndroidUtil.getApplicationContext();

    }

}
