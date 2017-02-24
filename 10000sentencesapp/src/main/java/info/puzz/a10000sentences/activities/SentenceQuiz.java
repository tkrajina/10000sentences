package info.puzz.a10000sentences.activities;

import android.content.Context;
import android.databinding.BaseObservable;
import android.preference.Preference;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.puzz.a10000sentences.Preferences;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.utils.WordChunk;
import info.puzz.a10000sentences.utils.WordChunkUtils;
import temp.DBG;

public class SentenceQuiz extends BaseObservable {

    private final List<WordChunk> chunks;
    private final List<String> vocabChunks;
    private final Sentence sentence;
    private int currentChunk;
    public String[] answers;

    int incorrectAnswersGiven = 0;
    int correctAnswersGiven = 0;
    private int currentKnownSentenceAlternative = 0;

    public SentenceQuiz(Sentence sentence, int answersNo, List<Sentence> randomSentencesForVocab) {
        this.sentence = sentence;
        chunks = WordChunkUtils.getWordChunks(sentence.targetSentence);
        currentChunk = 0;
        answers = new String[answersNo];
        vocabChunks = new ArrayList<>();
        DBG.todo("Check if empty");
        for (Sentence s : randomSentencesForVocab) {
            for (WordChunk wch : WordChunkUtils.getWordChunks(s.targetSentence)) {
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
    
    public String getKnownSentence() {
        String[] knownSentences = sentence.getKnownSentences();
        String res = knownSentences[currentKnownSentenceAlternative % knownSentences.length];
        if (knownSentences.length > 1) {
            res += "  {fa-refresh}";
        }
        return res;
    }

    public void nextKnownSentenceAlternative() {
        ++ currentKnownSentenceAlternative;
        notifyChange();
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

    public boolean canBeMarkedAsDone(Context context) {
        if (!isFinished()) {
            return false;
        }
        return 1F * incorrectAnswersGiven / chunks.size() < 1 - Preferences.getMinCorrectWords(context) / 100F;
    }

    public void resetRandomAnswers() {
        if (isFinished()) {
            return;
        }

        Set<String> answersSet = new HashSet<>();
        for (String vocabChunk : vocabChunks) {
            vocabChunk = vocabChunk.toLowerCase();
            answersSet.add(vocabChunk);
        }

        List<String> answersList = new ArrayList<>();
        answersList.addAll(answersSet);
        Collections.shuffle(answersList);

        answersList = answersList.subList(0, answers.length - 1);
        answersList.add(chunks.get(currentChunk).word.toLowerCase());

        Collections.shuffle(answersList);

        for (int i = 0; i < answers.length; i++) {
            answers[i] = answersList.get(i);
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
