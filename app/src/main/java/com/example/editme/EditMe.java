package com.example.editme;

import android.app.Application;

import com.example.editme.utils.AndroidUtil;


//********************************************************************
public class EditMe
        extends Application
//********************************************************************
{

    //**************************************************************************
    @Override
    public void onCreate()
    //**************************************************************************
    {
        super.onCreate();
        AndroidUtil.setContext(this);
    }

}
