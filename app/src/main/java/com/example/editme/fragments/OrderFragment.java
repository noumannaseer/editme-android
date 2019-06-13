package com.example.editme.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.R;
import com.example.editme.activities.PlaceOrderActivity;
import com.example.editme.databinding.FragmentOrderBinding;
import com.example.editme.databinding.FragmentPackagesBinding;


/**
 * A simple {@link Fragment} subclass.
 */

//*********************************************************************
public class OrderFragment
        extends Fragment
//*********************************************************************
{


    //*********************************************************************
    public OrderFragment()
    //*********************************************************************
    {
        // Required empty public constructor
    }

    private FragmentOrderBinding mBinding;
    private View mRootView;

    //*********************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    //*********************************************************************
    {
        if (mRootView == null)
        {
            mBinding = FragmentOrderBinding.inflate(inflater, container, false);
            mRootView = mBinding.getRoot();
        }
        initControls();
        return mRootView;
    }

    private void initControls()
    {
        mBinding.placeOrder.setOnClickListener(view -> gotoPlaceOrderScreen());
    }

    private void gotoPlaceOrderScreen()
    {
        Intent placeOrderIntent = new Intent(getContext(), PlaceOrderActivity.class);
        startActivity(placeOrderIntent);
    }

}
