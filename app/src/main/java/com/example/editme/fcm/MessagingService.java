package com.example.editme.fcm;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.activities.HomeActivity;
import com.example.editme.model.Notifications;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;
import lombok.val;

/*
 * Created by Mahmoud on 3/13/2017.
 */


public class MessagingService
        extends FirebaseMessagingService
{


    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {

        Log.d("notification_system", "sa");
        System.out.println("notify");
        // new RemoteMessageNotifier(getApplicationContext()).notify(remoteMessage);
        // Toast.makeText(new Application().getApplicationContext(), "sasasa", Toast.LENGTH_SHORT);
        //todo: handle notification
        // sendNotification(remoteMessage);
        /*
        Intent dialogIntent = new Intent(this, HomeActivity.class);
        dialogIntent.putExtra(HomeActivity.NOTIFICATION_CALLED, true);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
        */
        //AndroidUtil.toast(false, "notfication recived");
        // AndroidUtil.toast(false, "notifcation called");
        // showNotification();
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        val userID = EditMe.instance()
                           .getMUserId();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(HomeActivity.SELECTED_NOTIFICATION,
                        new Notifications(notification.getTitle(), notification.getBody(),
                                          remoteMessage.getData()
                                                       .get("orderId")));
        sendNotifaication2(this, notification.getTitle(), notification.getBody(),
                           intent);

    }


    private void sendNotifaication2(Context context, String title, String body, Intent intent)
    {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(
                Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01sas";
        String channelName = "Channel Namesasasasasasa";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());
    }

/*
    private void sendNotification(RemoteMessage remoteMessage)
    {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                                                                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                                           R.drawable.ic_launcher_background))
                .setSmallIcon(R.drawable.ic_access_time_black_24dp)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }*/

}
