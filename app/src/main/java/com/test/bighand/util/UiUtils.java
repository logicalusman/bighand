package com.test.bighand.util;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.test.bighand.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * UI methods common to the application.
 *
 * @author Usman
 */
public class UiUtils {

    /**
     * Shows alert dialog.
     *
     * @param ctx
     * @param title
     * @param msg
     */
    public static void showDialog(@NonNull Context ctx, String title, @NonNull String msg) {
        showDialog(ctx, title, msg, false);
    }

    /**
     * Shows alert dialog.
     *
     * @param ctx
     * @param title
     * @param msg
     * @param cancelable
     */
    public static void showDialog(@NonNull Context ctx, @Nullable String title, @NonNull String msg, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setCancelable(false).setMessage(msg).
                setPositiveButton(ctx.getString(R.string.ok), null);

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        builder.create().show();
    }

    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(@NonNull Activity ctx) {

        ctx.getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

}

