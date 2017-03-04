package info.puzz.a10000sentences.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
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

    /**
     * This field should really be named "order". In default language collections simpler sentences are listed first.
     * But in text collections, sentences are just ordered by using this field.
     * @see SentenceCollection#custom
     */
    @Column(name = "complexity", index = true)
    public float complexity;

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
}
