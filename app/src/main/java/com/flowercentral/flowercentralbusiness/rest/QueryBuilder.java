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
        sb.append("order_details");
        return sb.toString();
    }

    public static String getUpdateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("update_profile");
        return sb.toString();
    }

    public static String getProfileInformationUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("get_profile");
        return sb.toString();
    }

    public static String getMarkDeliveredUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("mark_delivered");
        return sb.toString();
    }

    public static String getChangePasswordUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("change_password");
        return sb.toString();
    }

    public static String getFeedbackListUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("feedbacks");
        return sb.toString();
    }
}
