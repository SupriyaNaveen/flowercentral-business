package com.flowercentral.flowercentralbusiness.feedback;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.order.OrderDetailsActivity;
import com.flowercentral.flowercentralbusiness.util.CircularTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
class ViewFeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_EMPTY_LIST = 0;
    private static final int VIEW_TYPE_NON_EMPTY_LIST = 1;
    private List<FeedbackItem> mFeedbackItemList;
    private Context mContext;

    /**
     * @param list list
     */
    ViewFeedbackAdapter(List<FeedbackItem> list) {
        this.mFeedbackItemList = list;
    }

    /**
     * Initialise the feedback each item row view.
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
                View viewEmptyList = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_order_item, parent, false);
                viewHolder = new EmptyListViewHolder(viewEmptyList);
                break;

            case VIEW_TYPE_NON_EMPTY_LIST:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feedback_item_row, parent, false);
                viewHolder = new ViewHolder(itemView);
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
            ViewHolder viewHolder = (ViewHolder) holder;
            final FeedbackItem feedbackItem = mFeedbackItemList.get(position);
            viewHolder.ratingBarFeedback.setRating(feedbackItem.getRating());

            viewHolder.textViewFeedbackMessage.setText(feedbackItem.getFeedbackMessage());
            viewHolder.textViewFeedbackBy.setText(feedbackItem.getFeedbackBy());

            viewHolder.textViewFeedbackOrderDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailIntent = new Intent(mContext, OrderDetailsActivity.class);
                    orderDetailIntent.putExtra(mContext.getString(R.string.key_order_id), feedbackItem.getFeedbackOrderId());
                    mContext.startActivity(orderDetailIntent);
                }
            });

            if (feedbackItem.getFeedbackBy().length() > 0)
                viewHolder.circularTextViewFeedback.setText(String.valueOf(feedbackItem.getFeedbackBy().charAt(0)));
        } else if (holder instanceof EmptyListViewHolder) {
            EmptyListViewHolder emptyListViewHolder = (EmptyListViewHolder) holder;
            emptyListViewHolder.txtNoItemFound.setText(mContext.getString(R.string.empty_feedback_list));
            emptyListViewHolder.txtNoItemFound.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
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
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.feedback_rating)
        RatingBar ratingBarFeedback;

        @BindView(R.id.text_view_feedback_message)
        TextView textViewFeedbackMessage;

        @BindView(R.id.feedback_message_by)
        TextView textViewFeedbackBy;

        @BindView(R.id.feedback_order_details)
        TextView textViewFeedbackOrderDetails;

        @BindView(R.id.feedback_profile_pic)
        CircularTextView circularTextViewFeedback;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            circularTextViewFeedback.setStrokeWidth(1);
            circularTextViewFeedback.setStrokeColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            circularTextViewFeedback.setSolidColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            circularTextViewFeedback.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }

    private class EmptyListViewHolder extends RecyclerView.ViewHolder {

        ImageView imgNoItemFound;
        TextView txtNoItemFound;

        EmptyListViewHolder(View itemView) {
            super(itemView);
            imgNoItemFound = (ImageView) itemView.findViewById(R.id.img_no_item_found);
            txtNoItemFound = (TextView) itemView.findViewById(R.id.txt_msg_no_item_found);
        }
    }
}
