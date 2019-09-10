package com.soft_sketch.hereim;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class BaseApp extends Application {
    public static final String CHANNEL_1_ID ="channel_1_id";
    public static final String CHANNEL_2_ID ="channel_2_id";

    @Override
    public void onCreate() {
        super.onCreate();

       createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel channel1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel1",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel1.setDescription("Channel 1 Description");
            channel1.enableVibration(true);

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel2",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel2.setDescription("Channel 2 Description");
            channel2.enableVibration(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }

    }
}
