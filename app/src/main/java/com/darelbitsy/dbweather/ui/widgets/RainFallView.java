package com.darelbitsy.dbweather.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import com.darelbitsy.dbweather.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Darel Bitsy on 20/01/17.
 * Rain Falling Animation
 */

public class RainFallView extends View {
    private int mRainFlakeCount = 10;
    private final List<Drawable> drawables = new ArrayList<>();
    private int[][] coords;
    private final Drawable mRainDrop;
    public static final int VIEW_ID = RainFallView.generateViewId();

    public RainFallView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setId(VIEW_ID);

        mRainDrop = ContextCompat.getDrawable(context, R.drawable.raindrop);
        mRainDrop.setBounds(0, 0,
                mRainDrop.getIntrinsicWidth(),
                mRainDrop.getIntrinsicHeight());
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        Random random = new SecureRandom();
        Interpolator interpolator = new LinearInterpolator();

        mRainFlakeCount = Math.max(width, height) / 20;
        coords = new int[mRainFlakeCount][];
        drawables.clear();
        for (int i = 0; i < mRainFlakeCount; i++) {
            Animation animation = new TranslateAnimation(0, height / 10
                    - random.nextInt(height / 5), 0, height + 30);
            animation.setDuration(10 * height + random.nextInt(5 * height));
            animation.setRepeatCount(-1);
            animation.initialize(10, 10, 10, 10);
            animation.setInterpolator(interpolator);

            coords[i] = new int[] { random.nextInt(width - 30), -30 };

            drawables.add(new AnimateDrawable(mRainDrop, animation));
            animation.setStartOffset(random.nextInt(20 * height));
            animation.startNow();
            int y;
            y = random.nextInt(2);
            if (y==0) { drawables.add(new AnimateDrawable(mRainDrop, animation)); }
            else { drawables.add(new AnimateDrawable(mRainDrop)); }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mRainFlakeCount; i++) {
            Drawable drawable = drawables.get(i);
            canvas.save();
            canvas.translate(coords[i][0], coords[i][1]);
            drawable.draw(canvas);
            canvas.restore();
        }
        invalidate();
    }
}
