package info.puzz.a10000sentences.importer.importers;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import info.puzz.a10000sentences.apimodels.CollectionType;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.importer.WordCounter;

public class EuImporter extends Importer {

    private static Pattern SENTENCE_DELIMITER = Pattern.compile("[\\.\\!\\?](?=\\s+\\p{javaUpperCase})");

    private static Pattern NUMBER_DELIMITER = Pattern.compile("^.*\\d+.*$");

    private static final Pattern numberPattern = Pattern.compile("^\\d+\\.$");
    private final String baseFilename;

    public static void main(String[] args) {
        String str = "Ljudje hočejo spoznati 1. politično ozadje itd. zato moramo biti vključeni. Druga rečenica.";
        String[] parts = SENTENCE_DELIMITER.split(str);
        for (String part : parts) {
            System.out.println(part);
        }
    }

    public EuImporter(String knownLanguageAbbrev3, String targetLanguageAbbrev3, String baseFilename) {
        super(knownLanguageAbbrev3, targetLanguageAbbrev3);
        this.baseFilename = baseFilename;
    }

    @Override
    public CollectionType getType() {
        return CollectionType.EU_CORPUS;
    }

    @Override
    public void importCollection(SentenceWriter writer) throws Exception {

        BufferedReader knownFile = new BufferedReader(new FileReader(String.format("%s/%s.%s", RAW_FILES_PATH, baseFilename, knownLang.getAbbrev())));
        BufferedReader targetFile = new BufferedReader(new FileReader(String.format("%s/%s.%s", RAW_FILES_PATH, baseFilename, targetLang.getAbbrev())));

        WordCounter counter = new WordCounter();

        List<SentenceVO> sentences = new ArrayList<>();
        Set<Integer> knownSenteceHashes = new HashSet<>();

        int ignoredSentences = 0;
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
                            if (!knownSenteceHashes.contains(h) && sentenceOK(s)) {
                                sentences.add(s);
                                counter.countWordsInSentence(s, knownLang, targetLang);
                                knownSenteceHashes.add(h);
                            } else {
                                ignoredSentences += 1;
                            }
                        }
                    }
                }
            }
        }

        System.out.printf("%d sentences ignored\n", ignoredSentences);
        System.out.printf("%d sentence candidates\n", sentences.size());

        calculateComplexityAndReorder(counter, sentences);

        // Let's ignore the 20% most complex
        int max = (int) (sentences.size() * 0.70);
        float oneEvery = max / ((float)MAX_SENTENCES_NO);
        for (float i = 0; i < max; i += oneEvery) {
            writer.writeSentence(sentences.get((int)i));
        }
    }

    private boolean sentenceOK(SentenceVO s) {
        String targ = s.getTargetSentence();
        String known = s.getKnownSentence();

        if (StringUtils.equals(targ, known)) {
            System.out.printf("Same: %s <-> %s\n", targ, known);
            return false;
        }

        int tLen = targ.length();
        int kLen = known.length();
        if (StringUtils.getLevenshteinDistance(targ, known) < 0.2 * (tLen + kLen) / 2.) {
            System.out.printf("Too similar: %s <-> %s\n", targ, known);
            return false;
        }

        if (tLen < 50 && kLen < 50) {
            return true;
        }

        if (Math.max(tLen, kLen) / Math.min(tLen, kLen) > 3) {
            System.out.printf("Nope: %s <-> %s\n", known, targ);
            return false;
        }

        if (NUMBER_DELIMITER.matcher(targ).matches() || NUMBER_DELIMITER.matcher(known).matches()) {
            System.out.printf("Has numbers: %s <-> %s\n", known, targ);
            return false;
        }

        return true;
    }

    private List<SentenceVO> importSentence(String targetLine, String knownLine) {
        String[] targetSentences = getSentences(targetLine);
        String[] knownSentences = getSentences(knownLine);

        ArrayList<SentenceVO> res = new ArrayList<>();

        if (targetSentences.length != knownSentences.length) {
            return res;
        }

        for (int i = 0; i < targetSentences.length; i++) {
            String target = targetSentences[i];
            String known = knownSentences[i];
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

    public String[] getSentences(String text) {
        return SENTENCE_DELIMITER.split(text);
    }

}
