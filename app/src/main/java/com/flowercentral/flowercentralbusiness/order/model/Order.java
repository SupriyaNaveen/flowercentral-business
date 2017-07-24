package com.flowercentral.flowercentralbusiness.order.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Order implements Parcelable {

    @SerializedName("order_total")
    private double orderTotal;

    @SerializedName("order_details")
    private ArrayList<OrderItem> orderItemArrayList = new ArrayList<>();

    protected Order(Parcel in) {
        orderTotal = in.readDouble();
        in.readList(orderItemArrayList, OrderItem.class.getClassLoader());
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(orderTotal);
        dest.writeList(orderItemArrayList);
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public ArrayList<OrderItem> getOrderItemArrayList() {
        return orderItemArrayList;
    }
}
