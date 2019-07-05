package com.example.editme.worker;

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
import com.example.editme.R;
import com.example.editme.activities.CheckOutActivity;
import com.example.editme.activities.HomeActivity;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import lombok.val;


//**************************************************************
public class UploadImageWorker
        extends Worker
//**************************************************************
{

    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    public static String ORDER_IMAGES_URI = "ORDER_IMAGES_URI";
    public static String ORDER_IMAGES_ID = "ORDER_IMAGES_ID";
    public static final String IMAGE_URL = "IMAGE_URL";
    private int mImageId;
    private Uri mImageIntentURI;
    private String mImageUrl;
    private CountDownLatch mLatch;


    //**************************************************************
    public UploadImageWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams)
    //**************************************************************
    {
        super(appContext, workerParams);
    }


    //********************************************************************************
    @NonNull
    @Override
    public Result doWork()
    //********************************************************************************
    {
        mNotificationManager = (NotificationManager)getApplicationContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        mImageId = getInputData().getInt(ORDER_IMAGES_ID, -1);
        mImageIntentURI = Uri.parse(getInputData().getString(ORDER_IMAGES_URI));


        mLatch = new CountDownLatch(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an",
                                                                              NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(AndroidUtil.getString(R.string.image_is_uploading));
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        Intent resultIntent = new Intent(AndroidUtil.getApplicationContext(), HomeActivity.class);
        resultIntent.putExtra(Constants.IMAGE_DOWNLOADED, true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(
                AndroidUtil.getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationBuilder = new NotificationCompat.Builder(AndroidUtil.getApplicationContext(),
                                                              "id")
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setContentTitle(AndroidUtil.getString(R.string.image) + (mImageId + 1))
                .setContentText(AndroidUtil.getString(R.string.image_is_uploading))
                .setDefaults(0)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        mNotificationManager.notify(mImageId, mNotificationBuilder.build());

        uploadImageToFireBase();

        try
        {
            mLatch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        Data data = createOutputData(mImageUrl, mImageId);
        return Result.success(createOutputData(mImageUrl, mImageId));
    }


    //*******************************************************************
    private void uploadImageToFireBase()
    //*******************************************************************
    {
        val userId = EditMe.instance()
                           .getMUserId();

        val mStorage = EditMe.instance()
                             .getMStorageReference()
                             .getReference();
        StorageReference filePath = mStorage.child(Constants.ORDER_IMAGES)
                                            .child(userId)
                                            .child(UUID.randomUUID()
                                                       .toString());


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
                                                public Task<Uri> then(@lombok.NonNull Task<UploadTask.TaskSnapshot> task)
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
                                        public void onComplete(@lombok.NonNull Task<Uri> task)
                                        {
                                            if (task.isSuccessful())
                                            {

                                                Log.d("image_url", task.getResult()
                                                                       .toString());
                                                onUploadComplete(true, task.getResult()
                                                                           .toString());
                                                val outputData = createOutputData(task.getResult()
                                                                                      .toString(),
                                                                                  mImageId);
                                                // setOutputData(outputData);
                                                Log.d("work_manager", task.getResult()
                                                                          .toString());
                                                mImageUrl = task.getResult()
                                                                .toString();
                                                mLatch.countDown();
                                            }
                                        }
                                    });
    }


    //*******************************************************************
    private Data createOutputData(String imageUrl, int imageId)
    //*******************************************************************
    {
        return new Data.Builder()
                .putString(IMAGE_URL, imageUrl)
                .putInt(ORDER_IMAGES_ID, imageId)
                .build();
    }


    //*******************************************************************
    private void updateNotification(int currentProgress)
    //*******************************************************************
    {
        mNotificationBuilder.setProgress(100, currentProgress, false);
        mNotificationBuilder.setContentText(
                AndroidUtil.getString(R.string.uploaded_format, currentProgress));
        mNotificationManager.notify(mImageId, mNotificationBuilder.build());
    }


    //**************************************************************************************
    private void sendProgressUpdate(boolean downloadComplete, String imageUrl)
    //**************************************************************************************
    {

        Intent intent = new Intent(CheckOutActivity.PROGRESS_UPDATE);
        intent.putExtra(AndroidUtil.getString(
                R.string.image_upload_complete, (mImageId + 1)),
                        downloadComplete);
        intent.putExtra(ORDER_IMAGES_ID, mImageId);
        intent.putExtra(IMAGE_URL, imageUrl);
    }

    //**************************************************************************************
    private void onUploadComplete(boolean downloadComplete, String imageUrl)
    //**************************************************************************************
    {
        sendProgressUpdate(downloadComplete, imageUrl);
        mNotificationManager.cancel(0);
        mNotificationBuilder.setProgress(0, 0, false);
        mNotificationBuilder.setContentText(AndroidUtil.getString(R.string.image_uploaded));
        mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_upload_done);
        mNotificationManager.notify(mImageId, mNotificationBuilder.build());

    }
}
