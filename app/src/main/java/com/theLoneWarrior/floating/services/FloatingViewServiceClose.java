package com.theLoneWarrior.floating.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.theLoneWarrior.floating.R;


public class FloatingViewServiceClose extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private WindowManager.LayoutParams params;
    Handler handler;
    Point p;
    private SharedPreferences positionPreference;

    public FloatingViewServiceClose() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


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

    private RelativeLayout removeView, hideView;
    private ImageView removeImg, hideImage;
    private WindowManager windowManager;
    private GestureDetector gestureDetector;

    @Override
    public void onCreate() {
        super.onCreate();

        positionPreference = getSharedPreferences("Position", Context.MODE_PRIVATE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        p = new Point();
        Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        d.getSize(p);
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_closed_widget, null);
        removeView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.remove, null);
        hideView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.hide, null);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

        removeView.setVisibility(View.GONE);
        removeImg = (ImageView) removeView.findViewById(R.id.remove_img);
        windowManager.addView(removeView, paramRemove);

        hideView.setVisibility(View.GONE);
        hideImage = (ImageView) hideView.findViewById(R.id.hide_img);
        windowManager.addView(hideView, paramRemove);


        //Inflate the floating view layout we created

        //Add the view to the window.
        /*final WindowManager.LayoutParams*/
     /*   params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);*/

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //Specify the view position
        if (positionPreference.getInt("PositionX", -6162) == -6162) {
            params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
            params.x = (int) -convertDpToPixel(30, this);  //-60;
            params.y = (int) convertPixelsToDp(600, this);
        } else {
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = positionPreference.getInt("PositionX", (int) -convertDpToPixel(30, this)); //-60;
            params.y = positionPreference.getInt("PositionY", (int) convertPixelsToDp(600, this));
            // Toast.makeText(this, positionPreference.getInt("PositionX", (int) convertPixelsToDp(600, this))+" reset "+positionPreference.getInt("PositionY",(int) -convertDpToPixel(30, this) ), Toast.LENGTH_SHORT).show();
        }


        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new Movement());
        mFloatingView.findViewById(R.id.root_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (getSharedPreferences("Setting", Context.MODE_PRIVATE).getInt("OutputView", 0)) {
                    case 0: {
                        startService(new Intent(FloatingViewServiceClose.this, FloatingViewServiceOpen.class));
                        break;
                    }
                    case 1: {
                        startService(new Intent(FloatingViewServiceClose.this, FloatingViewServiceOpenIconOnly.class));
                        break;
                    }
                    case 2: {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                startService(new Intent(FloatingViewServiceClose.this, SystemOverlayMenuService.class));
                                //     startActivity(new Intent(FloatingViewServiceClose.this, FloatingSystem.class));
                            }
                        }).start();

                        break;
                    }
                }


                stopForeground(true);
                stopSelf();
            }
        });
/*\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*/


    }

    ///// movement ontouch listener class ///
    private class Movement implements View.OnTouchListener {

        long time_start = 0, time_end = 0;
        boolean isLongclick = false, inBounded = false, inHide = false;
        int remove_img_width = 0, remove_img_height = 0;

        Handler handler_longClick = new Handler();
        Runnable runnable_longClick = new Runnable() {

            @Override
            public void run() {

                isLongclick = true;
                removeView.setVisibility(View.VISIBLE);
                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                int x_cord_remove = (p.x - removeView.getWidth()) / 2;
                int y_cord_remove = (int) (p.y - (removeView.getHeight() + convertPixelsToDp(200f, FloatingViewServiceClose.this)));

                param_remove.x = x_cord_remove;
                param_remove.y = y_cord_remove;

                windowManager.updateViewLayout(removeView, param_remove);

                hideView.setVisibility(View.VISIBLE);
                WindowManager.LayoutParams param_hide = (WindowManager.LayoutParams) hideView.getLayoutParams();
                // int x_cord_remove = (p.x - removeView.getWidth()) / 2;
                //  int y_cord_remove = (int) (p.y - (removeView.getHeight() + convertPixelsToDp(60f, FloatingViewServiceClose.this)));

                param_hide.x = x_cord_remove;
                param_hide.y = 0;

                windowManager.updateViewLayout(hideView, param_remove);
            }
        };


        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x_cord = (int) event.getRawX();
            int y_cord = (int) event.getRawY();
            int x_cord_Destination, y_cord_Destination;                // your code for move and drag

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    time_start = System.currentTimeMillis();
                    handler_longClick.postDelayed(runnable_longClick, 600);

                    remove_img_width = removeImg.getLayoutParams().width;
                    remove_img_height = removeImg.getLayoutParams().height;

                    //remember the initial position.
                    initialX = params.x;
                    initialY = params.y;

                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    break;
                //return true;
                case MotionEvent.ACTION_UP:
                    //   Toast.makeText(FloatingViewServiceClose.this, ""+a+"   "+event.getRawX(), Toast.LENGTH_SHORT).show();
                   /* if (isReadyToClose) {
                     //   Intent intent = new Intent(FloatingViewServiceClose.this, MyIntentService.class);
                    ///    intent.setFlags(4);
                        stopForeground(true);
                        handler_longClick.removeCallbacks(runnable_longClick);
                      //  startService(intent);
                        Toast.makeText(FloatingViewServiceClose.this, "end", Toast.LENGTH_SHORT).show();
                        stopSelf();
                    }*/
                    removeView.setVisibility(View.GONE);
                    hideView.setVisibility(View.GONE);

                    removeImg.getLayoutParams().height = remove_img_height;
                    removeImg.getLayoutParams().width = remove_img_width;

                    hideImage.getLayoutParams().height = remove_img_height;
                    hideImage.getLayoutParams().width = remove_img_width;

                    handler_longClick.removeCallbacks(runnable_longClick);

                    if (inBounded) {
                       /* if(MyDialog.active){
                            MyDialog.myDialog.finish();
                        }*/
                        stopService(new Intent(FloatingViewServiceClose.this, FloatingViewServiceClose.class));
                        inBounded = false;
                        break;
                    }
                    if (inHide) {
                       /* if(MyDialog.active){
                            MyDialog.myDialog.finish();
                        }*/
                        //  stopService(new Intent(FloatingViewServiceClose.this, FloatingViewServiceClose.class));
                        Intent intent = new Intent(FloatingViewServiceClose.this, MyIntentService.class);
                        stopForeground(true);
                        startService(intent);
                        inHide = false;
                        break;
                    }

                    int XDiff = (int) (event.getRawX() - initialTouchX);
                    int YDiff = (int) (event.getRawY() - initialTouchY);


                    if (event.getRawX() > (p.x / 2)) {
                        params.x = (int) (p.x - convertDpToPixel(30, FloatingViewServiceClose.this));
                        ;//(int) convertDpToPixel(p.x/2,FloatingViewServiceClose.this)-( (int) (convertDpToPixel(30,FloatingViewServiceClose.this)));//-60;/*initialX + (int) (event.getRawX() - initialTouchX);*/
                        // Toast.makeText(FloatingViewServiceClose.this, ""+p.x, Toast.LENGTH_SHORT).show();
                        if (convertPixelsToDp(600, FloatingViewServiceClose.this) < initialY + (int) (event.getRawY() - initialTouchY)) {

                            if ((p.y - convertPixelsToDp(600, FloatingViewServiceClose.this)) > initialY + (int) (event.getRawY() - initialTouchY)) {
                                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            } else {
                                params.y = (int) (p.y - convertPixelsToDp(600, FloatingViewServiceClose.this));
                            }

                        } else {
                            params.y = (int) convertPixelsToDp(600, FloatingViewServiceClose.this);
                        }
                    } else {
                        params.x = (int) -convertDpToPixel(30, FloatingViewServiceClose.this);//-60;/*initialX + (int) (event.getRawX() - initialTouchX);*/
                        if (convertPixelsToDp(600, FloatingViewServiceClose.this) < initialY + (int) (event.getRawY() - initialTouchY)) {

                            if ((p.y - convertPixelsToDp(600, FloatingViewServiceClose.this)) > initialY + (int) (event.getRawY() - initialTouchY)) {
                                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            } else {
                                params.y = (int) (p.y - convertPixelsToDp(600, FloatingViewServiceClose.this));
                            }

                        } else {
                            params.y = (int) convertPixelsToDp(600, FloatingViewServiceClose.this);
                        }
                    }

                    ///////////////////////////////setting Place ///////////////////////
                    SharedPreferences.Editor editor = positionPreference.edit();
                    editor.putInt("PositionX", params.x);
                    editor.putInt("PositionY", params.y);
                    editor.apply();
                    //  Toast.makeText(FloatingViewServiceClose.this, params.x+" "+params.y, Toast.LENGTH_SHORT).show();
                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    //The check for XDiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                    //So that is click event.
                    if (XDiff < 10 && YDiff < 10) {
                        //     new Thread(new Runnable() {
                        //         @Override
                        //        public void run() {

                    }
                    return false;
                case MotionEvent.ACTION_MOVE:


                  /*  int x_diff_move = x_cord - initialX;
                    int y_diff_move = y_cord - initialY;
*/

                    if (isLongclick) {
                        int x_bound_left = p.x / 2 - (int) (remove_img_width * 1.5);
                        int x_bound_right = p.x / 2 + (int) (remove_img_width * 1.5);
                        int y_bound_top = p.y - (int) (remove_img_height * 1.5);
                        int y_bound_bottom = (int) (remove_img_height * 1.5);

                        if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                            inBounded = true;

                            int x_cord_remove = (int) ((p.x - (remove_img_height * 1.5)) / 2);
                            int y_cord_remove = (int) (p.y - ((remove_img_width * 1.5) + convertPixelsToDp(200f, FloatingViewServiceClose.this)));

                            if (removeImg.getLayoutParams().height == remove_img_height) {
                                removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);


                                WindowManager.LayoutParams param_remove1 = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                param_remove1.x = x_cord_remove;
                                param_remove1.y = y_cord_remove;


                                windowManager.updateViewLayout(removeView, param_remove1);
                            }

                            params.x = (int) (x_cord_remove + convertPixelsToDp(270, FloatingViewServiceClose.this));
                            params.y = (int) (y_cord_remove + convertPixelsToDp(270, FloatingViewServiceClose.this));

                            windowManager.updateViewLayout(mFloatingView, params);
                            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            //  vib.vibrate(300);
                            if (vib.hasVibrator()) {
                                vib.vibrate(100);
                            }
                            break;
                        }
                        if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord <= y_bound_bottom) {

                            inHide = true;

                            int x_cord_remove = (int) ((p.x - (remove_img_height * 1.5)) / 2);
                            int y_cord_hide = 0/*(int) (((remove_img_width * 1.5) ))*/;

                            if (hideImage.getLayoutParams().height == remove_img_height) {
                                hideImage.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                hideImage.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                WindowManager.LayoutParams param_remove1 = (WindowManager.LayoutParams) hideView.getLayoutParams();


                                param_remove1.x = x_cord_remove;
                                param_remove1.y = y_cord_hide;

                                windowManager.updateViewLayout(hideView, param_remove1);
                            }
                            params.x = (int) (x_cord_remove + convertPixelsToDp(270, FloatingViewServiceClose.this));
                            params.y = (int) (y_cord_hide + convertPixelsToDp(270, FloatingViewServiceClose.this));

                            windowManager.updateViewLayout(mFloatingView, params);
                            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            //  vib.vibrate(300);
                           /* if (vib.hasVibrator()) {
                                vib.vibrate(100);
                            }*/
                            break;


                        } else {
                            inBounded = false;
                            inHide = false;
                            removeImg.getLayoutParams().height = remove_img_height;
                            removeImg.getLayoutParams().width = remove_img_width;

                            hideImage.getLayoutParams().height = remove_img_height;
                            hideImage.getLayoutParams().width = remove_img_width;

                            WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                            int x_cord_remove = (p.x - removeView.getWidth()) / 2;
                            int y_cord_remove = (int) (p.y - (removeView.getHeight() + convertPixelsToDp(200f, FloatingViewServiceClose.this)));

                            param_remove.x = x_cord_remove;
                            param_remove.y = y_cord_remove;

                            windowManager.updateViewLayout(removeView, param_remove);

                            WindowManager.LayoutParams param_remove1 = (WindowManager.LayoutParams) hideView.getLayoutParams();
                            int x_cord_remove1 = (p.x - hideView.getWidth()) / 2;
                            int y_cord_remove1 = 0;

                            param_remove1.x = x_cord_remove1;
                            param_remove1.y = y_cord_remove1;

                            windowManager.updateViewLayout(hideView, param_remove);
                        }

                    }
                    //Calculate the X and Y coordinates of the view.
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);

                    params.y = initialY + (int) (event.getRawY() - initialTouchY);

                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    break;

            }


            return false;
        }
    }


    public float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        handler.removeCallbacksAndMessages(null);
        // handler.getLooper().quit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Intent intent = new Intent(FloatingViewServiceClose.this, MyIntentService.class);
        intent.setFlags(5);
        stopForeground(true);
        startService(intent);
        stopSelf();
    }

    /*private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Toast.makeText(FloatingViewServiceClose.this, "Hello", Toast.LENGTH_SHORT).show();
            Log.e("Hi","Hi");
            return true;
        }
    }*/


}

