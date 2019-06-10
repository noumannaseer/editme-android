package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.editme.R;
import com.example.editme.databinding.ActivitySignUpBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

//***************************************************************
public class SignUpActivity
        extends AppCompatActivity
//***************************************************************
{

    private ActivitySignUpBinding mBinding;

    //************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
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

    }

    //************************************************
    private void goToLoginScreen()
    //************************************************
    {
        Intent loginScreen = new Intent(SignUpActivity.this, LoginActivity.class);
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
