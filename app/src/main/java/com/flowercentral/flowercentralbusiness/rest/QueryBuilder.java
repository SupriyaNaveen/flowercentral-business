package com.flowercentral.flowercentralbusiness.rest;

import com.flowercentral.flowercentralbusiness.BuildConfig;

/**
 *
 */
public class QueryBuilder {

    public static String getLoginUrl() {
        return BuildConfig.SERVER +
                "login";
    }

    public static String getPendingOrderListUrl() {
        return BuildConfig.SERVER +
                "pending";
    }

    public static String getCompletedOrderListUrl() {
        return BuildConfig.SERVER +
                "delivered";
    }

    public static String getRegisterUrl() {
        return BuildConfig.SERVER +
                "registration";
    }

    public static String getOrderDetailsUrl() {
        return BuildConfig.SERVER +
                "order_details";
    }

    public static String getUpdateUrl() {
        return BuildConfig.SERVER +
                "update_profile";
    }

    public static String getProfileInformationUrl() {
        return BuildConfig.SERVER +
                "get_profile";
    }

    public static String getMarkDeliveredUrl() {
        return BuildConfig.SERVER +
                "mark_delivered";
    }

    public static String getChangePasswordUrl() {
        return BuildConfig.SERVER +
                "change_password";
    }

    public static String getFeedbackListUrl() {
        return BuildConfig.SERVER +
                "feedbacks";
    }

    public static String getAcceptOrderUrl() {
        return BuildConfig.SERVER +
                "accept_order";
    }

    public static String getShopPictureUrl() {
        return BuildConfig.SERVER +
                "shop_picture";
    }

    public static String getRemovePictureUrl() {
        return BuildConfig.SERVER +
                "remove_shop_picture";
    }

    public static String getUploadShopPicturesUrl() {
        return BuildConfig.SERVER +
                "update_new";
    }
}
