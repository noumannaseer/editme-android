package com.example.editme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        View view = layoutInflater.inflate(R.layout.list_view_packages, container, false);
        TextView packageName = view.findViewById(R.id.package_type);
        TextView packageDescription = view.findViewById(R.id.package_detail);
        //    TextView packageType = view.findViewById(R.id.package_type);
        TextView packagePrice = view.findViewById(R.id.package_price);
        TextView totalImages = view.findViewById(R.id.total_images);

        packageName.setText(lstImages.get(position)
                                     .getPackageName());
        packageDescription.setText(lstImages.get(position)
                                            .getPackageDescription());
        packagePrice.setText("" + lstImages.get(position)
                                           .getPrice());
        totalImages.setText("" + lstImages.get(position)
                                          .getTotalImages());


        //ImageView imageView = view.findViewById(R.id.add_image);
        //imageView.setImageResource(lstImages.get(position)
        //.getImageResourceId());

        Button purchaseButton = view.findViewById(R.id.purchase);
        purchaseButton.setOnClickListener(view1 -> {
            if (mListener != null)
                mListener.onPurchaseClick(position);
        });
        container.addView(view);
        return view;
    }

    public interface CircularSliderListener
    {
        void onImageSlide(int position);

        void onPurchaseClick(int position);
    }
}
