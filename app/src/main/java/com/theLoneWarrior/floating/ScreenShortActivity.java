package com.theLoneWarrior.floating;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.theLoneWarrior.floating.services.FloatingViewServiceClose;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenShortActivity extends AppCompatActivity {
    private static final String TAG = "FloSo";
    private static final int REQUEST_CODE = 100;
    //   private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;
    private DisplayMetrics mMetrics;
    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private OrientationChangeCallback mOrientationChangeCallback;

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;

            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    /// old method
                    // create bitmap
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                   /* int offset = 0;

                    bitmap = Bitmap.createBitmap(mMetrics, mWidth, mHeight, Bitmap.Config.ARGB_8888);
                    for (int i = 0; i < mHeight; ++i) {
                        for (int j = 0; j < mWidth; ++j) {
                            int pixel = 0;
                            pixel |= (buffer.get(offset) & 0xff) << 16;     // R
                            pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
                            pixel |= (buffer.get(offset + 2) & 0xff);       // B
                            pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
                            bitmap.setPixel(j, i, pixel);
                            offset += pixelStride;
                        }
                        offset += rowPadding;
                    }*/



                  /*  IMAGES_PRODUCED++;
                    if(IMAGES_PRODUCED==1)
                    {*/
                    // stopProjection();
                    Log.e("Hello", "captured image: " + IMAGES_PRODUCED);

                    if(IMAGES_PRODUCED == 4 )
                    {
                        String aa = getFilename();
                        // write bitmap to a file
                        fos = new FileOutputStream(aa);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        startService(new Intent(ScreenShortActivity.this, FloatingViewServiceClose.class));
                        openScreenshot(new File(aa));
                        finish();
                    }
                    if (IMAGES_PRODUCED ==2)
                    {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (sMediaProjection != null) {
                                    sMediaProjection.stop();
                                }
                            }
                        });
                    }

                    //   }
                    Log.e(TAG, "captured image: " + IMAGES_PRODUCED++);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }
        }
    }

    private class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Toast.makeText(ScreenShortActivity.this, "ScreenCapture  stopping projection.", Toast.LENGTH_SHORT).show();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    /****************************************** Activity Lifecycle methods ************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_screen_short);

        // call for the projection manager
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        startProjection();
        // start capture handling thread
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            if (sMediaProjection != null) {

                // display metrics
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                mDensity = metrics.densityDpi;
                // mDisplay = getWindowManager().getDefaultDisplay();
                mDisplay = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register orientation change callback
                mOrientationChangeCallback = new OrientationChangeCallback(this);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }

                // register media projection stop callback
                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }
        }
    }

    /****************************************** UI Widget Callbacks *******************************/
    private void startProjection() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sMediaProjection != null) {
                    sMediaProjection.stop();
                }
            }
        });
    }

    /****************************************** Factoring Virtual Display creation ****************/
    private void createVirtualDisplay() {
        // get width and height

        final Point windowSize = new Point();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealSize(windowSize);

        mWidth = windowSize.x;
        mHeight = windowSize.y;
        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay("FloSo", mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, "FloSo");

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png");
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
}