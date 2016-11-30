package info.puzz.a10000sentences.importer;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class TatoebaSentence {
    int id;
    String text;
}
