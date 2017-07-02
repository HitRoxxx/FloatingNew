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
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.theLoneWarrior.floating.R;
import com.theLoneWarrior.floating.adapter.RecyclerViewAdapterResultIconOnly;
import com.theLoneWarrior.floating.pojoClass.PackageInfoStruct;

import java.util.ArrayList;


public class FloatingViewServiceOpenIconOnly extends Service implements RecyclerViewAdapterResultIconOnly.ListItemClickListener {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private ArrayList<PackageInfoStruct> result = new ArrayList<>();
    private View expandedView;
    private WindowManager.LayoutParams params;
    Intent intentService;
    Handler handler;

    public FloatingViewServiceOpenIconOnly() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
        //Log.d("RUN","");
        intentService = new Intent(FloatingViewServiceOpenIconOnly.this, FloatingViewServiceClose.class);
        searchPreviousService();

        // Bundle b = intent.getExtras();
        //////////////////////////////////////////////setting result from database/////////////////
      /*  SQLiteDatabase db = new AppDataStorage(this).getReadableDatabase();
        Cursor cursor = db.query(
                "APP_DATA",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PackageInfoStruct newInfo = new PackageInfoStruct();
                newInfo.setAppName(cursor.getString(1));
                newInfo.setPacName(cursor.getString(2));
                newInfo.setBitmapString(Uri.parse(cursor.getString(3)));
                result.add(newInfo);
            } while (cursor.moveToNext());
            cursor.close();

        }
        db.close();*/
       /* Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();*/


        SharedPreferences selectedAppPreference = FloatingViewServiceOpenIconOnly.this.getSharedPreferences("SelectedApp", Context.MODE_PRIVATE);

        String AppName = selectedAppPreference.getString("AppName", null);
        String PacName = selectedAppPreference.getString("PacName", null);
        String AppImage = selectedAppPreference.getString("AppImage", null);
        if (PacName != null && !PacName.equals("") && AppName != null && AppImage != null) {
            String[] split1 = AppName.split("\\+");
            String[] split2 = PacName.split("\\+");
            String[] split3 = AppImage.split("\\+");
            result.clear();
            for (int i = 0; i < split2.length; i++) {
                PackageInfoStruct newInfo = new PackageInfoStruct();
                newInfo.setAppName(split1[i]);
                newInfo.setPacName(split2[i]);
                newInfo.setBitmapString(Uri.parse(split3[i]));
                result.add(newInfo);
            }

        }


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
        populateRecycleView();
        //populate Service as a notification);
        // Notification notification =
        startForeground(111, new Notification.Builder(this)
                .setContentTitle("Floating Shortcut")
                .setContentText("select shortcut")
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setTicker("HI")
                .build());

        return START_NOT_STICKY;
    }

    private void searchPreviousService() {
        stopService(intentService);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget_icon_only, null);

        //Add the view to the window.
        /*final WindowManager.LayoutParams*/
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        // params.x = (int) -convertDpToPixel(30, this);  //-60;
        params.y = (int) convertPixelsToDp(600, this);

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        mFloatingView.findViewById(R.id.side_view).setOnTouchListener(new Movement());
        mFloatingView.findViewById(R.id.side_view_Left).setOnTouchListener(new Movement());
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


                    //The check for XDiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                    //So that is click event.
                    if (XDiff < 10 && YDiff < 10) {

                        startCloseService();
                    }
                  /*  params.x = 0;
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);*/
                    Point p = new Point();
                    Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    d.getSize(p);
                    View lv = mFloatingView.findViewById(R.id.side_view_Left);
                    if (event.getRawX() > (p.x / 2)) {
                        params.x = p.x;//(int) (p.x - convertDpToPixel(20, FloatingViewServiceOpenIconOnly.this));

                        lv.setVisibility(View.VISIBLE);
                        lv = mFloatingView.findViewById(R.id.side_view);
                        lv.setVisibility(View.GONE);
                        //(int) convertDpToPixel(p.x/2,FloatingViewServiceClose.this)-( (int) (convertDpToPixel(30,FloatingViewServiceClose.this)));//-60;/*initialX + (int) (event.getRawX() - initialTouchX);*/
                        // Toast.makeText(FloatingViewServiceClose.this, ""+p.x, Toast.LENGTH_SHORT).show();
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    } else {
                        lv.setVisibility(View.GONE);
                        lv = mFloatingView.findViewById(R.id.side_view);
                        lv.setVisibility(View.VISIBLE);
                        params.x = 0;
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    }

                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(mFloatingView, params);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        handler.removeCallbacksAndMessages(null);
        //  handler.getLooper().quit();
        /*for (int i = 0; i < 4; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Runtime.getRuntime().gc();
            } else {
                System.gc();
            }
        }*/
    }


    @Override
    public void onListItemClick(int checkedItemIndex, ArrayList<PackageInfoStruct> result) {

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(result.get(checkedItemIndex).getPacName().trim());
        //  launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(this, " Some Internal Error Occurred ", Toast.LENGTH_SHORT).show();
        }
        startCloseService();

    }

    void startCloseService() {
        Intent intent = new Intent(FloatingViewServiceOpenIconOnly.this, MyIntentService.class);
        intent.setFlags(3);
        stopForeground(true);
        startService(intent);
    }
    ///////////// Attaching recycle view result with data //////////////////////

    private void populateRecycleView() {
//
        if (!result.isEmpty()) {

            RecyclerView nApp = (RecyclerView) mFloatingView.findViewById(R.id.recyclerViewIconOnly);
            nApp.setLayoutManager(new LinearLayoutManager(FloatingViewServiceOpenIconOnly.this));
            // nApp.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
            nApp.setHasFixedSize(true);
            nApp.setAdapter(new RecyclerViewAdapterResultIconOnly(FloatingViewServiceOpenIconOnly.this, result));
            Toast.makeText(this, "Run", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No App Selected Please Select An App", Toast.LENGTH_SHORT).show();
        }

    }

  /*  public float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }*/

    public float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}

