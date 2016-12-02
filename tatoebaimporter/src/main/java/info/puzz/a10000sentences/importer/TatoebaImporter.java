package info.puzz.a10000sentences.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import info.puzz.a10000sentences.apimodels.InfoVO;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;
import info.puzz.a10000sentences.apimodels.SentenceVO;
import info.puzz.a10000sentences.language.Languages;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;

public class TatoebaImporter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

        InfoVO info = new InfoVO()
                .setLanguages(Languages.getLanguages())
                .addSentencesCollection(importSentences(bucketFiles, "eng", "ita"))
                .addSentencesCollection(importSentences(bucketFiles, "eng", "ara"))
                .addSentencesCollection(importSentences(bucketFiles, "eng", "deu"))
                .addSentencesCollection(importSentences(bucketFiles, "eng", "spa"));

        String infoFilename = Paths.get(bucketFiles, "info.json").toString();
        FileUtils.writeByteArrayToFile(new File(infoFilename), OBJECT_MAPPER.writeValueAsBytes(info));
    }

    private static SentenceCollectionVO importSentences(String outputDir, String knownLanguageAbbrev3, String targetLanguageAbbrev3) throws IOException {
        long started = System.currentTimeMillis();

        LanguageVO knownLanguage = Languages.getLanguageByAbbrev(knownLanguageAbbrev3);
        LanguageVO targetLanguage = Languages.getLanguageByAbbrev(targetLanguageAbbrev3);

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
                }
                if (knownLanguageAbbrev3.equals(lang)) {
                    knownLanguageSentences.put(sentenceId, sentence);
                }
            }
        }
        System.out.println(String.format("Found %d known language sentences", knownLanguageSentences.size()));
        System.out.println(String.format("Found %d target language sentences", targetLanguageSentences.size()));

        HashSet<Integer> sentencesFound = new HashSet<>();

        String outFilename = String.format("%s-%s.csv", knownLanguage.getAbbrev(), targetLanguage.getAbbrev());
        List<SentenceVO> sentences = new ArrayList<>();

        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("tmp_files/links.csv")));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                Integer sentence1 = Integer.parseInt(parts[0]);
                Integer sentence2 = Integer.parseInt(parts[1]);
                if (!sentencesFound.contains(sentence1) && !sentencesFound.contains(sentence2)) {
                    TatoebaSentence knownSentence = knownLanguageSentences.get(sentence1);
                    TatoebaSentence targetSentence = targetLanguageSentences.get(sentence2);
                    if (knownSentence != null && targetSentence != null) {
                        //System.out.println(targetSentence.id + ":" + knownSentence + " <-> " + targetSentence);
                        sentences.add(new SentenceVO()
                                .setSentenceId(targetSentence.id)
                                .setKnownSentence(knownSentence.text)
                                .setTargetSentence(targetSentence.text));
                        sentencesFound.add(sentence1);
                        sentencesFound.add(sentence2);
                    }
                }
            }
        }

        Collections.sort(sentences, new Comparator<SentenceVO>() {
            @Override
            public int compare(SentenceVO s1, SentenceVO s2) {
                return s1.getSentenceId() - s2.getSentenceId();
            }
        });

        int totalSentencesIncluded = 0;
        FileOutputStream out = new FileOutputStream(Paths.get(outputDir, outFilename).toString());
        write_loop:
        for (SentenceVO sentence : sentences) {
            out.write((sentence.getSentenceId() + "\t" + sentence.getKnownSentence() + "\t" + sentence.getTargetSentence() + "\n").getBytes("utf-8"));
            totalSentencesIncluded += 1;
            if (totalSentencesIncluded > 15_000) {
                break write_loop;
            }
        }
        out.close();

        System.out.println(String.format("Found %d, but included %d sentences in %ds", sentences.size(), totalSentencesIncluded,
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - started)));
        System.out.println("Results written to: " + outFilename);

        return new SentenceCollectionVO()
                .setKnownLanguage(knownLanguage.getAbbrev())
                .setTargetLanguage(targetLanguage.getAbbrev())
                .setFilename(outFilename);
    }

}
