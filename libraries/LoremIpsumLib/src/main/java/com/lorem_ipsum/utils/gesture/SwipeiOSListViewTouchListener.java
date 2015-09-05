package com.lorem_ipsum.utils.gesture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by originally.us on 7/31/14.
 * <p/>
 * Created base on https://github.com/dharanikumar/IOS_7-SwipeGesture-Android
 */
public class SwipeiOSListViewTouchListener implements View.OnTouchListener {

    // Cached ViewConfiguration and system-wide constant values
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    private ListView mListView;

    private int mWidthBehindView = 1; // maximun = [width of item listview] * 3/4
    private int mSwipeViewId;
    private int mBehindViewId;

    // Transient properties
    private float mDownX;
    private boolean mSwiping;
    private VelocityTracker mVelocityTracker;
    private int mTempPosition;
    private ViewGroup mSwipeView;
    private View mBehindView;
    private ViewGroup mOldSwipeView;
    public boolean mOptionsDisplay = false;
    static TouchCallbacks mTouchCallbacks;

    /*
     * Constructor
     */
    public SwipeiOSListViewTouchListener(ListView listView, int swipeViewId, int behindViewId, int widthOfBehindView, TouchCallbacks Callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mListView = listView;
        mTouchCallbacks = Callbacks;

        mWidthBehindView = widthOfBehindView;
        mSwipeViewId = swipeViewId;
        mBehindViewId = behindViewId;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mOptionsDisplay)
                    mTouchCallbacks.OnItemListViewClick(parent, view, mTempPosition, id);
            }
        });
    }

    /*
     * Handle touch event
     */
    @Override
    public boolean onTouch(final View view, MotionEvent event) {
        int widthListView = mListView.getWidth();
        if (mWidthBehindView >= widthListView)
            mWidthBehindView = (widthListView / 4) * 3;
        int tempwidth = mWidthBehindView;

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {
                Rect rect = new Rect();
                int childCount = mListView.getChildCount();
                int[] listViewCoords = new int[2];
                mListView.getLocationOnScreen(listViewCoords);
                int x = (int) event.getRawX() - listViewCoords[0];
                int y = (int) event.getRawY() - listViewCoords[1];
                ViewGroup child;
                for (int i = 0; i < childCount; i++) {
                    child = (ViewGroup) mListView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        mSwipeView = (ViewGroup) child.findViewById(mSwipeViewId);
                        mBehindView = child.findViewById(mBehindViewId);

                        if (mBehindView != null) {
                            mBehindView.setVisibility(View.VISIBLE);
                        }

                        if (mOldSwipeView != null) {
                            resetListItemState(mOldSwipeView);
                            mOldSwipeView = null;
                            return false;
                        }
                        break;
                    }
                }

                if (mSwipeView != null) {
                    mDownX = event.getRawX();
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(event);
                } else {
                    mSwipeView = null;
                }

                mTempPosition = mListView.pointToPosition((int) event.getX(), (int) event.getY());
                view.onTouchEvent(event);
                return true;
            }

            case MotionEvent.ACTION_UP: {
                if (mVelocityTracker == null)
                    break;

                float deltaX = event.getRawX() - mDownX;
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000); // 1000 by defaut but
                float velocityX = mVelocityTracker.getXVelocity(); // it was too much
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
                boolean swipe = false;
                boolean swipeRight = false;

                if (Math.abs(deltaX) > tempwidth) {
                    swipe = true;
                    swipeRight = deltaX > 0;
                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity && absVelocityY < absVelocityX) {
                    // dismiss only if flinging in the same direction as dragging
                    swipe = (velocityX < 0) == (deltaX < 0);
                    swipeRight = mVelocityTracker.getXVelocity() > 0;
                }

                if (deltaX < 0 && swipe) {
                    mListView.setDrawSelectorOnTop(false);

                    if (swipe && !swipeRight && deltaX <= -tempwidth)
                        fullSwipeTrigger();
                    else if (deltaX >= -mWidthBehindView)
                        resetListItemState(mSwipeView);
                    else
                        resetListItemState(mSwipeView);

                } else {
                    resetListItemState(mSwipeView);
                }

                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mSwipeView = null;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float deltaX = event.getRawX() - mDownX;
                if (mVelocityTracker == null || deltaX > 0)
                    break;

                mVelocityTracker.addMovement(event);

                if (Math.abs(deltaX) > mSlop) {
                    mSwiping = true;
                    mListView.requestDisallowInterceptTouchEvent(true);

                    // Cancel ListView's touch (un-highlighting the item)
                    MotionEvent cancelEvent = MotionEvent.obtain(event);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mListView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (mSwiping && deltaX < 0) {

                    int widthShowBehindView = widthListView;
                    if (-deltaX < widthShowBehindView) {
                        mSwipeView.setTranslationX(deltaX);
                        if (-mSwipeView.getX() > mWidthBehindView)
                            mSwipeView.setTranslationX(-mWidthBehindView);
                        return false;
                    }

                    return false;

                } else if (mSwiping) {
                    resetListItemState(mSwipeView);
                }
                break;
            }

        }
        return false;
    }

    /*
     * Close swipe view
     */
    private void resetListItemState(View tempView) {
        tempView.animate().translationX(0).alpha(1f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mOptionsDisplay = false;
                if (mBehindView != null) {
                    mBehindView.setVisibility(View.GONE);
                }
            }
        });
    }

    /*
     * Open full swipe view
     */
    private void fullSwipeTrigger() {
        mOldSwipeView = mSwipeView;
        int width = mWidthBehindView;

        mSwipeView.animate()
                .translationX(-width)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mOptionsDisplay = true;
                    }
                });
    }

    public void resetWhenItemDeleted() {
        mOldSwipeView = null;
        mDownX = 0;
        mSwipeView = null;
        mSwiping = false;
    }


    /**
     * Callback functions
     */
    public interface TouchCallbacks {
        void OnItemListViewClick(AdapterView<?> parent, View view, int position, long id);
    }

}
