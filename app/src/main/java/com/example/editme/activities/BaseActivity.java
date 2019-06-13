package com.example.editme.activities;


import android.view.MenuItem;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import lombok.NonNull;

// ******************************************************************
public class BaseActivity
        extends AppCompatActivity
// ******************************************************************
{
    // ******************************************************************
    @MainThread
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    // ******************************************************************
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            // go to previous screen when app icon in action bar is clicked
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
