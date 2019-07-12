package com.example.editme.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.activities.FaceBookLoginActivity;
import com.example.editme.activities.GoogleSignInActivity;
import com.example.editme.activities.HomeActivity;
import com.example.editme.activities.PackagesActivity;
import com.example.editme.activities.PlaceOrderActivity;
import com.example.editme.activities.RecoveryEmailActivity;
import com.example.editme.databinding.FragmentOrderBinding;
import com.example.editme.databinding.LoginDialogBinding;
import com.example.editme.databinding.SignupDialogBinding;
import com.example.editme.model.User;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;
import com.example.editme.viewmodel.LoginViewModel;
import com.example.editme.viewmodel.SignUpViewModel;
import com.example.editme.viewmodelfactory.LoginViewModelFactory;
import com.example.editme.viewmodelfactory.SignUpViewModelFactory;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import lombok.val;


/**
 * A simple {@link Fragment} subclass.
 */

//*********************************************************************
public class OrderFragment
        extends Fragment
        implements LoginViewModel.LoginSuccessListener, SignUpViewModel.SignUpSuccessListener
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
    private String mName;
    private String mEmail;
    private String mPassword;
    private SignupDialogBinding mSignUpDialogBinding;
    private Dialog mSignUpDialog;
    private Dialog mLoginDialog;
    private LoginDialogBinding mLoginDialogBinding;

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
            initControls();

        }
        return mRootView;
    }

    //*********************************************************************
    private void initControls()
    //*********************************************************************
    {

        if (EditMe.instance()
                  .getMAuth()
                  .getCurrentUser() == null)
            displayLoginDialog();
        else
            getImagesDataFromActivity();


        mBinding.placeOrder.setOnClickListener(view -> {
            if (EditMe.instance()
                      .getMAuth()
                      .getCurrentUser() != null || EditMe.instance()
                                                         .isFaceBookLogin())
            {
                val user = EditMe.instance()
                                 .getMAuth()
                                 .getCurrentUser();
                if (EditMe.instance()
                          .getMUserDetail()
                          .getCurrentPackage() == null)
                {
                    gotoPackagesScreen();
                }
                else
                    gotoPlaceOrderScreen();
            }
            else
                displayLoginDialog();
        });

    }

    //*************************************************************************************
    private void gotoPackagesScreen()
    //*************************************************************************************
    {
        Intent packagesIntent = new Intent(getActivity(), PackagesActivity.class);
        startActivity(packagesIntent);
    }

    //*********************************************************************
    private void gotoPlaceOrderScreen()
    //*********************************************************************
    {
        Intent placeOrderIntent = new Intent(getContext(), PlaceOrderActivity.class);
        startActivity(placeOrderIntent);
    }

    //*********************************************************************
    private void displaySignUpDialog()
    //*********************************************************************
    {
        mSignUpDialog = new Dialog(getActivity(), R.style.CustomTransparentDialog);
        mSignUpDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.signup_dialog, null, false);
        mSignUpDialog.setContentView(mSignUpDialogBinding.getRoot());
        mSignUpDialog.setCancelable(false);
        mSignUpDialog.show();


        SignUpViewModel signUpViewModel = ViewModelProviders.of(
                ((HomeActivity)getActivity()), new SignUpViewModelFactory(
                        ((HomeActivity)getActivity()),
                        new User(), mSignUpDialogBinding, OrderFragment.this))
                                                            .get(SignUpViewModel.class);

        mSignUpDialogBinding.setSignup(signUpViewModel);

        mSignUpDialogBinding.btnLoginNow.setOnClickListener(view -> {
            mSignUpDialog.dismiss();
            displayLoginDialog();
        });
        mSignUpDialogBinding.closeDialog.setOnClickListener(view -> mSignUpDialog.dismiss());

    }


    LoginViewModel loginViewModel;

    //*********************************************************************
    private void displayLoginDialog()
    //*********************************************************************
    {
        mLoginDialog = new Dialog(getActivity(), R.style.CustomTransparentDialog);
        //dialog.setContentView(R.layout.image_description_dialog);
        mLoginDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.login_dialog, null, false);
        mLoginDialog.setContentView(mLoginDialogBinding.getRoot());
        mLoginDialog.setCancelable(false);
        mLoginDialog.show();

        if (loginViewModel == null)
        {
            loginViewModel = ViewModelProviders.of(
                    ((HomeActivity)getActivity()), new LoginViewModelFactory(
                            ((HomeActivity)getActivity()),
                            new User(), mLoginDialogBinding, OrderFragment.this))
                                               .get(LoginViewModel.class);
        }
        mLoginDialogBinding.setUser(loginViewModel);

        mLoginDialogBinding.btnSignupNow.setOnClickListener(view -> {
            mLoginDialog.dismiss();
            displaySignUpDialog();
        });

        mLoginDialogBinding.forgotPassword.setOnClickListener(view -> gotoForgotPasswordScreen());
        mLoginDialogBinding.closeDialog.setOnClickListener(view -> mLoginDialog.dismiss());
        mLoginDialogBinding.btnFacebook.setOnClickListener(view -> {
            mLoginDialog.dismiss();
            mLoginDialog = null;
            mLoginDialogBinding = null;
            gotoFaceBookLoginScreen();
        });

        mLoginDialogBinding.btnGoogleplus.setOnClickListener(view -> {
            mLoginDialog.dismiss();
            gotoGoogleSignInScreen();
        });

    }

    //*************************************************************************************
    private void gotoGoogleSignInScreen()
    //*************************************************************************************
    {
        Intent googleSignInIntent = new Intent(getActivity(), GoogleSignInActivity.class);
        startActivity(googleSignInIntent);
    }

    //*********************************************************************
    private void gotoFaceBookLoginScreen()
    //*********************************************************************
    {
        Intent faceBookLoginIntent = new Intent(getActivity(), FaceBookLoginActivity.class);
        startActivity(faceBookLoginIntent);
    }

    //*********************************************************************
    private void gotoForgotPasswordScreen()
    //*********************************************************************
    {
        Intent forgotPasswordIntent = new Intent(getActivity(), RecoveryEmailActivity.class);
        startActivity(forgotPasswordIntent);
    }


    @Override
    public void onLoginSuccessFull()
    {
        EditMe.instance()
              .loadUserDetail();

        AndroidUtil.handler.postDelayed(() -> {
            mLoginDialog.dismiss();
            if (EditMe.instance()
                      .getMUserDetail()
                      .getCurrentPackage() == null)
            {

                UIUtils.showSnackBar(getActivity(), AndroidUtil.getString(R.string.please_package));

            }
            else
            {
                getImagesDataFromActivity();

            }
        }, 2000);

    }


    //*********************************************************************
    private void getImagesDataFromActivity()
    //*********************************************************************
    {

        val multipleImageUri = ((HomeActivity)getActivity()).getMMultipleImageUri();
        val singleImageUri = ((HomeActivity)getActivity()).getMSingleImageUri();

        if (multipleImageUri != null)
        {
            gotoPlaceOrderScreenWithMultipleImage(new ArrayList<>(multipleImageUri));
        }
        else if (((HomeActivity)getActivity()).getMSingleImageUri() != null)
        {
            gotoPlaceOrderScreenWithSingleImage(singleImageUri);
        }
    }

    //********************************************************************************
    private void gotoPlaceOrderScreenWithMultipleImage(ArrayList<Uri> uriList)
    //********************************************************************************
    {
        Intent placeOrderIntent = new Intent(getActivity(), PlaceOrderActivity.class);
        placeOrderIntent.putParcelableArrayListExtra(PlaceOrderActivity.SHARED_MULTIPLE_IMAGE_URI,
                                                     uriList);
        startActivity(placeOrderIntent);

    }

    //********************************************************************************
    private void gotoPlaceOrderScreenWithSingleImage(Uri singleImageUri)
    //********************************************************************************
    {
        Intent placeOrderIntent = new Intent(getActivity(), PlaceOrderActivity.class);
        placeOrderIntent.putExtra(PlaceOrderActivity.SHARED_SINGLE_IMAGE_URI, singleImageUri);
        startActivity(placeOrderIntent);
    }

    //********************************************************************************
    @Override
    public void onSignUpSuccessListener()
    //********************************************************************************
    {
        mSignUpDialog.dismiss();
        mSignUpDialog = null;
        mSignUpDialogBinding = null;
        displayLoginDialog();
    }

    //**********************************************
    interface AccountRegistrationListen
            //**********************************************
    {
        void onAccountCreatedSuccessfully(String userId);

        void onAccountCreationFailed();
    }

}
