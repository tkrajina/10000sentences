package info.puzz.a10000sentences.utils;

import junit.framework.Assert;

import org.apache.commons.lang3.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public final class StringUtilsTest {
    @Test
    public void testRomanianWithDash() {
        String sentence = "L-a trezit.";
        List<WordChunk> chunks = StringUtils.getWordChunks(sentence);
        Assert.assertEquals(2, chunks.size());
        Assert.assertEquals("L-a", chunks.get(0).word);
        Assert.assertEquals("trezit", chunks.get(1).word);
    }

    @Test
    public void testSentenceWithNumbers() {
        String sentence = "L-a 123 trezit.";
        List<WordChunk> chunks = StringUtils.getWordChunks(sentence);
        Assert.assertEquals(3, chunks.size());
        Assert.assertEquals("L-a", chunks.get(0).word);
        Assert.assertEquals("123", chunks.get(1).word);
        Assert.assertEquals("trezit", chunks.get(2).word);
    }

    @Test
    public void testSpaceBeforeQuestionMark() {
        String sentence = "Je li ?";
        List<WordChunk> chunks = StringUtils.getWordChunks(sentence);
        Assert.assertEquals(2, chunks.size());
        Assert.assertEquals("Je", chunks.get(0).word);
        Assert.assertEquals("Je", chunks.get(0).chunk);
        Assert.assertEquals("li", chunks.get(1).word);
        Assert.assertEquals("li?", chunks.get(1).chunk);
    }
}
