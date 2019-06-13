package com.example.editme.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.R;
import com.example.editme.activities.SettingsActivity;
import com.example.editme.databinding.FragmentProfileBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


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
