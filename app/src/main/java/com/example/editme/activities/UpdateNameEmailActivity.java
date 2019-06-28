package com.example.editme.activities;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.databinding.ActivityUpdateNameEmailBinding;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import lombok.val;


//****************************************************************************
public class UpdateNameEmailActivity
        extends AppCompatActivity
//****************************************************************************
{

    private ActivityUpdateNameEmailBinding mBinding;
    public static final String UPDATE_FIELD = "UPDATE_FIELD";
    private boolean mIsEmailUpdate;

    //****************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //****************************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_name_email);
        initControls();
    }

    //****************************************************************************
    private void initControls()
    //****************************************************************************
    {
        setTab();
        getParcelable();
        mBinding.updateName.setOnClickListener(view -> updateField());

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

    //****************************************************************************
    private void updateField()
    //****************************************************************************
    {
        val name = mBinding.userName.getText()
                                    .toString();
        if (TextUtils.isEmpty(name))
        {
            mBinding.userName.setError(AndroidUtil.getString(R.string.required));
            return;
        }
        updateName(name);
    }


    //****************************************************************************
    private void updateName(String name)
    //****************************************************************************
    {
        Map<String, Object> updateName = new HashMap<>();
        updateName.put(Constants.DISPLAY_NAME, name);
        EditMe.instance()
              .getMFireStore()
              .collection(Constants.Users)
              .document(EditMe.instance()
                              .getMUserId())
              .update(updateName)
              .addOnCompleteListener(new OnCompleteListener<Void>()
              {
                  @Override
                  public void onComplete(@NonNull Task<Void> task)
                  {
                      if (task.isSuccessful())
                      {
                          finish();
                      }
                  }
              });
        UIUtils.testToast(false, AndroidUtil.getString(R.string.name_updated));
        finish();

    }

    private void updateEmail()
    {
        UIUtils.testToast(false, AndroidUtil.getString(R.string.email_updated));
        finish();

    }


    //****************************************************************************
    private void getParcelable()
    //****************************************************************************
    {
        if (getIntent().getExtras()
                       .containsKey(UPDATE_FIELD))
        {
            mIsEmailUpdate = getIntent().getExtras()
                                        .getBoolean(UPDATE_FIELD);

        }

    }
}
