package com.lorem_ipsum.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.List;

/**
 * Created by originally.us on 4/18/14.
 */
public class IntentUtils {

    private static final String LOG_TAG = "IntentUtils";

    public static void openGalery(Activity activity, int requestCode) {
        try {
            // open default gallery app
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity.startActivityForResult(intent, requestCode);

        } catch (Exception ex) {
            LogUtils.logInDebug(LOG_TAG, "open default gallery app error: " + ex.getMessage());
            // open dialog to choose gallery app
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            Intent chooser = Intent.createChooser(intent, "Choose a Picture");
            activity.startActivityForResult(chooser, requestCode);
        }
    }

    public static void openCameraApp(Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(intent, requestCode);
            }
        } catch (Exception e) {
            LogUtils.logInDebug(LOG_TAG, "open default gallery app error: " + e.getMessage());
        }
    }

    public static void openPhoneCall(Activity activity, String phone) {
        if (StringUtils.isNull(phone))
            return;

        try {
            String data = "tel:" + phone;
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(data));
            activity.startActivity(intent);
        } catch (Exception e) {
            LogUtils.logInDebug(LOG_TAG, "open phone call error: " + e.getMessage());
        }
    }

    public static void openBrowser(Activity activity, String url) {
        if (StringUtils.isNull(url))
            return;

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            LogUtils.logInDebug(LOG_TAG, "open browser error: " + e.getMessage());
        }
    }

    public static void openSendSms(Context context, String message) {
        try {
            Intent sendSmsIntent = new Intent(Intent.ACTION_VIEW);
            sendSmsIntent.putExtra("sms_body", message);
            sendSmsIntent.setType("vnd.android-dir/mms-sms");
            context.startActivity(sendSmsIntent);
        } catch (Exception e) {
            if (e != null)
                LogUtils.logInDebug(LOG_TAG, "openSendSms error: " + e.getMessage());
        }
    }

    public static void openSendEmail(Context context, String subject, String message) {
        try {
            Intent sendEmailIntent = new Intent(Intent.ACTION_SEND);
            sendEmailIntent.setType("message/rfc822");
            sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendEmailIntent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(sendEmailIntent);
        } catch (Exception e) {
            if (e != null)
                LogUtils.logInDebug(LOG_TAG, "openSendSms error: " + e.getMessage());
        }
    }

    public static boolean startTargetShareIntent(Context context, String targetPackageName, String textMessage) {
        boolean targetPackageExist = false;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resInfos = context.getPackageManager().queryIntentActivities(shareIntent, 0);

        // Check Facebook Android app is installed
        if (!resInfos.isEmpty()) {
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                LogUtils.logInDebug(LOG_TAG, "package: " + packageName);

                if (packageName.contains(targetPackageName)) {
                    targetPackageExist = true;
                    Intent targetShareIntent = new Intent();
                    targetShareIntent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    targetShareIntent.setAction(Intent.ACTION_SEND);
                    targetShareIntent.setType("text/plain");
                    targetShareIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
                    targetShareIntent.setPackage(packageName);
                    context.startActivity(targetShareIntent);
                    break;
                }
            }
        }

        return targetPackageExist;
    }

}
