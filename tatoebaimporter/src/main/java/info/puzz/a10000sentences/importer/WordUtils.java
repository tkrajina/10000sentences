package info.puzz.a10000sentences.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordUtils {

    public static List<String> getWords(String str) {
        ArrayList<String> res = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c) || Character.isDigit(c)) {
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

    /**
     * Removes nonspacing chars. Those are, for example accents used in arabic, but <strong>not</strong>
     * diacritics used in Croatian (šđčćž...) or Italian accents (éì...).
     */
    public static String removeNonspacingChars(String s) {
        StringBuilder res = new StringBuilder();
        Pattern p = Pattern.compile("[^\\p{Mn}]");
        Matcher m = p.matcher(s);
        while (m.find()) {
            res.append(m.group());
        }
        m.reset();
        return res.toString();
    }

    public static void main(String[] args) {
        System.out.println(removeNonspacingChars("عَسَى أَن يَهْدِيَنِ رَبِّي لِأَقْرَبَ مِنْ هَذَا رَشَدًا"));
        System.out.println(removeNonspacingChars("Što bi to sad bilo šđčćžŠĐČĆŽ"));
        System.out.println(removeNonspacingChars("éí"));
    }
}
