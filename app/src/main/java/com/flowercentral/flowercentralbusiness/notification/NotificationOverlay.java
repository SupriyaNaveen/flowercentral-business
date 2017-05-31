package com.flowercentral.flowercentralbusiness.notification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.util.CircularTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 25-05-2017.
 */

public class NotificationOverlay extends AppCompatActivity {

    @BindView(R.id.text_view_timer)
    CircularTextView textViewTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_overlay);
        ButterKnife.bind(this);

        textViewTimer.setStrokeWidth(1);
        textViewTimer.setStrokeColor(ContextCompat.getColor(this, R.color.colorPrimary));
        textViewTimer.setSolidColor(ContextCompat.getColor(this, R.color.colorWhite));
        textViewTimer.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        textViewTimer.setText(String.valueOf(10) + "\nSec");

    }
}
