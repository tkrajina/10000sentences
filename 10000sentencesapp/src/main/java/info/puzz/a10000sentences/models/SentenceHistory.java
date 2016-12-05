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
@Table(name = "sentence_history")
public class SentenceHistory extends Model {

    @Column(name = "sentence_id", index = true)
    public String sentenceId;

    @Column(name = "status")
    public int status;

    @Column(name = "created", index = true)
    public long created;
}
