package info.puzz.a10000sentences.models;

import org.junit.Test;

import info.puzz.a10000sentences.models.Sentence;

import static org.junit.Assert.assertTrue;

public class SentenceTest {

    @Test
    public void getKnownSentences() {
        Sentence sentenceTester = new Sentence();
        sentenceTester.setKnownSentence("testForKnownSentence");
        assertTrue(sentenceTester.getKnownSentence() == "testForKnownSentence");
    }

}