package info.puzz.a10000sentences.apimodels;

public class LanguageVO {
    String abbrev;
    String abbrev3;
    String family;
    String name;
    String nativeName;
    boolean rightToLeft;

    public String getAbbrev() {
        return abbrev;
    }

    public LanguageVO setAbbrev(String abbrev) {
        this.abbrev = abbrev;
        return this;
    }

    public String getAbbrev3() {
        return abbrev3;
    }

    public LanguageVO setAbbrev3(String abbrev3) {
        this.abbrev3 = abbrev3;
        return this;
    }

    public String getFamily() {
        return family;
    }

    public LanguageVO setFamily(String family) {
        this.family = family;
        return this;
    }

    public String getName() {
        return name;
    }

    public LanguageVO setName(String name) {
        this.name = name;
        return this;
    }

    public String getNativeName() {
        return nativeName;
    }

    public LanguageVO setNativeName(String nativeName) {
        this.nativeName = nativeName;
        return this;
    }

    public boolean isRightToLeft() {
        return rightToLeft;
    }

    public LanguageVO setRightToLeft(boolean rightToLeft) {
        this.rightToLeft = rightToLeft;
        return this;
    }
}
