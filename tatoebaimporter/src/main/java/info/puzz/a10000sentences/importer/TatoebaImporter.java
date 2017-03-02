package info.puzz.a10000sentences.importer;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import info.puzz.a10000sentences.apimodels.InfoVO;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.language.Languages;

public class TatoebaImporter {

    private static final float MAX_SENTENCE_LENGTH = 80;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final int MAX_SENTENCES_NO = 12_000;

    private static final char ALTERNATIVE_DELIMITER = '|';

    /*
     wget  http://downloads.tatoeba.org/exports/sentences_detailed.tar.bz2
     bzip2 -d sentences_detailed.tar.bz2
     tar -xvf sentences_detailed.tar

     wget http://downloads.tatoeba.org/exports/links.tar.bz2
     bzip2 -d links.tar.bz2
     tar -xvf links.tar
     */
    public static void main(String[] args) throws Exception {
        String bucketFiles = "bucket_files";
        new File(bucketFiles).mkdirs();

        String[][] languagePairs = new String[][]{
                //new String[] {"pes", "eng"},
                new String[] {"nob", "eng"},
                new String[] {"ces", "eng"},
                new String[] {"mkd", "eng"},
                new String[] {"ces", "eng"},
                new String[] {"bul", "eng"},
                new String[] {"srp", "eng"},
                new String[] {"dan", "eng"},
                new String[] {"swe", "eng"},
                new String[] {"ukr", "eng"},
                new String[] {"nld", "eng"},
                new String[] {"fin", "eng"},
                new String[] {"mkd", "eng"},
                new String[] {"hun", "eng"},
                new String[] {"pol", "eng"},
                new String[] {"ita", "eng"},
                new String[] {"epo", "eng"},
                new String[] {"lat", "eng"},
                new String[] {"tur", "eng"},
                new String[] {"ell", "eng"},
                new String[] {"ron", "eng"},
                new String[] {"ara", "eng"},
                new String[] {"heb", "eng"},
                new String[] {"deu", "eng"},
                new String[] {"fra", "eng"},
                new String[] {"rus", "eng"},
                new String[] {"por", "eng"},
                new String[] {"spa", "eng"},

                // Nonenglish collections:
                new String[] {"spa", "fra"},
        };

        System.out.println("Caching links");
        Map<Integer, int[]> links = loadLinks();
        System.out.println("Loading sentences");
        Map<String, Map<Integer, TatoebaSentence>> sentencesPerLang = loadSentencesPerLanguage(languagePairs);

        InfoVO info = new InfoVO()
                .setLanguages(Languages.getLanguages());

        for (String[] languagePair : languagePairs) {
            info.addSentencesCollection(importSentencesBothWays(links, sentencesPerLang, bucketFiles, languagePair[0], languagePair[1]));
        }

        String infoFilename = Paths.get(bucketFiles, "info.json").toString();
        FileUtils.writeByteArrayToFile(new File(infoFilename), OBJECT_MAPPER.writeValueAsBytes(info));

        for (SentenceCollectionVO col : info.getSentenceCollections()) {
            LanguageVO knownLang = Languages.getLanguageByAbbrev(col.getKnownLanguage());
            LanguageVO targetLang = Languages.getLanguageByAbbrev(col.getTargetLanguage());
            System.out.println(String.format("%s (for %s speakers): %d sentences", targetLang.getName(), knownLang.getName(), col.getCount()));
        }
    }

    private static Map<String, Map<Integer, TatoebaSentence>> loadSentencesPerLanguage(String[][] languagePairs) throws Exception {
        HashMap<String, Map<Integer, TatoebaSentence>> res = new HashMap<>();

        Set<String> langs = new HashSet<>();
        for (String[] languagePair : languagePairs) {
            for (String language : languagePair) {
                langs.add(language);
            }
        }

        FileInputStream fstream = new FileInputStream("tmp_files/sentences_detailed.csv");
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

        return res;
    }

    private static String prepareSentenceText(String text, String lang) {
        if ("ar".equals(lang) || "ara".equals(lang)) {
            return WordUtils.removeNonspacingChars(text).replace(ALTERNATIVE_DELIMITER, ' ');
        }
        return text.replace(ALTERNATIVE_DELIMITER, ' ');
    }

    private static Map<Integer, int[]> loadLinks() throws Exception {
        HashMap<Integer, int[]> res = new HashMap<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("tmp_files/links.csv")));
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

    private static List<SentenceCollectionVO> importSentencesBothWays(
            Map<Integer, int[]> links,
            Map<String,
            Map<Integer, TatoebaSentence>> sentencesPerLang, String outputDir,
            String lang1, String lang2) throws IOException {
        ArrayList<SentenceCollectionVO> res = new ArrayList<>();
        res.add(importSentences(links, sentencesPerLang, outputDir, lang1, lang2));
        res.add(importSentences(links, sentencesPerLang, outputDir, lang2, lang1));
        System.out.println(res);
        return res;
    }

    private static SentenceCollectionVO importSentences(
            Map<Integer, int[]> links,
            Map<String, Map<Integer, TatoebaSentence>> sentencesPerLang,
            String outputDir,
            String knownLanguageAbbrev3, String targetLanguageAbbrev3) throws IOException {
        long started = System.currentTimeMillis();

        System.out.println(String.format("Processing %s->%s", knownLanguageAbbrev3, targetLanguageAbbrev3));

        LanguageVO knownLanguage = Languages.getLanguageByAbbrev(knownLanguageAbbrev3);
        LanguageVO targetLanguage = Languages.getLanguageByAbbrev(targetLanguageAbbrev3);

        WordCounter wordCounter = new WordCounter();

        Map<Integer, TatoebaSentence> targetLanguageSentences = sentencesPerLang.get(targetLanguageAbbrev3);
        Map<Integer, TatoebaSentence> knownLanguageSentences = sentencesPerLang.get(knownLanguageAbbrev3);

        for (TatoebaSentence sentence : targetLanguageSentences.values()) {
            wordCounter.countWordsInSentence(sentence.getText());
        }
        System.out.println(String.format("Found %d known language sentences", knownLanguageSentences.size()));
        System.out.println(String.format("Found %d target language sentences", targetLanguageSentences.size()));
        System.out.println(String.format("%d distinct words, %d words", wordCounter.size(), wordCounter.count.intValue()));

        String outFilename = String.format("%s-%s.csv", knownLanguage.getAbbrev(), targetLanguage.getAbbrev());
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
                    ++ alternatives;
                }
            }
            if (alternatives > 0) {
                String id = String.format("%s-%s-%d", knownLanguage.getAbbrev(), targetLanguage.getAbbrev(), targetSentence.id);
                sentences.add(new SentenceVO()
                        .setSentenceId(id)
                        .setTargetSentenceId(targetSentence.id)
                        .setKnownSentence(knownSentenceAlternatives.toString())
                        .setTargetSentence(targetSentence.getText()));
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

        for (SentenceVO sentence : sentences) {
            calculateSentenceComplexity(sentence, wordCounter);
        }
        Collections.sort(sentences, new Comparator<SentenceVO>() {
            @Override
            public int compare(SentenceVO s1, SentenceVO s2) {
                return Float.compare(s1.getComplexity(), s2.getComplexity());
            }
        });

        FileOutputStream out = new FileOutputStream(Paths.get(outputDir, outFilename).toString());
        for (SentenceVO sentence : sentences) {
            out.write((sentence.getSentenceId() + "\t" + sentence.getKnownSentence() + "\t" + sentence.getTargetSentence() + "\n").getBytes("utf-8"));
        }
        out.close();

        System.out.println(String.format("Found %d entences in %ds", sentences.size(), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - started)));
        System.out.println("Results written to: " + outFilename);

        return new SentenceCollectionVO()
                .setKnownLanguage(knownLanguage.getAbbrev())
                .setTargetLanguage(targetLanguage.getAbbrev())
                .setCount(sentences.size())
                .setFilename(outFilename);
    }

    private static void calculateSentenceComplexity(SentenceVO sentence, WordCounter wordCounter) {
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
