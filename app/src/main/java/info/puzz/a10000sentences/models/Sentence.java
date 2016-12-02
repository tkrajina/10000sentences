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

    @Column(name = "collection_id")
    public String collectionId;

    @Column(name = "known")
    public String knownSentence;

    @Column(name = "target")
    public String targetSentence;

    @Column(name = "status")
    public int status = SentenceStatus.TODO.getStatus();

}
