package com.example.editme.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User
        implements Parcelable
{
    private String displayName;
    private String email;
    private String uid;
    private String photoUrl;

    public User(String displayName, String email, String uid, String photoUrl)
    {
        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl()
    {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl)
    {
        this.photoUrl = photoUrl;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public User(String displayName, String email, String uid)
    {
        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
    }

    public User()
    {
    }

    protected User(Parcel in)
    {
        displayName = in.readString();
        email = in.readString();
        uid = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(displayName);
        dest.writeString(email);
        dest.writeString(uid);
    }
}
