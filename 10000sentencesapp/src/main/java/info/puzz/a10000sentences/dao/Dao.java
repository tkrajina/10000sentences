package info.puzz.a10000sentences.dao;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceStatus;

public class Dao {

    @Inject
    public Dao() {
    }

    public static void importLanguage(Language language) {
        language.save();
    }

    public static void importCollection(SentenceCollection col) {
        SentenceCollection sentenceCollection = new Select()
                .from(SentenceCollection.class)
                .where("collection_id = ?", col.collectionID)
                .executeSingle();
        if (sentenceCollection != null) {
            sentenceCollection.filename = col.filename;
            sentenceCollection.save();
        } else {
            col.save();
        }
    }

    public static void importSentences(List<Sentence> sentences) {

        List<String> ids = new ArrayList<>();
        for (Sentence sentence : sentences) {
            ids.add(sentence.sentenceId);
        }

        List<Sentence> existingSentences = new Select()
                .from(Sentence.class)
                .where(
                        String.format("sentence_id in (%s)", StringUtils.repeat("? ", sentences.size()).trim().replace(" ", ",")),
                        ids.toArray(new String[ids.size()]))
                .execute();

        Map<String, Sentence> existingSentencesMap = new HashMap<>();
        for (Sentence model : existingSentences) {
            existingSentencesMap.put(model.sentenceId, model);
        }

        ActiveAndroid.beginTransaction();
        try {
            for (Sentence sentence : sentences) {
                if (existingSentencesMap.containsKey(sentence.sentenceId)) {
                    Sentence existingSentence = existingSentencesMap.get(sentence.sentenceId);
                    existingSentence.targetSentence = sentence.targetSentence;
                    existingSentence.knownSentence = sentence.knownSentence;
                    existingSentence.complexity = sentence.complexity;
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
                .orderBy("-done_count, target_lang, known_lang")
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

    public static SentenceCollection reloadCollectionCounter(SentenceCollection collection) {
        int rows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ?",
                new String[] {collection.collectionID});
        int todoRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.collectionID, String.valueOf(SentenceStatus.TODO.getStatus())});
        int againRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.collectionID, String.valueOf(SentenceStatus.REPEAT.getStatus())});
        int doneRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.collectionID, String.valueOf(SentenceStatus.DONE.getStatus())});
        int ignoreRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.collectionID, String.valueOf(SentenceStatus.IGNORE.getStatus())});
        collection.count = rows;
        collection.todoCount = todoRows;
        collection.repeatCount = againRows;
        collection.doneCount = doneRows;
        collection.ignoreCount = ignoreRows;
        collection.save();

        return collection;
    }

    public static List<Sentence> getRandomSentences(SentenceCollection collection) {
        return new Select()
                .from(Sentence.class)
                .where("collection_id = ?", collection.collectionID)
                .orderBy("random()")
                .limit(100)
                .execute();
    }

    public static void removeCollectionSentences(SentenceCollection collection) {
        new Delete()
                .from(Sentence.class)
                .where("collection_id=?", collection.collectionID)
                .execute();
        reloadCollectionCounter(collection);
    }

    public static Map<String, Language> getLanguagesByLanguageID() {
        HashMap<String, Language> res = new HashMap<>();
        for (Language language : getLanguages()) {
            res.put(language.languageId, language);
        }
        return res;
    }

    public static Sentence getSentenceBySentenceId(String sentenceId) {
        return new Select()
                .from(Sentence.class)
                .where("sentence_id = ?", sentenceId)
                .executeSingle();
    }
}
