package com.example.editme.model;

public class PackagesDetails
{
    String packageName;
    String packageDescription;
    float price;
    Integer imageResourceId;


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

    public float getPrice()
    {
        return price;
    }

    public void setPrice(float price)
    {
        this.price = price;
    }

    public Integer getImageResourceId()
    {
        return imageResourceId;
    }

    public void setImageResourceId(Integer imageResourceId)
    {
        this.imageResourceId = imageResourceId;
    }

    public PackagesDetails(String packageName, String packageDescription, float price, Integer imageResourceId)
    {
        this.packageName = packageName;
        this.packageDescription = packageDescription;
        this.price = price;
        this.imageResourceId = imageResourceId;
    }

    public PackagesDetails()
    {
    }
}
