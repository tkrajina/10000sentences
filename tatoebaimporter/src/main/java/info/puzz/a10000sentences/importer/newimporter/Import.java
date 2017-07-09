package info.puzz.a10000sentences.importer.newimporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by puzz on 09/07/2017.
 */

public class Import {
    public static void main(String[] args) {
        List<Importer> importers = new ArrayList<>();

        String[][] tatoebaLanguagePairs = new String[][]{
                //new String[] {"pes", "eng"},
                new String[] {"nob", "eng"},
                new String[] {"ces", "eng"},
                new String[] {"mkd", "eng"},
                new String[] {"ces", "eng"},
                new String[] {"bul", "eng"},
                new String[] {"srp", "eng"},
                new String[] {"dan", "eng"},
                new String[] {"swe", "eng"},
                new String[] {"ukr", "eng"},
                new String[] {"nld", "eng"},
                new String[] {"fin", "eng"},
                new String[] {"mkd", "eng"},
                new String[] {"hun", "eng"},
                new String[] {"pol", "eng"},
                new String[] {"ita", "eng"},
                new String[] {"epo", "eng"},
                new String[] {"lat", "eng"},
                new String[] {"tur", "eng"},
                new String[] {"ell", "eng"},
                new String[] {"ron", "eng"},
                new String[] {"ara", "eng"},
                new String[] {"heb", "eng"},
                new String[] {"deu", "eng"},
                new String[] {"fra", "eng"},
                new String[] {"rus", "eng"},
                new String[] {"por", "eng"},
                new String[] {"spa", "eng"},
                new String[] {"lit", "eng"},

                // Nonenglish collections:
                new String[] {"spa", "fra"},
                new String[] {"deu", "ita"},
        };

        for (String[] tatoebaLanguagePair : tatoebaLanguagePairs) {
            importers.add(new NewTatoebaImporter(tatoebaLanguagePair[0], tatoebaLanguagePair[1]));
            importers.add(new NewTatoebaImporter(tatoebaLanguagePair[1], tatoebaLanguagePair[0]));
        }
    }
}
