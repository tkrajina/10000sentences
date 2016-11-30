package info.puzz.a10000sentences.importer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TatoebaImporter {
    /*
     wget  http://downloads.tatoeba.org/exports/sentences_detailed.tar.bz2
     bzip2 -d sentences_detailed.tar.bz2
     tar -xvf sentences_detailed.tar

     wget http://downloads.tatoeba.org/exports/links.tar.bz2
     bzip2 -d links.tar.bz2
     tar -xvf links.tar
     */
    public static void main(String[] args) throws Exception {
        long started = System.currentTimeMillis();
        
        Map<Integer, TatoebaSentence> targetLanguageSentences = new HashMap<>();
        Map<Integer, TatoebaSentence> knownLanguageSentences = new HashMap<>();

        String targetLanguage = "ita";
        String knownLanguage = "eng";

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
                if (targetLanguage.equals(lang)) {
                    targetLanguageSentences.put(sentenceId, sentence);
                }
                if (knownLanguage.equals(lang)) {
                    knownLanguageSentences.put(sentenceId, sentence);
                }
            }
        }
        System.out.println(String.format("Found %d known language sentences", knownLanguageSentences.size()));
        System.out.println(String.format("Found %d target language sentences", targetLanguageSentences.size()));

        HashSet<Integer> sentencesFound = new HashSet<>();

        {
            FileInputStream fstream = new FileInputStream("tmp_files/links.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                Integer sentence1 = Integer.parseInt(parts[0]);
                if (!sentencesFound.contains(sentence1)) {
                    Integer sentence2 = Integer.parseInt(parts[1]);
                    TatoebaSentence knownSentence = knownLanguageSentences.get(sentence1);
                    TatoebaSentence targetSentence = targetLanguageSentences.get(sentence2);
                    if (knownSentence != null && targetSentence != null) {
                        System.out.println(targetSentence.id + ":" + knownSentence + " <-> " + targetSentence);
                        sentencesFound.add(sentence1);
                    }
                }
            }
        }

        System.out.println(String.format("Found %d sentences in %ds", sentencesFound.size(), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - started)));
    }
}
