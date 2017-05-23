package com.flowercentral.flowercentralbusiness.preference;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 23-05-2017.
 */

public class Vendor implements Parcelable {

    @SerializedName("status")
    private String status;

    @SerializedName("api_token")
    private String accessToken;

    @SerializedName("message")
    private String message;

    @SerializedName("user_details")
    private VendorDetails vendorDetails;

    protected Vendor(Parcel in) {
        status = in.readString();
        accessToken = in.readString();
        message = in.readString();
        vendorDetails = (VendorDetails) in.readParcelable(VendorDetails.class.getClassLoader());
    }

    public static final Creator<Vendor> CREATOR = new Creator<Vendor>() {
        @Override
        public Vendor createFromParcel(Parcel in) {
            return new Vendor(in);
        }

        @Override
        public Vendor[] newArray(int size) {
            return new Vendor[size];
        }
    };

    public Vendor() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(accessToken);
        dest.writeString(message);

        // Add inner class
        dest.writeParcelable(vendorDetails, flags);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String apiToken) {
        this.accessToken = apiToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VendorDetails getVendorDetails() {
        return vendorDetails;
    }

    public void setVendorDetails(VendorDetails vendorDetails) {
        this.vendorDetails = vendorDetails;
    }

    public static Creator<Vendor> getCREATOR() {
        return CREATOR;
    }
}
