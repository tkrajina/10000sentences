package info.puzz.a10000sentences.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import info.puzz.a10000sentences.R;

/**
 * Created by puzz on 02/11/2016.
 */

public final class DialogUtils {
    private DialogUtils() throws Exception {
        throw new Exception();
    }

    public static void showWarningDialog(Activity activity, String title, String message) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showYesNoButton(Activity activity, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.yes, listener)
                .setNegativeButton(R.string.no, listener)
                .show();
    }
}
