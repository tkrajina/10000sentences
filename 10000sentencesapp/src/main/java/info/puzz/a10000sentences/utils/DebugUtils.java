package info.puzz.a10000sentences.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Add:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *
 * To manifest, then:
 *
 * adb pull /sdcard/debug_10000sentences.db && sqlite3 debug_10000sentences.db
 *
 * ...to use the database locally.
 */
public final class DebugUtils {

    private static final String TAG = DebugUtils.class.getSimpleName();

    private DebugUtils() throws Exception {
        throw new Exception();
    }

    public static void backupDatabase(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            String packageName = context.getApplicationInfo().packageName;

            if (sd.canWrite()) {
                String currentDBPath = String.format("//data//%s//databases//%s",
                        packageName, databaseName);
                String backupDBPath = String.format("debug_%s", databaseName);
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            } else {
                Log.e(TAG, "Cannot write to sdcart");
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
