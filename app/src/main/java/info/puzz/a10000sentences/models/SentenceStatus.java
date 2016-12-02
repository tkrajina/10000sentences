package info.puzz.a10000sentences.models;

import lombok.Getter;

public enum SentenceStatus {
    UNKNOWN(0),
    AGAIN(1),
    EASY(2),
    GOOD(3),

    ;

    @Getter
    private final int status;

    SentenceStatus(int status) {
        this.status = status;
    }
}
