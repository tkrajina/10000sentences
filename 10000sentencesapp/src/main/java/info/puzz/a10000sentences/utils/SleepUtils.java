package info.puzz.a10000sentences.utils;

import android.app.Activity;
import android.view.WindowManager;

public final class SleepUtils {
    public SleepUtils() throws Exception {
        throw new Exception();
    }

    public static void disableSleep(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
/*        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "tag");
        wl.acquire();
        return wl;*/
/*        if (wl != null) {
            wl.release();
        }*/
    }

    public static void enableSleep(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
