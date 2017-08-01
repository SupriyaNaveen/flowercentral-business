package com.flowercentral.flowercentralbusiness.order.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.ProductDetailRowBinding;
import com.flowercentral.flowercentralbusiness.order.model.ProductItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 */
public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder> {

    private static final String BLANK_SPACE = " ";
    private List<ProductItem> mProductList;
    private Context mContext;

    public ProductDetailsAdapter(List<ProductItem> list) {
        this.mProductList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProductDetailRowBinding mBinder = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.product_detail_row, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(mBinder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        ProductItem productItem = mProductList.get(position);
        holder.rowBinder.productDetails.setText(
                productItem.getQuantity() +
                        BLANK_SPACE +
                        productItem.getOrderTitle() +
                        BLANK_SPACE +
                        productItem.getCategory()
        );
        holder.rowBinder.productPriceDetails.setText(String.format("$ %s", mProductList.get(position).getPrice()));

        holder.rowBinder.productMessage.setText(String.format("Message : %s", mProductList.get(position).getMessage()));

        String imgUrl = mProductList.get(position).getImageUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Picasso.
                    with(mContext).
                    load(imgUrl).
                    into(holder.rowBinder.productItemImage);
        }
    }

    @Override
    public int getItemCount() {
        if (null != mProductList)
            return mProductList.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ProductDetailRowBinding rowBinder;

        ViewHolder(ProductDetailRowBinding binder) {
            super(binder.getRoot());
            rowBinder = binder;
        }
    }
}