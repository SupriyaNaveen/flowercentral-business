package com.flowercentral.flowercentralbusiness.order;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.util.CircularTextView;
import com.flowercentral.flowercentralbusiness.util.MapActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 18-05-2017.
 */

class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.ViewHolder> {

    private List<OrderItem> orderItemList;
    private Context context;

    public PendingOrderAdapter(List<OrderItem> list) {
        this.orderItemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_row, parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textViewOrderDetails.setText(orderItemList.get(position).getName());

        holder.textViewOrderPriceDetails.setText(context.getString(R.string.order_lbl_price, String.valueOf(orderItemList.get(position).getPrice())));
        holder.textViewPaidStatus.setText(orderItemList.get(position).getPaidStatus().value());
        holder.textViewOrderQuantity.setText(context.getString(R.string.order_lbl_quantity, String.valueOf(orderItemList.get(position).getQuantity())));

        SimpleDateFormat formatSrc = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatDest = new SimpleDateFormat("dd EEE yyyy, hh:mm a");
        Date date = null;
        try {
            date = formatSrc.parse(orderItemList.get(position).getScheduleDateTime());
            holder.textViewOrderSchedule.setText(context.getString(R.string.order_lbl_schedule, formatDest.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.textViewOrderAddress.setText(context.getString(R.string.order_lbl_address, orderItemList.get(position).getAddress()));

        holder.textViewDeliveryStatus.setText(String.valueOf(orderItemList.get(position).getDeliveryStatus()));

        Picasso.
                with(context).
                load(orderItemList.get(position).getImageUrl()).
                into(holder.orderItemImage);

        holder.circularTextViewCategory.setText(String.valueOf(orderItemList.get(position).getCategory()));

        holder.relativeLayoutMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent mapIntent = new Intent(context, MapActivity.class);
                    mapIntent.putExtra(context.getString(R.string.key_latitude), Double.parseDouble(orderItemList.get(position).getLatitude()));
                    mapIntent.putExtra(context.getString(R.string.key_longitude), Double.parseDouble(orderItemList.get(position).getLongitude()));
                    mapIntent.putExtra(context.getString(R.string.key_address), orderItemList.get(position).getAddress());
                    mapIntent.putExtra(context.getString(R.string.key_is_draggable), false);
                    context.startActivity(mapIntent);
                }catch (NumberFormatException e) {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            circularTextViewCategory.setStrokeWidth(1);
            circularTextViewCategory.setStrokeColor(ContextCompat.getColor(context, R.color.colorPrimary));
            circularTextViewCategory.setSolidColor(ContextCompat.getColor(context, R.color.colorWhite));
            circularTextViewCategory.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }
}