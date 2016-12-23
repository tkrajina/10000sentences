package info.puzz.a10000sentences.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang3.*;

import java.util.ArrayList;
import java.util.List;

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
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, text);

        String packageName = getTranslatePackageName(activity, intent);
        if (org.apache.commons.lang3.StringUtils.isEmpty(packageName)) {
            fallback(activity, text);
            return;
        }
        intent.setPackage(packageName);

        try {
            activity.startActivity(intent);
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
            fallback(activity, text);
        }
    }

    private static void fallback(Activity activity, String text) {
        Toast.makeText(activity, R.string.not_translate_app_found, Toast.LENGTH_SHORT).show();
        // If the user has "Tap to translate enabled", at least that will work:
        ShareUtils.copyToClipboard(activity, text);
    }

    private static String getTranslatePackageName(Activity activity, Intent intent) {
        return "com.google.android.apps.translate";
/*        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        List<LabeledIntent> intentList = new ArrayList<>();
        for (ResolveInfo ri : resInfo) {
            if(ri.activityInfo.packageName.contains("com.google.android") && ri.activityInfo.packageName.endsWith(".translate")) {
                return ri.activityInfo.packageName;
            }
        }
        return null;*/
    }
}
