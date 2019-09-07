package info.puzz.a10000sentences.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.NumberFormat;
import java.util.Locale;

import info.puzz.a10000sentences.apimodels.CollectionType;

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

    @Column(name = "skipped_count")
    public int skippedCount;

    @Column(name = "annotation_count")
    public int annotationCount;

    @Column(name="type")
    public CollectionType type;

    public boolean isDownloaded() {
        return count > 0;
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

    public String formatSkippedCount() {
        return formatCount(skippedCount);
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

    public String getCollectionID() {
        return collectionID;
    }

    public SentenceCollection setCollectionID(String collectionID) {
        this.collectionID = collectionID;
        return this;
    }

    public String getKnownLanguage() {
        return knownLanguage;
    }

    public SentenceCollection setKnownLanguage(String knownLanguage) {
        this.knownLanguage = knownLanguage;
        return this;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public SentenceCollection setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public SentenceCollection setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public int getCount() {
        return count;
    }

    public SentenceCollection setCount(int count) {
        this.count = count;
        return this;
    }

    public int getTodoCount() {
        return todoCount;
    }

    public SentenceCollection setTodoCount(int todoCount) {
        this.todoCount = todoCount;
        return this;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public SentenceCollection setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public SentenceCollection setDoneCount(int doneCount) {
        this.doneCount = doneCount;
        return this;
    }

    public int getIgnoreCount() {
        return ignoreCount;
    }

    public SentenceCollection setIgnoreCount(int ignoreCount) {
        this.ignoreCount = ignoreCount;
        return this;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public SentenceCollection setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
        return this;
    }

    public int getAnnotationCount() {
        return annotationCount;
    }

    public SentenceCollection setAnnotationCount(int annotationCount) {
        this.annotationCount = annotationCount;
        return this;
    }

    public CollectionType getType() {
        return type;
    }

    public SentenceCollection setType(CollectionType type) {
        this.type = type;
        return this;
    }
}
