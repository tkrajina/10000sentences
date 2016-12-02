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
@Table(name = "sentence_collection")
public class SentenceCollection extends Model {
    @Column(name = "collection_id", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String collectionID;

    @Column(name = "known_lang")
    public String knownLanguage;

    @Column(name = "target_lang")
    public String targetLanguage;

    @Column(name = "filename")
    public String filename;

    @Column(name = "count")
    public int count;

    @Column(name = "todo_count")
    public int todoCount;

    @Column(name = "again_count")
    public int againCount;

    @Column(name = "done_count")
    public int doneCount;
}
