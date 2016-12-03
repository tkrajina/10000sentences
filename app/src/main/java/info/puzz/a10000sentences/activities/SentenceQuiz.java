package info.puzz.a10000sentences.activities;

import android.databinding.BaseObservable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.utils.StringUtils;
import info.puzz.a10000sentences.utils.WordChunk;
import temp.DBG;

public class SentenceQuiz extends BaseObservable {

    private final List<WordChunk> chunks;
    private final Sentence sentence;
    private int currentChunk;
    public String[] answers;

    int incorrectAnswersGiven = 0;

    public SentenceQuiz(Sentence sentence, int answersNo) {
        this.sentence = sentence;
        chunks = StringUtils.getWordChunks(sentence.targetSentence);
        currentChunk = 0;
        answers = new String[answersNo];
        resetRandomAnswers();
    }

    public String getQuizSentence() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            if (res.length() > 0) {
                res.append(" ");
            }
            WordChunk chunk = chunks.get(i);
            if (i >= currentChunk) {
                res.append(chunk.chunk.replace(chunk.word, "???"));
            } else {
                res.append(chunk.chunk);
            }
        }
        return res.toString();
    }

    public boolean guessWord(String word) {
        if (isFinished()) {
            return false;
        }
        boolean guessed = StringUtils.equalsIgnoreCase(chunks.get(currentChunk).word, word);
        if (guessed) {
            ++ currentChunk;
            resetRandomAnswers();
            notifyChange();
        } else {
            ++ incorrectAnswersGiven;
        }
        return guessed;
    }

    public boolean isFinished() {
        return currentChunk >= chunks.size();
    }

    public boolean canBeMarkedAsDone() {
        if (!isFinished()) {
            return false;
        }
        DBG.todo();
        return 1F * incorrectAnswersGiven / chunks.size() < 0.3;
    }

    public void resetRandomAnswers() {
        if (isFinished()) {
            return;
        }
        List<String> answ = new ArrayList<>();
        answ.add(chunks.get(currentChunk).word);
        answ.add("aaa");
        answ.add("bbb");
        answ.add("ddd");
        answ.add("eee");
        answ = answ.subList(0, answers.length);
        Collections.shuffle(answ);
        for (int i = 0; i < answers.length; i++) {
            answers[i] = answ.get(i);
        }
    }

    public Sentence getSentence() {
        return sentence;
    }
}
