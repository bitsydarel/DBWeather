package com.dbeginc.dbweather.ui.animation.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import com.dbeginc.dbweather.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Darel Bitsy on 17/01/17.
 */

public class SnowFallView extends View {
    private int snow_flake_count = 10;
    private final List<Drawable> drawables = new ArrayList<>();
    private int[][] coords;
    private final Drawable snow_flake;
    public static final int VIEW_ID = SnowFallView.generateViewId();


    public SnowFallView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setId(VIEW_ID);

        snow_flake = ContextCompat.getDrawable(context, R.drawable.snow_fall);
        snow_flake.setBounds(0, 0,
                snow_flake.getIntrinsicWidth(),
                snow_flake.getIntrinsicHeight());
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        Random random = new SecureRandom();
        Interpolator interpolator = new LinearInterpolator();

        snow_flake_count = Math.max(width, height) / 20;
        coords = new int[snow_flake_count][];
        drawables.clear();
        for (int i = 0; i < snow_flake_count; i++) {
            Animation animation = new TranslateAnimation(0, height / 10
                    - random.nextInt(height / 5), 0, height + 30);
            animation.setDuration(10 * height + random.nextInt(5 * height));
            animation.setRepeatCount(-1);
            animation.initialize(10, 10, 10, 10);
            animation.setInterpolator(interpolator);

            coords[i] = new int[] { random.nextInt(width - 30), -30 };

            drawables.add(new AnimateDrawable(snow_flake, animation));
            animation.setStartOffset(random.nextInt(20 * height));
            animation.startNow();
            int y;
            y = random.nextInt(2);
            if (y==0) { drawables.add(new AnimateDrawable(snow_flake, animation)); }
            else { drawables.add(new AnimateDrawable(snow_flake)); }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < snow_flake_count; i++) {
            Drawable drawable = drawables.get(i);
            canvas.save();
            canvas.translate(coords[i][0], coords[i][1]);
            drawable.draw(canvas);
            canvas.restore();
        }
        invalidate();
    }

}
