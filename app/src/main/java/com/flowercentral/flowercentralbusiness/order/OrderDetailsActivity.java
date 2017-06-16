package com.flowercentral.flowercentralbusiness.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.order.adapters.ProductDetailsAdapter;
import com.flowercentral.flowercentralbusiness.order.model.OrderDetailedItem;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.MapActivity;
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
public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = OrderDetailsActivity.class.getSimpleName();
    private final String srcFormat = "yyyy-MM-dd HH:mm";

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.order_placed_schedule)
    TextView mTextViewOrderPlaced;

    @BindView(R.id.order_total)
    TextView mTextViewOrderTotal;

    @BindView(R.id.order_delivery_address)
    TextView mTextViewOrderDeliveryAddress;

    @BindView(R.id.order_is_scheduled_delivery)
    TextView mTextViewIsScheduledDelivery;

    @BindView(R.id.order_schedule)
    TextView mTextViewOrderSchedule;

    @BindView(R.id.order_status)
    TextView mTextViewOrderStatus;

    @BindView(R.id.order_detail_recycler_view)
    RecyclerView mRecyclerViewProductDetails;

    @BindView(R.id.root_layout)
    LinearLayout mLinearLayoutRoot;

    @BindView(R.id.view_on_map)
    ImageView mImageViewViewOnMap;

    @BindView(R.id.order_delivered_at)
    TextView mTextViewOrderDeliveredAt;

    @BindView(R.id.delivered_at_wrapper)
    LinearLayout mDeliveredAtWrapper;

    private OrderDetailedItem mOrderItem;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.order_details_wrapper)
    NestedScrollView dataWrapperLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            ActionBar mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.setHomeButtonEnabled(true);
                mActionBar.setTitle(getString(R.string.app_name));
                mActionBar.setDisplayHomeAsUpEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
            }
        }

        // For recycler view use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewProductDetails.setLayoutManager(mLayoutManager);

        getOrderDetails(getIntent().getIntExtra(getString(R.string.key_order_id), 0));
    }

    private void getOrderDetails(final int orderId) {

        mProgressBar.setVisibility(View.VISIBLE);
        dataWrapperLayout.setVisibility(View.GONE);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(this) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {

                mProgressBar.setVisibility(View.GONE);
                dataWrapperLayout.setVisibility(View.VISIBLE);

                OrderDetailedItem orderDetailedItem = new Gson().fromJson(String.valueOf(response),
                        new TypeToken<OrderDetailedItem>() {
                        }.getType());
                updateView(orderDetailedItem);
            }

            @Override
            public void onError(ErrorData error) {

                mProgressBar.setVisibility(View.GONE);
                dataWrapperLayout.setVisibility(View.VISIBLE);

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
            SimpleDateFormat formatSrc = new SimpleDateFormat(srcFormat, Locale.getDefault());
            requestObject.put(getString(R.string.api_key_timestamp), formatSrc.format(Calendar.getInstance().getTime()));
            baseModel.executePostJsonRequest(url, requestObject, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateView(OrderDetailedItem orderDetailedItem) {

        String destFormat = "dd EEE yyyy, hh:mm a";
        mOrderItem = orderDetailedItem;

        mTextViewOrderPlaced.setText(orderDetailedItem.getOrderDate());
        mTextViewIsScheduledDelivery.setText(orderDetailedItem.isScheduledDelivery() ? "Yes" : "No");
        mTextViewOrderTotal.setText(orderDetailedItem.getOrderTotal());
        mTextViewOrderStatus.setText(String.valueOf(orderDetailedItem.getDeliveryStatus()));

        mTextViewOrderSchedule.setText(Util.formatDate(orderDetailedItem.getScheduleDateTime(), srcFormat, destFormat));

        if (orderDetailedItem.getDeliveryStatus() == OrderDetailedItem.DELIVERY_STATUS.DELIVERED) {
            mDeliveredAtWrapper.setVisibility(View.VISIBLE);
            mTextViewOrderDeliveredAt.setText(Util.formatDate(orderDetailedItem.getDeliveredDateTime(), srcFormat, destFormat));
        }

        mTextViewOrderDeliveryAddress.setText(orderDetailedItem.getAddress());

        ProductDetailsAdapter adapter = new ProductDetailsAdapter(orderDetailedItem.getProductItemList());
        mRecyclerViewProductDetails.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.view_on_map)
    void mapViewSelected() {

        if (null != mOrderItem) {
            try {
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.putExtra(getString(R.string.key_latitude), Double.parseDouble(mOrderItem.getLatitude()));
                mapIntent.putExtra(getString(R.string.key_longitude), Double.parseDouble(mOrderItem.getLongitude()));
                mapIntent.putExtra(getString(R.string.key_address), mOrderItem.getAddress());
                mapIntent.putExtra(getString(R.string.key_is_draggable), false);
                startActivity(mapIntent);
            } catch (NumberFormatException e) {
                Snackbar.make(mLinearLayoutRoot, getString(R.string.map_error_unable_locate_address), Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(mLinearLayoutRoot, getString(R.string.map_error_unable_locate_address), Snackbar.LENGTH_SHORT).show();
        }
    }
}
