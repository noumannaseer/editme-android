package com.example.editme.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.editme.R;
import com.example.editme.databinding.ListViewImagesBinding;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

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


    private List<String> mImagesList;
    private ImageClickListener mImageClickListener;

    //**********************************************
    public AddImagesCustomAdapter(List<String> imagesList, ImageClickListener imageClickListener)
    //**********************************************
    {
        mImagesList = imagesList;
        mImageClickListener = imageClickListener;

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
        ListViewImagesBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_view_images, parent,
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
        UIUtils.loadImages(item, holder.mBinding.addImage,
                           AndroidUtil.getDrawable(R.drawable.buttonshape));

    }

    private void deleteImageFromFireBase(int position)
    {
        val deleteReference = FirebaseStorage.getInstance()
                                             .getReferenceFromUrl(mImagesList.get(position));
        deleteReference.delete()
                       .addOnSuccessListener(new OnSuccessListener<Void>()
                       {
                           @Override
                           public void onSuccess(Void aVoid)
                           {
                           }
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
        ListViewImagesBinding mBinding;

        //**********************************************
        public ViewHolder(@NonNull ListViewImagesBinding itemView)
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
        void onAddImageClick();

        void onImageDelete(int index);

    }

}
