package com.flowercentral.flowercentralbusiness.order;

/**
 * Created by admin on 17-05-2017.
 */

public class OrderItem {
    int quantity;
    String name;
    CATEGORY category; //S,M,L,XL, XL+, All
    double price;
    PAID_STATUS paidStatus; // 0: not paid, 1: paid
    DELIVERY_STATUS deliveryStatus; //0: pending, 1: delivered
    long scheduleInMillis;
    String address;

    enum CATEGORY {S, M, L, XL, XLL}

    enum PAID_STATUS {PENDING, PAID}

    enum DELIVERY_STATUS {PENDING, DELIVERED}

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

    public long getScheduleInMillis() {
        return scheduleInMillis;
    }

    public void setScheduleInMillis(long scheduleInMillis) {
        this.scheduleInMillis = scheduleInMillis;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
