package info.puzz.a10000sentences.models;

public enum SentenceCollectionType {
    DEFAULT(0),
    TEXT(1),

    ;

    private int id;

    SentenceCollectionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
