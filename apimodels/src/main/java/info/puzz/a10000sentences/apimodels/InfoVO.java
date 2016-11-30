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
    List<SenteceCollectionVO> sentences;

    public InfoVO getAddSentencesCollection(SenteceCollectionVO sentenceCollection) {
        if (sentences == null) {
            sentences = new ArrayList<>();
        }
        sentences.add(sentenceCollection);
        return this;
    }
}
