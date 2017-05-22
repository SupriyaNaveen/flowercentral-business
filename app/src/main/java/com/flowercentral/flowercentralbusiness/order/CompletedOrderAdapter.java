package com.flowercentral.flowercentralbusiness.order;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 17-05-2017.
 */

public class CompletedOrderAdapter extends RecyclerView.Adapter<CompletedOrderAdapter.ViewHolder> {

    private List<OrderItem> orderItemList;

    public CompletedOrderAdapter(List<OrderItem> list) {
        this.orderItemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CompletedOrderAdapter.ViewHolder holder, final int position) {
        holder.textViewOrderDetails.setText(orderItemList.get(position).getQuantity()
                + " "
                + orderItemList.get(position).getName()
                + ", "
                + orderItemList.get(position).getCategory());

        holder.textViewOrderPriceDetails.setText("INR "
                + orderItemList.get(position).getPrice()
                + " "
                + orderItemList.get(position).getPaidStatus());

//        SimpleDateFormat formatter = new SimpleDateFormat("dd EEE yyyy, hh:mm a");
//        Calendar calendarInstance = Calendar.getInstance();
//        calendarInstance.setTimeInMillis(orderItemList.get(position).getScheduleDateTime());

        holder.textViewOrderSchedule.setText(orderItemList.get(position).getScheduleDateTime());

        holder.textViewOrderAddress.setText(orderItemList.get(position).getAddress());

        holder.buttonDeliveryStatus.setText(" " + orderItemList.get(position).getDeliveryStatus());
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
        Button buttonDeliveryStatus;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}