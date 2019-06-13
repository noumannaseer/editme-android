package com.example.editme.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.editme.interfaces.ConnectionChangeCallback;


//**********************************************************************
public class NetworkChangeReceiver
        extends BroadcastReceiver
//**********************************************************************
{
    ConnectionChangeCallback mConnectionChangeCallback;

    //**********************************************************************
    @Override
    public void onReceive(Context context, Intent intent)
    //**********************************************************************
    {
        if (mConnectionChangeCallback != null)
        {
            mConnectionChangeCallback.onConnectionChanged(AndroidUtil.isNetworkStatusAvialable());
        }
    }

    //**********************************************************************
    public void setConnectionChangeCallback(ConnectionChangeCallback callback)
    //**********************************************************************
    {
        mConnectionChangeCallback = callback;
    }
}
