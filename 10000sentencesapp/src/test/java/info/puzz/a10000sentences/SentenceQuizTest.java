package info.puzz.a10000sentences;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import info.puzz.a10000sentences.activities.SentenceQuiz;
import info.puzz.a10000sentences.models.Sentence;

public class SentenceQuizTest {

    @Test
    public void test() {
        List<Sentence> randomSentences = new ArrayList<>();
        randomSentences.add(new Sentence() {{ targetSentence = "jkl fdjkls euio fdhjklds fdsjkl jkl"; }});
        randomSentences.add(new Sentence() {{ targetSentence = "cuxizo jdkl yyyy aaaa"; }});
        SentenceQuiz q = new SentenceQuiz(new Sentence() {{ targetSentence = "ovo je, samo test"; }}, 4, randomSentences);
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
