package com.flowercentral.flowercentralbusiness.order;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by admin on 18-05-2017.
 */

class PendingOrderAdapter  extends RecyclerView.Adapter<PendingOrderAdapter.ViewHolder> {

    private List<OrderItem> orderItemList;

    public PendingOrderAdapter(List<OrderItem> list) {
        this.orderItemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //holder.textViewClaimNumber.setText(claimList.get(position).getCLAIMNO());
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.claim_num)
//        TextView textViewClaimNumber;

        public ViewHolder(View view) {

            super(view);
            ButterKnife.bind(this, view);
        }
    }
}