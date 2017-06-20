package com.flowercentral.flowercentralbusiness.notification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.order.adapters.ProductDetailsAdapter;
import com.flowercentral.flowercentralbusiness.order.model.OrderDetailedItem;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.CircularTextView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class NotificationOverlay extends AppCompatActivity {

    private static final String TAG = NotificationOverlay.class.getSimpleName();

    @BindView(R.id.root_layout)
    RelativeLayout mLinearLayoutRoot;

    @BindView(R.id.text_view_timer)
    CircularTextView textViewTimer;

    @BindView(R.id.notification_recycler_view)
    RecyclerView mRecyclerViewProductDetails;

    @BindView(R.id.order_total)
    TextView mTextViewOrderTotal;

    @BindView(R.id.order_delivery_address)
    TextView mTextViewOrderDeliveryAddress;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    private Runnable mTimerRunnable;
    private final Handler mHandler = new Handler();
    private int orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_overlay);
        ButterKnife.bind(this);

        mToolbarTitle.setText(getString(R.string.app_name));

        textViewTimer.setStrokeWidth(1);
        textViewTimer.setStrokeColor(ContextCompat.getColor(this, R.color.colorPrimary));
        textViewTimer.setSolidColor(ContextCompat.getColor(this, R.color.colorWhite));
        textViewTimer.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        textViewTimer.setText(String.valueOf(10) + "\nSec");

        // For recycler view use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewProductDetails.setLayoutManager(mLayoutManager);

        orderId = getIntent().getIntExtra(getString(R.string.key_order_id), 0);
        getOrderDetails(orderId);
    }

    private void getOrderDetails(final int orderId) {

        progressBar.setVisibility(View.VISIBLE);
        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(this) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                progressBar.setVisibility(View.GONE);
                OrderDetailedItem orderDetailedItem = new Gson().fromJson(String.valueOf(response),
                        new TypeToken<OrderDetailedItem>() {
                        }.getType());
                updateView(orderDetailedItem);
                startTimerToAcceptOrReject();
            }

            @Override
            public void onError(ErrorData error) {
                progressBar.setVisibility(View.GONE);
                if (error != null) {
                    error.setErrorMessage("Order details fetch failed. Cause -> " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mLinearLayoutRoot, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mLinearLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mLinearLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mLinearLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mLinearLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mLinearLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mLinearLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mLinearLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
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
                    rejectSelected();
                } else {
                    textViewTimer.setText(currentSecond + "\nSec");
                    mHandler.postDelayed(this, delay);
                }
            }
        };
        mHandler.postDelayed(mTimerRunnable, delay);
    }

    private void updateView(OrderDetailedItem orderDetailedItem) {

        mTextViewOrderTotal.setText(orderDetailedItem.getOrderTotal());
        mTextViewOrderDeliveryAddress.setText(orderDetailedItem.getAddress());

        ProductDetailsAdapter adapter = new ProductDetailsAdapter(orderDetailedItem.getProductItemList());
        mRecyclerViewProductDetails.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        rejectSelected();
    }

    @OnClick(R.id.btn_accept)
    void acceptSelected() {
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
                        rejectSelected();
                    }
                } catch (JSONException e) {
                    rejectSelected();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ErrorData error) {
                rejectSelected();
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

    @OnClick(R.id.btn_reject)
    void rejectSelected() {

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
