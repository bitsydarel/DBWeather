package com.dbeginc.dbweather.ui.customviews;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by darel on 30.05.17.
 * Custom ViewPager
 */

public class WeatherViewPager extends ViewPager {

    public WeatherViewPager(Context context) {
        super(context);
    }

    public WeatherViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = 0;
        final int childCount = getChildCount();

        //all measure childreen
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            final int h = child.getMeasuredHeight();
            //check the height of the child
            // if greater than the current height then resize the viewPager height and remeasure
            if (h > height) { height = h; }
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
