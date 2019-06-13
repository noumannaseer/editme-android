package com.example.editme.activities;

import android.app.Dialog;
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
import android.view.View;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.editme.R;
import com.example.editme.adapters.AddImagesCustomAdapter;
import com.example.editme.databinding.ActivityPlaceOrderBinding;
import com.example.editme.databinding.ImageDescriptionDialogBinding;
import com.example.editme.model.EditImage;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import lombok.val;

import static com.example.editme.utils.AndroidUtil.getContext;


//******************************************************************
public class PlaceOrderActivity
        extends AppCompatActivity
        implements AddImagesCustomAdapter.ImageClickListener
//******************************************************************
{

    private ActivityPlaceOrderBinding mBinding;
    private Uri mImageIntentURI;
    private ArrayList<EditImage> mEditImages;
    private AddImagesCustomAdapter mAddImagesCustomAdapter;


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
    private void initControls()
    //******************************************************************
    {
        mBinding.addImage.setOnClickListener(view -> showImageDialog());
        mEditImages = new ArrayList<>();
        mBinding.orderDescription.clearFocus();
        mBinding.toolbarNext.setOnClickListener(view -> gotoCheckOutScreen());

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
        startActivity(checkOutIntent);

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
                    mEditImages.add(new EditImage("", mImageIntentURI));
                    showImageDescriptionDialog(-1);
                    //showDataOnRecyclerView();


                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }

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
            mDialogBinding.selectedImage.setImageDrawable(mEditImages.get(index)
                                                                     .getImageDrawable(this));
            mDialogBinding.imageDescription.setText(mEditImages.get(index)
                                                               .getDescription());

        }

        mDialogBinding.close.setOnClickListener(view -> {
            if (index == -1)
                mEditImages.remove(mEditImages.size() - 1);
            dialog.dismiss();
        });
        mDialogBinding.addImage.setOnClickListener(view -> {
            val description = mDialogBinding.imageDescription.getText()
                                                             .toString();
            if (TextUtils.isEmpty(description))
            {
                mDialogBinding.imageDescription.setError(AndroidUtil.getString(R.string.required));
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
                                                             this, true, this);
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
}
