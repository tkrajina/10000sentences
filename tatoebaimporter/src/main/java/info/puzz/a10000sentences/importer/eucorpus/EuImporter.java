package info.puzz.a10000sentences.importer.eucorpus;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EuImporter {

    private static final Pattern numberPattern = Pattern.compile("^\\d+\\.$");

    public static void main(String[] args) throws Exception {
        BufferedReader slFile = new BufferedReader(new FileReader("/Users/puzz/projects/10000sentences/tmp_eurofiles/europarl-v7.sl-en.sl"));
        BufferedReader enFile = new BufferedReader(new FileReader("/Users/puzz/projects/10000sentences/tmp_eurofiles/europarl-v7.sl-en.en"));

        int imported = 0;

        String slLine, enLine;
        while (true) {
            slLine = slFile.readLine();
            enLine = enFile.readLine();
            if (slLine == null && enLine == null) {
                break;
            }
            if (StringUtils.isNotEmpty(slLine) && StringUtils.isNotEmpty(enLine)) {
                if (numberPattern.matcher(slLine).matches()) {
                    //
                } else if (Character.isUpperCase(slLine.charAt(0)) && Character.isUpperCase(enLine.charAt(0))) {
                    if (slLine.indexOf(":") >= 0 || slLine.indexOf("(") >= 0) {

                    } else {
                        imported += importSentence(slLine, enLine);
                    }
                }
            }
        }

        System.out.println("Imported:" + imported);
    }

    private static int importSentence(String slLine, String enLine) {
        List<String> slSentences = getSentences(slLine);
        List<String> enSentences = getSentences(enLine);

        if (slSentences.size() != enSentences.size()) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < slSentences.size(); i++) {
            String sl = slSentences.get(i);
            String en = enSentences.get(i);
            if (sl.length() < 100) {
                System.out.println(sl + " " + en);
                count += 1;
            }
        }

        return count;
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
}
