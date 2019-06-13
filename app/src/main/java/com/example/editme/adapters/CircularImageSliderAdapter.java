package com.example.editme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.editme.R;
import com.example.editme.model.PackagesDetails;

import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

public class CircularImageSliderAdapter
        extends PagerAdapter
{

    List<PackagesDetails> lstImages;
    Context context;
    LayoutInflater layoutInflater;
    CircularSliderListener mListener;

    public CircularImageSliderAdapter(List<PackagesDetails> lstImages, Context context, CircularSliderListener listener)
    {
        this.lstImages = lstImages;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @Override
    public int getCount()
    {
        return lstImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mListener != null)
            mListener.onImageSlide(position);
        View view = layoutInflater.inflate(R.layout.list_view_images, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.add_image);
        imageView.setImageResource(lstImages.get(position)
                                            .getImageResourceId());
        container.addView(view);
        return view;
    }

    public interface CircularSliderListener
    {
        void onImageSlide(int position);
    }
}
