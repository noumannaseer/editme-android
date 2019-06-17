package com.example.editme.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.activities.PlaceOrderActivity;
import com.example.editme.activities.RecoveryEmailActivity;
import com.example.editme.databinding.FragmentOrderBinding;
import com.example.editme.databinding.LoginDialogBinding;
import com.example.editme.databinding.SignupDialogBinding;
import com.example.editme.model.User;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.widgets.Analyzer;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import lombok.NonNull;
import lombok.val;


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
    private String mName;
    private String mEmail;
    private String mPassword;
    private SignupDialogBinding mSignUpDialogBinding;
    private Dialog mSignUpDialog;
    private Dialog mLoginDialog;
    private LoginDialogBinding mLoginDialogBinding;
    private static final String EMAIL = "email";
    private CallbackManager callbackManager;

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

    //*********************************************************************
    private void initControls()
    //*********************************************************************
    {
        mBinding.placeOrder.setOnClickListener(view -> displayLoginDialog());
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
        //dialog.setContentView(R.layout.image_description_dialog);
        mSignUpDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.signup_dialog, null, false);
        mSignUpDialog.setContentView(mSignUpDialogBinding.getRoot());
        mSignUpDialog.setCancelable(false);
        mSignUpDialogBinding.btnSignup.setOnClickListener(view -> signUpUser());
        mSignUpDialogBinding.btnLoginNow.setOnClickListener(view -> {
            mSignUpDialog.dismiss();
            displayLoginDialog();
        });
        mSignUpDialogBinding.closeDialog.setOnClickListener(view -> mSignUpDialog.dismiss());

        mSignUpDialogBinding.btnFacebook.setOnClickListener(view -> {
            LoginManager.getInstance()
                        .logInWithReadPermissions(
                                this,
                                Arrays.asList("email"));
        });
        // If you are using in a fragment, call loginButton.setFragment(this);


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn)
            UIUtils.testToast(false, "Already facebook login");

        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        LoginManager.getInstance()
                    .registerCallback(
                            callbackManager,
                            new FacebookCallback<LoginResult>()
                            {
                                @Override
                                public void onSuccess(LoginResult loginResult)
                                {
                                    // Handle succes
                                    UIUtils.testToast(false, "FaceBook Login Sucesfull");
                                }

                                @Override
                                public void onCancel()
                                {
                                    UIUtils.testToast(false, "FaceBook Login Cancel");

                                }

                                @Override
                                public void onError(FacebookException exception)
                                {
                                    UIUtils.testToast(false, exception.getLocalizedMessage()
                                                                      .toString());

                                }
                            }
                    );
      /*  mSignUpDialogBinding.facebookLogin.registerCallback(callbackManager,
                                                            new FacebookCallback<LoginResult>()
                                                            {
                                                                @Override
                                                                public void onSuccess(LoginResult loginResult)
                                                                {
                                                                    // App code
                                                                }

                                                                @Override
                                                                public void onCancel()
                                                                {
                                                                    // App code
                                                                }

                                                                @Override
                                                                public void onError(FacebookException exception)
                                                                {
                                                                    // App code
                                                                }
                                                            });*/
        mSignUpDialog.show();

    }

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

        mLoginDialogBinding.btnSignupNow.setOnClickListener(view -> {
            mLoginDialog.dismiss();
            displaySignUpDialog();
        });

        mLoginDialogBinding.login.setOnClickListener(view -> loginUser());
        mLoginDialogBinding.forgotPassword.setOnClickListener(view -> gotoForgotPasswordScreen());
        mLoginDialogBinding.closeDialog.setOnClickListener(view -> mLoginDialog.dismiss());
        mLoginDialog.show();

    }

    //*********************************************************************
    private void gotoForgotPasswordScreen()
    //*********************************************************************
    {
        Intent forgotPasswordIntent = new Intent(getActivity(), RecoveryEmailActivity.class);
        startActivity(forgotPasswordIntent);
    }

//    @Override
//     void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //***************************************************************
    private void loginUser()
    //***************************************************************
    {


        mEmail = mLoginDialogBinding.loginEmail.getText()
                                               .toString();
        mPassword = mLoginDialogBinding.password.getText()
                                                .toString();


        if (TextUtils.isEmpty(mEmail))
        {
            mLoginDialogBinding.loginEmail.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        if (!UIUtils.isValidEmailId(mEmail))
        {
            mLoginDialogBinding.loginEmail.setError(AndroidUtil.getString(R.string.email_format));
            return;
        }

        if (TextUtils.isEmpty(mPassword))
        {
            mLoginDialogBinding.password.setError(AndroidUtil.getString(R.string.required));
            return;
        }


        if (mPassword.length() < 8)
        {
            mLoginDialogBinding.password.setError(AndroidUtil.getString(R.string.password_length));
            return;
        }


        mLoginDialogBinding.progressView.setVisibility(
                View.VISIBLE);

        EditMe.instance()
              .getMAuth()
              .signInWithEmailAndPassword(mEmail, mPassword)
              .addOnCompleteListener(task -> {

                  if (task.isSuccessful())
                  {

                      if (!EditMe.instance()
                                 .getMAuth()
                                 .getCurrentUser()
                                 .isEmailVerified())
                      {


                          EditMe.instance()
                                .getMAuth()
                                .getCurrentUser()
                                .sendEmailVerification()
                                .addOnSuccessListener(
                                        new OnSuccessListener<Void>()
                                        {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                UIUtils.displayAlertDialog(getString(
                                                        R.string.verify_acount),
                                                                           null,
                                                                           getActivity());
                                                mLoginDialogBinding.progressView.setVisibility(
                                                        View.GONE);

                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener()
                                        {
                                            @Override
                                            public void onFailure(@androidx.annotation.NonNull Exception e)
                                            {
                                                UIUtils.displayAlertDialog(getString(
                                                        R.string.verify_acount),
                                                                           null,
                                                                           getActivity());
                                                mLoginDialogBinding.progressView.setVisibility(
                                                        View.GONE);

                                            }
                                        });

                      }

                      else
                      {


                          UIUtils.setUserRemember(true);
                          UIUtils.testToast(false, "login successfulls");
                          mLoginDialogBinding.progressView.setVisibility(View.GONE);

                      }
                  }
                  else
                  {

                      UIUtils.displayAlertDialog(
                              AndroidUtil.getString(R.string.invalid_email_password),
                              getString(R.string.login_error),
                              getActivity());
                      mLoginDialogBinding.progressView.setVisibility(View.GONE);

                  }

              });

    }

    //***************************************************************
    private void signUpUser()
    //***************************************************************
    {

        mName = mSignUpDialogBinding.name.getText()
                                         .toString();
        mEmail = mSignUpDialogBinding.email.getText()
                                           .toString();
        mPassword = mSignUpDialogBinding.password.getText()
                                                 .toString();


        if (TextUtils.isEmpty(mName))
        {
            mSignUpDialogBinding.name.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        if (TextUtils.isEmpty(mEmail))
        {
            mSignUpDialogBinding.email.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        if (!UIUtils.isValidEmailId(mEmail))
        {
            mSignUpDialogBinding.email.setError(AndroidUtil.getString(R.string.email_format));
            return;
        }

        if (TextUtils.isEmpty(mPassword))
        {
            mSignUpDialogBinding.password.setError(AndroidUtil.getString(R.string.required));
            return;
        }


        if (mPassword.length() < 8)
        {
            mSignUpDialogBinding.password.setError(AndroidUtil.getString(R.string.password_length));
            return;
        }

        mSignUpDialogBinding.progressView.setVisibility(View.VISIBLE);
        createUserWithEmailAndPassword(mEmail, mPassword, mAccountRegistrationListener);

    }

    //*********************************************************************
    private AccountRegistrationListener mAccountRegistrationListener = new AccountRegistrationListener()
            //*********************************************************************
    {
        //*********************************************************************
        @Override
        public void onAccountCreatedSuccessfully(String userId)
        //*********************************************************************
        {
            if (TextUtils.isEmpty(userId))
                return;
            val user = new User(mName, mEmail, userId);
            EditMe.instance()
                  .getMFireStore()
                  .collection(Constants.Users)
                  .document(userId)
                  .set(user)
                  .addOnCompleteListener(new OnCompleteListener<Void>()
                  {
                      @Override
                      public void onComplete(@androidx.annotation.NonNull Task<Void> task)
                      {
                          val user = EditMe.instance()
                                           .getMAuth()
                                           .getCurrentUser();

                          user.sendEmailVerification()
                              .addOnSuccessListener(
                                      new OnSuccessListener<Void>()
                                      {
                                          @Override
                                          public void onSuccess(Void aVoid)
                                          {
                                              mSignUpDialogBinding.progressView.setVisibility(
                                                      View.GONE);
                                              UIUtils.displayAlertDialog(
                                                      AndroidUtil.getString(
                                                              R.string.verify_acount)
                                                      , getString(R.string.account_created),
                                                      getActivity(),
                                                      (dialog, which) ->
                                                      {
                                                          EditMe.instance()
                                                                .getMAuth()
                                                                .signOut();

                                                          displayLoginDialog();

                                                      }, AndroidUtil.getString(R.string.ok));

                                          }
                                      });

                      }
                  });

        }

        //*********************************************************************
        @Override
        public void onAccountCreationFailed()
        //*********************************************************************
        {
            UIUtils.displayAlertDialog(AndroidUtil.getString(R.string.email_exist),
                                       AndroidUtil.getString(R.string.signup),
                                       getActivity());
        }
    };

    //**********************************************************************
    private void createUserWithEmailAndPassword(@NonNull String email, @NonNull String
            password, @NonNull AccountRegistrationListener listener)
    //**********************************************************************
    {

        EditMe.instance()
              .getMAuth()
              .createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(getActivity(), task ->
              {
                  if (task.isSuccessful())
                  {
                      FirebaseUser user = EditMe.instance()
                                                .getMAuth()
                                                .getCurrentUser();
                      listener.onAccountCreatedSuccessfully(user.getUid());
                  }
                  else
                  {

                      if (task.getException() instanceof FirebaseAuthUserCollisionException)
                      {
                          mSignUpDialogBinding.email.setError(task.getException()
                                                                  .getMessage());
                          listener.onAccountCreationFailed();
                          return;
                      }
                      Toast.makeText(getActivity(),
                                     AndroidUtil.getString(
                                             R.string.authentication_failed),
                                     Toast.LENGTH_SHORT)
                           .show();
                  }
              });
    }


    //************************************************
    private void openLoginDialog()
    //************************************************
    {

    }

    //**********************************************
    interface AccountRegistrationListener
            //**********************************************
    {
        void onAccountCreatedSuccessfully(String userId);

        void onAccountCreationFailed();
    }

}
