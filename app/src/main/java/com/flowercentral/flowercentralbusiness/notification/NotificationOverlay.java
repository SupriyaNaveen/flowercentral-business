package com.flowercentral.flowercentralbusiness.notification;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.ActivityNotificationOverlayBinding;
import com.flowercentral.flowercentralbusiness.databinding.LayoutAppToolbarBinding;
import com.flowercentral.flowercentralbusiness.order.adapters.ProductDetailsAdapter;
import com.flowercentral.flowercentralbusiness.order.model.OrderDetailedItem;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Logger;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class NotificationOverlay extends AppCompatActivity {

    private static final String TAG = NotificationOverlay.class.getSimpleName();

    private Runnable mTimerRunnable;
    private final Handler mHandler = new Handler();
    private int orderId;
    private ActivityNotificationOverlayBinding mBinder;
    private LayoutAppToolbarBinding mToolbarBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_notification_overlay);
        mToolbarBinder = mBinder.ltToolbar;

        mToolbarBinder.toolbarTitle.setText(getString(R.string.app_name));

        mBinder.textViewTimer.setStrokeWidth(1);
        mBinder.textViewTimer.setStrokeColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mBinder.textViewTimer.setSolidColor(ContextCompat.getColor(this, R.color.colorWhite));
        mBinder.textViewTimer.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mBinder.textViewTimer.setText(String.valueOf(10) + "\nSec");

        // For recycler view use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mBinder.notificationRecyclerView.setLayoutManager(mLayoutManager);

        orderId = getIntent().getIntExtra(getString(R.string.key_order_id), 0);
        getOrderDetails(orderId);
    }

    private void getOrderDetails(final int orderId) {

        mBinder.progressBar.setVisibility(View.VISIBLE);
        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(this) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                mBinder.progressBar.setVisibility(View.GONE);
                OrderDetailedItem orderDetailedItem = new Gson().fromJson(String.valueOf(response),
                        new TypeToken<OrderDetailedItem>() {
                        }.getType());
                updateView(orderDetailedItem);
                startTimerToAcceptOrReject();
            }

            @Override
            public void onError(ErrorData error) {
                mBinder.progressBar.setVisibility(View.GONE);
                if (error != null) {
                    error.setErrorMessage("Order details fetch failed. Cause -> " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mBinder.rootLayout, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getOrderDetailsUrl();
        try {
            JSONObject requestObject = new JSONObject();
            requestObject.put(getString(R.string.api_key_order_id), orderId);
            String srcFormat = "yyyy-MM-dd HH:mm";
            SimpleDateFormat formatSrc = new SimpleDateFormat(srcFormat, Locale.getDefault());
            requestObject.put(getString(R.string.api_key_timestamp), formatSrc.format(Calendar.getInstance().getTime()));
            baseModel.executePostJsonRequest(url, requestObject, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startTimerToAcceptOrReject() {
        final int delay = 1000; //milliseconds

        mTimerRunnable = new Runnable() {
            int currentSecond = 10;

            public void run() {
                currentSecond--;
                if (currentSecond < 0) {
                    rejectOrder();
                } else {
                    mBinder.textViewTimer.setText(currentSecond + "\nSec");
                    mHandler.postDelayed(this, delay);
                }
            }
        };
        mHandler.postDelayed(mTimerRunnable, delay);
    }

    private void updateView(OrderDetailedItem orderDetailedItem) {

        mBinder.orderTotal.setText(orderDetailedItem.getOrderTotal());
        mBinder.orderDeliveryAddress.setText(orderDetailedItem.getAddress());

        ProductDetailsAdapter adapter = new ProductDetailsAdapter(orderDetailedItem.getProductItemList());
        mBinder.notificationRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        rejectOrder();
    }

    public void acceptSelected(View view) {
        cancelTimer();
        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(this) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                try {
                    if (response.getInt(getString(R.string.api_res_status)) == 1) {
                        //Refresh pending order list if the pending order is showing to the user.
                        Intent intent = new Intent(AppConstant.BROADCAST_ACTION_ORDER_ACCEPTED);
                        LocalBroadcastManager.getInstance(NotificationOverlay.this).sendBroadcastSync(intent);
                        finish();
                    } else {
                        rejectOrder();
                    }
                } catch (JSONException e) {
                    rejectOrder();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ErrorData error) {
                rejectOrder();
                Logger.log(NotificationOverlay.class.getSimpleName(), "acceptSelected", "ErrorType : " + error.getErrorType()
                        + ", Message : " + error.getErrorMessage(), AppConstant.LOG_LEVEL_DEBUG);
            }
        };

        String url = QueryBuilder.getAcceptOrderUrl();
        try {
            JSONObject requestObject = new JSONObject();
            requestObject.put(getString(R.string.api_key_order_id), orderId);
            baseModel.executePostJsonRequest(url, requestObject, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void rejectSelected(View view) {

        rejectOrder();
    }

    private void rejectOrder() {
        cancelTimer();
        final MaterialDialog materialDialog = Util.showProgressDialog(this, "Rejecting order", "Order is rejected. Please wait!", false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (materialDialog != null && materialDialog.isShowing() && !isFinishing()) {
                        materialDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        }, 1000);
    }

    void cancelTimer() {
        if (null != mTimerRunnable) {
            mHandler.removeCallbacks(mTimerRunnable);
            mTimerRunnable = null;
        }
    }

}
