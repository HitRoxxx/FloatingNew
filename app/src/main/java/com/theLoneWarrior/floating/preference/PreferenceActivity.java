package com.theLoneWarrior.floating.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.theLoneWarrior.floating.R;

import static com.theLoneWarrior.floating.splash.SplashScreen.mInterstitialAd;


public class PreferenceActivity extends AppCompatActivity {
    SharedPreferences settingPreference;

    int selectedView, temp;;
    NativeExpressAdView mAdView;
    VideoController mVideoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
//////////////////////////////ADS////////////////////

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
//////////////////////////////////////////////////////////////
        settingPreference = PreferenceActivity.this.getSharedPreferences("Setting", Context.MODE_PRIVATE);
       /* if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(R.string.setting);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(getDarkerColor(Color.RED)));
        }*/
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.setting);
        }
        selectedView = settingPreference.getInt("OutputView", 0);
        setOutputView();


        CardView outputView = (CardView) findViewById(R.id.outputView);

        outputView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PreferenceActivity.this);
                // Specify the dialog is not cancelable
                builder.setCancelable(false);

                // Set a title for alert dialog
                builder.setTitle("Your preferred Output View");


                // String array for alert dialog multi choice items
                final String[] outputView = new String[]{
                        "Default",
                        "Only Shortcut List",
                        "Every Thing",
                };
                builder.setSingleChoiceItems(outputView, selectedView, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp=selectedView;
                        selectedView=which;
                    }
                });

/*

                Toast.makeText(PreferenceActivity.this, ""+selectedView, Toast.LENGTH_SHORT).show();
                builder.setSingleChoiceItems(outputView, selectedView, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),
                               which+ " " + outputView[which], Toast.LENGTH_SHORT).show();
                    }

                });
*/


                // Set the positive/yes button click listener
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click positive button
                        SharedPreferences.Editor editor = settingPreference.edit();
                        editor.clear();
                        editor.putInt("OutputView", selectedView);
                        editor.apply();
                      //  selectedView = which;
                        setOutputView();

                    }
                })

                // Set the negative/no button click listener
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click the negative button
                        selectedView=temp;
                        Toast.makeText(PreferenceActivity.this, "Nothing Change", Toast.LENGTH_SHORT).show();
                    }
                })

                // Set the neutral/cancel button click listener
                .setNeutralButton("Default", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click the neutral button
                        SharedPreferences.Editor editor = settingPreference.edit();
                        editor.clear();
                        editor.putInt("OutputView", 0);
                        editor.apply();
                        selectedView = 0;
                        setOutputView();
                    }
                });

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
            }
        });
///////////////////////////////////NativeAd//////////////////////////////////////////////
        // Locate the NativeExpressAdView.
        mAdView = (NativeExpressAdView) findViewById(R.id.nativeExpressAdView);
        // Set its video options.
        mAdView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

        // The VideoController can be used to get lifecycle events and info about an ad's video
        // asset. One will always be returned by getVideoController, even if the ad has no video
        // asset.
        mVideoController = mAdView.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {

                super.onVideoEnd();
            }
        });

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mVideoController.hasVideoContent()) {

                } else {

                }
            }
        });

        mAdView.loadAd(new AdRequest.Builder().build());

        /////////////////////////////////////////////////////////////////////////////////////////////

    }

    private void setOutputView() {
        ImageView outPutViewImage = (ImageView) findViewById(R.id.outputViewImage);
        TextView outPutViewText = (TextView) findViewById(R.id.outputViewText);
        switch (selectedView) {
            case 0: {
                outPutViewText.setText("Default");
                outPutViewImage.setBackgroundResource(R.mipmap.ic_launcher);
                break;
            }
            case 1: {
                outPutViewText.setText("Only Shortcut List");
                outPutViewImage.setBackgroundResource(R.drawable.list_view);
                break;
            }
            case 2: {
                outPutViewText.setText("EveryThing");
                outPutViewImage.setBackgroundResource(R.drawable.button_action_red);
                break;
            }
           /* default: {
                outPutViewText.setText("Default");
                Toast.makeText(this, "default", Toast.LENGTH_SHORT).show();
                outPutViewImage.setBackgroundResource(R.mipmap.ic_launcher);
            }*/
        }

    }

    /*public static int getDarkerColor(int color) {
        float factor = 0.8f;
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
