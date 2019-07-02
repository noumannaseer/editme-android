package com.example.editme.viewmodelfactory;

import android.app.Dialog;
import android.content.Context;

import com.example.editme.activities.HomeActivity;
import com.example.editme.databinding.LoginDialogBinding;
import com.example.editme.model.User;
import com.example.editme.viewmodel.LoginViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

//************************************************************
public class LoginViewModelFactory
        extends ViewModelProvider.NewInstanceFactory
//************************************************************
{
    private User user;
    private Context context;
    private LoginDialogBinding mBinding;
    private LoginViewModel.LoginSuccessListener mListener;


    //************************************************************
    public LoginViewModelFactory(HomeActivity homeActivity, User user, LoginDialogBinding binding, LoginViewModel.LoginSuccessListener listener)
    //************************************************************
    {
        this.user = user;
        this.context = homeActivity;
        mBinding = binding;
        mListener = listener;
    }

    //************************************************************
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    //************************************************************
    {
        return (T)new LoginViewModel(user, context, mBinding, mListener);
    }
}
