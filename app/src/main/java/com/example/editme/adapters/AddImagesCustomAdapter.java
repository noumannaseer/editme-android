package com.example.editme.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.R;
import com.example.editme.databinding.ListViewEditImagesBinding;
import com.example.editme.databinding.ListViewOrderImagesBinding;
import com.example.editme.model.EditImage;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

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
    private boolean mShowProgress;

    //**********************************************
    public AddImagesCustomAdapter(List<EditImage> imagesList, ImageClickListener imageClickListener, boolean showDeleteIcon, Activity activity, boolean showProgress)
    //**********************************************
    {
        mImagesList = imagesList;
        mImageClickListener = imageClickListener;
        mShowDeleteIcon = showDeleteIcon;
        mActivity = activity;
        mShowDeleteIcon = showDeleteIcon;
        mShowProgress = showProgress;

    }

    //**********************************************
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    //**********************************************


    //*******************************-***************
    @Override
    public long getItemId(int position) {
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


        if (item.isUploading() == 0 && mShowProgress == true)
            holder.mBinding.progressBar.setVisibility(View.VISIBLE);
        else if (item.isUploading() == 1 && mShowProgress == true) {
            holder.mBinding.progressBar.setVisibility(View.GONE);
            holder.mBinding.progressView.setVisibility(View.VISIBLE);
            holder.mBinding.uploadCompleted.setVisibility(View.VISIBLE);
        } else if (item.isUploading() == -1) {
            holder.mBinding.progressView.setVisibility(View.GONE);
        }


//        holder.mBinding.addImage.setImageDrawable(item.getImageDrawable(mActivity));

        UIUtils.loadImages(item.getImageIntentURI().toString(), holder.mBinding.addImage, AndroidUtil.getDrawable(R.drawable.splash));
        holder.mBinding.description.setText(item.getDescription());

        holder.mBinding.addImageMainView.setOnClickListener(view -> {
            if (mImageClickListener != null)
                mImageClickListener.onImageClick(position);
        });
        if (!mShowDeleteIcon)
            holder.mBinding.remove.setVisibility(View.GONE);
        else {
            holder.mBinding.remove.setVisibility(View.VISIBLE);
            holder.mBinding.remove.setOnClickListener(view -> {
                if (mImageClickListener != null)
                    mImageClickListener.onImageDelete(position);
            });
        }
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
    public interface ImageClickListener
            //******************************************************************
    {
        void onImageClick(int index);

        void onImageDelete(int index);

    }

}
