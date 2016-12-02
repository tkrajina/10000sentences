package info.puzz.a10000sentences.models;

import info.puzz.a10000sentences.R;
import lombok.Getter;

public enum SentenceStatus {

    TODO(0, R.string.todo),
    AGAIN(1, R.string.retry_again),
    DONE(2, R.string.done),

    ;

    @Getter
    private final int status;

    @Getter
    private final int descResId;

    SentenceStatus(int status, int descResId) {
        this.status = status;
        this.descResId = descResId;
    }
}
