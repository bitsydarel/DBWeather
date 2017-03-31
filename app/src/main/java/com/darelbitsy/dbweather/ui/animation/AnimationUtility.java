package com.darelbitsy.dbweather.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.darelbitsy.dbweather.adapters.listAdapter.NewsAdapter;

/**
 * Created by Darel Bitsy on 06/03/17.
 * Animation utility class
 * This class help to animate view
 */

public class AnimationUtility {
    private static final int FAST_ANIMATION_DURATION = 225;
    private static final int SLOW_ANIMATION_DURATION = 365;

    private AnimationUtility() {}

    public static void autoScrollRecyclerView(final RecyclerView recyclerView,
                                              final NewsAdapter adapter) {
        final int speedScroll = 4000;

        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;
            @Override
            public void run() {
                if(count < adapter.getItemCount()){
                    if(count == adapter.getItemCount() -1){
                        flag = false;

                    } else if(count == 0){ flag = true; }

                    if(flag) { count++; }
                    else { count--; }

                    recyclerView.smoothScrollToPosition(count);
                    recyclerView.postDelayed(this, speedScroll);
                }
            }
        };
        recyclerView
                .postDelayed(runnable, speedScroll);

    }


    public static void slideTextUpThanUpdate(TextView textView, String value) {
        AnimatorSet compositor = new AnimatorSet();
        ObjectAnimator moveUp = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, 200);
        ObjectAnimator moveDown = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, 0);

        moveUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setText(value);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //Not needed
            }
        });

        compositor.setDuration(FAST_ANIMATION_DURATION).playSequentially(moveUp, moveDown);
        compositor.setInterpolator(new AnticipateOvershootInterpolator());
        compositor.start();
    }

    public static void fadeTextOutUpdateThanFadeIn(TextView textView, String value) {
        AnimatorSet compositor = new AnimatorSet();
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textView, View.ALPHA, 1f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(textView, View.ALPHA, 0f);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setText(value);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //Not needed
            }
        });

        compositor.setDuration(FAST_ANIMATION_DURATION).playSequentially(fadeOut, fadeIn);
        compositor.start();
    }

    public static void rotateTextThanUpdate(TextView textView, String value) {
        final boolean[] isTextSet = {false};

        ObjectAnimator rotationUp = ObjectAnimator.ofFloat(textView,
                View.ROTATION_Y,
                0, 360).setDuration(FAST_ANIMATION_DURATION);

        rotationUp.addUpdateListener(animation -> {
            if ((Float) animation.getAnimatedValue() > 100
                    && (Float) animation.getAnimatedValue() < 200
                    && !isTextSet[0]) {

                textView.setText(value);
                isTextSet[0] = true;
            }
        });

        rotationUp.start();

    }

    public static void slideTextRightThanLeft(TextView textView, String value) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator slideRight = ObjectAnimator.ofFloat(textView,
                View.TRANSLATION_X,
                200f);

        ObjectAnimator slideLeft = ObjectAnimator.ofFloat(textView,
                View.TRANSLATION_X,
                0f);

        slideRight.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setText(value);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //Not needed
            }
        });

        animatorSet.setTarget(textView);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(FAST_ANIMATION_DURATION);
        animatorSet.playSequentially(slideRight, slideLeft);
        animatorSet.start();
    }

    public static void slideTextLeftThanRight(TextView textView, String value) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator slideLeft = ObjectAnimator.ofFloat(textView,
                View.TRANSLATION_X,
                -200f);

        ObjectAnimator slideRight = ObjectAnimator.ofFloat(textView,
                View.TRANSLATION_X,
                0f);

        slideLeft.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setText(value);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //Not needed
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //Not needed
            }
        });

        animatorSet.setTarget(textView);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(FAST_ANIMATION_DURATION);
        animatorSet.playSequentially(slideLeft, slideRight);
        animatorSet.start();
    }

    public static AnimatorSet dayButtonAnimation(View view) {
        AnimatorSet animatorSet = new AnimatorSet();

        final float scaleX = view.getScaleX();

        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(view,
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
