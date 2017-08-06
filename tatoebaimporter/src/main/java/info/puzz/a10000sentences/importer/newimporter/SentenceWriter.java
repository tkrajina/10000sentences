package info.puzz.a10000sentences.importer.newimporter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import info.puzz.a10000sentences.apimodels.SentenceVO;

public class SentenceWriter {

    FileOutputStream out;

    final String filename;

    int counter = 0;

    public SentenceWriter(String filename) throws FileNotFoundException {
        this.filename = filename;
        out = new FileOutputStream(filename);
    }

    public void writeSentence(SentenceVO sentence) throws Exception {
        out.write((sentence.getSentenceId() + "\t" + sentence.getKnownSentence() + "\t" + sentence.getTargetSentence() + "\n").getBytes("utf-8"));
        ++ counter;
    }

    public void close() {
        System.out.println(String.format("%d sentences written to %s", counter, filename));
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
