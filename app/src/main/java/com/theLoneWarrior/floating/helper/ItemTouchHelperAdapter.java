package com.theLoneWarrior.floating.helper;

/**
 * Created by HitRo on 02-07-2017.
 */

public interface ItemTouchHelperAdapter {


    boolean onItemMove(int fromPosition, int toPosition);


    void onItemDismiss(int position);
}