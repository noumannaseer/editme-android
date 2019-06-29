package com.example.editme.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderImages
        implements Parcelable
{

    private String description;
    private String url;
    private String image_id;

    public OrderImages()
    {
    }

    public OrderImages(String description, String url, String image_id)
    {
        this.description = description;
        this.url = url;
        this.image_id = image_id;
    }

    protected OrderImages(Parcel in)
    {
        description = in.readString();
        url = in.readString();
        image_id = in.readString();
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

    public String getImage_id()
    {
        return image_id;
    }

    public void setImage_id(String image_id)
    {
        this.image_id = image_id;
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
        dest.writeString(image_id);
    }
}

