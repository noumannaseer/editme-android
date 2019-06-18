package com.example.editme.activities;

import android.os.Bundle;

import com.example.editme.R;
import com.example.editme.adapters.AddImagesCustomAdapter;
import com.example.editme.databinding.ActivityCheckoutBinding;
import com.example.editme.model.EditImage;
import com.example.editme.utils.UIUtils;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;


//******************************************************************
public class CheckOutActivity
        extends AppCompatActivity
        implements AddImagesCustomAdapter.ImageClickListener
//******************************************************************
{
    private ActivityCheckoutBinding mBinding;
    public static final String IMAGES_LIST = "IMAGES_LIST";
    public static final String ORDER_DESCRIPTION = "ORDER_DESCRIPTION";
    private List<EditImage> mEditImageList;
    private String mOrderDescription;
    private AddImagesCustomAdapter mAddImagesCustomAdapter;


    //******************************************************************
    @Override

    protected void onCreate(Bundle savedInstanceState)
    //******************************************************************

    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);
        initControls();
        if (mEditImageList != null)
        {
            showDataOnRecyclerView();
            mBinding.description.setText("" + mOrderDescription);
        }

    }

    //******************************************************************
    private void initControls()
    //******************************************************************
    {
        getParcelable();

    }

    private void getParcelable()
    {
        if (getIntent().getExtras()
                       .containsKey(IMAGES_LIST) && getIntent().getExtras()
                                                               .containsKey(ORDER_DESCRIPTION))
        {
            mOrderDescription = getIntent().getExtras()
                                           .getString(ORDER_DESCRIPTION);
            mEditImageList = getIntent().getExtras()
                                        .getParcelableArrayList(IMAGES_LIST);
            mBinding.totalImages.setText("" + mEditImageList.size());
        }

    }

    //******************************************************************
    private void showDataOnRecyclerView()
    //******************************************************************
    {


        mAddImagesCustomAdapter = new AddImagesCustomAdapter(mEditImageList,
                                                             this, false, this);
        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerView.setAdapter(mAddImagesCustomAdapter);

    }

    @Override
    public void onImageClick(int index)
    {

    }

    @Override
    public void onImageDelete(int index)
    {

    }
}
