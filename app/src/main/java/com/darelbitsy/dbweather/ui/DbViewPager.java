package com.darelbitsy.dbweather.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Darel Bitsy on 20/03/17.
 */

public class DbViewPager extends ViewPager implements View.OnTouchListener {
    private GestureDetector mDetector;
    private Context mContext;

    public DbViewPager(Context context) {
        super(context);
        mContext = context;
    }

    public DbViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void init() {
        mDetector = new GestureDetector(mContext, new GestureFinder() {
            /**
             * Override this method. The Direction enum will tell you how the user swiped.
             *
             * @param direction the user swiped.
             */
            @Override
            public boolean onSwipe(Direction direction) {
                if (direction == Direction.up) {

                    mContext.startActivity(new Intent(mContext,
                            ResumeWeatherFragment.class));
                }
                return true;
            }
        });
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }
}
