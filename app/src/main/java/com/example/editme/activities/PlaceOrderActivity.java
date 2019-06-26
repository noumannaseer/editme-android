package com.example.editme.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.adapters.AddImagesCustomAdapter;
import com.example.editme.adapters.OrderImagesListCustomAdapter;
import com.example.editme.databinding.ActivityPlaceOrderBinding;
import com.example.editme.databinding.ImageDescriptionDialogBinding;
import com.example.editme.model.EditImage;
import com.example.editme.model.Order;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import lombok.val;

import static com.example.editme.utils.AndroidUtil.getContext;


//******************************************************************
public class PlaceOrderActivity
        extends AppCompatActivity
        implements AddImagesCustomAdapter.ImageClickListener, OrderImagesListCustomAdapter.UpdateImageClickListener
//******************************************************************
{

    private ActivityPlaceOrderBinding mBinding;
    private Uri mImageIntentURI;
    private ArrayList<EditImage> mEditImages;
    private AddImagesCustomAdapter mAddImagesCustomAdapter;
    public static final String SELECTED_ORDER = "SELECTED_ORDER";
    public static final String SELECTED_INDEX = "SELECTED_INDEX";

    private Order mOrder;
    private boolean mIsUpdate = false;


    //******************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //******************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_place_order);
        initControls();
    }

    //******************************************************************
    public static void setMargins(View v, int l, int t, int r, int b)
    //******************************************************************
    {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)
        {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams)v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    //******************************************************************
    private void initControls()
    //******************************************************************
    {
        setTab();
        getParcelable();

        mBinding.addImage.setOnClickListener(view -> showImageDialog());
        mEditImages = new ArrayList<>();
        mBinding.orderDescription.clearFocus();
        if (mOrder != null)
        {
            mBinding.toolbarNext.setText(AndroidUtil.getString(R.string.update));
            mBinding.toolbarNext.setVisibility(View.VISIBLE);
            mBinding.addImage.setVisibility(View.GONE);
            mBinding.orderDescription.setText(mOrder.getDescription());
            setMargins(mBinding.nestedScrollView, 0, 0, 0, 0);
            loadImagesOnRecyclerView();
        }
        mBinding.toolbarNext.setOnClickListener(view -> {
            if (mOrder == null)
                gotoCheckOutScreen();
            else
                updateOrder();
        });

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
    private void loadImagesOnRecyclerView()
    //******************************************************************
    {
        OrderImagesListCustomAdapter orderImagesListCustomAdapter = new OrderImagesListCustomAdapter(
                mOrder.getImages(), PlaceOrderActivity.this);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(orderImagesListCustomAdapter);

    }

    //******************************************************************
    private void updateOrder()
    //******************************************************************
    {

        val description = mBinding.orderDescription.getText()
                                                   .toString();
        if (TextUtils.isEmpty(description))
        {
            mBinding.orderDescription.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        showProgressView();
        mOrder.setDescription(description);
        EditMe.instance()
              .getMFireStore()
              .collection(Constants.ORDERS)
              .document(mOrder.getOrderId())
              .set(mOrder, SetOptions.merge())
              .addOnCompleteListener(
                      new OnCompleteListener<Void>()
                      {
                          @Override
                          public void onComplete(@NonNull Task<Void> task)
                          {
                              if (task.isSuccessful())
                              {
                                  hideProgressView();
                                  UIUtils.displayAlertDialog(
                                          AndroidUtil.getString(R.string.order_updated),
                                          AndroidUtil.getString(R.string.update),
                                          PlaceOrderActivity.this,
                                          new DialogInterface.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which)
                                              {
                                                  if (which == -1)
                                                  {
                                                      Intent data = new Intent();
                                                      data.putExtra(Constants.ORDER_UPDATED, true);
                                                      setResult(RESULT_OK, data);
                                                      finish();
                                                  }

                                              }
                                          }, AndroidUtil.getString(R.string.ok));
                              }

                          }
                      });

    }

    //******************************************************************
    private void getParcelable()
    //******************************************************************
    {
        try
        {
            if (getIntent().getExtras()
                           .containsKey(SELECTED_ORDER))
            {
                mOrder = getIntent().getExtras()
                                    .getParcelable(SELECTED_ORDER);
                mIsUpdate = true;
            }
        }
        catch (Exception e)
        {
            mIsUpdate = false;
        }

    }

    //******************************************************************
    private void gotoCheckOutScreen()
    //******************************************************************
    {
        val description = mBinding.orderDescription.getText()
                                                   .toString();
        if (mEditImages.size() == 0)
        {
            UIUtils.showSnackBar(this, AndroidUtil.getString(R.string.please_add_image));
            return;
        }
        if (TextUtils.isEmpty(description))
        {
            mBinding.orderDescription.setError(AndroidUtil.getString(R.string.required));
            return;
        }


        Intent checkOutIntent = new Intent(this, CheckOutActivity.class);
        checkOutIntent.putParcelableArrayListExtra(CheckOutActivity.IMAGES_LIST, mEditImages);
        checkOutIntent.putExtra(CheckOutActivity.ORDER_DESCRIPTION, description);
        startActivityForResult(checkOutIntent, 111);

    }

    //**********************************************
    public void showImageDialog()
    //**********************************************
    {

        try
        {
            ImagePicker.create(this)
                       .returnMode(ReturnMode.ALL)
                       .toolbarFolderTitle(AndroidUtil.getString(R.string.select_profile))
                       .toolbarArrowColor(Color.WHITE)
                       .theme(getPackageManager().getActivityInfo(getComponentName(), 0)
                                                 .getThemeResource())
                       .single()
                       .toolbarImageTitle(AndroidUtil.getString(R.string.select_profile))
                       .showCamera(true)
                       .includeVideo(false)
                       //.theme(getTheme())
                       .enableLog(true)
                       .start();

        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    //**************************************************************************
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    //**************************************************************************
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data))
        {
            Image image = ImagePicker.getFirstImageOrNull(data);
            mImageIntentURI = data.getData();
            if (image != null)
            {
                mImageIntentURI = Uri.fromFile(new File(image.getPath()));
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(),
                            mImageIntentURI);
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    mEditImages.add(new EditImage("", mImageIntentURI, -1));
                    showImageDescriptionDialog(-1);
                    //showDataOnRecyclerView();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }
        else if (data != null && data.getExtras()
                                     .containsKey(Constants.ORDER_UPDATED))
        {
            gotoBack();
        }

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
    private void showImageDescriptionDialog(int index)
    //******************************************************************
    {
        Dialog dialog = new Dialog(this);
        //dialog.setContentView(R.layout.image_description_dialog);
        ImageDescriptionDialogBinding mDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.image_description_dialog, null, false);
        dialog.setContentView(mDialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();
        if (index == -1)
            mDialogBinding.selectedImage.setImageDrawable(mEditImages.get(mEditImages.size() - 1)
                                                                     .getImageDrawable(this));

        else
        {
            if (!mIsUpdate)
            {
                mDialogBinding.selectedImage.setImageDrawable(mEditImages.get(index)
                                                                         .getImageDrawable(this));
                mDialogBinding.imageDescription.setText(mEditImages.get(index)
                                                                   .getDescription());
            }
            else
            {
                UIUtils.loadImages(mOrder.getImages()
                                         .get(index)
                                         .getUrl(), mDialogBinding.selectedImage,
                                   AndroidUtil.getDrawable(R.drawable.splash));
                mDialogBinding.imageDescription.setText(mOrder.getImages()
                                                              .get(index)
                                                              .getDescription());
                mDialogBinding.addImage.setText(AndroidUtil.getString(R.string.update));

            }

        }

        mDialogBinding.close.setOnClickListener(view -> {
            if (index == -1)
                mEditImages.remove(mEditImages.size() - 1);
            dialog.dismiss();
        });
        mDialogBinding.addImage.setOnClickListener(view -> {
            val description = mDialogBinding.imageDescription.getText()
                                                             .toString();
            if (!mIsUpdate)
            {
                if (TextUtils.isEmpty(description))
                {
                    mDialogBinding.imageDescription.setError(
                            AndroidUtil.getString(R.string.required));
                    return;
                }
                if (index == -1)
                    mEditImages.get(mEditImages.size() - 1)
                               .setDescription(mDialogBinding.imageDescription.getText()
                                                                              .toString());
                else
                    mEditImages.get(index)
                               .setDescription(mDialogBinding.imageDescription.getText()
                                                                              .toString());

                showDataOnRecyclerView();
            }
            else
            {
                if (TextUtils.isEmpty(description))
                {
                    mDialogBinding.imageDescription.setError(
                            AndroidUtil.getString(R.string.required));
                    return;
                }

                mOrder.getImages()
                      .get(index)
                      .setDescription(mDialogBinding.imageDescription.getText()
                                                                     .toString());
                loadImagesOnRecyclerView();

            }
            dialog.dismiss();
        });

    }


    //******************************************************************
    private void showDataOnRecyclerView()
    //******************************************************************
    {
        if (mEditImages.size() > 0)
            mBinding.toolbarNext.setVisibility(View.VISIBLE);


        mAddImagesCustomAdapter = new AddImagesCustomAdapter(mEditImages,
                                                             this, true, this, false);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(mAddImagesCustomAdapter);

    }


    //******************************************************************
    @Override
    public void onImageClick(int index)
    //******************************************************************
    {
        showImageDescriptionDialog(index);
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

    //******************************************************************
    @Override
    public void onImageDelete(int index)
    //******************************************************************
    {

        mEditImages.remove(index);

        if (mEditImages.size() == 0)
            mBinding.toolbarNext.setVisibility(View.GONE);

        mAddImagesCustomAdapter.notifyDataSetChanged();
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

    @Override
    public void onUpdateImageClick(int index)
    {
        showImageDescriptionDialog(index);
    }
}
