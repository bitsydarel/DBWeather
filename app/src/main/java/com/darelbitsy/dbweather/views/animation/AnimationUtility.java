package com.darelbitsy.dbweather.views.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

/**
 * Created by Darel Bitsy on 06/03/17.
 * Animation utility class
 * This class help to animate view
 */

public class AnimationUtility {
    private static final int FAST_ANIMATION_DURATION = 225;

    private AnimationUtility() {}

    public static void slideTextUpThanUpdate(final TextView textView,
                                             final String value) {
        final AnimatorSet compositor = new AnimatorSet();
        final ObjectAnimator moveUp = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, 200);
        final ObjectAnimator moveDown = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, 0);

        moveUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                textView.setText(value);
            }

            @Override
            public void onAnimationCancel(final Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
                //Not needed
            }
        });

        compositor.setDuration(FAST_ANIMATION_DURATION).playSequentially(moveUp, moveDown);
        compositor.setInterpolator(new AnticipateOvershootInterpolator());
        compositor.start();
    }

    public static void fadeTextOutUpdateThanFadeIn(final TextView textView,
                                                   final String value) {
        final AnimatorSet compositor = new AnimatorSet();
        final ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textView, View.ALPHA, 1f);
        final ObjectAnimator fadeOut = ObjectAnimator.ofFloat(textView, View.ALPHA, 0f);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                textView.setText(value);
            }

            @Override
            public void onAnimationCancel(final Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
                //Not needed
            }
        });

        compositor.setDuration(FAST_ANIMATION_DURATION).playSequentially(fadeOut, fadeIn);
        compositor.start();
    }

    public static void rotateTextThanUpdate(final TextView textView,
                                            final String value) {
        final boolean[] isTextSet = {false};

        final ObjectAnimator rotationUp = ObjectAnimator.ofFloat(textView,
                View.ROTATION_Y,
                0, 360)
                .setDuration(FAST_ANIMATION_DURATION);

        rotationUp.addUpdateListener(animation -> {
            if ((Float) animation.getAnimatedValue() > 100
                    && (Float) animation.getAnimatedValue() < 360
                    && !isTextSet[0]) {

                textView.setText(value);
                isTextSet[0] = true;
            }
        });

        rotationUp.start();

    }

    public static void slideTextRightThanLeft(final TextView textView,
                                              final String value) {
        final AnimatorSet animatorSet = new AnimatorSet();

        final ObjectAnimator slideRight = ObjectAnimator.ofFloat(textView,
                View.TRANSLATION_X,
                200f);

        final ObjectAnimator slideLeft = ObjectAnimator.ofFloat(textView,
                View.TRANSLATION_X,
                0f);

        slideRight.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                textView.setText(value);
            }

            @Override
            public void onAnimationStart(final Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationCancel(final Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
                //Not needed
            }
        });

        animatorSet.setTarget(textView);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(FAST_ANIMATION_DURATION);
        animatorSet.playSequentially(slideRight, slideLeft);
        animatorSet.start();
    }

    public static void slideTextLeftThanRight(final TextView textView,
                                              final String value) {
        final AnimatorSet animatorSet = new AnimatorSet();
        final ObjectAnimator slideLeft = ObjectAnimator.ofFloat(textView,
                View.TRANSLATION_X,
                -200f);

        final ObjectAnimator slideRight = ObjectAnimator.ofFloat(textView,
                View.TRANSLATION_X,
                0f);

        slideLeft.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                textView.setText(value);
            }

            @Override
            public void onAnimationCancel(final Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
                //Not needed
            }
        });

        animatorSet.setTarget(textView);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(FAST_ANIMATION_DURATION);
        animatorSet.playSequentially(slideLeft, slideRight);
        animatorSet.start();
    }

    public static AnimatorSet dayButtonAnimation(final View view) {
        final AnimatorSet animatorSet = new AnimatorSet();

        final float scaleX = view.getScaleX();

        final ObjectAnimator scaleUp = ObjectAnimator.ofFloat(view,
                View.SCALE_X,
                scaleX <= 1.0f ? 1.5f : 1.0f);

        if (scaleX <= 1.0f ) {
            scaleUp.setRepeatCount(1);
            scaleUp.setRepeatMode(ValueAnimator.REVERSE);
        }
        animatorSet.play(scaleUp);
        animatorSet.setDuration(FAST_ANIMATION_DURATION);
        return animatorSet;
    }
}
