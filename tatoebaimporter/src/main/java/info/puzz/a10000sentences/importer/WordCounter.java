package info.puzz.a10000sentences.importer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;

/**
 * Helper for counting words (frequencies) in a collection.
 */
public class WordCounter {

    @Getter
    AtomicInteger count = new AtomicInteger(0);

    final Map<String, AtomicInteger> wordCounter = new HashMap<>();

    public WordCounter() {
    }

    public void countWordsInSentence(String sentence) {
        for (String word : WordUtils.getWords(sentence)) {
            countWord(word);
        }
    }
    
    public void countWord(String word) {
        word = word.toLowerCase();
        count.addAndGet(1);
        AtomicInteger counter = wordCounter.get(word);
        if (counter == null) {
            wordCounter.put(word, new AtomicInteger(1));
        } else {
            counter.addAndGet(1);
        }
    }

    public int getWordCount(String word) {
        word = word.toLowerCase();
        AtomicInteger c = wordCounter.get(word);
        if (c == null) {
            return 0;
        }
        return c.intValue();
    }
    
    public float getWordFrequency(String word) {
        return getWordCount(word) / count.floatValue();
    }

    public int size() {
        return wordCounter.size();
    }
    
    public List<Map.Entry<String, AtomicInteger>> orderedByFrequency() {
        List<Map.Entry<String, AtomicInteger>> res = new ArrayList<>();
        res.addAll(wordCounter.entrySet());
        Collections.sort(res, new Comparator<Map.Entry<String, AtomicInteger>>() {
            @Override
            public int compare(Map.Entry<String, AtomicInteger> entry1, Map.Entry<String, AtomicInteger> entry2) {
                return entry2.getValue().intValue() - entry1.getValue().intValue();
            }
        });
        return res;
    }

}
