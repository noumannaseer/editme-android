package com.example.editme.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.example.editme.R;
import com.example.editme.adapters.AddImagesCustomAdapter;
import com.example.editme.databinding.FragmentPackagesBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PackagesFragment
        extends Fragment
{

    FragmentPackagesBinding mBinding;

    public PackagesFragment()
    {
        // Required empty public constructor
    }


    private View mRootView;
    private List<String> mUrlList;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (mRootView == null)
        {
            mBinding = FragmentPackagesBinding.inflate(inflater, container, false);
            mRootView = mBinding.getRoot();
        }
        initControls();
        return mRootView;
    }

    private void initControls()
    {
        mUrlList = new ArrayList<>();
        mUrlList.add(
                "https://images.pexels.com/photos/462118/pexels-photo-462118.jpeg?auto=format%2Ccompress&cs=tinysrgb&dpr=1&w=500");
        mUrlList.add(
                "https://images.pexels.com/photos/257360/pexels-photo-257360.jpeg?auto=format%2Ccompress&cs=tinysrgb&dpr=1&w=500");

        mUrlList.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg");
        mUrlList.add(
                "https://images.unsplash.com/photo-1535498730771-e735b998cd64?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80");
        startPackageSlider();
    }

    private void startPackageSlider()
    {
        // vertical and cycle layout
        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(
                CarouselLayoutManager.VERTICAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());

        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setAdapter(new AddImagesCustomAdapter(mUrlList, null));
        mBinding.recyclerView.addOnScrollListener(new CenterScrollListener());
    }

}
