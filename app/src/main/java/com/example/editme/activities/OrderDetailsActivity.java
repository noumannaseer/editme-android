package com.example.editme.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.editme.R;
import com.example.editme.databinding.ActivityOrderDetailsBinding;


//*********************************************************************
public class OrderDetailsActivity
        extends AppCompatActivity
//*********************************************************************
{


    private ActivityOrderDetailsBinding mBinding;

    //*********************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //*********************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        initControls();

    }

    //*********************************************************************
    private void initControls()
    //*********************************************************************
    {

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //**************************************************************
    public boolean onOptionsItemSelected(MenuItem item)
    //**************************************************************
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            super.onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
