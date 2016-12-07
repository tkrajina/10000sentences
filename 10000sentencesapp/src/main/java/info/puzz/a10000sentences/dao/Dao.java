package info.puzz.a10000sentences.dao;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceStatus;

public class Dao {
    private Dao() throws Exception {
        throw new Exception();
    }

    public static void importLanguage(Language language) {
        language.save();
    }

    public static void importCollection(SentenceCollection col) {
        SentenceCollection sentenceCollection = new Select()
                .from(SentenceCollection.class)
                .where("collection_id = ?", col.getCollectionID())
                .executeSingle();
        if (sentenceCollection != null) {
            sentenceCollection
                    .setFilename(col.getFilename())
                    .save();
        } else {
            col.save();
        }
        col.save();
    }

    public static void importSentences(List<Sentence> sentences) {

        List<String> ids = new ArrayList<>();
        for (Sentence sentence : sentences) {
            ids.add(sentence.getSentenceId());
        }

        List<Sentence> existingSentences = new Select()
                .from(Sentence.class)
                .where(
                        String.format("sentence_id in (%s)", StringUtils.repeat("? ", sentences.size()).trim().replace(" ", ",")),
                        ids.toArray(new String[ids.size()]))
                .execute();

        Map<String, Sentence> existingSentencesMap = new HashMap<>();
        for (Sentence model : existingSentences) {
            existingSentencesMap.put(model.getSentenceId(), model);
        }

        ActiveAndroid.beginTransaction();
        try {
            for (Sentence sentence : sentences) {
                if (existingSentencesMap.containsKey(sentence.getSentenceId())) {
                    Sentence existingSentence = existingSentencesMap.get(sentence.getSentenceId());
                    existingSentence.setTargetSentence(sentence.getTargetSentence());
                    existingSentence.setKnownSentence(sentence.getKnownSentence());
                    existingSentence.setComplexity(sentence.getComplexity());
                    existingSentence.save();
                } else {
                    sentence.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static List<SentenceCollection> getCollections() {
        return new Select()
                .from(SentenceCollection.class)
                .execute();
    }

    public static List<Language> getLanguages() {
        return new Select()
                .from(Language.class)
                .execute();
    }

    public static SentenceCollection getCollection(String collectionId) {
        return new Select()
                .from(SentenceCollection.class)
                .where("collection_id = ?", collectionId)
                .executeSingle();
    }

    public static Language getLanguage(String languageID) {
        List<Language> res = new Select()
                .from(Language.class)
                .where("language_id = ?", languageID)
                .limit(1)
                .execute();
        if (res.size() == 0) {
            return null;
        }
        return res.get(0);
    }

    public static void reloadCollectionCounter(SentenceCollection collection) {
        int rows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ?",
                new String[] {collection.getCollectionID()});
        int todoRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.getCollectionID(), String.valueOf(SentenceStatus.TODO.getStatus())});
        int againRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.getCollectionID(), String.valueOf(SentenceStatus.AGAIN.getStatus())});
        int doneRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.getCollectionID(), String.valueOf(SentenceStatus.DONE.getStatus())});
        int ignoreRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.getCollectionID(), String.valueOf(SentenceStatus.IGNORE.getStatus())});
        collection.count = rows;
        collection.todoCount = todoRows;
        collection.repeatCount = againRows;
        collection.doneCount = doneRows;
        collection.ignoreCount = ignoreRows;
        collection.save();
    }

    public static List<Sentence> getRandomSentences(SentenceCollection collection) {
        return new Select()
                .from(Sentence.class)
                .where("collection_id = ?", collection.collectionID)
                .orderBy("random()")
                .limit(100)
                .execute();
    }
}
