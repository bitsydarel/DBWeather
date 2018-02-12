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

package com.dbeginc.dbweather.utils.views.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Bitsy Darel on 01.05.17.
 * View Pager App Intro
 */

public class NotSwipingViewPager extends ViewPager {

    private boolean pagingStatus;

    public NotSwipingViewPager(@NonNull final Context context) {
        super(context);
    }

    public NotSwipingViewPager(@NonNull final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        return pagingStatus && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev) { return pagingStatus && super.onTouchEvent(ev); }

    public boolean isPagingEnabled() {
        return pagingStatus;
    }

    public void setPagingEnabled(final boolean pagingEnabled) {
        pagingStatus = pagingEnabled;
    }


}
