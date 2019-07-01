package com.example.editme.viewmodel;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.databinding.SignupDialogBinding;
import com.example.editme.model.User;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import lombok.NonNull;
import lombok.val;


//*********************************************************************************************************************
public class SignUpViewModel
        extends ViewModel
//*********************************************************************************************************************
{

    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public User user;
    private Context context;
    private SignupDialogBinding mSignupDialogBinding;
    private SignUpSuccessListener mListener;

    //*********************************************************************************************************************
    public SignUpViewModel(User user, Context context, SignupDialogBinding signupDialogBinding, SignUpSuccessListener listener)
    //*********************************************************************************************************************
    {
        this.user = user;
        this.context = context;
        mSignupDialogBinding = signupDialogBinding;
        mListener = listener;
    }


    /*public void onSignUpClick(View view)
    {
        AndroidUtil.toast(false, "SignUp clicked");

    }*/

    //*********************************************************************************************************************
    public void onSignUpClick(View view)
    //*********************************************************************************************************************
    {

        if (TextUtils.isEmpty(name.getValue()))
        {
            mSignupDialogBinding.name.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        if (TextUtils.isEmpty(email.getValue()))
        {
            mSignupDialogBinding.email.setError(AndroidUtil.getString(R.string.required));
            return;
        }

        if (!UIUtils.isValidEmailId(email.getValue()))
        {
            mSignupDialogBinding.email.setError(AndroidUtil.getString(R.string.email_format));
            return;
        }

        if (TextUtils.isEmpty(password.getValue()))
        {
            mSignupDialogBinding.password.setError(AndroidUtil.getString(R.string.required));
            return;
        }


        if (password.getValue()
                    .length() < 8)
        {
            mSignupDialogBinding.password.setError(AndroidUtil.getString(R.string.password_length));
            return;
        }

        mSignupDialogBinding.progressView.setVisibility(View.VISIBLE);
        createUserWithEmailAndPassword(email.getValue(), password.getValue(),
                                       mAccountRegistrationListener);

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
            val user = new User(name.getValue(), email.getValue(), userId, null, null);
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
                                              mSignupDialogBinding.progressView.setVisibility(
                                                      View.GONE);
                                              UIUtils.displayAlertDialog(
                                                      AndroidUtil.getString(
                                                              R.string.verify_acount)
                                                      , AndroidUtil.getString(
                                                              R.string.account_created),
                                                      context,
                                                      (dialog, which) ->
                                                      {
                                                          EditMe.instance()
                                                                .getMAuth()
                                                                .signOut();

                                                          if (mListener != null)
                                                              mListener.onSignUpSuccessListener();

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
                                       context);
            mSignupDialogBinding.progressView.setVisibility(View.GONE);

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
              .addOnCompleteListener(task -> {
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
                          mSignupDialogBinding.email.setError(task.getException()
                                                                  .getMessage());
                          listener.onAccountCreationFailed();
                          return;
                      }
                      Toast.makeText(context,
                                     AndroidUtil.getString(
                                             R.string.authentication_failed),
                                     Toast.LENGTH_SHORT)
                           .show();
                  }
              });
    }

    //**********************************************
    interface AccountRegistrationListener
            //**********************************************
    {
        void onAccountCreatedSuccessfully(String userId);

        void onAccountCreationFailed();
    }


    public interface SignUpSuccessListener
    {
        void onSignUpSuccessListener();
    }
}
