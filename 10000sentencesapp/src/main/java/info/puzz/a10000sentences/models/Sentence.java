package info.puzz.a10000sentences.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "sentence")
public class Sentence extends Model {

    @Column(name = "sentence_id", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String sentenceId;

    @Column(name = "collection_id", index = true)
    public String collectionId;

    @Column(name = "known")
    public String knownSentence;

    @Column(name = "target")
    public String targetSentence;

    @Column(name = "status")
    public int status = SentenceStatus.TODO.getStatus();

    @Column(name = "complexity", index = true)
    float complexity;

    public String[] getKnownSentences() {
        String[] res = String.valueOf(knownSentence).split("\\|");
        if (res.length == 1) {
            return res;
        }
        for (int i = 0; i < res.length; i++) {
            res[i] += String.format(" [%d/%d]", i+1, res.length);
        }
        return res;
    }

    public String getFirstKnownSentence() {
        return getKnownSentences()[0];
    }

    public SentenceStatus getSentenceStatus() {
        return SentenceStatus.fromStatus(status);
    }

    public String getSentenceId() {
        return sentenceId;
    }

    public Sentence setSentenceId(String sentenceId) {
        this.sentenceId = sentenceId;
        return this;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public Sentence setCollectionId(String collectionId) {
        this.collectionId = collectionId;
        return this;
    }

    public String getKnownSentence() {
        return knownSentence;
    }

    public Sentence setKnownSentence(String knownSentence) {
        this.knownSentence = knownSentence;
        return this;
    }

    public String getTargetSentence() {
        return targetSentence;
    }

    public Sentence setTargetSentence(String targetSentence) {
        this.targetSentence = targetSentence;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Sentence setStatus(int status) {
        this.status = status;
        return this;
    }

    public float getComplexity() {
        return complexity;
    }

    public Sentence setComplexity(float complexity) {
        this.complexity = complexity;
        return this;
    }
}
