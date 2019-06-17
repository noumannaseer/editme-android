package com.example.editme.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.editme.R;
import com.example.editme.activities.SettingsActivity;
import com.example.editme.databinding.FragmentProfileBinding;
import com.example.editme.model.EditImage;
import com.example.editme.utils.AndroidUtil;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import lombok.val;


/**
 * A simple {@link Fragment} subclass.
 */

//*********************************************************************
public class ProfileFragment
        extends Fragment
//*********************************************************************
{


    //*********************************************************************
    public ProfileFragment()
    //*********************************************************************
    {
        // Required empty public constructor
    }


    private FragmentProfileBinding mBinding;
    private View mRootView;
    private Uri mImageIntentURI;


    //*********************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    //*********************************************************************
    {
        if (mRootView == null)
        {
            mBinding = FragmentProfileBinding.inflate(inflater, container, false);
            mRootView = mBinding.getRoot();
        }
        initControls();
        return mRootView;
    }

    //*********************************************************************
    private void initControls()
    //*********************************************************************
    {
        setHasOptionsMenu(true);
        Toolbar toolbar = mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                                          .setDisplayShowTitleEnabled(false);
        mBinding.profileImage.setOnClickListener(view -> showImageDialog());

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
                       .theme(getActivity().getPackageManager()
                                           .getActivityInfo(getActivity().getComponentName(), 0)
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
                            getActivity().getContentResolver(),
                            mImageIntentURI);
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    mBinding.profileImage.setImageDrawable(d);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    //******************************************************************
    {
        if (item.getItemId() == R.id.action_settings)
        {
            gotoSettingsScreen();
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoSettingsScreen()
    {
        Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(settingsIntent);

    }

    //*********************************************************************
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    //*********************************************************************
    {
        inflater.inflate(R.menu.settings_menu, menu);
    }
}
