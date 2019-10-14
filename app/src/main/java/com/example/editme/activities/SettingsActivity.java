package com.example.editme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import lombok.val;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.databinding.ActivitySettingsBinding;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;


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


    /*    for (UserInfo user : FirebaseAuth.getInstance()
                                         .getCurrentUser()
                                         .getProviderData())*/

        UserInfo user = FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getProviderData()
                                    .get(1);
        //  {
        mBinding.userName.setText(EditMe.instance()
                                        .getMUserDetail()
                                        .getDisplayName());
        if (UIUtils.getLoginType() == 0)
            hideNamePassword();
        else if (UIUtils.getLoginType() == 1)
            hideNamePassword();
        // }
        setTab();
        mBinding.aboutUsLayout.setOnClickListener(view -> openAboutDialog());
        mBinding.privacyPolicyLayout.setOnClickListener(view -> gotoBrowserActivity());
        mBinding.userNameLayout.setOnClickListener(view -> gotoUpdateNameScreen(false));
        mBinding.emailLayout.setOnClickListener(view -> gotoUpdateNameScreen(true));
        mBinding.changePasswordLayout.setOnClickListener(view -> gotoUpdatePasswordScreen());
        mBinding.howToUse.setOnClickListener(view -> gotoVisitScreen());

        mBinding.paymentsLayout.setOnClickListener(view -> {

            if (EditMe.instance()
                      .getMUserDetail()
                      .getCurrentPackage() == null)
                UIUtils.testToast(false,
                                  "Package is not subscribed");
            else
                gotoCurrentPackageScree();
        });
        mBinding.logoutLayout.setOnClickListener(view -> logoutUser());

    }

    private void gotoVisitScreen()
    {
        Intent introWizardIntent = new Intent(this, IntroSliderActivity.class);
        startActivity(introWizardIntent);
    }

    private void gotoCurrentPackageScree()
    {
        Intent currenPackageIntent = new Intent(this, CurrentPackageActivity.class);
        startActivity(currenPackageIntent);
    }


    //****************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    //****************************************************************************************
    {
        super.onActivityResult(requestCode, resultCode, data);
        mBinding.userName.setText(EditMe.instance()
                                        .getMUserDetail()
                                        .getDisplayName());
    }

    //****************************************************************************************
    private void hideNamePassword()
    //****************************************************************************************
    {
        mBinding.changePasswordLayout.setVisibility(View.GONE);
        mBinding.userNameLayout.setVisibility(View.GONE);

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
        startActivityForResult(updateIntent, 0);

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
                                       AndroidUtil.getString(R.string.yes),
                                       AndroidUtil.getString(R.string.no),
                                       new DialogInterface.OnClickListener()
                                       {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which)
                                           {
                                               if (which == -1)
                                               {
                                                   UIUtils.setPackageStatus(
                                                           false);
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
                                       });

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
