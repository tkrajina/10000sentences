package info.puzz.a10000sentences.importer.eucorpus;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.importer.WordCounter;
import info.puzz.a10000sentences.importer.newimporter.Importer;
import info.puzz.a10000sentences.importer.newimporter.SentenceWriter;

public class EuImporter extends Importer {

    private static final Pattern numberPattern = Pattern.compile("^\\d+\\.$");

    public EuImporter(String knownLanguageAbbrev3, String targetLanguageAbbrev3) {
        super(knownLanguageAbbrev3, targetLanguageAbbrev3);
    }

    public static void main(String[] args) throws Exception {
        new EuImporter("", "").importCollection(new SentenceWriter("jkl"));
    }

    @Override
    public void importCollection(SentenceWriter writer) throws Exception {
        BufferedReader slFile = new BufferedReader(new FileReader("raw_files/europarl-v7.sl-en.sl"));
        BufferedReader enFile = new BufferedReader(new FileReader("raw_files/europarl-v7.sl-en.en"));

        WordCounter counter = new WordCounter();

        List<SentenceVO> sentences = new ArrayList<>();
        Set<Integer> knownSenteceHashes = new HashSet<>();

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
                        for (SentenceVO s : importSentence(slLine, enLine)) {
                            Integer h = s.getKnownSentence().hashCode();
                            if (!knownSenteceHashes.contains(h)) {
                                sentences.add(s);
                                counter.countWordsInSentence(slLine);
                                knownSenteceHashes.add(h);
                            }
                        }
                    }
                }
            }
        }

        calculateComplexityAndReorder(counter, sentences);

        // Let's ignore the 20% most complex
        int max = (int) (sentences.size() * 0.70);
        float oneEvery = max / ((float)MAX_SENTENCES_NO);
        for (float i = 0; i < max; i += oneEvery) {
            System.out.println((int) i);
            writer.writeSentence(sentences.get((int)i));
        }
    }

    private static List<SentenceVO> importSentence(String slLine, String enLine) {
        List<String> slSentences = getSentences(slLine);
        List<String> enSentences = getSentences(enLine);

        ArrayList<SentenceVO> res = new ArrayList<>();

        if (slSentences.size() != enSentences.size()) {
            return res;
        }

        for (int i = 0; i < slSentences.size(); i++) {
            String sl = slSentences.get(i);
            String en = enSentences.get(i);
            if (sl.length() < 100) {
                System.out.println(sl + " " + en);
                res.add(new SentenceVO()
                        .setSentenceId(String.valueOf(slLine.hashCode()))
                        .setKnownSentence(enLine)
                        .setTargetSentence(slLine));
            }
        }

        return res;
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
