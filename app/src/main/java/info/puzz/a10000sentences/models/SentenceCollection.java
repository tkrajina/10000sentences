package info.puzz.a10000sentences.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
@Table(name = "sentence_collection")
public class SentenceCollection {
    @Column(name = "collection_id", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String collectionID;
}
