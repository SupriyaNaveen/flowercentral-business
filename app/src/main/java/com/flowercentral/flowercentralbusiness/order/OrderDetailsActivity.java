package com.flowercentral.flowercentralbusiness.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.order.adapters.ProductDetailsAdapter;
import com.flowercentral.flowercentralbusiness.order.model.OrderDetailedItem;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.MapActivity;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 26-05-2017.
 */

public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = OrderDetailsActivity.class.getSimpleName();
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
    TextView mTextViewViewOnMap;

    private ActionBar mActionBar;

    private OrderDetailedItem mOrderItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mActionBar = getSupportActionBar();
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

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(this) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                OrderDetailedItem orderDetailedItem = new Gson().<OrderDetailedItem>fromJson(String.valueOf(response),
                        new TypeToken<OrderDetailedItem>() {
                        }.getType());
                updateView(orderDetailedItem);
            }

            @Override
            public void onError(ErrorData error) {
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
            requestObject.put("order_id", orderId);
            SimpleDateFormat formatSrc = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            requestObject.put("timestamp", formatSrc.format(Calendar.getInstance().getTime()));
            if (requestObject != null) {
                baseModel.executePostJsonRequest(url, requestObject, TAG);
            } else {
                Snackbar.make(mLinearLayoutRoot, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }
    }

    private void updateView(OrderDetailedItem orderDetailedItem) {

        mOrderItem = orderDetailedItem;

        mTextViewOrderPlaced.setText(orderDetailedItem.getOrderDate());
        mTextViewIsScheduledDelivery.setText(orderDetailedItem.getOrderTotal());
        mTextViewOrderTotal.setText(orderDetailedItem.getOrderTotal());
        mTextViewOrderStatus.setText(String.valueOf(orderDetailedItem.getDeliveryStatus()));

        SimpleDateFormat formatSrc = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatDest = new SimpleDateFormat("dd EEE yyyy, hh:mm a");
        Date date = null;
        try {
            date = formatSrc.parse(orderDetailedItem.getScheduleDateTime());
            mTextViewOrderSchedule.setText(formatDest.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
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
                Snackbar.make(mLinearLayoutRoot, "Unable to locate address.", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(mLinearLayoutRoot, "Unable to locate address.", Snackbar.LENGTH_SHORT).show();
        }
    }
}
