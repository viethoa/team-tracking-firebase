package com.lorem_ipsum.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.lorem_ipsum.R;


public class ConfirmationDialog {
    public Runnable ans_true = null;
    public Runnable ans_false = null;

    /**
     * Show a simple dialog with just one button, callback function is optional
     *
     * @param activity
     * @param titleText
     * @param messageText
     * @param positiveBtn       title of the button
     * @param positiveProcedure callback function, set to null if not needed
     * @return
     */
    public boolean SimpleDialog(Activity activity, String titleText, String messageText,
                                String positiveBtn, Runnable positiveProcedure) {
        if (positiveBtn == null || positiveBtn.isEmpty())
            positiveBtn = "Ok";

        return Confirm(activity, titleText, messageText, null, positiveBtn, null, positiveProcedure);
    }

    /**
     * Show a confirmation dialog with a negative and possitive button
     *
     * @param activity
     * @param titleText
     * @param messageText
     * @param negativeBtn       title of the negative/cancel button
     * @param positiveBtn       title of the possible/ok button
     * @param negativeProcedure callback function for negative button, set to null if not needed
     * @param positiveProcedure callback function for positive button, set to null if not needed
     * @return
     */
    public boolean Confirm(Activity activity, String titleText, String messageText,
                           String negativeBtn, String positiveBtn,
                           Runnable negativeProcedure, Runnable positiveProcedure) {
        ans_true = positiveProcedure;
        ans_false = negativeProcedure;

        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(titleText);
        dialog.setMessage(messageText);
        dialog.setCancelable(false);

        //Negative button
        if (negativeBtn != null && negativeBtn.isEmpty() == false)
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, negativeBtn,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int buttonId) {
                            if (ans_false != null)
                                ans_false.run();
                        }
                    }
            );

        //Positive button
        if (positiveBtn != null && positiveBtn.isEmpty() == false)
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveBtn,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int buttonId) {
                            if (ans_true != null)
                                ans_true.run();
                        }
                    }
            );

        dialog.setIcon(R.drawable.ic_dialog_alert_green);
        dialog.show();
        return true;
    }
}