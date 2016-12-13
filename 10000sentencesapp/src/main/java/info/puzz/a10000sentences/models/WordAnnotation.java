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
@Table(name = "word_annotation")
public class WordAnnotation extends Model {

    public WordAnnotation() {
        super();
    }

    public WordAnnotation(String word, long annotationId) {
        super();
        this.wordAnnotationId = word + "|" + annotationId;
        this.word = word;
        this.annotationId = annotationId;
    }

    @Column(name = "word_annotation_id", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String wordAnnotationId;

    @Column(name = "word")
    public String word;

    @Column(name = "annotation_id")
    public long annotationId;

    @Column(name = "collection_id")
    public String collectionId;

    /**
     * Generated from {@link Annotation}
     */
    @Column(name = "annotation")
    public String annotation;

    /**
     * Generated from {@link Annotation}
     */
    @Column(name = "words")
    public String words;

}
