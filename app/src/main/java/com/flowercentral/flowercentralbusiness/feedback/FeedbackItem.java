package com.flowercentral.flowercentralbusiness.feedback;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
class FeedbackItem implements android.os.Parcelable {

    @SerializedName("rating")
    private int rating;

    @SerializedName("feedback")
    private String feedbackMessage;

    @SerializedName("feedback_by")
    private String feedbackBy;

    @SerializedName("order_id")
    private int feedbackOrderId;

    private FeedbackItem(Parcel in) {
        rating = in.readInt();
        feedbackMessage = in.readString();
        feedbackBy = in.readString();
        feedbackOrderId = in.readInt();
    }

    public static final Creator<FeedbackItem> CREATOR = new Creator<FeedbackItem>() {
        @Override
        public FeedbackItem createFromParcel(Parcel in) {
            return new FeedbackItem(in);
        }

        @Override
        public FeedbackItem[] newArray(int size) {
            return new FeedbackItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rating);
        dest.writeString(feedbackMessage);
        dest.writeString(feedbackBy);
        dest.writeInt(feedbackOrderId);
    }

    int getRating() {
        return rating;
    }

    String getFeedbackMessage() {
        return feedbackMessage;
    }

    String getFeedbackBy() {
        return feedbackBy;
    }

    int getFeedbackOrderId() {
        return feedbackOrderId;
    }
}
