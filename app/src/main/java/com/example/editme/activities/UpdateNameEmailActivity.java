package com.example.editme.activities;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;

import com.example.editme.R;
import com.example.editme.databinding.ActivityUpdateNameEmailBinding;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import lombok.val;


//****************************************************************************
public class UpdateNameEmailActivity
        extends AppCompatActivity
//****************************************************************************
{

    private ActivityUpdateNameEmailBinding mBinding;
    public static final String UPDATE_FIELD = "UPDATE_FIELD";
    private boolean mIsEmailUpdate;

    //****************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //****************************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_name_email);
        initControls();
    }

    //****************************************************************************
    private void initControls()
    //****************************************************************************
    {
        getParcelable();
        if (mIsEmailUpdate)
        {
            mBinding.updateValue.setHint(AndroidUtil.getString(R.string.new_email));
            mBinding.toolbarTitle.setText(AndroidUtil.getString(R.string.update_email));
            mBinding.update.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        mBinding.update.setOnClickListener(view -> updateField());

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

    //****************************************************************************
    private void updateField()
    //****************************************************************************
    {
        val fieldData = mBinding.updateValue.getText()
                                            .toString();
        if (TextUtils.isEmpty(fieldData))
        {
            mBinding.updateValue.setError(AndroidUtil.getString(R.string.required));
            return;
        }
        else if (!UIUtils.isValidEmailId(fieldData) && mIsEmailUpdate)
        {
            mBinding.updateValue.setError(AndroidUtil.getString(R.string.incorrect_email_format));
            return;

        }
        if (mIsEmailUpdate)
            updateEmail();
        else
            updateName();
    }


    //****************************************************************************
    private void updateName()
    //****************************************************************************
    {
        UIUtils.testToast(false, AndroidUtil.getString(R.string.name_updated));
        finish();

    }

    private void updateEmail()
    {
        UIUtils.testToast(false, AndroidUtil.getString(R.string.email_updated));
        finish();

    }


    //****************************************************************************
    private void getParcelable()
    //****************************************************************************
    {
        if (getIntent().getExtras()
                       .containsKey(UPDATE_FIELD))
        {
            mIsEmailUpdate = getIntent().getExtras()
                                        .getBoolean(UPDATE_FIELD);

        }

    }
}
