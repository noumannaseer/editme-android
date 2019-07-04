package com.example.editme.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.EditMe;
import com.example.editme.activities.HomeActivity;
import com.example.editme.activities.OrderDetailsActivity;
import com.example.editme.adapters.OrdersCustomAdapter;
import com.example.editme.databinding.FragmentHomeBinding;
import com.example.editme.model.Order;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import lombok.val;


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


    private FragmentHomeBinding mBinding;
    private View mRootView;
    private List<Order> mOrderList;
    public HomeActivity mHomeActivity;


    //**********************************************************************
    @Override
    public void onAttach(@NonNull Activity activity)
    {
        super.onAttach(activity);
        mHomeActivity = (HomeActivity)activity;
    }

    //********************************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    //********************************************************************************
    {
        if (mRootView == null)
        {
            mBinding = FragmentHomeBinding.inflate(inflater, container, false);
            mRootView = mBinding.getRoot();
            initControls();
        }
        return mRootView;
    }

    //********************************************************************************
    private void initControls()
    //********************************************************************************
    {
        showProgressView();
        AndroidUtil.handler.postDelayed(() -> fetchUserOrder(), 500);
        mBinding.createOrder.setOnClickListener(view -> {
            ((HomeActivity)getActivity()).loadOrderFragment();
        });
    }

    //********************************************************************************
    private void fetchUserOrder()
    //********************************************************************************
    {

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
            EditMe.instance()
                  .loadUserDetail();
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
                  public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e)
                  {
                      mOrderList = new ArrayList<>();
                      for (val child : queryDocumentSnapshots.getDocuments())
                      {
                          val order = child.toObject(Order.class);
                          if (!order.getStatus()
                                    .equals(Constants.STATUS_COMPLETE))
                              mOrderList.add(order);
                      }
                      showDataOnRecyclerView();
                      hideProgressView();

                  }
              });

    }

    //*************************************************************************************
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    //*************************************************************************************
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getExtras()
                                .containsKey(Constants.ORDER_UPDATED))
        {
            initControls();
        }
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


        val orderCustomAdapter = new OrdersCustomAdapter(mOrderList, (mHomeActivity));
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.setAdapter(orderCustomAdapter);

    }

    //********************************************************************************
    private void gotoOrderDetailsScreen()
    //********************************************************************************
    {
        Intent orderDetailIntent = new Intent(getActivity(), OrderDetailsActivity.class);
        startActivity(orderDetailIntent);
    }

}
