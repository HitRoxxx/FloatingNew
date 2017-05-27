package com.theLoneWarrior.floating.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.theLoneWarrior.floating.R;
import com.theLoneWarrior.floating.adapter.RecyclerViewAdapterResult;
import com.theLoneWarrior.floating.database.AppDataStorage;
import com.theLoneWarrior.floating.pojoClass.PackageInfoStruct;

import java.util.ArrayList;


public class FloatingViewServiceNew extends Service implements RecyclerViewAdapterResult.ListItemClickListener {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private ArrayList<PackageInfoStruct> result = new ArrayList<>();
    private View expandedView;
    private WindowManager.LayoutParams params;
    Intent intentService;

    public FloatingViewServiceNew() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        intentService = new Intent(FloatingViewServiceNew.this, FloatingViewServiceClose.class);
        searchPreviousService();

        // Bundle b = intent.getExtras();
        //////////////////////////////////////////////setting result from database/////////////////
        SQLiteDatabase db = new AppDataStorage(this).getReadableDatabase();
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
        db.close();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Runtime.getRuntime().gc();
        } else {
            System.gc();
        }
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
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget_new, null);
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
        params.y = 200;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                Intent intent = new Intent(FloatingViewServiceNew.this, MyIntentService.class);
                intent.setFlags(1);
                startService(intent);
                //stopSelf();
            }
        });


        Button minimize = (Button) mFloatingView.findViewById(R.id.minimize);
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                Intent intent = new Intent(FloatingViewServiceNew.this, MyIntentService.class);
                startService(intent);
                // stopSelf();
                //stopSelf();
            }
        });

       /* final ImageView openShortcut = (ImageView) mFloatingView.findViewById(R.id.collapsed_iv);
        openShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShortcut();
            }
        });*/

       /* Button closeShortcut = (Button) mFloatingView.findViewById(R.id.radioButton);
        closeShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShortcut(true);
            }
        });*/


        //Drag and move floating view using user's touch action.

/*\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*/

        mFloatingView.findViewById(R.id.radioButton).setOnTouchListener(new Movement());

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

                        Intent intent = new Intent(FloatingViewServiceNew.this, MyIntentService.class);
                        intent.setFlags(2);
                        startService(intent);
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


    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);

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
        startService(intentService);
        stopSelf();

    }


    ///////////// Attaching recycle view result with data //////////////////////

    private void populateRecycleView() {
//
        if (!result.isEmpty()) {

            RecyclerView nApp = (RecyclerView) mFloatingView.findViewById(R.id.recyclerView);
            if (result.size() > 4) {
                RelativeLayout main = (RelativeLayout) mFloatingView.findViewById(R.id.up_view);
                RelativeLayout.LayoutParams shape = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.recycle));
                shape.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    shape.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                }*/
                shape.addRule(RelativeLayout.ALIGN_TOP, R.id.up_iv);
                shape.setMargins(220, 220, 0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    nApp.setBackgroundColor(this.getColor(R.color.param));
                } else {
                    //noinspection deprecation
                    nApp.setBackgroundColor(getResources().getColor(R.color.param));
                }
                main.removeView(nApp);
                main.addView(nApp, shape);
            }
            nApp.setLayoutManager(new LinearLayoutManager(FloatingViewServiceNew.this));
            // nApp.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
            nApp.setHasFixedSize(true);
            nApp.setAdapter(new RecyclerViewAdapterResult(FloatingViewServiceNew.this, result));

        } else {
            Toast.makeText(this, "No App Selected Please Select An App", Toast.LENGTH_SHORT).show();
        }

    }

}

