package info.puzz.a10000sentences;

import org.junit.Assert;
import org.junit.Test;

import info.puzz.a10000sentences.activities.SentenceQuiz;
import info.puzz.a10000sentences.models.Sentence;

public class SentenceQuizTest {

    @Test
    public void test() {
        SentenceQuiz q = new SentenceQuiz(new Sentence().setTargetSentence("ovo je, samo test"), 4);
        Assert.assertFalse(q.guessWord("jkljkl"));
        Assert.assertFalse(q.isFinished());
        Assert.assertTrue(q.guessWord("ovo"));
        Assert.assertFalse(q.isFinished());
        Assert.assertTrue(q.guessWord("je"));
        Assert.assertFalse(q.isFinished());
        Assert.assertTrue(q.guessWord("sAMO"));
        Assert.assertFalse(q.isFinished());
        Assert.assertTrue(q.guessWord("test"));
        Assert.assertTrue(q.isFinished());

        Assert.assertFalse(q.guessWord("jkljkljljKLJKLJK"));
        Assert.assertTrue(q.isFinished());
    }

}
