package com.darelbitsy.dbweather.ui.animation;

import android.view.View;

/**
 * Created by Darel Bitsy on 23/03/17.
 */

public class CubeOutTransformer extends BasePageTransformer {
    @Override
    protected void onTransform(View view, float position) {
        view.setPivotX(position < 0f ? view.getWidth() : 0f);
        view.setPivotY(view.getHeight() * 0.5f);
        view.setRotationY(90f * position);
    }

    @Override
    public boolean isPagingEnabled() { return true; }
}
