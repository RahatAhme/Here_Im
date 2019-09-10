package com.soft_sketch.hereim.lockscreen;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import com.soft_sketch.hereim.AppUseActivity;
import com.soft_sketch.hereim.R;


public class UnlockReciever extends BroadcastReceiver  {
       static int count=0;
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences preferences = context.getSharedPreferences("unlock", Context.MODE_PRIVATE);
            count = preferences.getInt("unlock", AppUseActivity.unlock);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.app_icon)
                                .setContentTitle("App Usage")
                                .setContentText("Today device unlocked "+ ++count +" times ");


                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
                if(count>80)
                {
                    count=0;
                }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("unlock", count);
            editor.commit();
        }


}

