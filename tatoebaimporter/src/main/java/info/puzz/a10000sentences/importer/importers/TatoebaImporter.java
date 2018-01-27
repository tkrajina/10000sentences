package info.puzz.a10000sentences.importer.importers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import info.puzz.a10000sentences.apimodels.CollectionType;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.importer.TatoebaSentence;
import info.puzz.a10000sentences.importer.WordCounter;
import info.puzz.a10000sentences.importer.WordUtils;
import info.puzz.a10000sentences.language.Languages;

public class TatoebaImporter extends Importer {

    private static final char ALTERNATIVE_DELIMITER = '|';

    private static String[][] allLanguagePairs;

    private static HashMap<String, Map<Integer, TatoebaSentence>> sentencesPerLang;
    private static Map<Integer, int[]> links;

    public TatoebaImporter(String fromLang, String toLang, String[][] allLanguagePairs) {
        super(fromLang, toLang);
        this.allLanguagePairs = allLanguagePairs;
    }

    @Override
    public CollectionType getType() {
        return CollectionType.TATOEBA;
    }

    private synchronized static void reloadSentencesIfNeeded() throws Exception {
        if (TatoebaImporter.sentencesPerLang != null) {
            return;
        }

        HashMap<String, Map<Integer, TatoebaSentence>> res = new HashMap<>();

        Set<String> langs = new HashSet<>();
        for (String[] languagePair : allLanguagePairs) {
            for (String language : languagePair) {
                langs.add(language);
            }
        }

        FileInputStream fstream = new FileInputStream(RAW_FILES_PATH + "/sentences_detailed.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\t");
            int sentenceId = Integer.parseInt(parts[0]);
            String lang = parts[1];
            String text = parts[2];

            if (langs.contains(lang)) {
                if (!res.containsKey(lang)) {
                    res.put(lang, new HashMap<Integer, TatoebaSentence>());
                }
                TatoebaSentence sentence = new TatoebaSentence()
                        .setId(sentenceId)
                        .setText(prepareSentenceText(text, lang));
                res.get(lang).put(sentenceId, sentence);
            }
        }

        TatoebaImporter.links = loadLinks();
        TatoebaImporter.sentencesPerLang = res;
    }

    private static Map<Integer, int[]> loadLinks() throws Exception {
        HashMap<Integer, int[]> res = new HashMap<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(RAW_FILES_PATH + "/links.csv")));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\t");
            int sentence1 = Integer.parseInt(parts[0]);
            int sentence2 = Integer.parseInt(parts[1]);

            int[] related = res.get(sentence1);
            if (related == null) {
                res.put(sentence1, new int[] {sentence2});
            } else {
                int[] newRelated = new int[related.length + 1];
                for (int i = 0; i < related.length; i++) {
                    newRelated[i] = related[i];
                }
                newRelated[newRelated.length - 1] = sentence2;
                res.put(sentence1, newRelated);
            }
        }

        return res;
    }

    private static String prepareSentenceText(String text, String lang) {
        if ("ar".equals(lang) || "ara".equals(lang)) {
            return WordUtils.removeNonspacingChars(text).replace(ALTERNATIVE_DELIMITER, ' ');
        }
        return text.replace(ALTERNATIVE_DELIMITER, ' ');
    }

    @Override
    public void importCollection(SentenceWriter writer) throws Exception {

        reloadSentencesIfNeeded();

        long started = System.currentTimeMillis();

        System.out.println(String.format("Processing %s->%s", knownLanguageAbbrev3, targetLanguageAbbrev3));

        LanguageVO knownLanguage = Languages.getLanguageByAbbrev(knownLanguageAbbrev3);
        LanguageVO targetLanguage = Languages.getLanguageByAbbrev(targetLanguageAbbrev3);

        WordCounter wordCounter = new WordCounter();

        Map<Integer, TatoebaSentence> targetLanguageSentences = sentencesPerLang.get(targetLanguageAbbrev3);
        Map<Integer, TatoebaSentence> knownLanguageSentences = sentencesPerLang.get(knownLanguageAbbrev3);

        System.out.println(String.format("Found %d known language sentences", knownLanguageSentences.size()));
        System.out.println(String.format("Found %d target language sentences", targetLanguageSentences.size()));
        System.out.println(String.format("%d distinct words, %d words", wordCounter.size(), wordCounter.getCount().intValue()));

        List<SentenceVO> sentences = new ArrayList<>();

        for (TatoebaSentence targetSentence : targetLanguageSentences.values()) {
            if (targetSentence == null) {
                continue;
            }

            int[] knownSentenceIds = links.get(targetSentence.getId());
            if (knownSentenceIds == null) {
                continue;
            }

            StringBuilder knownSentenceAlternatives = new StringBuilder();
            int alternatives = 0;
            for (int knownSentenceId : knownSentenceIds) {
                TatoebaSentence knownSentence = knownLanguageSentences.get(knownSentenceId);
                if (knownSentence != null) {
                    if (knownSentenceAlternatives.length() > 0) {
                        knownSentenceAlternatives.append(ALTERNATIVE_DELIMITER);
                    }
                    knownSentenceAlternatives.append(knownSentence.getText());
                    ++alternatives;
                }
            }
            if (alternatives > 0) {
                String id = String.format("%s-%s-%d", knownLanguage.getAbbrev(), targetLanguage.getAbbrev(), targetSentence.getId());
                SentenceVO sentence = new SentenceVO()
                        .setSentenceId(id)
                        .setTargetSentenceId(targetSentence.getId())
                        .setKnownSentence(knownSentenceAlternatives.toString())
                        .setTargetSentence(targetSentence.getText());
                wordCounter.countWordsInSentence(sentence, knownLang, targetLang);
                sentences.add(sentence);
            }
        }

        // Order by id, so that older ids are deployed in the database (they are more likely to be
        // without errors:
        Collections.sort(sentences, new Comparator<SentenceVO>() {
            @Override
            public int compare(SentenceVO s1, SentenceVO s2) {
                return s1.getTargetSentenceId() - s2.getTargetSentenceId();
            }
        });
        sentences = sentences.subList(0, Math.min(MAX_SENTENCES_NO, sentences.size()));

        calculateComplexityAndReorder(wordCounter, sentences);

        for (SentenceVO sentence : sentences) {
            writer.writeSentence(sentence);
        }

        System.out.println(String.format("Found %d entences in %ds", sentences.size(), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - started)));
    }

}
