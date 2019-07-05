package com.example.editme.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import lombok.NonNull;
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
    public static String PROGRESS_UPDATE = "PROGRESS_UPDATE";

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
        mBinding.placeOrder.setOnClickListener(view -> uploadImages());
        mBinding.deliveryTimeLayout.setOnClickListener(view -> showCalendar(mBinding.deliveryTime));

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

    //*****************************************************************
    public void showCalendar(@NonNull TextView textView)
    //*****************************************************************
    {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.calender_dialog);
        CalendarView calender = dialog.findViewById(R.id.calendarView1);
        Button select = dialog.findViewById(R.id.select_date);
        Button cancel = dialog.findViewById(R.id.cancel);

        select.setOnClickListener(view -> dialog.dismiss());

        cancel.setOnClickListener(view -> dialog.dismiss());

        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth)
            {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                mDueDate = calendar.getTime();

                String startDate = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance()
                                                                                     .getTime());
                val endDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                getDaysBetweenDates(startDate, endDate);

            }
        });
        dialog.show();
    }


    //**********************************************************************************
    private void getDaysBetweenDates(String start, String end)
    //**********************************************************************************
    {
        if (start.equals(
                AndroidUtil.getString(R.string.standard_date_format)) || end.equals(
                AndroidUtil.getString(R.string.standard_date_format)))
        {
            //  AndroidUtil.toast(false, "Please select valid order of date");
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try
        {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        if (numberOfDays > 1)
        {
            mBinding.deliveryTime.setText(
                    "" + (int)numberOfDays + AndroidUtil.getString(R.string.days4));
        }
        else
        {
            mBinding.deliveryTime.setText("");
            AndroidUtil.toast(false,
                              AndroidUtil.getString(R.string.please_select_a_valid_date_order));
            mDueDate = null;

        }

    }

    //**********************************************************************************
    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit)
    //**********************************************************************************
    {
        long timeDiff =
                endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
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

                                  val packagesDetails = EditMe.instance()
                                                              .getMUserDetail()
                                                              .getCurrentPackage();
                                  packagesDetails.decrement(mEditImageList.size());
                                  val userId = EditMe.instance()
                                                     .getMUserId();

                                  EditMe.instance()
                                        .getMFireStore()
                                        .collection(Constants.Users)
                                        .document(userId)
                                        .update("currentPackage", packagesDetails)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Void>()
                                                {
                                                    @Override
                                                    public void onSuccess(Void aVoid)
                                                    {
                                                        UIUtils.testToast(false, "Order posted");
                                                        gotoBack();
                                                    }
                                                });

                              }
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
            mBinding.totalImages.setText("" + mEditImageList.size());
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
