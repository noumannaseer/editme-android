package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.example.editme.R;
import com.example.editme.databinding.ActivityRecoveryEmailBinding;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


//**************************************************************
public class RecoveryEmailActivity
        extends AppCompatActivity
//**************************************************************
{


    private ActivityRecoveryEmailBinding mBinding;
    private String mEmail;


    //**************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //**************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recovery_email);
        intiControls();

    }


    //**************************************************************
    private void intiControls()
    //**************************************************************
    {

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.sendEmail.setOnClickListener(view -> sendRecoveryEmail());

    }


    //**************************************************************
    private void sendRecoveryEmail()
    //**************************************************************
    {
        mEmail = mBinding.email.getText()
                               .toString();


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

        gotoLoginScreen();
    }

    private void gotoLoginScreen()
    {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    //**************************************************************
    public boolean onOptionsItemSelected(MenuItem item)
    //**************************************************************
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            super.onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
