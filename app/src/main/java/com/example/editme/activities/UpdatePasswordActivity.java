package com.example.editme.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.example.editme.R;
import com.example.editme.databinding.ActivityUpdatePasswordBinding;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import lombok.val;


//*****************************************************************
public class UpdatePasswordActivity
        extends AppCompatActivity
//*****************************************************************
{


    private ActivityUpdatePasswordBinding mBinding;

    //*****************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //*****************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_password);
        initControls();

    }

    //*****************************************************************
    private void initControls()
    //*****************************************************************
    {
        setTab();
        mBinding.update.setOnClickListener(view -> updatePassword());

    }

    //*****************************************************************
    private void updatePassword()
    //*****************************************************************
    {
        val oldPassword = mBinding.oldPassword.getText()
                                              .toString();
        val newPassword = mBinding.newPassword.getText()
                                              .toString();
        val confirmPassword = mBinding.confirmNewPassword.getText()
                                                         .toString();
        if (TextUtils.isEmpty(oldPassword))
        {
            mBinding.oldPassword.setError(AndroidUtil.getString(R.string.required));
            return;
        }
        if (oldPassword.length() < 8)
        {
            mBinding.oldPassword.setError(AndroidUtil.getString(R.string.minimum_8_character));
            return;
        }

        if (TextUtils.isEmpty(newPassword))
        {
            mBinding.newPassword.setError(AndroidUtil.getString(R.string.required));
            return;
        }
        if (newPassword.length() < 8)
        {
            mBinding.newPassword.setError(AndroidUtil.getString(R.string.minimum_8_character));
            return;
        }
        if (TextUtils.isEmpty(confirmPassword))
        {
            mBinding.confirmNewPassword.setError(AndroidUtil.getString(R.string.required));
            return;
        }
        if (confirmPassword.length() < 8)
        {
            mBinding.confirmNewPassword.setError(
                    AndroidUtil.getString(R.string.minimum_8_character));
            return;
        }
        if (!newPassword.equals(confirmPassword))
        {
            mBinding.confirmNewPassword.setError(
                    AndroidUtil.getString(R.string.password_not_match));
            return;

        }

        UIUtils.testToast(false, "Password updated");
        finish();
    }

    //******************************************************************
    private void setTab()
    //******************************************************************
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //******************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    //******************************************************************
    {
        if (item.getItemId() == android.R.id.home)
        {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
