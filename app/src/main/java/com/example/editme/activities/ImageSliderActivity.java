package com.example.editme.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.editme.R;
import com.example.editme.adapters.SlidingImageAdapter;
import com.example.editme.databinding.ActivityImageSliderBinding;
import com.example.editme.model.Order;
import com.example.editme.services.retrofit_image_download_service.BackgroundNotificationService;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import lombok.NonNull;
import lombok.val;

//********************************************************
public class ImageSliderActivity
        extends AppCompatActivity
//********************************************************

{

    private static int mCurrentPage = 0;
    private static int mTotalPage = 0;
    private boolean mIsPressed = false;
    private ActivityImageSliderBinding mBinding;
    public static final String SELECTED_ORDER = "SELECTED_ORDER";
    private Order mOrder;
    public static final String PROGRESS_UPDATE = "progress_update";
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static final String SELECTED_INDEX = "SELECTED_INDEX";


    //********************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //********************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_slider);
        initControls();
        registerReceiver();

    }

    //********************************************************
    private void initControls()
    //********************************************************
    {
        getParcelable();
        String[] stringArray = new String[getImagesUrls().size()];
        getImagesUrls().toArray(stringArray);
        startImageSlider(stringArray);
    }


    //********************************************************
    List<String> getImagesUrls()
    //********************************************************
    {
        List<String> urlList = new ArrayList<>();
        for (val images : mOrder.getImages())
        {
            urlList.add(images.getUrl());
        }
        return urlList;
    }

    //**************************************************************
    private void getParcelable()
    //**************************************************************
    {
        if (getIntent().getExtras()
                       .containsKey(SELECTED_ORDER))
        {
            mOrder = getIntent().getParcelableExtra(SELECTED_ORDER);
            mCurrentPage = getIntent().getExtras()
                                      .getInt(SELECTED_INDEX);
        }

    }

    //*********************************************************************
    private void startImageSlider(@NonNull String[] urls)
    //*********************************************************************
    {

        mBinding.pager.setAdapter(new SlidingImageAdapter(this, urls,
                                                          new SlidingImageAdapter.SliderTouchListener()
                                                          {
                                                              @Override
                                                              public void onPressed()
                                                              {
                                                                  mIsPressed = true;
                                                              }

                                                              @Override
                                                              public void onModifyClick(int index)
                                                              {
                                                              }

                                                              @Override
                                                              public void onDownloadClick(int index)
                                                              {
                                                                  if (checkPermission())
                                                                      startImageDownload(index);
                                                                  else
                                                                      requestPermission();
                                                              }

                                                              @Override
                                                              public void onRelease()
                                                              {
                                                                  mIsPressed = false;
                                                              }
                                                          }));

        mBinding.indicator.setViewPager(mBinding.pager);

        final float density = getResources().getDisplayMetrics().density;

        mBinding.indicator.setRadius(5 * density);
        mTotalPage = urls.length;
        final Handler handler = new Handler();
        final Runnable Update = new Runnable()
        {
            public void run()
            {
                if (mCurrentPage == mTotalPage)
                {
                    mCurrentPage = 0;
                }
                if (!mIsPressed)
                    mBinding.pager.setCurrentItem(mCurrentPage++, true);
            }
        };


        // Pager listener over indicator
        mBinding.pager.setOnClickListener(view -> {
            AndroidUtil.toast(false, "pager click");
        });
        mBinding.indicator.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener()
                {

                    @Override
                    public void onPageSelected(int position)
                    {
                        mCurrentPage = position;

                    }

                    @Override
                    public void onPageScrolled(int pos, float arg1, int arg2)
                    {

                    }

                    @Override
                    public void onPageScrollStateChanged(int pos)
                    {

                    }
                });
    }

    //*********************************************************************
    private void registerReceiver()
    //*********************************************************************
    {

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PROGRESS_UPDATE);
        bManager.registerReceiver(mBroadcastReceiver, intentFilter);

    }

    //*********************************************************************
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
            //*********************************************************************
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            if (intent.getAction()
                      .equals(PROGRESS_UPDATE))
            {

                boolean downloadComplete = intent.getBooleanExtra(Constants.DOWNLOAD_COMPLETE,
                                                                  false);

                if (downloadComplete)
                {
                }
            }
        }
    };


    //*********************************************************************
    private boolean checkPermission()
    //*********************************************************************
    {
        int result = ContextCompat.checkSelfPermission(this,
                                                       Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //*********************************************************************
    private void startImageDownload(int index)
    //*********************************************************************
    {


        Intent intent = new Intent(this, BackgroundNotificationService.class);
        intent.putExtra(BackgroundNotificationService.ORDER_IMAGES, mOrder.getImages()
                                                                          .get(index));
        startService(intent);

    }

    //*********************************************************************
    private void requestPermission()
    //*********************************************************************
    {

        ActivityCompat.requestPermissions(this,
                                          new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                          PERMISSION_REQUEST_CODE);

    }

    //**************************************************************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    //**************************************************************************************************
    {
        switch (requestCode)
        {
        case PERMISSION_REQUEST_CODE:
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
//                startImageDownload();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT)
                     .show();
            }
            break;
        }
    }

}
