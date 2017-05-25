package com.flowercentral.flowercentralbusiness.notification;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 25-05-2017.
 */

public class NotificationData implements Parcelable {

    public static final Parcelable.Creator<NotificationData> CREATOR = new Parcelable.Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel source) {
            return new NotificationData(source);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };
    private Integer NotificationId;
    private String NotificationServerId;
    //pending
    private String NotificationType;

    public NotificationData() {
    }

    protected NotificationData(Parcel in) {
        this.NotificationId = in.readInt();
        this.NotificationType = in.readString();
    }


    public Bundle getData() {
        Bundle bundle = new Bundle();
        bundle.putString("NotificationType", getNotificationType());
        return bundle;
    }

    public Integer getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(Integer notificationId) {
        NotificationId = notificationId;
    }

    public String getNotificationServerId() {
        return NotificationServerId;
    }

    public void setNotificationServerId(String notificationServerId) {
        NotificationServerId = notificationServerId;
    }

    public String getNotificationType() {
        return NotificationType;
    }

    public void setNotificationType(String notificationType) {
        NotificationType = notificationType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.NotificationId);
        dest.writeString(this.NotificationType);
    }
}

