package com.lorem_ipsum.customviews;

import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by Torin on 23/10/14.
 */

public class CustomLinkMovementMethod extends LinkMovementMethod {

    private String LOG_TAG = this.getClass().getSimpleName();

    private static CustomLinkMovementMethod linkMovementMethod = new CustomLinkMovementMethod();
    private static CustomLinkMovementMethodListerner mListerner = null;

    public static android.text.method.MovementMethod getInstance(CustomLinkMovementMethodListerner listerner) {
        mListerner = listerner;
        return linkMovementMethod;
    }

    public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, android.view.MotionEvent event) {
        int action = event.getAction();
        if (action != MotionEvent.ACTION_UP)
            return super.onTouchEvent(widget, buffer, event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
        if (link.length != 0) {
            String url = link[0].getURL();
            Log.d(LOG_TAG, url);

            //If we have a listener, call it
            if (mListerner != null) {
                boolean isHandled = mListerner.onLinkClicked(url);
                if (isHandled)
                    return true;
            }
            //For internal debug, without a listener
            else {
                if (url.startsWith("https://") || url.startsWith("http://")) {
                    Toast.makeText(widget.getContext(), "Link was clicked", Toast.LENGTH_SHORT).show();
                } else if (url.startsWith("tel")) {
                    Toast.makeText(widget.getContext(), "Tel was clicked", Toast.LENGTH_SHORT).show();
                } else if (url.startsWith("mailto")) {
                    Toast.makeText(widget.getContext(), "Mail link was clicked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(widget.getContext(), "Something else was clicked", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    //-----------------------------------------------------------------------------

    public interface CustomLinkMovementMethodListerner {
        public boolean onLinkClicked(String url);
    }

}