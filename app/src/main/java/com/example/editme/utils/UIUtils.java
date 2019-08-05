package com.example.editme.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.editme.R;
import com.example.editme.activities.HomeActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import lombok.NonNull;
import lombok.val;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static android.media.MediaRecorder.VideoSource.CAMERA;
import static com.example.editme.utils.AndroidUtil.getApplicationContext;
import static com.example.editme.utils.AndroidUtil.getResources;
import static com.example.editme.utils.Constants.CHANNEL_ID;

public class UIUtils {


    public static final String USER_REMEMBER = "USER_REMEMBER";
    public static final String USER_NAME = "USER_NAME";
    public static final String PREF_KEY_FILE_NAME = "EDITME";
    public static final String INTRO_LOADED = "INTRO_LOADED";
    public static final String CURRENT_LANGUAGE = "CURRENT_LANGUAGE";
    public static final String PACKAGE_STATUS = "PACKAGE_STATUS";


    public static void setPackageStatus(boolean status) {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PACKAGE_STATUS, status);
        editor.commit();
    }

    public static boolean getPackageStatus() {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(PACKAGE_STATUS, false);
        return value;

    }


    //****************************************************************
    public static void setCurrentLanguage(boolean status)
    //****************************************************************
    {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CURRENT_LANGUAGE, status);
        editor.commit();
    }

    //****************************************************************
    public static @Nullable
    boolean getCurrentLanguage()
    //****************************************************************
    {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(CURRENT_LANGUAGE, false);
        return value;
    }

    //****************************************************************
    public static void setUserRemember(boolean status)
    //****************************************************************
    {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_REMEMBER, status);
        editor.commit();
    }

    //****************************************************************
    public static @Nullable
    boolean getUserRemember()
    //****************************************************************
    {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(USER_REMEMBER, false);
        return value;
    }

    //****************************************************************
    public static void setUserName(String userName)
    //****************************************************************
    {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, userName);
        editor.commit();
    }


    //****************************************************************
    public static @Nullable
    String getUserName()
    //****************************************************************
    {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        String value = sharedPreferences.getString(USER_NAME, null);
        return value;
    }


    //****************************************************************
    public static @Nullable
    boolean getIntroLoaded()
    //****************************************************************
    {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(INTRO_LOADED, false);
        return value;
    }


    //****************************************************************
    public static void setIntroLoaded(boolean value)
    //****************************************************************
    {

        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREF_KEY_FILE_NAME,
                        MODE_PRIVATE);
        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(INTRO_LOADED, value);
        editor.commit();

    }

    //******************************************************************
    public static boolean isValidEmailId(String email)
    //******************************************************************
    {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|-25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")
                .matcher(email)
                .matches();
    }


    public static int getRandomNumber(int max) {
        return new Random().nextInt(max);
    }


    //************************************************
    public static void loadImages(String url, ImageView imageView, Drawable defaultImage)
    //************************************************
    {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageDrawable(defaultImage);
            return;
        } else {
            // RequestOptions myOptions = new RequestOptions()
            ///       .override(300, 200);

            Glide.with(getApplicationContext())
                    .load(url)
                    .centerCrop()
                    // .apply(myOptions)
                    .thumbnail(0.1f)
                    .placeholder(R.drawable.image_load_progress)
                    .into(imageView);
        }

    }


    //*********************************************************************
    public static void handleFailure(Throwable t, Context context)
    //*********************************************************************
    {
        UIUtils.displayAlertDialog(t.getLocalizedMessage(), AndroidUtil.getString(R.string.error),
                context);
    }


    //******************************************************************
    public static void displayAlertDialog(String message, String title, Context context)
    //******************************************************************
    {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }

    //*********************************************************************
    public static void openLinkInBrowser(@Nullable String link)
    //*********************************************************************
    {
        if (TextUtils.isEmpty(link))
            return;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(link));
        getApplicationContext()
                .startActivity(i);

    }


    //*************************************************
    public static String readFileFroRawFolder(@androidx.annotation.NonNull int rawResourceID)
    //*************************************************
    {
        InputStream inputStream = getResources()
                .openRawResource(rawResourceID);
        String jsonString = readJsonFile(inputStream);

        return jsonString;

    }

    //*************************************************
    private static String readJsonFile(InputStream inputStream)
    //*************************************************
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] bufferByte = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(bufferByte)) != -1) {
                outputStream.write(bufferByte, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }


    //******************************************************************
    public static void displayAlertDialog(String message, String title, Context context, String positive, DialogInterface.OnClickListener listerner)
    //******************************************************************
    {

        new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                //.setNegativeButton("No", listerner)
                .setPositiveButton(positive, listerner)
                .create()
                .show();
    }

    //******************************************************************
    public static void displayAlertDialog(String message, String title, Context context, String positive, String negative, DialogInterface.OnClickListener listerner)
    //******************************************************************
    {

        new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setNegativeButton(negative, listerner)
                .setPositiveButton(positive, listerner)
                .create()
                .show();
    }

    public static boolean checkNetworkConnectivity(Activity activity, View view) {
        if (AndroidUtil.isNetworkStatusAvailable())
            return true;

        else {
            Snackbar snackbar = Snackbar
                    .make(view, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.startActivity(
                                    new Intent(android.provider.Settings.ACTION_SETTINGS));
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
            return false;
        }
    }

    //**********************************************************************************************************************************
    public static void displaySingleCheckBox(@NonNull Context context
            , @NonNull List<String> itemList
            , @NonNull String title
            , @NonNull String Ok
            , @NonNull String cancel
            , int checkedItem
            , @NonNull CheckBoxSingleItemListener listener)
    //**********************************************************************************************************************************

    {

        final CharSequence[] items = itemList.toArray(new CharSequence[itemList.size()]);
        final int[] selectedIndex = {0};
        val dialog = new AlertDialog.Builder(context);
        dialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                selectedIndex[0] = which;
            }
        })
                .setTitle(title)
                .setPositiveButton(Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        if (listener == null)
                            return;
                        listener.onItemSelected(selectedIndex[0]);
                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();

    }


    public static String getPostedTime(long previousTime) {


        long timeDifference = System.currentTimeMillis() - (previousTime * 1000);
        long days = timeDifference / (1000 * 60 * 60 * 24);
        long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
        long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
        long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);

        String alert = "";
        if (days > 0) {
            alert += AndroidUtil.getString(R.string.elapsed_time_days_hour, days, hours);
        } else {
            if (hours > 0) {
//                alert += String.format("%d hours, %d minutes and %d seconds ago",
//                                       hours, minutes, seconds);
                alert += AndroidUtil.getString(R.string.elapsed_hour_minutes,
                        hours, minutes, seconds);

            } else {
                if (minutes > 0) {
                    alert += AndroidUtil.getString(R.string.elapsed_time_minutes, minutes, seconds);
                } else {
                    alert += AndroidUtil.getString(R.string.elapsed_time_secounds, seconds);
                }
            }
        }

        //     AndroidUtil.toast(false, alert);
        return alert;
    }

    public static String getRemainingTime(long previousTime) {


        long timeDifference = (previousTime * 1000) - System.currentTimeMillis();
        long days = timeDifference / (1000 * 60 * 60 * 24);
        long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
        long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
        long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);

        String alert = "";
        if (days > 0) {
            alert += AndroidUtil.getString(R.string.elapsed_time_days_hour, days);
        } else {
            if (hours > 0) {
//                alert += String.format("%d hours, %d minutes and %d seconds ago",
//                                       hours, minutes, seconds);
                alert += AndroidUtil.getString(R.string.elapsed_hour_minutes,
                        hours);

            } else {
                if (minutes > 0) {
                    alert += AndroidUtil.getString(R.string.elapsed_time_minutes, minutes, seconds);
                } else {
                    alert += AndroidUtil.getString(R.string.elapsed_time_secounds, seconds);
                }
            }
        }

        //     AndroidUtil.toast(false, alert);
        return alert;
    }

    public static Dialog displayNoInternetDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity, R.style.full_screen_alert);
        dialog.setContentView(R.layout.no_internet_dialog);
        dialog.setCancelable(false);
        Button openSetting = dialog.findViewById(R.id.opensetting);
        openSetting.setOnClickListener(view -> {
            activity.startActivity(
                    new Intent(android.provider.Settings.ACTION_SETTINGS));
            dialog.hide();
        });
        dialog.show();
        return dialog;

    }


    public static void showSnackBar(@NonNull Activity activity, @NonNull String message) {
        val view = activity.getWindow()
                .getDecorView()
                .getRootView();
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .show();
    }

    public static String getTagName(Context context) {
        return context.getClass()
                .getSimpleName();
    }

    public interface CheckBoxSingleItemListener {
        void onItemSelected(int itemIndex);
    }

    public static String getAppVersion() {
        try {
            PackageInfo pInfo = AndroidUtil.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(AndroidUtil.getApplicationContext()
                            .getPackageName(), 0);
            String version = pInfo.versionName;

            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0";
        }

    }

    //*********************************************************************
    public static String getAppVersionString()
    //*********************************************************************
    {
        try {
            PackageInfo pInfo = AndroidUtil.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(AndroidUtil.getApplicationContext()
                            .getPackageName(), 0);
            String version = pInfo.versionName;

            return AndroidUtil.getString(R.string.app_version, version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return AndroidUtil.getString(R.string.app_version, "");
        }
    }

    /**
     * Toast a message.
     *
     * @param longToast Should the toast be a long one?
     * @param message   Message to toast.
     */
    //******************************************************************
    @UiThread
    public static void testToast(boolean longToast,
                                 @NonNull String message)
    //******************************************************************
    {

        Toast
                .makeText(AndroidUtil.getApplicationContext(), message,
                        longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)
                .show();
    }


    //*********************************************************************
    public static void printKeyHash(Activity activity)
    //*********************************************************************
    {
        // Add code to print out the key hash
        try {
            PackageInfo info = activity.getPackageManager()
                    .getPackageInfo("com.example.editme",
                            PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }


    static void makeStatusNotification(String message, Context context) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME;
            String description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(Constants.NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        // Show the notification
        NotificationManagerCompat.from(context)
                .notify(Constants.NOTIFICATION_ID, builder.build());

    }

    public static String getDate(long timestamp) {
        timestamp = timestamp * 1000;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd-MM-yyyy", cal)
                .toString();
        return date;
    }

    public static String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static int getLoginType() {
        UserInfo user = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getProviderData()
                .get(1);
        //  {
        if (user.getProviderId()
                .equals("facebook.com"))
            return 0;
        else if (user.getProviderId()
                .equals("google.com"))
            return 1;
     /*   else if (user.getProviderId()
                     .equals("firebase"))
     */
        return 2;

    }
}
