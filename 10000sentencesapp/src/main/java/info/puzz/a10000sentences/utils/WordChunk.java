package info.puzz.a10000sentences.utils;

public class WordChunk {
    public String chunk;
    public String word; // (only letters)

    public WordChunk() {
        this.chunk = "";
        this.word = "";
    }

    public WordChunk(String chunk, String word) {
        this.chunk = chunk;
        this.word = word;
    }
}
