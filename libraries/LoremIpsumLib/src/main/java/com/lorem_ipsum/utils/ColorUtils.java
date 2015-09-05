package com.lorem_ipsum.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.TextView;

public class ColorUtils {

    private static final String LOG_TAG = "ColorUtils";

    public static int appPrimaryColor() {
        return Color.argb(255, 147, 193, 31);
    }

    public static int errorColor() {
        return Color.argb(255, 255, 72, 60);
    }

    public static int navbarButtonColor() {
        return appPrimaryColor();
    }

    public static int lightGreyTextColor() {
        return Color.argb(255, 178, 178, 178);
    }

    public static int darkGreyTextColor() {
        return Color.argb(255, 102, 102, 102);
    }

    public static int greyBackgroundColor() {
        return Color.argb(255, 234, 234, 234);
    }

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static void setStatefulTextColor(TextView txtView, int enabled, int disabled, int unchecked, int pressed) {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };
        int[] colors = new int[] {
                enabled, disabled, unchecked, pressed
        };

        ColorStateList myColorStateList = new ColorStateList(states, colors);
        txtView.setTextColor(myColorStateList);
    }

    public static void setSimpleStatefulTextColor(TextView txtView, int normal, int pressed) {
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_pressed},  // not pressed
                new int[] { android.R.attr.state_pressed}   // pressed
        };
        int[] colors = new int[] {
                normal, pressed
        };

        ColorStateList myColorStateList = new ColorStateList(states, colors);
        txtView.setTextColor(myColorStateList);
    }
}
