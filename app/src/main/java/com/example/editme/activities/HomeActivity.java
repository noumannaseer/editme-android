package com.example.editme.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.databinding.ActivityHomeBinding;
import com.example.editme.fragments.HomeFragment;
import com.example.editme.fragments.OrderFragment;
import com.example.editme.fragments.ProfileFragment;
import com.example.editme.model.Notifications;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import lombok.Getter;
import retrofit2.http.GET;

//****************************************************************
public class HomeActivity
        extends AppCompatActivity
//****************************************************************
{


    public static final String SELECTED_NOTIFICATION = "SELECTED_NOTIFICATION";
    private static final int SELECT_IMAGE = 454;
    private ActivityHomeBinding mBinding;
    private HomeFragment mHomeFragment;
    private OrderFragment mOrderFragment;
    private ProfileFragment mProfileFragment;
    @Getter
    private Uri mSingleImageUri;
    @Getter
    private List<Uri> mMultipleImageUri;

    //****************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //****************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initControls();
    }

    //****************************************************************
    private void initControls()
    //****************************************************************
    {
        mHomeFragment = new HomeFragment();
        mOrderFragment = new OrderFragment();
        mProfileFragment = new ProfileFragment();

        receiveImageFromGallery();
        loadFragment(mOrderFragment);
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(
                mOnNavigationItemSelectedListener);
        getNotificationData();
    }

    private void receiveImageFromGallery()
    {
        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        if (TextUtils.isEmpty(receivedAction))
            return;
        String receivedType = receivedIntent.getType();

        Log.d(UIUtils.getTagName(this), receivedIntent.toString());
        if (receivedAction.equals(Intent.ACTION_SEND))
        {
            if (receivedType.startsWith("image/"))
                handleSendImage(receivedIntent);
        }
        else if (receivedAction.equals(Intent.ACTION_SEND_MULTIPLE))
        {
            if (receivedType.startsWith("image/"))
                handleSendMultipleImages(receivedIntent);
        }
        else if (receivedAction.equals(Intent.ACTION_MAIN))
        {
        }
    }

    private void handleSendImage(Intent intent)
    {
        mSingleImageUri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
    }

    private void handleSendMultipleImages(Intent intent)
    {
        mMultipleImageUri = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    }

    //*********************************************************************
    private void getNotificationData()
    //*********************************************************************
    {
        try
        {
         /*   if (EditMe.instance()
                      .getMAuth()
                      .getCurrentUser() == null)
            {
                return;
            }
            */
            if (getIntent().getExtras()
                           .containsKey(Constants.IMAGE_DOWNLOADED))
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivity(Intent.createChooser(intent,
                                                   AndroidUtil.getString(R.string.select_picture)));
            }


            else if (getIntent().getExtras()
                                .containsKey(SELECTED_NOTIFICATION))
            {
                Notifications notification = getIntent().getParcelableExtra(SELECTED_NOTIFICATION);
                gotoOrderDetailScreen(notification.getOrderId());
            }

            else if (getIntent().getExtras()
                                .containsKey(Constants.ORDER_ID))
            {
                gotoOrderDetailScreen(getIntent().getExtras()
                                                 .getString(Constants.ORDER_ID));
            }

        }
        catch (NullPointerException e)
        {
        }

    }

    private void gotoOrderDetailScreen(String orderId)
    {
        Intent orderDetailIntent = new Intent(this, OrderDetailsActivity.class);
        orderDetailIntent.putExtra(OrderDetailsActivity.ORDER_ID, orderId);
        startActivity(orderDetailIntent);
    }


    //****************************************************************
    public void loadFragment(Fragment fragment)
    //****************************************************************
    {
        if (fragment instanceof HomeFragment)
            mBinding.bottomNavigation.getMenu()
                                     .getItem(0)
                                     .setChecked(true);
        else if (fragment instanceof OrderFragment)
            mBinding.bottomNavigation.getMenu()
                                     .getItem(1)
                                     .setChecked(true);
        else if (fragment instanceof ProfileFragment)
            mBinding.bottomNavigation.getMenu()
                                     .getItem(2)
                                     .setChecked(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    //****************************************************************
    public void loadOrderFragment()
    //****************************************************************
    {

        loadFragment(mOrderFragment);
    }


    //****************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    //****************************************************************************************
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras()
                                .containsKey(Constants.ORDER_UPDATED))
        {
            mHomeFragment.onActivityResult(requestCode, resultCode, data);
            loadFragment(mHomeFragment);
        }
    }

    //****************************************************************
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
            //****************************************************************
    {
        @Override
        public boolean onNavigationItemSelected(MenuItem item)
        {
            switch (item.getItemId())
            {
            case R.id.action_home:
                if (EditMe.instance()
                          .getMAuth()
                          .getCurrentUser() == null)
                {
                    UIUtils.showSnackBar(HomeActivity.this, "Please login first to continue");
                    return true;
                }
                loadFragment(mHomeFragment);
                return true;
            case R.id.action_orders:
                loadFragment(mOrderFragment);
                return true;
            case R.id.action_profile:
                if (EditMe.instance()
                          .getMAuth()
                          .getCurrentUser() == null)
                {
                    UIUtils.showSnackBar(HomeActivity.this, "Please login first to continue");
                    return true;
                }
                loadFragment(mProfileFragment);

                return true;
            }
            return false;
        }
    };

}
