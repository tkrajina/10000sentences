package info.puzz.a10000sentences.dao;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.List;

import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;

public class Dao {
    private Dao() throws Exception {
        throw new Exception();
    }

    public static void saveLanguage(Language language) {
        language.save();
    }

    public static void saveCollection(SentenceCollection col) {
        col.save();
    }

    public static void saveSentences(List<Sentence> sentences) {
        ActiveAndroid.beginTransaction();
        try {
            // TODO: Load existing sentences and preserve fields
            for (Sentence sentence : sentences) {
                sentence.save();
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
}
