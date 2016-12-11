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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import info.puzz.a10000sentences.apimodels.InfoVO;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.language.Languages;

public class TatoebaImporter {

    private static final float MAX_SENTENCE_LENGTH = 100;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final int MAX_SENTENCES_NO = 12_000;

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

        System.out.println("Caching links");
        Map<Integer, int[]> links = loadLinks();

        InfoVO info = new InfoVO()
                .setLanguages(Languages.getLanguages())
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "ita"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "epo"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "lat"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "tur"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "ell"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "ron"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "ara"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "heb"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "deu"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "fra"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "rus"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "por"))
                .addSentencesCollection(importSentencesBothWays(links, bucketFiles, "eng", "spa"));

        String infoFilename = Paths.get(bucketFiles, "info.json").toString();
        FileUtils.writeByteArrayToFile(new File(infoFilename), OBJECT_MAPPER.writeValueAsBytes(info));
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
                related = new int[] {-1, -1, -1, -1}; // Max 4 related sentences
                res.put(sentence1, related);
            }

            related_loop:
            for (int i = 0; i < related.length; i++) {
                if (related[i] < 0) {
                    related[i] = sentence2;
                    break related_loop;
                }
            }
        }

        return res;
    }

    private static List<SentenceCollectionVO> importSentencesBothWays(
            Map<Integer, int[]> links,
            String outputDir,
            String lang1, String lang2) throws IOException {
        ArrayList<SentenceCollectionVO> res = new ArrayList<>();
        res.add(importSentences(links, outputDir, lang1, lang2));
        res.add(importSentences(links, outputDir, lang2, lang1));
        System.out.println(res);
        return res;
    }

    private static SentenceCollectionVO importSentences(
            Map<Integer, int[]> links,
            String outputDir,
            String knownLanguageAbbrev3, String targetLanguageAbbrev3) throws IOException {
        long started = System.currentTimeMillis();

        System.out.println(String.format("Processing %s->%s", knownLanguageAbbrev3, targetLanguageAbbrev3));

        LanguageVO knownLanguage = Languages.getLanguageByAbbrev(knownLanguageAbbrev3);
        LanguageVO targetLanguage = Languages.getLanguageByAbbrev(targetLanguageAbbrev3);

        WordCounter wordCounter = new WordCounter();

        Map<Integer, TatoebaSentence> targetLanguageSentences = new HashMap<>();
        Map<Integer, TatoebaSentence> knownLanguageSentences = new HashMap<>();

        {
            FileInputStream fstream = new FileInputStream("tmp_files/sentences_detailed.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                int sentenceId = Integer.parseInt(parts[0]);
                String lang = parts[1];
                String text = parts[2];
                TatoebaSentence sentence = new TatoebaSentence().setId(sentenceId).setText(text);
                if (targetLanguageAbbrev3.equals(lang)) {
                    targetLanguageSentences.put(sentenceId, sentence);
                    wordCounter.countWordsInSentence(sentence.getText());
                }
                if (knownLanguageAbbrev3.equals(lang)) {
                    knownLanguageSentences.put(sentenceId, sentence);
                }
            }
        }
        System.out.println(String.format("Found %d known language sentences", knownLanguageSentences.size()));
        System.out.println(String.format("Found %d target language sentences", targetLanguageSentences.size()));
        System.out.println(String.format("%d distinct words, %d words", wordCounter.size(), wordCounter.count.intValue()));

        HashSet<Integer> sentencesFound = new HashSet<>();

        String outFilename = String.format("%s-%s.csv", knownLanguage.getAbbrev(), targetLanguage.getAbbrev());
        List<SentenceVO> sentences = new ArrayList<>();

        for (Map.Entry<Integer, int[]> e : links.entrySet()) {
            Integer sentence1 = e.getKey();
            Integer sentence2 = e.getValue()[0];
            if (!sentencesFound.contains(sentence1) && !sentencesFound.contains(sentence2)) {
                TatoebaSentence knownSentence = knownLanguageSentences.get(sentence1);
                TatoebaSentence targetSentence = targetLanguageSentences.get(sentence2);
                if (knownSentence != null && targetSentence != null && knownSentence.text.length() < MAX_SENTENCE_LENGTH) {
                    //System.out.println(targetSentence.id + ":" + knownSentence + " <-> " + targetSentence);
                    String id = String.format("%s-%s-%d", knownLanguage.getAbbrev(), targetLanguage.getAbbrev(), targetSentence.id);
                    sentences.add(new SentenceVO()
                            .setSentenceId(id)
                            .setTargetSentenceId(targetSentence.id)
                            .setKnownSentence(knownSentence.text)
                            .setTargetSentence(WordUtils.removeNonspacingChars(targetSentence.text)));
                    sentencesFound.add(sentence1);
                    sentencesFound.add(sentence2);
                }
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
