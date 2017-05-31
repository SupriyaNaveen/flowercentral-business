package com.flowercentral.flowercentralbusiness.login.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class Vendor implements Parcelable {

    @SerializedName("status")
    private String status;

    @SerializedName("api_token")
    private String apiToken;

    @SerializedName("message")
    private String message;

    @SerializedName("user_details")
    private VendorDetails vendorDetails;

    private Vendor(Parcel in) {
        status = in.readString();
        apiToken = in.readString();
        message = in.readString();
        vendorDetails = in.readParcelable(VendorDetails.class.getClassLoader());
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
        dest.writeString(apiToken);
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

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
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
