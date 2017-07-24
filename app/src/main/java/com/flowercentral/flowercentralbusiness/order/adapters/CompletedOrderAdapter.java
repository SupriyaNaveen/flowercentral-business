package com.flowercentral.flowercentralbusiness.order.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andexert.library.RippleView;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.LayoutNoOrderItemBinding;
import com.flowercentral.flowercentralbusiness.databinding.OrderItemRowBinding;
import com.flowercentral.flowercentralbusiness.map.MapActivity;
import com.flowercentral.flowercentralbusiness.order.OrderDetailsActivity;
import com.flowercentral.flowercentralbusiness.order.model.FlowerDetails;
import com.flowercentral.flowercentralbusiness.order.model.Order;
import com.flowercentral.flowercentralbusiness.order.model.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 *
 */
public class CompletedOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_NON_EMPTY_LIST = 1;
    private Order mOrder;
    private Context mContext;

    public CompletedOrderAdapter(Order order) {
        this.mOrder = order;
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
            itemRowBinder.orderDeliveredAt.setVisibility(View.VISIBLE);
            itemRowBinder.orderStatus.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen));

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

            itemRowBinder.orderStatus.setEnabled(false);
        } else if (holder instanceof EmptyListViewHolder) {
            EmptyListViewHolder emptyListViewHolder = (EmptyListViewHolder) holder;
            emptyListViewHolder.emptyViewBinder.txtMsgNoItemFound.setText(mContext.getString(R.string.empty_completed_order_items));
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