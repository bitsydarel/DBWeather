/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweather.utils.views.animations;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Darel Bitsy on 23/03/17.
 *
 * Base Pager Transformer
 */
public abstract class BasePageTransformer implements ViewPager.PageTransformer {

    /**
     * Called each {@link #transformPage(android.view.View, float)}.
     *
     * @param page     Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center position of the pager. 0 is front and
     *                 center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    protected abstract void onTransform(final View page, final float position);

    /**
     * Apply a property transformation to the given page. For most use cases, this method should not be overridden.
     * to perform typical transformations.
     *
     * @param page     Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center position of the pager. 0 is front and
     *                 center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    @Override
    public void transformPage(final View page, final float position) {
        onPreTransform(page, position);
        onTransform(page, position);
        onPostTransform(page, position);
    }

    /**
     * If the position offset of a fragment is less than negative one or greater than one, returning true will set the
     * fragment alpha to 0f. Otherwise fragment alpha is always defaulted to 1f.
     *
     * @return true
     */
    protected boolean hideOffscreenPages() {
        return true;
    }

    /**
     * Indicates if the default animations of the view pager should be used.
     *
     * @return
     */
    protected boolean isPagingEnabled() {
        return false;
    }

    /**
     * Called each {@link #transformPage(android.view.View, float)} before {{@link #onTransform(android.view.View, float)}.
     * <p>
     * The default implementation attempts to reset all view properties. This is useful when toggling transforms that do
     * not modify the same page properties. For instance changing from a transformation that applies rotation to a
     * transformation that fades can inadvertently leave a fragment stuck with a rotation or with some degree of applied
     * alpha.
     *
     * @param page     Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center position of the pager. 0 is front and
     *                 center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    protected void onPreTransform(final View page, final float position) {
        final float width = page.getWidth();

        page.setRotationX(0);
        page.setRotationY(0);
        page.setRotation(0);
        page.setScaleX(1);
        page.setScaleY(1);
        page.setPivotX(0);
        page.setPivotY(0);
        page.setTranslationY(0);
        page.setTranslationX(isPagingEnabled() ? 0f : -width * position);

        if (hideOffscreenPages()) {
            page.setAlpha(position <= -1f || position >= 1f ? 0f : 1f);
            page.setEnabled(false);
        } else {
            page.setEnabled(true);
            page.setAlpha(1f);
        }
    }

    /**
     * Called each {@link #transformPage(android.view.View, float)} after {@link #onTransform(android.view.View, float)}.
     *
     * @param page     Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center position of the pager. 0 is front and
     *                 center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    protected void onPostTransform(final View page, final float position) {}

    /**
     * Same as {@link Math#min(double, double)} without double casting, zero closest to infinity handling, or NaN support.
     *
     * @param val
     * @param min
     * @return
     */
    protected static float min(final float val, final float min) {
        return val < min ? min : val;
    }
}
