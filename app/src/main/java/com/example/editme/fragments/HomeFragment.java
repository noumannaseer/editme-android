package com.example.editme.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.activities.OrderDetailsActivity;
import com.example.editme.databinding.FragmentHomefragmentBinding;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */

//********************************************************************************
public class HomeFragment
        extends Fragment
//********************************************************************************
{


    //********************************************************************************
    public HomeFragment()
    //********************************************************************************
    {
        // Required empty public constructor
    }


    private FragmentHomefragmentBinding mBinding;
    private View mRootView;

    //********************************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    //********************************************************************************
    {
        if (mRootView == null)
        {
            mBinding = FragmentHomefragmentBinding.inflate(inflater, container, false);
            mRootView = mBinding.getRoot();
            initControls();
        }
        return mRootView;
    }

    //********************************************************************************
    private void initControls()
    //********************************************************************************
    {
        mBinding.orderDetail.setOnClickListener(view -> gotoOrderDetailsScreen());
    }

    //********************************************************************************
    private void gotoOrderDetailsScreen()
    //********************************************************************************
    {
        Intent orderDetailIntent = new Intent(getActivity(), OrderDetailsActivity.class);
        startActivity(orderDetailIntent);
    }

}
