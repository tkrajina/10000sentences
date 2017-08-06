package info.puzz.a10000sentences.importer.newimporter;

import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;

public class EuCorpusImporter extends Importer {

    public EuCorpusImporter(String knownLanguageAbbrev3, String targetLanguageAbbrev3) {
        super(knownLanguageAbbrev3, targetLanguageAbbrev3);
    }

    @Override
    public SentenceCollectionVO importCollection(SentenceWriter writer) throws Exception {
        return null;
    }
}
