package com.lorem_ipsum.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Torin on 11/10/14.
 */
public class ViewUtils {

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setRoundedCorners(View view, float radius, int backgroundColor) {
        //Calculate the radius
        if (radius <= 0) {
            float width = view.getWidth();
            float height = view.getHeight();
            radius = Math.min(width, height) / 2.0f;
            if (radius <= 0)
                return;
        }

        //Construct a rounded corner shape with that color
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(radius);
        shape.setColor(backgroundColor);

        if (Build.VERSION.SDK_INT > 16) {
            view.setBackground(shape);
        } else {
            view.setBackgroundDrawable(shape);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setRoundedCorners(View view, float radius, int borderWidth, int backgroundColor, int borderColor) {
        //Calculate the radius
        if (radius <= 0) {
            float width = view.getWidth();
            float height = view.getHeight();
            radius = Math.min(width, height) / 2.0f;
            if (radius <= 0)
                return;
        }

        //Construct a rounded corner shape with that color
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(radius);
        shape.setColor(backgroundColor);
        shape.setStroke(borderWidth, borderColor);

        if (Build.VERSION.SDK_INT > 16) {
            view.setBackground(shape);
        } else {
            view.setBackgroundDrawable(shape);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setRoundedCornersWithRadius(View view, float radius, int backgroundColor) {

        //Try to obtain the current background color of the view
        //Note: only works with views with simple color background
        if (backgroundColor <= 0) {
            Drawable currentBackground = view.getBackground();
            if (currentBackground instanceof ColorDrawable) {
                backgroundColor = ((ColorDrawable) currentBackground).getColor();
            }
        }

        //Construct a rounded corner shape with that color
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(radius);
        shape.setColor(backgroundColor);

        if (Build.VERSION.SDK_INT > 16) {
            view.setBackground(shape);
        } else {
            view.setBackgroundDrawable(shape);
        }
    }

    //-----------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------

    public static int convertDPtoPixel(Activity context, float dimensionInDP) {
        int dimensionInPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInDP, context.getResources().getDisplayMetrics());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return dimensionInPixel;
    }

    public static float convertPixelToDP(Activity context, float dimensionInPixel) {
        float dp = 1000;
        float pixel = convertDPtoPixel(context, dp);
        float ratio = dp / pixel;
        return dimensionInPixel * ratio;
    }

    //-----------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------

    public static void setViewWidthInDP(Activity context, View view, float dimensionInDP) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = convertDPtoPixel(context, dimensionInDP);
        view.setLayoutParams(layoutParams);
    }

    public static void setViewHeightInDP(Activity context, View view, float dimensionInDP) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = convertDPtoPixel(context, dimensionInDP);
        view.setLayoutParams(layoutParams);
    }

    public static float getViewWidthInDP(Activity context, View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        return convertPixelToDP(context, layoutParams.width);
    }

    public static float getViewHeightInDP(Activity context, View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        return convertPixelToDP(context, layoutParams.height);
    }

    //-----------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------

    /*
     * Expand the listView to full size of its content
     * Doesn't work well with dynamic height list rows, use ExpandedListView class instead
     */
    public static void setListViewHeightBasedOnContent(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

        Log.d("ViewUtils", "setListViewHeightBasedOnContent: " + listAdapter.getCount() + " items, height: " + totalHeight);
    }
}
