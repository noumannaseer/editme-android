package com.example.editme.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import androidx.recyclerview.widget.LinearLayoutManager;
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

    //******************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //******************************************************************

    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);
        initControls();
        if (mEditImageList != null)
        {
            showDataOnRecyclerView();
            mBinding.description.setText("" + mOrderDescription);
        }
    }

    //******************************************************************
    private void initControls()
    //******************************************************************
    {
        getParcelable();
        mBinding.placeOrder.setOnClickListener(view -> uploadImages());
        mBinding.deliveryTimeLayout.setOnClickListener(view -> {
            showCalendar(mBinding.deliveryTime);
        });

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
//                val startDate = currentDate.getDay() + "/" + currentDate.getMonth() + "/" + currentDate.getYear();
                val endDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                getDaysBetweenDates(startDate, endDate);

                // int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
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
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }


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
        for (int i = 0; i < mEditImageList.size(); i++)
        {

            StorageReference filePath = mStorage.child(Constants.ORDER_IMAGES)
                                                .child(userId)
                                                .child(orderId + "" + i);
            mEditImageList.get(i)
                          .setUploading(0);
            mAddImagesCustomAdapter.notifyItemChanged(i);


            int finalI = i;
            val imageIntentUri = mEditImageList.get(finalI)
                                               .getImageIntentURI();
            Task<Uri> uriTask = filePath.putFile(imageIntentUri)
                                        .continueWithTask(
                                                new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                                                {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                                                            throws Exception
                                                    {
                                                        if (!task.isSuccessful())
                                                        {
                                                            AndroidUtil.toast(false,
                                                                              task.getException()
                                                                                  .toString());
                                                            throw task.getException();
                                                        }
                                                        return filePath.getDownloadUrl();
                                                    }
                                                })
                                        .addOnCompleteListener(new OnCompleteListener<Uri>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task)
                                            {
                                                if (task.isSuccessful())
                                                {

                                                    Log.d("image_url", task.getResult()
                                                                           .toString());
                                                    mOrderImageList.add(
                                                            new OrderImages(
                                                                    mEditImageList.get(
                                                                            finalI)
                                                                                  .getDescription(),
                                                                    task.getResult()
                                                                        .toString()));
                                                    mEditImageList.get(finalI)
                                                                  .setUploading(1);
                                                    mAddImagesCustomAdapter.notifyItemChanged(
                                                            finalI);

                                                    if (mOrderImageList.size() == mEditImageList.size())
                                                    {
                                                        saveDataToFireStore(orderId);
                                                    }
                                                }
                                            }
                                        });

        }

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
                              "" + orderId);
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
                                  UIUtils.testToast(false, "Order posted");
                                  gotoBack();
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
