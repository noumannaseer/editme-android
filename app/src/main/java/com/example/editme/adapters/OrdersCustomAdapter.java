package com.example.editme.adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.editme.R;
import com.example.editme.activities.OrderDetailsActivity;
import com.example.editme.databinding.ListViewOrdersBinding;
import com.example.editme.model.Order;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import lombok.NonNull;
import lombok.val;

public class OrdersCustomAdapter
        extends RecyclerView.Adapter<OrdersCustomAdapter.ViewHolder>
{

    private List<Order> mOrderList;
    private Activity mActivity;

    //**********************************************
    public OrdersCustomAdapter(List<Order> orderList, Activity activity)
    //**********************************************
    {
        mOrderList = orderList;
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
        ListViewOrdersBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_view_orders, parent,
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
        holder.mBinding.totalImages.setText(AndroidUtil.getString(R.plurals.photo_count_template,
                                                                  item.getImages()
                                                                      .size()));
        holder.mBinding.orderDescription.setText(item.getDescription());
        holder.mBinding.postedDate.setText(UIUtils.getDate(item.getOrderDate()
                                                               .getSeconds()));
        holder.mBinding.remainingTime.setText(UIUtils.getRemainingTime(item.getDueDate()
                                                                           .getSeconds()));
        if (!item.getStatus()
                 .equals(Constants.STATUS_PENDING))
            holder.mBinding.mainView.setOnClickListener(view -> gotoOrderDetailScreen(item));

        holder.mBinding.orderStatus.setText(item.getStatus());
        if (item.getStatus()
                .equals(Constants.STATUS_COMPLETE))
        {
            holder.mBinding.remainingTime.setVisibility(View.GONE);
        }
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
        ListViewOrdersBinding mBinding;

        //**********************************************
        public ViewHolder(@NonNull ListViewOrdersBinding itemView)
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
