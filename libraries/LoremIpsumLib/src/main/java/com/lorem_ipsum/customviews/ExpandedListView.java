package com.lorem_ipsum.customviews;

/**
 * Created by Torin on 30/11/14.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class ExpandedListView extends ListView {

    private static final String LOG_TAG = "ExpandedListView";
    private static final boolean enableDeltaCheck = true;

    private int oldCount = -1;

    public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void invalidateCount() {
        oldCount = -1;
        Log.d(LOG_TAG, "oldCount is invalidated");
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //Check for change in count
        int newCount = getCount();
        Log.d(LOG_TAG, "oldCount: " + oldCount + ", newCount: " + newCount);
        if (enableDeltaCheck && newCount == oldCount) {
            Log.d(LOG_TAG, "No change in count");
            super.onDraw(canvas);
            return;
        }

        boolean firstTime = oldCount < 0;
        oldCount = newCount;
        android.view.ViewGroup.LayoutParams params = getLayoutParams();

        int totalHeight = 0;
        for (int i = 0; i < newCount; i++) {
            this.measure(0, 0);
            totalHeight += getMeasuredHeight();     //this appears to be expanding the listView on the fly
        }

        params.height = totalHeight;
        setLayoutParams(params);

        if (firstTime)
            Log.d(LOG_TAG, "Total height: " + totalHeight);

        super.onDraw(canvas);
    }

}