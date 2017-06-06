package com.flowercentral.flowercentralbusiness.feedback;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
class ViewFeedbackAdapter extends RecyclerView.Adapter<ViewFeedbackAdapter.ViewHolder> {

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feedback_item_row, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(itemView);
    }

    /**
     * For each row update the data.
     *
     * @param holder   holder
     * @param position position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final FeedbackItem feedbackItem = mFeedbackItemList.get(position);
        holder.ratingBarFeedback.setRating(feedbackItem.getRating());

        holder.textViewFeedbackMessage.setText(feedbackItem.getFeedbackMessage());
        holder.textViewFeedbackBy.setText(feedbackItem.getFeedbackBy());

        holder.textViewFeedbackOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderDetailIntent = new Intent(mContext, OrderDetailsActivity.class);
                orderDetailIntent.putExtra(mContext.getString(R.string.key_order_id), feedbackItem.getFeedbackOrderId());
                mContext.startActivity(orderDetailIntent);
            }
        });

        if (feedbackItem.getFeedbackBy().length() > 0)
            holder.circularTextViewFeedback.setText(String.valueOf(feedbackItem.getFeedbackBy().charAt(0)));
    }

    /**
     * Number of items in the list.
     *
     * @return size of list
     */
    public int getItemCount() {
        return mFeedbackItemList.size();
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
}
