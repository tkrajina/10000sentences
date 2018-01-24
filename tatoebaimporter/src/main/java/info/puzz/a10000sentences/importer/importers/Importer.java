package info.puzz.a10000sentences.importer.importers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.puzz.a10000sentences.apimodels.CollectionType;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.importer.WordCounter;
import info.puzz.a10000sentences.importer.WordUtils;
import info.puzz.a10000sentences.language.Languages;

public abstract class Importer {

    protected static final String RAW_FILES_PATH = "raw_files";

    public static final int MAX_SENTENCES_NO = 12_000;

    final String knownLanguageAbbrev3;
    final String targetLanguageAbbrev3;
    final LanguageVO knownLang;
    final LanguageVO targetLang;

    public Importer(String knownLanguageAbbrev3, String targetLanguageAbbrev3) {
        this.knownLanguageAbbrev3 = knownLanguageAbbrev3;
        this.targetLanguageAbbrev3 = targetLanguageAbbrev3;
        knownLang = Languages.getLanguageByAbbrev(knownLanguageAbbrev3);
        targetLang = Languages.getLanguageByAbbrev(targetLanguageAbbrev3);

    }

    public abstract CollectionType getType();
    public abstract void importCollection(SentenceWriter writer) throws Exception;

    protected void calculateComplexityAndReorder(WordCounter wordCounter, List<SentenceVO> sentences) {
        for (SentenceVO sentence : sentences) {
            calculateSentenceComplexity(sentence, wordCounter);
        }
        Collections.sort(sentences, new Comparator<SentenceVO>() {
            @Override
            public int compare(SentenceVO s1, SentenceVO s2) {
                return Float.compare(s1.getComplexity(), s2.getComplexity());
            }
        });
    }

    protected static void calculateSentenceComplexity(SentenceVO sentence, WordCounter wordCounter) {
        List<String> sentenceWords = WordUtils.getWords(sentence.getTargetSentence());

        int[] counters = new int[sentenceWords.size()];
        for (int i = 0; i < sentenceWords.size(); i++) {
            counters[i] = wordCounter.getWordCount(sentenceWords.get(i));
        }

        Arrays.sort(counters);
        if (counters.length > 3) {
            // First are the less frequent words, ignore the 30% more frequent:
            counters = Arrays.copyOfRange(counters, 0, (int) (counters.length * 0.70));
        }

        int sum = 0;
        for (int counter : counters) {
            sum += counter;
        }
        float avg = sum / ((float) counters.length);

        sentence.setComplexity(- (float) (avg * Math.pow(0.95, sentenceWords.size())));
    }

}
