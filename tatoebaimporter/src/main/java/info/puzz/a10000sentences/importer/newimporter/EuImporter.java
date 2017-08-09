package info.puzz.a10000sentences.importer.newimporter;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import info.puzz.a10000sentences.apimodels.CollectionType;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.importer.WordCounter;
import info.puzz.a10000sentences.language.Languages;

public class EuImporter extends Importer {

    private static Pattern SENTENCE_DELIMITER = Pattern.compile("[\\.\\!\\?](?=\\s+\\p{javaUpperCase})");

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
                                counter.countWordsInSentence(s, knownLang, targetLang);
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
