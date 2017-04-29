package com.darelbitsy.dbweather.ui.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.darelbitsy.dbweather.R;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.PREFS_NAME;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.RECYCLER_BOTTOM_LIMIT;

/**
 * Created by Darel Bitsy on 31/03/17.
 * Custom ViewGroup to manage hourlyRecycler view dragging
 */

public class CustomConstraintLayout extends ConstraintLayout {
    private ViewDragHelper mDragHelper;
    private SharedPreferences mPreferences;

    public CustomConstraintLayout(final Context context) {
        super(context);
        mPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        addChangeListener();
    }

    public CustomConstraintLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        addChangeListener();
    }

    public CustomConstraintLayout(final Context context, final AttributeSet attrs,
                                  final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        addChangeListener();
    }

    private void addChangeListener() {
        this.addOnLayoutChangeListener((v, left, top, right, bottom,
                                        oldLeft, oldTop, oldRight, oldBottom) -> {
            if (isMoving()) {
                v.setTop(oldTop);
                v.setBottom(oldBottom);
                v.setLeft(oldLeft);
                v.setRight(oldRight);
            }
        });

        Log.i("drag", "Was in constructor");
    }

    public boolean isMoving() {
        return mDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING ||
                mDragHelper.getViewDragState() == ViewDragHelper.STATE_SETTLING;
    }


    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        Log.i("drag", "Was in interceptTouch");
        return mDragHelper.shouldInterceptTouchEvent(ev)
                || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        Log.i("drag", "Was in ontouchevent");
        mDragHelper.processTouchEvent(ev);
        return super.onTouchEvent(ev) || true;
    }


    /**
     * Called by a parent to request that a child update its values for mScrollX
     * and mScrollY if necessary. This will typically be done if the child is
     * animating a scroll using a Scroller
     * object.
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * Finalize inflating a view from XML.  This is called as the last phase
     * of inflation, after all child views have been added.
     * <p>
     * <p>Even if the subclass overrides onFinishInflate, they should always be
     * sure to call the super method, so that we get called.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragHelper = ViewDragHelper.create(this,
                new DragHelperCallback());
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(final View child, final int pointerId) {
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
        public int getViewVerticalDragRange(final View child) {
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
        public int clampViewPositionVertical(final View child,
                                             final int top,
                                             final int dy) {
            Log.i("drag", "Was in clamp position");
            final int defaultBottom = Math.round(getMeasuredHeight() * 0.7f);
            return Math.min(Math.max(top, getPaddingTop()),
                    (int) mPreferences.getFloat(RECYCLER_BOTTOM_LIMIT, defaultBottom));
        }

        /**
         * Called when the captured view's position changes as the result of a drag or settle.
         *
         * @param changedView View whose position changed
         * @param left        New X coordinate of the left edge of the view
         * @param top         New Y coordinate of the top edge of the view
         * @param dx          Change in X position from the last call
         * @param dy          Change in Y position from the last call
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            changedView.invalidate();
        }
    }
}
