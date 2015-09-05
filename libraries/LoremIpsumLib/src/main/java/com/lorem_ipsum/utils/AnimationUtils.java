package com.lorem_ipsum.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lorem_ipsum.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;


public class AnimationUtils {
    /**
     * Animation standard
     */

    public static void fadeIn(View view, int duration) {
        android.animation.ObjectAnimator.ofFloat(view, "alpha", 1).setDuration(duration).start();
    }

    public static void fadeOut(View view, int duration) {
        android.animation.ObjectAnimator.ofFloat(view, "alpha", 0).setDuration(duration).start();
    }

    public static void scaleX(View view, float start, float end, int duration) {
        android.animation.ObjectAnimator.ofFloat(view, "scaleX", start, end).setDuration(duration).start();
    }

    public static void scaleY(View view, float start, float end, int duration) {
        android.animation.ObjectAnimator.ofFloat(view, "scaleY", start, end).setDuration(duration).start();
    }

    public static void translationX(View view, float start, float end, int duration) {
        android.animation.ObjectAnimator.ofFloat(view, "translationX", start, end).setDuration(duration).start();
    }

    public static void translationY(View view, float start, float end, int duration) {
        android.animation.ObjectAnimator.ofFloat(view, "translationY", start, end).setDuration(duration).start();
    }

    public static void translationYInt(View view, float start, float end, int duration, int delay) {
        fadeOut(view, 0);

        android.animation.AnimatorSet translate = new android.animation.AnimatorSet();
        translate.playTogether(
                android.animation.ObjectAnimator.ofFloat(view, "translationY", start, end),
                android.animation.ObjectAnimator.ofFloat(view, "alpha", 1)
        );

        translate.setStartDelay(delay);
        translate.setDuration(duration);
        translate.start();
    }

    public static void translationYOut(View view, float start, float end, int duration, int delay) {
        android.animation.AnimatorSet translate = new android.animation.AnimatorSet();
        translate.playTogether(
                android.animation.ObjectAnimator.ofFloat(view, "translationY", start, end),
                android.animation.ObjectAnimator.ofFloat(view, "alpha", 0)
        );

        translate.setStartDelay(delay);
        translate.setDuration(duration);
        translate.start();
    }

    /**
     * Animation open activity
     */
    public static void slideInFromRight(Activity activity) {
        activity.overridePendingTransition(R.anim.acitivity_in_from_right_to_left,
                R.anim.acitivity_out_from_left);
    }

    /**
     * Animation close activity
     */
    public static void slideOutFromLeft(Activity activity) {
        activity.overridePendingTransition(R.anim.acitivity_in_from_left_to_right,
                R.anim.activity_out_from_right);
    }

    /**
     * Animation open activity facebook messenger
     */
    public static void slideInFromRightZoom(Activity activity) {
        activity.overridePendingTransition(R.anim.acitivity_in_from_right_to_left,
                R.anim.activity_zoom_out);
    }

    /**
     * Animation close activity facebook messenger
     */
    public static void slideOutFromLeftZoom(Activity activity) {
        activity.overridePendingTransition(R.anim.activity_zoom_in,
                R.anim.activity_out_from_right);
    }

    public static void shakeAnimationEdittext(Context context, final EditText editText) {
        // shake
        Animation shake = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.shake);

        Integer currentColor = editText.getCurrentTextColor();
        String currentText = editText.getText().toString();
        final boolean isEmpty = currentText.length() <= 0;
        if (isEmpty)
            currentColor = editText.getHintTextColors().getDefaultColor();

        // fade-in color
        Integer errorColor = Color.RED;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), errorColor, currentColor);
        colorAnimation.setDuration(1500);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (isEmpty)
                    editText.setHintTextColor((Integer) animator.getAnimatedValue());
                else
                    editText.setTextColor((Integer) animator.getAnimatedValue());
            }
        });

        // start animation
        editText.startAnimation(shake);
        colorAnimation.start();
    }

    public static void shakeAnimationTextView(Context context, final TextView textView) {
        // shake
        Animation shake = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.shake);

        // fade-in color
        Integer currentColor = textView.getCurrentTextColor();
        Integer errorColor = Color.RED;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), errorColor, currentColor);
        colorAnimation.setDuration(1500);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView.setTextColor((Integer) animator.getAnimatedValue());
            }
        });

        // start animation
        textView.startAnimation(shake);
        colorAnimation.start();
    }

    public static void fadeOutViewWithDuration(final View view, final int milliseconds, final boolean hideWhenDone, final int delay) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "alpha", 0.0f);
        animation.setDuration(milliseconds);
        animation.setStartDelay(delay);

        float currentAlpha = view.getAlpha();
        if (currentAlpha <= 0)
            return;

        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (hideWhenDone)
                    view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();
    }

    public static void fadeInViewWithDuration(final View view, final int milliseconds, final boolean setVisibilityBeforeStart, final int delay) {
        if (setVisibilityBeforeStart)
            view.setVisibility(View.VISIBLE);

//        float currentAlpha = view.getAlpha();
//        if (currentAlpha >= 1)
//            return;

        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        animation.setDuration(milliseconds);
        animation.setStartDelay(delay);
        animation.start();
    }

    public static void fadeInAndScale(View view, int milliseconds, float scaleStart, float scaleEnd, boolean setVisibilityBeforeStart) {
        if (setVisibilityBeforeStart)
            view.setVisibility(View.VISIBLE);

        float currentAlpha = view.getAlpha();
        if (currentAlpha >= 1)
            return;

        // fade in
        ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        fadeAnimation.setDuration(milliseconds);
        // scale
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(view, "scaleX", scaleStart, scaleEnd);
        scaleXAnimation.setDuration(milliseconds);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(view, "scaleY", scaleStart, scaleEnd);
        scaleYAnimation.setDuration(milliseconds);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeAnimation).with(scaleXAnimation).with(scaleYAnimation);
        animatorSet.start();
    }

    public static void fadeOutAndScale(final View view, int milliseconds, float scaleStart, float scaleEnd, final boolean hideWhenDone) {
        // fade out
        ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeAnimation.setDuration(milliseconds);

        float currentAlpha = view.getAlpha();
        if (currentAlpha <= 0)
            return;

        // scale
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(view, "scaleX", scaleStart, scaleEnd);
        scaleXAnimation.setDuration(milliseconds);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(view, "scaleY", scaleStart, scaleEnd);
        scaleYAnimation.setDuration(milliseconds);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeAnimation).with(scaleXAnimation).with(scaleYAnimation);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (hideWhenDone) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    public static void rotateWithDuration(View view, int milliseconds, int radiusFrom, int radiusTo) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotation", radiusFrom, radiusTo);
        animation.setDuration(milliseconds);
        animation.start();
    }

    //************************************************************
    // for dialog
    //************************************************************

    public static void fadeAndScaleAnimationForDialog(Context context, View v) {
        Animation scaleAndFadeAnimation =
                android.view.animation.AnimationUtils.loadAnimation(context, R.anim.scale_and_fade_fordialog);
        v.startAnimation(scaleAndFadeAnimation);
    }

    public static void AnimationWheelForDialog(Context context, View viewContainer) {
        if (viewContainer == null)
            return;

        Animation rotation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.loading_animate);
        ImageView imgLoading = (ImageView) viewContainer.findViewById(R.id.iv_loading);
        viewContainer.setVisibility(View.VISIBLE);
        imgLoading.startAnimation(rotation);
        rotation.setDuration(800);
    }

    public static void parallelScaleAnimationForDialog(View viewContainer) {
        if (viewContainer == null)
            return;
        viewContainer.setVisibility(View.VISIBLE);

        final View firstView = viewContainer.findViewById(R.id.loading_circle_view_first);
        final View secondView = viewContainer.findViewById(R.id.loading_circle_view_second);
        if (firstView == null || secondView == null)
            return;
        firstView.setVisibility(View.GONE);
        secondView.setVisibility(View.GONE);

        // define constant
        int duration = 1000;
        float minScale = 0.0f;
        float maxScale = 0.8f;

        // animation for firstView
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(firstView, "scaleX", minScale, maxScale);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(firstView, "scaleY", minScale, maxScale);
        scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownX.setDuration(duration);
        scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownY.setDuration(duration);
        // animation for secondView
        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(secondView, "scaleX", maxScale, minScale);
        ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(secondView, "scaleY", maxScale, minScale);
        scaleDownX2.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownX2.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownX2.setDuration(duration);
        scaleDownY2.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownY2.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownY2.setDuration(duration);

        // set of animation
        AnimatorSet scaleAnimation = new AnimatorSet();
        scaleAnimation.play(scaleDownX).with(scaleDownY).with(scaleDownX2).with(scaleDownY2);
        scaleAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                firstView.setVisibility(View.VISIBLE);
                secondView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleAnimation.start();
    }

    /**
     * Animation rebound standard
     */
    public static void translateReboundX(View view, float x, int duration, int delay, OvershootInterpolator overshoot) {
        view.animate().translationX(x)
                .setDuration(duration)
                .setInterpolator(overshoot)
                .setStartDelay(delay);
    }

    public static void translateReboundY(View view, float x, int duration, int delay, OvershootInterpolator overshoot) {
        view.animate().translationY(x)
                .setDuration(duration)
                .setInterpolator(overshoot)
                .setStartDelay(delay);
    }
}
