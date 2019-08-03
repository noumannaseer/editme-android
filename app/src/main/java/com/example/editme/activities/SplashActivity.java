package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.fragments.HomeFragment;
import com.example.editme.utils.AndroidUtil;

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


        //gotoWelcomeScreen();

    }


    //********************************************************************
    private void gotoWelcomeScreen()
    //********************************************************************
    {
        AndroidUtil.handler.postDelayed(() -> {
            Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }, SPLASH_TIME_OUT);

    }

}
