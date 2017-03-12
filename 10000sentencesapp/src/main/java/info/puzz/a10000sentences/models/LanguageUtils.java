package info.puzz.a10000sentences.models;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class LanguageUtils {
    private LanguageUtils() throws Exception {
        throw new Exception();
    }

    public static List<Language> sortByFamilyAndName(List<Language> languages) {
        Collections.sort(languages, new Comparator<Language>() {
            @Override
            public int compare(Language o1, Language o2) {
                if (StringUtils.equals(o1.family, o2.family)) {
                    return StringUtils.compare(o1.name, o2.name);
                }
                return StringUtils.compare(o1.family, o2.family);
            }
        });
        return languages;
    }
}
