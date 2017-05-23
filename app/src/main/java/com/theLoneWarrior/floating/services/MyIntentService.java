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
        System.gc();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

          /*  //Intent in = new Intent(this, MainActivity.class);
            Intent i = new Intent();
            String pkg = "com.example.android.floating";
            String cls = "com.example.android.floating.FloatingViewService";
            i.setComponent(new ComponentName(pkg, cls));
           // startService(i);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(i);

            PendingIntent intent1 = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Hello")
                    .setContentText("Kumar rohit")
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(intent1)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            int NOTIFICATION_ID = 382;
            notificationManager.notify(NOTIFICATION_ID, notification);*/

            Intent notificationIntent = new Intent(this, FloatingViewService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);

            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Background Work")
                    .setContentText("Click To Start Floating Shortcut")
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            stopService(new Intent(this, FloatingViewService.class));

            notificationManager.notify(382, notification);
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
