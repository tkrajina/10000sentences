package info.puzz.a10000sentences.apimodels;

public class SentenceCollectionVO {
    String knownLanguage;
    String targetLanguage;
    String filename;
    int count;

    public String getKnownLanguage() {
        return knownLanguage;
    }

    public SentenceCollectionVO setKnownLanguage(String knownLanguage) {
        this.knownLanguage = knownLanguage;
        return this;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public SentenceCollectionVO setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public SentenceCollectionVO setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public int getCount() {
        return count;
    }

    public SentenceCollectionVO setCount(int count) {
        this.count = count;
        return this;
    }
}
