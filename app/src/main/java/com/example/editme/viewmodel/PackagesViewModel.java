package com.example.editme.viewmodel;

import android.content.Context;

import com.example.editme.EditMe;
import com.example.editme.databinding.SignupDialogBinding;
import com.example.editme.model.PackagesDetails;
import com.example.editme.model.User;
import com.example.editme.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import lombok.Getter;
import lombok.val;
import retrofit2.http.GET;

public class PackagesViewModel
        extends ViewModel
{


    @Getter
    LiveData<List<PackagesDetails>> mPackagesList;

    MutableLiveData<List<PackagesDetails>> mPackages;
    Context mContext;

    //*********************************************************************************************************************
    public PackagesViewModel(Context context)
    //*********************************************************************************************************************
    {
        mContext = context;
        getPackagesList();
    }

    private void getPackagesList()
    {


        EditMe.instance()
              .getMFireStore()
              .collection(Constants.PACKAGES)
              .get()
              .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
              {
                  @Override
                  public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                  {

                      val packagesList = new ArrayList<PackagesDetails>();
                      for (val child : queryDocumentSnapshots.getDocuments())
                      {
                          PackagesDetails packagesDetails = child.toObject(PackagesDetails.class);
                          packagesList.add(packagesDetails);
                      }
                      mPackages.setValue(packagesList);
                      mPackagesList = mPackages;
                  }
              });

    }

}
