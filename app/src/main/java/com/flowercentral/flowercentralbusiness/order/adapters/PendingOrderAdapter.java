package com.flowercentral.flowercentralbusiness.order.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.andexert.library.RippleView;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.LayoutNoOrderItemBinding;
import com.flowercentral.flowercentralbusiness.databinding.OrderItemRowBinding;
import com.flowercentral.flowercentralbusiness.map.MapActivity;
import com.flowercentral.flowercentralbusiness.order.OrderDetailsActivity;
import com.flowercentral.flowercentralbusiness.order.PendingOrder;
import com.flowercentral.flowercentralbusiness.order.model.FlowerDetails;
import com.flowercentral.flowercentralbusiness.order.model.Order;
import com.flowercentral.flowercentralbusiness.order.model.OrderItem;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 */
public class PendingOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PendingOrderAdapter.class.getSimpleName();
    private final PendingOrder.RefreshViews mRefreshViews;
    private Order mOrder;
    private Context mContext;
    private final RelativeLayout mRootLayout;

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_NON_EMPTY_LIST = 1;

    public PendingOrderAdapter(Order order, RelativeLayout rootLayout, PendingOrder.RefreshViews refreshViews) {
        this.mOrder = order;
        mRootLayout = rootLayout;
        mRefreshViews = refreshViews;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        mContext = parent.getContext();
        switch (viewType) {
            case VIEW_TYPE_EMPTY_LIST:
                LayoutNoOrderItemBinding emptyBinder =
                        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                                R.layout.layout_no_order_item, parent,
                                false);
                viewHolder = new EmptyListViewHolder(emptyBinder);
                break;

            case VIEW_TYPE_NON_EMPTY_LIST:
                OrderItemRowBinding rowBinder = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.order_item_row, parent, false);
                viewHolder = new ViewHolder(rowBinder);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            OrderItemRowBinding itemRowBinder = ((ViewHolder) holder).itemRowBinder;
            final OrderItem orderItem = mOrder.getOrderItemArrayList().get(position);
            itemRowBinder.setOrder(orderItem);
            //TODO check in xml
            ArrayList<FlowerDetails> flowerDetails = orderItem.getFlowerDetails();
            if (flowerDetails != null && !flowerDetails.isEmpty()) {
                itemRowBinder.orderDetails.setText(flowerDetails.get(0).getFlowerName());
                if (flowerDetails.size() > 1) {
                    itemRowBinder.orderDetails.setText(itemRowBinder.orderDetails.getText() + ", " +
                            String.valueOf(flowerDetails.size() - 1) +
                            " more");
                }
            }
            itemRowBinder.orderQuantity.setText(mContext.getString(R.string.order_format_quantity, String.valueOf(orderItem.getQuantity())));
            itemRowBinder.orderAddress.setText(mContext.getString(R.string.order_format_address, orderItem.getAddress()));

            if (orderItem.getImageUrl() != null && !orderItem.getImageUrl().isEmpty()) {
                Picasso.
                        with(mContext).
                        load(orderItem.getImageUrl()).
                        into(itemRowBinder.orderItemImage);
            }

            itemRowBinder.orderMapDetails.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    try {
                        Intent mapIntent = new Intent(mContext, MapActivity.class);
                        mapIntent.putExtra(mContext.getString(R.string.key_latitude), Double.parseDouble(orderItem.getLatitude()));
                        mapIntent.putExtra(mContext.getString(R.string.key_longitude), Double.parseDouble(orderItem.getLongitude()));
                        mapIntent.putExtra(mContext.getString(R.string.key_address), orderItem.getAddress());
                        mapIntent.putExtra(mContext.getString(R.string.key_is_draggable), false);
                        mContext.startActivity(mapIntent);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });

            itemRowBinder.orderDetailContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailIntent = new Intent(mContext, OrderDetailsActivity.class);
                    orderDetailIntent.putExtra(mContext.getString(R.string.key_order_id), orderItem.getId());
                    mContext.startActivity(orderDetailIntent);
                }
            });

            itemRowBinder.orderStatus.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    processOrderDeliveredRequestByVendor(orderItem.getId());
                }
            });
        } else if (holder instanceof EmptyListViewHolder) {
            EmptyListViewHolder emptyListViewHolder = (EmptyListViewHolder) holder;
            emptyListViewHolder.emptyViewBinder.txtMsgNoItemFound.setText(mContext.getString(R.string.empty_pending_order_items));
        }
    }

    private void processOrderDeliveredRequestByVendor(int orderId) {

        try {
            JSONObject requestObject = new JSONObject();
            requestObject.put("order_id", orderId);
            markAsDelivered(requestObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void markAsDelivered(JSONObject requestObject) {

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(mContext) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                try {

                    if (response.getInt("status") == 1) {
                        Snackbar.make(mRootLayout, "Order delivered status processed.", Snackbar.LENGTH_SHORT).show();
                        mRefreshViews.performRefreshView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ErrorData error) {

                if (error != null) {
                    error.setErrorMessage("Mark failed. Cause -> " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getMarkDeliveredUrl();
        if (requestObject != null) {
            baseModel.executePostJsonRequest(url, requestObject, TAG);
        } else {
            Snackbar.make(mRootLayout, mContext.getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        int size;

        if (mOrder != null && mOrder.getOrderItemArrayList() != null && mOrder.getOrderItemArrayList().size() > 0) {
            size = mOrder.getOrderItemArrayList().size();
        } else {
            //To show empty view
            size = 1;
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (mOrder != null && mOrder.getOrderItemArrayList() != null && mOrder.getOrderItemArrayList().size() > 0) {
            viewType = VIEW_TYPE_NON_EMPTY_LIST;
        } else {
            viewType = VIEW_TYPE_EMPTY_LIST;
        }
        return viewType;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        OrderItemRowBinding itemRowBinder;

        ViewHolder(OrderItemRowBinding binder) {
            super(binder.getRoot());
            itemRowBinder = binder;
            itemRowBinder.orderCategory.setStrokeWidth(1);
            itemRowBinder.orderCategory.setStrokeColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            itemRowBinder.orderCategory.setSolidColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            itemRowBinder.orderCategory.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }

    private class EmptyListViewHolder extends RecyclerView.ViewHolder {

        LayoutNoOrderItemBinding emptyViewBinder;

        EmptyListViewHolder(LayoutNoOrderItemBinding binder) {
            super(binder.getRoot());
            emptyViewBinder = binder;
        }
    }
}