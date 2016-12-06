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
}
