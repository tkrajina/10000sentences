package info.puzz.a10000sentences.dao;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
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
import info.puzz.a10000sentences.models.SentenceHistory;
import info.puzz.a10000sentences.models.SentenceStatus;
import info.puzz.a10000sentences.utils.SqlFilterUtils;
import temp.DBG;

public class Dao {

    @Inject
    public Dao() {
    }

    public void importLanguage(Language language) {
        language.save();
    }

    public void importCollection(SentenceCollection col) {
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
    }

    public void importSentences(List<Sentence> sentences) {

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

    public List<SentenceCollection> getDefaultCollections() {
        return new Select()
                .from(SentenceCollection.class)
                .where("custom=0")
                .orderBy("-done_count, target_lang, known_lang")
                .execute();
    }

    public List<SentenceCollection> getCustomCollections() {
        return new Select()
                .from(SentenceCollection.class)
                .where("custom=1")
                .orderBy("-done_count, target_lang, known_lang")
                .execute();
    }

    public List<Language> getLanguages() {
        return new Select()
                .from(Language.class)
                .execute();
    }

    public SentenceCollection getCollection(String collectionId) {
        if (StringUtils.isEmpty(collectionId)) {
            return null;
        }

        return new Select()
                .from(SentenceCollection.class)
                .where("collection_id = ?", collectionId)
                .executeSingle();
    }

    public Language getLanguage(String languageID) {
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

    public SentenceCollection reloadCollectionCounter(SentenceCollection collection) {
        DBG.todo("Threads");

        int rows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ?",
                new String[] {collection.getCollectionID()});
        int todoRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.getCollectionID(), String.valueOf(SentenceStatus.TODO.getStatus())});
        int againRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.getCollectionID(), String.valueOf(SentenceStatus.REPEAT.getStatus())});
        int doneRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.getCollectionID(), String.valueOf(SentenceStatus.DONE.getStatus())});
        int ignoreRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.getCollectionID(), String.valueOf(SentenceStatus.IGNORE.getStatus())});
        int skippedRows = SQLiteUtils.intQuery(
                "select count(*) from sentence where collection_id = ? and status = ?",
                new String[] {collection.getCollectionID(), String.valueOf(SentenceStatus.SKIPPED.getStatus())});
        int annotationCount = SQLiteUtils.intQuery(
                "select count(*) from annotation where collection_id = ?",
                new String[] {collection.getCollectionID()});

        if (todoRows > SentenceCollection.MAX_SENTENCES) {
            todoRows = SentenceCollection.MAX_SENTENCES;
        }
        todoRows = todoRows - doneRows - skippedRows;

        collection.count = rows;
        collection.todoCount = todoRows;
        collection.repeatCount = againRows;
        collection.doneCount = doneRows;
        collection.ignoreCount = ignoreRows;
        collection.annotationCount = annotationCount;
        collection.skippedCount = skippedRows;
        collection.save();

        return collection;
    }

    public List<Sentence> getRandomSentences(SentenceCollection collection) {
        return new Select()
                .from(Sentence.class)
                .where("collection_id = ?", collection.collectionID)
                .orderBy("random()")
                .limit(100)
                .execute();
    }

    public void removeCollectionSentences(SentenceCollection collection) {
        new Delete()
                .from(Sentence.class)
                .where("collection_id=?", collection.getCollectionID())
                .execute();
        reloadCollectionCounter(collection);
    }

    public Map<String, Language> getLanguagesByLanguageID() {
        HashMap<String, Language> res = new HashMap<>();
        for (Language language : getLanguages()) {
            res.put(language.getLanguageId(), language);
        }
        return res;
    }

    public Sentence getSentenceBySentenceId(String sentenceId) {
        return new Select()
                .from(Sentence.class)
                .where("sentence_id = ?", sentenceId)
                .executeSingle();
    }

    public From getSentencesByCollection(String collectionId, String filter) {
        From res = new Select()
                .from(Sentence.class)
                .where("collection_id=?", collectionId);
        if (!StringUtils.isEmpty(filter)) {
            SqlFilterUtils.addFilter(res, new String[]{"known", "target"}, filter);
        }
        res.orderBy("complexity");
        return res;
    }

    public From getSentencesByCollectionAndStatus(String collectionId, int sentenceStatus, String filter) {
        From res = new Select()
                .from(Sentence.class)
                .where("(collection_id=? and status=?)", collectionId, sentenceStatus);
        if (!StringUtils.isEmpty(filter)) {
            SqlFilterUtils.addFilter(res, new String[]{"known", "target"}, filter);
        }
        res.orderBy("complexity");
        return res;
    }

    public SentenceHistory getLatestSentenceHistory() {
        return new Select()
                .from(SentenceHistory.class)
                .orderBy("created desc")
                .executeSingle();
    }
}
