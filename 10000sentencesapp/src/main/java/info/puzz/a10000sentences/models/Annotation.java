package info.puzz.a10000sentences.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "annotation")
public class Annotation extends Model {
    @Column(name = "annotation")
    public String annotation;

    @Column(name = "collection_id")
    public String collectionId;

    /**
     * This is regenerated every time {@link WordAnnotation} adds/removes a new word for this annotation.
     */
    @Column(name = "words")
    public String words;

    @Column(name = "created", index = true)
    public long created;

    @Column(name = "updated", index = true)
    public long updated;
}
