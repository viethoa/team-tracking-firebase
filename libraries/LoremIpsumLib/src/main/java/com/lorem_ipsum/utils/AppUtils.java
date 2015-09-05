package com.lorem_ipsum.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;

import com.lorem_ipsum.managers.NetworkStateReceiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Originally.US on 5/4/14.
 * Remember to add the following into AndroidManifest.xml
 * <application
 * android:name="com.lorem_ipsum.utils.AppUtils"
 * ...
 */
public class AppUtils extends MultiDexApplication implements NetworkStateReceiver.NetworkStateReceiverListener {

    private static SimpleDateFormat formatter = new SimpleDateFormat("E MMM d yyyy hh:mm a", java.util.Locale.getDefault());
    private static final String TAG = "AppUtils";

    private static Context appContext;
    private static boolean appState;

    private static boolean networkState;
    public static NetworkListener networkListener;
    private NetworkStateReceiver networkStateReceiver;

    public void onCreate() {
        MultiDex.install(getApplicationContext());
        super.onCreate();
        AppUtils.appContext = getApplicationContext();
        AppUtils.networkState = isOnline(appContext);

        initialiseNetworkManagement();

        //Print out package name for debug purpose
        String packageName = "Package name: " + getAppPackageName();
        Log.i(TAG, packageName);

        ImageLoaderUtils.configImageLoader(appContext);
    }

    public static Context getAppContext() {
        return appContext;
    }

    /**
     * Using for detect application status
     * Note: need set instance is false in onCreate and true in startActivity
     */
    public static void setAppState(boolean bool) {
        appState = bool;
    }

    public static boolean getAppState() {
        return appState;
    }

    public static Bundle getBundleMetaData() {
        try {
            ApplicationInfo ai = appContext.getPackageManager().getApplicationInfo(
                    appContext.getPackageName(),
                    PackageManager.GET_META_DATA);

            Bundle bundle = ai.metaData;
            if (bundle == null) {
                ToastUtils.showErrorMessageWithSuperToast("don't get bundle meta data", TAG);
                return null;
            }

            return bundle;
        } catch (PackageManager.NameNotFoundException e) {
            ToastUtils.showErrorMessageWithSuperToast(e.getMessage(), TAG);
        }

        return null;
    }

    /**
     * Initialise network management
     * <p/>
     * That's handle will be return network status for changed.
     * Note: you need register network listener in your activity
     */
    public interface NetworkListener {
        void networkAvailable();

        void networkUnavailable();
    }

    private void initialiseNetworkManagement() {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        IntentFilter intentFilter = new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(networkStateReceiver, intentFilter);
    }

    public static boolean getNetworkState() {
        return networkState;
    }

    @Override
    public void networkAvailable() {
        networkState = true;
        if (networkListener != null) {
            networkListener.networkAvailable();
        }
    }

    @Override
    public void networkUnavailable() {
        networkState = false;
        if (networkListener != null) {
            networkListener.networkUnavailable();
        }
    }

    /**
     * Return the device uuid (integer)
     *
     * @return Return the device uuid
     */

    public static String getDeviceUUID() {
        return DeviceUtils.getDeviceUUID(getAppContext());
    }

    /**
     * Return the app version name, eg. 1.2
     *
     * @return Return the app version number
     */
    public static String getAppVersionName() {
        Context context = getAppContext();

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        } catch (Exception e) {
            // should never happen
            throw new RuntimeException("Unknown expected exception in getAppVersion: " + e);
        }
    }

    /**
     * Return the app version code (integer)
     *
     * @return Return the app version number
     */
    public static int getAppVersion() {
        Context context = getAppContext();

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        } catch (Exception e) {
            // should never happen
            throw new RuntimeException("Unknown expected exception in getAppVersion: " + e);
        }
    }

    /**
     * Return the app package name
     *
     * @return Return the app package name
     */
    public static String getAppPackageName() {
        Context context = getAppContext();
        return context.getPackageName();
    }

    /**
     * Helper function to check if this app and device is able to make call
     *
     * @param context should be an Activity context
     */
    public static boolean isTelephonyAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    /**
     * Check current network hardware states, note: doesn't handle proxy being unavailable
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Check for permission to write to storage
     */
    public static boolean canWriteExternalStorage() {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = getAppContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Helper function to initiate a phone call to a given number
     *
     * @param context     should be an Activity context
     * @param phoneNumber the phone number to call
     */
    public static void callPhoneNumber(Context context, String phoneNumber) {
        String uri = "tel:" + phoneNumber.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));

        String messageNoTelephony = "Unable to make call from this app";
        String messageUnknownError = "Unexpected error occurs";

        boolean hasPhone = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        if (hasPhone == false) {
            ToastUtils.showErrorMessageWithSuperToast(messageNoTelephony, "AppUtils");
            return;
        }

        try {
            context.startActivity(intent);
        } catch (SecurityException exception) {
            ToastUtils.showErrorMessageWithSuperToast(messageNoTelephony, "AppUtils");
        } catch (Exception exception) {
            ToastUtils.showErrorMessageWithSuperToast(messageUnknownError, "AppUtils");
        }
    }

    /**
     * Helper function to initiate a phone call to a given number
     *
     * @param context should be an Activity context
     * @param url     the url to open
     */
    public static void openUrlInBrowser(Context context, String url) {
        if (url == null || url.length() <= 0)
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse(url));
        } catch (Exception e) {
            return;
        }
        context.startActivity(intent);
    }

    /**
     * Return the first hash generated to be used with Facebook SDK
     *
     * @return Return the first hash generated to be used with Facebook SDK
     */
    public static String showFacebookHash() {
        String hashString = null;

        //Generate Facebook hash
        try {
            Context ctx = getAppContext();
            PackageManager pm = ctx.getPackageManager();
            String packageName = ctx.getPackageName();
            Log.d("Package Name: ", packageName);

            PackageInfo info = pm != null ? pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES) : null;
            if (info != null && info.signatures != null) {
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    hashString = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                    Log.d("KeyHash: ", hashString);
                }
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashString;
    }

    /**
     * Helper function to append text to a log file
     *
     * @param tag  the tag name to log
     * @param text the string to log
     */
    public static void writeToLogFile(String tag, String text) {
        if (text == null || text.isEmpty())
            return;

        boolean canWrite = AppUtils.canWriteExternalStorage();
        if (canWrite == false)
            return;

        String storagePath = Environment.getExternalStorageDirectory().toString();
        File logFile = new File(storagePath, "/application_logs.txt");

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                logFile = null;
                e.printStackTrace();
            }
        }
        if (logFile == null) {
            Log.e(TAG, "Failed to create log file");
            return;
        }

        try {
            //User BufferedWriter for good performance, 'true' to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));

            buf.append(formatter.format(new Date()));
            if (tag != null && !tag.isEmpty())
                buf.append(tag).append(": ");
            if (!text.isEmpty())
                buf.append(text);

            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function to check if another app has been installed in user's device
     *
     * @param packageName the package name of the main activity. Eg. com.example.MainActivity
     */
    public static boolean hasOtherAppWithPackage(String packageName) {
        if (packageName == null || packageName.length() <= 0)
            return false;
        Intent intent = getAppContext().getPackageManager().getLaunchIntentForPackage(packageName);
        return intent != null;
    }

    // get UUID

}
