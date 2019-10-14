package com.example.editme.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.editme.R;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

import androidx.appcompat.app.AppCompatActivity;

//********************************************************************
public class SplashActivity
        extends AppCompatActivity
//********************************************************************
{
    private static int SPLASH_TIME_OUT = 2000;

    //********************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //********************************************************************
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        initControls();
    }

    //********************************************************************
    private void initControls()
    //********************************************************************
    {


        gotoWelcomeScreen();
        UIUtils.printKeyHash(SplashActivity.this);

    }


    //********************************************************************
    private void gotoWelcomeScreen()
    //********************************************************************
    {
        SharedPreferences savedInstanceState = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean visit = savedInstanceState.getBoolean("visit",false);
        AndroidUtil.handler.postDelayed(() -> {
            Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            if (!visit)
            startIntroWizardActivity();
            finish();
        }, SPLASH_TIME_OUT);

    }
    //******************************************************************
    private void startIntroWizardActivity()
    //******************************************************************
    {
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("visit", true);
        editor.commit();
        editor.apply();
        Intent introWizardIntent = new Intent(this, IntroSliderActivity.class);
        startActivity(introWizardIntent);
    }

}
