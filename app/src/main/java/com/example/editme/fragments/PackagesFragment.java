package com.example.editme.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.R;
import com.example.editme.activities.HomeActivity;
import com.example.editme.adapters.CircularImageSliderAdapter;
import com.example.editme.databinding.FragmentPackagesBinding;
import com.example.editme.model.PackagesDetails;
import com.example.editme.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import lombok.val;

/**
 * A simple {@link Fragment} subclass.
 */

//*********************************************************************
public class PackagesFragment
        extends Fragment
        implements CircularImageSliderAdapter.CircularSliderListener
//*********************************************************************
{


    //*********************************************************************
    public PackagesFragment()
    //*********************************************************************
    {
        // Required empty public constructor
    }

    private FragmentPackagesBinding mBinding;
    private View mRootView;
    private List<String> mUrlList;


    //*********************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    //*********************************************************************
    {
        if (mRootView == null)
        {
            mBinding = FragmentPackagesBinding.inflate(inflater, container, false);
            mRootView = mBinding.getRoot();
        }
        initControls();
        return mRootView;
    }

    //*********************************************************************
    private void initControls()
    //*********************************************************************
    {
        initData();
        mBinding.packagesPrice.setOnClickListener(view -> {
            UIUtils.testToast(false, "Package bu successfull");
            UIUtils.setPackageStatus(true);
            ((HomeActivity)getActivity()).loadFragment(new OrderFragment());
        });
    }

    List<PackagesDetails> lstImages;

    //*********************************************************************
    private void initData()
    //*********************************************************************

    {
        lstImages = new ArrayList<>();

        lstImages.add(
                new PackagesDetails("Package 1", "package 1 descrption", 150));
        lstImages.add(
                new PackagesDetails("Package 2", "package 2 descrption", 250));
        lstImages.add(
                new PackagesDetails("Package 3", "package 3 descrption", 300));

        CircularImageSliderAdapter adapter = new CircularImageSliderAdapter(lstImages,
                                                                            getActivity(),
                                                                            PackagesFragment.this);
        mBinding.horizontalCycle.setAdapter(adapter);
    }


    //*********************************************************************
    @Override
    public void onImageSlide(int position)
    //*********************************************************************
    {
        val packagesDetail = lstImages.get(position);
        mBinding.packageName.setText(packagesDetail.getPackageName());
        mBinding.packagesDetail.setText(packagesDetail.getPackageDescription());
        mBinding.packagesPrice.setText("$" + packagesDetail.getPrice());

    }

    @Override
    public void onPurchaseClick(int position)
    {
    }
}
