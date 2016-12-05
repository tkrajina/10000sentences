package info.puzz.a10000sentences.utils;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {
    private StringUtils() throws Exception {
        throw new Exception();
    }

    public static List<WordChunk> getWordChunks(String string) {
        ArrayList<WordChunk> res = new ArrayList<>();
        for (String str : string.split("\\s+")) {
            res.add(new WordChunk(str, getWord(str)));
        }
        return res;
    }

    public static String getWord(String str) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isLetter(ch)) {
                res.append(ch);
            } else {
                res.append(' ');
            }
        }
        return res.toString().replace("\\s+", " ").trim();
    }

    public static void main(String[] args) {
        System.out.println(getWordChunks("ovo... je ,samo. test"));
    }

    public static boolean equals(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        } else if (s1 != null && s2 != null) {
            return s1.equals(s2);
        }
        return false;
    }

    public static boolean equalsIgnoreCase(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        } else if (s1 != null && s2 != null) {
            return s1.toLowerCase().equals(s2.toLowerCase());
        }
        return false;
    }
}
