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
public class WordAnnotation extends Model {

    @Column(name = "annotation")
    private String word;

    @Column(name = "annotation_id")
    private int annotationId;

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
