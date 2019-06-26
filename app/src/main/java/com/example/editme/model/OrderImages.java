package com.example.editme.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderImages
        implements Parcelable
{

    private String description;
    private String url;

    protected OrderImages(Parcel in)
    {
        description = in.readString();
        url = in.readString();
    }

    public static final Creator<OrderImages> CREATOR = new Creator<OrderImages>()
    {
        @Override
        public OrderImages createFromParcel(Parcel in)
        {
            return new OrderImages(in);
        }

        @Override
        public OrderImages[] newArray(int size)
        {
            return new OrderImages[size];
        }
    };

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }


    public OrderImages()
    {
    }

    public OrderImages(String description, String url)
    {
        this.description = description;
        this.url = url;

    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(description);
        dest.writeString(url);
    }
}

