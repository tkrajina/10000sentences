package info.puzz.a10000sentences.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by puzz on 05/12/2016.
 */

public class ShareUtils {
    private ShareUtils() throws Exception {
        throw new Exception();
    }

    public static void shareWithTranslate(Activity activity, String text) {
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);

        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<>();
        for (ResolveInfo ri : resInfo) {
            if(ri.activityInfo.packageName.contains("translate")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name));
                intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intentList.add(new LabeledIntent(intent, ri.activityInfo.packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        Intent openInChooser = Intent.createChooser(sendIntent, "Translate");

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        activity.startActivity(openInChooser);
    }

    public static void copyToClipboard(Activity activity, String string) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("translate", string);
        clipboard.setPrimaryClip(clip);
    }
}
