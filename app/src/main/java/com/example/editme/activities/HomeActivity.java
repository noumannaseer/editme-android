package com.example.editme.activities;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
        loadFragment(mOrderFragment);
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(
                mOnNavigationItemSelectedListener);
        getNotificationData();
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
