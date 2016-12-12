package info.puzz.a10000sentences.apimodels;

public class CourseVO {
    String learningLangId;
    String knownLangId;

    public String getLearningLangId() {
        return learningLangId;
    }

    public CourseVO setLearningLangId(String learningLangId) {
        this.learningLangId = learningLangId;
        return this;
    }

    public String getKnownLangId() {
        return knownLangId;
    }

    public CourseVO setKnownLangId(String knownLangId) {
        this.knownLangId = knownLangId;
        return this;
    }
}
