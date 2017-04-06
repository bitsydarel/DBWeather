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

public class DbViewPager extends ViewPager {

    public DbViewPager(Context context) {
        super(context);
    }

    public DbViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
