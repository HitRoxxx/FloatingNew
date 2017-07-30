package com.theLoneWarrior.floating.services;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.theLoneWarrior.floating.R;
import com.theLoneWarrior.floating.circular_menu.FloatingActionButton;
import com.theLoneWarrior.floating.circular_menu.FloatingActionMenu;
import com.theLoneWarrior.floating.circular_menu.SubActionButton;
import com.theLoneWarrior.floating.splash.SplashScreen;


public class SystemOverlayMenuService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private FloatingActionButton rightLowerButton;
    private FloatingActionButton topCenterButton;
    private FloatingActionButton centerButton;

    private FloatingActionMenu rightLowerMenu;
    private FloatingActionMenu topCenterMenu;
    private FloatingActionMenu centerMenu;
    boolean flashEnabled;
    private boolean serviceWillBeDismissed;
    WifiManager wifiManager;
    boolean wifiEnabled;
    boolean isEnabled;
    BluetoothAdapter bluetoothAdapter;

    public SystemOverlayMenuService() {
    }

    public class LocalBinder extends Binder {
        SystemOverlayMenuService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SystemOverlayMenuService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        serviceWillBeDismissed = false;

        // Set up the white button on the lower right corner
        // more or less with default parameter
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        WindowManager.LayoutParams params = FloatingActionButton.Builder.getDefaultSystemWindowParams(this);

        rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .setSystemOverlay(true)
                .setBackgroundDrawable(R.drawable.button_sub_action_dark_selector)
                .setLayoutParams(params)
                .build();


        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView screenRecording = new ImageView(this);
        ImageView cameraVideoMode = new ImageView(this);
        ImageView cameraPictureVideo = new ImageView(this);
        final ImageView flashMode = new ImageView(this);

        screenRecording.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat_light));
        cameraVideoMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video_light));
        cameraPictureVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));
        //  flashMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_black_24dp));

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        SubActionButton rlSub1 = rLSubBuilder.setContentView(screenRecording).build();
        SubActionButton rlSub2 = rLSubBuilder.setContentView(cameraVideoMode).build();
        SubActionButton rlSub3 = rLSubBuilder.setContentView(cameraPictureVideo).build();
        SubActionButton rlSub4 = rLSubBuilder.setContentView(flashMode).build();
        rightLowerMenu = new FloatingActionMenu.Builder(this, true)
                .addSubActionView(rlSub1, rlSub1.getLayoutParams().width, rlSub1.getLayoutParams().height)
                .addSubActionView(rlSub2, rlSub2.getLayoutParams().width, rlSub2.getLayoutParams().height)
                .addSubActionView(rlSub3, rlSub3.getLayoutParams().width, rlSub3.getLayoutParams().height)
                .addSubActionView(rlSub4, rlSub4.getLayoutParams().width, rlSub4.getLayoutParams().height)
                .setStartAngle(180)
                .setEndAngle(270)
                .attachTo(rightLowerButton)
                .build();

        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });
        ////ON CLick ON Button//////////////////////////////////////////////////////////////////////////////////////////////

        cameraPictureVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(cameraIntent);
                startCloseService();
            }
        });

        cameraVideoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivity(cameraIntent);
                startCloseService();
            }
        });

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                flashMode.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                Camera cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                if (cam.getParameters().getFlashMode().equals("torch")) {
                    flashMode.setBackgroundResource(R.drawable.ic_flash_on_black_24dp);
                    flashEnabled = true;
                } else {
                    flashMode.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                    flashEnabled = false;
                }
            } else {
                flashMode.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String cameraId = null; // Usually front camera is at 0 position.
                try {
                    cameraId = camManager.getCameraIdList()[0];

                    camManager.setTorchMode(cameraId, false);

                } catch (CameraAccessException e) {
                    Toast.makeText(SystemOverlayMenuService.this, "Some Problem In Opening Camera", Toast.LENGTH_SHORT).show();
                }
                flashEnabled = false;

            }
        } else {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Toast.makeText(this, "No Flash in System", Toast.LENGTH_SHORT).show();
            }
        }
        flashMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {
                    Intent intent = new Intent(SystemOverlayMenuService.this, ScreenShortActivity.class);
                    stopForeground(true);
                    startActivity(intent);
                    startCloseService();
                    //takeScreenshot();
                } else {*/
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    if (!flashEnabled) {
                        flashMode.setBackgroundResource(R.drawable.ic_flash_on_black_24dp);
                        Camera cam = Camera.open();
                        Camera.Parameters p = cam.getParameters();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        cam.setParameters(p);
                        cam.startPreview();
                        flashEnabled = true;
                    } else {
                        flashMode.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                        Camera camera2 = Camera.open();
                        Camera.Parameters parameters2 = camera2.getParameters();
                        parameters2.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera2.setParameters(parameters2);
                        camera2.stopPreview();
                        flashEnabled = false;
                    }


                } else {

                    if (!flashEnabled) {
                        flashMode.setBackgroundResource(R.drawable.ic_flash_on_black_24dp);
                        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        String cameraId = null; // Usually front camera is at 0 position.
                        try {
                            cameraId = camManager.getCameraIdList()[0];

                            camManager.setTorchMode(cameraId, true);

                        } catch (CameraAccessException e) {
                            Toast.makeText(SystemOverlayMenuService.this, "Some Problem In Opening Camera", Toast.LENGTH_SHORT).show();
                        }
                        flashEnabled = true;
                    } else {
                        flashMode.setBackgroundResource(R.drawable.ic_flash_off_black_24dp);
                        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        String cameraId = null; // Usually front camera is at 0 position.
                        try {
                            cameraId = camManager.getCameraIdList()[0];

                            camManager.setTorchMode(cameraId, false);

                        } catch (CameraAccessException e) {
                            Toast.makeText(SystemOverlayMenuService.this, "Some Problem In Opening Camera", Toast.LENGTH_SHORT).show();
                        }
                        flashEnabled = false;
                    }
                }
            }

            //  }
        });

        screenRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SystemOverlayMenuService.this, "Not Implemented", Toast.LENGTH_SHORT).show();
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        // Set up the large red button on the top center side
        // With custom button and content sizes and margins
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        final ImageView fabIconStar = new ImageView(this);
        fabIconStar.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));


        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
      /*  fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);*/

        WindowManager.LayoutParams params2 = FloatingActionButton.Builder.getDefaultSystemWindowParams(this);
        params2.width = redActionButtonSize;
        params2.height = redActionButtonSize;

        topCenterButton = new FloatingActionButton.Builder(this)
                .setSystemOverlay(true)
                .setContentView(fabIconStar, fabIconStarParams)
                .setBackgroundDrawable(R.mipmap.ic_launcher)
                .setPosition(FloatingActionButton.POSITION_CENTER)
                .setLayoutParams(params2)
                .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder tCSubBuilder = new SubActionButton.Builder(this);
        tCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

        SubActionButton.Builder tCRedBuilder = new SubActionButton.Builder(this);
        tCRedBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_red_selector));

        SubActionButton.Builder tCRedBuilderFloSo = new SubActionButton.Builder(this);
        tCRedBuilderFloSo.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_launcher));

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);

        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        tCSubBuilder.setLayoutParams(blueParams);
        tCRedBuilder.setLayoutParams(blueParams);

        ImageView floSo = new ImageView(this);
        ImageView tcIcon2 = new ImageView(this);
        ImageView home = new ImageView(this);
        ImageView back = new ImageView(this);
        ImageView closeSystemOverlay = new ImageView(this);
        final ImageView wifi = new ImageView(this);
        ImageView listViewApp = new ImageView(this);
        final ImageView blueTooth = new ImageView(this);

        //  floSo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        tcIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_picture));
        home.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_black));
        back.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace));
        closeSystemOverlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_cancel));
        // wifi.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_headphones));
        listViewApp.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat));
        //  blueTooth.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_important));

        SubActionButton tcSub1 = tCRedBuilderFloSo.setContentView(floSo, blueContentParams).build();
        SubActionButton tcSub2 = tCSubBuilder.setContentView(tcIcon2, blueContentParams).build();
        SubActionButton tcSub3 = tCSubBuilder.setContentView(home, blueContentParams).build();
        SubActionButton tcSub4 = tCSubBuilder.setContentView(back, blueContentParams).build();
        SubActionButton tcSub5 = tCRedBuilder.setContentView(closeSystemOverlay, blueContentParams).build();
        SubActionButton tcSub6 = tCSubBuilder.setContentView(wifi, blueContentParams).build();
        SubActionButton tcSub7 = tCSubBuilder.setContentView(listViewApp, blueContentParams).build();
        SubActionButton tcSub8 = tCSubBuilder.setContentView(blueTooth, blueContentParams).build();


        // Build another menu with custom options
        topCenterMenu = new FloatingActionMenu.Builder(this, true)
                .addSubActionView(tcSub1, tcSub1.getLayoutParams().width, tcSub1.getLayoutParams().height)
                .addSubActionView(tcSub2, tcSub2.getLayoutParams().width, tcSub2.getLayoutParams().height)
                .addSubActionView(tcSub3, tcSub3.getLayoutParams().width, tcSub3.getLayoutParams().height)
                .addSubActionView(tcSub4, tcSub4.getLayoutParams().width, tcSub4.getLayoutParams().height)
                .addSubActionView(tcSub5, tcSub5.getLayoutParams().width, tcSub5.getLayoutParams().height)
                .addSubActionView(tcSub6, tcSub6.getLayoutParams().width, tcSub6.getLayoutParams().height)
                .addSubActionView(tcSub7, tcSub7.getLayoutParams().width, tcSub7.getLayoutParams().height)
                .addSubActionView(tcSub8, tcSub8.getLayoutParams().width, tcSub8.getLayoutParams().height)
                .setRadius(redActionMenuRadius)
                .setStartAngle(-180)
                .setEndAngle(180)
                .attachTo(topCenterButton)
                .build();


        topCenterMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconStar.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();

            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {

                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconStar.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();
                topCenterMenu.hide();
                centerMenu.show();
                centerMenu.open(true);

                if (serviceWillBeDismissed) {
                    SystemOverlayMenuService.this.stopSelf();
                    serviceWillBeDismissed = false;
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //   topCenterMenu.setSystemOvelayMenuService(SystemOverlayMenuService.this);
                rightLowerMenu.open(true);
                topCenterMenu.open(true);
            }
        }, 200);
        // make the red button terminate the service
        closeSystemOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startCloseService();
            }
        });

        oneMore();


        //////////////////////////////////////////////////

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   KeyEvent kdown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
             //   dispatchKeyEvent(kdown);
            }
        });

        floSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent cameraIntent = new Intent(SystemOverlayMenuService.this, SplashScreen.class);
                        startActivity(cameraIntent);
                    }
                }).start();

                startCloseService();
            }
        });


        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        //
        wifiEnabled = wifiManager.isWifiEnabled();
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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isEnabled = bluetoothAdapter.isEnabled();
        if (isEnabled) {
            blueTooth.setBackgroundResource(R.drawable.ic_bluetooth_on);
        } else {
            blueTooth.setBackgroundResource(R.drawable.ic_bluetooth_disabled);
        }

        blueTooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnabled) {
                    bluetoothAdapter.disable();
                    blueTooth.setBackgroundResource(R.drawable.ic_bluetooth_disabled);
                    isEnabled = false;
                } else {
                    blueTooth.setBackgroundResource(R.drawable.ic_bluetooth_on);
                    bluetoothAdapter.enable();
                    isEnabled = true;
                }

            }
        });

        listViewApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(SystemOverlayMenuService.this, FloatingViewServiceOpenIconOnly.class));
                // startCloseService();
                serviceWillBeDismissed = true; // the order is important
                topCenterMenu.close(true);
                rightLowerMenu.close(true);
                centerMenu.close(true);
                stopSelf();
            }
        });
        ////////////////////////////////////////////////////
    }


    private void startCloseService() {
        serviceWillBeDismissed = true; // the order is important
        topCenterMenu.close(true);
        rightLowerMenu.close(true);
        centerMenu.close(true);
        Intent intent = new Intent(SystemOverlayMenuService.this, MyIntentService.class);
        intent.setFlags(5);
        startService(intent);
        stopSelf();
    }

    private void oneMore() {

        // Set up the large red button on the top center side
        // With custom button and content sizes and margins
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        final ImageView fabIcon = new ImageView(this);
        fabIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));


        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
      /*  fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);*/

        WindowManager.LayoutParams params2 = FloatingActionButton.Builder.getDefaultSystemWindowParams(this);
        params2.width = redActionButtonSize;
        params2.height = redActionButtonSize;

        centerButton = new FloatingActionButton.Builder(this)
                .setSystemOverlay(true)
                .setContentView(fabIcon, fabIconStarParams)
                .setBackgroundDrawable(R.mipmap.ic_launcher)
                .setPosition(FloatingActionButton.POSITION_CENTER)
                .setLayoutParams(params2)
                .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder tCSubBuilder = new SubActionButton.Builder(this);
        tCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

        SubActionButton.Builder tCRedBuilder = new SubActionButton.Builder(this);
        tCRedBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_red_selector));

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);

        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        tCSubBuilder.setLayoutParams(blueParams);
        tCRedBuilder.setLayoutParams(blueParams);

        ImageView tcIcon1 = new ImageView(this);
        ImageView tcIcon2 = new ImageView(this);
        ImageView tcIcon3 = new ImageView(this);
        ImageView tcIcon4 = new ImageView(this);
        ImageView tcIcon5 = new ImageView(this);
        ImageView tcIcon6 = new ImageView(this);

        tcIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera));
        tcIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_picture));
        tcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video));
        tcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location_found));
        tcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_headphones));
        tcIcon6.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_cancel));

        SubActionButton tcSub1 = tCSubBuilder.setContentView(tcIcon1, blueContentParams).build();
        SubActionButton tcSub2 = tCSubBuilder.setContentView(tcIcon2, blueContentParams).build();
        SubActionButton tcSub3 = tCSubBuilder.setContentView(tcIcon3, blueContentParams).build();
        SubActionButton tcSub4 = tCSubBuilder.setContentView(tcIcon4, blueContentParams).build();
        SubActionButton tcSub5 = tCSubBuilder.setContentView(tcIcon5, blueContentParams).build();
        SubActionButton tcSub6 = tCRedBuilder.setContentView(tcIcon6, blueContentParams).build();


        // Build another menu with custom options
        centerMenu = new FloatingActionMenu.Builder(this, true)
                .addSubActionView(tcSub1, tcSub1.getLayoutParams().width, tcSub1.getLayoutParams().height)
                .addSubActionView(tcSub2, tcSub2.getLayoutParams().width, tcSub2.getLayoutParams().height)
                .addSubActionView(tcSub3, tcSub3.getLayoutParams().width, tcSub3.getLayoutParams().height)
                .addSubActionView(tcSub4, tcSub4.getLayoutParams().width, tcSub4.getLayoutParams().height)
                .addSubActionView(tcSub5, tcSub5.getLayoutParams().width, tcSub5.getLayoutParams().height)
                .addSubActionView(tcSub6, tcSub6.getLayoutParams().width, tcSub6.getLayoutParams().height)
                .setRadius(redActionMenuRadius)
                .setStartAngle(-180)
                .setEndAngle(0)
                .attachTo(centerButton)
                .build();


        centerMenu.hide();
        centerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIcon.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIcon, pvhR);
                animation.start();

            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIcon.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIcon, pvhR);
                animation.start();
                topCenterMenu.show();
                topCenterMenu.open(true);
                centerMenu.hide();
                if (serviceWillBeDismissed) {
                    SystemOverlayMenuService.this.stopSelf();
                    serviceWillBeDismissed = false;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (rightLowerMenu != null && rightLowerMenu.isOpen()) rightLowerMenu.close(false);
        if (topCenterMenu != null && topCenterMenu.isOpen()) topCenterMenu.close(false);
        if (centerMenu != null && centerMenu.isOpen()) centerMenu.close(false);
        if (rightLowerButton != null) rightLowerButton.detach();
        if (topCenterButton != null) topCenterButton.detach();
        if (centerButton != null) centerButton.detach();

        super.onDestroy();
    }

    public void closeFloatingOverlay() {
        serviceWillBeDismissed = true; // the order is important
        topCenterMenu.close(true);
    }
}
