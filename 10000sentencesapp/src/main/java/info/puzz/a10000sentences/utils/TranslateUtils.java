package info.puzz.a10000sentences.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import info.puzz.a10000sentences.R;

public final class TranslateUtils {

    private static final String TAG = TranslateUtils.class.getSimpleName();

    private TranslateUtils() throws Exception {
        throw new Exception();
    }

    public static void translate(Activity activity, String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PROCESS_TEXT);
        intent.setType("text/plain");
        intent.setPackage("com.google.android.apps.translate");
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, text);
        try {
            activity.startActivity(intent);
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(activity, R.string.not_translate_app_found, Toast.LENGTH_SHORT).show();
        }
    }
}
