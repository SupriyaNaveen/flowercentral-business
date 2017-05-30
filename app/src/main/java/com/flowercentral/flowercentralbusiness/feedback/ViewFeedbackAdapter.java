package com.flowercentral.flowercentralbusiness.feedback;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 30-05-2017.
 */

public class ViewFeedbackAdapter extends RecyclerView.Adapter<ViewFeedbackAdapter.ViewHolder> {

    private static final String TAG = ViewFeedbackAdapter.class.getSimpleName();
    private List<FeedbackItem> mFeedbackItemList;
    private Context mContext;
    private final RelativeLayout mRootLayout;

    public ViewFeedbackAdapter(List<FeedbackItem> list, RelativeLayout rootLayout) {
        this.mFeedbackItemList = list;
        mRootLayout = rootLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feedback_item_row, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.ratingBarFeedback.setRating(mFeedbackItemList.get(position).getRating());

        holder.textViewFeedbackMessage.setText(mFeedbackItemList.get(position).getFeedbackMessage());
        holder.textViewFeedbackBy.setText(mFeedbackItemList.get(position).getFeedbackBy());

        holder.textViewFeedbackOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public int getItemCount() {
        return mFeedbackItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.feedback_rating)
        RatingBar ratingBarFeedback;

        @BindView(R.id.text_view_feedback_message)
        TextView textViewFeedbackMessage;

        @BindView(R.id.feedback_message_by)
        TextView textViewFeedbackBy;

        @BindView(R.id.feedback_order_details)
        TextView textViewFeedbackOrderDetails;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
