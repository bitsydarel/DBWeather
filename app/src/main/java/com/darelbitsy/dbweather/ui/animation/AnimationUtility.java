package com.darelbitsy.dbweather.ui.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.Button;

/**
 * Created by Darel Bitsy on 06/03/17.
 */

public class AnimationUtility {

    public static AnimatorSet dayButtonAnimation(View view) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(view,
                View.SCALE_X,
                1.5f);
        scaleUp.setRepeatCount(1);
        scaleUp.setRepeatMode(ValueAnimator.REVERSE);

        animatorSet.play(scaleUp);
        animatorSet.setDuration(320);

        return animatorSet;
    }
}
