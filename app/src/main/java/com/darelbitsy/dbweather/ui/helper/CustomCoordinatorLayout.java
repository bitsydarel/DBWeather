package com.darelbitsy.dbweather.ui.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.darelbitsy.dbweather.R;

import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.PREFS_NAME;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.RECYCLER_BOTTOM_LIMIT;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.RECYCLER_TOP_LIMIT;

/**
 * Created by Darel Bitsy on 31/03/17.
 */

public class CustomCoordinatorLayout extends ConstraintLayout {
    ViewDragHelper mDragHelper;
    SharedPreferences mPreferences;

    public CustomCoordinatorLayout(Context context) {
        super(context);
        mDragHelper = ViewDragHelper.create(this,
                1.0f,
                new DragHelperCallback());

        mPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Log.i("drag", "Was in constructor");
    }

    public CustomCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this,
                1.0f,
                new DragHelperCallback());
        mPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Log.i("drag", "Was in constructor");
    }

    public CustomCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this,
                1.0f,
                new DragHelperCallback());
        mPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Log.i("drag", "Was in constructor");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("drag", "Was in interceptTouch");
        return mDragHelper.shouldInterceptTouchEvent(ev)
                || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i("drag", "Was in ontouchevent");
        mDragHelper.processTouchEvent(ev);
        return super.onTouchEvent(ev) || true;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.i("drag", "Was in try capture view");
            return child.getId() == R.id.hourlyRecyclerView;
        }

        /**
         * Return the magnitude of a draggable child view's vertical range of motion in pixels.
         * This method should return 0 for views that cannot move vertically.
         *
         * @param child Child view to check
         * @return range of vertical motion in pixels
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight();
        }

        /**
         * Restrict the motion of the dragged child view along the vertical axis.
         * The default implementation does not allow vertical motion; the extending
         * class must override this method and provide the desired clamping.
         *
         * @param child Child view being dragged
         * @param top   Attempted motion along the Y axis
         * @param dy    Proposed change in position for top
         * @return The new clamped position for top
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.i("drag", "Was in clamp position");
            return Math.min(Math.max(top, (int) mPreferences.getFloat(RECYCLER_TOP_LIMIT, 380)),
                    (int) mPreferences.getFloat(RECYCLER_BOTTOM_LIMIT, 1344));
        }
    }
}
