package info.puzz.a10000sentences.utils;

import java.util.ArrayList;
import java.util.List;

public final class TextUtils {

    private TextUtils() throws Exception {
        throw new Exception();
    }

    public static List<String> getSentences(String text) {
        List<String> sentences = new ArrayList<>();

        StringBuilder curr = new StringBuilder();
        boolean prevCharSentenceDelimiter = true;
        for (char c : text.toCharArray()) {
            boolean sentenceDelimiter = isSentenceDelimiter(c);
            if (prevCharSentenceDelimiter && !sentenceDelimiter) {
                String str = curr.toString().trim();
                if (str.length() > 0) {
                    sentences.add(str);
                }
                curr = new StringBuilder();
            }
            curr.append(c);
            prevCharSentenceDelimiter = sentenceDelimiter;
        }
        String str = curr.toString().trim();
        if (str.length() > 0) {
            sentences.add(str);
        }

        return sentences;
    }

    public static boolean isSentenceDelimiter(char c) {
        return c == '.' || c == '!' || c == '?' || c == '\n';
    }

    public static void main(String[] args) {
        String txt = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse auctor fringilla pharetra\n" +
                "Proin aliquam lacinia fermentum. Class aptent taciti sociosqu ad litora torquent per conubia nostra,";
        for (String sentence : getSentences(txt)) {
            System.out.println("Sentence:" + sentence);
        }
    }
}
