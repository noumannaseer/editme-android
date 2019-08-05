package com.example.editme.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PackagesDetails
        implements Parcelable
{
    String packageName;
    String packageDescription;
    double price;
    int totalImages;
    int remainingImages;

    public int getTotalImages()
    {
        return totalImages;
    }

    public void setTotalImages(int totalImages)
    {
        this.totalImages = totalImages;
    }

    public int getRemainingImages()
    {
        return remainingImages;
    }

    public void decrement(int value)
    {
        remainingImages = remainingImages - value;

    }

    public void setRemainingImages(int remainingImages)
    {
        this.remainingImages = remainingImages;
    }

    protected PackagesDetails(Parcel in)
    {
        packageName = in.readString();
        packageDescription = in.readString();
        price = in.readDouble();
        totalImages = in.readInt();
        remainingImages = in.readInt();
    }

    public static final Creator<PackagesDetails> CREATOR = new Creator<PackagesDetails>()
    {
        @Override
        public PackagesDetails createFromParcel(Parcel in)
        {
            return new PackagesDetails(in);
        }

        @Override
        public PackagesDetails[] newArray(int size)
        {
            return new PackagesDetails[size];
        }
    };

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public String getPackageDescription()
    {
        return packageDescription;
    }

    public void setPackageDescription(String packageDescription)
    {
        this.packageDescription = packageDescription;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }


    public PackagesDetails(String packageName, String packageDescription, float price)
    {
        this.packageName = packageName;
        this.packageDescription = packageDescription;
        this.price = price;
    }

    public PackagesDetails()
    {
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(packageName);
        dest.writeString(packageDescription);
        dest.writeDouble(price);
        dest.writeInt(totalImages);
        dest.writeInt(remainingImages);
    }
}
