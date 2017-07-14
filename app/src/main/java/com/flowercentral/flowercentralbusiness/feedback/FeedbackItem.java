package com.flowercentral.flowercentralbusiness.feedback;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class FeedbackItem implements android.os.Parcelable {

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

    public int getRating() {
        return rating;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public String getFeedbackBy() {
        return feedbackBy;
    }

    public int getFeedbackOrderId() {
        return feedbackOrderId;
    }

    public String getFeedbackByLetter(String feedbackBy) {
        if (feedbackBy.length() > 0)
            return String.valueOf(feedbackBy.charAt(0));
        return "";
    }
}
