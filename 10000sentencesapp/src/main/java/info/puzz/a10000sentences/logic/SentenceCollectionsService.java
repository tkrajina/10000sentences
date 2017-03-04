package info.puzz.a10000sentences.logic;

import android.content.Context;
import android.os.AsyncTask;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import info.puzz.a10000sentences.Preferences;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceHistory;
import info.puzz.a10000sentences.models.SentenceStatus;
import info.puzz.a10000sentences.utils.TextUtils;
import temp.DBG;

public final class SentenceCollectionsService {

    private static final String TAG = SentenceCollectionsService.class.getSimpleName();

    private static final Random RANDOM = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());
    public static final int SUCCESS_STREAK_FOR_SKIPPING = 10;

    private final Dao dao;

    public SentenceCollectionsService(Dao dao) {
        this.dao = dao;
    }

    public void importNewTextCollection(String languageId, String title, String text) {
        SentenceCollection collection = new SentenceCollection()
                .setCollectionID(String.format("%s-%s", languageId, (title + text).hashCode()))
                .setCustom(true)
                .setTitle(title);
        collection.save();

        DBG.todo("Validations");
        List<String> sentenes = TextUtils.getSentences(text);
        for (int i = 0; i < sentenes.size(); i++) {
            String sen = sentenes.get(i);
            Sentence sentence = new Sentence()
                    .setSentenceId(String.format("%s-%s", collection.collectionID, sen.hashCode()))
                    .setCollectionId(collection.collectionID)
                    .setTargetSentence(sen)
                    .setComplexity(i);
            sentence.save();
        }
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

    public void updateStatus(final Sentence sentence, final SentenceStatus status, final long started, final long finished) {
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
                h.created = finished;
                h.time = (int) (finished - started);
                h.doneCount = collection.doneCount;
                h.todoCount = collection.todoCount;
                h.repeatCount = collection.repeatCount;
                h.ignoreCount = collection.ignoreCount;
                h.save();

                return null;
            }
        }.execute();
    }

    public Sentence findPreviousSentence(String collectionId) {
        SentenceHistory hist = new Select()
                .from(SentenceHistory.class)
                .where("collection_id=?", collectionId)
                .orderBy("created desc")
                .executeSingle();
        if (hist == null) {
            return null;
        }

        return new Select()
                .from(Sentence.class)
                .where("sentence_id=?", hist.sentenceId)
                .executeSingle();
    }

    /**
     * Update counters after calling this.
     */
    public void updateStatusByComplexity(String collectionId, int limit, SentenceStatus fromStatus, SentenceStatus toStatus, String complecityOrder) {
        List<Sentence> sentences = new Select()
                .from(Sentence.class)
                .where("collection_id=? and status=?", collectionId, fromStatus.getStatus())
                .orderBy("complexity " + complecityOrder)
                .limit(limit)
                .execute();

        String[] sentenceIds = new String[sentences.size()];
        for (int i = 0; i < sentences.size(); i++) {
            sentenceIds[i] = sentences.get(i).sentenceId;
        }
        
        new Update(Sentence.class)
                .set("status=?", toStatus.getStatus())
                .where("sentence_id in (" + StringUtils.repeat(",?", sentences.size()).substring(1) + ")", sentenceIds)
                .execute();
    }

    /**
     * @see #updateStatusByComplexity(String, int, SentenceStatus, SentenceStatus, String)
     */
    public void skipSentences(String collectionId, int limit) {
        updateStatusByComplexity(collectionId, Math.abs(limit), SentenceStatus.TODO, SentenceStatus.SKIPPED, "asc");
    }

    /**
     * @see #updateStatusByComplexity(String, int, SentenceStatus, SentenceStatus, String)
     */
    public void unskipSentences(String collectionId, int limit) {
        updateStatusByComplexity(collectionId, Math.abs(limit), SentenceStatus.SKIPPED, SentenceStatus.TODO, "desc");
    }

    public Language unknownLanguage() {
        return new Language().setName("Unknown");
    }

/*    public boolean isCandidateForSkipping(String collectionId) {
        List<SentenceHistory> hist = new Select()
                .from(SentenceHistory.class)
                .where("collection_id=?", collectionId)
                .orderBy("created desc")
                .limit(SUCCESS_STREAK_FOR_SKIPPING)
                .execute();

        if (hist.size() < SUCCESS_STREAK_FOR_SKIPPING) {
            return false;
        }

        Set<String> sentenceIds = new HashSet<>();
        for (SentenceHistory h : hist) {
            if (sentenceIds.contains(h.sentenceId)) {
                return false;
            }
            sentenceIds.add(h.sentenceId);

            if (h.status != SentenceStatus.DONE.getStatus()) { // If any sentence not DONE => nope
                return false;
            }

            if (h.previousStatus == SentenceStatus.DONE.getStatus() && h.status == SentenceStatus.DONE.getStatus()) {
                // this was a practice of known sentences => not counting it:
                return false;
            }
        }

        return true;
    }*/

}

