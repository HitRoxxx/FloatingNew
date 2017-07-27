/*
 *   Copyright 2014 Oguz Bilgener
 */
package com.theLoneWarrior.floating.circular_menu.animation;

import android.animation.Animator;
import android.graphics.Point;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.theLoneWarrior.floating.circular_menu.FloatingActionMenu;


/**
 * An abstract class that is a prototype for the actual animation handlers
 */
public abstract class MenuAnimationHandler {

    // There are only two distinct animations at the moment.
    protected enum ActionType {
        OPENING, CLOSING
    }

    protected FloatingActionMenu menu;

    public MenuAnimationHandler() {
    }

    public void setMenu(FloatingActionMenu menu) {
        this.menu = menu;
    }

    /**
     * Starts the opening animation
     * Should be overriden by children
     *
     * @param center
     */
    public void animateMenuOpening(Point center) {
        if (menu == null) {
            throw new NullPointerException("MenuAnimationHandler cannot animate without a valid FloatingActionMenu.");
        }

    }

    /**
     * Ends the opening animation
     * Should be overriden by children
     *
     * @param center
     */
    public void animateMenuClosing(Point center) {
        if (menu == null) {
            throw new NullPointerException("MenuAnimationHandler cannot animate without a valid FloatingActionMenu.");
        }
    }

    /**
     * Restores the specified sub action view to its final state, according to the current actionType
     * Should be called after an animation finishes.
     *
     * @param subActionItem
     * @param actionType
     */
    protected void restoreSubActionViewAfterAnimation(FloatingActionMenu.Item subActionItem, ActionType actionType) {
        ViewGroup.LayoutParams params = subActionItem.view.getLayoutParams();
        subActionItem.view.setTranslationX(0);
        subActionItem.view.setTranslationY(0);
        subActionItem.view.setRotation(0);
        subActionItem.view.setScaleX(1);
        subActionItem.view.setScaleY(1);
        subActionItem.view.setAlpha(1);
        if (actionType == ActionType.OPENING) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) params;
            if (menu.isSystemOverlay()) {
                WindowManager.LayoutParams overlayParams = (WindowManager.LayoutParams) menu.getOverlayContainer().getLayoutParams();
                lp.setMargins(subActionItem.x - overlayParams.x, subActionItem.y - overlayParams.y, 0, 0);
            } else {
                lp.setMargins(subActionItem.x, subActionItem.y, 0, 0);
            }
            subActionItem.view.setLayoutParams(lp);


        } else if (actionType == ActionType.CLOSING) {
            Point center = menu.getActionViewCenter();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) params;
            if (menu.isSystemOverlay()) {
                WindowManager.LayoutParams overlayParams = (WindowManager.LayoutParams) menu.getOverlayContainer().getLayoutParams();
                lp.setMargins(center.x - overlayParams.x - subActionItem.width / 2, center.y - overlayParams.y - subActionItem.height / 2, 0, 0);
            } else {
                lp.setMargins(center.x - subActionItem.width / 2, center.y - subActionItem.height / 2, 0, 0);
            }
            subActionItem.view.setLayoutParams(lp);
            menu.removeViewFromCurrentContainer(subActionItem.view);

            if (menu.isSystemOverlay()) {
                // When all the views are removed from the overlay container,
                // we also need to detach it
                if (menu.getOverlayContainer().getChildCount() == 0) {
                    menu.detachOverlayContainer();
                }
            }
           /* if (!notNow)
                startAppMenu();*/
        }
    }

/*
    private FloatingActionButton topCenterButton2;
    static boolean notNow;

    private FloatingActionMenu topCenterMenu2;

    private boolean serviceWillBeDismissed;

    private void startAppMenu() {

        final SystemOverlayMenuService soms = FloatingActionMenu.soms;
        serviceWillBeDismissed = false;
        notNow = true;
        int redActionButtonSize = soms.getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int redActionButtonMargin = soms.getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = soms.getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = soms.getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = soms.getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = soms.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = soms.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        final ImageView fabIconStar = new ImageView(soms);
        fabIconStar.setImageDrawable(soms.getResources().getDrawable(R.mipmap.ic_launcher));

        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
       *//* fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);*//*

        WindowManager.LayoutParams params2 = FloatingActionButton.Builder.getDefaultSystemWindowParams(soms);
        params2.width = redActionButtonSize;
        params2.height = redActionButtonSize;

        topCenterButton2 = new FloatingActionButton.Builder(soms)
                .setSystemOverlay(true)
                .setContentView(fabIconStar, fabIconStarParams)
                .setBackgroundDrawable(R.drawable.button_action_red_selector)
                .setPosition(FloatingActionButton.POSITION_CENTER)
                .setLayoutParams(params2)
                .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder tCSubBuilder = new SubActionButton.Builder(soms);
        tCSubBuilder.setBackgroundDrawable(soms.getResources().getDrawable(R.drawable.button_action_blue_selector));

        SubActionButton.Builder tCRedBuilder = new SubActionButton.Builder(soms);
        tCRedBuilder.setBackgroundDrawable(soms.getResources().getDrawable(R.drawable.button_action_red_selector));

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);

        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        tCSubBuilder.setLayoutParams(blueParams);
        tCRedBuilder.setLayoutParams(blueParams);

        ImageView tcIcon1 = new ImageView(soms);
        ImageView tcIcon2 = new ImageView(soms);
        ImageView tcIcon3 = new ImageView(soms);
        ImageView tcIcon4 = new ImageView(soms);
        ImageView tcIcon5 = new ImageView(soms);
        ImageView tcIcon6 = new ImageView(soms);

        tcIcon1.setImageDrawable(soms.getResources().getDrawable(R.drawable.ic_action_camera));
        tcIcon2.setImageDrawable(soms.getResources().getDrawable(R.drawable.ic_action_picture));
        tcIcon3.setImageDrawable(soms.getResources().getDrawable(R.drawable.ic_action_video));
        tcIcon4.setImageDrawable(soms.getResources().getDrawable(R.drawable.ic_action_location_found));
        tcIcon5.setImageDrawable(soms.getResources().getDrawable(R.drawable.ic_action_headphones));
        tcIcon6.setImageDrawable(soms.getResources().getDrawable(R.drawable.ic_action_cancel));

        SubActionButton tcSub1 = tCSubBuilder.setContentView(tcIcon1, blueContentParams).build();
        SubActionButton tcSub2 = tCSubBuilder.setContentView(tcIcon2, blueContentParams).build();
        SubActionButton tcSub3 = tCSubBuilder.setContentView(tcIcon3, blueContentParams).build();
        SubActionButton tcSub4 = tCSubBuilder.setContentView(tcIcon4, blueContentParams).build();
        SubActionButton tcSub5 = tCSubBuilder.setContentView(tcIcon5, blueContentParams).build();
        SubActionButton tcSub6 = tCRedBuilder.setContentView(tcIcon6, blueContentParams).build();


        // Build another menu with custom options
        topCenterMenu2 = new FloatingActionMenu.Builder(soms, true)
                .addSubActionView(tcSub1, tcSub1.getLayoutParams().width, tcSub1.getLayoutParams().height)
                .addSubActionView(tcSub2, tcSub2.getLayoutParams().width, tcSub2.getLayoutParams().height)
                .addSubActionView(tcSub3, tcSub3.getLayoutParams().width, tcSub3.getLayoutParams().height)
                .addSubActionView(tcSub4, tcSub4.getLayoutParams().width, tcSub4.getLayoutParams().height)
                .addSubActionView(tcSub5, tcSub5.getLayoutParams().width, tcSub5.getLayoutParams().height)
                .addSubActionView(tcSub6, tcSub6.getLayoutParams().width, tcSub6.getLayoutParams().height)
                .setRadius(redActionMenuRadius)
                .setStartAngle(0)
                .setEndAngle(180)
                .attachTo(topCenterButton2)
                .build();

        topCenterMenu2.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                topCenterMenu2.open(true);
            }
        }, 200);

        tcSub6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceWillBeDismissed = true; // the order is important
                topCenterMenu2.close(true);
                Intent intent = new Intent(soms, MyIntentService.class);
                intent.setFlags(5);
                soms.startService(intent);
                soms.stopSelf();
            }
        });
    }*/

    /**
     * A special animation listener that is intended to listen the last of the sequential animations.
     * Changes the animating property of children.
     */
    public class LastAnimationListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            setAnimating(true);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setAnimating(false);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            setAnimating(false);
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            setAnimating(true);
        }
    }

    public abstract boolean isAnimating();

    protected abstract void setAnimating(boolean animating);
}
