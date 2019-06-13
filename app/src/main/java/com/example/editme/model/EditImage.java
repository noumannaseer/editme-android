package com.example.editme.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.IOException;
import java.net.URI;

import lombok.val;

import static com.example.editme.utils.AndroidUtil.getResources;

public class EditImage
        implements Parcelable

{
    private String description;
    private Uri imageIntentURI;

    public EditImage(String description, Uri imageIntentURI)
    {
        this.description = description;
        this.imageIntentURI = imageIntentURI;
    }

    protected EditImage(Parcel in)
    {
        description = in.readString();
        imageIntentURI = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<EditImage> CREATOR = new Creator<EditImage>()
    {
        @Override
        public EditImage createFromParcel(Parcel in)
        {
            return new EditImage(in);
        }

        @Override
        public EditImage[] newArray(int size)
        {
            return new EditImage[size];
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

    public Uri getImageIntentURI()
    {
        return imageIntentURI;
    }

    public void setImageIntentURI(Uri imageIntentURI)
    {
        this.imageIntentURI = imageIntentURI;
    }

    public Drawable getImageDrawable(Activity activity)
    {
        Bitmap bitmap = null;
        try
        {
            bitmap = MediaStore.Images.Media.getBitmap(
                    activity.getContentResolver(),
                    imageIntentURI);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        return d;

    }

    public EditImage()
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
        dest.writeString(description);
        dest.writeParcelable(imageIntentURI, flags);
    }
}
