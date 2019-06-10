package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.editme.R;
import com.example.editme.databinding.ActivityLoginBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;


//********************************************************************
public class LoginActivity
        extends AppCompatActivity
//********************************************************************
{


    ActivityLoginBinding mBinding;

    //********************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //********************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initControls();

    }

    private void initControls()
    {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void gotosignup()
    {
        Intent SignUPScreen = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(SignUPScreen);
    }

    private void gotoRecoveryscreen()
    {
        Intent recoveryScreen = new Intent(LoginActivity.this, RecoveryEmailActivity.class);
        startActivity(recoveryScreen);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}

