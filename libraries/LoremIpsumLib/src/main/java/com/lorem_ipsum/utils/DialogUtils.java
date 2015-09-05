package com.lorem_ipsum.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.lorem_ipsum.R;


/**
 * Created by originally.us on 4/17/14.
 */
public class DialogUtils {

    // create dialog with custom layout
    public static Dialog createCustomDialog(Activity activity, int layoutResourceId, String title, boolean cancelable) {
        Dialog dialog;

        // title
        if (StringUtils.isNull(title)) {
            dialog = new Dialog(activity, R.style.CustomDialog);
        } else {
            dialog = new Dialog(activity);
            dialog.setTitle(title);
        }

        // custom view
        if (layoutResourceId > 0) {
            View v = LayoutInflater.from(activity).inflate(layoutResourceId, null);
            dialog.setContentView(v);
        }

        dialog.setCancelable(cancelable);

        return dialog;
    }

    // create dialog with custom layout
    public static Dialog createCustomDialogLoading(Activity activity) {
        Dialog dialog = new Dialog(activity, R.style.CustomDialogLoading);
        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_progress_wheel, null);
        dialog.setContentView(v);
        dialog.setCancelable(false);

        return dialog;
    }

    public static void showConfirmDialog(Context context, String title, String message,
                                         String negativeBtnText, String positiveBtnText,
                                         final Runnable negativeProcedure, final Runnable positiveProcedure) {
        if (title == null)
            title = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (positiveProcedure != null)
                    positiveProcedure.run();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (negativeProcedure != null)
                    negativeProcedure.run();
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
