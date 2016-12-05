package info.puzz.a10000sentences.importer;

import java.util.ArrayList;
import java.util.List;

public class WordUtils {

    public static List<String> getWords(String str) {
        ArrayList<String> res = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                currentWord.append(c);
            } else {
                if (currentWord.length() > 0) {
                    res.add(currentWord.toString());
                    currentWord = new StringBuilder();
                }
            }
        }
        if (currentWord.length() > 0) {
            res.add(currentWord.toString());
        }
        return res;
    }

}
