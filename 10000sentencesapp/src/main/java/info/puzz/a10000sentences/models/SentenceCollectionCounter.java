package info.puzz.a10000sentences.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * A {@link SentenceCollection} can be deleted, but we still need to store counters (for stats). This
 * entity will remain in the database even after a collection is deleted.
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "sentence_collection_counter")
public class SentenceCollectionCounter extends Model {

    @Column(name = "collection_id", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String collectionID;

    @Column(name = "target_lang")
    public String targetLanguage;

    @Column(name = "done_count")
    public int doneCount;

    public SentenceCollectionCounter(SentenceCollection collection) {
        this.collectionID = collection.collectionID;
        this.targetLanguage = collection.targetLanguage;
        this.doneCount = collection.doneCount;
    }

    public SentenceCollectionCounter() {
    }
}
