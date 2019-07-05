package com.example.editme.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.adapters.CircularImageSliderAdapter;
import com.example.editme.databinding.ActivityPackagesBinding;
import com.example.editme.fragments.OrderFragment;
import com.example.editme.model.PackagesDetails;
import com.example.editme.model.User;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.PaymentsUtil;
import com.example.editme.utils.UIUtils;
import com.example.editme.viewmodel.LoginViewModel;
import com.example.editme.viewmodel.PackagesViewModel;
import com.example.editme.viewmodelfactory.LoginViewModelFactory;
import com.example.editme.viewmodelfactory.PackagesViewModelFactory;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import lombok.NonNull;
import lombok.val;

//*********************************************************************
public class PackagesActivity
        extends AppCompatActivity
        implements CircularImageSliderAdapter.CircularSliderListener
//*********************************************************************
{


    private ActivityPackagesBinding mBinding;
    private View mRootView;
    private List<String> mUrlList;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private PaymentsClient mPaymentsClient;
    private List<PackagesDetails> mPackagesList;

    private PackagesViewModel mPackagesViewModel;

    //*********************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //*********************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_packages);
        initControls();
    }


    //*********************************************************************
    private void initControls()
    //*********************************************************************
    {
       /* mPackagesViewModel = ViewModelProviders.of(PackagesActivity.this,
                                                   new PackagesViewModelFactory(this))
                                               .get(PackagesViewModel.class);


        mPackagesViewModel.getMPackagesList()
                          .observe(this, packagesDetails -> {

                              if (mPackagesList == null)
                                  return;
                              mPackagesList = packagesDetails;

                          });*/
        setTab();
        getPackagesList();
        mPaymentsClient =
                Wallet.getPaymentsClient(
                        this,
                        new Wallet.WalletOptions.Builder()
                                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                .build());

        mBinding.packagesPrice.setOnClickListener(view -> {
            requestPayment(getWindow().getDecorView()
                                      .getRootView());
            UIUtils.setPackageStatus(true);
        });
    }

    private void getPackagesList()
    {
        showProgressView();

        EditMe.instance()
              .getMFireStore()
              .collection(Constants.PACKAGES)
              .get()
              .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
              {
                  @Override
                  public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                  {

                      mPackagesList = new ArrayList<PackagesDetails>();
                      for (val child : queryDocumentSnapshots.getDocuments())
                      {
                          PackagesDetails packagesDetails = child.toObject(PackagesDetails.class);
                          mPackagesList.add(packagesDetails);
                      }
                      hideProgressView();
                      showDataOnRecyclerView();
                  }

              });

    }


    //*********************************************************************
    private void showDataOnRecyclerView()
    //*********************************************************************
    {

        CircularImageSliderAdapter adapter = new CircularImageSliderAdapter(mPackagesList,
                                                                            this,
                                                                            this);
        mBinding.horizontalCycle.setAdapter(adapter);
    }

    //*********************************************************************
    @Override
    public void onImageSlide(int position)
    //*********************************************************************
    {
        val packagesDetail = mPackagesList.get(position);
        mBinding.packageName.setText(packagesDetail.getPackageName());
        mBinding.packagesDetail.setText(packagesDetail.getPackageDescription());
        mBinding.packagesPrice.setText("$" + packagesDetail.getPrice());

    }

    @Override
    public void onPurchaseClick(int position)
    {

        Map<String, Object> update = new HashMap<>();
        update.put("currentPackage", mPackagesList.get(position));


        EditMe.instance()
              .getMFireStore()
              .collection(Constants.Users)
              .document(EditMe.instance()
                              .getMUserId())
              .update(update)
              .addOnSuccessListener(new OnSuccessListener<Void>()
              {
                  @Override
                  public void onSuccess(Void aVoid)
                  {
                      AndroidUtil.toast(false,
                                        AndroidUtil.getString(R.string.package_buy_successfully));
                      finish();
                  }
              });
       /* requestPayment(getWindow().getDecorView()
                                  .getRootView());*/
        UIUtils.setPackageStatus(true);

    }


    //******************************************************************
    private void setTab()
    //******************************************************************
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //******************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    //******************************************************************
    {
        if (item.getItemId() == android.R.id.home)
        {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    //*********************************************************************
    public void requestPayment(View view)
    //*********************************************************************

    {
        // Disables the button to prevent multiple clicks.

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        String price = PaymentsUtil.microsToString(100000);

        // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent())
        {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get()
                                                                  .toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null)
        {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }


    //*****************************************
    private void showProgressView()
    //*****************************************
    {
        mBinding.progressView.setVisibility(View.VISIBLE);

    }

    //*****************************************
    private void hideProgressView()
    //*****************************************
    {
        mBinding.progressView.setVisibility(View.GONE);

    }

    //**********************************************************************
    private void possiblyShowGooglePayButton()
    //**********************************************************************
    {
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent())
        {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get()
                                                                                   .toString());
        if (request == null)
        {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                                   new OnCompleteListener<Boolean>()
                                   {
                                       @Override
                                       public void onComplete(@NonNull Task<Boolean> task)
                                       {
                                           if (task.isSuccessful())
                                           {
                                               AndroidUtil.toast(false, AndroidUtil.getString(
                                                       R.string.google_pay_is_available));
                                           }
                                           else
                                           {
                                               Log.w(AndroidUtil.getString(
                                                       R.string.is_ready_to_pay_failed),
                                                     task.getException());
                                           }
                                       }
                                   });
    }


    //**********************************************************************
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    //**********************************************************************
    {
        switch (requestCode)
        {
        // value passed in AutoResolveHelper
        case LOAD_PAYMENT_DATA_REQUEST_CODE:
            switch (resultCode)
            {
            case Activity.RESULT_OK:
                PaymentData paymentData = PaymentData.getFromIntent(data);
                handlePaymentSuccess(paymentData);
                break;
            case Activity.RESULT_CANCELED:
                // Nothing to here normally - the user simply cancelled without selecting a
                // payment method.
                break;
            case AutoResolveHelper.RESULT_ERROR:
                Status status = AutoResolveHelper.getStatusFromIntent(data);
                handleError(status.getStatusCode());
                break;
            default:
                // Do nothing.
            }

            // Re-enables the Google Pay payment button.
            break;
        }
    }

    //**********************************************************************
    private void handlePaymentSuccess(PaymentData paymentData)
    //**********************************************************************
    {
        String paymentInformation = paymentData.toJson();

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        if (paymentInformation == null)
        {
            return;
        }
        JSONObject paymentMethodData;

        try
        {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject(
                    "paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken"))
            {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "GooglePayConstants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

            String billingName =
                    paymentMethodData.getJSONObject("info")
                                     .getJSONObject("billingAddress")
                                     .getString("name");
            Log.d("BillingName", billingName);
            Toast.makeText(this, getString(R.string.payments_show_name, billingName),
                           Toast.LENGTH_LONG)
                 .show();

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData")
                                                         .getString("token"));
        }
        catch (JSONException e)
        {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }

    //**********************************************************************
    private void handleError(int statusCode)
    //**********************************************************************
    {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }
}
