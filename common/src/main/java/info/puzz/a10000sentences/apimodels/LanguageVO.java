package info.puzz.a10000sentences.apimodels;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class LanguageVO {
    String abbrev;
    String abbrev3;
    String family;
    String name;
    String nativeName;
    boolean rightToLeft;
}
