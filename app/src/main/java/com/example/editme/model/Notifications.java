package com.example.editme.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Notifications
        implements Parcelable
{
    private String title;
    private String body;
    private String orderId;

    public Notifications()
    {
    }

    protected Notifications(Parcel in)
    {
        title = in.readString();
        body = in.readString();
        orderId = in.readString();
    }

    public static final Creator<Notifications> CREATOR = new Creator<Notifications>()
    {
        @Override
        public Notifications createFromParcel(Parcel in)
        {
            return new Notifications(in);
        }

        @Override
        public Notifications[] newArray(int size)
        {
            return new Notifications[size];
        }
    };

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public Notifications(String title, String body, String orderId)
    {
        this.title = title;
        this.body = body;
        this.orderId = orderId;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(orderId);
    }
}
