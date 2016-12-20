package info.puzz.a10000sentences.logic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.query.Select;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import info.puzz.a10000sentences.Preferences;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceHistory;
import info.puzz.a10000sentences.models.SentenceStatus;

public final class SentenceCollectionsService {

    private static final String TAG = SentenceCollectionsService.class.getSimpleName();

    private static final Random RANDOM = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());

    private final Dao dao;

    public SentenceCollectionsService(Dao dao) {
        this.dao = dao;
    }

    public Sentence nextSentence(Context context, SentenceCollection collection, String exceptSentenceId) {
        int maxRepeat = Preferences.getMaxRepeat(context);
        int status = SentenceStatus.TODO.getStatus();
        if (RANDOM.nextInt(maxRepeat) < collection.repeatCount) {
            // New sentence:
            status = SentenceStatus.REPEAT.getStatus();
        }

        Sentence result = getRandomSentenceByStatus(collection, SentenceStatus.fromStatus(status), exceptSentenceId);
        if (result == null) {
            return getRandomSentenceByStatus(collection, SentenceStatus.TODO, exceptSentenceId);
        }

        return result;
    }

    public Sentence getRandomKnownSentence(Context context, SentenceCollection collection, String exceptSentenceId) {
        return  new Select()
                .from(Sentence.class)
                .where("collection_id=? and status=? and sentence_id<>?", collection.collectionID, SentenceStatus.DONE.getStatus(), String.valueOf(exceptSentenceId))
                .orderBy("random()")
                .executeSingle();
    }

    private Sentence getRandomSentenceByStatus(SentenceCollection collection, SentenceStatus status, String exceptSentenceId) {
        List<Sentence> sentences = new Select()
                .from(Sentence.class)
                .where("collection_id=? and status=? and sentence_id!=?", collection.getCollectionID(), status.getStatus(), String.valueOf(exceptSentenceId))
                .orderBy("complexity")
                .limit(20)
                .execute();

        if (status == SentenceStatus.REPEAT) {
            // REPEAT sentences are special because we don't want them to be repeated immediately after
            // the user failed to guess them. So, find the oldest one:
            Map<String, Long> sentenceTakenTime = new HashMap<>();
            List<SentenceHistory> senHist = new Select()
                    .from(SentenceHistory.class)
                    .orderBy("created")
                    .limit(200)
                    .execute();
            for (SentenceHistory sh : senHist) {
                sentenceTakenTime.put(sh.sentenceId, sh.created);
            }

            for (Sentence sentence : sentences) {
                Long timeTaken = sentenceTakenTime.get(sentence.sentenceId);
                if (timeTaken == null) { // Longer than the sample of history we took => OK:
                    return sentence;
                }

                if (System.currentTimeMillis() - timeTaken.longValue() > TimeUnit.MINUTES.toMillis(5)) {
                    return sentence;
                }
            }
        }

        if (sentences.size() == 0) {
            return null;
        }
        return sentences.get(RANDOM.nextInt(sentences.size()));
    }

    public void updateStatus(final Sentence sentence, final SentenceStatus status, final long started) {
        sentence.status = status.getStatus();
        sentence.save();

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                SentenceCollection collection = dao.reloadCollectionCounter(dao.getCollection(sentence.collectionId));

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

