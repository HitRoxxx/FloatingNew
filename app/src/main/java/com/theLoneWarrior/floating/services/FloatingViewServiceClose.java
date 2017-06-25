package com.theLoneWarrior.floating.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.theLoneWarrior.floating.R;


public class FloatingViewServiceClose extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private WindowManager.LayoutParams params;
    Handler handler;

    public FloatingViewServiceClose() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Bundle b = intent.getExtras();
        //////////////////////////////////////////////setting result from database/////////////////

        //populate Service as a notification);
        // Notification notification =
        startForeground(1, new Notification.Builder(this)
                .setContentTitle("Floating Shortcut")
                .setContentText("select shortcut")
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setTicker("HI")
                .build());
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Runtime.getRuntime().gc();
                } else {
                    System.gc();
                }
                handler.postDelayed(this, 5000);
            }
        });


        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_closed_widget, null);
        //Add the view to the window.
        /*final WindowManager.LayoutParams*/
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = (int) -convertDpToPixel(30, this);  //-60;
        params.y = (int) convertPixelsToDp(600,this);

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new Movement());

/*\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*/


    }

    ///// movement ontouch listener class ///
    private class Movement implements View.OnTouchListener {

        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //remember the initial position.
                    initialX = params.x;
                    initialY = params.y;

                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_UP:
                    int XDiff = (int) (event.getRawX() - initialTouchX);
                    int YDiff = (int) (event.getRawY() - initialTouchY);

                    Point p = new Point();
                    Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    d.getSize(p);
                    if (event.getRawX() > (p.x / 2)) {
                        params.x = (int) (p.x - convertDpToPixel(30, FloatingViewServiceClose.this));
                        ;//(int) convertDpToPixel(p.x/2,FloatingViewServiceClose.this)-( (int) (convertDpToPixel(30,FloatingViewServiceClose.this)));//-60;/*initialX + (int) (event.getRawX() - initialTouchX);*/
                        // Toast.makeText(FloatingViewServiceClose.this, ""+p.x, Toast.LENGTH_SHORT).show();
                        if (convertPixelsToDp(600,FloatingViewServiceClose.this) < initialY + (int) (event.getRawY() - initialTouchY)) {

                            if((p.y-convertPixelsToDp(600,FloatingViewServiceClose.this) )>initialY + (int) (event.getRawY() - initialTouchY))
                            {
                                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            }
                            else
                            {
                                params.y = (int) (p.y-convertPixelsToDp(600,FloatingViewServiceClose.this)) ;
                            }

                        }
                        else
                        {
                            params.y =(int) convertPixelsToDp(600,FloatingViewServiceClose.this);
                        }
                    } else {
                        params.x = (int) -convertDpToPixel(30, FloatingViewServiceClose.this);//-60;/*initialX + (int) (event.getRawX() - initialTouchX);*/
                        if (convertPixelsToDp(600,FloatingViewServiceClose.this) < initialY + (int) (event.getRawY() - initialTouchY)) {

                            if((p.y-convertPixelsToDp(600,FloatingViewServiceClose.this) )>initialY + (int) (event.getRawY() - initialTouchY))
                            {
                                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            }
                            else
                            {
                                params.y = (int) (p.y-convertPixelsToDp(600,FloatingViewServiceClose.this)) ;
                            }

                        }
                        else
                        {
                            params.y =(int) convertPixelsToDp(600,FloatingViewServiceClose.this);
                        }
                    }


                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    //The check for XDiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                    //So that is click event.
                    if (XDiff < 10 && YDiff < 10) {
                        //     new Thread(new Runnable() {
                        //         @Override
                        //        public void run() {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FloatingViewServiceClose.this);
                        // String syncConnPref = sharedPref.getString("OutputVie", "");
                        if (sharedPref.getBoolean("OutputView", true)) {
                            startService(new Intent(FloatingViewServiceClose.this, FloatingViewServiceOpen.class));
                        } else {
                            startService(new Intent(FloatingViewServiceClose.this, FloatingViewServiceOpenIconOnly.class));
                        }
                        //


                        //         }
                        //      }).start();
                        stopForeground(true);
                        stopSelf();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //Calculate the X and Y coordinates of the view.
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);

                    params.y = initialY + (int) (event.getRawY() - initialTouchY);


                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    return true;

            }


            return false;
        }
    }


    public float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public  float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        handler.removeCallbacksAndMessages(null);
        // handler.getLooper().quit();
    }


}

