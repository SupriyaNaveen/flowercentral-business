package com.flowercentral.flowercentralbusiness.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 30-05-2017.
 */

public class ProfileDetails implements Parcelable {

    @SerializedName("shop_name")
    String shopName;

    @SerializedName("address")
    String address;

    @SerializedName("city")
    String city;

    @SerializedName("state")
    String state;

    @SerializedName("country")
    String country;

    @SerializedName("pin")
    String pin;

    @SerializedName("latitude")
    Double latitude;

    @SerializedName("longitude")
    Double longitude;

    @SerializedName("phone1")
    String phone1;

    @SerializedName("phone2")
    String phone2;

    @SerializedName("tin_num")
    String tinNumber;

    protected ProfileDetails(Parcel in) {
        shopName = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        phone1 = in.readString();
        phone2 = in.readString();
        tinNumber = in.readString();
    }

    public static final Creator<ProfileDetails> CREATOR = new Creator<ProfileDetails>() {
        @Override
        public ProfileDetails createFromParcel(Parcel in) {
            return new ProfileDetails(in);
        }

        @Override
        public ProfileDetails[] newArray(int size) {
            return new ProfileDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shopName);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(pin);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(phone1);
        dest.writeString(phone2);
        dest.writeString(tinNumber);
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }
}
