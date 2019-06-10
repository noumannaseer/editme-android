package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.editme.R;
import com.example.editme.databinding.ActivityWelcomeScreenBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


//********************************************************************
public class WelcomeActivity
        extends AppCompatActivity
//********************************************************************
{

    private ActivityWelcomeScreenBinding mBinding;

    //********************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //********************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_screen);
        initControls();
    }

    //********************************************************************
    private void initControls()
    //********************************************************************
    {
        mBinding.letsStart.setOnClickListener(view -> gotoSignUpScreen());
    }

    //********************************************************************
    private void gotoSignUpScreen()
    //********************************************************************
    {
        Intent signUpIntent = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(signUpIntent);
        finish();

    }
}
