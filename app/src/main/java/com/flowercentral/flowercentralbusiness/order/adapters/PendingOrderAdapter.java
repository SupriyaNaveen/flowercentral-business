package com.flowercentral.flowercentralbusiness.order.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
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
import com.flowercentral.flowercentralbusiness.order.PendingOrder;
import com.flowercentral.flowercentralbusiness.order.model.OrderItem;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.CircularTextView;
import com.flowercentral.flowercentralbusiness.util.MapActivity;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 18-05-2017.
 */

public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.ViewHolder> {

    private static final String TAG = PendingOrderAdapter.class.getSimpleName();
    private final PendingOrder.RefreshViews mRefreshViews;
    private List<OrderItem> mOrderItemList;
    private Context mContext;
    private final RelativeLayout mRootLayout;

    public PendingOrderAdapter(List<OrderItem> list, RelativeLayout rootLayout, PendingOrder.RefreshViews refreshViews) {
        this.mOrderItemList = list;
        mRootLayout = rootLayout;
        mRefreshViews = refreshViews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_row, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textViewOrderDetails.setText(mOrderItemList.get(position).getName());

        holder.textViewOrderPriceDetails.setText(mContext.getString(R.string.order_lbl_price, String.valueOf(mOrderItemList.get(position).getPrice())));
        holder.textViewPaidStatus.setText(mOrderItemList.get(position).getPaidStatus().value());
        holder.textViewOrderQuantity.setText(mContext.getString(R.string.order_lbl_quantity, String.valueOf(mOrderItemList.get(position).getQuantity())));

        SimpleDateFormat formatSrc = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatDest = new SimpleDateFormat("dd EEE yyyy, hh:mm a");
        Date date = null;
        try {
            date = formatSrc.parse(mOrderItemList.get(position).getScheduleDateTime());
            holder.textViewOrderSchedule.setText(mContext.getString(R.string.order_lbl_schedule, formatDest.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.textViewOrderAddress.setText(mContext.getString(R.string.order_lbl_address, mOrderItemList.get(position).getAddress()));

//        holder.textViewDeliveryStatus.setText(String.valueOf(mOrderItemList.get(position).getDeliveryStatus()));

        Picasso.
                with(mContext).
                load(mOrderItemList.get(position).getImageUrl()).
                into(holder.orderItemImage);

        holder.circularTextViewCategory.setText(String.valueOf(mOrderItemList.get(position).getCategory()));

        holder.relativeLayoutMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent mapIntent = new Intent(mContext, MapActivity.class);
                    mapIntent.putExtra(mContext.getString(R.string.key_latitude), Double.parseDouble(mOrderItemList.get(position).getLatitude()));
                    mapIntent.putExtra(mContext.getString(R.string.key_longitude), Double.parseDouble(mOrderItemList.get(position).getLongitude()));
                    mapIntent.putExtra(mContext.getString(R.string.key_address), mOrderItemList.get(position).getAddress());
                    mapIntent.putExtra(mContext.getString(R.string.key_is_draggable), false);
                    mContext.startActivity(mapIntent);
                } catch (NumberFormatException e) {

                }
            }
        });

        holder.linearLayoutOrderDetailContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderDetailIntent = new Intent(mContext, OrderDetailsActivity.class);
                orderDetailIntent.putExtra(mContext.getString(R.string.key_order_id), mOrderItemList.get(position).getId());
                mContext.startActivity(orderDetailIntent);
            }
        });

        holder.buttonDeliveryStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processOrderDeliveredRequestByVendor(mOrderItemList.get(position).getId());
            }
        });
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