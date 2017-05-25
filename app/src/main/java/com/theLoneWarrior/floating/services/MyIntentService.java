package com.theLoneWarrior.floating.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import com.theLoneWarrior.floating.R;

public class MyIntentService extends IntentService {
    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            int flag = intent.getFlags();

            if (flag == 1) {
                stopService(new Intent(this, FloatingViewService.class));
                stopSelf();

            } else {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                stopService(new Intent(this, FloatingViewService.class));

                notificationManager.notify(382, new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Background Work")
                        .setContentText("Click To Start Floating Shortcut")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentIntent(PendingIntent.getService(this, 0, new Intent(this, FloatingViewService.class), 0))
                        .build());
            }


            for (int i = 0; i < 10; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Runtime.getRuntime().gc();
                } else {
                    System.gc();
                }
            }

        }
    }


}
