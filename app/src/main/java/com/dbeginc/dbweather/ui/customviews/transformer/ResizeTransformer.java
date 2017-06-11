package com.dbeginc.dbweather.ui.customviews.transformer;

/**
 * Abstract class created to be implemented by different classes are going to change the size of a
 * view. The most basic one is going to scale the view and the most complex used with VideoView is
 * going to change the size of the view.
 * <p/>
 * The view used in this class has to be contained by a RelativeLayout.
 * <p/>
 * This class also provide information about the size of the view and the position because
 * different Transformer implementations could change the size of the view but not the position,
 * like ScaleTransformer does.
 *
 * @author Pedro Vicente Gómez Sánchez
 */

import android.view.View;
import android.widget.RelativeLayout;

public class ResizeTransformer extends Transformer {

    private final RelativeLayout.LayoutParams layoutParams;

    ResizeTransformer(View view, View parent) {
        super(view, parent);
        layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
    }

    /**
     * Changes view scale using view's LayoutParam.
     *
     * @param verticalDragOffset used to calculate the new size.
     */
    @Override public void updateScale(float verticalDragOffset) {
        layoutParams.width = (int) (getOriginalWidth() * (1 - verticalDragOffset / getXScaleFactor()));
        layoutParams.height = (int) (getOriginalHeight() * (1 - verticalDragOffset / getYScaleFactor()));

        getView().setLayoutParams(layoutParams);
    }


    /**
     * Changes X view position using layout() method.
     *
     * @param verticalDragOffset used to calculate the new X position.
     */
    @Override
    public void updatePosition(float verticalDragOffset) {
        int right = getViewRightPosition(verticalDragOffset);
        int left = right - layoutParams.width;
        int top = getView().getTop();
        int bottom = top + layoutParams.height;

        getView().layout(left, top, right, bottom);
    }


    /**
     * @return true if the right position of the view plus the right margin is equals to the parent
     * width.
     */
    @Override public boolean isViewAtRight() {
        return getView().getRight() + getMarginRight() == getParentView().getWidth();
    }

    /**
     * @return true if the bottom position of the view plus the margin right is equals to
     * the parent view height.
     */
    @Override public boolean isViewAtBottom() {
        return getView().getBottom() + getMarginBottom() == getParentView().getHeight();
    }

    /**
     * @return true if the left position of the view is to the right of the seventy five percent of
     * the parent view width.
     */
    @Override public boolean isNextToRightBound() {
        return (getView().getLeft() - getMarginRight()) > getParentView().getWidth() * 0.75;
    }

    /**
     * @return true if the left position of the view is to the left of the twenty five percent of
     * the parent width.
     */
    @Override public boolean isNextToLeftBound() {
        return (getView().getLeft() - getMarginRight()) < getParentView().getWidth() * 0.05;
    }

    /**
     * Uses the Y scale factor to calculate the min possible height.
     */
    @Override public int getMinHeightPlusMargin() {
        return (int) (getOriginalHeight() * (1 - 1 / getYScaleFactor()) + getMarginBottom());
    }

    /**
     * Uses the X scale factor to calculate the min possible width.
     */
    @Override public int getMinWidthPlusMarginRight() {
        return (int) (getOriginalWidth() * (1 - 1 / getXScaleFactor()) + getMarginRight());
    }

    /**
     * Calculate the current view right position for a given verticalDragOffset.
     *
     * @param verticalDragOffset used to calculate the new right position.
     */
    private int getViewRightPosition(float verticalDragOffset) {
        return (int) ((getOriginalWidth()) - getMarginRight() * verticalDragOffset);
    }
}
