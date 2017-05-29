package com.flowercentral.flowercentralbusiness.order.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 17-05-2017.
 */

public class OrderItem implements Parcelable {

    @SerializedName("id")
    int id;

    @SerializedName("quantity")
    int quantity;

    @SerializedName("flower")
    String name;

    @SerializedName("category")
    CATEGORY category; //S,M,L,XL, XL+, All

    @SerializedName("price")
    double price;

    @SerializedName("payment_status")
    PAID_STATUS paidStatus; // 0: not paid, 1: paid

    @SerializedName("Status")
    DELIVERY_STATUS deliveryStatus; //0: pending, 1: delivered

    @SerializedName("Schedule_datetime")
    String scheduleDateTime;

    @SerializedName("Address")
    String address;

    @SerializedName("longitude")
    String longitude;

    @SerializedName("latitude")
    String latitude;

    @SerializedName("img_url")
    String imageUrl;

    @SerializedName("delivered_at")
    String deliveredSchedule;

    /**
     * Standard basic constructor for non-parcel
     * object creation
     */
    public OrderItem() {
    }

    /**
     * Constructor to use when re-constructing object
     * from a parcel
     *
     * @param in a parcel from which to read this object
     */
    public OrderItem(Parcel in) {
        readFromParcel(in);
    }

    /**
     * Called from the constructor to create this
     * object from a parcel.
     *
     * @param in parcel from which to re-create object
     */
    private void readFromParcel(Parcel in) {

        // We just need to read back each
        // field in the order that it was
        // written to the parcel
        id = in.readInt();
        quantity = in.readInt();
        name = in.readString();
        category = CATEGORY.valueOf(in.readString());
        price = in.readDouble();
        paidStatus = PAID_STATUS.valueOf(in.readString());
        deliveryStatus = DELIVERY_STATUS.valueOf(in.readString());
        scheduleDateTime = in.readString();
        deliveredSchedule = in.readString();
        address = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeInt(id);
        dest.writeInt(quantity);
        dest.writeString(name);
        dest.writeString(category.name());
        dest.writeDouble(price);
        dest.writeString(paidStatus.name());
        dest.writeString(deliveryStatus.name());
        dest.writeString(scheduleDateTime);
        dest.writeString(deliveredSchedule);
        dest.writeString(address);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(imageUrl);
    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays.
     * <p>
     * This also means that you can use use the default
     * constructor to create the object and use another
     * method to hyrdate it as necessary.
     * <p>
     * I just find it easier to use the constructor.
     * It makes sense for the way my brain thinks ;-)
     */
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public OrderItem createFromParcel(Parcel in) {
                    return new OrderItem(in);
                }

                public OrderItem[] newArray(int size) {
                    return new OrderItem[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }


    enum CATEGORY {
        @SerializedName("S")
        S,
        @SerializedName("M")
        M,
        @SerializedName("L")
        L,
        @SerializedName("X")
        XL,
        @SerializedName("XXL")
        XXL
    }

    public enum PAID_STATUS {
        @SerializedName("1")
        PENDING("Cash On Delivery"),
        @SerializedName("0")
        PAID("Paid Delivery");

        PAID_STATUS(String s) {
            this.paidStatus = s;
        }

        String paidStatus;

        public String value() {
            return paidStatus;
        }
    }

    enum DELIVERY_STATUS {
        @SerializedName("Pending")
        PENDING,
        @SerializedName("Delivered")
        DELIVERED
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CATEGORY getCategory() {
        return category;
    }

    public void setCategory(CATEGORY category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PAID_STATUS getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(PAID_STATUS paidStatus) {
        this.paidStatus = paidStatus;
    }

    public DELIVERY_STATUS getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DELIVERY_STATUS deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getScheduleDateTime() {
        return scheduleDateTime;
    }

    public void setScheduleDateTime(String scheduleDateTime) {
        this.scheduleDateTime = scheduleDateTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDeliveredSchedule() {
        return deliveredSchedule;
    }

    public void setDeliveredSchedule(String deliveredSchedule) {
        this.deliveredSchedule = deliveredSchedule;
    }

    public static Creator getCREATOR() {
        return CREATOR;
    }
}
