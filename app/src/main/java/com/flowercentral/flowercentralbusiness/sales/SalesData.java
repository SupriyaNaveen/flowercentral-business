package com.flowercentral.flowercentralbusiness.sales;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class SalesDetails implements Parcelable {

    @SerializedName("total_orders")
    private Integer totalOrders;

    @SerializedName("total_sale")
    private Integer totalSales;

    @SerializedName("startdate")
    private String startDate;

    @SerializedName("enddate")
    private String endDate;

    @SerializedName("month_year")
    private String month;

    @SerializedName("graph_objects")
    private ArrayList<GraphData> graphDataArrayList = new ArrayList<>();

    private SalesDetails(Parcel in) {
        totalOrders = in.readInt();
        totalSales = in.readInt();
        month = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        in.readList(graphDataArrayList, GraphData.class.getClassLoader());
    }

    public static final Creator<SalesDetails> CREATOR = new Creator<SalesDetails>() {
        @Override
        public SalesDetails createFromParcel(Parcel in) {
            return new SalesDetails(in);
        }

        @Override
        public SalesDetails[] newArray(int size) {
            return new SalesDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(totalOrders);
        dest.writeInt(totalSales);
        dest.writeString(month);
        dest.writeList(graphDataArrayList);
        dest.writeString(startDate);
        dest.writeString(endDate);
    }

    Integer getTotalOrders() {
        return totalOrders;
    }

    Integer getTotalSales() {
        return totalSales;
    }

    ArrayList<GraphData> getGraphDataArrayList() {
        return graphDataArrayList;
    }

    String getStartDate() {
        return startDate;
    }

    String getEndDate() {
        return endDate;
    }

    String getMonth() {
        return month;
    }
}
