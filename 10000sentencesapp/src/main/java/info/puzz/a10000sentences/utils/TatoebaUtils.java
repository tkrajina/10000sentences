package info.puzz.a10000sentences.utils;

import info.puzz.a10000sentences.models.Sentence;

public class TatoebaUtils {
    public TatoebaUtils() throws Exception {
        throw new Exception();
    }

    public static String getTatotebaUrl(String sentenceId) {
        if (sentenceId.indexOf('-') >= 0) {
            String[] parts = sentenceId.split("\\-");
            sentenceId = parts[parts.length - 1];
        }
        return "https://tatoeba.org/sentences/show/" + sentenceId;
    }

    public static String getTatotebaUrl(Sentence sentence) {
        return getTatotebaUrl(sentence.sentenceId);
    }
}
