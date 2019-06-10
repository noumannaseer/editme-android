package com.example.editme.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.editme.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import lombok.NonNull;
import lombok.val;

import static android.content.Context.MODE_PRIVATE;
import static com.example.editme.utils.AndroidUtil.getApplicationContext;
import static com.example.editme.utils.AndroidUtil.getResources;

public class UIUtils
{


    public static final String USER_REMEMBER = "USER_REMEMBER";
    public static final String USER_NAME = "USER_NAME";
    public static final String PREF_KEY_FILE_NAME = "messages39";
    public static final String INTRO_LOADED = "INTRO_LOADED";
    public static final String CURRENT_LANGUAGE = "CURRENT_LANGUAGE";


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


    public static int getRandomNumber(int max)
    {
        return new Random().nextInt(max);
    }


    //************************************************
    public static void loadImages(String url, ImageView imageView, Drawable defaultImage)
    //************************************************
    {
        if (TextUtils.isEmpty(url))
        {
            imageView.setImageDrawable(defaultImage);
            return;
        }

        else
        {
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

        byte bufferByte[] = new byte[1024];
        int length;
        try
        {
            while ((length = inputStream.read(bufferByte)) != -1)
            {
                outputStream.write(bufferByte, 0, length);
            }
            outputStream.close();
            inputStream.close();
        }
        catch (IOException e)
        {

        }
        return outputStream.toString();
    }



    //******************************************************************
    public static void displayAlertDialog(String message, String title, Context context, DialogInterface.OnClickListener listerner, String positive)
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
    public static void displayAlertDialog(String message, String title, Context context, DialogInterface.OnClickListener listerner, String positive, String negative)
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

    public static boolean checkNetworkConnectivity(Activity activity, View view)
    {
        if (AndroidUtil.isNetworkStatusAvailable())
            return true;

        else
        {
            Snackbar snackbar = Snackbar
                    .make(view, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("Settings", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
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
        final int[] selectedIndex = { 0 };
        val dialog = new AlertDialog.Builder(context);
        dialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                selectedIndex[0] = which;
            }
        })
              .setTitle(title)
              .setPositiveButton(Ok, new DialogInterface.OnClickListener()
              {
                  public void onClick(DialogInterface dialog, int whichButton)
                  {
                      dialog.dismiss();
                      if (listener == null)
                          return;
                      listener.onItemSelected(selectedIndex[0]);
                  }
              })
              .setNegativeButton(cancel, new DialogInterface.OnClickListener()
              {
                  @Override
                  public void onClick(DialogInterface dialog, int which)
                  {
                      dialog.dismiss();
                  }
              });
        dialog.show();

    }




    public static String getPostedTime(long previousTime)
    {


        long timeDifference = System.currentTimeMillis() - (previousTime * 1000);
        long days = timeDifference / (1000 * 60 * 60 * 24);
        long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
        long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
        long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);

        String alert = "";
        if (days > 0)
        {
            alert += AndroidUtil.getString(R.string.elapsed_time_days_hour, days, hours);
        }
        else
        {
            if (hours > 0)
            {
//                alert += String.format("%d hours, %d minutes and %d seconds ago",
//                                       hours, minutes, seconds);
                alert += AndroidUtil.getString(R.string.elapsed_hour_minutes,
                                               hours, minutes, seconds);

            }
            else
            {
                if (minutes > 0)
                {
                    alert += AndroidUtil.getString(R.string.elapsed_time_minutes, minutes, seconds);
                }
                else
                {
                    alert += AndroidUtil.getString(R.string.elapsed_time_secounds, seconds);
                }
            }
        }

        //     AndroidUtil.toast(false, alert);
        return alert;
    }


    public static Dialog displayNoInternetDialog(Activity activity)
    {

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


    public static void showSnackBar(@NonNull View view, @NonNull String message)
    {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .show();
    }

    public interface CheckBoxSingleItemListener
    {
        void onItemSelected(int itemIndex);
    }

}
