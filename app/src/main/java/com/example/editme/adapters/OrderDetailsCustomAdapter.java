package com.example.editme.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.editme.R;
import com.example.editme.activities.OrderDetailsActivity;
import com.example.editme.databinding.ListViewOrderDetailBinding;
import com.example.editme.model.Order;
import com.example.editme.model.OrderImages;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.UIUtils;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import lombok.NonNull;
import lombok.val;

public class OrderDetailsCustomAdapter
        extends RecyclerView.Adapter<OrderDetailsCustomAdapter.ViewHolder>
{

    private List<OrderImages> mOrderList;
    private Activity mActivity;
    private ImageClickListener mListener;

    //**********************************************
    public OrderDetailsCustomAdapter(List<OrderImages> orderList, Activity activity, ImageClickListener listener)
    //**********************************************
    {
        mOrderList = orderList;
        mActivity = activity;
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
        ListViewOrderDetailBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_view_order_detail, parent,
                false);
        ViewHolder holder = new ViewHolder(mBinding);
        return holder;
    }

    //**********************************************
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position)
    //**********************************************
    {
        final val item = mOrderList
                .get(position);
        UIUtils.loadImages(item.getUrl(), holder.mBinding.image,
                           AndroidUtil.getDrawable(R.drawable.splash));

        holder.mBinding.mainView.setOnClickListener(view -> {
            if (mListener != null)
                mListener.onImageClick(position);

        });

    }

    private void gotoOrderDetailScreen(Order item)
    {
        Intent orderDetailIntent = new Intent(mActivity, OrderDetailsActivity.class);
        orderDetailIntent.putExtra(OrderDetailsActivity.SELECTED_ORDER, item);
        mActivity.startActivityForResult(orderDetailIntent, 0);

    }


    //**********************************************
    @Override
    public int getItemCount()
    //**********************************************
    {
        return mOrderList.size();
    }

    //**********************************************
    public class ViewHolder
            extends RecyclerView.ViewHolder
            //**********************************************
    {
        ListViewOrderDetailBinding mBinding;

        //**********************************************
        public ViewHolder(@NonNull ListViewOrderDetailBinding itemView)
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
