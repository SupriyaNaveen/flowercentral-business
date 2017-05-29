package com.flowercentral.flowercentralbusiness.order.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 17-05-2017.
 */

public class OrderDetailedItem implements Parcelable {

    @SerializedName("order_date")
    String orderDate;

    @SerializedName("order_total")
    String orderTotal;

    @SerializedName("delivery_address")
    String address;

    @SerializedName("longitude")
    String longitude;

    @SerializedName("latitude")
    String latitude;

    @SerializedName("status")
    DELIVERY_STATUS deliveryStatus; //0: pending, 1: delivered

    @SerializedName("schedule_datetime")
    String scheduleDateTime;

    @SerializedName("scheduled_delivery")
    boolean isScheduledDelivery;

    @SerializedName("products")
    List<ProductItem> productItemList = new ArrayList<>();

    /**
     * Standard basic constructor for non-parcel
     * object creation
     */
    public OrderDetailedItem() {
    }

    /**
     * Constructor to use when re-constructing object
     * from a parcel
     *
     * @param in a parcel from which to read this object
     */
    public OrderDetailedItem(Parcel in) {
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
        orderDate = in.readString();
        orderTotal = in.readString();
        address = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        deliveryStatus = DELIVERY_STATUS.valueOf(in.readString());
        scheduleDateTime = in.readString();
        isScheduledDelivery = in.readByte() != 0;
        productItemList = in.readArrayList(ProductItem.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeString(orderDate);
        dest.writeString(orderTotal);
        dest.writeString(address);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(deliveryStatus.name());
        dest.writeString(scheduleDateTime);
        dest.writeByte((byte) (isScheduledDelivery ? 1 : 0));
        dest.writeTypedList(productItemList);
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
    public static final Creator CREATOR =
            new Creator() {
                public OrderDetailedItem createFromParcel(Parcel in) {
                    return new OrderDetailedItem(in);
                }

                public OrderDetailedItem[] newArray(int size) {
                    return new OrderDetailedItem[size];
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
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

    public boolean isScheduledDelivery() {
        return isScheduledDelivery;
    }

    public void setScheduledDelivery(boolean scheduledDelivery) {
        isScheduledDelivery = scheduledDelivery;
    }

    public List<ProductItem> getProductItemList() {
        return productItemList;
    }

    public void setProductItemList(List<ProductItem> productItemList) {
        this.productItemList = productItemList;
    }
}
