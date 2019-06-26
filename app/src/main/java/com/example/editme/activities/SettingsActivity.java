package com.example.editme.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import lombok.NonNull;
import lombok.val;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.databinding.ActivitySettingsBinding;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;


//*****************************************************************
public class SettingsActivity
        extends AppCompatActivity
//*****************************************************************
{

    private ActivitySettingsBinding mBinding;

    //*****************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //*****************************************************************
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        initControls();
    }

    //*****************************************************************
    private void initControls()
    //*****************************************************************
    {

        setTab();
        mBinding.aboutUsLayout.setOnClickListener(view -> openAboutDialog());
        mBinding.privacyPolicyLayout.setOnClickListener(view -> gotoBrowserActivity());
        mBinding.userNameLayout.setOnClickListener(view -> gotoUpdateNameScreen(false));
        mBinding.emailLayout.setOnClickListener(view -> gotoUpdateNameScreen(true));
        mBinding.changePasswordLayout.setOnClickListener(view -> gotoUpdatePasswordScreen());
        mBinding.paymentsLayout.setOnClickListener(view -> {
            UIUtils.testToast(false,
                              "Package is not subscribed");
        });
        mBinding.logoutLayout.setOnClickListener(view -> logoutUser());

    }

    //*****************************************************************
    private void gotoUpdatePasswordScreen()
    //*****************************************************************
    {
        Intent updatePasswordIntent = new Intent(this, UpdatePasswordActivity.class);
        startActivity(updatePasswordIntent);
    }

    //*****************************************************************
    private void gotoUpdateNameScreen(boolean isEmailUpdate)
    //*****************************************************************
    {
        Intent updateIntent = new Intent(this, UpdateNameEmailActivity.class);
        updateIntent.putExtra(UpdateNameEmailActivity.UPDATE_FIELD, isEmailUpdate);
        startActivity(updateIntent);

    }

    //*****************************************************************
    private void gotoBrowserActivity()
    //*****************************************************************
    {
        Intent browserIntent = new Intent(this, BrowserActivity.class);
        browserIntent.putExtra(BrowserActivity.INFO_TITLE,
                               AndroidUtil.getString(R.string.privacy_policy));
        browserIntent.putExtra(BrowserActivity.SCREEN_URL,
                               AndroidUtil.getString(R.string.privacy_policy_url));
        startActivity(browserIntent);

    }

    //*****************************************************************
    private void logoutUser()
    //*****************************************************************
    {
        mBinding.logoutLayout.setOnClickListener(view -> {

            UIUtils.displayAlertDialog(AndroidUtil.getString(R.string.logout_dialog), null, this,
                                       new DialogInterface.OnClickListener()
                                       {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which)
                                           {
                                               if (which == -1)
                                               {
                                                   UIUtils.setPackageStatus(false);
                                                   EditMe.instance()
                                                         .getMAuth()
                                                         .signOut();
                                                   LoginManager.getInstance()
                                                               .logOut();
                                                   googleLogout();
                                                   openHomeActivity();
//                                                   SettingsActivity.super.onBackPressed();
                                               }
                                           }
                                       }, AndroidUtil.getString(R.string.yes),
                                       AndroidUtil.getString(R.string.no));

        });

    }

    private void openHomeActivity()
    {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    private void googleLogout()
    {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AndroidUtil.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        val googleSignInClient = GoogleSignIn.getClient(this, gso);
        // Google sign out
        googleSignInClient.signOut();
    }

    //*****************************************************************
    private void gotoLoginActivity()
    //*****************************************************************
    {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    //******************************************************************
    private void setTab()
    //******************************************************************
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    //************************************************
    private void openAboutDialog()
    //************************************************
    {
        final Dialog dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.about_dialog);
        TextView appVersion = dialog.findViewById(R.id.ap_version);
        appVersion.setText(UIUtils.getAppVersionString());
        dialog.show();
    }

    //******************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    //******************************************************************
    {
        if (item.getItemId() == android.R.id.home)
        {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
