package com.example.editme.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import lombok.val;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.databinding.ActivityCurrentPackageBinding;


//******************************************************
public class CurrentPackageActivity
        extends AppCompatActivity
//******************************************************
{

    private ActivityCurrentPackageBinding mBinding;

    //*****************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //*****************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_current_package);
        initControls();
    }

    //******************************************************
    private void initControls()
    //******************************************************
    {
        setTab();
        setPackageDetail();

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


    //******************************************************
    private void setPackageDetail()
    //******************************************************\
    {
        val packageDetail = EditMe.instance()
                                  .getMUserDetail()
                                  .getCurrentPackage();


        mBinding.packageType.setText(packageDetail.getPackageName());
        mBinding.packagePrice.setText("" + packageDetail.getPrice());
        mBinding.totalImages.setText("" + packageDetail.getTotalImages());
        mBinding.packageDetail.setText("" + packageDetail.getPackageDescription());
        mBinding.remainingImages.setText("" + packageDetail.getRemainingImages());

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
