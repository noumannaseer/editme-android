package com.example.editme.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.example.editme.EditMe;
import com.example.editme.activities.CheckOutActivity;
import com.example.editme.activities.HomeActivity;
import com.example.editme.services.upload_services.UploadImagesService;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import lombok.val;

public class UploadImageWorker
        extends Worker
{

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    public static String ORDER_IMAGES_URI = "ORDER_IMAGES_URI";
    public static String ORDER_IMAGES_ID = "ORDER_IMAGES_ID";
    public static final String IMAGE_URL = "IMAGE_URL";
    private int mImageId;
    private Uri mImageIntentURI;
    private Context mContext;

    /*public UploadImageWorker(int mImageId, Uri mImageIntentURI, Context context)
    {
        this.mImageId = mImageId;
        this.mImageIntentURI = mImageIntentURI;
        mContext = context;
    }

    */
    public UploadImageWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams)
    {
        super(appContext, workerParams);
    }

    CountDownLatch latch;


    @NonNull
    @Override
    public Result doWork()
    {
        notificationManager = (NotificationManager)getApplicationContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        mImageId = getInputData().getInt(ORDER_IMAGES_ID, -1);
        mImageIntentURI = Uri.parse(getInputData().getString(ORDER_IMAGES_URI));


        latch = new CountDownLatch(1);
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
        Intent resultIntent = new Intent(AndroidUtil.getApplicationContext(), HomeActivity.class);
        resultIntent.putExtra(Constants.IMAGE_DOWNLOADED, true);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(
                AndroidUtil.getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(AndroidUtil.getApplicationContext(),
                                                             "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Image" + mImageId)
                .setContentText("Your Image is uploading")
                .setDefaults(0)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(mImageId, notificationBuilder.build());

        uploadImageToFireBase();

        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return Result.success();
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
                                                onDownloadComplete(true, task.getResult()
                                                                             .toString());
                                                val outputData = createOutputData(task.getResult()
                                                                                      .toString(),
                                                                                  mImageId);
                                                // setOutputData(outputData);
                                                Log.d("work_manager", task.getResult()
                                                                          .toString());
                                                latch.countDown();
                                            }
                                        }
                                    });
    }

    private Data createOutputData(String imageUrl, int imageId)

    {
        return new Data.Builder()
                .putString(IMAGE_URL, imageUrl)
                .putInt(ORDER_IMAGES_ID, imageId)
                .build();
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
       /* LocalBroadcastManager.getInstance(UploadImagesService.this)
                             .sendBroadcast(intent);*/
    }

    private void onDownloadComplete(boolean downloadComplete, String imageUrl)
    {
        sendProgressUpdate(downloadComplete, imageUrl);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Image Upload Complete");
        notificationManager.notify(mImageId, notificationBuilder.build());

    }
}
