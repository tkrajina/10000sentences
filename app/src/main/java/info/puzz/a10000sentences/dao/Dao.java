package info.puzz.a10000sentences.dao;

import info.puzz.a10000sentences.models.Language;
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
}
