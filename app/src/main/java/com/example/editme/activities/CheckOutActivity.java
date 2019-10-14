package com.example.editme.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.adapters.AddImagesCustomAdapter;
import com.example.editme.databinding.ActivityCheckoutBinding;
import com.example.editme.model.EditImage;
import com.example.editme.model.Order;
import com.example.editme.model.OrderImages;
import com.example.editme.services.upload_services.UploadImagesService;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.example.editme.worker.UploadImageWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import lombok.val;


//******************************************************************
public class CheckOutActivity
        extends AppCompatActivity
        implements AddImagesCustomAdapter.ImageClickListener
//******************************************************************
{
    private ActivityCheckoutBinding mBinding;
    public static final String IMAGES_LIST = "IMAGES_LIST";
    public static final String ORDER_DESCRIPTION = "ORDER_DESCRIPTION";
    private List<EditImage> mEditImageList;
    private String mOrderDescription;
    private AddImagesCustomAdapter mAddImagesCustomAdapter;
    private List<Order> mOrderList;
    private List<OrderImages> mOrderImageList;
    private StorageReference mStorage;
    private Date mDueDate;
    private String mDueDateString;

    public static String PROGRESS_UPDATE = "PROGRESS_UPDATE";
    public static String DUE_DATE = "DUE_DATE";
    public static String DUE_DATE_STRING = "DUE_DATE_STRING";


    //******************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //******************************************************************

    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);
        initControls();

    }

    //******************************************************************
    private void initControls()
    //******************************************************************
    {
        getParcelable();
        setTab();
        mBinding.placeOrder.setOnClickListener(view -> uploadImages());
//        mBinding.deliveryTimeLayout.setOnClickListener(view -> showCalendar(mBinding.deliveryTime));

        if (mEditImageList != null)
        {
            showDataOnRecyclerView();
            mBinding.remainingImages.setText("" + (EditMe.instance()
                                                         .getMUserDetail()
                                                         .getCurrentPackage()
                                                         .getRemainingImages() - mEditImageList.size()));
            mBinding.description.setText("" + mOrderDescription);
        }
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


    List<OneTimeWorkRequest> mOneTimeWorkRequestsList;

    //******************************************************************
    private void uploadImages()
    //******************************************************************
    {
        if (mDueDate == null)
        {
            UIUtils.testToast(false, "Please select due date");
            return;
        }
        mStorage = EditMe.instance()
                         .getMStorageReference()
                         .getReference();
        val userId = EditMe.instance()
                           .getMUserId();
        mOrderImageList = new ArrayList<>();
        val orderId = UUID.randomUUID()
                          .toString();

        mOneTimeWorkRequestsList = new ArrayList<>();
        for (int i = 0; i < mEditImageList.size(); i++)
        {
            mEditImageList.get(i)
                          .setUploading(0);
            mAddImagesCustomAdapter.notifyItemChanged(i);
            startImageDownload(i);

        }

    }

    //*************************************************************************************
    private void startImageDownload(int index)
    //*************************************************************************************
    {


        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(
                UploadImageWorker.class).setInputData(createInputDataForUri(index))
                                        .build();
        mOneTimeWorkRequestsList.add(uploadWorkRequest);
        WorkManager.getInstance()
                   .enqueue(uploadWorkRequest);

        WorkManager.getInstance()
                   .getWorkInfoByIdLiveData(uploadWorkRequest.getId())
                   .observe(this, workInfo -> {
                       if (workInfo.getState() == WorkInfo.State.SUCCEEDED)
                           try
                           {


                               val imageId = workInfo.getOutputData()
                                                     .getInt(UploadImageWorker.ORDER_IMAGES_ID, -1);
                               val imageUrl = workInfo.getOutputData()
                                                      .getString(UploadImageWorker.IMAGE_URL);

                               Log.d("worker_info", imageId + " " + workInfo.getOutputData()
                                                                            .getString(
                                                                                    UploadImageWorker.IMAGE_URL));
                               mOrderImageList.add(
                                       new OrderImages(
                                               mEditImageList.get(
                                                       imageId)
                                                             .getDescription(),
                                               imageUrl, ""));
                               mEditImageList.get(imageId)
                                             .setUploading(1);
                               mAddImagesCustomAdapter.notifyItemChanged(
                                       imageId);
                               if (mEditImageList.size() == mOrderImageList.size())
                               {

                                   val orderID = UUID.randomUUID()
                                                     .toString();
                                   saveDataToFireStore(orderID);
                               }

                           }
                           catch (Exception w)
                           {

                           }

                   });
    }

    //***************************************************************************
    private Data createInputDataForUri(int index)
    //***************************************************************************
    {
        Data.Builder builder = new Data.Builder();
        builder.putInt(UploadImageWorker.ORDER_IMAGES_ID, index);
        builder.putString(UploadImageWorker.ORDER_IMAGES_URI, mEditImageList.get(index)
                                                                            .getImageIntentURI()
                                                                            .toString());
        return builder.build();
    }

    //******************************************************************
    private void saveDataToFireStore(String orderId)
    //******************************************************************
    {


        val order = new Order(EditMe.instance()
                                    .getMAuth()
                                    .getCurrentUser()
                                    .getEmail(),
                              "Test title",
                              Constants.STATUS_PENDING,
                              mOrderDescription,
                              new Timestamp(new Date()),
                              new Timestamp(mDueDate),
                              mOrderImageList,
                              "" + orderId, EditMe.instance()
                                                  .getMUserId(), 0);
        EditMe.instance()
              .getMFireStore()
              .collection(Constants.ORDERS)
              .document(orderId)
              .set(order)
              .addOnCompleteListener(
                      new OnCompleteListener<Void>()
                      {
                          @Override
                          public void onComplete(@androidx.annotation.NonNull Task<Void> task)
                          {
                              if (task.isSuccessful())
                              {


                                  if (EditMe.instance()
                                            .getMUserDetail()
                                            .getCurrentPackage()
                                            .getRemainingImages() - mEditImageList.size() == 0)
                                  {
                                      EditMe.instance()
                                            .getMUserDetail()
                                            .getCurrentPackage()
                                            .setRemainingImages(0);
                                      updateFirebase("currentPackage",
                                                     null);
                                  }
                                  else
                                  {
                                      EditMe.instance()
                                            .getMUserDetail()
                                            .getCurrentPackage()
                                            .setRemainingImages(EditMe.instance()
                                                                      .getMUserDetail()
                                                                      .getCurrentPackage()
                                                                      .getRemainingImages() - mEditImageList.size());
                                      updateFirebase("currentPackage.remainingImages",
                                                     FieldValue.increment(-mEditImageList.size()));
                                  }

                              }
                          }
                      });
    }

    private void updateFirebase(String field, FieldValue s1)
    {
        EditMe.instance()
              .getMFireStore()
              .collection(Constants.Users)
              .document(EditMe.instance()
                              .getMUserId())
              .update(field, s1)
              .addOnCompleteListener(
                      new OnCompleteListener<Void>()
                      {
                          @Override
                          public void onComplete(@NonNull Task<Void> task)
                          {

                              UIUtils.testToast(false, "Order posted");
                              gotoBack();
                          }
                      });
    }

    //******************************************************************
    private void getParcelable()
    //******************************************************************
    {
        if (getIntent().getExtras()
                       .containsKey(IMAGES_LIST) && getIntent().getExtras()
                                                               .containsKey(ORDER_DESCRIPTION))
        {
            mOrderDescription = getIntent().getExtras()
                                           .getString(ORDER_DESCRIPTION);
            mEditImageList = getIntent().getExtras()
                                        .getParcelableArrayList(IMAGES_LIST);

            mDueDate = new Date(getIntent().getLongExtra(DUE_DATE, 0));
            mBinding.totalImages.setText("" + mEditImageList.size());
            mBinding.deliveryTime.setText(getIntent().getExtras()
                                                     .getString(DUE_DATE_STRING));
        }

    }

    //******************************************************************
    private void showDataOnRecyclerView()
    //******************************************************************
    {


        mAddImagesCustomAdapter = new AddImagesCustomAdapter(mEditImageList,
                                                             this, false, this, true);
        /*mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        */
        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(mAddImagesCustomAdapter);

    }

    //******************************************************************
    private void gotoBack()
    //******************************************************************
    {
        Intent intentData = new Intent();
        intentData.putExtra(Constants.ORDER_UPDATED, true);
        setResult(RESULT_OK, intentData);
        finish();
    }

    //******************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    //******************************************************************
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    //******************************************************************
    @Override
    public void onImageClick(int index)
    //******************************************************************
    {

    }

    //******************************************************************
    @Override
    public void onImageDelete(int index)
    //******************************************************************
    {

    }
}
