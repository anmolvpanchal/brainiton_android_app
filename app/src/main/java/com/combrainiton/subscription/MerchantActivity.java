package com.combrainiton.subscription;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.combrainiton.R;
import com.combrainiton.managers.UserManagementInterface;
import com.combrainiton.models.CheckSumModel;
import com.combrainiton.models.UserResponseModel;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is the sample app which will make use of the PG SDK. This activity will
 * show the usage of Paytm PG SDK API's.
 **/

public class MerchantActivity extends Activity {

    String mobileNo, checkSumHash, customerId, orderId, apiToken;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static String TAG = "MerchantActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_tm_gateway);
        // initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // shared prefrence data retrival
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        //getting userid
        apiToken = "8d7e1d81-ae5d-41af-b459-0e6254288056";
        Log.e(TAG, "user token madeyo  " + apiToken);

        if (!(apiToken == "")){

            getCheckSum();
            getUserPhoneNo();

        }
        else {
            Log.e(TAG,"null" + apiToken);
        }


    }

    //This is to refresh the order id: Only for the Sample App's purpose.
    @Override
    protected void onStart() {
        super.onStart();
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


   /* private void initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        String orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
        EditText orderIdEditText = (EditText) findViewById(R.id.order_id);
        orderIdEditText.setText(orderId);
    }*/

    public void onStartTransaction() {
        PaytmPGService Service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters

        paramMap.put("CALLBACK_URL", "http://13.235.33.12:8000/api/subs/subscribe/checksum/verify/");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("CHECKSUMHASH", checkSumHash);
        paramMap.put("CUST_ID", customerId);
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("MID", "uIESTN63010202657383");
        paramMap.put("ORDER_ID", orderId);
        paramMap.put("TXN_AMOUNT", "1");
        paramMap.put("WEBSITE", "WEBSTAGING");

/*
        paramMap.put("MID" , "WorldP64425807474247");
        paramMap.put("ORDER_ID" , "210lkldfka2a27");
        paramMap.put("CUST_ID" , "mkjNYC1227");
        paramMap.put("INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put("CHANNEL_ID" , "WAP");
        paramMap.put("TXN_AMOUNT" , "1");
        paramMap.put("WEBSITE" , "worldpressplg");
        paramMap.put("CALLBACK_URL" , "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");*/


        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);

		/*PaytmMerchant Merchant = new PaytmMerchant(
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");*/

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.

                        Log.e(TAG, "someUIErrorOccurred" + inErrorMessage);

                    }

					/*@Override
					public void onTransactionSuccess(Bundle inResponse) {
						// After successful transaction this method gets called.
						// // Response bundle contains the merchant response
						// parameters.
						Log.d("LOG", "Payment Transaction is successful " + inResponse);
						Toast.makeText(getApplicationContext(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
					}
					@Override
					public void onTransactionFailure(String inErrorMessage,
							Bundle inResponse) {
						// This method gets called if transaction failed. //
						// Here in this case transaction is completed, but with
						// a failure. // Error Message describes the reason for
						// failure. // Response bundle contains the merchant
						// response parameters.
						Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
						Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
					}*/

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.e(TAG, "onTransactionResponse" + inResponse.toString());
                        Toast.makeText(getApplicationContext(), "Payment Transaction success ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.

                        Log.e(TAG, "networkNotAvailable");
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.

                        Log.e(TAG, "clientAuthenticationFailed" + inErrorMessage);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                        Log.e(TAG, "onErrorLoadingWebPage" + inErrorMessage);
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Toast.makeText(MerchantActivity.this, "Back pressed. Transaction cancelled", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onBackPressedCancelTransaction");
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.e(TAG, "onTransactionCancel" + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                });
    }


    public void getCheckSum() {
        SubscriptionInterface subscriptionInterface = ServiceGenerator.INSTANCE.getClient(apiToken).create(SubscriptionInterface.class);

        Map<String, String> type = new HashMap<String, String>();
        type.put("type", "type1");

        Call<ResponseBody> call = subscriptionInterface.getCheckSumHash(type);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String resp = response.body().string();
                    JSONObject rootObj = new JSONObject(resp);

                    checkSumHash = rootObj.getString("CHECKSUMHASH");
                    orderId = rootObj.getString("ORDERID");
                    customerId = rootObj.getString("CUSTOMERID");

                    Log.e(TAG, "user checkSumHash madeyo  " + checkSumHash);
                    Log.e(TAG, "user orderId madeyo  " + orderId);
                    Log.e(TAG, "user customerId madeyo  " + customerId);

                    onStartTransaction();


                } catch (Exception e) {
                    Log.e(TAG, "error" + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure in checksum" + t.getMessage());
            }
        });
    }

    public void getUserPhoneNo() {

        SubscriptionInterface subscriptionInterface = ServiceGenerator.INSTANCE.getClient(apiToken).create(SubscriptionInterface.class);

        Call<ResponseBody> call = subscriptionInterface.getUserDetail();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String resp = response.body().string();
                    JSONObject rootObj = new JSONObject(resp);

                    mobileNo = rootObj.getString("mobile");
                    Log.e(TAG, "user mobileNo madeyo  " + mobileNo);

                } catch (Exception e) {
                    Log.e(TAG, "error" + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure in mobno" + t.getMessage());
            }
        });
    }


}