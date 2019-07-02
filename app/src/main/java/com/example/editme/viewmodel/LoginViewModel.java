package com.example.editme.viewmodel;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.databinding.LoginDialogBinding;
import com.example.editme.model.User;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


//****************************************************
public class LoginViewModel
        extends ViewModel
//****************************************************
{

    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public User user;
    private Context context;
    private LoginDialogBinding mLoginDialogBinding;
    private LoginSuccessListener mListener;

    //*********************************************************************************************************************
    public LoginViewModel(User user, Context context, LoginDialogBinding loginDialogBinding, LoginSuccessListener listener)
    //*********************************************************************************************************************
    {
        this.user = user;
        this.context = context;
        mLoginDialogBinding = loginDialogBinding;
        mListener = listener;
    }


    //*********************************************************************************************************************
    public void onLoginClick(View view)
    //*********************************************************************************************************************
    {

        if (TextUtils.isEmpty(email.getValue()))
        {
            mLoginDialogBinding.loginEmail.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        if (!UIUtils.isValidEmailId(email.getValue()))
        {
            mLoginDialogBinding.loginEmail.setError(AndroidUtil.getString(R.string.email_format));
            return;
        }

        if (TextUtils.isEmpty(password.getValue()))
        {
            mLoginDialogBinding.password.setError(AndroidUtil.getString(R.string.required));
            return;
        }


        if (password.getValue()
                    .length() < 8)
        {
            mLoginDialogBinding.password.setError(AndroidUtil.getString(R.string.password_length));
            return;
        }


        mLoginDialogBinding.progressView.setVisibility(
                View.VISIBLE);

        EditMe.instance()
              .getMAuth()
              .signInWithEmailAndPassword(email.getValue(), password.getValue())
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
                                                UIUtils.displayAlertDialog(AndroidUtil.getString(
                                                        R.string.verify_acount),
                                                                           null,
                                                                           context);
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
                                                UIUtils.displayAlertDialog(AndroidUtil.getString(
                                                        R.string.verify_acount),
                                                                           null,
                                                                           context);
                                                mLoginDialogBinding.progressView.setVisibility(
                                                        View.GONE);

                                            }
                                        });

                      }

                      else
                      {


                          String instanceId = FirebaseInstanceId.getInstance()
                                                                .getToken();
                          Map<String, Object> fcmToken = new HashMap<>();
                          fcmToken.put(Constants.FCM_TOKEN, instanceId);
                          EditMe.instance()
                                .loadUserDetail();
                          EditMe.instance()
                                .getMFireStore()
                                .collection(Constants.Users)
                                .document(EditMe.instance()
                                                .getMUserId())
                                .update(fcmToken)
                                .addOnCompleteListener(
                                        new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@androidx.annotation.NonNull Task<Void> task)
                                            {
                                                AndroidUtil.toast(false, "Login successfully");
                                            }
                                        });

                          UIUtils.setUserRemember(true);
//F                          UIUtils.testToast(false, "login successfulls");
                          mLoginDialogBinding.progressView.setVisibility(View.GONE);
                          EditMe.instance()
                                .loadUserDetail();
                          if (mListener != null)
                          {
                              mListener.onLoginSuccessFull();
                          }
                      }
                  }
                  else
                  {
                      UIUtils.displayAlertDialog(
                              AndroidUtil.getString(R.string.invalid_email_password),
                              AndroidUtil.getString(R.string.login_error),
                              context);
                      mLoginDialogBinding.progressView.setVisibility(View.GONE);

                  }

              });
    }


    public interface LoginSuccessListener
    {
        public void onLoginSuccessFull();
    }

}
