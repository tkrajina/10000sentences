package info.puzz.a10000sentences.importer;

import java.util.HashMap;
import java.util.Map;

public class TatoebaImporter {
    /*
     wget  http://downloads.tatoeba.org/exports/sentences_detailed.tar.bz2
     bzip2 -d sentences_detailed.tar.bz2
     tar -xvf sentences_detailed.tar

     wget http://downloads.tatoeba.org/exports/links.tar.bz2
     bzip2 -d links.tar.bz2
     tar -xvf links.tar
     */
    public static void main(String[] args) {
        Map<Integer, Integer> links = new HashMap<>();
        Map<Integer, String> sentences = new HashMap<>();
    }
}
