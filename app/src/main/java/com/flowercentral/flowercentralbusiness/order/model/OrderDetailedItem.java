package com.flowercentral.flowercentralbusiness.order.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.flowercentral.flowercentralbusiness.util.Util;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OrderDetailedItem implements Parcelable {

    @SerializedName("order_date")
    private String orderDate;

    @SerializedName("order_total")
    private String orderTotal;

    @SerializedName("delivery_address")
    private String address;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("status")
    private DELIVERY_STATUS deliveryStatus; //0: pending, 1: delivered

    @SerializedName("scheduled_dateTime")
    private String scheduleDateTime;

    @SerializedName("delivery_at")
    private String deliveredDateTime;

    @SerializedName("scheduled_delivery")
    private IS_SCHEDULED_DELIVERY isScheduledDelivery; // 0 no, 1 yes

    @SerializedName("product_details")
    private List<ProductItem> productItemList = new ArrayList<>();

    private String formatDate(String dateString) {
        String srcFormat = "yyyy-MM-dd HH:mm";
        String destFormat = "dd EEE yyyy, hh:mm a";
        return Util.formatDate(dateString, srcFormat, destFormat);
    }

    /**
     * Constructor to use when re-constructing object
     * from a parcel
     *
     * @param in a parcel from which to read this object
     */
    private OrderDetailedItem(Parcel in) {
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
        isScheduledDelivery = IS_SCHEDULED_DELIVERY.valueOf(in.readString());
        in.readList(productItemList, ProductItem.class.getClassLoader());
        deliveredDateTime = in.readString();
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
        dest.writeString(isScheduledDelivery.name());
        dest.writeList(productItemList);
        dest.writeString(deliveredDateTime);
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

    public enum DELIVERY_STATUS {
        @SerializedName("Pending")
        PENDING,
        @SerializedName("Delivered")
        DELIVERED
    }

    public enum IS_SCHEDULED_DELIVERY {
        @SerializedName("0")
        No,
        @SerializedName("1")
        Yes
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public String getAddress() {
        return address;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public DELIVERY_STATUS getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getScheduleDateTime() {
        return scheduleDateTime;
    }

    public List<ProductItem> getProductItemList() {
        return productItemList;
    }

    public String getDeliveredDateTime() {
        return deliveredDateTime;
    }

    public IS_SCHEDULED_DELIVERY getIsScheduledDelivery() {
        return isScheduledDelivery;
    }

    public String getFormattedDate(String dateString) {
        String srcFormat = "yyyy-MM-dd HH:mm";
        String destFormat = "dd EEE yyyy, hh:mm a";
        return Util.formatDate(dateString, srcFormat, destFormat);
    }
}
