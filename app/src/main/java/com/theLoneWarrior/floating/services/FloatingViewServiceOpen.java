package com.theLoneWarrior.floating.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
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
import com.theLoneWarrior.floating.ScreenShortActivity;
import com.theLoneWarrior.floating.adapter.RecyclerViewAdapterResult;
import com.theLoneWarrior.floating.pojoClass.AppInfo;
import com.theLoneWarrior.floating.splash.SplashScreen;

import java.io.File;
import java.util.ArrayList;


public class FloatingViewServiceOpen extends Service implements RecyclerViewAdapterResult.ListItemClickListener {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private ArrayList<AppInfo> result = new ArrayList<>();
    private View expandedView;
    private WindowManager.LayoutParams params;
    Intent intentService;
    Handler handler;
    boolean wifiEnabled;
    WifiManager wifiManager;
    boolean flashEnabled;

    public FloatingViewServiceOpen() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        intentService = new Intent(FloatingViewServiceOpen.this, FloatingViewServiceClose.class);
        searchPreviousService();


        SharedPreferences selectedAppPreference = FloatingViewServiceOpen.this.getSharedPreferences("SelectedApp", Context.MODE_PRIVATE);

        String AppName = selectedAppPreference.getString("AppName", null);
        String PacName = selectedAppPreference.getString("PacName", null);
        String AppImage = selectedAppPreference.getString("AppImage", null);
        if (PacName != null && !PacName.equals("")) {
            String[] split1 = AppName.split("\\+");
            String[] split2 = PacName.split("\\+");
            String[] split3 = AppImage.split("\\+");
            result.clear();
            for (int i = 0; i < split2.length; i++) {
                AppInfo newInfo = new AppInfo();
                newInfo.setAppName(split1[i]);
                newInfo.setPacName(split2[i]);
                newInfo.setBitmapString(Uri.parse(split3[i]));
                result.add(newInfo);
            }
            split1 = null;
            split2 = null;
            split3 = null;

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
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget_new, null);
        //Add the view to the window.
        /*final WindowManager.LayoutParams*/

        SharedPreferences positionPreference = getSharedPreferences("Position", Context.MODE_PRIVATE);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
      /*  params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 200;*/
        if (positionPreference.getInt("PositionX", -6162) == -6162) {
            params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
            params.x = (int) 0;  //-60;
            params.y = 200;
        } else {
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = positionPreference.getInt("PositionX", 0); //-60;
            params.y = positionPreference.getInt("PositionY", 200);
            // Toast.makeText(this, positionPreference.getInt("PositionX", (int) convertPixelsToDp(600, this))+" reset "+positionPreference.getInt("PositionY",(int) -convertDpToPixel(30, this) ), Toast.LENGTH_SHORT).show();
        }
        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //Set the close button
        final ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.camera);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  //close the service and remove the from from the window
                Intent intent = new Intent(FloatingViewServiceOpen.this, MyIntentService.class);
                intent.setFlags(1);
                stopForeground(true);
                startService(intent);
                //stopSelf();*/

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(cameraIntent);
                startCloseService();


            }
        });


        Button floso = (Button) mFloatingView.findViewById(R.id.floso);
        floso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(FloatingViewServiceOpen.this, SplashScreen.class);
                startActivity(cameraIntent);
                stopSelf();
            }
        });

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        //
        wifiEnabled = wifiManager.isWifiEnabled();
        final Button wifi = (Button) mFloatingView.findViewById(R.id.wifi);
        if (wifiEnabled) {
            //   wifi.set();
            wifi.setBackgroundResource(R.drawable.ic_wifi_black_24dp);
        } else {
            wifi.setBackgroundResource(R.drawable.ic_signal_wifi_off_black_24dp);
        }
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiEnabled) {
                    wifiEnabled = false;
                    wifiManager.setWifiEnabled(false);
                    wifi.setBackgroundResource(R.drawable.ic_signal_wifi_off_black_24dp);
                } else {
                    wifiEnabled = true;
                    wifiManager.setWifiEnabled(true);
                    wifi.setBackgroundResource(R.drawable.ic_wifi_black_24dp);
                }
            }
        });


        final Button screenShort = (Button) mFloatingView.findViewById(R.id.screenShort);

        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                screenShort.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                Camera cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                if (cam.getParameters().getFlashMode().equals("torch")) {
                    screenShort.setBackgroundResource(R.drawable.ic_flash_on_black_24dp);
                    flashEnabled = true;
                } else {
                    screenShort.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                    flashEnabled = false;
                }
            } else {
                screenShort.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String cameraId = null; // Usually front camera is at 0 position.
                try {
                    cameraId = camManager.getCameraIdList()[0];

                    camManager.setTorchMode(cameraId, false);

                } catch (CameraAccessException e) {
                    Toast.makeText(FloatingViewServiceOpen.this, "Some Problem In Opening Camera", Toast.LENGTH_SHORT).show();
                }
                flashEnabled = false;

            }
        }
        screenShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
              /*  Intent intent = new Intent(FloatingViewServiceOpen.this, MyIntentService.class);
                intent.setFlags(1);
                getApplicationContext();
                stopForeground(true);
                startService(intent);*/
             /*  Intent intent = new Intent(FloatingViewServiceOpen.this, RecordingScreen.class);
               stopForeground(true);
               startActivity(intent);
                stopSelf();*/
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {
                    Intent intent = new Intent(FloatingViewServiceOpen.this, ScreenShortActivity.class);
                    stopForeground(true);
                    startActivity(intent);
                    startCloseService();
                    //takeScreenshot();
                } else {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                        if (!flashEnabled) {
                            screenShort.setBackgroundResource(R.drawable.ic_flash_on_black_24dp);
                            Camera cam = Camera.open();
                            Camera.Parameters p = cam.getParameters();
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            cam.setParameters(p);
                            cam.startPreview();
                            flashEnabled = true;
                        } else {
                            screenShort.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                            Camera camera2 = Camera.open();
                            Camera.Parameters parameters2 = camera2.getParameters();
                            parameters2.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            camera2.setParameters(parameters2);
                            camera2.stopPreview();
                            flashEnabled = false;
                        }


                    } else {

                        if (!flashEnabled) {
                            screenShort.setBackgroundResource(R.drawable.ic_flash_on_black_24dp);
                            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                            String cameraId = null; // Usually front camera is at 0 position.
                            try {
                                cameraId = camManager.getCameraIdList()[0];

                                camManager.setTorchMode(cameraId, true);

                            } catch (CameraAccessException e) {
                                Toast.makeText(FloatingViewServiceOpen.this, "Some Problem In Opening Camera", Toast.LENGTH_SHORT).show();
                            }
                            flashEnabled = true;
                        } else {
                            screenShort.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                            String cameraId = null; // Usually front camera is at 0 position.
                            try {
                                cameraId = camManager.getCameraIdList()[0];

                                camManager.setTorchMode(cameraId, false);

                            } catch (CameraAccessException e) {
                                Toast.makeText(FloatingViewServiceOpen.this, "Some Problem In Opening Camera", Toast.LENGTH_SHORT).show();
                            }
                            flashEnabled = false;
                        }
                    }
                }

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

                        startCloseService();
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
    public void onListItemClick(int checkedItemIndex, ArrayList<AppInfo> result) {

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
        Intent intent = new Intent(FloatingViewServiceOpen.this, MyIntentService.class);
        intent.setFlags(2);
        stopForeground(true);
        startService(intent);
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
            nApp.setLayoutManager(new LinearLayoutManager(FloatingViewServiceOpen.this));
            // nApp.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
            nApp.setHasFixedSize(true);
            nApp.setAdapter(new RecyclerViewAdapterResult(FloatingViewServiceOpen.this, result));

        } else {
            Toast.makeText(this, "No App Selected Please Select An App", Toast.LENGTH_SHORT).show();
        }

    }

    /* private void takeScreenshot() {
         Date now = new Date();
         android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

         try {
             // image naming and path  to include sd card  appending name you choose for file
             String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

             // create bitmap screen capture
             Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();

           *//*  params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);*//*

ImageView imageView= (ImageView) mFloatingView.findViewById(R.id.test);
imageView.setVisibility(View.VISIBLE);
            RelativeLayout rl = (RelativeLayout) mFloatingView.findViewById(R.id.root2_container);
            rl.setVisibility(View.INVISIBLE);
            View v1 = mFloatingView;
            v1.setDrawingCacheEnabled(true);
            Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();


            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }*/
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


}

