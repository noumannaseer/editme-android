package com.example.editme.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.editme.R;
import com.example.editme.databinding.ActivityHomeBinding;
import com.example.editme.fragments.HomeFragment;
import com.example.editme.fragments.OrderFragment;
import com.example.editme.fragments.PackagesFragment;
import com.example.editme.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

//****************************************************************
public class HomeActivity
        extends AppCompatActivity
//****************************************************************
{


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
        loadFragment(mHomeFragment);
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(
                mOnNavigationItemSelectedListener);
    }

    //****************************************************************
    public boolean onCreateOptionsMenu(Menu menu)
    //****************************************************************
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.postorderlist, menu);
        return true;
    }

    //****************************************************************
    public boolean onOptionsItemSelected(MenuItem item)
    //****************************************************************
    {
        if (item.getItemId() == R.id.action_settings)
        {
            //  Intent setting = new Intent(HomeActivity.this, Setting_Screen.class);
            //startActivity(setting);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //****************************************************************
    private void loadFragment(Fragment fragment)
    //****************************************************************
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
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
                loadFragment(mHomeFragment);
                return true;
            case R.id.action_orders:
                loadFragment(mOrderFragment);
                return true;
            case R.id.action_profile:
                loadFragment(mProfileFragment);
                return true;
            }
            return false;
        }
    };

}
