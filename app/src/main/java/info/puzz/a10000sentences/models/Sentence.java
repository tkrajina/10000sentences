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
    public int sentenceId;

    @Column(name = "text")
    String text;
}
