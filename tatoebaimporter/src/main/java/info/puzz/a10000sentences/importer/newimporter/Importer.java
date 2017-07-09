package info.puzz.a10000sentences.importer.newimporter;

import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;

public abstract class Importer {

    public static final int MAX_SENTENCES_NO = 12_000;

    final String knownLanguageAbbrev3;
    final String targetLanguageAbbrev3;

    public Importer(String knownLanguageAbbrev3, String targetLanguageAbbrev3) {
        this.knownLanguageAbbrev3 = knownLanguageAbbrev3;
        this.targetLanguageAbbrev3 = targetLanguageAbbrev3;
    }

    public abstract SentenceCollectionVO importCollection() throws Exception;
}
