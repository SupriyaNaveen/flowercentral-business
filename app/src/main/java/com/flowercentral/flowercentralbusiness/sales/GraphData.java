package com.flowercentral.flowercentralbusiness.sales;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

class GraphData implements Parcelable {

    @SerializedName("date")
    private String weekDate;

    @SerializedName("total_orders")
    private Integer totalOrders;

    @SerializedName("total_sale")
    private Double totalSales;

    @SerializedName("startdate")
    private String startDate;

    @SerializedName("enddate")
    private String endDate;

    private GraphData(Parcel in) {
        weekDate = in.readString();
        totalOrders = in.readInt();
        totalSales = in.readDouble();
        startDate = in.readString();
        endDate = in.readString();
    }

    public static final Creator<GraphData> CREATOR = new Creator<GraphData>() {
        @Override
        public GraphData createFromParcel(Parcel in) {
            return new GraphData(in);
        }

        @Override
        public GraphData[] newArray(int size) {
            return new GraphData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(weekDate);
        dest.writeInt(totalOrders);
        dest.writeDouble(totalSales);
        dest.writeString(startDate);
        dest.writeString(endDate);
    }

    Double getTotalSales() {
        return totalSales;
    }

    String getWeekDate() {
        return weekDate;
    }
}
