package com.example.editme;

import android.app.Application;

import com.example.editme.activities.HomeActivity;
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
import com.google.firebase.functions.FirebaseFunctions;
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


    @Getter
    private FirebaseAuth mAuth;
    @Getter
    private FirebaseFirestore mFireStore;
    @Getter
    private AccessToken mFaceBookAccessToken;
    @Getter
    private String mUserId;
    @Getter
    private User mUserDetail;
    @Getter
    private FirebaseStorage mStorageReference;
    @Getter
    private FirebaseFunctions firebaseFunctions;
    @Getter
    private String mStripeKey;

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
        firebaseFunctions = FirebaseFunctions.getInstance(app, "us-central1");
        loadUserDetail();
        getStripeKeyFromDatabase();
        mStripeKey = "pk_test_MTaMbNd25WpLdWZCuQgww49W003ZcNOmDk";
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
        return (EditMe) AndroidUtil.getApplicationContext();

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
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        mUserDetail = documentSnapshot.toObject(User.class);

                    }
                });
    }

    private void getStripeKeyFromDatabase() {
//        this.getMDataBaseInstance()
//                .getReference("keys")
//                .child("stripeKey")
//                .addValueEventListener(
//                        new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
//                                mStripeKey = dataSnapshot.getValue()
//                                        .toString();
//
//                            }
//
//                            @Override
//                            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
//                            }
//                        });
    }


}
