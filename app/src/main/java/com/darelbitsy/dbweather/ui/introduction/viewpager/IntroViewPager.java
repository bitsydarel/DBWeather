package com.darelbitsy.dbweather.ui.introduction.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Bitsy Darel on 01.05.17.
 * View Pager App Intro
 */

public class IntroViewPager extends ViewPager {

    private boolean isPagingEnabled;

    public IntroViewPager(@NonNull final Context context) {
        super(context);
    }

    public IntroViewPager(@NonNull final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        return isPagingEnabled && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev) { return isPagingEnabled && super.onTouchEvent(ev); }

    public boolean isPagingEnabled() {
        return isPagingEnabled;
    }

    public void setPagingEnabled(final boolean pagingEnabled) {
        isPagingEnabled = pagingEnabled;
    }


}
