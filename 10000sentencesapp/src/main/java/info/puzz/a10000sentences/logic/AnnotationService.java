package info.puzz.a10000sentences.logic;

import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.models.Annotation;

public class AnnotationService {
    private final Dao dao;

    public AnnotationService(Dao dao) {
        this.dao = dao;
    }

    public void saveAnnotation(String word, String annotation) {
    }

    public void addWordToAnnotation(Annotation annotation, String word) {
    }

    public void removeWordToAnnotation(Annotation annotation, String word) {
    }

    private void reloadGeneratedFields(Annotation annotation) {
    }

}
