package com.flowercentral.flowercentralbusiness.feedback;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.andexert.library.RippleView;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FeedbackItemRowBinding;
import com.flowercentral.flowercentralbusiness.databinding.LayoutNoOrderItemBinding;
import com.flowercentral.flowercentralbusiness.order.OrderDetailsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Feedback adapter provides view to each item of feedback list.
 */
class ViewFeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_NON_EMPTY_LIST = 1;
    private List<FeedbackItem> mFeedbackItemList;
    private Context mContext;

    /**
     * Init the adapter by setting the feedback list data.
     *
     * @param list list
     */
    ViewFeedbackAdapter(List<FeedbackItem> list) {
        this.mFeedbackItemList = list;
    }

    /**
     * Get the view type.
     * If type is empty then show the empty view.
     * If type is non empty, then
     * Initialise the feedback view for each item in the list.
     *
     * @param parent   parent
     * @param viewType viewType
     * @return view
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        mContext = parent.getContext();
        switch (viewType) {
            case VIEW_TYPE_EMPTY_LIST:
                LayoutNoOrderItemBinding noOrderItemBinding = DataBindingUtil
                        .inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_no_order_item, parent, false);
                viewHolder = new EmptyListViewHolder(noOrderItemBinding);
                break;

            case VIEW_TYPE_NON_EMPTY_LIST:
                FeedbackItemRowBinding rowBinding = DataBindingUtil
                        .inflate(LayoutInflater.from(parent.getContext()), R.layout.feedback_item_row, parent, false);
                viewHolder = new ViewHolder(rowBinding);
                break;
        }
        return viewHolder;
    }

    /**
     * For each row update the data.
     *
     * @param holder   holder
     * @param position position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            FeedbackItemRowBinding feedbackItemRowBinder = ((ViewHolder) holder).feedbackItemRowBinder;
            final FeedbackItem feedbackItem = mFeedbackItemList.get(position);
            feedbackItemRowBinder.setFeedback(feedbackItem);

            feedbackItemRowBinder.feedbackOrderDetails.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    Intent orderDetailIntent = new Intent(mContext, OrderDetailsActivity.class);
                    orderDetailIntent.putExtra(mContext.getString(R.string.key_order_id), feedbackItem.getFeedbackOrderId());
                    mContext.startActivity(orderDetailIntent);
                }
            });
        } else if (holder instanceof EmptyListViewHolder) {
            LayoutNoOrderItemBinding emptyViewBinder = ((EmptyListViewHolder) holder).emptyViewBinder;
            emptyViewBinder.txtMsgNoItemFound.setText(mContext.getString(R.string.empty_feedback_list));
            emptyViewBinder.txtMsgNoItemFound.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        }
    }

    /**
     * Number of items in the list.
     *
     * @return size of list
     */
    @Override
    public int getItemCount() {
        int size;
        if (mFeedbackItemList != null && mFeedbackItemList.size() > 0) {
            size = mFeedbackItemList.size();
        } else {
            //To show empty view
            size = 1;
        }
        return size;
    }

    /**
     * Get the view type based on the list size.
     *
     * @param position position
     * @return view type
     */
    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (mFeedbackItemList != null && mFeedbackItemList.size() > 0) {
            viewType = VIEW_TYPE_NON_EMPTY_LIST;
        } else {
            viewType = VIEW_TYPE_EMPTY_LIST;
        }
        return viewType;
    }


    /**
     * View holder class.
     */
    private class ViewHolder extends RecyclerView.ViewHolder {

        FeedbackItemRowBinding feedbackItemRowBinder;

        ViewHolder(FeedbackItemRowBinding binder) {
            super(binder.getRoot());

            feedbackItemRowBinder = binder;
            binder.feedbackProfilePic.setStrokeWidth(1);
            binder.feedbackProfilePic.setStrokeColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            binder.feedbackProfilePic.setSolidColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            binder.feedbackProfilePic.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }

    /**
     * Empty view holder class.
     */
    private class EmptyListViewHolder extends RecyclerView.ViewHolder {

        LayoutNoOrderItemBinding emptyViewBinder;

        EmptyListViewHolder(LayoutNoOrderItemBinding binder) {
            super(binder.getRoot());
            emptyViewBinder = binder;
        }
    }

    /**
     * Replace the feedback item list with new set of data.
     * Refresh the view accordingly.
     *
     * @param _list list
     */
    void replaceAll(List<FeedbackItem> _list) {
        if (null == mFeedbackItemList) {
            mFeedbackItemList = new ArrayList<>();
        }
        if (mFeedbackItemList.size() > 0) {
            mFeedbackItemList.clear();
        }
        mFeedbackItemList.addAll(_list);
        this.notifyDataSetChanged();
    }
}
