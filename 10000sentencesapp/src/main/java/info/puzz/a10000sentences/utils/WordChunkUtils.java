package info.puzz.a10000sentences.utils;

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
            String str = parts[i];
            String word = getWord(str);
            res.add(new WordChunk(str, word));
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
