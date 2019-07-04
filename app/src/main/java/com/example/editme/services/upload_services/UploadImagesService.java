package com.example.editme.services.upload_services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.editme.EditMe;
import com.example.editme.activities.CheckOutActivity;
import com.example.editme.activities.HomeActivity;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import lombok.NonNull;
import lombok.val;

public class UploadImagesService
        extends IntentService
{

    public UploadImagesService()
    {
        super("Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    public static String ORDER_IMAGES_URI = "ORDER_IMAGES_URI";
    public static String ORDER_IMAGES_ID = "ORDER_IMAGES_ID";
    private int mImageId;
    private Uri mImageIntentURI;
    public static final String IMAGE_URL = "IMAGE_URL";


//    private static int mChannelId = 0;


    @Override
    protected void onHandleIntent(Intent intent)
    {

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent.getExtras()
                  .containsKey(ORDER_IMAGES_URI) && intent.getExtras()
                                                          .containsKey(ORDER_IMAGES_ID))
        {
            //mSelectedImage = intent.getParcelableExtra(ORDER_IMAGES);
            mImageId = intent.getIntExtra(ORDER_IMAGES_ID, -1);
            mImageIntentURI = Uri.parse(intent.getStringExtra(ORDER_IMAGES_URI));

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an",
                                                                              NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription("You Image is uploading");
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, HomeActivity.class);
        resultIntent.putExtra(Constants.IMAGE_DOWNLOADED, true);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Image" + mImageId)
                .setContentText("Your Image is uploading")
                .setDefaults(0)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(mImageId, notificationBuilder.build());
//        mChannelId++;
        //initRetrofit();
        uploadImageToFireBase();

    }

    private void uploadImageToFireBase()
    {
        String orderId = UIUtils.randomAlphaNumeric(5);
        val userId = EditMe.instance()
                           .getMUserId();

        val mStorage = EditMe.instance()
                             .getMStorageReference()
                             .getReference();
        StorageReference filePath = mStorage.child(Constants.ORDER_IMAGES)
                                            .child(userId);


        Task<Uri> uriTask = filePath.putFile(mImageIntentURI)
                                    .addOnProgressListener(taskSnapshot -> {
                                        double progress = (100.0 * taskSnapshot
                                                .getBytesTransferred()) / taskSnapshot
                                                .getTotalByteCount();

                                        updateNotification((int)progress);

                                    })
                                    .addOnPausedListener(taskSnapshot -> {


                                    })
                                    .continueWithTask(
                                            new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                                            {
                                                @Override
                                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                                                        throws Exception
                                                {
                                                    if (!task.isSuccessful())
                                                    {
                                                        AndroidUtil.toast(false,
                                                                          task.getException()
                                                                              .toString());
                                                        throw task.getException();
                                                    }
                                                    return filePath.getDownloadUrl();
                                                }
                                            })
                                    .addOnCompleteListener(new OnCompleteListener<Uri>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task)
                                        {
                                            if (task.isSuccessful())
                                            {

                                                Log.d("image_url", task.getResult()
                                                                       .toString());
                                                onDownloadComplete(true, task.getResult()
                                                                             .toString());

                                            }
                                        }
                                    });
    }


    private void updateNotification(int currentProgress)
    {


        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Uploaded: " + currentProgress + "%");
        notificationManager.notify(mImageId, notificationBuilder.build());
    }


    private void sendProgressUpdate(boolean downloadComplete, String imageUrl)
    {

        Intent intent = new Intent(CheckOutActivity.PROGRESS_UPDATE);
        intent.putExtra("Image" + mImageId + " UploadComplete", downloadComplete);
        intent.putExtra(ORDER_IMAGES_ID, mImageId);
        intent.putExtra(IMAGE_URL, imageUrl);
        LocalBroadcastManager.getInstance(UploadImagesService.this)
                             .sendBroadcast(intent);
    }

    private void onDownloadComplete(boolean downloadComplete, String imageUrl)
    {
        sendProgressUpdate(downloadComplete, imageUrl);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Image Upload Complete");
        notificationManager.notify(mImageId, notificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        notificationManager.cancel(0);
    }

}
