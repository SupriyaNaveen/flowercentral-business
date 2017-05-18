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
        sb.append("patientApi/v1/login");
        return sb.toString();
    }

    public static String getPendingOrderListUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/getPendingOrderList");
        return sb.toString();
    }

    public static String getCompletedOrderListUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/getCompletedOrderList");
        return sb.toString();
    }

    public static String getRegisterUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/register");
        return sb.toString();
    }
}
