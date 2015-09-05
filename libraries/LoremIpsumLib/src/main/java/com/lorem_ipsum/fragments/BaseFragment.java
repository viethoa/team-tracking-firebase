package com.lorem_ipsum.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lorem_ipsum.R;
import com.lorem_ipsum.activities.BaseWebviewActivity;
import com.lorem_ipsum.utils.AnimationUtils;
import com.lorem_ipsum.utils.CustomDateParser;
import com.lorem_ipsum.utils.DialogUtils;
import com.lorem_ipsum.utils.RetrofitUtils;
import com.lorem_ipsum.utils.ToastUtils;

import java.lang.reflect.Type;
import java.util.Date;

import retrofit.RetrofitError;

/**
 * Created by Originally.US on 11/10/14.
 */
public class BaseFragment extends Fragment {

    protected final String LOG_TAG = this.getClass().getSimpleName();

    protected View mRootView;
    protected Activity mHostActivity;
    protected AQuery mAQuery;
    protected Menu mMenu;
    protected ActionBar mActionBar;

    private Dialog mLoadingDialog;

    public String mTitle;

    //Generic callback interface
    public static abstract class OnCallbackListener {
        public abstract void onCallback(Object object);
    }

    private OnCallbackListener mCallbackListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mAQuery = new AQuery(mRootView);

        //Create loading dialog
        if (this.mLoadingDialog == null)
            this.mLoadingDialog = DialogUtils.createCustomDialogLoading(getActivity());

        this.mHostActivity = getActivity();
        this.mActionBar = this.mHostActivity.getActionBar();

        //Update title, if any
        if (this.mTitle != null) {
            mHostActivity.setTitle(this.mTitle);
            if (mActionBar != null)
                mActionBar.setTitle(this.mTitle);
        }

        if (this.mRootView == null)
            logError("mView must be set before calling super.onCreateView");

        setHasOptionsMenu(true);

        //Don't need to call super for onCreateView
        //return super.onCreateView(inflater, container, savedInstanceState);
        return this.mRootView;
    }

    /**
     * custom getter
     */
    protected Activity getmHostActivity() {
        if (mHostActivity == null)
            mHostActivity = getActivity();
        return mHostActivity;
    }

    /**
     * subclass can override this to handle custom back button
     */
    public boolean allowBackPressed() {
        return true;
    }

    protected void hideHomeButtonInActionBar() {
        if (mActionBar == null)
            return;

        mActionBar.setHomeButtonEnabled(false);          // disable the button
        mActionBar.setDisplayHomeAsUpEnabled(false);     // remove the left caret
        mActionBar.setDisplayShowHomeEnabled(false);     // remove the icon
    }


    //----------------------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------------------

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mAQuery = null;
        mRootView = null;
        mHostActivity = null;
        mCallbackListener = null;

        //Force garbage collector to work
        //System.gc();

        super.onDestroyView();
    }


    //----------------------------------------------------------------------------------------------------------
    // Custom Action Bar
    //----------------------------------------------------------------------------------------------------------

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.mMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showOrHideMenuItem(int resId, boolean isShow) {
        if (mMenu == null)
            return;
        MenuItem menuItem = mMenu.findItem(resId);
        if (menuItem == null)
            return;
        menuItem.setVisible(isShow);
    }


    //----------------------------------------------------------------------------------------------------------
    // Loading UI Helpers
    //----------------------------------------------------------------------------------------------------------

    /**
     * show dialog loading
     */
    protected void showLoadingDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();

            View progressbarContainer = mLoadingDialog.findViewById(R.id.loading_progress_wheel_view_container);
            AnimationUtils.AnimationWheelForDialog(getActivity(), progressbarContainer);
        }
    }

    protected boolean isShowLoadingDialog() {
        if (mLoadingDialog == null)
            return false;

        return mLoadingDialog.isShowing();
    }

    /**
     * Dismiss loading dialog if it's showing
     */
    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            try {
                mLoadingDialog.dismiss();
            } catch (Exception e) {
                // dismiss dialog after destroy activity
            }
        }
    }


    //----------------------------------------------------------------------------------------------------------
    // Error & Logging Helpers
    //----------------------------------------------------------------------------------------------------------

    /**
     * shortcuts to logging methods so that we can disable everything all together
     * or redirect logging to a file or to server
     */
    protected void logError(String message) {
        Log.e(LOG_TAG, message);
    }

    protected void logDebug(String message) {
        Log.d(LOG_TAG, message);
    }

    protected void logWarning(String message) {
        Log.w(LOG_TAG, message);
    }

    protected void logInfo(String message) {
        Log.i(LOG_TAG, message);
    }

    /**
     * show a toast with message and log out to console as well
     */
    protected void showToastMessage(String message) {
        ToastUtils.showToastMessageWithSuperToast(message);
    }

    protected void showToastErrorMessage(String message) {
        ToastUtils.showErrorMessageWithSuperToast(message, LOG_TAG);
    }

    protected void showErrorMessage(String message) {
        ToastUtils.showErrorMessageWithSuperToast(message, LOG_TAG);
    }


    /**
     * Standard function to handle error from Retrofit
     */
    protected void handleRetrofitError(final RetrofitError retrofitError) {
        dismissLoadingDialog();
        RetrofitUtils.handleRetrofitError(this.getActivity(), retrofitError, null, true);
    }

    /**
     * Standard function to handle error from Retrofit
     */
    protected void handleRetrofitErrorNoToast(final RetrofitError retrofitError) {
        dismissLoadingDialog();
        RetrofitUtils.handleRetrofitError(this.getActivity(), retrofitError, null, false);
    }


    //----------------------------------------------------------------------------------------------------------
    // Other utilities
    //----------------------------------------------------------------------------------------------------------

    protected void openUrl(String url, boolean useExternalBrowser) {
        if (url == null)
            return;

        //Use external browser
        if (useExternalBrowser) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return;
        }

        //Our internal webview
        Intent intent = new Intent(mHostActivity, BaseWebviewActivity.class);
        intent.putExtra("url", url);
        this.startActivity(intent);
    }


    //----------------------------------------------------------------------------------------------------------
    // Show/Hide view within fade in/out animation
    //----------------------------------------------------------------------------------------------------------

    protected void showView(View view) {
        showView(view, 200);
    }

    protected void hideView(View view) {
        hideView(view, 200);
    }

    protected void showView(View view, int duration) {
        AnimationUtils.fadeInViewWithDuration(view, duration, true, 0);
    }

    protected void hideView(View view, int duration) {
        AnimationUtils.fadeOutViewWithDuration(view, duration, true, 0);
    }


    //----------------------------------------------------------------------------------------------------------
    // Passing data between fragment and activity
    //----------------------------------------------------------------------------------------------------------

    protected Intent getIntentWithObjectAndKey(Object obj, String key) {
        return getIntentWithObjectAndKey(null, obj, key);
    }

    protected Intent getIntentWithObjectAndKey(Class<?> activityClass, Object obj, String key) {
        String modelString = getSerializedModelString(obj);

        Intent intent;
        if (activityClass == null)
            intent = new Intent();
        else
            intent = new Intent(mHostActivity, activityClass);
        intent.putExtra(key, modelString);
        return intent;
    }

    protected String getSerializedModelString(Object obj) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new CustomDateParser());
        Gson gson = gsonBuilder.create();
        return gson.toJson(obj);
    }

    //protected <T> T getSerializedModelFromIntent(Intent data, String key, Class<T> classOfT) {
    protected Object getSerializedModelFromIntent(Intent data, String key, Type type) {
        String modelString = data.getStringExtra(key);
        if (modelString == null || modelString.length() <= 0)
            return null;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new CustomDateParser());
        Gson gson = gsonBuilder.create();

        Object object = gson.fromJson(modelString, type);
        return object;
        //return Primitives.wrap(classOfT).cast(object);
    }

    //----------------------------------------------------------------------------------------------------------
    // Generic callback mechanism
    //----------------------------------------------------------------------------------------------------------

    public void setCallbackListener(final OnCallbackListener listener) {
        this.mCallbackListener = listener;
    }

    protected void performCallbackWithObject(Object object) {
        if (this.mCallbackListener != null)
            this.mCallbackListener.onCallback(object);
    }

    protected void clearCallbackListener() {
        this.mCallbackListener = null;
    }
}
