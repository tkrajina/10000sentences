package info.puzz.a10000sentences.importer.newimporter;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.importer.WordCounter;
import info.puzz.a10000sentences.language.Languages;

public class EuImporter extends Importer {

    private static final Pattern numberPattern = Pattern.compile("^\\d+\\.$");
    private final String baseFilename;

    public EuImporter(String knownLanguageAbbrev3, String targetLanguageAbbrev3, String baseFilename) {
        super(knownLanguageAbbrev3, targetLanguageAbbrev3);
        this.baseFilename = baseFilename;
    }

    @Override
    public void importCollection(SentenceWriter writer) throws Exception {

        BufferedReader knownFile = new BufferedReader(new FileReader(String.format("%s/%s.%s", RAW_FILES_PATH, baseFilename, knownLang.getAbbrev())));
        BufferedReader targetFile = new BufferedReader(new FileReader(String.format("%s/%s.%s", RAW_FILES_PATH, baseFilename, targetLang.getAbbrev())));

        WordCounter counter = new WordCounter();

        List<SentenceVO> sentences = new ArrayList<>();
        Set<Integer> knownSenteceHashes = new HashSet<>();

        String targetLine, knownLine;
        while (true) {
            targetLine = targetFile.readLine();
            knownLine = knownFile.readLine();
            if (targetLine == null && knownLine == null) {
                break;
            }
            if (StringUtils.isNotEmpty(targetLine) && StringUtils.isNotEmpty(knownLine)) {
                if (numberPattern.matcher(targetLine).matches()) {
                    //
                } else if (Character.isUpperCase(targetLine.charAt(0)) && Character.isUpperCase(knownLine.charAt(0))) {
                    if (targetLine.indexOf(":") >= 0 || targetLine.indexOf("(") >= 0) {

                    } else {
                        for (SentenceVO s : importSentence(targetLine, knownLine)) {
                            Integer h = s.getKnownSentence().hashCode();
                            if (!knownSenteceHashes.contains(h)) {
                                sentences.add(s);
                                counter.countWordsInSentence(targetLine);
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

    private List<SentenceVO> importSentence(String targetLine, String knownLine) {
        List<String> targetSentences = getSentences(targetLine);
        List<String> knownSentences = getSentences(knownLine);

        ArrayList<SentenceVO> res = new ArrayList<>();

        if (targetSentences.size() != knownSentences.size()) {
            return res;
        }

        for (int i = 0; i < targetSentences.size(); i++) {
            String target = targetSentences.get(i);
            String known = knownSentences.get(i);
            if (target.length() < 100) {
                //System.out.println(sl + " " + en);
                String id = String.format("%s-%s-%d", knownLang.getAbbrev(), targetLang.getAbbrev(), target.hashCode());
                res.add(new SentenceVO()
                        .setSentenceId(String.valueOf(id))
                        .setKnownSentence(known)
                        .setTargetSentence(target));
            }
        }

        return res;
    }

    public List<String> getSentences(String text) {
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
