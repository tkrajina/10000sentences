package info.puzz.a10000sentences.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class WordChunkUtils {

    private static final String SENTENCE_ENDING_INTERPUNCTIONS = ".?!";

    private WordChunkUtils() throws Exception {
        throw new Exception();
    }

    public static List<WordChunk> getWordChunks(String string) {
        string = string.replaceAll("\\s+", " ");

        for (char c : SENTENCE_ENDING_INTERPUNCTIONS.toCharArray()) {
            string = string.replace(" " + c, "" + c);
        }

        ArrayList<WordChunk> res = new ArrayList<>();
        String[] parts = string.split("\\s+");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            String word = getWord(part);
            if (res.size() > 0 && StringUtils.isEmpty(word)) {
                res.get(res.size() - 1).chunk += " " + part;
            } else {
                res.add(new WordChunk(part, word));
            }
        }

        WordChunk firstChunk = res.get(0);
        if (res.size() > 1 && StringUtils.isEmpty(firstChunk.word)) {
            res.remove(0);
            res.get(0).chunk = firstChunk.chunk + " " + res.get(0).chunk;
        }

        return res;
    }

    public static String getWord(String str) {
        int firstCharPos = -1;
        int lastCharPos = -1;
        for (int i = 0; i < str.toCharArray().length; i++) {
            char ch = str.charAt(i);
            if (Character.isLetter(ch) || Character.isDigit(ch)) {
                if (firstCharPos < 0) {
                    firstCharPos = i;
                }
                lastCharPos = i;
            }
        }

        if (firstCharPos >= 0) {
            return str.substring(firstCharPos, lastCharPos + 1);
        }

        return "";
    }

    public static void main(String[] args) {
        System.out.println(getWordChunks("ovo... je ,samo. test"));
    }

}
