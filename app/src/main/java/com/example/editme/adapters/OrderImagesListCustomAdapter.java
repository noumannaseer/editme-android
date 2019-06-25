package com.example.editme.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.R;
import com.example.editme.databinding.ListViewOrderImagesBinding;
import com.example.editme.model.OrderImages;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import lombok.NonNull;
import lombok.val;

public class OrderImagesListCustomAdapter
        extends RecyclerView.Adapter<OrderImagesListCustomAdapter.ViewHolder>
{

    private List<OrderImages> mImagesList;
    private UpdateImageClickListener mListener;
    private boolean mShowDeleteIcon;
    private Activity mActivity;


    //**********************************************
    public OrderImagesListCustomAdapter(List<OrderImages> imagesList, UpdateImageClickListener listener)
    //**********************************************
    {
        mImagesList = imagesList;
        mListener = listener;
    }

    //**********************************************
    @Override
    public int getItemViewType(int position)
    {
        return position;
    }
    //**********************************************


    //*******************************-***************
    @Override
    public long getItemId(int position)
    {
        return super.getItemId(position);
    }
    //**********************************************


    //**********************************************
    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType)
    //**********************************************
    {
        ListViewOrderImagesBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_view_order_images, parent,
                false);
        ViewHolder holder = new ViewHolder(mBinding);
        return holder;
    }

    //**********************************************
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position)
    //**********************************************
    {
        final val item = mImagesList
                .get(position);
        holder.mBinding.description.setText(item.getDescription());
        UIUtils.loadImages(item.getUrl(), holder.mBinding.addImage,
                           AndroidUtil.getDrawable(R.drawable.splash));
        holder.mBinding.remove.setVisibility(View.GONE);
        holder.mBinding.addImageMainView.setOnClickListener(view -> {
            if (mListener != null)
                mListener.onUpdateImageClick(position);
        });
    }


    //**********************************************
    @Override
    public int getItemCount()
    //**********************************************
    {
        return mImagesList.size();
    }

    //**********************************************
    public class ViewHolder
            extends RecyclerView.ViewHolder
            //**********************************************
    {
        ListViewOrderImagesBinding mBinding;

        //**********************************************
        public ViewHolder(@NonNull ListViewOrderImagesBinding itemView)
        //**********************************************
        {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }


    //******************************************************************
    public interface UpdateImageClickListener
            //******************************************************************
    {
        void onUpdateImageClick(int index);
    }

}
