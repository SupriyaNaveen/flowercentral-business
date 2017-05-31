package com.flowercentral.flowercentralbusiness.login.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class VendorDetails implements Parcelable {

    @SerializedName("shop_name")
    private String vendorShopName;

    @SerializedName("email")
    private String vendorEmail;

    @SerializedName("address")
    private String vendorAddress;
//    private ArrayList<String> vendorImageList = new ArrayList<>();

    public VendorDetails() {
    }

    private VendorDetails(Parcel in) {
        vendorShopName = in.readString();
        vendorEmail = in.readString();
        vendorAddress = in.readString();
//        in.readTypedList(vendorImageList, String.class);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vendorShopName);
        dest.writeString(vendorEmail);
        dest.writeString(vendorAddress);
//        dest.writeTypedList(vendorImageList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VendorDetails> CREATOR = new Creator<VendorDetails>() {
        @Override
        public VendorDetails createFromParcel(Parcel in) {
            return new VendorDetails(in);
        }

        @Override
        public VendorDetails[] newArray(int size) {
            return new VendorDetails[size];
        }
    };

    public String getVendorShopName() {
        return vendorShopName;
    }

    public void setVendorShopName(String mVendorShopName) {
        this.vendorShopName = mVendorShopName;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String mVendorEmail) {
        this.vendorEmail = mVendorEmail;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String mVendorAddress) {
        this.vendorAddress = mVendorAddress;
    }

//    public ArrayList<String> getVendorImageList() {
//        return vendorImageList;
//    }
//
//    public void setVendorImageList(ArrayList<String> vendorImageList) {
//        this.vendorImageList = vendorImageList;
//    }

    public static Creator<VendorDetails> getCREATOR() {
        return CREATOR;
    }
}

