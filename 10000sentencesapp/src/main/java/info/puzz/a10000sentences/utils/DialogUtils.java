package info.puzz.a10000sentences.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import info.puzz.a10000sentences.R;

/**
 * Created by puzz on 02/11/2016.
 */

public final class DialogUtils {

    public interface OnInputDialogClickListener {
        void onClick(DialogInterface dialogInterface, int which, String value);
    }

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

    public static void showInputDialog(Activity activity, String message, final OnInputDialogClickListener listener) {
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        final DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int wh) {
                if (listener != null) {
                    listener.onClick(dialogInterface, wh, input.getText().toString());
                }
            }
        };

        new AlertDialog.Builder(activity)
                .setTitle(message)
                .setView(input)
                .setPositiveButton(R.string.ok, l)
                .setNegativeButton(R.string.cancel, l)
                .show();
    }

}
