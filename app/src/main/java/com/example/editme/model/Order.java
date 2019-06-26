package com.example.editme.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.List;

public class Order
        implements Parcelable
{
    private String email;
    private String title;
    private String status;
    private String description;
    private Timestamp orderDate;
    private Timestamp dueDate;
    private List<OrderImages> images;
    private String orderId;


    protected Order(Parcel in)
    {
        email = in.readString();
        title = in.readString();
        status = in.readString();
        description = in.readString();
        orderDate = in.readParcelable(Timestamp.class.getClassLoader());
        dueDate = in.readParcelable(Timestamp.class.getClassLoader());
        images = in.createTypedArrayList(OrderImages.CREATOR);
        orderId = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>()
    {
        @Override
        public Order createFromParcel(Parcel in)
        {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size)
        {
            return new Order[size];
        }
    };

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Timestamp getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate)
    {
        this.orderDate = orderDate;
    }

    public Timestamp getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate)
    {
        this.dueDate = dueDate;
    }

    public List<OrderImages> getImages()
    {
        return images;
    }

    public void setImages(List<OrderImages> images)
    {
        this.images = images;
    }

    public Order()
    {
    }

    public Order(String email, String title, String status, String description, Timestamp orderDate, Timestamp dueDate, List<OrderImages> images)
    {
        this.email = email;
        this.title = title;
        this.status = status;
        this.description = description;
        this.orderDate = orderDate;
        this.dueDate = dueDate;
        this.images = images;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public Order(String email, String title, String status, String description, Timestamp orderDate, Timestamp dueDate, List<OrderImages> images, String orderId)
    {
        this.email = email;
        this.title = title;
        this.status = status;
        this.description = description;
        this.orderDate = orderDate;
        this.dueDate = dueDate;
        this.images = images;
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
        dest.writeString(email);
        dest.writeString(title);
        dest.writeString(status);
        dest.writeString(description);
        dest.writeParcelable(orderDate, flags);
        dest.writeParcelable(dueDate, flags);
        dest.writeTypedList(images);
        dest.writeString(orderId);
    }
}
