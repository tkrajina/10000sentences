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

    public InfoVO addSentencesCollection(SentenceCollectionVO sentenceCollection) {
        if (this.sentenceCollections == null) {
            this.sentenceCollections = new ArrayList<>();
        }
        this.sentenceCollections.add(sentenceCollection);
        return this;
    }


    public List<LanguageVO> getLanguages() {
        return languages;
    }

    public InfoVO setLanguages(List<LanguageVO> languages) {
        this.languages = languages;
        return this;
    }

    public List<SentenceCollectionVO> getSentenceCollections() {
        return sentenceCollections;
    }

    public InfoVO setSentenceCollections(List<SentenceCollectionVO> sentenceCollections) {
        this.sentenceCollections = sentenceCollections;
        return this;
    }
}
