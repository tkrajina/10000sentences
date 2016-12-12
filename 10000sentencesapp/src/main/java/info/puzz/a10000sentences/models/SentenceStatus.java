package info.puzz.a10000sentences.models;

import info.puzz.a10000sentences.R;

public enum SentenceStatus {

    TODO(0, R.string.todo, android.R.color.primary_text_light),
    REPEAT(1, R.string.retry_again, R.color.error),
    DONE(2, R.string.done, R.color.active),
    IGNORE(100, R.string.ignored, R.color.inactive),

    ;

    private final int status;

    private final int descResId;

    private final int color;

    SentenceStatus(int status, int descResId, int color) {
        this.status = status;
        this.descResId = descResId;
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

    public int getDescResId() {
        return descResId;
    }

    public int getColor() {
        return color;
    }
}
