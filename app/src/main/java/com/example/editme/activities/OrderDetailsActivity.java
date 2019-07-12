package com.example.editme.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.adapters.OrderDetailsCustomAdapter;
import com.example.editme.adapters.OrderImagesListCustomAdapter;
import com.example.editme.databinding.ActivityOrderDetailsBinding;
import com.example.editme.model.Order;
import com.example.editme.services.retrofit_image_download_service.BackgroundNotificationService;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import lombok.val;


//*********************************************************************
public class OrderDetailsActivity
        extends AppCompatActivity
        implements OrderDetailsCustomAdapter.ImageClickListener
//*********************************************************************
{


    public static final String ORDER_ID = "ORDER_ID";
    private static final int PERMISSION_REQUEST_CODE = 1220;
    private ActivityOrderDetailsBinding mBinding;
    private Order mOrder;
    public static final String SELECTED_ORDER = "SELECTED_ORDER";

    //*********************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //*********************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        initControls();
    }


    //*********************************************************************
    private void initControls()
    //*********************************************************************
    {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getParcelable();


        //    showDataOnRecyclerView();
        //   mBinding.viewImages.setOnClickListener(view -> gotoSliderScreen());
        mBinding.modifyOrder.setOnClickListener(view -> gotoPlaceOrderScreen());
        mBinding.completeOrder.setOnClickListener(view -> completeOrder());
        mBinding.downloadImages.setOnClickListener(view -> {
            if (!checkPermission())
            {
                requestPermission();
                return;

            }
            for (int i = 0; i < mOrder.getImages()
                                      .size(); i++)
            {

                startImageDownload(i);
            }
            super.onBackPressed();
        });
        if (mOrder != null)
        {
            setData();
            showImagesOnRecyclerView();
        }
    }

    //*************************************************************************************
    private void showImagesOnRecyclerView()
    //*************************************************************************************
    {
        OrderDetailsCustomAdapter orderDetailsCustomAdapter = new OrderDetailsCustomAdapter(
                mOrder.getImages(), this, this);
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mBinding.recyclerView.setAdapter(orderDetailsCustomAdapter);
    }

    //*************************************************************************************
    private void completeOrder()
    //*************************************************************************************
    {

        showProgressView();
        mOrder.setStatus(Constants.STATUS_COMPLETE);
        EditMe.instance()
              .getMFireStore()
              .collection(Constants.ORDERS)
              .document(mOrder.getOrderId())
              .set(mOrder)
              .addOnCompleteListener(new OnCompleteListener<Void>()
              {
                  @Override
                  public void onComplete(@NonNull Task<Void> task)
                  {
                      gotoBack();
                      hideProgressView();
                  }
              });
    }

    //*************************************************************************************
    private void gotoPlaceOrderScreen()
    //*************************************************************************************
    {
        Intent placeOrderIntent = new Intent(this, PlaceOrderActivity.class);
        placeOrderIntent.putExtra(PlaceOrderActivity.SELECTED_ORDER, mOrder);
        startActivityForResult(placeOrderIntent, 0);
    }

    //*************************************************************************************
    private void gotoSliderScreen(int index)
    //*************************************************************************************
    {
        Intent slidingImagesIntent = new Intent(this, ImageSliderActivity.class);
        slidingImagesIntent.putExtra(ImageSliderActivity.SELECTED_ORDER, mOrder);
        slidingImagesIntent.putExtra(ImageSliderActivity.SELECTED_INDEX, index);
        startActivity(slidingImagesIntent);
    }

    //*************************************************************************************
    private void showDataOnRecyclerView()
    //*************************************************************************************
    {
        if (mOrder == null)
            return;
        OrderImagesListCustomAdapter orderImagesListCustomAdapter = new OrderImagesListCustomAdapter(
                mOrder.getImages(), null);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(orderImagesListCustomAdapter);

    }

    //*************************************************************************************
    private void setData()
    //*************************************************************************************
    {
        if (mOrder == null)
            return;
        mBinding.orderDescription.setText(mOrder.getDescription());
        mBinding.orderDate.setText(UIUtils.getDate(mOrder.getOrderDate()
                                                         .getSeconds()));
        mBinding.remainingTime.setText(UIUtils.getRemainingTime(mOrder.getDueDate()
                                                                      .getSeconds()));
        mBinding.status.setText(mOrder.getStatus());
        mBinding.totalImages.setText(AndroidUtil.getString(R.plurals.photo_count_template,
                                                           mOrder.getImages()
                                                                 .size()));

    }

    //*************************************************************************************
    private void getParcelable()
    //*************************************************************************************
    {
        if (getIntent().getExtras()
                       .containsKey(SELECTED_ORDER))
        {
            mOrder = getIntent().getExtras()
                                .getParcelable(SELECTED_ORDER);
            if (mOrder.getStatus()
                      .equals(Constants.STATUS_COMPLETE))
            {
                mBinding.downloadImages.setVisibility(View.VISIBLE);
                mBinding.modifyOrder.setVisibility(View.GONE);
                mBinding.completeOrder.setVisibility(View.GONE);
            }

        }
        else if (getIntent().getExtras()
                            .containsKey(ORDER_ID))
        {
            val orderId = getIntent().getExtras()
                                     .getString(ORDER_ID);
            EditMe.instance()
                  .getMFireStore()
                  .collection(Constants.ORDERS)
                  .document(orderId)
                  .get()
                  .addOnSuccessListener(
                          new OnSuccessListener<DocumentSnapshot>()
                          {
                              @Override
                              public void onSuccess(DocumentSnapshot documentSnapshot)
                              {
                                  mOrder = documentSnapshot.toObject(Order.class);
                                  setData();
                                  showImagesOnRecyclerView();
                              }
                          });
        }
    }

    //*************************************************************************************
    private void startImageDownload(int index)
    //*************************************************************************************
    {


        Intent intent = new Intent(this, BackgroundNotificationService.class);
        intent.putExtra(BackgroundNotificationService.ORDER_IMAGES, mOrder.getImages()
                                                                          .get(index));
        startService(intent);

    }

    //*************************************************************************************
    private boolean checkPermission()
    //*************************************************************************************
    {
        int result = ContextCompat.checkSelfPermission(this,
                                                       Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //*************************************************************************************
    private void requestPermission()
    //*************************************************************************************
    {

        ActivityCompat.requestPermissions(this,
                                          new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                          PERMISSION_REQUEST_CODE);

    }

    //*************************************************************************************
    private void gotoBack()
    //*************************************************************************************
    {
        Intent intentData = new Intent();
        intentData.putExtra(Constants.ORDER_UPDATED, true);
        setResult(RESULT_OK, intentData);
        finish();
    }


    //*************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    //*************************************************************************************
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getExtras()
                                .containsKey(Constants.ORDER_UPDATED))
        {
            gotoBack();
        }

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

    //*****************************************
    private void showProgressView()
    //*****************************************
    {
        mBinding.progressView.setVisibility(View.VISIBLE);

    }

    //*****************************************
    private void hideProgressView()
    //*****************************************
    {
        mBinding.progressView.setVisibility(View.GONE);

    }

    //*************************************************************************************
    @Override
    public void onImageClick(int index)
    //*************************************************************************************
    {
        gotoSliderScreen(index);

    }

    //*************************************************************************************
    @Override
    public void onImageDelete(int index)
    //*************************************************************************************
    {

    }
}
