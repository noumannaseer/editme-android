package com.example.editme.fcm;

import android.util.Log;

import com.example.editme.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

/*
 * Created by Mahmoud on 3/13/2017.
 */


public class InstanceIdService
        extends FirebaseMessagingService
{


    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);

        String instanceId = FirebaseInstanceId.getInstance()
                                              .getToken();
        Log.d("@@@@", "onTokenRefresh: " + instanceId);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance()
                                                .getCurrentUser();
        if (firebaseUser != null)
        {
         /*   FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.REGISTERATION)
                            .child(firebaseUser.getUid())
                            .child("instance_id")
                            .setValue(instanceId);
            */
        }
    }
}
