package com.flowercentral.flowercentralbusiness.order;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.andexert.library.RippleView;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.ActivityOrderDetailsBinding;
import com.flowercentral.flowercentralbusiness.map.MapActivity;
import com.flowercentral.flowercentralbusiness.order.adapters.ProductDetailsAdapter;
import com.flowercentral.flowercentralbusiness.order.model.OrderDetailedItem;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
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
public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = OrderDetailsActivity.class.getSimpleName();
    private ActivityOrderDetailsBinding mBinder;
    private OrderDetailedItem mOrderItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_order_details);

        setSupportActionBar(mBinder.ltToolbar.toolbar);

        if (mBinder.ltToolbar.toolbar != null) {
            setSupportActionBar(mBinder.ltToolbar.toolbar);
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
        mBinder.orderDetailRecyclerView.setLayoutManager(mLayoutManager);

        getOrderDetails(getIntent().getIntExtra(getString(R.string.key_order_id), 0));

        mBinder.ltOrderDetails.viewOnMap.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mapViewSelected(rippleView);
            }
        });
    }

    private void getOrderDetails(final int orderId) {

        mBinder.progressBar.setVisibility(View.VISIBLE);
        mBinder.orderDetailsWrapper.setVisibility(View.GONE);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(this) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {

                mBinder.progressBar.setVisibility(View.GONE);
                mBinder.orderDetailsWrapper.setVisibility(View.VISIBLE);

                OrderDetailedItem orderDetailedItem = new Gson().fromJson(String.valueOf(response),
                        new TypeToken<OrderDetailedItem>() {
                        }.getType());
                updateView(orderDetailedItem);
            }

            @Override
            public void onError(ErrorData error) {

                mBinder.progressBar.setVisibility(View.GONE);
                mBinder.orderDetailsWrapper.setVisibility(View.VISIBLE);

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

    private void updateView(OrderDetailedItem orderDetailedItem) {

        mOrderItem = orderDetailedItem;
        mBinder.ltOrderDetails.setOrderdetails(orderDetailedItem);
        //TODO move to xml
        mBinder.ltOrderDetails.orderIsScheduledDelivery.setText(orderDetailedItem.isScheduledDelivery() ? "Yes" : "No");
        if (orderDetailedItem.getDeliveryStatus() == OrderDetailedItem.DELIVERY_STATUS.DELIVERED) {
            mBinder.ltOrderDetails.deliveredAtWrapper.setVisibility(View.VISIBLE);
        }

        ProductDetailsAdapter adapter = new ProductDetailsAdapter(orderDetailedItem.getProductItemList());
        mBinder.orderDetailRecyclerView.setAdapter(adapter);
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

    public void mapViewSelected(RippleView view) {

        if (null != mOrderItem) {
            try {
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.putExtra(getString(R.string.key_latitude), Double.parseDouble(mOrderItem.getLatitude()));
                mapIntent.putExtra(getString(R.string.key_longitude), Double.parseDouble(mOrderItem.getLongitude()));
                mapIntent.putExtra(getString(R.string.key_address), mOrderItem.getAddress());
                mapIntent.putExtra(getString(R.string.key_is_draggable), false);
                startActivity(mapIntent);
            } catch (NumberFormatException e) {
                Snackbar.make(mBinder.rootLayout, getString(R.string.map_error_unable_locate_address), Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(mBinder.rootLayout, getString(R.string.map_error_unable_locate_address), Snackbar.LENGTH_SHORT).show();
        }
    }
}
