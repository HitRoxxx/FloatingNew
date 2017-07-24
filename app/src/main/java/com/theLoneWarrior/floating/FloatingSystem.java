package com.theLoneWarrior.floating;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.theLoneWarrior.floating.circular_menu.FloatingActionButton;
import com.theLoneWarrior.floating.circular_menu.FloatingActionMenu;
import com.theLoneWarrior.floating.circular_menu.SubActionButton;
import com.theLoneWarrior.floating.services.MyIntentService;


public class FloatingSystem extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_system);

/*
        if (getIntent().getBooleanExtra("LAND",false)) {
            FloatingActionMenu.flag = true;
            Toast.makeText(this, "dd", Toast.LENGTH_SHORT).show();
        }*/
        // Set up the white button on the lower right corner
        // more or less with default parameter
       /* final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat_light));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video_light));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_place_light));

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon4).build())
                .attachTo(rightLowerButton)
                .build();

        // Listen menu open and close events to animate the button content view
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
        });*/

        // Set up the large red button on the center right side
        // With custom button and content sizes and margins
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        final ImageView fabIconStar = new ImageView(this);
        // fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_important));

        fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));

        FloatingActionButton.LayoutParams starParams = new FloatingActionButton.LayoutParams(redActionButtonSize, redActionButtonSize);
        starParams.setMargins(redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin);
        fabIconStar.setLayoutParams(starParams);

        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
        fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);

        final FloatingActionButton leftCenterButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconStar, fabIconStarParams)
                .setBackgroundDrawable(R.drawable.button_action_red_selector)
                .setPosition(FloatingActionButton.POSITION_CENTER)
                .setLayoutParams(starParams)
                .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(this);
        lCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);
        lCSubBuilder.setLayoutParams(blueContentParams);
        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        lCSubBuilder.setLayoutParams(blueParams);

        ImageView lcIcon1 = new ImageView(this);
        ImageView lcIcon2 = new ImageView(this);
        ImageView lcIcon3 = new ImageView(this);
        ImageView lcIcon4 = new ImageView(this);
        ImageView lcIcon5 = new ImageView(this);

        ImageView lcIcon6 = new ImageView(this);
        ImageView lcIcon7 = new ImageView(this);
        ImageView lcIcon8 = new ImageView(this);
        ImageView lcIcon9 = new ImageView(this);
        ImageView lcIcon10 = new ImageView(this);
        ImageView lcIcon11 = new ImageView(this);
        ImageView lcIcon12 = new ImageView(this);
        ImageView lcIcon13 = new ImageView(this);
        ImageView lcIcon14 = new ImageView(this);
        ImageView lcIcon15 = new ImageView(this);

        lcIcon6.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat_light));
        lcIcon7.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));
        lcIcon8.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video_light));
        lcIcon9.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_place_light));
        lcIcon10.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat_light));
        lcIcon11.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));
        lcIcon12.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video_light));
        lcIcon13.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_place_light));
        lcIcon14.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat_light));
        lcIcon15.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));
        lcIcon15.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video_light));


        lcIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera));
        lcIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_picture));
        lcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video));
        lcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location_found));
        lcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_headphones));

        // Build another menu with custom options
        leftCenterMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(lCSubBuilder.setContentView(lcIcon1, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon2, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon3, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon4, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon5, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon6, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon7, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon8, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon9, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon10, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon11, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon12, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon13, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon14, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon15, blueContentParams).build())
                .setRadius(redActionMenuRadius)
                .setStartAngle(180)
                .setEndAngle(-180)
                .attachTo(leftCenterButton)
                .build();

        leftCenterMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
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
            }
        });
        ImageView iv = (ImageView) findViewById(R.id.hide);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCloseService();
                finish();
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                leftCenterMenu.open(true);
            }
        }, 400);
    }

    FloatingActionMenu leftCenterMenu;

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        leftCenterMenu.getActionViewCenter();
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startCloseService();
    }

    void startCloseService() {
        Intent intent = new Intent(this, MyIntentService.class);
        intent.setFlags(5);
        startService(intent);
    }

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Intent intent = new Intent(this, FloatingSystem.class);
        intent.setFlags(6);
        if (newConfig.orientation == 2) {
            intent.putExtra("LAND",true);
            Toast.makeText(this, "jdgjsgdjhhs", Toast.LENGTH_SHORT).show();
        }

        startService(intent);
        finish();
    }*/
}
