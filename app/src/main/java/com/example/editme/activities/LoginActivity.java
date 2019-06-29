package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.example.editme.R;
import com.example.editme.databinding.ActivityLoginBinding;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


//********************************************************************
public class LoginActivity
        extends AppCompatActivity
//********************************************************************
{


    private ActivityLoginBinding mBinding;
    private String mEmail;
    private String mPassword;

    //********************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //********************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initControls();

    }

    //**************************************************************
    private void initControls()
    //**************************************************************
    {

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBinding.btnSignupNow.setOnClickListener(view -> gotoSignUpScreen());
        mBinding.login.setOnClickListener(view -> loginUser());
        mBinding.forgotPassword.setOnClickListener(view -> gotoForgotPasswordScreen());
    }

    //**************************************************************
    private void loginUser()
    //**************************************************************
    {

        mEmail = mBinding.loginEmail.getText()
                                    .toString();
        mPassword = mBinding.password.getText()
                                     .toString();


        if (TextUtils.isEmpty(mEmail))
        {
            mBinding.loginEmail.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        if (!UIUtils.isValidEmailId(mEmail))
        {
            mBinding.loginEmail.setError(AndroidUtil.getString(R.string.email_format));
            return;
        }

        if (TextUtils.isEmpty(mPassword))
        {
            mBinding.password.setError(AndroidUtil.getString(R.string.required));
            return;
        }


        if (mPassword.length() < 8)
        {
            mBinding.password.setError(AndroidUtil.getString(R.string.password_length));
            return;
        }


        gotoHomeScreen();

    }

    //**************************************************************
    private void gotoHomeScreen()
    //**************************************************************
    {

        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);

    }

    //**************************************************************
    private void gotoSignUpScreen()
    //**************************************************************
    {
        Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        signUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signUpIntent);
        finish();
    }


    //**************************************************************
    private void gotoForgotPasswordScreen()
    //**************************************************************
    {
        Intent recoveryScreen = new Intent(LoginActivity.this, RecoveryEmailActivity.class);
        startActivity(recoveryScreen);
    }


    //**************************************************************
    public boolean onOptionsItemSelected(MenuItem item)
    //**************************************************************
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

