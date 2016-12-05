package info.puzz.a10000sentences;

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

    public static Sentence nextSentence(SentenceCollection collection) {
        Dao.reloadCollectionCounter(collection);

        int status = SentenceStatus.TODO.getStatus();
        if (RANDOM.nextInt(Constants.MAX_REPEAT_SENTENCES) < collection.repeatCount) {
            // New sentence:
            status = SentenceStatus.AGAIN.getStatus();
        }

        return getRandomSentenceByStatus(collection, status);
    }

    private static Sentence getRandomSentenceByStatus(SentenceCollection collection, int status) {
        List<Sentence> sentences = new Select()
                .from(Sentence.class)
                .where("collection_id=? and status=?", collection.getCollectionID(), status)
                .orderBy("complexity")
                .limit(200)
                .execute();
        if (sentences.size() == 0) {
            return null;
        }
        return sentences.get(RANDOM.nextInt(sentences.size()));
    }

    public static void updateStatus(Sentence sentence, SentenceStatus status) {
        sentence.status = status.getStatus();
        sentence.save();

        SentenceHistory h = new SentenceHistory();
        h.sentenceId = sentence.getSentenceId();
        h.status = status.getStatus();
        h.created = System.currentTimeMillis();
        h.save();
    }
}
