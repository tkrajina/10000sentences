package info.puzz.a10000sentences.apimodels;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class SentenceVO {
    String sentenceId;
    /**
     * This field is not sent to the client, it is used only for ordering when exporting sentences.
     * This field cannot be unique, because the same sentenceId can be part of multiple collections.
     * @see #sentenceId
     */
    int targetSentenceId;
    String knownSentence;
    String targetSentence;

    /**
     * Calculated based on sentence length and words frequency.
     */
    float complexity;

    public String getSentenceId() {
        return sentenceId;
    }

    public SentenceVO setSentenceId(String sentenceId) {
        this.sentenceId = sentenceId;
        return this;
    }

    public int getTargetSentenceId() {
        return targetSentenceId;
    }

    public SentenceVO setTargetSentenceId(int targetSentenceId) {
        this.targetSentenceId = targetSentenceId;
        return this;
    }

    public String getKnownSentence() {
        return knownSentence;
    }

    public SentenceVO setKnownSentence(String knownSentence) {
        this.knownSentence = knownSentence;
        return this;
    }

    public String getTargetSentence() {
        return targetSentence;
    }

    public SentenceVO setTargetSentence(String targetSentence) {
        this.targetSentence = targetSentence;
        return this;
    }

    public float getComplexity() {
        return complexity;
    }

    public SentenceVO setComplexity(float complexity) {
        this.complexity = complexity;
        return this;
    }
}
