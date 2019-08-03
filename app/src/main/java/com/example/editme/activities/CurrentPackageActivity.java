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
import com.example.editme.model.PackagesDetails;
import com.example.editme.utils.AndroidUtil;


//******************************************************
public class CurrentPackageActivity
        extends AppCompatActivity
//******************************************************
{

    private ActivityCurrentPackageBinding mBinding;
    private static int mAnimationTime = 1000;
    PackagesDetails mPackageDetail;

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
        startAnimation();

    }


    int mTimeElapsed = 0;

    private void startAnimation()
    {
        AndroidUtil.handler.postDelayed(() -> {
            mTimeElapsed += 1;
            mBinding.progressBar.setProgress((mTimeElapsed * 1));
            if (mTimeElapsed < getPercentage())
            {
                startAnimation();
            }
            else
                mBinding.progressBar.setProgress(getPercentage());

        }, (mAnimationTime / 500));
    }

    private int getPercentage()
    {

        float remainingImages = 100;
        if (mPackageDetail != null)
        {
            remainingImages = mPackageDetail.getRemainingImages();
            remainingImages = remainingImages / mPackageDetail.getTotalImages();
            remainingImages = remainingImages * 100;
        }
        return (int)(remainingImages);
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
        mPackageDetail = EditMe.instance()
                               .getMUserDetail()
                               .getCurrentPackage();


        mBinding.packageType.setText(mPackageDetail.getPackageName());
        mBinding.packagePrice.setText("" + mPackageDetail.getPrice());
        mBinding.remainingImages.setText("" + mPackageDetail.getRemainingImages());
        mBinding.packageDetail.setText("" + mPackageDetail.getPackageDescription());
        mBinding.totalImages.setText(AndroidUtil.getString(R.string.total_images_template,
                                                           mPackageDetail.getTotalImages()));

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
