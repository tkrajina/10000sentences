package info.puzz.a10000sentences.activities;

import android.databinding.BaseObservable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import info.puzz.a10000sentences.Constants;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.utils.StringUtils;
import info.puzz.a10000sentences.utils.WordChunk;
import lombok.Getter;
import temp.DBG;

public class SentenceQuiz extends BaseObservable {

    private final List<WordChunk> chunks;
    private final List<String> vocabChunks;
    private final Sentence sentence;
    private int currentChunk;
    public String[] answers;

    int incorrectAnswersGiven = 0;
    int correctAnswersGiven = 0;

    public SentenceQuiz(Sentence sentence, int answersNo, List<Sentence> randomSentencesForVocab) {
        this.sentence = sentence;
        chunks = StringUtils.getWordChunks(sentence.targetSentence);
        currentChunk = 0;
        answers = new String[answersNo];
        vocabChunks = new ArrayList<>();
        DBG.todo("Check if empty");
        for (Sentence s : randomSentencesForVocab) {
            for (WordChunk wch : StringUtils.getWordChunks(s.targetSentence)) {
                vocabChunks.add(wch.word);
            }
        }
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
                res.append(chunk.chunk.replace(chunk.word, StringUtils.repeat('_', 3)));
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
            ++ correctAnswersGiven;
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
        return 1F * incorrectAnswersGiven / chunks.size() < 1 - Constants.MIN_WORDS_GUESSED;
    }

    public void resetRandomAnswers() {
        if (isFinished()) {
            return;
        }
        List<String> answ = new ArrayList<>();
        answ.add(chunks.get(currentChunk).word);
        for (String vocabChunk : vocabChunks) {
            if (answ.size() < answers.length) {
                if (!answ.contains(vocabChunk)) {
                    answ.add(vocabChunk);
                }
            }
        }
        answ = answ.subList(0, answers.length);
        Collections.shuffle(answ);
        for (int i = 0; i < answers.length; i++) {
            answers[i] = answ.get(i);
        }
    }

    public Sentence getSentence() {
        return sentence;
    }

    public int getIncorrectAnswersGiven() {
        return incorrectAnswersGiven;
    }

    public int getCorrectAnswersGiven() {
        return correctAnswersGiven;
    }
}
