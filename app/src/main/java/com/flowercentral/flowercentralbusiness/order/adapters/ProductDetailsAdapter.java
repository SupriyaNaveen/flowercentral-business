package com.flowercentral.flowercentralbusiness.order.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.order.model.ProductItem;
import com.flowercentral.flowercentralbusiness.util.CircularTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder> {

    private List<ProductItem> mProductList;
    private Context mContext;

    public ProductDetailsAdapter(List<ProductItem> list) {
        this.mProductList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_detail_row, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textViewProductDetails.setText(mProductList.get(position).getName());
        holder.textViewProductPriceDetails.setText(String.valueOf(mProductList.get(position).getPrice()));
        holder.textViewProductQuantity.setText(String.valueOf(mProductList.get(position).getQuantity()));

//        holder.textViewProductMessage.setText(mContext.getString(R.string.product_lbl_address, mProductList.get(position).getMessage()));

        Picasso.
                with(mContext).
                load(mProductList.get(position).getImageUrl()).
                into(holder.productItemImage);

        holder.circularTextViewCategory.setText(String.valueOf(mProductList.get(position).getCategory()));
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.product_category)
        CircularTextView circularTextViewCategory;

        @BindView(R.id.product_details)
        TextView textViewProductDetails;

        @BindView(R.id.product_quantity)
        TextView textViewProductQuantity;

        @BindView(R.id.product_price_details)
        TextView textViewProductPriceDetails;

        @BindView(R.id.product_message)
        TextView textViewProductMessage;

        @BindView(R.id.product_item_image)
        ImageView productItemImage;

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