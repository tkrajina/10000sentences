package info.puzz.a10000sentences.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
@Table(name = "sentence_collection")
public class SentenceCollection extends Model {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    public static final int MAX_SENTENCES = 10_000;

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

    @Column(name = "repeat_count")
    public int repeatCount;

    @Column(name = "done_count")
    public int doneCount;

    @Column(name = "ignore_count")
    public int ignoreCount;

    @Column(name = "annotation_count")
    public int annotationCount;

    public boolean isDownloaded() {
        return count > 0;
    }

    public String formatProgress() {
        if (isDownloaded()) {
            return formatDoneCount() + " of 10,000 sentences";
        } else {
            return "Not downloaded";
        }
    }

    public String formatCount() {
        return formatCount(count);
    }

    public String formatDoneCount() {
        return formatCount(doneCount);
    }

    public String formatTodoCount() {
        return formatCount(todoCount);
    }

    public String formatIgnoreCount() {
        return formatCount(ignoreCount);
    }

    public String formatRepeatCount() {
        return formatCount(repeatCount);
    }

    public String formatAnnotationCount() {
        return formatCount(annotationCount);
    }

    private String formatCount(int count) {
        if (count <= MAX_SENTENCES) {
            return NUMBER_FORMAT.format(count);
        }
        return NUMBER_FORMAT.format(MAX_SENTENCES) + "+";
    }

}
