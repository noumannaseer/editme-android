package com.example.editme.activities;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.editme.EditMe;
import com.example.editme.R;
import com.example.editme.databinding.ActivityPaymentCardBinding;
import com.example.editme.interfaces.ConnectionChangeCallback;
import com.example.editme.model.PackagesDetails;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.Constants;
import com.example.editme.utils.UIUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;
import com.stripe.android.Stripe;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.CardException;
import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.model.Card;

import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import lombok.val;


// ******************************************************************
public class PaymentCardActivity
        extends AppCompatActivity
        implements ConnectionChangeCallback
        // ******************************************************************
{


    private ActivityPaymentCardBinding mBinding;
    private PackagesDetails mPackageDetail;
    public static final String CURRENT_PACKAGE = "CURRENT_PACKAGE";


    // ******************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    // ******************************************************************
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment_card);
        initControls();
    }

    // ******************************************************************
    private void initControls()
    // ******************************************************************
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getParecelable();
        if (mPackageDetail == null)
            return;
        if (!AndroidUtil.isNetworkStatusAvailable()) {
            UIUtils.showSnackBar(PaymentCardActivity.this,
                    AndroidUtil.getString(R.string.internet_is_not_available));
            return;

        }
        mBinding.payment.setOnClickListener(view -> performPayment());
        showPaymentDetail();

    }

    private void getParecelable() {

        if (getIntent().getExtras().containsKey(CURRENT_PACKAGE)) {
            mPackageDetail = getIntent().getParcelableExtra(CURRENT_PACKAGE);
        }

    }

    private void showPaymentDetail() {
//        mBookingInformation = AtlSafe.instance()
//                .getMBookSafeInformation();
//        if (mBookingInformation == null)
//            return;
//        mBinding.selectedHour.setText("" + mBookingInformation.getPickup_hourly_plan());
//        mBinding.paymentRate.setText("$" + mBookingInformation.getPrice());
//        mBinding.total.setText(
//                "$" + (mBookingInformation.getPickup_hourly_plan() * mBookingInformation.getPrice()));

    }


    //******************************************************************
    public void chargeAmount(@NonNull String token)
    //******************************************************************
    {


        // Create the arguments to the callable function.
        Log.d("test-tag", "========callCloudFunction========");
        Log.d("test-tag", "token-> " + token);

        int ammount = (int) mPackageDetail.getPrice() * 100;
        val functions = EditMe.instance()
                .getFirebaseFunctions();
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("currency", "usd");
        data.put("amount", ammount);
        data.put("description", "charge test");
        data.put("statement_descriptor", "atl safe rental");
        data.put("uid", EditMe.instance()
                .getMUserId());


        //val data = getLocationID();
        functions.getHttpsCallable(Constants.FUNCTION_CHARGE_AMOUNT)
                .call(data)
                .addOnCompleteListener(task ->

                {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        e.printStackTrace();
                        if (e instanceof FirebaseFunctionsException) {
                            e.printStackTrace();
                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                            FirebaseFunctionsException.Code code = ffe.getCode();
                            if (code == FirebaseFunctionsException.Code.INTERNAL)
                                Log.d("test-tag",
                                        "data => " + e.getMessage());
                        }
                    } else {
                        Log.d("test-tag", "success!!");
                        Log.d("test-tag", "data => " + task.getResult()
                                .getData());
                        Gson g = new Gson();
                        val dataString = task.getResult()
                                .getData();
                        val datajson = g.toJson(dataString);
                        Log.d("test-tag", "datajson => " + datajson);
                        updatePackageInDataBase();

                    }
                });
    }

    private void updatePackageInDataBase() {
        Map<String, Object> update = new HashMap<>();
        update.put("currentPackage", mPackageDetail);
        EditMe.instance()
                .getMFireStore()
                .collection(Constants.Users)
                .document(EditMe.instance()
                        .getMUserId())
                .update(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mBinding.progressBar.setVisibility(View.INVISIBLE);
                        mBinding.payedSuccessfully.setText(AndroidUtil.getString(R.string.payed_sussfully));
                        AndroidUtil.handler.postDelayed(() -> {
                            finish();
                        }, 1000);
//
                    }
                });
    }


    // ******************************************************************
    @MainThread
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    // ******************************************************************
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //********************************************************
    private void performPayment()
    //********************************************************
    {
        Card card = mBinding.cardInputWidget.getCard();
        if (card == null) {
            UIUtils.showSnackBar(PaymentCardActivity.this,
                    AndroidUtil.getString(R.string.card_is_invalid));
            return;
        }
        Stripe stripe;
        stripe = new Stripe(PaymentCardActivity.this, EditMe.instance()
                .getMStripeKey());

        StripeCardCreator cardCreator = new StripeCardCreator(PaymentCardActivity.this, stripe,
                EditMe.instance()
                        .getMStripeKey());
        cardCreator.execute(card);
        showProgressView();
    }

    //*********************************************************************
    private void showProgressView()
    //*********************************************************************
    {
        mBinding.progressView.setVisibility(View.VISIBLE);

    }

    //*********************************************************************
    private void hideProgressView()
    //*********************************************************************
    {
        mBinding.progressView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {

    }


    //********************************************************
    private class StripeCardCreator
            extends AsyncTask<Card, Void, String>
            //********************************************************
    {
        private Stripe mStripe;
        private String publishedKey;
        PaymentCardActivity mActivity;
        String tokenError = null;
        boolean isError = false;

        //********************************************************
        public StripeCardCreator(PaymentCardActivity activity, Stripe stripe, String key)
        //********************************************************
        {
            mActivity = activity;
            mStripe = stripe;
            publishedKey = key;
        }


        //********************************************************
        @Override
        protected String doInBackground(Card... cards)
        //********************************************************
        {

            String stripeToken = null;
            val stripeCard = cards[0];
            try {
                val token = mStripe.createTokenSynchronous(stripeCard, publishedKey);
                if (token != null)
                    stripeToken = token.getId();

            } catch (AuthenticationException e) {
                tokenError = e.getLocalizedMessage();
            } catch (InvalidRequestException e) {
                tokenError = e.getLocalizedMessage();
            } catch (APIConnectionException e) {
                tokenError = e.getLocalizedMessage();
            } catch (CardException e) {
                tokenError = e.getLocalizedMessage();
            } catch (APIException e) {
                tokenError = e.getLocalizedMessage();
            }
            if (!TextUtils.isEmpty(tokenError)) {
                isError = true;
                stripeToken = tokenError;
            }


            return stripeToken;
        }


        //********************************************************
        @Override
        protected void onPostExecute(String token)
        //********************************************************
        {
            super.onPostExecute(token);
            if (isError) {
                mActivity.hideProgressView();
                AndroidUtil.toast(false, token);
            } else
                mActivity.chargeAmount(token);
        }
    }

}
