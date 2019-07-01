package com.example.editme;

import android.app.Application;

import com.example.editme.model.User;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;

import javax.annotation.Nullable;

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
    @Getter private String mUserId;
    @Getter private User mUserDetail;
    @Getter private FirebaseStorage mStorageReference;

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
        mStorageReference = FirebaseStorage.getInstance();
        loadUserDetail();
    }

    //*********************************************************************
    public boolean isFaceBookLogin()
    //*********************************************************************
    {
        mFaceBookAccessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = mFaceBookAccessToken != null && !mFaceBookAccessToken.isExpired();
        return isLoggedIn;
    }

    //*********************************************************************
    public static @NonNull
    EditMe instance()
    //*********************************************************************
    {
        return (EditMe)AndroidUtil.getApplicationContext();

    }

    //*********************************************************************
    public void loadUserDetail()
    //*********************************************************************

    {
        if (getMAuth().getCurrentUser() != null)
            mUserId = getMAuth().getCurrentUser()
                                .getUid();
        if (mUserId == null)
            return;
        EditMe.instance()
              .getMFireStore()
              .collection(Constants.Users)
              .document(mUserId)
              .addSnapshotListener(new EventListener<DocumentSnapshot>()
              {
                  @Override
                  public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
                  {
                      mUserDetail = documentSnapshot.toObject(User.class);

                  }
              });
    }

}
