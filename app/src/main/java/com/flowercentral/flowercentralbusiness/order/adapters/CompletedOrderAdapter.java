package com.flowercentral.flowercentralbusiness.order.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.order.OrderDetailsActivity;
import com.flowercentral.flowercentralbusiness.order.model.OrderItem;
import com.flowercentral.flowercentralbusiness.util.CircularTextView;
import com.flowercentral.flowercentralbusiness.util.MapActivity;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class CompletedOrderAdapter extends RecyclerView.Adapter<CompletedOrderAdapter.ViewHolder> {

    private List<OrderItem> mOrderItemList;
    private Context mContext;

    public CompletedOrderAdapter(List<OrderItem> list) {
        this.mOrderItemList = list;
    }

    @Override
    public CompletedOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_row, parent, false);
        mContext = parent.getContext();
        return new CompletedOrderAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CompletedOrderAdapter.ViewHolder holder, final int position) {

        final OrderItem orderItem = mOrderItemList.get(position);
        String srcFormat = "yyyy-MM-dd HH:mm";
        String destFormat = "dd EEE yyyy, hh:mm a";
        holder.textViewOrderDetails.setText(orderItem.getName());

        holder.textViewOrderPriceDetails.setText(mContext.getString(R.string.order_format_price, String.valueOf(orderItem.getPrice())));
        holder.textViewPaidStatus.setText(orderItem.getPaidStatus().value());
        holder.textViewOrderQuantity.setText(mContext.getString(R.string.order_format_quantity, String.valueOf(orderItem.getQuantity())));

        String dateStr = Util.formatDate(orderItem.getScheduleDateTime(), srcFormat, destFormat);
        holder.textViewOrderSchedule.setText(mContext.getString(R.string.order_format_schedule, dateStr));

        holder.textViewOrderDeliveredAt.setVisibility(View.VISIBLE);
        dateStr = Util.formatDate(orderItem.getDeliveredSchedule(), srcFormat, destFormat);
        holder.textViewOrderDeliveredAt.setText(mContext.getString(R.string.order_format_delivered_at, dateStr));

        holder.textViewOrderAddress.setText(mContext.getString(R.string.order_format_address, orderItem.getAddress()));

        holder.buttonDeliveryStatus.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen));

        Picasso.
                with(mContext).
                load(orderItem.getImageUrl()).
                into(holder.orderItemImage);

        holder.circularTextViewCategory.setText(String.valueOf(orderItem.getCategory()));

        holder.relativeLayoutMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        holder.linearLayoutOrderDetailContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderDetailIntent = new Intent(mContext, OrderDetailsActivity.class);
                orderDetailIntent.putExtra(mContext.getString(R.string.key_order_id), orderItem.getId());
                mContext.startActivity(orderDetailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrderItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_details)
        TextView textViewOrderDetails;

        @BindView(R.id.order_price_details)
        TextView textViewOrderPriceDetails;

        @BindView(R.id.order_schedule)
        TextView textViewOrderSchedule;

        @BindView(R.id.order_address)
        TextView textViewOrderAddress;

        @BindView(R.id.order_status)
        RelativeLayout buttonDeliveryStatus;

        @BindView(R.id.tv_status)
        TextView textViewDeliveryStatus;

        @BindView(R.id.order_item_image)
        ImageView orderItemImage;

        @BindView(R.id.order_category)
        CircularTextView circularTextViewCategory;

        @BindView(R.id.order_paid_status)
        TextView textViewPaidStatus;

        @BindView(R.id.order_quantity)
        TextView textViewOrderQuantity;

        @BindView(R.id.order_map_details)
        RelativeLayout relativeLayoutMaps;

        @BindView(R.id.order_detail_container)
        LinearLayout linearLayoutOrderDetailContainer;

        @BindView(R.id.order_delivered_at)
        TextView textViewOrderDeliveredAt;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            circularTextViewCategory.setStrokeWidth(1);
            circularTextViewCategory.setStrokeColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            circularTextViewCategory.setSolidColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            circularTextViewCategory.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }
}