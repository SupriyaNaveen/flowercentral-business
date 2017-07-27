package com.flowercentral.flowercentralbusiness.order.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 *
 */
public class ProductItem implements Parcelable {

    @SerializedName("product_qty")
    private int quantity;

    @SerializedName("title")
    private String orderTitle;

    @SerializedName("flower_details")
    private ArrayList<FlowerDetails> flowerDetails = new ArrayList<>();

    @SerializedName("category")
    private OrderItem.CATEGORY category; //S,M,L,XL, XL+, All

    @SerializedName("price")
    private double price;

    @SerializedName("img_url")
    private String imageUrl;

    @SerializedName("message")
    private String message;

//    @SerializedName("tag")
//    private String[] tag;
//
//    @SerializedName("images")
//    private String[] imagesUrlList;
//
//    @SerializedName("id")
//    private String id;

//    @SerializedName("liked")
//    private String liked;
//
//    @SerializedName("status")
//    private String status;
//
//    @SerializedName("description")
//    private String description;

    /**
     * Constructor to use when re-constructing object
     * from a parcel
     *
     * @param in a parcel from which to read this object
     */
    public ProductItem(Parcel in) {
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
        quantity = in.readInt();
        orderTitle = in.readString();
        in.readList(flowerDetails, OrderItem.class.getClassLoader());
        category = OrderItem.CATEGORY.valueOf(in.readString());
        price = in.readDouble();
        imageUrl = in.readString();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeInt(quantity);
        dest.writeList(flowerDetails);
        dest.writeString(category.name());
        dest.writeDouble(price);
        dest.writeString(message);
        dest.writeString(imageUrl);
        dest.writeString(orderTitle);
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

    public int getQuantity() {
        return quantity;
    }

    public ArrayList<FlowerDetails> getFlowerDetails() {
        return flowerDetails;
    }

    public OrderItem.CATEGORY getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderTitle() {
        return orderTitle;
    }
}
