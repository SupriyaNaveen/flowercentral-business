package com.flowercentral.flowercentralbusiness.rest;

import com.flowercentral.flowercentralbusiness.BuildConfig;

/**
 * Created by Ashish Upadhyay on 11/20/16.
 */

public class QueryBuilder {

    private static String profileUpdateUrl;

    public static String getLoginUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("login");
        return sb.toString();
    }

    public static String getPendingOrderListUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("pending");
        return sb.toString();
    }

    public static String getCompletedOrderListUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("delivered");
        return sb.toString();
    }

    public static String getRegisterUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("registration");
        return sb.toString();
    }

    public static String getOrderDetailsUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("orderdetails");
        return sb.toString();
    }
}
