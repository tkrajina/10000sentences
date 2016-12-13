package info.puzz.a10000sentences;

import android.content.Context;
import android.os.AsyncTask;

import com.activeandroid.query.Select;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceHistory;
import info.puzz.a10000sentences.models.SentenceStatus;

public final class SentenceCollectionsService {

    private static final Random RANDOM = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());

    public static Sentence nextSentence(Context context, SentenceCollection collection, String exceptSentenceId) {
        int maxRepeat = Preferences.getMaxRepeat(context);
        int status = SentenceStatus.TODO.getStatus();
        if (RANDOM.nextInt(maxRepeat) < collection.repeatCount) {
            // New sentence:
            status = SentenceStatus.REPEAT.getStatus();
        }

        return getRandomSentenceByStatus(collection, status, exceptSentenceId);
    }

    public static Sentence getRandomKnownSentence(Context context, SentenceCollection collection, String exceptSentenceId) {
        return new Select()
                .from(Sentence.class)
                .where("collection_id=? and status=? and sentence_id<>?", collection.collectionID, SentenceStatus.DONE.getStatus(), String.valueOf(exceptSentenceId))
                .orderBy("random()")
                .executeSingle();
    }

    private static Sentence getRandomSentenceByStatus(SentenceCollection collection, int status, String exceptSentenceId) {
        List<Sentence> sentences = new Select()
                .from(Sentence.class)
                .where("collection_id=? and status=? and sentence_id!=?", collection.getCollectionID(), status, String.valueOf(exceptSentenceId))
                .orderBy("complexity")
                .limit(20)
                .execute();
        if (sentences.size() == 0) {
            return null;
        }
        return sentences.get(RANDOM.nextInt(sentences.size()));
    }

    public static void updateStatus(final Sentence sentence, final SentenceStatus status, final long started) {
        sentence.status = status.getStatus();
        sentence.save();

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                SentenceCollection collection = Dao.reloadCollectionCounter(Dao.getCollection(sentence.collectionId));

                SentenceHistory h = new SentenceHistory();
                h.sentenceId = sentence.getSentenceId();
                h.collectionId = sentence.collectionId;
                h.status = status.getStatus();
                h.created = System.currentTimeMillis();
                h.time = (int) (System.currentTimeMillis() - started);
                h.doneCount = collection.doneCount;
                h.todoCount = collection.todoCount;
                h.repeatCount = collection.repeatCount;
                h.ignoreCount = collection.ignoreCount;
                h.save();

                return null;
            }
        }.execute();
    }
}
