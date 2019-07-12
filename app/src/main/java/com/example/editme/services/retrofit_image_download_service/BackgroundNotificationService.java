package com.example.editme.services.retrofit_image_download_service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.editme.R;
import com.example.editme.activities.HomeActivity;
import com.example.editme.activities.ImageSliderActivity;
import com.example.editme.model.OrderImages;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class BackgroundNotificationService
        extends IntentService
{

    public BackgroundNotificationService()
    {
        super("Service");
    }

    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    private OrderImages mSelectedImage;
    public static String ORDER_IMAGES = "ORDER_IMAGES";


    @Override
    protected void onHandleIntent(Intent intent)
    {

        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent.getExtras()
                  .containsKey(ORDER_IMAGES))
        {
            mSelectedImage = intent.getParcelableExtra(ORDER_IMAGES);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an",
                                                                              NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(mSelectedImage.getDescription());
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        Intent resultIntent = new Intent(this, HomeActivity.class);
        resultIntent.putExtra(Constants.IMAGE_DOWNLOADED, true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(AndroidUtil.getString(R.string.image))
                .setContentText(AndroidUtil.getString(R.string.downloading_image))
                .setDefaults(0)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        mNotificationManager.notify(new Random().nextInt(50) + 1, mNotificationBuilder.build());
//        mChannelId++;
        initRetrofit();

    }

    private void initRetrofit()
    {

        String[] a = mSelectedImage.getUrl()
                                   .split("apis.com");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(a[0] + "apis.com")
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<ResponseBody> request = retrofitInterface.downloadImage(
                a[1]);
        try
        {

            downloadImage(request.execute()
                                 .body());

        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT)
                 .show();

        }
    }

    private void scanFile(String path)
    {

        MediaScannerConnection.scanFile(getApplicationContext(),
                                        new String[] { path }, null,
                                        new MediaScannerConnection.OnScanCompletedListener()
                                        {

                                            public void onScanCompleted(String path, Uri uri)
                                            {
                                                Log.d("Tag",
                                                      "Scan finished. You can view the image in the gallery now.");
                                            }
                                        });
    }

    private void downloadImage(ResponseBody body)
            throws IOException
    {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                UIUtils.randomAlphaNumeric(12) + ".jpg");
        OutputStream outputStream = new FileOutputStream(outputFile);
        long total = 0;
        boolean downloadComplete = false;
        //int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));

        while ((count = inputStream.read(data)) != -1)
        {
            total += count;
            int progress = (int)((double)(total * 100) / (double)fileSize);


            updateNotification(progress);
            outputStream.write(data, 0, count);
            downloadComplete = true;
        }
        scanFile(outputFile.getAbsolutePath());
        onDownloadComplete(downloadComplete);
        outputStream.flush();
        outputStream.close();
        inputStream.close();

    }

    private void updateNotification(int currentProgress)
    {


        mNotificationBuilder.setProgress(100, currentProgress, false);
        mNotificationBuilder.setContentText(
                AndroidUtil.getString(R.string.dowload_progress_template, currentProgress));
        mNotificationManager.notify(0, mNotificationBuilder.build());
    }


    private void sendProgressUpdate(boolean downloadComplete)
    {

        Intent intent = new Intent(ImageSliderActivity.PROGRESS_UPDATE);
        intent.putExtra(Constants.UPLOAD_COMPLETE, downloadComplete);
        LocalBroadcastManager.getInstance(BackgroundNotificationService.this)
                             .sendBroadcast(intent);
    }

    private void onDownloadComplete(boolean downloadComplete)
    {
        sendProgressUpdate(downloadComplete);

        mNotificationManager.cancel(0);
        mNotificationBuilder.setProgress(0, 0, false);
        mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        mNotificationBuilder.setContentText(
                AndroidUtil.getString(R.string.image_download_complete));
        mNotificationManager.notify(0, mNotificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        mNotificationManager.cancel(0);
    }

}
