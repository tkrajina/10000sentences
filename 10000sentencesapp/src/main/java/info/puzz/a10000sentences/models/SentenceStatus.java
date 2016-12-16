package info.puzz.a10000sentences.models;

import info.puzz.a10000sentences.R;

public enum SentenceStatus {

    TODO(0, R.string.todo, R.string.todo_desc, android.R.color.primary_text_light),
    REPEAT(1, R.string.retry_again, R.string.retry_again_desc, R.color.error),
    DONE(2, R.string.done, R.string.done_desc, R.color.active),
    SKIPPED(3, R.string.skippped, R.string.skipped_desc, R.color.skipped),
    IGNORE(100, R.string.ignored, R.string.ignored_desc, R.color.inactive),

    ;

    private final int status;

    private final int nameResId;
    private final int descriptionResId;

    private final int color;

    SentenceStatus(int status, int nameResId, int descriptionResId, int color) {
        this.status = status;
        this.nameResId = nameResId;
        this.descriptionResId = descriptionResId;
        this.color = color;
    }

    public static SentenceStatus fromStatus(int status) {
        for (SentenceStatus sentenceStatus : values()) {
            if (sentenceStatus.getStatus() == status) {
                return sentenceStatus;
            }
        }
        return IGNORE;
    }

    public int getStatus() {
        return status;
    }

    public int getNameResId() {
        return nameResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public int getColor() {
        return color;
    }
}
