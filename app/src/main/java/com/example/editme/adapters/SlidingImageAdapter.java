package com.example.editme.adapters;


import android.content.Context;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.editme.R;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import static com.example.editme.utils.AndroidUtil.getResources;
import static com.example.editme.utils.AndroidUtil.getWindowManager;


/**
 * Created by Parsania Hardik on 23/04/2016.
 */
public class SlidingImageAdapter
        extends PagerAdapter
{


    private String[] urls;
    private LayoutInflater inflater;
    private Context context;
    private SliderTouchListener listener;


    public SlidingImageAdapter(Context context, String[] urls, SliderTouchListener listener)
    {
        this.context = context;
        this.urls = urls;

        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View)object);
    }

    @Override
    public int getCount()
    {
        return urls.length;
    }


    public interface SliderTouchListener
    {
        void onPressed();

        void onModifyClick(int index);

        void onDownloadClick(int index);

        void onRelease();

    }

    @Override
    public Object instantiateItem(ViewGroup view, int position)
    {

//        AndroidUtil.toast(false, "" + getDeviceWidth());

        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView)imageLayout
                .findViewById(R.id.image);

        final TextView modify = (TextView)imageLayout.findViewById(R.id.modify);
        final CardView download = (CardView)imageLayout.findViewById(R.id.download);

        modify.setOnClickListener(vieww -> listener.onModifyClick(position));
        download.setOnClickListener(vieww -> listener.onDownloadClick(position));


//        imageView.getLayoutParams().height = (int)(getDeviceWidth() * 0.6);

        imageLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                case MotionEvent.ACTION_DOWN:
                    if (listener != null)
                        listener.onPressed();
                    return true;
                case MotionEvent.ACTION_UP:
                    if (listener != null)
                        listener.onRelease();

                    return true;
                }
                return false;
            }
        });


        UIUtils.loadImages(urls[position], imageView,
                           AndroidUtil.getDrawable(R.drawable.image_load_progress));


        view.addView(imageLayout, 0);

        return imageLayout;
    }

    private float getDeviceWidth()
    {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
        return dpWidth;
    }


    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader)
    {
    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }

}