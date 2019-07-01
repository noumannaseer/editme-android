package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.example.editme.R;
import com.example.editme.databinding.ActivitySignUpBinding;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

//***************************************************************
public class SignUpActivity
        extends AppCompatActivity
//***************************************************************
{

    private ActivitySignUpBinding mBinding;
    private String mName;
    private String mEmail;
    private String mPassword;

    //************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(SignUpActivity.this, R.layout.activity_sign_up);
        initControls();
    }

    //***************************************************************
    private void initControls()
    //***************************************************************
    {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBinding.btnLoginNow.setOnClickListener(view -> goToLoginScreen());
        mBinding.btnSignup.setOnClickListener(view -> signUpUser());

    }

    //***************************************************************
    private void signUpUser()
    //***************************************************************
    {

        mName = mBinding.name.getText()
                             .toString();
        mEmail = mBinding.email.getText()
                               .toString();
        mPassword = mBinding.password.getText()
                                     .toString();


        if (TextUtils.isEmpty(mName))
        {
            mBinding.name.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        if (TextUtils.isEmpty(mEmail))
        {
            mBinding.email.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        if (!UIUtils.isValidEmailId(mEmail))
        {
            mBinding.email.setError(AndroidUtil.getString(R.string.email_format));
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


        goToLoginScreen();

    }

    //************************************************
    private void goToLoginScreen()
    //************************************************
    {
        Intent loginScreen = new Intent(SignUpActivity.this, LoginActivity.class);
        loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginScreen);
    }


    //************************************************
    public boolean onCreateOptionsMenu(Menu menu)
    //************************************************
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //************************************************
    public boolean onOptionsItemSelected(MenuItem item)
    //************************************************
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
