package info.puzz.a10000sentences.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.apache.commons.lang3.StringUtils;

@Table(name = "language")
public class Language extends Model {
    @Column(name = "language_id", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String languageId;

    @Column(name = "family")
    public String family;

    @Column(name = "name")
    public String name;

    @Column(name = "native_name")
    public String nativeName;

    @Column(name = "rtl")
    public boolean rightToLeft;

    public String formatNativeName() {
        if (nativeName.contains(",")) {
            return StringUtils.capitalize(nativeName.split(",")[0]);
        }
        return StringUtils.capitalize(nativeName);
    }
}