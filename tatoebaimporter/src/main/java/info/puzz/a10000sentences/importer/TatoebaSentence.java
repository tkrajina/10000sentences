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

    public int getId() {
        return id;
    }

    public TatoebaSentence setId(int id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public TatoebaSentence setText(String text) {
        this.text = text;
        return this;
    }
}
