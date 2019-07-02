package com.example.editme.viewmodelfactory;

import android.content.Context;

import com.example.editme.activities.HomeActivity;
import com.example.editme.databinding.LoginDialogBinding;
import com.example.editme.databinding.SignupDialogBinding;
import com.example.editme.model.User;
import com.example.editme.viewmodel.LoginViewModel;
import com.example.editme.viewmodel.SignUpViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


//**********************************************************************
public class SignUpViewModelFactory
        extends ViewModelProvider.NewInstanceFactory
//************************************************************
{


    private User user;
    private Context context;
    private SignupDialogBinding mBinding;
    private SignUpViewModel.SignUpSuccessListener mListener;

    //**************************************************************************
    public SignUpViewModelFactory(HomeActivity homeActivity, User user, SignupDialogBinding binding, SignUpViewModel.SignUpSuccessListener listener)
    //**************************************************************************
    {
        this.user = user;
        this.context = homeActivity;
        mBinding = binding;
        mListener = listener;
    }


    //**************************************************************************
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    //**************************************************************************
    {
        return (T)new SignUpViewModel(user, context, mBinding, mListener);
    }
}
