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

    /** New status */
    @Column(name = "status")
    public int status;

    @Column(name = "previous_status")
    public int previousStatus;

    /**
     * Time spend on this quiz
     */
    @Column(name = "time")
    public int time;

    @Column(name = "todo_count")
    public int todoCount;

    @Column(name = "repeat_count")
    public int repeatCount;

    @Column(name = "done_count")
    public int doneCount;

    @Column(name = "ignore_count")
    public int ignoreCount;

    @Column(name = "created", index = true)
    public long created;
}
