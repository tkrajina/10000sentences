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
    boolean rightToLeft;

    public String formatNativeName(String delimiter) {
        String[] parts;
        if (nativeName.contains(",")) {
            parts = StringUtils.capitalize(nativeName.split(",")[0]).split("\\s+");
        } else {
            parts = StringUtils.capitalize(nativeName).split("\\s+");
        }
        return StringUtils.join(parts, delimiter);
    }

    public String formatNameAndNativeName() {
        if (StringUtils.equals(name, nativeName)) {
            return name;
        }
        return name + " / " + nativeName;
    }

    public String getLanguageId() {
        return languageId;
    }

    public Language setLanguageId(String languageId) {
        this.languageId = languageId;
        return this;
    }

    public String getFamily() {
        return family;
    }

    public Language setFamily(String family) {
        this.family = family;
        return this;
    }

    public String getName() {
        return name;
    }

    public Language setName(String name) {
        this.name = name;
        return this;
    }

    public String getNativeName() {
        return nativeName;
    }

    public Language setNativeName(String nativeName) {
        this.nativeName = nativeName;
        return this;
    }

    public boolean isRightToLeft() {
        return rightToLeft;
    }

    public Language setRightToLeft(boolean rightToLeft) {
        this.rightToLeft = rightToLeft;
        return this;
    }
}