package com.flowercentral.flowercentralbusiness.notification;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
class NotificationData implements Parcelable {

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
    private String NotificationType;

    NotificationData() {
    }

    private NotificationData(Parcel in) {
        this.NotificationId = in.readInt();
        this.NotificationType = in.readString();
    }


    public Bundle getData() {
        Bundle bundle = new Bundle();
        bundle.putString("NotificationType", getNotificationType());
        return bundle;
    }

    private String getNotificationType() {
        return NotificationType;
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

