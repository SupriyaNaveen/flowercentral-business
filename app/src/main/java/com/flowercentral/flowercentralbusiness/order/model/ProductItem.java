package com.flowercentral.flowercentralbusiness.order.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 26-05-2017.
 */

public class ProductItem implements Parcelable {

    @SerializedName("quantity")
    int quantity;

    @SerializedName("flower")
    String name;

    @SerializedName("category")
    OrderItem.CATEGORY category; //S,M,L,XL, XL+, All

    @SerializedName("price")
    double price;

    @SerializedName("image")
    String imageUrl;

    @SerializedName("message")
    String message;

    @SerializedName("tag")
    String tag;

    /**
     * Standard basic constructor for non-parcel
     * object creation
     */
    public ProductItem() {
    }

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
        name = in.readString();
        category = OrderItem.CATEGORY.valueOf(in.readString());
        price = in.readDouble();
        imageUrl = in.readString();
        message = in.readString();
        tag = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeInt(quantity);
        dest.writeString(name);
        dest.writeString(category.name());
        dest.writeDouble(price);
        dest.writeString(message);
        dest.writeString(imageUrl);
        dest.writeString(tag);

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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderItem.CATEGORY getCategory() {
        return category;
    }

    public void setCategory(OrderItem.CATEGORY category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
