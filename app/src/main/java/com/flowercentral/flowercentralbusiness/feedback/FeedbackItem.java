package com.flowercentral.flowercentralbusiness.feedback;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by admin on 30-05-2017.
 */

public class FeedbackItem implements android.os.Parcelable {

    @SerializedName("rating")
    int rating;

    @SerializedName("feedback")
    String feedbackMessage;

    @SerializedName("feedback_by")
    String feedbackBy;

    @SerializedName("order_id")
    ArrayList<Integer> orderList = new ArrayList<>();

    protected FeedbackItem(Parcel in) {
        rating = in.readInt();
        feedbackMessage = in.readString();
        feedbackBy = in.readString();
        orderList = in.readArrayList(Integer.class.getClassLoader());
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
        dest.writeArray(orderList.toArray());
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public String getFeedbackBy() {
        return feedbackBy;
    }

    public void setFeedbackBy(String feedbackBy) {
        this.feedbackBy = feedbackBy;
    }

    public ArrayList<Integer> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<Integer> orderList) {
        this.orderList = orderList;
    }
}
