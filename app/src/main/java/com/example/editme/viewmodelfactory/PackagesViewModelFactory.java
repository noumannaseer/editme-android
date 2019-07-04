package com.example.editme.viewmodelfactory;

import android.content.Context;

import com.example.editme.activities.PackagesActivity;
import com.example.editme.viewmodel.PackagesViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PackagesViewModelFactory
        extends ViewModelProvider.NewInstanceFactory

{

    Context mContext;


    //************************************************************
    public PackagesViewModelFactory(PackagesActivity packagesActivity)
    //************************************************************
    {
        mContext = packagesActivity;
    }

    //************************************************************
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    //************************************************************
    {
        return (T)new PackagesViewModel(mContext);
    }
}
