package com.example.editme.fragments;


import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.activities.HomeActivity;
import com.example.editme.activities.SettingsActivity;
import com.example.editme.adapters.OrdersCustomAdapter;
import com.example.editme.databinding.FragmentProfileBinding;
import com.example.editme.model.EditImage;
import com.example.editme.model.Order;
import com.example.editme.model.User;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    private User mUser;
    private List<Order> mOrderList;


    public HomeActivity mHomeActivity;


    @Override
    public void onAttach(@NonNull Activity activity)
    {
        super.onAttach(activity);
        mHomeActivity = (HomeActivity)activity;
    }

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
            initControls();
        }
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

        mUser = EditMe.instance()
                      .getMUserDetail();

        if (mUser == null)
            return;

        UIUtils.loadImages(mUser.getPhotoUrl(), mBinding.profileImage,
                           AndroidUtil.getDrawable(R.drawable.ic_person_black_24dp));
        mBinding.userName.setText(mUser.getDisplayName());
        mBinding.profileImage.setOnClickListener(view -> showImageDialog());
        getCompletedOrder();

    }

    private void getCompletedOrder()
    {
        showProgressView();
        if (EditMe.instance()
                  .getMAuth()
                  .getCurrentUser() == null)
        {
            hideProgressView();
            return;
        }
        else if (EditMe.instance()
                       .getMUserDetail() == null)
        {
            initControls();
            return;
        }
        val userEmail = EditMe.instance()
                              .getMUserDetail()
                              .getEmail();
        EditMe.instance()
              .getMFireStore()
              .collection(Constants.ORDERS)
              .whereEqualTo(Constants.EMAIL, userEmail)
              .addSnapshotListener(new EventListener<QuerySnapshot>()
              {
                  @Override
                  public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                  {
                      mOrderList = new ArrayList<>();
                      for (val child : queryDocumentSnapshots.getDocuments())
                      {

                          val order = child.toObject(Order.class);
                          if (order.getStatus()
                                   .equals(Constants.STATUS_COMPLETE))
                              mOrderList.add(order);
                      }
                      showDataOnRecyclerView();
                      hideProgressView();
                  }
              });

    }


    //********************************************************************************
    private void showDataOnRecyclerView()
    //********************************************************************************
    {
        if (mOrderList == null || mOrderList.size() == 0)
        {
            mBinding.noResultFound.setVisibility(View.VISIBLE);
            return;
        }
        else
            mBinding.noResultFound.setVisibility(View.GONE);

        val orderCustomAdapter = new OrdersCustomAdapter(mOrderList, mHomeActivity);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.setAdapter(orderCustomAdapter);

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
