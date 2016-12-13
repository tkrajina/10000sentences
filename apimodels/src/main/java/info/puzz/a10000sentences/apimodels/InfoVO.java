package info.puzz.a10000sentences.apimodels;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class InfoVO {
    List<LanguageVO> languages;
    List<SentenceCollectionVO> sentenceCollections;

    public InfoVO() {
        super();
    }

    public InfoVO addSentencesCollection(List<SentenceCollectionVO> sentenceCollections) {
        if (this.sentenceCollections == null) {
            this.sentenceCollections = new ArrayList<>();
        }
        this.sentenceCollections.addAll(sentenceCollections);
        return this;
    }

}
