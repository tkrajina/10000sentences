package info.puzz.a10000sentences.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.activeandroid.query.Select;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import info.puzz.a10000sentences.services.SentenceCollectionsService;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceStatus;

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

    private static final Random RANDOM = new SecureRandom(("" + System.currentTimeMillis()).getBytes());

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

    public static void createDummyDoneSentences(
            final SentenceCollectionsService sentenceCollectionsService,
            final String collectionId)
    {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 0; i < RANDOM.nextInt(100); i++) {
                    Sentence sentence = new Select()
                            .from(Sentence.class)
                            .where("collection_id=?", collectionId)
                            .orderBy("random()")
                            .executeSingle();
                    if (sentence != null) {
                        long finished = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2 * i);
                        sentenceCollectionsService.updateStatus(sentence, SentenceStatus.DONE, finished - TimeUnit.MINUTES.toMillis(RANDOM.nextInt(5)), finished);
                    }
                }
                return null;
            }
        }.execute();
    }

}
