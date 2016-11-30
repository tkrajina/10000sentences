package info.puzz.a10000sentences;

import junit.framework.Assert;

import java.util.List;

public class SentenceQuiz {
    private final List<WordChunk> chunks;
    private int currentChunk;

    public SentenceQuiz(String sentence) {
        chunks = StringUtils.getWordChunks(sentence);
        currentChunk = 0;
    }

    public boolean guessWord(String word) {
        if (isFinished()) {
            return false;
        }
        boolean guessed = StringUtils.equalsIgnoreCase(chunks.get(currentChunk).word, word);
        if (guessed) {
            currentChunk += 1;
        }
        return guessed;
    }

    public boolean isFinished() {
        return currentChunk >= chunks.size();
    }

}
