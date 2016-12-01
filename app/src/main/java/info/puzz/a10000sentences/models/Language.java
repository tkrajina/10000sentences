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
@Table(name = "language")
public class Language extends Model {
    @Column(name = "language_id", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String languageId;

    @Column(name = "family")
    String family;

    @Column(name = "name")
    String name;

    @Column(name = "native_name")
    String nativeName;

    @Column(name = "rtl")
    boolean rightToLeft;
}