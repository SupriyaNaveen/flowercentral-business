package com.flowercentral.flowercentralbusiness.order.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.flowercentral.flowercentralbusiness.util.Util;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class OrderItem implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("flower")
    private String name;

    @SerializedName("category")
    private CATEGORY category; //S,M,L,XL, XL+, All

    @SerializedName("price")
    private double price;

    @SerializedName("payment_status")
    private PAID_STATUS paidStatus; // 0: not paid, 1: paid

    @SerializedName("Status")
    private DELIVERY_STATUS deliveryStatus; //0: pending, 1: delivered

    @SerializedName("Schedule_datetime")
    private String scheduleDateTime;

    @SerializedName("Address")
    private String address;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("img_url")
    private String imageUrl;

    @SerializedName("delivered_at")
    private String deliveredSchedule;

    /**
     * Constructor to use when re-constructing object
     * from a parcel
     *
     * @param in a parcel from which to read this object
     */
    OrderItem(Parcel in) {
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


    public enum CATEGORY {
        @SerializedName("S")
        S,
        @SerializedName("M")
        M,
        @SerializedName("L")
        L,
        @SerializedName("XL")
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

    private enum DELIVERY_STATUS {
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

    public String getName() {
        return name;
    }

    public CATEGORY getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public PAID_STATUS getPaidStatus() {
        return paidStatus;
    }

    public DELIVERY_STATUS getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getScheduleDateTime() {
        return scheduleDateTime;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDeliveredSchedule() {
        return deliveredSchedule;
    }

    public static Creator getCREATOR() {
        return CREATOR;
    }

    public String getFormattedDate(String dateString) {
        String srcFormat = "yyyy-MM-dd HH:mm";
        String destFormat = "dd EEE yyyy, hh:mm a";
        return Util.formatDate(dateString, srcFormat, destFormat);
    }
}
