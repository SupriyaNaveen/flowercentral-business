package com.flowercentral.flowercentralbusiness.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ShopPictureDetails implements Parcelable {

    @SerializedName("picture_id")
    private String pictureId;

    @SerializedName("img_url")
    private String imageUrl;

    private ShopPictureDetails(Parcel in) {
        pictureId = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<ShopPictureDetails> CREATOR = new Creator<ShopPictureDetails>() {
        @Override
        public ShopPictureDetails createFromParcel(Parcel in) {
            return new ShopPictureDetails(in);
        }

        @Override
        public ShopPictureDetails[] newArray(int size) {
            return new ShopPictureDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pictureId);
        dest.writeString(imageUrl);
    }

    public String getPictureId() {
        return pictureId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
