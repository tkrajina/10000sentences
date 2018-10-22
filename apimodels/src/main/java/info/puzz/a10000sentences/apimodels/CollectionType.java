package info.puzz.a10000sentences.apimodels;

import lombok.Getter;

public enum CollectionType {

    TATOEBA("https://tatoeba.org", new CollectionTypeInfo() {
        @Override
        public String getSentenceUrl(String sentenceId) {
            return null;
        }
    }),

    EU_CORPUS("http://www.statmt.org/europarl/", new CollectionTypeInfo() {
        @Override
        public String getSentenceUrl(String sentenceId) {
            return "http://www.statmt.org/europarl/";
        }
    }),

    OPUS_OPENSUBTITLES("http://opus.nlpl.eu", new CollectionTypeInfo() {
        @Override
        public String getSentenceUrl(String sentenceId) {
            return "http://opus.nlpl.eu";
        }
    }),

    ;

    @Getter
    private final String url;
    private final CollectionTypeInfo info;

    CollectionType(String url, CollectionTypeInfo info) {
        this.url = url;
        this.info = info;
    }

    public interface CollectionTypeInfo {
        String getSentenceUrl(String sentenceId);
    }

    public String getSentenceUrl(String sentenceId) {
        return info.getSentenceUrl(sentenceId);
    }

}
