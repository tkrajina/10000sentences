package info.puzz.a10000sentences.logic;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.models.Annotation;
import info.puzz.a10000sentences.models.WordAnnotation;
import temp.DBG;

public class AnnotationService {
    private final Dao dao;

    public AnnotationService(Dao dao) {
        this.dao = dao;
    }

    public void addWordToAnnotation(Annotation annotation, String word) {
        if (StringUtils.isEmpty(annotation.collectionId)) {
            throw new Error("No collection");
        }

        word = String.valueOf(word).toLowerCase();

        if (annotation.getId() == null) {
            annotation.created = System.currentTimeMillis();
            annotation.save();
        }
        WordAnnotation wa = new WordAnnotation(word, annotation.getId());
        wa.collectionId = annotation.collectionId;
        wa.save();

        reloadGeneratedFields(annotation);
    }

    public void removeWordToAnnotation(Annotation annotation, WordAnnotation word) {
        word.delete();
        reloadGeneratedFields(annotation);
    }

    public void delete(Annotation annotation) {
        new Delete()
                .from(WordAnnotation.class)
                .where("annotation_id=?", annotation.getId())
                .execute();
        annotation.delete();
    }

    private void reloadGeneratedFields(Annotation annotation) {
        List<WordAnnotation> words = new Select()
                .from(WordAnnotation.class)
                .where("annotation_id=?", annotation.getId())
                .execute();

        Collections.sort(words, new Comparator<WordAnnotation>() {
            @Override
            public int compare(WordAnnotation m1, WordAnnotation m2) {
                return StringUtils.compare(m1.word, m2.word);
            }
        });

        StringBuilder wordList = new StringBuilder();
        for (WordAnnotation word : words) {
            if (wordList.length() > 0) {
                wordList.append(", ");
            }
            wordList.append(String.valueOf(word.word).toLowerCase());
        }

        for (WordAnnotation word : words) {
            word.words = wordList.toString();
            word.annotation = annotation.annotation;
        }

        annotation.words = wordList.toString();

        ActiveAndroid.beginTransaction();
        try {
            annotation.save();
            for (WordAnnotation word : words) {
                word.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public List<Annotation> findAnnotations(String collectionId, String word) {
        List<WordAnnotation> wordAnnotations = new Select()
                .from(WordAnnotation.class)
                .where("word=? and collection_id=?", String.valueOf(word).toLowerCase(), collectionId)
                .execute();
        ArrayList<Annotation> annotations = new ArrayList<>();
        for (WordAnnotation wordAnnotation : wordAnnotations) {
            Annotation annotation = Annotation.load(Annotation.class, wordAnnotation.annotationId);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }
        return annotations;
    }

    public From getAnnotationsSelectByCollectionAndFilter(String collectionId, String text) {
        String likeFilter = getLikeFilter(text);
        return new Select()
                .from(Annotation.class)
                .where("collection_id=? and (annotation like ? or annotation like ?)", collectionId, likeFilter.toString() + "%", "% " + likeFilter + "%")
                .orderBy("created desc");
    }

    public From getAnnotationsSelectBydFilter(String text) {
        String likeFilter = getLikeFilter(text);
        return new Select()
                .from(Annotation.class)
                .where("annotation like ? or annotation like ?", likeFilter.toString() + "%", "% " + likeFilter + "%")
                .orderBy("created desc");
    }

    private String getLikeFilter(String text) {
        StringBuilder likeFilter = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c) || Character.isDigit(c) || Character.isSpaceChar(c)) {
                likeFilter.append(c);
            } else {
                likeFilter.append(' ');
            }
        }
        return likeFilter.toString();
    }

}
