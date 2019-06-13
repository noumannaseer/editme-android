package com.example.editme.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.R;
import com.example.editme.databinding.ListViewEditImagesBinding;
import com.example.editme.model.EditImage;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import lombok.NonNull;
import lombok.val;

//******************************************************************
public class AddImagesCustomAdapter
        extends RecyclerView.Adapter<AddImagesCustomAdapter.ViewHolder>
        //******************************************************************
{


    private List<EditImage> mImagesList;
    private ImageClickListener mImageClickListener;
    private boolean mShowDeleteIcon;
    private Activity mActivity;

    //**********************************************
    public AddImagesCustomAdapter(List<EditImage> imagesList, ImageClickListener imageClickListener, boolean showDeleteIcon, Activity activity)
    //**********************************************
    {
        mImagesList = imagesList;
        mImageClickListener = imageClickListener;
        mShowDeleteIcon = showDeleteIcon;
        mActivity = activity;

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
        ListViewEditImagesBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_view_edit_images, parent,
                false);
        ViewHolder holder = new ViewHolder(
                mBinding);
        return holder;
    }

    //**********************************************
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position)
    //**********************************************
    {
        final val item = mImagesList
                .get(position);

        holder.mBinding.addImage.setImageDrawable(item.getImageDrawable(mActivity));
        holder.mBinding.description.setText(item.getDescription());

        holder.mBinding.addImageMainView.setOnClickListener(view -> {
            if (mImageClickListener != null)
                mImageClickListener.onImageClick(position);
        });

        if (!mShowDeleteIcon)
            holder.mBinding.remove.setVisibility(View.GONE);
        else
            holder.mBinding.remove.setOnClickListener(view -> {
                if (mImageClickListener != null)
                    mImageClickListener.onImageDelete(position);

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
        ListViewEditImagesBinding mBinding;

        //**********************************************
        public ViewHolder(@NonNull ListViewEditImagesBinding itemView)
        //**********************************************
        {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }


    //******************************************************************
    public interface ImageClickListener
            //******************************************************************
    {
        void onImageClick(int index);

        void onImageDelete(int index);

    }

}
